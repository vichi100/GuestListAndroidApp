package com.application.club.guestlist.qrcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.application.club.guestlist.MainActivity;
import com.application.club.guestlist.R;
import com.application.club.guestlist.db.DBHelper;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

import static com.application.club.guestlist.utils.Constants.BOOKING_TYPE;
import static com.application.club.guestlist.utils.Constants.CLUB_ID;
import static com.application.club.guestlist.utils.Constants.CLUB_NAME;
import static com.application.club.guestlist.utils.Constants.DETAILS;
import static com.application.club.guestlist.utils.Constants.EVENTDATE;
import static com.application.club.guestlist.utils.Constants.QRNUMBER;
import static com.application.club.guestlist.utils.Constants.REMAINING_AMOUNT;

public class QRCodeActivity extends AppCompatActivity {
    private final String tag = "QRCGEN";
    private final int REQUEST_PERMISSION = 0xf0;

    private QRCodeActivity self;
    private Snackbar snackbar;
    private Bitmap qrImage;

    private EditText txtQRText;
    private TextView clubNametv;
    private Button btnGenerate, btnReset;
    private ImageView imgResult;
    private ProgressBar loader;
    private DBHelper myDb;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_activity);
        getSupportActionBar().setTitle("Booking Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        self = this;

        Intent intent = getIntent();
        final String clubName  = intent.getStringExtra(CLUB_NAME);
        final String clubId  = intent.getStringExtra(CLUB_ID);
        final String eventdate  = intent.getStringExtra(EVENTDATE);
        final String bookingType  = intent.getStringExtra(BOOKING_TYPE);
        final String qrNumber  = intent.getStringExtra(QRNUMBER);
        String qrNumberStr = UtillMethods.splitString(qrNumber);
        final String remainingAmt  = intent.getStringExtra(REMAINING_AMOUNT);
        final String allDetails = intent.getStringExtra(DETAILS);

        String quantityViewDefaultstag = null;
        String quantityViewDefaultCouple = null;
        String quantityViewDefaultGirl = null;




        clubNametv = (TextView) findViewById(R.id.clubName);
        clubNametv.setText(UtillMethods.toCamelCase(clubName));

        TextView datetv = (TextView) findViewById(R.id.date);
        datetv.setText(eventdate);

        TextView qrNumberTv = (TextView) findViewById(R.id.qrNumber);
        qrNumberTv.setText(qrNumberStr);

        TextView entrytv = (TextView) findViewById(R.id.entry);
        TextView remainingAmtTv = (TextView) findViewById(R.id.remainingamount);

        TextView notetv = (TextView) findViewById(R.id.note);

        //check from activity this class is called
        if(bookingType.equalsIgnoreCase("pass")){

            final String totalCost  = intent.getStringExtra(Constants.TOTAL_COST);

            String entry = "";
            quantityViewDefaultCouple = intent.getStringExtra("quantityViewDefaultCouple");
            quantityViewDefaultGirl = intent.getStringExtra("quantityViewDefaultGirl");
            quantityViewDefaultstag = intent.getStringExtra("quantityViewDefaultstag");
            if(!quantityViewDefaultCouple.equalsIgnoreCase("0")){
                entry = quantityViewDefaultCouple+" couple ";
            }

            if(!quantityViewDefaultGirl.equalsIgnoreCase("0")){
                if(entry.equalsIgnoreCase(""))
                    entry = entry+ quantityViewDefaultGirl+" girl ";
                else
                    entry = entry+"and "+ quantityViewDefaultGirl+" girl ";
            }

            if(!quantityViewDefaultstag.equalsIgnoreCase("0")){
                if(entry.equalsIgnoreCase(""))
                    entry = entry + quantityViewDefaultstag+" stag ";
                else
                    entry = entry +"and "+ quantityViewDefaultstag+" stag ";
            }

            if(!entry.equalsIgnoreCase("")){
                entry = entry+ " is allowed";
            }


            entrytv.setText(entry);


            notetv.setText("As FULL COVER Rs"+totalCost+" is all paid");
        }

        if(bookingType.equalsIgnoreCase("guest list")){
            String selectedGuestType = intent.getStringExtra(Constants.SELECTED_GUEST_TYPE);
            if(selectedGuestType.equalsIgnoreCase("couple")){
                entrytv.setText("One Couple is Allowed");
            }else if(selectedGuestType.equalsIgnoreCase("girls")){
                entrytv.setText("Max Two Girls Allowed");
            }

            notetv.setText("Entry after 10PM club charges will apply");
        }

        if(bookingType.equalsIgnoreCase("Table")){


            final String cost = intent.getStringExtra(Constants.COST);
            final String size = intent.getStringExtra(Constants.TABLE_SIZE);
            final String details = intent.getStringExtra(Constants.DETAILS);
            String entry = "Table For "+size+ " with full cover of Rs"+cost;
            entrytv.setText(allDetails);

            remainingAmtTv.setText("Rs "+remainingAmt+" Need to Pay At Club");

        }




        imgResult   = (ImageView)findViewById(R.id.imgResult);

        self.generateImage(qrNumber,eventdate, clubName);


        // Locate the button in activity_main.xml
        button = (Button) findViewById(R.id.done);

        // Capture button clicks
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(QRCodeActivity.this,
                        MainActivity.class);
                startActivity(myIntent);

                finish();

            }
        });



    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
        return true;
    }

