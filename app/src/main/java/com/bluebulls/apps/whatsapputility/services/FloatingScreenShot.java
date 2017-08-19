package com.bluebulls.apps.whatsapputility.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.util.CustomLayout;
import com.bluebulls.apps.whatsapputility.util.SSBridge;

import java.io.File;

import static com.bluebulls.apps.whatsapputility.util.SSBridge.STOP_SS_SERV;

/**
 * Created by ogil on 08/08/17.
 */

public class FloatingScreenShot extends Service implements CustomLayout.BackButtonListener {

    private WindowManager mWindowManager;
    private View mFloatingSS;

    private SSBridge bridge = new SSBridge();
    public static boolean isVisible = false;
    public FloatingScreenShot() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (startId == Service.START_STICKY) {
            Bundle bundle = intent.getExtras();
            Uri uri = Uri.parse(bundle.getString("img_uri"));
            handleStart(uri);
            return super.onStartCommand(intent, flags, startId);
        }
        else {
            return Service.START_NOT_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(bridge, new IntentFilter(STOP_SS_SERV));
    }

    private void handleStart(final Uri imgUri){
        mFloatingSS = LayoutInflater.from(this).inflate(R.layout.floating_ss, null);
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 20;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingSS, params);

        //Drag and move chat head using user's touch action.
        final ImageView ss = (ImageView) mFloatingSS.findViewById(R.id.ss);
        Bitmap myBitmap = BitmapFactory.decodeFile(new File(imgUri.getPath()).getAbsolutePath());
        ss.setImageBitmap(myBitmap);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isVisible = true;
            }
        });
        t.start();

        ss.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            shareOnWhatsApp(imgUri);
                            //close the service and remove the chat heads
                            stopSelf();
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        //params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingSS, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
                deleteShot(imgUri);
            }
        }, 30000); /* close screen shot after 30 sec */
    }

    private void deleteShot(Uri uri){
        File file = new File(uri.getPath());
        if (file.exists())
            file.delete();
    }

    public static final String WHATS_APP_PACKAGE = "com.whatsapp";

    private void shareOnWhatsApp(Uri imgUri){
        Intent intent = new Intent();
        intent.setPackage(WHATS_APP_PACKAGE);
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, "ScreenShot using Paradox: WhatsApp Utility!");
        intent.putExtra(Intent.EXTRA_STREAM, imgUri);
        intent.setType("image/*");
        startActivity(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        isVisible = false;
        super.onDestroy();
        if (mFloatingSS != null) mWindowManager.removeView(mFloatingSS);
        unregisterReceiver(bridge);
    }

    @Override
    public void onBackButtonPressed() {
        stopSelf();
    }
}
