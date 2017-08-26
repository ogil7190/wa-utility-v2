package com.bluebulls.apps.whatsapputility.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.HomeActivity;
import com.rvalerio.fgchecker.AppChecker;

import java.util.Arrays;

import static android.support.v4.app.NotificationCompat.EXTRA_TEXT_LINES;
import static com.bluebulls.apps.whatsapputility.util.CustomBridge.STOP_SELF;
import static com.bluebulls.apps.whatsapputility.util.SSBridge.STOP_SS_SERV;

/**
 * Created by ogil on 29/07/17.
 */

public class CustomNotificationListener extends NotificationListenerService {

    private AppChecker appChecker = new AppChecker();
    public static String TAG = "Listener";
    private boolean isTargetActive = false, isChatHeadServiceStopped = false;

    @Override
    public void onCreate() {
        super.onCreate();
        resetItself();
        handleStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, (60*1000)-500);
    }

    private void handleStart(){
        appChecker.when("com.whatsapp", new AppChecker.Listener() {
            @Override
            public void onForeground(String process) {
                if(!isTargetActive){
                    isTargetActive = true;
                    isChatHeadServiceStopped = false;
                    if(!ChatHeadService.isRunning) {
                        startChatHead();
                    }
                }
            }

        }).other(new AppChecker.Listener() {
            @Override
            public void onForeground(String process) {
                isTargetActive = false;
                if(!isChatHeadServiceStopped) {
                    isChatHeadServiceStopped = true;
                    stopChatHead();
                }
            }
        }).timeout(500).start(getApplicationContext());
    }

    @Override
    public void onListenerConnected() {
        /* read all active notifications */
        super.onListenerConnected();
    }

    private void resetItself(){
        Intent i = new Intent(getApplicationContext(), CustomNotificationListener.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 7160, i, 0);
        AlarmManager alarm = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60*1000, pi);
    }

    private void startChatHead(){
        startService(new Intent(getApplicationContext(), ChatHeadService.class));
    }

    private void stopChatHead() {
        Intent i = new Intent(STOP_SELF);
        sendBroadcast(i);
        /* stopping any floating ss */
        Intent x = new Intent(STOP_SS_SERV);
        sendBroadcast(x);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack  = sbn.getPackageName();
        //CharSequence ticker = sbn.getNotification().tickerText;
        //Log.e(TAG,"ticker:"+ticker);
            if(pack.equals("com.whatsapp"))
            for (String key : sbn.getNotification().extras.keySet()) {
                if (sbn.getNotification().extras.get(key)!=null) {
                    //Log.i(TAG + ":"+ key, sbn.getNotification().extras.get(key).toString());
                    if(key.equals(EXTRA_TEXT_LINES)){
                        CharSequence[] p = sbn.getNotification().extras.getCharSequenceArray(EXTRA_TEXT_LINES);
                        String[] res = new String[p.length];
                        int i = 0;
                        for(CharSequence ch: p){
                            res[i++] = ch.toString();
                        }
                        Log.d(TAG,"New Notification:"+res);
                        for(String s : res){
                         if(s.contains("https://wa.bluebulls/poll_id/")){
                             String poll_id = s.replace("https://wa.bluebulls/poll_id/","");
                             Log.d(TAG,"Poll Received:"+poll_id);
                             showNotification("Got a new poll!","Check out the new poll from WhatsApp",poll_id);
                             noti_id++;
                         }
                        }
                    }
                }
            }
    }
    private int noti_id = 7190;

    private void showNotification(String title, String text, String poll_id){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_create_grey600_24dp)
                        .setContentTitle(title)
                        .setContentText(text);
        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.setAction(Intent.ACTION_SEND);
        resultIntent.setType("text/ogil");
        resultIntent.putExtra(Intent.EXTRA_TEXT,poll_id);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        7190,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(noti_id, mBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("App", "Removed");
        Intent i = new Intent(getApplicationContext(), CustomNotificationListener.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 7180, i, 0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.d("App", "Destroyed");
        Intent i = new Intent(getApplicationContext(), CustomNotificationListener.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 7180, i, 0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 6000, pi);
        super.onDestroy();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

}