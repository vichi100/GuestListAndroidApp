package com.application.club.guestlist.bookingTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.club.guestlist.App;
import com.application.club.guestlist.MainActivity;
import com.application.club.guestlist.booking.BookPassActivity;
import com.application.club.guestlist.payumoney.AppEnvironment;
import com.application.club.guestlist.payumoney.BaseApplication;
//import com.payumoney.core.PayUmoneyConfig;
//import com.payumoney.core.PayUmoneyConstants;
//import com.payumoney.core.PayUmoneySdkInitializer;
//import com.payumoney.core.entity.TransactionResponse;
//import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
//import com.payumoney.sdkui.ui.utils.ResultModel;
import com.squareup.picasso.Picasso;
import com.application.club.guestlist.R;
import com.application.club.guestlist.paytm.BuyFromPaytm;
import com.application.club.guestlist.qrcode.QRCodeActivity;
import com.application.club.guestlist.service.EventListener;
import com.application.club.guestlist.service.SocketOperator;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;


public class TableConfirmationActivity extends AppCompatActivity implements EventListener {

    public static final String TAG = "TableConfirmation : ";

    //private PayUmoneySdkInitializer.PaymentParam mPaymentParams;



    SocketOperator socketOperator  = new SocketOperator(this);
    boolean orderBooked = false;
    boolean isTicketBooked = false;
    boolean isrecivedData = false;
    String tableDiscount = "0";
    String date;
    String tableNumber;
    String tableId;
    String tableType;
    String cost;
    String size;
    String details;
    String clubName;
    String clubId;
    String qrNumber="11111111111";
    String detailsx="No Booking";
    String email = null;
    SharedPreferences settings;




    EditText et_mobile = null;
    EditText et_email = null;
    TextView et_warningtxt = null;

    AlertDialog.Builder builderx;
    AlertDialog dialogx;

    String custmerName = null;
    String custmerMobile = null;

    int fullAmount;
    int restAmount;
    int paidAmount;

    InstapayListener listener;

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


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                // Start NewActivity.class
                Intent intent = new Intent(TableConfirmationActivity.this,
                        QRCodeActivity.class);
                intent.putExtra(Constants.BOOKING_TYPE, "Table");
                intent.putExtra(Constants.EVENTDATE, date);
                intent.putExtra(Constants.CLUB_ID, clubId);
                intent.putExtra(Constants.CLUB_NAME, clubName);
                intent.putExtra(Constants.EVENTDATE, date);
                intent.putExtra(Constants.COST, Integer.toString(fullAmount));
                intent.putExtra(Constants.REMAINING_AMOUNT, Integer.toString(restAmount));
                String allDetails = tableType+", Table No. "+tableNumber+" for "+size+" guest ; "+details;//// VIP, Table No 15 for 12 guest
                intent.putExtra(Constants.DETAILS, allDetails);
                intent.putExtra(Constants.TABLE_SIZE, size);
                long time= System.currentTimeMillis();
                qrNumber = Long.toString(time);

