package com.bluebulls.apps.whatsapputility.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.MainActivity;
import com.bluebulls.apps.whatsapputility.services.CustomNotificationListener;
import com.bluebulls.apps.whatsapputility.services.PackageService;

/**
 * Created by ogil on 30/07/17.
 */

public class ResetManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("App","Reboot Fired! & "+intent.getAction());
        Toast.makeText(context, "Restarted", Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Restarted", Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Restarted", Toast.LENGTH_LONG).show();
        MainActivity.haveAutoStartPerm = true;
        startBackgroundProcesses(context);
        if(!OverlayUtil.canDrawOverlays(context)){
            showNotification(context,"Phone Rebooted!","Take this action urgently!");
        }
    }

    private void startBackgroundProcesses(Context context){
        context.startService(new Intent(context, CustomNotificationListener.class));
    }

    private void showNotification(Context context, String title, String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setOngoing(true)
                        .setContentText(text);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent restartPendingIntent = PendingIntent.getActivity(context, 7189, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(restartPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(7171, mBuilder.build());
    }
}
