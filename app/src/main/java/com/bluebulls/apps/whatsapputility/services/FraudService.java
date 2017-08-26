package com.bluebulls.apps.whatsapputility.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.bluebulls.apps.whatsapputility.R;

/**
 * Created by ogil on 23/08/17.
 */

public class FraudService extends Service {
    private static final String TAG = "FraudService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleStart(){
        Notification notification;
        NotificationCompat.Builder bBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setSmallIcon(R.drawable.icon)
                .setContentTitle("Timer")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText("Timer is running...").setOngoing(true);
        notification = bBuilder.build();
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(54312, notification);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
                stopSelf();
            }
        }, 100);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        } else {
            return Service.START_NOT_STICKY;
        }
    }
}
