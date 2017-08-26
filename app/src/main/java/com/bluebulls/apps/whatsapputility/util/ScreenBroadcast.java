package com.bluebulls.apps.whatsapputility.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluebulls.apps.whatsapputility.services.CustomNotificationListener;

/**
 * Created by ogil on 26/08/17.
 */

public class ScreenBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.d("App", "Screen On");
            Thread service = new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent in = new Intent(context, CustomNotificationListener.class);
                    context.startService(in);
                }
            });
            service.start();
        }

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.d("App", "Screen Off");
        }

        if(intent.getAction() == Intent.ACTION_SCREEN_OFF){
            Log.d("App", "Screen Off2");
        }
    }
}
