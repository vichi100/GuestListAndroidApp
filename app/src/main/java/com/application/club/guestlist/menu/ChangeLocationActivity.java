package com.application.club.guestlist.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.club.guestlist.MainActivity;
import com.application.club.guestlist.R;
import com.application.club.guestlist.login.CustomGridViewActivity;
import com.application.club.guestlist.login.SelectCityActivity;
import com.application.club.guestlist.utils.Constants;


/**
 * Created by vichi on 20/03/18.
 */

public class ChangeLocationActivity extends AppCompatActivity {

    GridView androidGridView;

    private int previousSelectedPosition = 0;
    int firstTime = 0;

    String[] gridViewString = {
            "MUMBAI", "PUNE", "DELHI", "BANGLOORE", "HYDRABAD", "CHENNAI",

    } ;
    int[] gridViewImageId = {
            R.drawable.mumbai, R.drawable.pune1, R.drawable.delhi,
            R.drawable.bangloore, R.drawable.hydrabad, R.drawable.chennai,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_location_activity);
        getSupportActionBar().setTitle("City");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) findViewById(R.id.startParty);
        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(ChangeLocationActivity.this, gridViewString, gridViewImageId);
        androidGridView=(GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.CITY, "Mumbai");
        editor.commit();

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {



                LinearLayout iv = (LinearLayout) view;
                ImageView imagex = (ImageView)iv.findViewById(R.id.android_gridview_image);
                // Set the current selected item background color
                //iv.setBackgroundColor(Color.parseColor("#FF9AD082"));

                if(position == 0) {
                    imagex.setBackgroundColor(Color.parseColor("#FF9AD082"));
                    imagex.setPadding(16, 16, 16, 16);
                }
                // Get the last selected View from GridView
                LinearLayout previousSelectedView = (LinearLayout) androidGridView.getChildAt(previousSelectedPosition);
                ImageView pimagex = (ImageView)previousSelectedView.findViewById(R.id.android_gridview_image);
                //pimagex.setPadding(16, 16, 16, 16);
                // If there is a previous selected view exists
                if (previousSelectedPosition != -1 && firstTime !=0)
                {
                    // Set the last selected View to deselect
                    pimagex.setSelected(false);

                    // Set the last selected View background color as deselected item
                    pimagex.setBackgroundColor(Color.TRANSPARENT);

                    // Set the last selected View text color as deselected item
                    //previousSelectedView.setTextColor(Color.DKGRAY);
                }
                firstTime = 1;
                // Set the current selected view position as previousSelectedPosition
                previousSelectedPosition = position;

                if(position == 0){
                    editor.putString(Constants.CITY, "Mumbai");
                }

//                else if(position == 1){
//                    editor.putString(Constants.CITY, "Pune");
//                }

                else{
                    Toast.makeText(getBaseContext(), "Comming soon in your city", Toast.LENGTH_SHORT).show();
                }

                editor.commit();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(ChangeLocationActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }

        });

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