                intent.putExtra(Constants.QRNUMBER, qrNumber);
                sendBookingDetailsToServer(response);
                startActivity(intent);
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
                                callInstamojoPay(email, custmerMobile, Integer.toString(paidAmount), "Table Booking", custmerName);
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
                }else {


                    AlertDialog.Builder adb = new AlertDialog.Builder(TableConfirmationActivity.this);


                    //adb.setView(alertDialogView);


                    adb.setTitle("Payment Failure !!!");
                    //myAlertDialog.setTitle("--- Title ---");
                    adb.setMessage(Constants.PAYMENT_FAILURE_MESSAGE);


                    adb.setIcon(R.drawable.alerticon);


                    adb.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            //finish();
                            isrecivedData = false;
                            socketOperator = new SocketOperator(TableConfirmationActivity.this);

                        }
                    });


                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Start NewActivity.class
                            Intent myIntent = new Intent(TableConfirmationActivity.this,
                                    MainActivity.class);
                            startActivity(myIntent);

                            finish();
                        }
                    });
                    adb.show();

                }
            }
        };
    }


    public void sendBookingDetailsToServer(String response){
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
            ticketBookingDetails.put(Constants.TICKETTYPE, "table");
            if(qrNumber.equalsIgnoreCase("11111111111")){
                ticketBookingDetails.put(Constants.TICKET_DETAILS, "No Booking");
            }else {
                ticketBookingDetails.put(Constants.TICKET_DETAILS, detailsx);
            }
            //ticketBookingDetails.put(Constants.TICKET_DETAILS, detailsx);
            ticketBookingDetails.put(Constants.EVENTDATE, date);
            ticketBookingDetails.put(Constants.CLUB_ID, clubId);
            ticketBookingDetails.put(Constants.CLUB_NAME, clubName);
            ticketBookingDetails.put(Constants.QRNUMBER, qrNumber);
            ticketBookingDetails.put(Constants.CUSTOMERNAME, custmerName);
            ticketBookingDetails.put(Constants.MOBILE, custmerMobile);
            ticketBookingDetails.put(Constants.CUSTOMERID, customerId);
            ticketBookingDetails.put(Constants.COST, cost);

            ticketBookingDetails.put(Constants.COSTAFTERDISCOUNT, Integer.toString(fullAmount));

            ticketBookingDetails.put(Constants.PAID_AMOUNT, Integer.toString(paidAmount));
            ticketBookingDetails.put(Constants.REMAINING_AMOUNT, Integer.toString(restAmount));
            ticketBookingDetails.put(Constants.DISCOUNT, tableDiscount);
            ticketBookingDetails.put(Constants.TABLE_ID, tableId);
            ticketBookingDetails.put(Constants.RESPONSE_FROM_PAYMENTGETWAY, response);
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
        setContentView(R.layout.table_confirmation_activity);
        getSupportActionBar().setTitle("Table Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) findViewById(R.id.done);
        Intent intent = getIntent();
        date  = intent.getStringExtra(Constants.EVENTDATE);


        builderx = new AlertDialog.Builder(TableConfirmationActivity.this);

        tableNumber  = intent.getStringExtra(Constants.TABLE_NUMBER);
        tableId  = intent.getStringExtra(Constants.TABLE_ID);
        tableType  = intent.getStringExtra(Constants.TABLE_TYPE);
        cost = intent.getStringExtra(Constants.COST);
        size = intent.getStringExtra(Constants.TABLE_SIZE);
        details = intent.getStringExtra(Constants.DETAILS);
        clubName = intent.getStringExtra(Constants.CLUB_NAME);
        clubId = intent.getStringExtra(Constants.CLUB_ID);
        tableDiscount = intent.getStringExtra(Constants.TABLE_DISCOUNT);
        final String imageURL = intent.getStringExtra(Constants.IMAGE_URL);

        String imgURL = Constants.HTTP_URL+imageURL;


        ImageView imgIcon = (ImageView) findViewById(R.id.mainImage);
        Picasso.with(this.getApplicationContext()).load(imgURL).into(imgIcon);


        TextView costtv =  (TextView) findViewById(R.id.costValue);

        int costInt = Integer.parseInt(cost);
        if(tableDiscount != null && !tableDiscount.equalsIgnoreCase("0")){
            costInt = costInt - (costInt * Integer.parseInt(tableDiscount)/100);
            costtv.setText("Rs "+costInt+" FULL COVER AFETR "+tableDiscount+"% DISCOUNT");
        }else{
            tableDiscount = "0";
            costtv.setText("Rs "+costInt+" FULL COVER");
        }


        TextView datetv = (TextView) findViewById(R.id.date);
        datetv.setText(date);
        String dayx = UtillMethods.getDayFromDate(date);
        TextView daytv = (TextView) findViewById(R.id.day);
        daytv.setText(dayx);
        TextView sizetv =  (TextView) findViewById(R.id.guestCountValue);
        sizetv.setText(size);
        TextView detailstv =  (TextView) findViewById(R.id.detailsValue);
        detailsx = tableType+", Table No. "+tableNumber+" \n"+details;
        detailstv.setText(detailsx);

        TextView bookingAmttv =  (TextView) findViewById(R.id.bookingAmountValue);


        fullAmount = costInt;

        //int costInt = Integer.parseInt(cost);
        if(costInt <= 10000){
            costInt = (costInt*50)/100;
        }else if(costInt <= 20000){
            costInt = (costInt*30)/100;
        }else if(costInt <= 50000){
            costInt = (costInt*25)/100;
        }else if(costInt <=200000){
            costInt = (costInt*20)/100;
        }
        paidAmount = costInt;
        final int costIntx = costInt;
        bookingAmttv.setText(Integer.toString(costInt));
        restAmount = fullAmount - costInt;
        TextView restRookingAmttv =  (TextView) findViewById(R.id.restBookingAmount);
        restRookingAmttv.setText("Rs "+restAmount +" need to pay at club");



        // Capture button clicks
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                try {

                    settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                    custmerName = null;
                    custmerMobile = null;
                    String customerId = null;

                    if (settings.getString("logged", "").toString().equals("logged")) {

                        custmerName = settings.getString("name", "");
                        custmerMobile = settings.getString("mobile", "");
                        customerId = settings.getString(Constants.CUSTOMERID, "");
                        email = settings.getString(Constants.EMAIL, "");
                        if(custmerMobile == null || custmerMobile.trim().equalsIgnoreCase("")
                                || email == null || email.trim().equalsIgnoreCase("")){

                            AlertDialog.Builder builder = new AlertDialog.Builder(TableConfirmationActivity.this);

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
                                        callInstamojoPay(email, custmerMobile, Integer.toString(paidAmount), "Table Booking", custmerName);
                                    }else {
                                        Toast.makeText(getApplication(),
                                                "Email or Mobile can not be empty !!!", Toast.LENGTH_LONG).show();
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
                            callInstamojoPay(email, custmerMobile, Integer.toString(paidAmount), "Table Booking", custmerName);
                        }

                    }






                    //callInstamojoPay(email, custmerMobile, Integer.toString(paidAmount), "Table Booking", custmerName);


                }catch (Exception ex){
                    ex.printStackTrace();
                }





//                    // Start NewActivity.class
//                    Intent intent = new Intent(TableConfirmationActivity.this,
//                            QRCodeActivity.class);
//                    intent.putExtra(Constants.BOOKING_TYPE, "Table");
//                    intent.putExtra(Constants.EVENTDATE, date);
//                intent.putExtra(Constants.CLUB_ID, clubId);
//                intent.putExtra(Constants.CLUB_NAME, clubName);
//                intent.putExtra(Constants.EVENTDATE, date);
//                intent.putExtra(Constants.COST, Integer.toString(fullAmount));
//                intent.putExtra(Constants.REMAINING_AMOUNT, Integer.toString(restAmount));
//                String allDetails = tableType+", Table No. "+tableNumber+" for "+size+" guest ; "+details;//// VIP, Table No 15 for 12 guest
//                intent.putExtra(Constants.DETAILS, allDetails);
//                intent.putExtra(Constants.TABLE_SIZE, size);
//                long time= System.currentTimeMillis();
//                final String qrNumber = Long.toString(time);
//
//                intent.putExtra(Constants.QRNUMBER, qrNumber);
//
//                String tableDetails = "Table For "+size;
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
//                    ticketBookingDetails.put(Constants.TICKETTYPE, "table");
//                    ticketBookingDetails.put(Constants.TICKET_DETAILS, detailsx);
//                    ticketBookingDetails.put(Constants.EVENTDATE, date);
//                    ticketBookingDetails.put(Constants.CLUB_ID, clubId);
//                    ticketBookingDetails.put(Constants.CLUB_NAME, clubName);
//                    ticketBookingDetails.put(Constants.QRNUMBER, qrNumber);
//                    ticketBookingDetails.put(Constants.CUSTOMERNAME, custmerName);
//                    ticketBookingDetails.put(Constants.MOBILE, custmerMobile);
//                    ticketBookingDetails.put(Constants.CUSTOMERID, customerId);
//                    ticketBookingDetails.put(Constants.COST, cost);
//
//                    ticketBookingDetails.put(Constants.COSTAFTERDISCOUNT, Integer.toString(fullAmount));
//
//                    ticketBookingDetails.put(Constants.PAID_AMOUNT, Integer.toString(paidAmount));
//                    ticketBookingDetails.put(Constants.REMAINING_AMOUNT, Integer.toString(restAmount));
//                    ticketBookingDetails.put(Constants.DISCOUNT, tableDiscount);
//                    ticketBookingDetails.put(Constants.TABLE_ID, tableId);
//
//                    //launchPayUMoneyFlow();
//
//
////                    BuyFromPaytm buyFromPaytm = new BuyFromPaytm(TableConfirmationActivity.this);
////                    buyFromPaytm.generateCheckSum(Integer.toString(costIntx), qrNumber, customerId);
//
//
//
//                   // socketOperator.sendMessage(ticketBookingDetails);
//
//                }catch (Exception ex){
//                    ex.printStackTrace();
//                }
//
//
////                while(!isrecivedData){
////                    SystemClock.sleep(1000);
////                }
////
////                startActivity(intent);
            }
        });





    }



    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
