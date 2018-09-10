package com.application.club.guestlist.profile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.club.guestlist.R;
import com.application.club.guestlist.login.AboutUsActivity;
import com.application.club.guestlist.utils.Constants;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vichi on 12/02/18.
 */

public class ProfileScreenFragment extends Fragment {

    public static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    String custmerMobile;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.profile_screen,null,false);


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);



        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String custmerName = settings.getString("name","");
        custmerMobile = settings.getString("mobile","");

        TextView cnametv = (TextView) getActivity().findViewById(R.id.user_profile_name);
        cnametv.setText(custmerName);
        TextView mobiletv = (TextView) getActivity().findViewById(R.id.user_profile_mobile);
        mobiletv.setText("+91 "+custmerMobile);

        TextView callustc = (TextView) getActivity().findViewById(R.id.call_us);
        TextView aboutusTv = (TextView) getActivity().findViewById(R.id.about_us);

        ImageView imgIcon = (ImageView) getActivity().findViewById(R.id.header_cover_image);

        Picasso.with(getActivity()).load(R.drawable.profilebackground)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imgIcon);


        callustc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+91"+"9833097595"));

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //You already have permission
                    try {
                        startActivity(callIntent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }
                //startActivity(callIntent);

            }

        });

        aboutusTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent abtIntent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(abtIntent);

            }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:91"+custmerMobile));
                    try {
                        startActivity(callIntent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }

                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }

}


