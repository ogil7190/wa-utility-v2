package com.bluebulls.apps.whatsapputility.activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.MyViewPager;
import com.bluebulls.apps.whatsapputility.fragments.FragmentEvent;
import com.bluebulls.apps.whatsapputility.fragments.FragmentPoll;
import com.bluebulls.apps.whatsapputility.fragments.FragmentReminder;
import com.bluebulls.apps.whatsapputility.fragments.FragmentSettings;
import com.bluebulls.apps.whatsapputility.fragments.FragmentWish;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;


public class HomeActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private String data = "";
    private MyViewPager viewPager;
    private NavigationTabBar navigationTabBar;
    private ArrayList<NavigationTabBar.Model> models=new ArrayList<>();
    private Toolbar toolbar;
    private static final int COUNT=4;
    public static final String PREF_BATTERY_ENABLE = "to_enable_battery_fraud_os";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        clearNotification();
        pref = getSharedPreferences(PREF_USER, MODE_PRIVATE);

        if(pref.getBoolean(PREF_BATTERY_ENABLE, true)) {
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                Intent intent = new Intent();
                intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity");
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Disable Battery Saving Mode to\n NO RESTRICTION \n for WhatsApp Utility Here", Toast.LENGTH_LONG).show();
                pref.edit().putBoolean(PREF_BATTERY_ENABLE,false).commit();
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                Intent intent = new Intent();
                intent.setClassName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity");
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Disable Battery Saving Mode to\n NO RESTRICTION MODE \n for WhatsApp Utility Here", Toast.LENGTH_LONG).show();
                pref.edit().putBoolean(PREF_BATTERY_ENABLE,false).commit();
            }
        }

        fragmentManager = getSupportFragmentManager();
        viewPager=(MyViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(true, new AccordionTransformer());
        navigationTabBar=(NavigationTabBar)findViewById(R.id.navigation);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_assessment_black_24dp),
                        Color.rgb(7,94,84)
                ).title("Poll")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_event_black_24dp),
                        Color.rgb(255,102,0)
                ).title("Event")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_alarm_black_24dp),
                        Color.BLUE
                ).title("Reminder")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this,R.drawable.ic_settings_black_18dp),
                        Color.BLACK
                ).title("About")
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

    private int selection = 0;
    public static final String STR_EVENT_ID = "event_id";
    public static final String STR_POLL_ID = "poll_id";


    private void checkReply(){
        Intent intent = getIntent();
        fragmentManager=getSupportFragmentManager();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri incomingData = intent.getData();
            List<String> params = incomingData.getPathSegments();
            switch (params.get(0)){
                case STR_POLL_ID :
                    data = params.get(1);
                    selection = 0;
                    viewPager.setCurrentItem(0,false);
                    break;

                case STR_EVENT_ID :
                    data = params.get(1);
                    selection = 1;
                    viewPager.setCurrentItem(1,false);
                    break;
            }

            if(params.size()>0) {
                getIntent().setAction("");
            }
        }

        else if(Intent.ACTION_SEND.equals(intent.getAction())){
            if(intent.getType().equals("text/ogil")){
                data = intent.getStringExtra(Intent.EXTRA_TEXT);
                getIntent().setAction("");
            }
        }

        else viewPager.setCurrentItem(0,false);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                if(selection == position)
                    return FragmentPoll.newInstance(true, data);
                else
                    return FragmentPoll.newInstance(false, data);
            }

            if(position == 1) {
                if(selection == position)
                    return FragmentEvent.newInstance(fragmentManager, true, data);
                else
                    return FragmentEvent.newInstance(fragmentManager, false, data);
            }

            if(position == 2) {
                if(selection == position)
                    return FragmentReminder.newInstance(fragmentManager);
                else
                    return FragmentReminder.newInstance(fragmentManager);
            }
            if(position == 3) {
                if(selection == position)
                    return FragmentSettings.newInstance();
                else
                    return FragmentSettings.newInstance();
            }
            else{
                if(selection == position)
                    return FragmentPoll.newInstance(true, data);
                else
                    return FragmentPoll.newInstance(false, data);
            }
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }

    private void clearNotification(){
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
