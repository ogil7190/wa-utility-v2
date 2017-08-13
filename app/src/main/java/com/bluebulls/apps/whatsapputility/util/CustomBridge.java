package com.bluebulls.apps.whatsapputility.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluebulls.apps.whatsapputility.services.ChatHeadService;

/**
 * Created by ogil on 25/07/17.
 */

public class CustomBridge extends BroadcastReceiver {

    public static final String STOP_SELF = "Stop Service";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case STOP_SELF:
                context.stopService(new Intent(context, ChatHeadService.class));
                break;
        }
    }
}
