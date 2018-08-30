package com.application.club.guestlist.bookingTable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.application.club.guestlist.R;
import com.application.club.guestlist.paytm.BuyFromPaytm;
import com.application.club.guestlist.qrcode.QRCodeActivity;
import com.application.club.guestlist.service.EventListener;
import com.application.club.guestlist.service.SocketOperator;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import org.json.JSONObject;




public class TableConfirmationActivity extends AppCompatActivity implements EventListener {

    SocketOperator socketOperator  = new SocketOperator(this);
    boolean orderBooked = false;
    boolean isTicketBooked = false;
    boolean isrecivedData = false;
    String tableDiscount = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_confirmation_activity);
        getSupportActionBar().setTitle("Table Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) findViewById(R.id.done);
        Intent intent = getIntent();
        final String date  = intent.getStringExtra(Constants.EVENTDATE);

        final String tableNumber  = intent.getStringExtra(Constants.TABLE_NUMBER);
        final String tableId  = intent.getStringExtra(Constants.TABLE_ID);
        final String tableType  = intent.getStringExtra(Constants.TABLE_TYPE);
        final String cost = intent.getStringExtra(Constants.COST);
        final String size = intent.getStringExtra(Constants.TABLE_SIZE);
        final String details = intent.getStringExtra(Constants.DETAILS);
        final String clubName = intent.getStringExtra(Constants.CLUB_NAME);
        final String clubId = intent.getStringExtra(Constants.CLUB_ID);
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
        final String detailsx = tableType+", Table No. "+tableNumber+"; "+details;
        detailstv.setText(detailsx);

        TextView bookingAmttv =  (TextView) findViewById(R.id.bookingAmountValue);


        final int fullAmount = costInt;

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
        final int paidAmount = costInt;
        final int costIntx = costInt;
        bookingAmttv.setText(Integer.toString(costInt));
        final int restAmount = fullAmount - costInt;
        TextView restRookingAmttv =  (TextView) findViewById(R.id.restBookingAmount);
        restRookingAmttv.setText("Rs "+restAmount +" need to pay at club");



        // Capture button clicks
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
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
                intent.putExtra(Constants.DETAILS, detailsx);
                intent.putExtra(Constants.TABLE_SIZE, size);
                long time= System.currentTimeMillis();
                final String qrNumber = Long.toString(time);

                intent.putExtra(Constants.QRNUMBER, qrNumber);

                String tableDetails = "Table For "+size;

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
                    ticketBookingDetails.put(Constants.TICKET_DETAILS, detailsx);
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




                    BuyFromPaytm buyFromPaytm = new BuyFromPaytm(TableConfirmationActivity.this);
                    buyFromPaytm.generateCheckSum(Integer.toString(costIntx), qrNumber, customerId);



                    socketOperator.sendMessage(ticketBookingDetails);

                }catch (Exception ex){
                    ex.printStackTrace();
                }


                while(!isrecivedData){
                    SystemClock.sleep(1000);
                }

                startActivity(intent);
            }
        });





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
