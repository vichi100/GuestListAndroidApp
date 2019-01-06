package com.application.club.guestlist.booking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;
import org.json.JSONException;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.club.guestlist.MainActivity;
import com.application.club.guestlist.bookingTable.TableConfirmationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.application.club.guestlist.R;
import com.application.club.guestlist.clubdetails.TicketDetailsItem;
import com.application.club.guestlist.paytm.BuyFromPaytm;
import com.application.club.guestlist.qrcode.QRCodeActivity;
import com.application.club.guestlist.service.EventListener;
import com.application.club.guestlist.service.SocketOperator;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import org.json.JSONArray;
import org.json.JSONObject;


import static com.application.club.guestlist.utils.Constants.CLUB_NAME;


public class BookPassActivity extends AppCompatActivity implements QuantityView.OnQuantityChangeListener , EventListener {

    private int pricePerProduct = 180;
    private QuantityView currentQuantityView;

    String custmerName = null;
    String custmerMobile = null;

    int coupleCost = 0;
    int stagCost = 0;
    int girlCost = 0;
    int totalCost = 0;
    int costWithoutDiscount = 0;
    String passDiscount = "0";
    QuantityView quantityViewDefaultCouple = null;
    QuantityView quantityViewDefaultGirl = null;
    QuantityView quantityViewDefaultstag = null;

    TextView totalCosttv;

    SocketOperator socketOperator  = new SocketOperator(this);

    boolean isTicketBooked = false;
    boolean isrecivedData = false;

    String clubName;
    String clubidx;
    String date;
    String qrNumber="11111111111";
    String bookingDetails="No Booking";
    String email = null;
    SharedPreferences settings;

    EditText et_mobile = null;
    EditText et_email = null;
    TextView et_warningtxt = null;

    AlertDialog.Builder builderx;
    AlertDialog dialogx;



    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
    final Activity activity = this;
    InstamojoPay instamojoPay = new InstamojoPay();
    IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
    registerReceiver(instamojoPay, filter);
    JSONObject pay = new JSONObject();
        try {
        pay.put("email", email);
        pay.put("phone", phone);
        pay.put("purpose", purpose);
        pay.put("amount", amount);
        pay.put("name", buyername);
        pay.put("send_sms", false);
        pay.put("send_email", false);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    initListener();
        instamojoPay.start(activity, pay, listener);
}
    
    InstapayListener listener;



    
    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
//                        .show();
                if(totalCost != 0){
                    // Start NewActivity.class
                    Intent intent = new Intent(BookPassActivity.this,
                            QRCodeActivity.class);
                    intent.putExtra(Constants.CLUB_NAME, clubName);
                    intent.putExtra(Constants.EVENTDATE, date);
                    intent.putExtra(Constants.BOOKING_TYPE, "pass");

                    intent.putExtra("quantityViewDefaultCouple", Integer.toString(quantityViewDefaultCouple.getQuantity()));
                    intent.putExtra("quantityViewDefaultGirl", Integer.toString(quantityViewDefaultGirl.getQuantity()));
                    intent.putExtra("quantityViewDefaultstag", Integer.toString(quantityViewDefaultstag.getQuantity()));

                    String entry = "";
                    if(!Integer.toString(quantityViewDefaultCouple.getQuantity()).equalsIgnoreCase("0")){
                        entry = quantityViewDefaultCouple.getQuantity()+" couple ";
                    }

                    if(!Integer.toString(quantityViewDefaultGirl.getQuantity()).equalsIgnoreCase("0")){
                        if(entry.equalsIgnoreCase(""))
                            entry = entry+ quantityViewDefaultGirl.getQuantity()+" girl ";
                        else
                            entry = entry+"and "+ quantityViewDefaultGirl.getQuantity()+" girl ";
                    }

                    if(!Integer.toString(quantityViewDefaultstag.getQuantity()).equalsIgnoreCase("0")){
                        if(entry.equalsIgnoreCase(""))
                            entry = entry + quantityViewDefaultstag.getQuantity()+" stag ";
                        else
                            entry = entry +"and "+ quantityViewDefaultstag.getQuantity()+" stag ";
                    }

                    if(!entry.equalsIgnoreCase("")){
                        entry = entry+ " is allowed";
                    }

                    bookingDetails = entry;
                    intent.putExtra(Constants.TOTAL_COST, Integer.toString(totalCost));
                    intent.putExtra(Constants.CLUB_NAME, clubName);
                    intent.putExtra(Constants.CLUB_ID, clubidx);
                    intent.putExtra(Constants.EVENTDATE, date);
                    long time= System.currentTimeMillis();
                    qrNumber = Long.toString(time);
                    intent.putExtra(Constants.QRNUMBER, qrNumber);
                    sendBookingDetailsToServer(response);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();

                sendBookingDetailsToServer(reason);




                // if mobile number is invaild show alert box to change

                if(reason != null && reason.contains("Invalid Mobile")){


                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.email_alertdialog_custom_view,null);

                    // Specify alert dialog is not cancelable/not ignorable
                    builderx.setCancelable(false);

                    // Set the custom layout as alert dialog view
                    builderx.setView(dialogView);

                    // Get the custom alert dialog view widgets reference
                    Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
                    Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);