//     */
//    private void launchPayUMoneyFlow() {
//
//        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
//
//        //Use this to set your custom text on result screen button
//        payUmoneyConfig.setDoneButtonText("DONE");
//
//        //Use this to set your custom title for the activity
//        payUmoneyConfig.setPayUmoneyActivityTitle("PAYMENT");
//
//        //payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);
//
//        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
//
//        double amount = Double.valueOf(paidAmount);
//        try {
//            amount = amount;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String txnId = System.currentTimeMillis() + "";
//        String phone = "9867614466";//mobile_til.getEditText().getText().toString().trim();
//        String productName = "table";//mAppPreference.getProductInfo();
//        String firstName = "vihcitra";//mAppPreference.getFirstName();
//        String email = "vichi100@gmail.com";//email_til.getEditText().getText().toString().trim();
//        String udf1 = "";
//        String udf2 = "";
//        String udf3 = "";
//        String udf4 = "";
//        String udf5 = "";
//        String udf6 = "";
//        String udf7 = "";
//        String udf8 = "";
//        String udf9 = "";
//        String udf10 = "";
//
//        AppEnvironment appEnvironment = ((App) getApplication()).getAppEnvironment();
//        builder.setAmount(amount)
//                .setTxnId(txnId)
//                .setPhone(phone)
//                .setProductName(productName)
//                .setFirstName(firstName)
//                .setEmail(email)
//                .setsUrl(appEnvironment.surl())
//                .setfUrl(appEnvironment.furl())
//                .setUdf1(udf1)
//                .setUdf2(udf2)
//                .setUdf3(udf3)
//                .setUdf4(udf4)
//                .setUdf5(udf5)
//                .setUdf6(udf6)
//                .setUdf7(udf7)
//                .setUdf8(udf8)
//                .setUdf9(udf9)
//                .setUdf10(udf10)
//                .setIsDebug(appEnvironment.debug())
//                .setKey(appEnvironment.merchant_Key())
//                .setMerchantId(appEnvironment.merchant_ID());
//
//        try {
//            mPaymentParams = builder.build();
//
//            /*
//             * Hash should always be generated from your server side.
//             * */
//            generateHashFromServer(mPaymentParams);
//
//            /*            *//**
//             * Do not use below code when going live
//             * Below code is provided to generate hash from sdk.
//             * It is recommended to generate hash from server side only.
//             * */
//            //mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);
//
////            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,TableConfirmationActivity.this, R.style.AppTheme_default, true);
//
//
//            if (false) {
//                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,TableConfirmationActivity.this, R.style.AppTheme_default,true);
//            } else {
//                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,TableConfirmationActivity.this, R.style.AppTheme_default, true);
//            }
//
//        } catch (Exception e) {
//            // some exception occurred
//            e.printStackTrace();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//            //payNowButton.setEnabled(true);
//        }
//    }
//


    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
