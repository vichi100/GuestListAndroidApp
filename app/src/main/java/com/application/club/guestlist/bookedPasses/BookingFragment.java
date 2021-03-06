package com.application.club.guestlist.bookedPasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.application.club.guestlist.R;
import com.application.club.guestlist.service.EventListener;
import com.application.club.guestlist.service.SocketOperator;
import com.application.club.guestlist.utils.Constants;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Oclemmy on 5/10/2016 for ProgrammingWizards Channel and http://www.Camposha.com.
 */
public class BookingFragment extends ListFragment implements AdapterView.OnItemClickListener, EventListener {



    BookedPassesListAdapter adapter;
    private List<PassRowItem> bookedTicketRowItems;
    static JSONArray bookedTicketListJsonArray;

    boolean getTicketList = false;

    SocketOperator socketOperator  = new SocketOperator(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        return inflater.inflate(R.layout.booked_pass_frgament,null,false);

//        ListView lv= (ListView) rootView.findViewById(R.id.dramaListView);
//        ArrayAdapter adapter=new ArrayAdapter(this.getActivity(),android.R.layout.simple_list_item_1,drama);
//        lv.setAdapter(adapter);
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), drama[position], Toast.LENGTH_SHORT).show();
//            }
//        });
//        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //ListView lv = (ListView)getActivity().findViewById(android.R.id.list);
//        TextView emptyView = new TextView(getActivity().getApplicationContext());
//        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
//        emptyView.setTextColor(Color.WHITE);
//        emptyView.setText("hi");
//        emptyView.setTextSize(20);
//        emptyView.setVisibility(View.GONE);
//        emptyView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//
//
        //lv.setEmptyView(getView().findViewById(android.R.id.empty));

        try{

            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String custmerName = null;
            String custmerMobile = null;
            String customerId = null;
            if (settings.getString("logged", "").toString().equals("logged")) {

                custmerName = settings.getString("name", "");
                custmerMobile = settings.getString("mobile", "");
                customerId = settings.getString(Constants.CUSTOMERID,"");
            }

            JSONObject getbookedTicketFromDatabase = new JSONObject();
            getbookedTicketFromDatabase.put("action", "getbookedTicketFromDatabase");
            getbookedTicketFromDatabase.put("mobile", custmerMobile);
            getbookedTicketFromDatabase.put(Constants.CUSTOMERID, customerId);

            socketOperator.sendMessage(getbookedTicketFromDatabase);

            while(!getTicketList){
                SystemClock.sleep(1000);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }





        bookedTicketRowItems = new ArrayList<PassRowItem>();

        try{

            if(bookedTicketListJsonArray != null){
                for(int i=0; i < bookedTicketListJsonArray.length(); i++){
                    PassRowItem bookedTicketRowObj = new PassRowItem();

                    JSONObject bookedTicketjObj = bookedTicketListJsonArray.getJSONObject(i);

                    String clubid = bookedTicketjObj.getString(Constants.CLUB_ID);
                    String clubname = bookedTicketjObj.getString(Constants.CLUB_NAME);
                    String cutomername = bookedTicketjObj.getString(Constants.CUSTOMERNAME);
                    String mobile = bookedTicketjObj.getString(Constants.MOBILE);
                    String customerId = bookedTicketjObj.getString(Constants.CUSTOMERID);
                    String QRnumber = bookedTicketjObj.getString(Constants.QRNUMBER);
                    String tickettype = bookedTicketjObj.getString(Constants.TICKETTYPE);
                    String eventDate = bookedTicketjObj.getString(Constants.EVENT_DATE);
                    String cost = bookedTicketjObj.getString(Constants.COSTAFTERDISCOUNT);
                    String remainingAmt = bookedTicketjObj.getString(Constants.REMAINING_AMOUNT);
                    String ticketDetails = bookedTicketjObj.getString(Constants.TICKET_DETAILS);

                    bookedTicketRowObj.setClubid(clubid);
                    bookedTicketRowObj.setClubname(clubname);
                    bookedTicketRowObj.setCutomername(cutomername);
                    bookedTicketRowObj.setMobile(mobile);
                    bookedTicketRowObj.setCustomerId(customerId);
                    bookedTicketRowObj.setQRnumber(QRnumber);
                    bookedTicketRowObj.setTickettype(tickettype);
                    bookedTicketRowObj.setEventDate(eventDate);
                    bookedTicketRowObj.setCost(cost);
                    bookedTicketRowObj.setRemainingAmount(remainingAmt);
                    bookedTicketRowObj.setTicketDetails(ticketDetails);

                    bookedTicketRowItems.add(bookedTicketRowObj);




                }

            }

        }catch (Exception ex){

        }



        adapter = new BookedPassesListAdapter(getActivity(), bookedTicketRowItems);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        PassRowItem items = bookedTicketRowItems.get(position);


        Intent intent = new Intent(getActivity(), DisplayPassActivity.class);
        intent.putExtra(Constants.CLUB_NAME, items.getClubname());
        intent.putExtra(Constants.EVENTDATE, items.getEventDate());
        intent.putExtra(Constants.TICKET_DETAILS, items.getTicketDetails());
        intent.putExtra(Constants.QRNUMBER, items.getQRnumber());
        intent.putExtra(Constants.TICKET_TYPE, items.getTickettype());

        intent.putExtra(Constants.COSTAFTERDISCOUNT, items.getCost());
        intent.putExtra(Constants.REMAINING_AMOUNT, items.getRemainingAmount());
        startActivity(intent);

    }


    public void eventReceived(String message){
        // conver message to list
        if(message != null){

            try{
                JSONObject eventJObjX = new JSONObject(message);
                bookedTicketListJsonArray = eventJObjX.getJSONArray("bookedTicketList");



            }catch (Exception ex){
                ex.printStackTrace();

            }

        }


        getTicketList = true;



    }
}
