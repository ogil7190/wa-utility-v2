package com.bluebulls.apps.whatsapputility.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.fragments.FragmentEvent;
import com.bluebulls.apps.whatsapputility.fragments.FragmentPoll;
import com.bluebulls.apps.whatsapputility.fragments.FragmentReminder;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;


public class HomeActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    public String data = "";
    private ViewPager viewPager;
    NavigationTabBar navigationTabBar;
    ArrayList<NavigationTabBar.Model> models=new ArrayList<>();
    Toolbar toolbar;
    private boolean isPollSelected = false;

    private static final int COUNT=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        navigationTabBar=(NavigationTabBar)findViewById(R.id.navigation);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_assessment_black_24dp),
                        Color.rgb(255,102,0)
                ).title("Heart")
                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_event_black_24dp),
                        Color.rgb(7,94,84)
                ).title("Cup")
                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_alarm_black_24dp),
                        Color.BLACK
                ).title("Diploma")
                        .badgeTitle("state")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_settings_black_18dp),
                        Color.BLUE
                ).title("Flag")
                        .badgeTitle("icon")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_error_outline_black_18dp),
                        Color.RED
                ).title("Flag")
                        .badgeTitle("icon")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager);
        checkReply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    int selection = 0;

    private void checkReply(){
        Intent intent = getIntent();
        fragmentManager=getSupportFragmentManager();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri incomingData = intent.getData();
            List<String> params = incomingData.getPathSegments();

            switch (params.get(0)){
                case "poll_id" :
                    data = params.get(1);
                    selection = 0;
                    viewPager.setCurrentItem(0,true);
                    break;
                case "event_id" :
                    data = params.get(1);
                    viewPager.setCurrentItem(1,true);
                    selection = 1;
                    break;
            }

            if(params.size()>0) {
                intent.setAction(Intent.ACTION_VOICE_COMMAND);
            }
        }

        else if(Intent.ACTION_SEND.equals(intent.getAction())){
            if(intent.getType().equals("text/ogil")){
                Log.d("HOME","We have poll!");
                data = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d("HOME","PollID:"+data);
                intent.setAction(Intent.ACTION_VOICE_COMMAND);
            }
        }
        else viewPager.setCurrentItem(0,true);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return data.equals("") ? FragmentPoll.newInstance(false,data):FragmentPoll.newInstance(true,data);
            if(position==1)
                return data.equals("") ? FragmentEvent.newInstance(fragmentManager,false,data): FragmentEvent.newInstance(fragmentManager,true,data);
            if(position==2)
                return FragmentReminder.newInstance(fragmentManager);
            else
                return FragmentReminder.newInstance(fragmentManager);
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }
}
