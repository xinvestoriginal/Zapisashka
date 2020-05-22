package us.xinvestoriginal.callrec.Helpers;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import us.xinvestoriginal.callrec.Activities.MainActivity;
import us.xinvestoriginal.callrec.R;

/**
 * Created by x-invest on 08.10.2014.
 */
public class NotifiController {


    private static final int NOTIFI_ID    = 220583;
    private static ArrayList<Integer> ids = new ArrayList<>();

    public static void Start(Context context,String message){
        Start(context,context.getString(R.string.app_name),new ArrayList<>(Arrays.asList(new String[]{message})), false);
    }

    public static void Start(Context context,String title,String message, boolean useSound){
        Start(context,title,new ArrayList<>(Arrays.asList(new String[]{message})), useSound);
    }

    public static void Start(Context context,String title,List<String> messages, boolean useSound){

        final int sdkVersion = Build.VERSION.SDK_INT;
        final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //final Uri sound = ResToUri(context,R.raw.osw2);
        final String titleValue = title;
        for (String text : messages){
            final String textValue = text;
            if (sdkVersion < 11)
                OldSdkNotification(context, titleValue, textValue, useSound ? sound : null);
            else
                NewSdkNotification(context, titleValue, textValue, useSound ? sound : null);
        }
    }

    public static void ClearNotification(Context ctx) {
        NotificationManager nMgr = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nMgr != null){
            for (Integer id : ids){
                nMgr.cancel(id);
            }
        }
        ids = new ArrayList<>();
    }

    public static String channelId(Context context){
        return context.getString(R.string.app_name);
    }


    private static void OldSdkNotification(Context context, String title, String text, Uri sound){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icongray)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(intent); //Required on Gingerbread and below

        if (sound != null) mBuilder.setSound(sound);
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GetID(), mBuilder.build());
    }

    @TargetApi(11)
    private static void NewSdkNotification(Context context,String title,String text, Uri sound){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int notificationID = GetID();
        PendingIntent intent =
                PendingIntent.getActivity(context, notificationID, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(context);

        notificationBuilder.setWhen(0);
        notificationBuilder.setContentIntent(intent);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setTicker(title);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (sound != null) notificationBuilder.setSound(sound);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(text));
        }
        notificationBuilder.setContentText(text);

        if (Build.VERSION.SDK_INT >= 23){
            notificationBuilder.setColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setSmallIcon(R.drawable.icon_small);
            notificationBuilder.setLargeIcon(
                    BitmapFactory.decodeResource(context.getResources(),R.drawable.icongray));
        }else{
            notificationBuilder.setSmallIcon(R.drawable.icongray);
        }

        Notification notification;
        if (Build.VERSION.SDK_INT >= 16){
            notification = notificationBuilder.build();
        }else{
            notification = notificationBuilder.getNotification();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(NOTIFI_ID), "X-REC_CHANNEL", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            // Creating an Audio Attribute

            if (sound != null){
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                notificationChannel.setSound(sound,audioAttributes);
            }

            assert notificationManager != null;
            notificationBuilder.setChannelId(String.valueOf(NOTIFI_ID));
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static Notification createForegroundNotification(Context context){
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int notificationID = NOTIFI_ID;
        PendingIntent intent =
                PendingIntent.getActivity(context, notificationID, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        String channelStr = NotifiController.channelId(context) + " work channel";

        NotificationChannel channel = new NotificationChannel(channelStr,context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null,null);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(context, channelStr)
                .setContentIntent(intent)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.icon_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.icongray))
                .setContentText(context.getString(R.string.app_is_working)).build();
        return notification;
    }





    private static int GetID(){
        int res = NOTIFI_ID + ids.size();
        ids.add(res);
        return res;
    }

    private static Uri ResToUri(Context context, int resId) {
        final String ANDROID_RESOURCE = "android.resource://";
        final String FORESLASH = "/";
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FORESLASH + resId);
    }
}
