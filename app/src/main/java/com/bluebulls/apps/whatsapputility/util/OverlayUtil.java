package com.bluebulls.apps.whatsapputility.util;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class OverlayUtil {

	public static boolean canDrawOverlays(Context context){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		else
		{
			return Settings.canDrawOverlays(context);
		}
	}


}
