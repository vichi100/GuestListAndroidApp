package com.application.club.guestlist.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by vichi on 26/11/17.
 */

public class UtillMethods {

    public static String toCamelCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

//    public static Date getTodayDate(){
//        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
//        Date date = new Date();
//        return date;
//    }

    public static Date stringToDate(String dateStr){
        DateFormat newDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        Date MyDate=null;
        try{
            MyDate = newDateFormat.parse(dateStr);

        }catch (Exception ex){

        }
        return MyDate;
    }

    public static String getDayFromDate(String date){
        String MyDay = "";

        try {
            // date/Month/year
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            Date MyDate = newDateFormat.parse(date);
            newDateFormat.applyPattern("EEEE");
            MyDay = newDateFormat.format(MyDate);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return MyDay;

    }


    public static  String getTodayDate(){

        TimeZone tz = TimeZone.getTimeZone("Asia/Kolkata");

        //TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        Calendar c = Calendar.getInstance(tz);
        Date todayDate = c.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(todayDate);
        return formattedDate;

    }

    public static  Date getTodayDateAsDate(){

        //TimeZone tz = TimeZone.getTimeZone("Asia/Kolkata");

        //TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        Date datex = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            Date date = new Date();

            dateFormat.format(date);

            datex = dateFormat.parse(dateFormat.format(date));

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return datex;

    }

    public static String getRemaingTime(String startDateTimeStr, String endDateTimeStr){
        String remainingTime = "";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");
        try{
            Date endDateTime = simpleDateFormat.parse(endDateTimeStr);

            Date startDateTime = simpleDateFormat.parse(startDateTimeStr);


//            String currentdateTimeStr = simpleDateFormat.format(new Date());
//            Date currentdateTime = simpleDateFormat.parse(currentdateTimeStr);
            //milliseconds
            long different = endDateTime.getTime() - startDateTime.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;


            if(elapsedDays != 0){
                remainingTime = elapsedDays+"D:"+elapsedHours+"H:"+elapsedMinutes+"M";
            }else{
                if(elapsedHours != 0){
                    remainingTime = elapsedHours+"H:"+elapsedMinutes+"M";
                }else{
                    if(elapsedMinutes != 0){
                        remainingTime = elapsedMinutes+"M";
                    }
                }
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }

        return remainingTime;
    }


    /*
*To get a Bitmap image from the URL received
* */
    public static Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    public static String splitString(String strTmp){
        int n =3;
        StringBuilder str = new StringBuilder(strTmp);
        int idx = str.length() - n;
        while (idx > 0){
            str.insert(idx, " ");
            idx = idx - n;
        }
        //System.out.println(str.toString());
        return str.toString();
    }


}
