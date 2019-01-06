package com.application.club.guestlist.clubdetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.club.guestlist.bookingTable.TableSelectionWebClientActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.application.club.guestlist.R;
import com.application.club.guestlist.booking.BookGuestListActivity;
import com.application.club.guestlist.booking.BookPassActivity;
import com.application.club.guestlist.bookingTable.TableBookingActivity;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;


public class ClubsDetailListAdapter extends ArrayAdapter<ClubEventsDetailsItem> {
    private Context mContext;
    private ArrayList<TicketDetailsItem> ticketDetailsItemList;




    private JSONArray ticketDetailsListJsonArray;
    private JSONArray  tableDetailsListJsonArray;

    public ClubsDetailListAdapter(Context context, ArrayList<ClubEventsDetailsItem> clubEventsDetailsItems) {
        super(context, 0, clubEventsDetailsItems);
        this.mContext= context;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.club_details_list_item, parent, false);
        }

         //ticketDetailsItemList = Constants.getTicketDetailsItemList();

         // Get the data item for this position
         ClubEventsDetailsItem clubEventsDetailsItem = getItem(position);
         final String clubId = clubEventsDetailsItem.getClubid();
         final String clubName = clubEventsDetailsItem.getClubname();
         final String eventdate = clubEventsDetailsItem.getDate();
         final String imgURL = clubEventsDetailsItem.getImageURL();



         ImageView imgIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
         //Picasso.with(this.getContext()).load(Constants.HTTP_URL+imgURL).into(imgIcon);
         Glide.with(this.getContext())
                 .load(Constants.HTTP_URL+imgURL)
                 //.placeholder(R.drawable.circular_progress_bar)
                 //.apply(options)
                 .diskCacheStrategy(DiskCacheStrategy.RESULT)
                 //.skipMemoryCache(true)
                 .into(imgIcon);

         TextView tvDay = (TextView) convertView.findViewById(R.id.day);
         String day = UtillMethods.getDayFromDate(clubEventsDetailsItem.getDate());
         tvDay.setText(UtillMethods.toCamelCase(day));
         TextView tvDate = (TextView) convertView.findViewById(R.id.date);
         tvDate.setText(eventdate);

         TextView tvDj = (TextView) convertView.findViewById(R.id.dj);
         tvDj.setText(UtillMethods.toCamelCase(clubEventsDetailsItem.getDjname()));
         TextView tvMusic = (TextView) convertView.findViewById(R.id.musicx);
         tvMusic.setText(UtillMethods.toCamelCase(clubEventsDetailsItem.getMusic()));

         // Lookup view for data population
        TextView guestList = (TextView) convertView.findViewById(R.id.guestList);
        TextView table = (TextView) convertView.findViewById(R.id.table);
         TextView pass = (TextView) convertView.findViewById(R.id.pass);

         Date eventdateAsDate = UtillMethods.stringToDate(eventdate);
         Date todayDate = UtillMethods.getTodayDateAsDate();
         int x = eventdateAsDate.compareTo(todayDate);
         if(x >= 1 || x == 0){
             convertView.setAlpha((float) 1.0);
             table.setEnabled(true);
             pass.setEnabled(true);
             guestList.setEnabled(true);
         }else if(x <= -1){
             convertView.setAlpha((float) 0.5);
             convertView.setBackgroundColor(Color.GRAY);
             table.setEnabled(false);
             pass.setEnabled(false);
             guestList.setEnabled(false);
         }

         //if(todayDate.getYear() >= eventdateAsDate.getYear() && todayDate.getMonth()>= eventdateAsDate.getMonth() && todayDate.getDate()<=eventdateAsDate.getDate() ){
//         if(eventdateAsDate.getYear() >= todayDate.getYear() && eventdateAsDate.getMonth()>= todayDate.getMonth()
//                 && eventdateAsDate.getDate()>=todayDate.getDate() ){
//
//                 convertView.setAlpha((float) 1.0);
//             table.setEnabled(true);
//             pass.setEnabled(true);
//             guestList.setEnabled(true);
//         }else{
//             convertView.setAlpha((float) 0.5);
//             convertView.setBackgroundColor(Color.GRAY);
//             table.setEnabled(false);
//             pass.setEnabled(false);
//             guestList.setEnabled(false);
//         }


         guestList.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent = new Intent(mContext, BookGuestListActivity.class);
                 intent.putExtra(Constants.CLUB_ID, clubId);
                 intent.putExtra(Constants.CLUB_NAME, clubName);
                 intent.putExtra(Constants.EVENTDATE, eventdate);
                 intent.putExtra(Constants.IMAGE_URL, imgURL);
                 intent.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                 mContext.startActivity(intent);

             }
         });

         pass.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mContext, BookPassActivity.class);
                 intent.putExtra(Constants.CLUB_ID, clubId);
                 intent.putExtra(Constants.CLUB_NAME, clubName);
                 intent.putExtra(Constants.EVENTDATE, eventdate);
                 intent.putExtra(Constants.IMAGE_URL, imgURL);
                 intent.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                 mContext.startActivity(intent);

             }
         });

         table.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mContext, TableSelectionWebClientActivity.class);
                 intent.putExtra(Constants.CLUB_ID, clubId);
                 intent.putExtra(Constants.CLUB_NAME, clubName);
                 intent.putExtra(Constants.EVENTDATE, eventdate);
                 intent.putExtra(Constants.IMAGE_URL, imgURL);
                 intent.putExtra(Constants.TICKET_DETAILS, tableDetailsListJsonArray.toString());
                 mContext.startActivity(intent);

             }
         });

        // Return the completed view to render on screen


        return convertView;
    }

    public JSONArray getTicketDetailsListJsonArray() {
        return ticketDetailsListJsonArray;
    }

    public void setTicketDetailsListJsonArray(JSONArray ticketDetailsListJsonArray) {
        this.ticketDetailsListJsonArray = ticketDetailsListJsonArray;
    }


    public JSONArray getTableDetailsListJsonArray() {
        return tableDetailsListJsonArray;
    }

    public void setTableDetailsListJsonArray(JSONArray tableDetailsListJsonArray) {
        this.tableDetailsListJsonArray = tableDetailsListJsonArray;
    }
}
