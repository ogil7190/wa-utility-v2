package com.bluebulls.apps.whatsapputility.entity.actors;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Created by ogil on 09/08/17.
 */

public class FadingTextViewAnimator {
    TextView blobText;
    public String[] text = new String[] { "" };
    public int position = 0;
    Animation fadeiInAnimationObject;
    Animation textDisplayAnimationObject;
    Animation delayBetweenAnimations;
    Animation fadeOutAnimationObject;
    int fadeEffectDuration;
    int delayDuration;
    int displayFor;


    public FadingTextViewAnimator(TextView textV, String[] textList)
    {
        this(textV,300,500,1000, textList);
    }
    public FadingTextViewAnimator(TextView textView, int fadeEffectDuration, int delayDuration, int displayLength, String[] textList)
    {
        blobText = textView;
        text = textList;
        this.fadeEffectDuration = fadeEffectDuration;
        this.delayDuration = delayDuration;
        this.displayFor = displayLength;
        InnitializeAnimation();
    }

    private void InnitializeAnimation()
    {
        fadeiInAnimationObject = new AlphaAnimation(0f, 1f);
        fadeiInAnimationObject.setDuration(fadeEffectDuration);
        textDisplayAnimationObject = new AlphaAnimation(1f, 1f);
        textDisplayAnimationObject.setDuration(displayFor);
        delayBetweenAnimations = new AlphaAnimation(0f, 0f);
        delayBetweenAnimations.setDuration(delayDuration);
        fadeOutAnimationObject = new AlphaAnimation(1f, 0f);
        fadeOutAnimationObject.setDuration(fadeEffectDuration);
        fadeiInAnimationObject.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                position++;
                if(position>=text.length)
                {
                    position = 0;
                }
                blobText.setText(text[position]);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                blobText.startAnimation(textDisplayAnimationObject);
            }
        });
        textDisplayAnimationObject.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                blobText.startAnimation(fadeOutAnimationObject);
            }
        });
        fadeOutAnimationObject.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                blobText.startAnimation(delayBetweenAnimations);
            }
        });
        delayBetweenAnimations.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                blobText.startAnimation(fadeiInAnimationObject);
            }
        });
    }

    public void startAnimation()
    {
        blobText.startAnimation(fadeOutAnimationObject);
    }

    public void stopAnimation(){
        blobText.setVisibility(View.GONE);
        fadeiInAnimationObject.setAnimationListener(null);
        blobText.clearAnimation();
    }
}
