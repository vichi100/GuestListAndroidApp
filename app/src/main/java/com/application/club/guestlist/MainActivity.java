package com.application.club.guestlist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.google.firebase.messaging.FirebaseMessaging;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.application.club.guestlist.db.DBHelper;
import com.application.club.guestlist.firebasenotifications.Config;
import com.application.club.guestlist.firebasenotifications.NotificationUtils;
import com.application.club.guestlist.bookedPasses.BookingFragment;
import com.application.club.guestlist.clubsListFragment.ClubsListFragment;
import com.application.club.guestlist.menu.LocationChangeActivity;
import com.application.club.guestlist.offer.OffersFragment;
import com.application.club.guestlist.profile.ProfileScreenFragment;
import com.application.club.guestlist.utils.Constants;

//https://github.com/Ashok-Varma/BottomNavigation


public class MainActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener{

    AHBottomNavigation bottomNavigation;
    DBHelper mydb;

    private static final String TAG = MainActivity.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


//        bottomNavigation= (AHBottomNavigation) findViewById(R.id.myBottomNavigation_ID);
//
//        bottomNavigation.setOnTabSelectedListener(this);
//        this.createNavItems();



        //https://github.com/Ashok-Varma/BottomNavigation
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .setActiveColor( "#1c1d1d")
                .setInActiveColor("#FFFFFF")
                .setBarBackgroundColor("#ff0066");

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.pub, "Clubs"))
                .addItem(new BottomNavigationItem(R.drawable.offer2, "Offers"))
                .addItem(new BottomNavigationItem(R.drawable.ticket3, "Passes"))
                .addItem(new BottomNavigationItem(R.drawable.guy, "Me"))
                .initialise();
        bottomNavigationBar.setFirstSelectedPosition(0);



        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        String city = settings.getString(Constants.CITY,"");
        ClubsListFragment clubsListFragment =new ClubsListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_id, clubsListFragment).commit();
        updateToolbarText(city);


        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {

                String title="";
                if(position==0)
                {
                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                    String city = settings.getString(Constants.CITY,"");
                    ClubsListFragment clubsListFragment =new ClubsListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_id, clubsListFragment).commit();
                    title = city;
                }
                else if(position==1)
                {

                    OffersFragment offersScreenFragment =new OffersFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_id, offersScreenFragment).commit();

                    title = "Offers";
                }

                else if(position==2)
                {

                    BookingFragment bookingFragment =new BookingFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_id, bookingFragment).commit();
                    title = "Passes";
                }


                else if(position==3)
                {

                    ProfileScreenFragment profileScreenFragment =new ProfileScreenFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_id, profileScreenFragment).commit();
                    title = "Me";
                }

                updateToolbarText(title);


            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });


        final Intent notificationIntent = new Intent(this, LocationChangeActivity.class);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value.toString());
            }
        }

        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

        displayFirebaseRegId();


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received


                    String message = intent.getStringExtra("message");

                    PendingIntent contentIntent = PendingIntent.getActivity(context,
                            0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);



                    Resources res = context.getResources();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setContentIntent(contentIntent)
                            .setSmallIcon(R.drawable.ac)
                            .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ac))
                            .setTicker("vichi")
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle("Message")
                            .setContentText("vichi");
                    Notification n = builder.getNotification();

                    n.defaults |= Notification.DEFAULT_ALL;
//                    // Since android Oreo notification channel is needed.
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        String channelId = context.getString(R.string.default_notification_channel_id);
//                        NotificationChannel channel = new NotificationChannel(channelId,   "title", NotificationManager.IMPORTANCE_DEFAULT);
//                        channel.setDescription("body");
//                        nm.createNotificationChannel(channel);
//                        builder.setChannelId(channelId);
//                    }

                    nm.notify(0, n);

                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };

        //displayTuto(); // one second delay

    }

    @Override
    protected void onNewIntent(Intent intent) {

    }

    @Override
    public void onBackPressed(){

    }

    private void createNavItems()
    {
        //CREATE ITEMS
        AHBottomNavigationItem crimeItem=new AHBottomNavigationItem("Clubs",R.drawable.pub);
        AHBottomNavigationItem passItem=new AHBottomNavigationItem("Passes",R.drawable.ticket3);
        AHBottomNavigationItem offer=new AHBottomNavigationItem("Offers",R.drawable.offer2);

        //AHBottomNavigationItem trending=new AHBottomNavigationItem("Trending",R.drawable.trending);
        AHBottomNavigationItem docsItem=new AHBottomNavigationItem("Me",R.drawable.guy);


        //ADD ITEMS TO BAR
        bottomNavigation.addItem(crimeItem);
        bottomNavigation.addItem(offer);
        //bottomNavigation.addItem(trending);
        bottomNavigation.addItem(passItem);
        bottomNavigation.addItem(docsItem);

        //PROPERTIES
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#1c1d1d"));

        bottomNavigation.setCurrentItem(0);

    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        String title="";
        if(position==0)
        {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            String city = settings.getString(Constants.CITY,"");
//            ClubListxFragment searchFragment=new ClubListxFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,searchFragment).commit();
            ClubsListFragment clubsListFragment =new ClubsListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, clubsListFragment).commit();
            title = city;
        }
        else if(position==1)
        {

            OffersFragment offersScreenFragment =new OffersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, offersScreenFragment).commit();

            title = "Offers";


        }

//        else if(position==1)
//        {
//
//            OffersFragment offersScreenFragment =new OffersFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, offersScreenFragment).commit();
//
//            title = "Trending";
//
//
//        }
        else if(position==2)
        {
//            DocumentaryFragment documentaryFragment=new DocumentaryFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,documentaryFragment).commit();
//            CrimeFragment crimeFragment=new CrimeFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,crimeFragment).commit();

            //Intent intent = new Intent(this, ProfileScreen.class);
//            this.startActivity(intent);

            BookingFragment bookingFragment =new BookingFragment();


            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, bookingFragment).commit();
            title = "Passes";



        }


        else if(position==3)
        {
//            DocumentaryFragment documentaryFragment=new DocumentaryFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,documentaryFragment).commit();
//            CrimeFragment crimeFragment=new CrimeFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,crimeFragment).commit();

            //Intent intent = new Intent(this, ProfileScreen.class);
//            this.startActivity(intent);




            ProfileScreenFragment profileScreenFragment =new ProfileScreenFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, profileScreenFragment).commit();

            title = "Me";
        }

        updateToolbarText(title);
        return true;
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }



    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        //NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    protected void displayTuto() {
        TutoShowcase.from(this)
                .setListener(new TutoShowcase.Listener() {
                    @Override
                    public void onDismissed() {
                        Toast.makeText(MainActivity.this, "Tutorial dismissed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setContentView(R.layout.activity_main)
                .setFitsSystemWindows(true)
                //.on(R.id.myBottomNavigation_ID)
                //.addCircle()
                //.withBorder()
//                .onClick(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })

//                .on(R.id.swipable)
//                .displaySwipableLeft()
               // .delayed(399)
               // .animated(true)
                .show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