//    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
//        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.
//
//        HashMap<String, String> params = paymentParam.getParams();
//
//        // lets create the post params
//        StringBuffer postParamsBuffer = new StringBuffer();
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
//        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
//        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
//        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));
//
//        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
//
//        // lets make an api call
//        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
//        getHashesFromServerTask.execute(postParams);
//    }
//
//    protected String concatParams(String key, String value) {
//        return key + "=" + value + "&";
//    }


    /**
     * This AsyncTask generates hash from server.
//     */
//    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
//        private ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            try{
//                progressDialog = new ProgressDialog(TableConfirmationActivity.this);
//                progressDialog.setMessage("Please wait...");
//                progressDialog.show();
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//
//
//        }
//
//        @Override
//        protected String doInBackground(String... postParams) {
//
//            String merchantHash = "";
//            try {
//                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
//                URL url = new URL("https://payu.herokuapp.com/get_hash");
//
//                String postParam = postParams[0];
//
//                byte[] postParamsByte = postParam.getBytes("UTF-8");
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
//                conn.setDoOutput(true);
//                conn.getOutputStream().write(postParamsByte);
//
//                InputStream responseInputStream = conn.getInputStream();
//                StringBuffer responseStringBuffer = new StringBuffer();
//                byte[] byteContainer = new byte[1024];
//                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
//                    responseStringBuffer.append(new String(byteContainer, 0, i));
//                }
//
//                JSONObject response = new JSONObject(responseStringBuffer.toString());
//
//                Iterator<String> payuHashIterator = response.keys();
//                while (payuHashIterator.hasNext()) {
//                    String key = payuHashIterator.next();
//                    switch (key) {
//                        /**
//                         * This hash is mandatory and needs to be generated from merchant's server side
//                         *
//                         */
//                        case "payment_hash":
//                            merchantHash = response.getString(key);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return merchantHash;
//        }
//
//        @Override
//        protected void onPostExecute(String merchantHash) {
//            super.onPostExecute(merchantHash);
//
//            progressDialog.dismiss();
//            //payNowButton.setEnabled(true);
//
//            if (merchantHash.isEmpty() || merchantHash.equals("")) {
//                Toast.makeText(TableConfirmationActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
//            } else {
//                mPaymentParams.setMerchantHash(merchantHash);
//                //PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, TableConfirmationActivity.this, R.style.AppTheme_default, true);
//
//                if (false) {
//                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, TableConfirmationActivity.this, R.style.AppTheme_default, true);
//                } else {
//                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, TableConfirmationActivity.this, R.style.AppTheme_default, true);
//                }
//            }
//        }
//    }
//

    /**
     * Thus function calculates the hash for transaction
     *
     *
     * @return payment params along with calculated merchant hash
     */
