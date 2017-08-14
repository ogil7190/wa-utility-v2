package com.bluebulls.apps.whatsapputility.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluebulls.apps.whatsapputility.services.ChatHeadService;
import com.bluebulls.apps.whatsapputility.services.FloatingScreenShot;

/**
 * Created by ogil on 25/07/17.
 */

public class SSBridge extends BroadcastReceiver {

    public static final String STOP_SS_SERV = "Stop SS Service";
    public static boolean closed = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case STOP_SS_SERV:
                if(FloatingScreenShot.isVisible) {
                    context.stopService(new Intent(context, FloatingScreenShot.class));
                    closed = true;
                }
                break;
        }
    }
}
