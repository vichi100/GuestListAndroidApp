package com.application.club.guestlist.firebasenotifications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by AndroidBash on 20-Aug-16.
 */
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
import android.media.RingtoneManager;
        import android.net.Uri;
        import android.support.v4.app.NotificationCompat;
        import android.util.Log;

        import com.google.firebase.messaging.FirebaseMessagingService;
        import com.google.firebase.messaging.RemoteMessage;
import com.application.club.guestlist.R;
import com.application.club.guestlist.offer.OfferDisplayActivity;
import com.application.club.guestlist.utils.Constants;

import org.json.JSONObject;

import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;
    String clubId;
    String clubName;
    String eventDate;
    String imageUri;
    String tablediscount;
    String passdiscount;
    String djname;
    String music;
    String eventName;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.e("JSON_OBJECT", object.toString());
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message
        String message = remoteMessage.getData().get("title");
        //imageUri will contain URL of the image to be displayed with Notification
        imageUri = remoteMessage.getData().get("imageURL");
        String imageUrix = Constants.HTTP_URL+imageUri;
        clubId = remoteMessage.getData().get(Constants.CLUB_ID);
        clubName = remoteMessage.getData().get(Constants.CLUB_NAME);
        djname = remoteMessage.getData().get(Constants.DJNAME);
        music = remoteMessage.getData().get(Constants.MUSIC);
        eventDate = remoteMessage.getData().get(Constants.EVENT_DATE);
        passdiscount = remoteMessage.getData().get(Constants.PASS_DISCOUNT);
        tablediscount = remoteMessage.getData().get(Constants.TABLE_DISCOUNT);
        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
        String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");

        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUrix);

        sendNotification(message, bitmap, TrueOrFlase);

    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void sendNotification(String messageBody, Bitmap image, String TrueOrFalse) {
        Intent intent = new Intent(this, OfferDisplayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", TrueOrFalse);
        intent.putExtra(Constants.CLUB_ID, clubId);
        intent.putExtra(Constants.CLUB_NAME, clubName);

        intent.putExtra(Constants.DJ_NAME, djname);
        intent.putExtra(Constants.MUSIC, music);

        intent.putExtra(Constants.EVENT_DATE, eventDate);
        intent.putExtra(Constants.IMAGE_URL, imageUri);
        intent.putExtra(Constants.TABLE_DISCOUNT, tablediscount);
        intent.putExtra(Constants.PASS_DISCOUNT, passdiscount);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(messageBody)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
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
}