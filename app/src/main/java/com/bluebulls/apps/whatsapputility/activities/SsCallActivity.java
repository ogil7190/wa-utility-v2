package com.bluebulls.apps.whatsapputility.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.lib.libscreenshotter.ScreenshotCallback;
import com.bluebulls.apps.whatsapputility.lib.libscreenshotter.Screenshotter;
import com.bluebulls.apps.whatsapputility.services.FloatingScreenShot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class SsCallActivity extends Activity {

    public static final String TAG = "SS";
    private MediaProjectionManager mProjectionManager;

    public static final int REQUEST_CODE_SCREEN_SHOT = 7191;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss_call);
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_SHOT);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CODE_SCREEN_SHOT == requestCode){
            if(resultCode == RESULT_OK){
                Screenshotter.getInstance()
                        .setDefaultSize(this)
                        .takeScreenshot(getApplicationContext(), resultCode, data, new ScreenshotCallback() {
                            @Override
                            public void onScreenshot(Bitmap bitmap) {
                                File folder = new File(Environment.getExternalStorageDirectory() +
                                        File.separator + "WA");
                                boolean success = true;
                                if (!folder.exists()) {
                                    success = folder.mkdirs();
                                }
                                if (success) {
                                    File img = new File(folder,"wau-img-"+getDate()+"-"+getRandFileName()+".png");
                                    try {
                                        FileOutputStream outputStream = new FileOutputStream(img);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
                                        outputStream.flush();
                                        outputStream.close();
                                        Intent i = new Intent(getApplicationContext(), FloatingScreenShot.class);
                                        i.putExtra("img_uri", Uri.parse(img.toString()).toString());
                                        startService(i);
                                        shutterEffect();
                                        finish();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    Log.d("SS","Error creating file!");
                                }
                            }
                        });
            }
            else
                finish();
        }
    }

    protected String getRandFileName() {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private String getDate(){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        return date;
    }

    private void shutterEffect(){
        final FrameLayout flash = (FrameLayout)findViewById(R.id.pnlFlash);
        flash.setVisibility(View.VISIBLE);
        AlphaAnimation fade = new AlphaAnimation(1, 0);
        fade.setDuration(20);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation anim) {
                flash.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flash.startAnimation(fade);
    }

}