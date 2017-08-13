package com.bluebulls.apps.whatsapputility.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bluebulls.apps.whatsapputility.util.CustomBridge.STOP_SELF;

/**
 * Created by ogil on 22/07/17.
 */

public class RootService extends AccessibilityService {
    private String TAG = "WhatsApp Poll:";
    public static final String SHARED_PREF_CONTACT_NAME = "WhatsApp_Contacts";

    private Intent chatHeadService;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_CONTACT_NAME, MODE_PRIVATE);

        chatHeadService = new Intent(RootService.this, ChatHeadService.class);

        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
        //startService(new Intent(getApplicationContext(), CustomNotificationListener.class));
        //startService(new Intent(getApplicationContext(), PackageService.class));
    }

    private boolean target = false, done = true;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );

                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity)
                    if(componentName.getPackageName().equals("com.whatsapp"))
                        target = true;
                    else {
                        target = false;
                        done = true;
                        Intent i = new Intent(STOP_SELF);
                        sendBroadcast(i);
                    }
            }
        }

        if(target && done){
            //fetchWhatsAppContacts();
            done = false;
            startService(chatHeadService);
        }
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String longestRepeatingSubstring (String string, int min, int max)
    {
        for (int i=max; i>=min; i--){
            for (int j=0; j<string.length()-i+1; j++){

                String substr = string.substring(j,j+i);
                String wonder = Pattern.quote(substr);
                Pattern pattern = Pattern.compile(wonder);
                Matcher matcher = pattern.matcher(string);

                int count = 0;
                while (matcher.find()) count++;

                if (count > 1) return substr;
            }
        }

        return null;
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void fetchWhatsAppContacts(){
        sharedPreferences.edit().clear().commit();
        int counter = 0;
        final String[] projection={
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.MIMETYPE,
                "account_type",
                ContactsContract.Data.DATA3,
        };
        final String selection= ContactsContract.Data.MIMETYPE+" =? and account_type=?";
        final String[] selectionArgs = {
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "com.whatsapp"
        };
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        while(c.moveToNext()){
            String id=c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            String number=c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
            String name="";
            Cursor mCursor=getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                    ContactsContract.Contacts._ID+" =?",
                    new String[]{id},
                    null);
            while(mCursor.moveToNext()){
                name=mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
            mCursor.close();
            putData(name, number);
            counter++;
        }
        Log.v("WhatsApp", "Total WhatsApp Contacts: "+counter);
        c.close();
    }

    private SharedPreferences sharedPreferences;
    private void putData(String name, String number) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String num = number.replace("Message ","");
        Log.d(TAG,"Name:"+name+" Number:"+num);
        editor.putString(name,num);
        editor.commit();
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
