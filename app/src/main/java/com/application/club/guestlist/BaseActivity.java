package com.application.club.guestlist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.application.club.guestlist.mFragments.CrimeFragment;
import com.application.club.guestlist.bookedPasses.BookingFragment;
import com.application.club.guestlist.clubsListFragment.ClubsListFragment;


public abstract class BaseActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener{

    AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        bottomNavigation= (AHBottomNavigation) findViewById(R.id.myBottomNavigation_ID);
//
//        bottomNavigation.setOnTabSelectedListener(this);
//        this.createNavItems();

    }

    private void createNavItems()
    {
        //CREATE ITEMS
        AHBottomNavigationItem crimeItem=new AHBottomNavigationItem("Club",R.drawable.ic_home_black);
        AHBottomNavigationItem dramaItem=new AHBottomNavigationItem("Event",R.drawable.ac);
        AHBottomNavigationItem docsItem=new AHBottomNavigationItem("Me",R.drawable.dr);

        //ADD ITEMS TO BAR
        bottomNavigation.addItem(crimeItem);
        bottomNavigation.addItem(dramaItem);
        bottomNavigation.addItem(docsItem);

        //PROPERTIES
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setCurrentItem(0);

    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        String title="";
        if(position==0)
        {
//            ClubListxFragment searchFragment=new ClubListxFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,searchFragment).commit();
            ClubsListFragment clubsListFragment =new ClubsListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, clubsListFragment).commit();
            title = "Club";
        }else if(position==1)
        {
            BookingFragment bookingFragment =new BookingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id, bookingFragment).commit();
            title = "Event";
        }else if(position==2)
        {
//            DocumentaryFragment documentaryFragment=new DocumentaryFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,documentaryFragment).commit();
            CrimeFragment crimeFragment=new CrimeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_id,crimeFragment).commit();
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
}
