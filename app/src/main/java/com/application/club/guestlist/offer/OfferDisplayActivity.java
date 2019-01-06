package com.application.club.guestlist.offer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.club.guestlist.bookedPasses.BookingFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.application.club.guestlist.MainActivity;
import com.application.club.guestlist.R;
import com.application.club.guestlist.booking.BookGuestListActivity;
import com.application.club.guestlist.booking.BookPassActivity;
import com.application.club.guestlist.bookingTable.TableBookingActivity;
import com.application.club.guestlist.clubdetails.ClubEventsDetailsItem;
import com.application.club.guestlist.clubdetails.TicketDetailsItem;
import com.application.club.guestlist.service.EventListener;
import com.application.club.guestlist.service.SocketOperator;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/**
 * Created by vichi on 03/03/18.
 */

public class OfferDisplayActivity extends AppCompatActivity implements EventListener {

    SocketOperator socketOperator  = new SocketOperator(this);
    boolean getData = false;
    static JSONArray clubsEventListJsonArray;
    static JSONArray ticketDetailsListJsonArray;
    String clubId;
    String clubname;
    String eventDate;
    String djname;
    String music;
    String imageURL;
    String isNotification;
    String passdiscount;
    String tablediscount;
    String location;
    String offersDetails;

    private ArrayList<ClubEventsDetailsItem> clubEventDetailsItemList;
    private ArrayList<TicketDetailsItem> ticketDetailsItemList;


    AlertDialog alert;

