package pt.ua.tomasr.imhere.fcm;

/**
 * Created by reytm on 18/10/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import pt.ua.tomasr.imhere.MainActivity;
import pt.ua.tomasr.imhere.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        String image = remoteMessage.getNotification().getIcon();
//        String title = remoteMessage.getNotification().getTitle();
//        String text = remoteMessage.getNotification().getBody();
//        String sound = remoteMessage.getNotification().getSound();

        int id = 0;
        Object obj = remoteMessage.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
        }

        //NEW...
        Log.d(TAG, "FROM:" + remoteMessage.getFrom());

        //Check if the message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());
            Map data = remoteMessage.getData();

            Log.d("",data.get("op_id").toString());
            switch (data.get("op_id").toString()){
                case "0":
                    Intent i = new Intent();
                    i.putExtra("action",data.get("op_id").toString());
                    break;
                case "1":
                    Intent i1 = new Intent();
                    i1.putExtra("action",data.get("op_id").toString());
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
            }
        }

        //Check if the message contains notification
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Mesage body:" + remoteMessage.getNotification().getBody());
        }

        //this.sendNotification(new NotificationData(image, id, title, text, sound)); este é o verdadeiro
        this.sendNotification(new NotificationData());
    }


    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param notificationData GCM message received.
     */
    private void sendNotification(NotificationData notificationData) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NotificationData.TEXT, notificationData.getTextMessage());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = null;
//        try {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Firebase Cloud Messaging")//o de baixo
                    //.setContentTitle(URLDecoder.decode(notificationData.getTitle(), "UTF-8"))
//                    .setContentText(URLDecoder.decode(notificationData.getTextMessage(), "UTF-8"))
                    .setContentText("cenas")
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "Não foi possível criar objeto notificationBuilder");
        }
    }
}