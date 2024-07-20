package com.andromob.andronews.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.andromob.andronews.R;
import com.andromob.andronews.activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FcmReceiver extends FirebaseMessagingService {
    private String bigpicture = null;
    public static final String CHANNEL_ID = "notify";
    String title, message;
    public static String external_link = "false";

    @Override
    public void onNewToken(@NonNull String s) {
        initFCM(this);
    }

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);

            try {
                title = object.getString("title");
                message = object.getString("message");
                external_link = object.getString("external_link");
                bigpicture = object.getString("big_picture");
            } catch (Exception e) {
                Log.e("Notify Error", "Exception: " + e.getMessage());
            }
            sendNotification(title, message, external_link);
        }
    }

    @SuppressLint("WrongConstant")
    private void sendNotification(String title, String msg, String url) {
        FcmReceiver.createNotificationChannel(this);
        Intent intent;
        if (!url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent contentIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setColor(getColour())
                .setAutoCancel(true)
                .setLights(Color.RED, 800, 800)
                .setContentText(msg)
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
        try {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher));
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "errror large- " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(message);

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)));
        } else {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(createRandomCode(7), mBuilder.build());
    }

    @SuppressLint("ObsoleteSdkInt")
    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.drawable.ic_notifications;
        } else {
            return R.drawable.ic_notifications;
        }
    }

    private int getColour() {
        return getResources().getColor(R.color.colorPrimaryDark);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            InputStream input;
            URL url = new URL(src);

            URLConnection conn = new URL(src).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    public int createRandomCode(int codeLength) {
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return Integer.parseInt(sb.toString());
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "All Notifications";
            String description = "App Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(FcmReceiver.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static void initFCM(Context context) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                FirebaseMessaging.getInstance().subscribeToTopic("alluser")
                        .addOnCompleteListener(task -> {
                            Prefs.getInstance(context).setSubscribed(true);
                            String msg = "Subscribed Successfully";
                            if (!task.isSuccessful()) {
                                msg = "Failed To Subscribe";
                            }
                            Log.e("fcm", msg);
                            // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("alluser")
                    .addOnCompleteListener(task -> {
                        Prefs.getInstance(context).setSubscribed(true);
                        String msg = "Subscribed Successfully";
                        if (!task.isSuccessful()) {
                            msg = "Failed To Subscribe";
                        }
                        Log.e("fcm", msg);
                        // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    });
        }
    }

}