//    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {
//
//        StringBuilder stringBuilder = new StringBuilder();
//        HashMap<String, String> params = paymentParam.getParams();
//        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
//        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");
//
//        AppEnvironment appEnvironment = ((App) getApplication()).getAppEnvironment();
//        stringBuilder.append(appEnvironment.salt());
//
//        String hash = hashCal(stringBuilder.toString());
//        paymentParam.setMerchantHash(hash);
//
//        return paymentParam;
//    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }



    @Override
    protected void onResume() {
        super.onResume();

    }



    //To know when the payment has completed, override the onActivityResult in your activity as exemplified in the sample code below
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result Code is -1 send from Payumoney activity
//        Log.d(TAG, "request code " + requestCode + " resultcode " + resultCode);
//        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
//            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );
//
//            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
//
//            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
//
//                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
//                    //Success Transaction
//                    // Start NewActivity.class
//                    Intent intent = new Intent(TableConfirmationActivity.this,
//                            QRCodeActivity.class);
//                    intent.putExtra(Constants.BOOKING_TYPE, "Table");
//                    intent.putExtra(Constants.EVENTDATE, date);
//                    intent.putExtra(Constants.CLUB_ID, clubId);
//                    intent.putExtra(Constants.CLUB_NAME, clubName);
//                    intent.putExtra(Constants.EVENTDATE, date);
//                    intent.putExtra(Constants.COST, Integer.toString(fullAmount));
//                    intent.putExtra(Constants.REMAINING_AMOUNT, Integer.toString(restAmount));
//                    String allDetails = tableType+", Table No. "+tableNumber+" for "+size+" guest ; "+details;//// VIP, Table No 15 for 12 guest
//                    intent.putExtra(Constants.DETAILS, allDetails);
//                    intent.putExtra(Constants.TABLE_SIZE, size);
//                    long time= System.currentTimeMillis();
//                    final String qrNumber = Long.toString(time);
//
//                    intent.putExtra(Constants.QRNUMBER, qrNumber);
//                    startActivity(intent);
//                } else{
//                    //Failure Transaction
//                    // need to try again: vichi
//                }
//
//                // Response from Payumoney
//                String payuResponse = transactionResponse.getPayuResponse();
//
//                // Response from SURl and FURL
//                String merchantResponse = transactionResponse.getTransactionDetails();
//            }  else if (resultModel != null && resultModel.getError() != null) {
//                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
//            } else {
//                Log.d(TAG, "Both objects are null!");
//            }
//        }
//
//
//
//
//    }
//
//


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
