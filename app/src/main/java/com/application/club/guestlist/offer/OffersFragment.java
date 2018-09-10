package com.application.club.guestlist.offer;

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
import com.application.club.guestlist.utils.UtillMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by vichi on 07/03/18.
 */

public class OffersFragment extends ListFragment implements AdapterView.OnItemClickListener, EventListener {



    OffersListAdapter adapter;
    private List<OfferRowItem> offerRowItems;
    static JSONArray offerListJsonArray;

    boolean getTicketList = false;

    SocketOperator socketOperator  = new SocketOperator(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.offers_frgament,null,false);

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

        try{

            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String custmerName = null;
            String custmerMobile = null;
            String customerId = null;
            String city = null;
            if (settings.getString("logged", "").toString().equals("logged")) {

                custmerName = settings.getString("name", "");
                custmerMobile = settings.getString("mobile", "");
                customerId = settings.getString(Constants.CUSTOMERID,"");
                city = settings.getString(Constants.CITY,"");
            }

            JSONObject getOffersFromDatabase = new JSONObject();
            getOffersFromDatabase.put("action", "getOffersFromDatabase");
            getOffersFromDatabase.put(Constants.CITY, city);

            socketOperator.sendMessage(getOffersFromDatabase);

            while(!getTicketList){
                SystemClock.sleep(1000);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }





        offerRowItems = new ArrayList<OfferRowItem>();

        try{



            if(offerListJsonArray != null){
                for(int i = 0; i < offerListJsonArray.length(); i++){
                    OfferRowItem offerItemRowObj = new OfferRowItem();

                    JSONObject offerjObj = offerListJsonArray.getJSONObject(i);

                    String clubid = offerjObj.getString(Constants.CLUB_ID);
                    String clubname = offerjObj.getString(Constants.CLUB_NAME);
                    String cityx = offerjObj.getString(Constants.CITY);
                    String location = offerjObj.getString(Constants.LOACTION);
                    String offerid = offerjObj.getString(Constants.OFFERID);
                    String offerName = offerjObj.getString(Constants.OFFERNAME);

                    String offerForTable = offerjObj.getString(Constants.OFFERFORTABLE);
                    String offerForPass = offerjObj.getString(Constants.OFFERFORPASS);
                    String eventName = offerjObj.getString(Constants.EVENTNAME);

                    String djName = offerjObj.getString(Constants.DJ_NAME);
                    String music = offerjObj.getString(Constants.MUSIC);
                    String eventDate = offerjObj.getString(Constants.EVENTDATE);

                    String imageURL = offerjObj.getString(Constants.IMAGE_URL);
                    String starttime = offerjObj.getString(Constants.STARTTIME);
                    String timetoexpire = offerjObj.getString(Constants.TIME_TO_EXPIRE);
                    String remaingTime = UtillMethods.getRemaingTime(starttime, timetoexpire);

                    offerItemRowObj.setClubid(clubid);
                    offerItemRowObj.setClubname(clubname);
                    offerItemRowObj.setCity(cityx);
                    offerItemRowObj.setLocation(location);
                    offerItemRowObj.setEventdate(eventDate);
                    offerItemRowObj.setOfferid(offerid);
                    offerItemRowObj.setOfferName(offerName);
                    offerItemRowObj.setOfferForTable(offerForTable);
                    offerItemRowObj.setOfferForPass(offerForPass);
                    offerItemRowObj.setEventName(eventName);
                    offerItemRowObj.setDjname(djName);
                    offerItemRowObj.setMusic(music);
                    offerItemRowObj.setImageURL(imageURL);
                    offerItemRowObj.setStarttime(starttime);
                    offerItemRowObj.setTimetoexpire(remaingTime);

                    offerRowItems.add(offerItemRowObj);
                }

            }

        }catch (Exception ex){

        }



        adapter = new OffersListAdapter(getActivity(), offerRowItems);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        OfferRowItem items = offerRowItems.get(position);


        Intent intent = new Intent(getActivity(), OfferDisplayActivity.class);
        intent.putExtra(Constants.CLUB_ID, items.getClubid());
        intent.putExtra(Constants.CLUB_NAME, items.getClubname());
        intent.putExtra(Constants.LOACTION, items.getLocation());
        intent.putExtra(Constants.DJNAME, items.getDjname());
        intent.putExtra(Constants.MUSIC, items.getMusic());
        intent.putExtra(Constants.PASS_DISCOUNT, items.getOfferForPass());
        intent.putExtra(Constants.TABLE_DISCOUNT, items.getOfferForTable());
        intent.putExtra(Constants.EVENT_DATE, items.getEventdate());
        intent.putExtra(Constants.IMAGE_URL, items.getImageURL());
        intent.putExtra(Constants.IS_NOTIFICATION, "No");
        startActivity(intent);

    }


    public void eventReceived(String message){
        // conver message to list
        if(message != null){

            try{
                JSONObject eventJObjX = new JSONObject(message);
                offerListJsonArray = eventJObjX.getJSONArray("offersList");



            }catch (Exception ex){
                ex.printStackTrace();

            }

        }


        getTicketList = true;



    }
}
