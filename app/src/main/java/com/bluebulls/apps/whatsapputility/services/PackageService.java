package com.bluebulls.apps.whatsapputility.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluebulls.apps.whatsapputility.util.SSBridge;
import com.rvalerio.fgchecker.AppChecker;

import static com.bluebulls.apps.whatsapputility.util.CustomBridge.STOP_SELF;
import static com.bluebulls.apps.whatsapputility.util.SSBridge.STOP_SS_SERV;

/**
 * Created by ogil on 30/07/17.
 */

public class PackageService extends Service {
    private static final String TAG = "PackageService";
    private AppChecker appChecker = new AppChecker();
    @Override
    public void onCreate() {
        super.onCreate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, (10000)-500);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isTargetActive = false, isChatHeadServiceStopped = false;

    private void handleStart(){
        resetItself();
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

    private void startChatHead(){
        startService(new Intent(getApplicationContext(), ChatHeadService.class));
    }

    private void resetItself(){
        Intent i = new Intent(getApplicationContext(), PackageService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 7160, i, 0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pi);
    }

    private void stopChatHead(){
        Intent i = new Intent(STOP_SELF);
        sendBroadcast(i);

        /* stopping any floating ss */

        Intent x = new Intent(STOP_SS_SERV);
        sendBroadcast(x);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent i = new Intent(getApplicationContext(), PackageService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 7160, i, 0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pi);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
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
