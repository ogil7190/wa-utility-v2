package com.bluebulls.apps.whatsapputility.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;

public class CreditsActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView ramanPic,vivekPic;
    TextView ramanText,vivekText,ramanNumber,vivekNumber;
    ImageButton ramanFb,vivekFb,ramanInsta,vivekInsta,ramanTwit,vivekTwit,ramanLikendin,vivekLikendin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ramanPic=(ImageView)findViewById(R.id.ramanPic);
        ramanText=(TextView)findViewById(R.id.ramanText);
        ramanNumber=(TextView)findViewById(R.id.ramanNumber);
        ramanFb=(ImageButton)findViewById(R.id.ramanFb);
        ramanInsta=(ImageButton)findViewById(R.id.ramanInsta);
        ramanTwit=(ImageButton) findViewById(R.id.ramanTwitter);
        ramanLikendin=(ImageButton)findViewById(R.id.ramanLinkedin);
        vivekPic=(ImageView)findViewById(R.id.vivekPic);
        vivekText=(TextView)findViewById(R.id.vivekText);
        vivekNumber=(TextView)findViewById(R.id.vivekNumber);
        vivekFb=(ImageButton)findViewById(R.id.vivekFb);
        vivekInsta=(ImageButton)findViewById(R.id.vivekInsta);
        vivekTwit=(ImageButton) findViewById(R.id.vivekTwitter);
        vivekLikendin=(ImageButton)findViewById(R.id.vivekLinkedin);
        ramanPic.setOnClickListener(this);
        vivekPic.setOnClickListener(this);
        ramanText.setOnClickListener(this);
        vivekText.setOnClickListener(this);
        ramanNumber.setOnClickListener(this);
        vivekNumber.setOnClickListener(this);
        ramanFb.setOnClickListener(this);
        vivekFb.setOnClickListener(this);
        ramanInsta.setOnClickListener(this);
        vivekInsta.setOnClickListener(this);
        ramanTwit.setOnClickListener(this);
        vivekTwit.setOnClickListener(this);
        ramanLikendin.setOnClickListener(this);
        vivekLikendin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ramanNumber) {

        }
        if(v.getId()==R.id.ramanFb)
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.facebook.com/raman.kataria.16"));
            startActivity(i);
        }
        if(v.getId()==R.id.ramanInsta)
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.facebook.com"));
            startActivity(i);
        }
        if(v.getId()==R.id.ramanTwitter)
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.facebook.com"));
            startActivity(i);
        }
        if(v.getId()==R.id.ramanLinkedin)
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.facebook.com"));
            startActivity(i);
        }
        if(v.getId()==R.id.vivekNumber) {

        }
        if(v.getId()==R.id.vivekFb)
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.facebook.com/vivek.rajpoot.7190"));
            startActivity(i);
        }
        if(v.getId()==R.id.vivekInsta)
        {
            Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.instagram.com/ogil7190"));
            startActivity(i);
        }
    }
}
