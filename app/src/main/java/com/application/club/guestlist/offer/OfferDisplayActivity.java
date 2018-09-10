package com.application.club.guestlist.offer;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ArrayList<ClubEventsDetailsItem> clubEventDetailsItemList;
    private ArrayList<TicketDetailsItem> ticketDetailsItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_display_activity);
        getSupportActionBar().setTitle("Offer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        String day = UtillMethods.getDayFromDate(eventDate);

        TextView daytv = (TextView) findViewById(R.id.day);
        daytv.setText(day);

        TextView datetv = (TextView) findViewById(R.id.date);
        datetv.setText(eventDate);

        TextView clubNametv = (TextView) findViewById(R.id.club);
        clubNametv.setText(clubname);

        populateEventsListForClub();

        TextView djtv = (TextView) findViewById(R.id.dj);
        djtv.setText(djname);

        TextView musictv = (TextView) findViewById(R.id.musicx);
        musictv.setText(music);

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

        Picasso.with(this.getApplicationContext()).load(Constants.HTTP_URL+imageURL).into(mainImagetv);

//        TextView timetv = (TextView) findViewById(R.id.time);
//        timetv.setText("TIME    "+startTime);

        TextView guestList = (TextView)findViewById(R.id.guestList);
        TextView table = (TextView) findViewById(R.id.table);
        TextView pass = (TextView) findViewById(R.id.pass);




        final Intent intentG = new Intent(this, BookGuestListActivity.class);
        final Intent intentP = new Intent(this, BookPassActivity.class);
        final Intent intentT = new Intent(this, TableBookingActivity.class);


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


//    @Override
//    public void onBackPressed() {
//
//        finish();
//        Intent intent = new Intent(OfferDisplayActivity.this, MainActivity.class);
//        startActivity(intent);
//    }

    @Override
    public boolean onSupportNavigateUp(){
        if(isNotification != null && !isNotification.isEmpty()){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


        finish();
        return true;
    }
}
