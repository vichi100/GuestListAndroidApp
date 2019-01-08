package com.application.club.guestlist.clubdetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.club.guestlist.R;
import com.application.club.guestlist.booking.BookGuestListActivity;
import com.application.club.guestlist.booking.BookPassActivity;
import com.application.club.guestlist.bookingTable.TableSelectionWebClientActivity;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;


public class ClubsDetailListAdapterRecyclerView extends RecyclerView.Adapter<ClubsDetailListAdapterRecyclerView.MyViewHolder> {
    private Context mContext;
    private ArrayList<TicketDetailsItem> ticketDetailsItemList;

    ArrayList<ClubEventsDetailsItem> clubEventsDetailsItems;




    private JSONArray ticketDetailsListJsonArray;
    private JSONArray  tableDetailsListJsonArray;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dj, music, day , date, guestList, table, pass;
        ImageView imgIcon;
        public View view;
//        TextView
//        TextView
//        TextView

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            dj = (TextView) view.findViewById(R.id.dj);
            music = (TextView) view.findViewById(R.id.musicx);
            day = (TextView) view.findViewById(R.id.day);
            date = (TextView) view.findViewById(R.id.date);
            guestList = (TextView) view.findViewById(R.id.guestList);
            table = (TextView) view.findViewById(R.id.table);
            pass = (TextView) view.findViewById(R.id.pass);
            imgIcon = (ImageView) view.findViewById(R.id.ivUserIcon);




        }
    }

    @Override
    public int getItemCount() {
        return clubEventsDetailsItems.size();
    }

    public ClubsDetailListAdapterRecyclerView(Context context, ArrayList<ClubEventsDetailsItem> clubEventsDetailsItems) {
        //super(context, 0, clubEventsDetailsItems);
        this.mContext= context;
        this.clubEventsDetailsItems = clubEventsDetailsItems;
     }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_details_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ClubEventsDetailsItem clubEventsDetailsItem = clubEventsDetailsItems.get(position);
        holder.dj.setText(clubEventsDetailsItem.getDjname());
        holder.music.setText(clubEventsDetailsItem.getMusic());
        holder.date.setText(clubEventsDetailsItem.getDate());
        String day = UtillMethods.getDayFromDate(clubEventsDetailsItem.getDate());
        holder.day.setText(day);
        String imgURL = clubEventsDetailsItem.getImageURL();
        Glide.with(mContext)
                .load(Constants.HTTP_URL+imgURL)
                //.placeholder(R.drawable.circular_progress_bar)
                //.apply(options)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.skipMemoryCache(true)
                .into(holder.imgIcon);


        Date eventdateAsDate = UtillMethods.stringToDate(clubEventsDetailsItem.getDate());
        Date todayDate = UtillMethods.getTodayDateAsDate();
        int x = eventdateAsDate.compareTo(todayDate);
        if(x >= 1 || x == 0){
            holder.view.setAlpha((float) 1.0);
            holder.table.setEnabled(true);
            holder.pass.setEnabled(true);
            holder.guestList.setEnabled(true);
        }else if(x <= -1){
            holder.view.setAlpha((float) 0.5);
            holder.view.setBackgroundColor(Color.GRAY);
            holder.table.setEnabled(false);
            holder.pass.setEnabled(false);
            holder.guestList.setEnabled(false);
        }

        holder.guestList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, BookGuestListActivity.class);
                intent.putExtra(Constants.CLUB_ID, clubEventsDetailsItem.getClubid());
                intent.putExtra(Constants.CLUB_NAME, clubEventsDetailsItem.getClubname());
                intent.putExtra(Constants.EVENTDATE, clubEventsDetailsItem.getDate());
                intent.putExtra(Constants.IMAGE_URL, clubEventsDetailsItem.getImageURL());
                intent.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                mContext.startActivity(intent);

            }
        });


        holder.pass.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mContext, BookPassActivity.class);
                 intent.putExtra(Constants.CLUB_ID, clubEventsDetailsItem.getClubid());
                 intent.putExtra(Constants.CLUB_NAME, clubEventsDetailsItem.getClubname());
                 intent.putExtra(Constants.EVENTDATE, clubEventsDetailsItem.getDate());
                 intent.putExtra(Constants.IMAGE_URL, clubEventsDetailsItem.getImageURL());
                 intent.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
                 mContext.startActivity(intent);

             }
         });

        holder.table.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mContext, TableSelectionWebClientActivity.class);
                 intent.putExtra(Constants.CLUB_ID, clubEventsDetailsItem.getClubid());
                 intent.putExtra(Constants.CLUB_NAME, clubEventsDetailsItem.getClubname());
                 intent.putExtra(Constants.EVENTDATE, clubEventsDetailsItem.getDate());
                 intent.putExtra(Constants.IMAGE_URL, clubEventsDetailsItem.getImageURL());
                 intent.putExtra(Constants.TICKET_DETAILS, tableDetailsListJsonArray.toString());
                 mContext.startActivity(intent);

             }
         });


    }


