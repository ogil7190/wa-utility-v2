package com.bluebulls.apps.whatsapputility.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bluebulls.apps.whatsapputility.R;

/**
 * Created by ogil on 02/09/17.
 */

public class ContextMenuLayout extends LinearLayout {

    private static final int CONTEXT_MENU_WIDTH = dpToPx(240);

    private int feedItem = -1;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public ContextMenuLayout(Context context) {
        super(context);
        init();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.context_menu, this, true);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button cancel = (Button)findViewById(R.id.btnCancel);
        Button request = (Button) findViewById(R.id.btnRequest);

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onCancelClick(feedItem);
            }
        });

        request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onSendRequestClick(feedItem);
            }
        });
    }

    public void bindToItem(int feedItem) {
        this.feedItem = feedItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(ContextMenuLayout.this);
    }

    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnFeedContextMenuItemClickListener {

        public void onSendRequestClick(int feedItem);

        public void onCancelClick(int feedItem);
    }
}