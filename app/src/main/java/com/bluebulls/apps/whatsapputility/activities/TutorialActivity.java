package com.bluebulls.apps.whatsapputility.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bluebulls.apps.whatsapputility.R;
import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

public class TutorialActivity extends AppCompatActivity implements BetterVideoCallback {
BetterVideoPlayer player;
    //TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Uri uri=Uri.parse("android.resource://com.bluebulls.apps.whatsapputility/"+R.raw.video);
        //Uri uri=Uri.parse("https://www.bing.com/videos/search?q=video&view=detail&mid=815506C1A033A0CA3F66815506C1A033A0CA3F66&FORM=VIRE");
        player=(BetterVideoPlayer)findViewById(R.id.player);
        //textView=(TextView)findViewById(R.id.percent);
        //textView.setVisibility(View.GONE);
        player.setCallback(this);
        player.enableSwipeGestures(getWindow());
        player.setSource(uri);
    }

    @Override
    public void onStarted(BetterVideoPlayer player) {

    }

    @Override
    public void onPaused(BetterVideoPlayer player) {

    }

    @Override
    public void onPreparing(BetterVideoPlayer player) {

    }

    @Override
    public void onPrepared(BetterVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {
        /*textView.setVisibility(View.VISIBLE);
        textView.setText(percent);*/
    }

    @Override
    public void onError(BetterVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(BetterVideoPlayer player) {

    }

    @Override
    public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {

    }
}
