package com.example.ningyuwen.music.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.ningyuwen.music.R;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";
    private static NotificationUtils mNotificationUtils;
    private NotificationChannel mChannel;    //channel
    private Notification mNotification;       //notification
    private Notification.Builder mNotifiBuilder;    //notificationBuilder
    private NotificationCompat.Builder mNotifiCompatBuilder;    //notifiCompatBuilder

    private NotificationUtils(Context context) {
        super(context);
    }

    /**
     * 获取单一实例
     * @return instance
     */
    public static NotificationUtils getInstance(Context context){
        if (mNotificationUtils == null){
            synchronized (NotificationUtils.class){
                if (mNotificationUtils == null){
                    mNotificationUtils = new NotificationUtils(context);
                }
            }
        }
        return mNotificationUtils;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        if (mChannel == null){
            mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            getManager().createNotificationChannel(mChannel);
        }
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /**
     * api为26的
     * @param views remoteView
     * @param mainPendingIntent intent
     * @return Notification.Builder
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(RemoteViews views, PendingIntent mainPendingIntent) {
        if (mNotifiBuilder == null){
            mNotifiBuilder = new Notification.Builder(getApplicationContext(), id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
        }
        mNotifiBuilder
//                .setCustomContentView(views)
                .setContent(views)
                .setContentIntent(mainPendingIntent);
        return mNotifiBuilder;
    }

    /**
     * api小于26
     * @param views remoteView
     * @param mainPendingIntent intent
     * @return NotificationCompat.Builder
     */
    public NotificationCompat.Builder getNotification_25(RemoteViews views, PendingIntent mainPendingIntent) {
        if (mNotifiCompatBuilder == null){
            mNotifiCompatBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
        }
        mNotifiCompatBuilder
//                .setCustomContentView(views)
                .setContent(views)
                .setContentIntent(mainPendingIntent);
        return mNotifiCompatBuilder;
    }

    /**
     * 清除notification
     */
    public void deleteNotification(){
        getManager().cancelAll();
    }

    /**
     * 创建Notification
     * @param views views
     * @param mainPendingIntent mainPendingIntent
     */
    public void sendNotification(RemoteViews views, PendingIntent mainPendingIntent) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            mNotification = getChannelNotification
                    (views, mainPendingIntent).build();
        } else {
            mNotification = getNotification_25(views, mainPendingIntent).build();
        }
        getManager().notify(1, mNotification);
    }
}
