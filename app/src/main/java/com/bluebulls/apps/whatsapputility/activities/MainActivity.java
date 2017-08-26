package com.bluebulls.apps.whatsapputility.activities;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.util.OverlayUtil;
import com.bluebulls.apps.whatsapputility.services.CustomNotificationListener;
import com.bluebulls.apps.whatsapputility.services.PackageService;
import com.bluebulls.apps.whatsapputility.services.RootService;

import java.util.List;

import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;

public class MainActivity extends AppCompatActivity {

    public static final int OVERLAY_PERMISSION_REQ_CODE_CHAT_HEAD = 7171;
    public static boolean haveAutoStartPerm = false;
    private SharedPreferences pref;
    ImageView imageView;
    TextView notify,overlay;
    Switch notifications,drawOvelay,ua;
    public static final String TAG = "WhatsApp Poll";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearNotification();
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences(PREF_USER,MODE_PRIVATE);

        notify=(TextView)findViewById(R.id.notifyTxt);
        overlay=(TextView)findViewById(R.id.overlayTxt);
        imageView=(ImageView)findViewById(R.id.image);

        drawOvelay=(Switch)findViewById(R.id.drawOverlay);
        notifications=(Switch)findViewById(R.id.notifications);
        ua = (Switch)findViewById(R.id.ua);

        checkPermissions();
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });
        drawOvelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHAT_HEAD);
            }
        });

        ua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });

        if(pref.getBoolean(PREF_KEY_FIRST_TIME_BOOL,true)) {
            fraudOS();
            pref.edit().putBoolean(PREF_KEY_FIRST_TIME_BOOL,false).commit();
        }
        if(dr==true&&no==true&&us==true)
        {
            gotoHome();
        }
    }

    public static final String PREF_KEY_FIRST_TIME_BOOL = "first-time";

    private void fraudOS(){
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            }

            List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                this.startActivity(intent);
                Toast.makeText(getApplicationContext(),"Please enable auto start for \nWhatsApp Utility",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d(TAG,"Error:"+e);
        }
    }

    @Override
    protected void onResume() {
        checkPermissions();
        super.onResume();
    }

    private boolean isAllSet = true;
    private boolean no=false;
    private boolean dr=false;
    private boolean us=false;
    private void checkPermissions(){
        if(OverlayUtil.canDrawOverlays(this)) {
            drawOvelay.setChecked(true);
            isAllSet = isAllSet && true;
            dr=true;
        }
        else {
            isAllSet = false;
            drawOvelay.setChecked(false);
        }
        if(checkNLPermission()) {
            notifications.setChecked(true);
            no=true;
            isAllSet = isAllSet && true;
        }
        else {
            isAllSet = false;
            notifications.setChecked(false);
        }
        if(hasUsageStatsPermission(this)){
            ua.setChecked(true);
            us=true;
            isAllSet = isAllSet && true;
        }
        else {
            isAllSet = false;
            ua.setChecked(false);
        }
        if(dr==true&&no==true&&us==true)
        {
            gotoHome();
        }
    }

    private void gotoHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        AlarmManager alarm = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        /*Intent i = new Intent(getApplicationContext(), PackageService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 7190, i, 0);
        alarm.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi); */


        /*Intent in = new Intent(getApplicationContext(), CustomNotificationListener.class);
        PendingIntent p = PendingIntent.getService(getApplicationContext(), 7191, in, 0);
        alarm.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), p); */
        Thread service = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(getApplicationContext(), CustomNotificationListener.class);
                startService(in);
            }
        });
        service.start();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + RootService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error finding setting\nDefault accessibility to not found\nManually do this",
                    Toast.LENGTH_LONG).show();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    private void clearNotification(){
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

        private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This permission is very important.\nYou need to allow permission");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(requestCode);
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    private boolean isNLServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CustomNotificationListener.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkNLPermission(){
        boolean weHaveNotificationListenerPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            for (String service : NotificationManagerCompat.getEnabledListenerPackages(this)) {
                if (service.equals(getPackageName()))
                    weHaveNotificationListenerPermission = true;
            }
        }
        return weHaveNotificationListenerPermission;
    }

    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHAT_HEAD) {
            if (!OverlayUtil.canDrawOverlays(this)) {
                needPermissionDialog(requestCode);
            }

        }
    }
}
