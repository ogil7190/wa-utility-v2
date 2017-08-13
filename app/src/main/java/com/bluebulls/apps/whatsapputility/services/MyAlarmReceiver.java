package com.bluebulls.apps.whatsapputility.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.HomeActivity;

public class MyAlarmReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("ALM","ACTION:"+intent.getAction());
        if (intent.getAction().contains("NEW_ALARM")) {
            Toast.makeText(context, "Alarm Rang", Toast.LENGTH_SHORT).show();
            String event_id = intent.getAction().substring(intent.getAction().indexOf("#")+1, intent.getAction().length());
            Log.d("EVENT_ID:",event_id);
            showNotification("Event has Arrived!","Tap to view event",event_id);
            /*Uri uri=Uri.parse("android.resource://com.bluebulls.apps.whatsapputility/"+R.raw.audio);
            mp=new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mp.setDataSource(context,uri);
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }
    private void showNotification(String title, String text, String event_id){
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