                    et_mobile = (EditText) dialogView.findViewById(R.id.et_mobile);
                    //et_email = (EditText) dialogView.findViewById(R.id.et_email);
                    et_warningtxt = (TextView) dialogView.findViewById(R.id.warningtxt);

                    //if(custmerMobile == null || custmerMobile.trim().equalsIgnoreCase("")){

                    et_mobile.setVisibility(View.VISIBLE);
                    et_warningtxt.setVisibility(View.VISIBLE);
                    et_warningtxt.setText(custmerMobile+" is invalid, Please enter valid mobile");
                    //}

//                    if(email == null ||  email.trim().equalsIgnoreCase("")){
//                        et_email.setVisibility(View.VISIBLE);
//                    }

                    //final EditText et_email = (EditText) dialogView.findViewById(R.id.et_email);

                    // Create the alert dialog
                    if(dialogx != null){
                        dialogx.dismiss();
                    }

                    dialogx = builderx.create();

                    btn_positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Dismiss the alert dialog
//                                    if(custmerMobile != null || ! custmerMobile.trim().equalsIgnoreCase("")){
                            if(custmerMobile != null || ! custmerMobile.trim().equalsIgnoreCase("")
                                    ){

                                custmerMobile = et_mobile.getText().toString();
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Constants.MOBILE, custmerMobile);
                                editor.commit();
                                callInstamojoPay(email, custmerMobile, Integer.toString(totalCost), "Pass Booking", custmerName);
                            }else {
                                Toast.makeText(getApplication(),
                                        "Mobile can not be empty !!!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    btn_negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Dismiss/cancel the alert dialog
                            //dialog.cancel();
                            dialogx.dismiss();
                            Toast.makeText(getApplication(),
                                    "Mobile can not be empty !!!", Toast.LENGTH_LONG).show();
                        }
                    });

                    dialogx.show();
                }else{

                    AlertDialog.Builder adb = new AlertDialog.Builder(BookPassActivity.this);


                    //adb.setView(alertDialogView);


                    adb.setTitle("Payment Failure !!!");
                    //myAlertDialog.setTitle("--- Title ---");
                    adb.setMessage(Constants.PAYMENT_FAILURE_MESSAGE);


                    adb.setIcon(R.drawable.alerticon);


                    adb.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            //finish();
                            isrecivedData = false;
                            socketOperator  = new SocketOperator(BookPassActivity.this);

                        } });


                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Start NewActivity.class
                            Intent myIntent = new Intent(BookPassActivity.this,
                                    MainActivity.class);
                            startActivity(myIntent);

                            finish();
                        } });
                    adb.show();

                }


                //



            }
        };
    }


    public void sendBookingDetailsToServer(String response){
        try {

            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            String custmerName = null;
            String custmerMobile = null;
            String customerId = null;
            if (settings.getString("logged", "").toString().equals("logged")) {

                custmerName = settings.getString("name", "");
                custmerMobile = settings.getString("mobile", "");
                customerId = settings.getString(Constants.CUSTOMERID, "");

            }


            JSONObject ticketBookingDetails = new JSONObject();
            ticketBookingDetails.put("action", "inserOrderDetails");
            ticketBookingDetails.put(Constants.TICKETTYPE, "pass");
            ticketBookingDetails.put(Constants.TICKET_DETAILS, bookingDetails);
            ticketBookingDetails.put(Constants.EVENTDATE, date);
            ticketBookingDetails.put(Constants.CLUB_ID, clubidx);
            ticketBookingDetails.put(Constants.CLUB_NAME, clubName);
            ticketBookingDetails.put(Constants.QRNUMBER, qrNumber);
            ticketBookingDetails.put(Constants.CUSTOMERNAME, custmerName);
            ticketBookingDetails.put(Constants.MOBILE, custmerMobile);
            ticketBookingDetails.put(Constants.CUSTOMERID, customerId);
            ticketBookingDetails.put(Constants.COST, Integer.toString(costWithoutDiscount));
            ticketBookingDetails.put(Constants.PAID_AMOUNT, Integer.toString(totalCost));
            ticketBookingDetails.put(Constants.COSTAFTERDISCOUNT, Integer.toString(totalCost));
            ticketBookingDetails.put(Constants.REMAINING_AMOUNT, Integer.toString(0));
            ticketBookingDetails.put(Constants.DISCOUNT, passDiscount);
            ticketBookingDetails.put(Constants.RESPONSE_FROM_PAYMENTGETWAY, response);
            //String todayDate = UtillMethods.getTodayDate();
            //ticketBookingDetails.put(Constants.BOOKINGDATE, todayDate);

//                    BuyFromPaytm buyFromPaytm = new BuyFromPaytm(BookPassActivity.this);
//                    buyFromPaytm.generateCheckSum(Integer.toString(totalCost), qrNumber, customerId);

            socketOperator.sendMessage(ticketBookingDetails);
            while(!isrecivedData){
                SystemClock.sleep(1000);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.book_pass);
        // Call the function callInstamojo to start payment here
        getSupportActionBar().setTitle("Pass Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) findViewById(R.id.book);

        builderx = new AlertDialog.Builder(BookPassActivity.this);



        Intent intent = getIntent();
        clubName  = intent.getStringExtra(CLUB_NAME);
        clubidx  = intent.getStringExtra(Constants.CLUB_ID);
        date = intent.getStringExtra(Constants.EVENTDATE);
        passDiscount = intent.getStringExtra(Constants.PASS_DISCOUNT);



        if(passDiscount!= null && !passDiscount.equalsIgnoreCase("0")){
            TextView tv = (TextView) findViewById(R.id.passdiscountNote);
            tv.setText(passDiscount+"% Discount will apply on passes");
            tv.setVisibility(View.VISIBLE);
        }else if(passDiscount == null){
            passDiscount = "0";
        }





        final String imageURL = intent.getStringExtra(Constants.IMAGE_URL);


        String imgURL = Constants.HTTP_URL+imageURL;


        ImageView imgIcon = (ImageView) findViewById(R.id.mainImage);
        //Picasso.with(this.getApplicationContext()).load(imgURL).into(imgIcon);
        Glide.with(this)
                .load(imgURL)
                //.placeholder(R.drawable.circular_progress_bar)
                //.apply(options)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.skipMemoryCache(true)
                .into(imgIcon);

        final String ticketDetailsJsonArryStr = intent.getStringExtra(Constants.TICKET_DETAILS);
        JSONArray ticketDetailsListJsonArray = null;
        try {

            ticketDetailsListJsonArray = new JSONArray(ticketDetailsJsonArryStr);

            if(ticketDetailsListJsonArray != null){

                for(int i=0; i < ticketDetailsListJsonArray.length(); i++){
                    TicketDetailsItem ticketDetailsItemObj = new TicketDetailsItem();
                    JSONObject ticketDetailJObj = ticketDetailsListJsonArray.getJSONObject(i);
                    String type = ticketDetailJObj.getString(Constants.TICKET_TYPE);
                    String category = ticketDetailJObj.getString(Constants.CATEGORY);
                    String clubid = ticketDetailJObj.getString(Constants.CLUB_ID);
                    String clubname = ticketDetailJObj.getString(Constants.CLUB_NAME);
                    String eventdate = ticketDetailJObj.getString(Constants.EVENTDATE);
                    if(type.equalsIgnoreCase("pass") && category.equalsIgnoreCase("couple") && date.equalsIgnoreCase(eventdate)){
                        String cost = ticketDetailJObj.getString(Constants.COST);
                        TextView tv = (TextView) findViewById(R.id.couple);
                        tv.setText("Couple/"+cost);
                        coupleCost = Integer.parseInt(cost);
                        quantityViewDefaultCouple = (QuantityView) findViewById(R.id.quantityView_couple);
                        setQuantityViewObjectToListner(quantityViewDefaultCouple);

                    }if(type.equalsIgnoreCase("pass") && category.equalsIgnoreCase("stag") && date.equalsIgnoreCase(eventdate)){
                        String cost = ticketDetailJObj.getString(Constants.COST);
                        TextView tv = (TextView) findViewById(R.id.stag);
                        tv.setText("Stag/"+cost);
                        stagCost = Integer.parseInt(cost);
                        quantityViewDefaultstag = (QuantityView) findViewById(R.id.quantityView_stag);
                        setQuantityViewObjectToListner(quantityViewDefaultstag);

                    }if(type.equalsIgnoreCase("pass") && category.equalsIgnoreCase("girl") && date.equalsIgnoreCase(eventdate)){
                        String cost = ticketDetailJObj.getString(Constants.COST);
                        TextView tv = (TextView) findViewById(R.id.girl);
                        tv.setText("Girl/"+cost);
                        girlCost = Integer.parseInt(cost);
                        quantityViewDefaultGirl = (QuantityView) findViewById(R.id.quantityView_girl);
                        setQuantityViewObjectToListner(quantityViewDefaultGirl);

                    }

                }

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        totalCosttv = (TextView) findViewById(R.id.totalCost);
        TextView datetv= (TextView) findViewById(R.id.date);
        datetv.setText(date);
        TextView daytv= (TextView) findViewById(R.id.day);
        String dayx = UtillMethods.getDayFromDate(date);
        daytv.setText(dayx);

//        quantityViewDefaultCouple = (QuantityView) findViewById(R.id.quantityView_couple);
//        setQuantityViewObjectToListner(quantityViewDefaultCouple);

//        quantityViewDefaultGirl = (QuantityView) findViewById(R.id.quantityView_girl);
//        setQuantityViewObjectToListner(quantityViewDefaultGirl);

//        quantityViewDefaultstag = (QuantityView) findViewById(R.id.quantityView_stag);
//        setQuantityViewObjectToListner(quantityViewDefaultstag);



        // Capture button clicks
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                try {

                    settings = getSharedPreferences(Constants.PREFS_NAME, 0);

                    String customerId = null;


                    if (settings.getString("logged", "").toString().equals("logged")) {

                        custmerName = settings.getString("name", "");
                        custmerMobile = settings.getString("mobile", "");
                        customerId = settings.getString(Constants.CUSTOMERID, "");
                        email = settings.getString(Constants.EMAIL, "");
                        if(custmerMobile == null || custmerMobile.trim().equalsIgnoreCase("")
                                || email == null || email.trim().equalsIgnoreCase("")){

                            AlertDialog.Builder builder = new AlertDialog.Builder(BookPassActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.email_alertdialog_custom_view,null);

                            // Specify alert dialog is not cancelable/not ignorable
                            builder.setCancelable(false);

                            // Set the custom layout as alert dialog view
                            builder.setView(dialogView);

                            // Get the custom alert dialog view widgets reference
                            Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
                            Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);

                            et_mobile = (EditText) dialogView.findViewById(R.id.et_mobile);
                            et_email = (EditText) dialogView.findViewById(R.id.et_email);

                            if(custmerMobile == null || custmerMobile.trim().equalsIgnoreCase("")){

                                et_mobile.setVisibility(View.VISIBLE);
                            }

                            if(email == null ||  email.trim().equalsIgnoreCase("")){
                                et_email.setVisibility(View.VISIBLE);
                            }

                            final EditText et_email = (EditText) dialogView.findViewById(R.id.et_email);

                            // Create the alert dialog
                            final AlertDialog dialog = builder.create();

                            btn_positive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Dismiss the alert dialog
//                                    if(custmerMobile != null || ! custmerMobile.trim().equalsIgnoreCase("")){
                                    if(custmerMobile != null || ! custmerMobile.trim().equalsIgnoreCase("")
                                    || email != null || !email.trim().equalsIgnoreCase("")){
                                        dialog.cancel();

                                        if(custmerMobile == null || custmerMobile.trim().equalsIgnoreCase("")){
                                            custmerMobile = et_mobile.getText().toString();
                                        }

                                        if(email == null ||  email.trim().equalsIgnoreCase("")){
                                            email = et_email.getText().toString();
                                        }



                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString(Constants.MOBILE, custmerMobile);
                                        editor.putString(Constants.EMAIL, email);
                                        editor.commit();
                                        callInstamojoPay(email, custmerMobile, Integer.toString(totalCost), "Pass Booking", custmerName);
                                    }else {
                                        Toast.makeText(getApplication(),
                                                "Mobile or Email can not be empty !!!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                            btn_negative.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Dismiss/cancel the alert dialog
                                    //dialog.cancel();
                                    dialog.dismiss();
                                    Toast.makeText(getApplication(),
                                            "Mobile or Email can not be empty !!!", Toast.LENGTH_LONG).show();
                                }
                            });

                            dialog.show();


                        }else{
                            callInstamojoPay(email, custmerMobile, Integer.toString(totalCost), "Pass Booking", custmerName);
                        }

                    }



                }catch (Exception ex){
                    ex.printStackTrace();
                }

//            if(totalCost != 0) {
//                // Start NewActivity.class
//                Intent intent = new Intent(BookPassActivity.this,
//                        QRCodeActivity.class);
//                intent.putExtra(Constants.CLUB_NAME, clubName);
//                intent.putExtra(Constants.EVENTDATE, date);
//                intent.putExtra(Constants.BOOKING_TYPE, "pass");
//
//                intent.putExtra("quantityViewDefaultCouple", Integer.toString(quantityViewDefaultCouple.getQuantity()));
//                intent.putExtra("quantityViewDefaultGirl", Integer.toString(quantityViewDefaultGirl.getQuantity()));
//                intent.putExtra("quantityViewDefaultstag", Integer.toString(quantityViewDefaultstag.getQuantity()));
//
//                String entry = "";
//                if(!Integer.toString(quantityViewDefaultCouple.getQuantity()).equalsIgnoreCase("0")){
//                    entry = quantityViewDefaultCouple.getQuantity()+" couple ";
//                }
//
//                if(!Integer.toString(quantityViewDefaultGirl.getQuantity()).equalsIgnoreCase("0")){
//                    if(entry.equalsIgnoreCase(""))
//                        entry = entry+ quantityViewDefaultGirl.getQuantity()+" girl ";
//                    else
//                        entry = entry+"and "+ quantityViewDefaultGirl.getQuantity()+" girl ";
//                }
//
//                if(!Integer.toString(quantityViewDefaultstag.getQuantity()).equalsIgnoreCase("0")){
//                    if(entry.equalsIgnoreCase(""))
//                        entry = entry + quantityViewDefaultstag.getQuantity()+" stag ";
//                    else
//                        entry = entry +"and "+ quantityViewDefaultstag.getQuantity()+" stag ";
//                }
//
//                if(!entry.equalsIgnoreCase("")){
//                    entry = entry+ " is allowed";
//                }
//
//                bookingDetails = entry;
//                intent.putExtra(Constants.TOTAL_COST, Integer.toString(totalCost));
//                intent.putExtra(Constants.CLUB_NAME, clubName);
//                intent.putExtra(Constants.CLUB_ID, clubidx);
//                intent.putExtra(Constants.EVENTDATE, date);
//                long time= System.currentTimeMillis();
//                final String qrNumber = Long.toString(time);
//
//
//
//                intent.putExtra(Constants.QRNUMBER, qrNumber);
//
//
//                try{
//
//                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
//                    String custmerName = null;
//                    String custmerMobile = null;
//                    String customerId = null;
//                    if (settings.getString("logged", "").toString().equals("logged")) {
//
//                        custmerName = settings.getString("name","");
//                        custmerMobile = settings.getString("mobile","");
//                        customerId = settings.getString(Constants.CUSTOMERID,"");
//
//                    }
//
//
//                    JSONObject ticketBookingDetails = new JSONObject();
//                    ticketBookingDetails.put("action", "inserOrderDetails");
//                    ticketBookingDetails.put(Constants.TICKETTYPE, "pass");
//                    ticketBookingDetails.put(Constants.TICKET_DETAILS, bookingDetails);
//                    ticketBookingDetails.put(Constants.EVENTDATE, date);
//                    ticketBookingDetails.put(Constants.CLUB_ID, clubidx);
//                    ticketBookingDetails.put(Constants.CLUB_NAME, clubName);
//                    ticketBookingDetails.put(Constants.QRNUMBER, qrNumber);
//                    ticketBookingDetails.put(Constants.CUSTOMERNAME, custmerName);
//                    ticketBookingDetails.put(Constants.MOBILE, custmerMobile);
//                    ticketBookingDetails.put(Constants.CUSTOMERID, customerId);
//                    ticketBookingDetails.put(Constants.COST, Integer.toString(costWithoutDiscount));
//                    ticketBookingDetails.put(Constants.PAID_AMOUNT, Integer.toString(totalCost));
//                    ticketBookingDetails.put(Constants.COSTAFTERDISCOUNT, Integer.toString(totalCost));
//                    ticketBookingDetails.put(Constants.REMAINING_AMOUNT, Integer.toString(0));
//                    ticketBookingDetails.put(Constants.DISCOUNT, passDiscount);
//                    //String todayDate = UtillMethods.getTodayDate();
//                    //ticketBookingDetails.put(Constants.BOOKINGDATE, todayDate);
//
////                    BuyFromPaytm buyFromPaytm = new BuyFromPaytm(BookPassActivity.this);
////                    buyFromPaytm.generateCheckSum(Integer.toString(totalCost), qrNumber, customerId);
//
//                    socketOperator.sendMessage(ticketBookingDetails);



//                }catch (Exception ex){
//                    ex.printStackTrace();
//                }


//                while(!isrecivedData){
//                    SystemClock.sleep(1000);
//                }
//                //startActivity(intent);
//            }else{
//                Toast.makeText(BookPassActivity.this, "No pass is selected", Toast.LENGTH_LONG).show();
//            }
            }
        });

    }

    private void upDateTotalCost() {

        new Thread() {
            public void run() {

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(passDiscount != null && !passDiscount.equalsIgnoreCase("0")){
                                    totalCosttv.setText("Rs "+Integer.toString(totalCost)+" FULL COVER AFTER "+passDiscount+"% DISCOUNT");
                                }else {
                                    totalCosttv.setText("Rs "+Integer.toString(totalCost)+" FULL COVER");
                                }

                            }
                        });
                        //Thread.sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        }.start();
    }



    private void setQuantityViewObjectToListner( final QuantityView quantityViewDefault){

        quantityViewDefault.setOnQuantityChangeListener(this);
        //currentQuantityView = quantityViewDefault;



    }

    @Override
    public void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically) {
//        QuantityView quantityViewCustom1 = (QuantityView) findViewById(R.id.quantityView_custom_1);
//        if (newQuantity == 3) {
//            quantityViewCustom1.setQuantity(oldQuantity);
//        }
        //currentQuantityView.setQuantity(newQuantity);

        int coupleCount = quantityViewDefaultCouple.getQuantity();
        int girlCount = quantityViewDefaultGirl.getQuantity();
        int stagCount = quantityViewDefaultstag.getQuantity();

        totalCost = coupleCount * coupleCost + stagCount * stagCost + girlCount * girlCost;
        costWithoutDiscount = totalCost;
        if(passDiscount!= null && !passDiscount.equalsIgnoreCase("0")){
            totalCost = totalCost - (totalCost * Integer.parseInt(passDiscount)/100);
        }
        upDateTotalCost();

        //Toast.makeText(BookPassActivity.this, "Quantity: vihi"+totalCost, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLimitReached() {
        Log.d(getClass().getSimpleName(), "Limit reached");
    }


    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void eventReceived(String message){
        // conver message to list
        if(message != null){


                try{
                    if(message.equalsIgnoreCase("success")){
                        isTicketBooked = true;
                    }else{
                        isTicketBooked = false;
                    }
                    isrecivedData = true;



            }catch (Exception ex){
                ex.printStackTrace();

            }

        }
    }
}