//     public View getView(int position, View convertView, ViewGroup parent) {
//
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//           convertView = LayoutInflater.from(getContext()).inflate(R.layout.club_details_list_item, parent, false);
//        }
//
//         //ticketDetailsItemList = Constants.getTicketDetailsItemList();
//
//         // Get the data item for this position
//         ClubEventsDetailsItem clubEventsDetailsItem = getItem(position);
//         final String clubId = clubEventsDetailsItem.getClubid();
//         final String clubName = clubEventsDetailsItem.getClubname();
//         final String eventdate = clubEventsDetailsItem.getDate();
//         final String imgURL = clubEventsDetailsItem.getImageURL();
//
//
//
//         ImageView imgIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
//         //Picasso.with(this.getContext()).load(Constants.HTTP_URL+imgURL).into(imgIcon);
//         Glide.with(this.getContext())
//                 .load(Constants.HTTP_URL+imgURL)
//                 //.placeholder(R.drawable.circular_progress_bar)
//                 //.apply(options)
//                 .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                 //.skipMemoryCache(true)
//                 .into(imgIcon);
//
//         TextView tvDay = (TextView) convertView.findViewById(R.id.day);
//         String day = UtillMethods.getDayFromDate(clubEventsDetailsItem.getDate());
//         tvDay.setText(UtillMethods.toCamelCase(day));
//         TextView tvDate = (TextView) convertView.findViewById(R.id.date);
//         tvDate.setText(eventdate);
//
//         TextView tvDj = (TextView) convertView.findViewById(R.id.dj);
//         tvDj.setText(UtillMethods.toCamelCase(clubEventsDetailsItem.getDjname()));
//         TextView tvMusic = (TextView) convertView.findViewById(R.id.musicx);
//         tvMusic.setText(UtillMethods.toCamelCase(clubEventsDetailsItem.getMusic()));
//
//         // Lookup view for data population
//        TextView guestList = (TextView) convertView.findViewById(R.id.guestList);
//        TextView table = (TextView) convertView.findViewById(R.id.table);
//         TextView pass = (TextView) convertView.findViewById(R.id.pass);
//
//         Date eventdateAsDate = UtillMethods.stringToDate(eventdate);
//         Date todayDate = UtillMethods.getTodayDateAsDate();
//         int x = eventdateAsDate.compareTo(todayDate);
//         if(x >= 1 || x == 0){
//             convertView.setAlpha((float) 1.0);
//             table.setEnabled(true);
//             pass.setEnabled(true);
//             guestList.setEnabled(true);
//         }else if(x <= -1){
//             convertView.setAlpha((float) 0.5);
//             convertView.setBackgroundColor(Color.GRAY);
//             table.setEnabled(false);
//             pass.setEnabled(false);
//             guestList.setEnabled(false);
//         }
//
//         //if(todayDate.getYear() >= eventdateAsDate.getYear() && todayDate.getMonth()>= eventdateAsDate.getMonth() && todayDate.getDate()<=eventdateAsDate.getDate() ){
////         if(eventdateAsDate.getYear() >= todayDate.getYear() && eventdateAsDate.getMonth()>= todayDate.getMonth()
////                 && eventdateAsDate.getDate()>=todayDate.getDate() ){
////
////                 convertView.setAlpha((float) 1.0);
////             table.setEnabled(true);
////             pass.setEnabled(true);
////             guestList.setEnabled(true);
////         }else{
////             convertView.setAlpha((float) 0.5);
////             convertView.setBackgroundColor(Color.GRAY);
////             table.setEnabled(false);
////             pass.setEnabled(false);
////             guestList.setEnabled(false);
////         }
//
//
//         guestList.setOnClickListener(new OnClickListener() {
//             @Override
//             public void onClick(View v) {
//
//                 Intent intent = new Intent(mContext, BookGuestListActivity.class);
//                 intent.putExtra(Constants.CLUB_ID, clubId);
//                 intent.putExtra(Constants.CLUB_NAME, clubName);
//                 intent.putExtra(Constants.EVENTDATE, eventdate);
//                 intent.putExtra(Constants.IMAGE_URL, imgURL);
//                 intent.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
//                 mContext.startActivity(intent);
//
//             }
//         });
//
//         pass.setOnClickListener(new OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent intent = new Intent(mContext, BookPassActivity.class);
//                 intent.putExtra(Constants.CLUB_ID, clubId);
//                 intent.putExtra(Constants.CLUB_NAME, clubName);
//                 intent.putExtra(Constants.EVENTDATE, eventdate);
//                 intent.putExtra(Constants.IMAGE_URL, imgURL);
//                 intent.putExtra(Constants.TICKET_DETAILS, ticketDetailsListJsonArray.toString());
//                 mContext.startActivity(intent);
//
//             }
//         });
//
//         table.setOnClickListener(new OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent intent = new Intent(mContext, TableSelectionWebClientActivity.class);
//                 intent.putExtra(Constants.CLUB_ID, clubId);
//                 intent.putExtra(Constants.CLUB_NAME, clubName);
//                 intent.putExtra(Constants.EVENTDATE, eventdate);
//                 intent.putExtra(Constants.IMAGE_URL, imgURL);
//                 intent.putExtra(Constants.TICKET_DETAILS, tableDetailsListJsonArray.toString());
//                 mContext.startActivity(intent);
//
//             }
//         });
//
//        // Return the completed view to render on screen
//
//
//        return convertView;
//    }

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
