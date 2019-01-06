package com.application.club.guestlist.login;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;


import com.application.club.guestlist.MainActivity;
import com.application.club.guestlist.R;
import com.application.club.guestlist.booking.BookGuestListActivity;
import com.application.club.guestlist.offer.OfferDisplayActivity;
import com.application.club.guestlist.qrcode.QRCodeActivity;
import com.application.club.guestlist.service.EventListener;
import com.application.club.guestlist.service.SocketOperator;
import com.application.club.guestlist.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;

import dmax.dialog.SpotsDialog;

/**
 * Created by vichi on 11/01/18.
 */

public class LoginActivity extends  AppCompatActivity
        implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,  EventListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    Button button;

    SocketOperator socketOperator;//  = new SocketOperator(this);

    boolean isCustomerCreated = false;

    boolean startMainActivityFlag = true;

    String customerId = null;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    String latlong;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private LoginButton loginButton;
    private CallbackManager callbackManager;


    AlertDialog alert;
    private android.app.AlertDialog progressDialog;

    Profile profile;

    int waitingCount = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        progressDialog= new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5*60*1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



        /*
         * Check if we successfully logged in before.
         * If we did, redirect to home page
         */
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);

        if (settings.getString("logged", "").toString().equals("logged")) {

            Bundle bundle = getIntent().getExtras();
            Intent intentOffer = new Intent(this, OfferDisplayActivity.class);
            if (bundle != null) {
                boolean startOfferActivity = false;
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d(TAG, "Key: " + key + " Value: " + value.toString());
                    intentOffer.putExtra(key, value.toString());
                    if(key.equalsIgnoreCase("clubid")){
                        startOfferActivity = true;
                        startMainActivityFlag = false;
                    }


                }
                if(startOfferActivity == true){
                    intentOffer.putExtra(Constants.IS_NOTIFICATION, "notification");
                    startActivity(intentOffer);
                }

            }


            if(startMainActivityFlag){

                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                String custmerName = settings.getString("name","");
                String custmerEmail = settings.getString("email","");
                intent.putExtra(Constants.CUSTOMERNAME, custmerName);
                intent.putExtra(Constants.MOBILE, custmerEmail);
                startActivity(intent);

            }

        }


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.setReadPermissions("public_profile", "email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {


                if(Profile.getCurrentProfile() == null) {

                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            //Log.v("facebook - profile", currentProfile.getFirstName());
                            profile = currentProfile;
                            //goMainScreen();

                            Handler handler = new Handler();
                            LoginActivity.FetchData fdTask = new LoginActivity.FetchData();
                            fdTask.execute();

                            LoginActivity.TaskCanceler taskCanceler = new LoginActivity.TaskCanceler(fdTask);

                            handler.postDelayed(taskCanceler, 5*1000);
                            mProfileTracker.stopTracking();

                        }
                    };

                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.

                }else {
                    AccessToken accessToken = loginResult.getAccessToken();
                    profile = Profile.getCurrentProfile();
//                    String id = profile.getId();
//                    String name = profile.getName();
                    //goMainScreen();

                    Handler handler = new Handler();
                    LoginActivity.FetchData fdTask = new LoginActivity.FetchData();
                    fdTask.execute();

                    LoginActivity.TaskCanceler taskCanceler = new LoginActivity.TaskCanceler(fdTask);

                    handler.postDelayed(taskCanceler, 60*1000);

                }



                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try {
                                    String Name = object.getString("name");

                                    String FEmail = object.getString("email");
                                    Log.v("Email = ", " " + FEmail);
                                    Toast.makeText(getApplicationContext(), "Name " + Name, Toast.LENGTH_LONG).show();
                                    //goMainScreen(object);
                                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    //editor.putString("logged", "logged");
                                    //editor.putString("name", Name);
                                    editor.putString("email", FEmail);
                                    //editor.putString(Constants.CUSTOMERID, customerId);
                                    editor.commit();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });



//        // Locate the button in activity_main.xml
//        button = (Button) findViewById(R.id.next);
//
//        // Capture button clicks
//        button.setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//
//                EditText cname = (EditText) findViewById(R.id.cname);
//                final String name = cname.getText().toString();
//                final EditText cmobile = (EditText) findViewById(R.id.cmobile);
//                final String mobile = cmobile.getText().toString();
//                EditText ccmobile = (EditText) findViewById(R.id.ccmobile);
//                final String confirmMobile = ccmobile.getText().toString();
//
//                if(null != name && !name.trim().equalsIgnoreCase("")) {
//
//                    if (null != mobile && mobile.trim().length() >= 10) {
//
//                        if (null != confirmMobile && confirmMobile.trim().length() >= 10 && mobile.equalsIgnoreCase(confirmMobile)) {
//
//
//                            /*
//                             * So login information is correct,
//                             * we will save the Preference data
//                             * and redirect to next class / home
//                             */
//                            try{
//
//                                CheckBox chkBox = (CheckBox) findViewById(R.id.chkIos);
//                                if(!chkBox.isChecked()){
//                                    Toast.makeText(LoginActivity.this, "Please confirm your age is 21", Toast.LENGTH_LONG).show();
//                                    return;
//                                }
//                                JSONObject customerDetails = new JSONObject();
//                                customerDetails.put("action", "createNewCustomer");
//                                customerDetails.put(Constants.CUSTOMERNAME, name);
//                                customerDetails.put(Constants.MOBILE, confirmMobile);
//
//                                socketOperator.sendMessage(customerDetails);
//
//                                while(!isCustomerCreated){
//                                    SystemClock.sleep(1000);
//                                }
//
//
//
//                            }catch (Exception ex){
//                                ex.printStackTrace();
//                            }
//
//
//                            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putString("logged", "logged");
//                            editor.putString("name", name);
//                            editor.putString("mobile", confirmMobile);
//                            editor.putString(Constants.CUSTOMERID, customerId);
//                            editor.commit();
//
//
//
//                            Intent intent = new Intent(LoginActivity.this,
//                                    SelectCityActivity.class);
//
////                            intent.putExtra(Constants.CUSTOMERNAME, name);
////                            intent.putExtra(Constants.MOBILE, mobile);
//
//                            startActivity(intent);
//
//                        }else{
//                            Toast.makeText(LoginActivity.this, "Mobile Number are not matching!", Toast.LENGTH_LONG).show();
//                        }
//
//
//                    }else{
//                        Toast.makeText(LoginActivity.this, "Pleae Enter Correct Mobile Number !", Toast.LENGTH_LONG).show();
//                    }
//
//                }else{
//                    Toast.makeText(LoginActivity.this, "Pleae Enter Name !", Toast.LENGTH_LONG).show();
//                }
//
//
//            }
//        });


    }



    private void goMainScreen(){

        String name = profile.getName();
        String email = profile.getId();

        try{

            JSONObject customerDetails = new JSONObject();
            customerDetails.put("action", "createNewCustomer");
            customerDetails.put(Constants.CUSTOMERNAME, name);
            customerDetails.put(Constants.EMAIL, email);// this is FB id

            socketOperator  = new SocketOperator(this);
            socketOperator.sendMessage(customerDetails);

            while(!isCustomerCreated && waitingCount > 0){
                SystemClock.sleep(100);
                waitingCount = waitingCount -1;
            }



        }catch (Exception ex){
            ex.printStackTrace();
        }




//        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("logged", "logged");
//        editor.putString("name", name);
//        //editor.putString("email", email);
//        editor.putString(Constants.CUSTOMERID, customerId);
//        editor.commit();
//
//
//
//        Intent intent = new Intent(LoginActivity.this,
//                SelectCityActivity.class);
//
//        startActivity(intent);


//        Intent intent = new Intent(this, com.application.club.guestlist.fblogin.MainActivity.class);
//        intent.putExtra("name", object.getString("name"));
//        intent.putExtra("email", object.getString("email"));
//        intent.putExtra("email", object.getString("id"));
//        //intent.putExtra("imageUrl", profile.get);
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    public void eventReceived(String message){
        // conver message to list
        if(message != null){

            try{
                if(message != null){
                    JSONObject customerIdjObj = new JSONObject(message);
                    customerId = customerIdjObj.getString(Constants.CUSTOMERID);

                    isCustomerCreated = true;
                }



            }catch (Exception ex){
                ex.printStackTrace();

            }

        }
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        //Toast.makeText(LoginActivity.this, "latlong: "+latlong, Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onPause() {
//        ((LocationManager)getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        ((LocationManager)getSystemService(Context.LOCATION_SERVICE))
//                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 , 1, this);
//        super.onResume();
//    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
//            String strLocation =
//                    DateFormat.getTimeInstance().format(location.getTime()) + "\n" +
//                            "Latitude=" + location.getLatitude() + "\n" +
//                            "Longitude=" + location.getLongitude();
//
//            textAutoUpdateLocation.setText(strLocation);

            latlong = location.getLatitude()+","+location.getLongitude();
//            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
//
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString("latlong", latlong);
//
//            editor.commit();
//
//            Toast.makeText(LoginActivity.this, "latlong: "+latlong, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);



            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // here we go you can see current lat long.
//            Log.e(TAG, "onConnected: " + String.valueOf(mLastLocation.getLatitude()) + ":" + String.valueOf(mLastLocation.getLongitude()));
//            Toast.makeText(LoginActivity.this,
//                    "permission was granted, :)"+"onConnected: " + String.valueOf(mLastLocation.getLatitude()) + ":" + String.valueOf(mLastLocation.getLongitude()),
//                    Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(LoginActivity.this,
//                            "permission was granted, :)",
//                            Toast.LENGTH_LONG).show();

                    try{
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, this);
                    }catch(SecurityException e){
                        Toast.makeText(LoginActivity.this,
                                "SecurityException:\n" + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "permission denied, ...:(",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




    private class FetchData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setCancelable(true);
            if(! LoginActivity.this.isFinishing()){
                progressDialog.show();
            }


        }

        @Override
        protected String doInBackground(String... f_url) {
            goMainScreen();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            try {
                if(customerId != null){

                SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("logged", "logged");
                editor.putString("name", profile.getName());
                //editor.putString("email", email);
                //editor.putString(Constants.CUSTOMERID, customerId);
                editor.putString(Constants.CUSTOMERID, customerId);
                editor.commit();


                Intent intent = new Intent(LoginActivity.this,
                        SelectCityActivity.class);

                startActivity(intent);
                }




            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                waitingCount = 200;
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if(!isCustomerCreated){
                    Toast toast = Toast.makeText(LoginActivity.this, "Netwok issue, Please try later", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    LoginManager.getInstance().logOut();
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
