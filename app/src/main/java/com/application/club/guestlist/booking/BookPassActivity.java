package com.application.club.guestlist.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.book_pass);
        getSupportActionBar().setTitle("Pass Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) findViewById(R.id.book);




        Intent intent = getIntent();
        final String clubName  = intent.getStringExtra(CLUB_NAME);
        final String clubidx  = intent.getStringExtra(Constants.CLUB_ID);
        final String date = intent.getStringExtra(Constants.EVENTDATE);
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
        Picasso.with(this.getApplicationContext()).load(imgURL).into(imgIcon);

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

            if(totalCost != 0) {
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

                String bookingDetails = entry;
                intent.putExtra(Constants.TOTAL_COST, Integer.toString(totalCost));
                intent.putExtra(Constants.CLUB_NAME, clubName);
                intent.putExtra(Constants.CLUB_ID, clubidx);
                intent.putExtra(Constants.EVENTDATE, date);
                long time= System.currentTimeMillis();
                final String qrNumber = Long.toString(time);



                intent.putExtra(Constants.QRNUMBER, qrNumber);


                try{

                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                    String custmerName = null;
                    String custmerMobile = null;
                    String customerId = null;
                    if (settings.getString("logged", "").toString().equals("logged")) {

                        custmerName = settings.getString("name","");
                        custmerMobile = settings.getString("mobile","");
                        customerId = settings.getString(Constants.CUSTOMERID,"");

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
                    //String todayDate = UtillMethods.getTodayDate();
                    //ticketBookingDetails.put(Constants.BOOKINGDATE, todayDate);

//                    BuyFromPaytm buyFromPaytm = new BuyFromPaytm(BookPassActivity.this);
//                    buyFromPaytm.generateCheckSum(Integer.toString(totalCost), qrNumber, customerId);

                    socketOperator.sendMessage(ticketBookingDetails);

                }catch (Exception ex){
                    ex.printStackTrace();
                }


                while(!isrecivedData){
                    SystemClock.sleep(1000);
                }
                startActivity(intent);
            }else{
                Toast.makeText(BookPassActivity.this, "No pass is selected", Toast.LENGTH_LONG).show();
            }
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
