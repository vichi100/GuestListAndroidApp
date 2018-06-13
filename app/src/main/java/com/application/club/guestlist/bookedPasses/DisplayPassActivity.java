package com.application.club.guestlist.bookedPasses;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.application.club.guestlist.utils.UtillMethods;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.application.club.guestlist.R;
import com.application.club.guestlist.db.DBHelper;
import com.application.club.guestlist.utils.Constants;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

public class DisplayPassActivity extends AppCompatActivity {
    private final String tag = "QRCGEN";
    private final int REQUEST_PERMISSION = 0xf0;

    private DisplayPassActivity self;
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
        setContentView(R.layout.display_pass_activity);
        getSupportActionBar().setTitle("Ticket Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        self = this;

        Intent intent = getIntent();
        final String eventDate  = intent.getStringExtra(Constants.EVENTDATE);
        final String clubName = intent.getStringExtra(Constants.CLUB_NAME);
        final String qrNumber = intent.getStringExtra(Constants.QRNUMBER);
        String qrNumberStr = UtillMethods.splitString(qrNumber);
        final String ticketDetails = intent.getStringExtra(Constants.TICKET_DETAILS);
        final String ticketType = intent.getStringExtra(Constants.TICKET_TYPE);

        final String costAfterDiscount = intent.getStringExtra(Constants.COSTAFTERDISCOUNT);
        final String remainingAmt = intent.getStringExtra(Constants.REMAINING_AMOUNT);

        TextView qrNumberTv = (TextView) findViewById(R.id.qrNumber);
        qrNumberTv.setText(qrNumberStr);

        //txtQRText   = (EditText)findViewById(R.id.txtQR);
        clubNametv = (TextView) findViewById(R.id.clubName);
        clubNametv.setText(clubName);

        TextView eventDatetv = (TextView) findViewById(R.id.eventDate);
        eventDatetv.setText(eventDate);

        TextView ticketDetailstv = (TextView) findViewById(R.id.details);
        ticketDetailstv.setText(ticketDetails);
        //btnGenerate = (Button)findViewById(R.id.btnGenerate);
        //btnReset    = (Button)findViewById(R.id.btnReset);
        imgResult   = (ImageView)findViewById(R.id.imgResult);
        //loader      = (ProgressBar)findViewById(R.id.loader);

        if(ticketType.equalsIgnoreCase("guest list")){
            TextView notetv = (TextView) findViewById(R.id.note);
            notetv.setText("Entry after 11PM club charges will apply");
        }else if(ticketType.equalsIgnoreCase("table") ){
            ticketDetailstv.setText(ticketDetails+" With FULL COVER of "+costAfterDiscount+"Rs");
        }else if(ticketType.equalsIgnoreCase("pass") ){
            TextView remainingAmttv = (TextView) findViewById(R.id.remainingamount);
            remainingAmttv.setText("As FULL COVER of "+costAfterDiscount+"Rs is All Paid");

        }

        if(!remainingAmt.equalsIgnoreCase("0")){
            TextView remainingAmttv = (TextView) findViewById(R.id.remainingamount);
            remainingAmttv.setText(remainingAmt+" Rs Need To Pay At Club");
        }





        self.generateImage(qrNumber);

        // Locate the button in activity_main.xml
        button = (Button) findViewById(R.id.done);

        // Capture button clicks
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
//                Intent myIntent = new Intent(QRCodeActivity.this,
//                        MainActivity.class);
//                startActivity(myIntent);

                finish();

            }
        });


//        btnGenerate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                self.generateImage();
//            }
//        });
//
//        btnReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                self.reset();
//            }
//        });
//
//        imgResult.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                self.confirm("Simpan Gambar ?", "Iya", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        saveImage();
//                    }
//                });
//            }
//        });
//
//        txtQRText.setText("hello");
    }

    @Override
    public boolean onSupportNavigateUp(){
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return null;
        }


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
            alert("Failed to save image");
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


    private void generateImage(String qrNumber){
//        long time= System.currentTimeMillis();

        final String text = qrNumber;//Long.toString(time);
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
                    BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size,
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
                            //myDb.insertBooking( fileName,  "29/OCT/2017",  clubName.getText().toString());
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
