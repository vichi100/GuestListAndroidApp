package com.application.club.guestlist.login;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.application.club.guestlist.R;

/**
 * Created by vichi on 14/03/18.
 */

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);
        getSupportActionBar().setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv = (TextView)findViewById(R.id.aboutus);

        Spannable wordtoSpan = new SpannableString("We are here to make your party exprience more ROCKING");
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 15, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.GREEN), 35, 45, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 46, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(wordtoSpan);
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }
}
