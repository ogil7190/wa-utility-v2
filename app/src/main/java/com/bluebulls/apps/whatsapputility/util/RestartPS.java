package com.bluebulls.apps.whatsapputility.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluebulls.apps.whatsapputility.services.PackageService;

import static com.bluebulls.apps.whatsapputility.services.PackageService.PACK_RESTART;

/**
 * Created by ogil on 16/08/17.
 */

public class RestartPS  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Restarted! Pack");
        switch (intent.getAction()){
            case PACK_RESTART:
                context.startService(new Intent(context, PackageService.class));
                System.out.println("Restarted! PackKKK");
                break;
        }
    }
}