    private android.app.AlertDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_display_activity);
        getSupportActionBar().setTitle("Offer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog= new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).build();

        Intent intent = getIntent();
        eventDate  = intent.getStringExtra(Constants.EVENT_DATE);
        clubId  = intent.getStringExtra(Constants.CLUB_ID);
        clubname  = intent.getStringExtra(Constants.CLUB_NAME);

        djname = intent.getStringExtra(Constants.DJ_NAME);
        music = intent.getStringExtra(Constants.MUSIC);
        location = intent.getStringExtra(Constants.LOACTION);

        imageURL = intent.getStringExtra(Constants.IMAGE_URL);
        isNotification = intent.getStringExtra(Constants.IS_NOTIFICATION);
        passdiscount = intent.getStringExtra(Constants.PASS_DISCOUNT);
        tablediscount = intent.getStringExtra(Constants.TABLE_DISCOUNT);
        offersDetails = intent.getStringExtra(Constants.OFFERS_DETAILS);

        String day = UtillMethods.getDayFromDate(eventDate);

        TextView daytv = (TextView) findViewById(R.id.day);
        daytv.setText(day);

        TextView datetv = (TextView) findViewById(R.id.date);
        datetv.setText(eventDate);

        TextView clubNametv = (TextView) findViewById(R.id.club);
        clubNametv.setText(clubname);

        //populateEventsListForClub();

        TextView djtv = (TextView) findViewById(R.id.dj);
        djtv.setText(djname);

        TextView musictv = (TextView) findViewById(R.id.musicx);
        musictv.setText(music);


        TextView offersDetailstv = (TextView) findViewById(R.id.offersDetails);
        musictv.setText(offersDetails);

        if(passdiscount != null && !passdiscount.equalsIgnoreCase("0")){
            TextView passDiscounttv = (TextView) findViewById(R.id.passdiscount);
            passDiscounttv.setText("PASS: "+passdiscount+"% Off");
            passDiscounttv.setVisibility(View.VISIBLE);
        }

        if(tablediscount != null && !tablediscount.equalsIgnoreCase("0")){
            TextView tableDiscounttv = (TextView) findViewById(R.id.tablediscount);
            tableDiscounttv.setText("TABLE: "+tablediscount+"% Off");
            tableDiscounttv.setVisibility(View.VISIBLE);
        }


        ImageView mainImagetv = (ImageView)findViewById(R.id.mainImage);

        //Picasso.with(this.getApplicationContext()).load(Constants.HTTP_URL+imageURL).into(mainImagetv);

        Glide.with(this)
                .load(Constants.HTTP_URL+imageURL)
                //.placeholder(R.drawable.circular_progress_bar)
                //.apply(options)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.skipMemoryCache(true)
                .into(mainImagetv);


        Handler handler = new Handler();
        OfferDisplayActivity.FetchData fdTask = new OfferDisplayActivity.FetchData();
        fdTask.execute();

        OfferDisplayActivity.TaskCanceler taskCanceler = new OfferDisplayActivity.TaskCanceler(fdTask);

        handler.postDelayed(taskCanceler, 60*1000);

//        TextView timetv = (TextView) findViewById(R.id.time);
//        timetv.setText("TIME    "+startTime);

//        TextView guestList = (TextView)findViewById(R.id.guestList);
//        TextView table = (TextView) findViewById(R.id.table);
//        TextView pass = (TextView) findViewById(R.id.pass);




//        final Intent intentG = new Intent(this, BookGuestListActivity.class);
//        final Intent intentP = new Intent(this, BookPassActivity.class);
//        final Intent intentT = new Intent(this, TableBookingActivity.class);
//
//
//        guestList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Intent intentx = new Intent(this, BookGuestListActivity.class);
//                intentG.putExtra(Constants.CLUB_ID, clubId);
//                intentG.putExtra(Constants.CLUB_NAME, clubname);
//                intentG.putExtra(Constants.EVENTDATE, eventDate);
//                intentG.putExtra(Constants.IMAGE_URL, imageURL);
//                intentG.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
//                startActivity(intentG);
//
//            }
//        });
//
//        pass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                intentP.putExtra(Constants.CLUB_ID, clubId);
//                intentP.putExtra(Constants.CLUB_NAME, clubname);
//                intentP.putExtra(Constants.EVENTDATE, eventDate);
//                intentP.putExtra(Constants.IMAGE_URL, imageURL);
//                intentP.putExtra(Constants.PASS_DISCOUNT, passdiscount);
//                intentP.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
//                startActivity(intentP);
//
//            }
//        });
//
//        table.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent intent = new Intent(mContext, TableBookingActivity.class);
//                intentT.putExtra(Constants.CLUB_ID, clubId);
//                intentT.putExtra(Constants.CLUB_NAME, clubname);
//                intentT.putExtra(Constants.EVENTDATE, eventDate);
//                intentT.putExtra(Constants.IMAGE_URL, imageURL);
//                intentT.putExtra(Constants.TABLE_DISCOUNT, tablediscount);
//                intentT.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
//                startActivity(intentT);
//
//            }
//        });



    }


    private void populateEventsListForClub() {


        try{

            JSONObject loadClubEventsListFromDatabase = new JSONObject();
            loadClubEventsListFromDatabase.put("action", "getEventDetailsForOfferFromDatabase");
            loadClubEventsListFromDatabase.put(Constants.CLUB_ID, clubId);
            loadClubEventsListFromDatabase.put(Constants.EVENTDATE, eventDate);
            socketOperator.sendMessage(loadClubEventsListFromDatabase);


            while(!getData){
                SystemClock.sleep(1000);
            }

            clubEventDetailsItemList = new ArrayList<ClubEventsDetailsItem>();

            if(clubsEventListJsonArray != null){

                for(int i=0; i < clubsEventListJsonArray.length(); i++){
                    ClubEventsDetailsItem clubEventsDetailsItemObj = new ClubEventsDetailsItem();
                    JSONObject clubEventJObj = clubsEventListJsonArray.getJSONObject(i);




                    String clubid = clubEventJObj.getString(Constants.CLUB_ID);
                    String clubname = clubEventJObj.getString(Constants.CLUB_NAME);
                    djname = clubEventJObj.getString(Constants.DJ_NAME);
                    music = clubEventJObj.getString(Constants.MUSIC_TYPE);
                    String date = clubEventJObj.getString(Constants.EVENTDATE);
                    String imageURL = clubEventJObj.getString(Constants.IMAGE_URL);

                    clubEventsDetailsItemObj.setClubname(clubname);
                    clubEventsDetailsItemObj.setClubid(clubid);
                    clubEventsDetailsItemObj.setDjname(djname);
                    clubEventsDetailsItemObj.setMusic(music);
                    clubEventsDetailsItemObj.setDate(date);
                    clubEventsDetailsItemObj.setImageURL(imageURL);

                    clubEventDetailsItemList.add(clubEventsDetailsItemObj);



                }

            }

            ticketDetailsItemList = new ArrayList<TicketDetailsItem>();

            if(ticketDetailsListJsonArray != null){

                for(int i=0; i < ticketDetailsListJsonArray.length(); i++){
                    TicketDetailsItem ticketDetailsItemObj = new TicketDetailsItem();
                    JSONObject ticketDetailJObj = ticketDetailsListJsonArray.getJSONObject(i);


                    String clubid = ticketDetailJObj.getString(Constants.CLUB_ID);
                    String clubname = ticketDetailJObj.getString(Constants.CLUB_NAME);

                    String type = ticketDetailJObj.getString(Constants.TICKET_TYPE);
                    String cost = ticketDetailJObj.getString(Constants.COST);
                    String details = ticketDetailJObj.getString(Constants.DETAILS);

                    String day = ticketDetailJObj.getString(Constants.DAY);
                    String date = ticketDetailJObj.getString(Constants.EVENTDATE);
                    String totaltickets = ticketDetailJObj.getString(Constants.TOTAL_TICKETS);
                    String availbletickets = ticketDetailJObj.getString(Constants.AVAILBLE_TICKETS);


                    ticketDetailsItemObj.setClubname(clubname);
                    ticketDetailsItemObj.setClubid(clubid);
                    ticketDetailsItemObj.setType(type);
                    ticketDetailsItemObj.setCost(cost);
                    ticketDetailsItemObj.setDetails(details);
                    ticketDetailsItemObj.setDay(day);
                    ticketDetailsItemObj.setDate(date);
                    ticketDetailsItemObj.setTotaltickets(totaltickets);
                    ticketDetailsItemObj.setAvailbletickets(availbletickets);

                    ticketDetailsItemList.add(ticketDetailsItemObj);



                }

            }


        }catch (Exception ex){
            ex.printStackTrace();
        }

        // Create the adapter to convert the array to views
//        ClubsDetailListAdapter adapter = new ClubsDetailListAdapter(this, clubEventDetailsItemList);
//
//        adapter.setTicketDetailsListJsonArray(ticketDetailsListJsonArray);
//
//        Constants.setTicketDetailsItemList(ticketDetailsItemList);
//        // Attach the adapter to a ListView
//        ListView listView = (ListView) this.findViewById(R.id.lvUsers);
//        listView.setAdapter(adapter);
    }

    public void eventReceived(String message){
        // conver message to list
        if(message != null){

            try{
                JSONObject eventJObjX = new JSONObject(message);
                clubsEventListJsonArray = eventJObjX.getJSONArray("eventsDetailList");
                ticketDetailsListJsonArray = eventJObjX.getJSONArray("ticketDetailsList");


            }catch (Exception ex){
                ex.printStackTrace();

            }

        }


        getData = true;



    }


    @Override
    public void onBackPressed() {

        if(isNotification != null && !isNotification.equalsIgnoreCase("No")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        finish();

    }

    @Override
    public boolean onSupportNavigateUp(){
        if(isNotification != null && !isNotification.equalsIgnoreCase("No")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


        finish();
        return true;
    }





    private class FetchData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setCancelable(true);
            if(! OfferDisplayActivity.this.isFinishing()){
                progressDialog.show();
            }

            //linlaHeaderProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... f_url) {
            populateEventsListForClub();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            try {

                TextView guestList = (TextView) findViewById(R.id.guestList);
                TextView table = (TextView) findViewById(R.id.table);
                TextView pass = (TextView) findViewById(R.id.pass);


                final Intent intentG = new Intent(OfferDisplayActivity.this, BookGuestListActivity.class);
                final Intent intentP = new Intent(OfferDisplayActivity.this, BookPassActivity.class);
                final Intent intentT = new Intent(OfferDisplayActivity.this, TableBookingActivity.class);


                guestList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Intent intentx = new Intent(this, BookGuestListActivity.class);
                        intentG.putExtra(Constants.CLUB_ID, clubId);
                        intentG.putExtra(Constants.CLUB_NAME, clubname);
                        intentG.putExtra(Constants.EVENTDATE, eventDate);
                        intentG.putExtra(Constants.IMAGE_URL, imageURL);
                        intentG.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                        startActivity(intentG);

                    }
                });

                pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        intentP.putExtra(Constants.CLUB_ID, clubId);
                        intentP.putExtra(Constants.CLUB_NAME, clubname);
                        intentP.putExtra(Constants.EVENTDATE, eventDate);
                        intentP.putExtra(Constants.IMAGE_URL, imageURL);
                        intentP.putExtra(Constants.PASS_DISCOUNT, passdiscount);
                        intentP.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                        startActivity(intentP);

                    }
                });

                table.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent intent = new Intent(mContext, TableBookingActivity.class);
                        intentT.putExtra(Constants.CLUB_ID, clubId);
                        intentT.putExtra(Constants.CLUB_NAME, clubname);
                        intentT.putExtra(Constants.EVENTDATE, eventDate);
                        intentT.putExtra(Constants.IMAGE_URL, imageURL);
                        intentT.putExtra(Constants.TABLE_DISCOUNT, tablediscount);
                        intentT.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                        startActivity(intentT);

                    }
                });

            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }





        }
    }


    public class TaskCanceler implements Runnable{
        private AsyncTask task;

        public TaskCanceler(AsyncTask task) {
            this.task = task;
        }

        @Override
        public void run() {
//            int count = 5;
            if (task.getStatus() == AsyncTask.Status.RUNNING ){
//                while(count>0){
//                    Toast.makeText(getActivity(),
//                        "Your Internet Connection Seems Slow, We Are Still Trying !!!",
//                        Toast.LENGTH_LONG).show();
//                    count--;
//                    SystemClock.sleep(5*1000);
//                }

                //alert.show();
                task.cancel(true);
            }

        }
    }


}
