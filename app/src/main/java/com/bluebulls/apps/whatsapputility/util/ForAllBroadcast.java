package com.bluebulls.apps.whatsapputility.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluebulls.apps.whatsapputility.services.PackageService;

import static com.bluebulls.apps.whatsapputility.fragments.FragmentSettings.STOP_SELF_PACK;


/**
 * Created by ogil on 28/08/17.
 */

public class ForAllBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STOP_SELF_PACK)) {
            context.stopService(new Intent(context, PackageService.class));
            context.startService(new Intent(context, PackageService.class));
        }
    }
}
