package com.application.club.guestlist.menu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.application.club.guestlist.R;

/**
 * keep it simple for now later covert it to grid view
 */

public class LocationChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_change_activity);
        getSupportActionBar().setTitle("City");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        self = this;
//
//        //txtQRText   = (EditText)findViewById(R.id.txtQR);
//        clubName = (TextView) findViewById(R.id.clubName);
//        //btnGenerate = (Button)findViewById(R.id.btnGenerate);
//        //btnReset    = (Button)findViewById(R.id.btnReset);
//        imgResult = (ImageView) findViewById(R.id.imgResult);
//        //loader      = (ProgressBar)findViewById(R.id.loader);
//
//        self.generateImage();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