//    @Override
//    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                saveImage();
//            } else {
//                alert("The app does not have access to add images.");
//            }
//        }
//    }

    private String saveImage() {
        if (qrImage == null) {
            alert("No pictures yet.");
            return null;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_PERMISSION);
//            return null;
//        }


        String fname = "qrcode-" + Calendar.getInstance().getTimeInMillis();
        boolean success = true;
        try {
            String result = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    qrImage,
                    fname,
                    "QRCode Image"
            );
            if (result == null) {
                success = false;
            } else {
                Log.e(tag, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        if (!success) {
            //alert("Failed to save image");
        } else {
            //self.snackbar("Gambar tersimpan ke gallery.");
        }

        return fname;
    }

    private void alert(String message){
        AlertDialog dlg = new AlertDialog.Builder(self)
                .setTitle("QRCode Generator")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    private void confirm(String msg, String yesText, final AlertDialog.OnClickListener yesListener) {
        AlertDialog dlg = new AlertDialog.Builder(self)
                .setTitle("Konfirmasi")
                .setMessage(msg)
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(yesText, yesListener)
                .create();
        dlg.show();
    }

//    private void snackbar(String msg) {
//        if (self.snackbar != null) {
//            self.snackbar.dismiss();
//        }
//
//        self.snackbar = Snackbar.make(
//                findViewById(R.id.mainBody),
//                msg, Snackbar.LENGTH_SHORT);
//
//        self.snackbar.show();
//    }

    private void endEditing(){
        txtQRText.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void generateImage(final String qrNumber, final String eventdate, final String clubName){
        long time= System.currentTimeMillis();

        final String text = Long.toString(time);
//        final String text = txtQRText.getText().toString();
//        if(text.trim().isEmpty()){
//            alert("Ketik dulu data yang ingin dibuat QR Code");
//            return;
//        }

        //endEditing();
        //showLoadingVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = imgResult.getMeasuredWidth();
                if( size > 1){
                    Log.e(tag, "size is set manually");
                    size = 260;
                }

                Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
                hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                hintMap.put(EncodeHintType.MARGIN, 1);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try {
                    String QRImageCode = "Code: "+qrNumber+" | Club: "+clubName+" | Event Date: "+eventdate;
                    BitMatrix byteMatrix = qrCodeWriter.encode(QRImageCode, BarcodeFormat.QR_CODE, size,
                            size, hintMap);
                    int height = byteMatrix.getHeight();
                    int width = byteMatrix.getWidth();
                    self.qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++){
                        for (int y = 0; y < height; y++){
                            qrImage.setPixel(x, y, byteMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                        }
                    }


                    self.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            self.showImage(self.qrImage);
                            //String fileName = saveImage();
                            //myDb.insertBooking( fileName,  "29/OCT/2017",  clubNametv.getText().toString());
                            //self.showLoadingVisible(false);
                            //self.snackbar("QRCode telah dibuat");
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                    alert(e.getMessage());
                }
            }
        }).start();
    }

//    private void showLoadingVisible(boolean visible){
//        if(visible){
//            showImage(null);
//        }
//
//        loader.setVisibility(
//                (visible) ? View.VISIBLE : View.GONE
//        );
//    }

    private void reset(){
        txtQRText.setText("");
        showImage(null);
        endEditing();
    }

    private void showImage(Bitmap bitmap) {
        if (bitmap == null) {
            imgResult.setImageResource(android.R.color.transparent);
            qrImage = null;
            clubNametv.setVisibility(View.GONE);
        } else {
            imgResult.setImageBitmap(bitmap);
            clubNametv.setVisibility(View.VISIBLE);
        }
    }
}
