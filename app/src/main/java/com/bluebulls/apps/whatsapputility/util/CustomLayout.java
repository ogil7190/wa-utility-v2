package com.bluebulls.apps.whatsapputility.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * Created by ogil on 25/07/17.
 */

public class CustomLayout extends LinearLayout {

    public interface BackButtonListener {
        void onBackButtonPressed();
    }

    public interface HomeButtonListener {
        void onHomeButtonPressed();
    }

    @Nullable
    private HomeButtonListener hListener;

    @Nullable
    private BackButtonListener mListener;

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setBackButtonListener(@Nullable BackButtonListener listener) {
        mListener = listener;
    }

    public void setHomeButtonListner(@Nullable HomeButtonListener listener){
        hListener = listener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && mListener != null) {
            mListener.onBackButtonPressed();
            return true;
        }

        if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_HOME
                && hListener != null){
            hListener.onHomeButtonPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
