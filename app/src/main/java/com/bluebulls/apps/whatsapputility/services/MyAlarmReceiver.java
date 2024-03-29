package com.bluebulls.apps.whatsapputility.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.HomeActivity;

public class MyAlarmReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().contains("NEW_EVENT_ALARM")) {
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(new long[]{ 1000,500,500,1000 }, 3);
            String event_id = intent.getAction().substring(intent.getAction().indexOf("#")+1, intent.getAction().length());
            showNotificationEvent("Event has Arrived!","Tap to view event",event_id);
        }

        if(intent.getAction().contains("NEW_REM_ALARM")) {
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(new long[]{ 1000,500,500,1000 }, 3);
            String rem_id = intent.getAction().substring(intent.getAction().indexOf("#")+1, intent.getAction().length());
            int remID = Integer.valueOf(rem_id);
            showNotificationRem(remID, "Here is your reminder","Tap to view reminder");
        }
    }

    private void showNotificationRem(int remId, String title, String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_alarm_add_black_48dp)
                        .setContentTitle(title)
                        .setContentText(text);

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.setAction(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse("rem_id/"+remId));
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        7180,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void showNotificationEvent(String title, String text, String event_id){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_alarm_on_black_18dp)
                        .setContentTitle(title)
                        .setContentText(text);

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.setAction(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse("event_id/"+event_id));
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        7190,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
