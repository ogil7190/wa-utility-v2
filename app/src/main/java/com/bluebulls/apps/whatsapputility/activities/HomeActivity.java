package com.bluebulls.apps.whatsapputility.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bluebulls.apps.whatsapputility.fragments.FragmentEvent;
import com.bluebulls.apps.whatsapputility.fragments.FragmentPoll;
import com.bluebulls.apps.whatsapputility.fragments.FragmentReminder;
import com.bluebulls.apps.whatsapputility.R;

import java.util.List;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager;
    public String data = "";

    private boolean isPollSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager=getSupportFragmentManager();
        checkReply();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(!data.equals("")){
            selectedItem = navigationView.getMenu().getItem(selection).setChecked(true);
        }
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
                    goToPoll();
                    break;
                case "event_id" :
                    data = params.get(1);
                    goToEvent();
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
        else goToPoll();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    MenuItem selectedItem;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedItem = item;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_poll) {
           goToPoll();

        } else if (id == R.id.nav_gallery) {
            goToEvent();

        } else if (id == R.id.nav_slideshow) {
            goToReminder();

        } /*else if (id == R.id.nav_manage) {

        }*/ else if (id == R.id.nav_share) {

        } /*else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void goToPoll()
    {
        isPollSelected = true;
        FragmentTransaction PollTransaction=fragmentManager.beginTransaction();
        PollTransaction.replace(R.id.container_frame, data.equals("") ? FragmentPoll.newInstance(false,data):FragmentPoll.newInstance(true,data));
        data = "";
        PollTransaction.commit();
    }
    void goToEvent()
    {
        FragmentTransaction EventTransaction=fragmentManager.beginTransaction();
        EventTransaction.replace(R.id.container_frame, data.equals("") ? FragmentEvent.newInstance(fragmentManager,false,data): FragmentEvent.newInstance(fragmentManager,true,data));
        data = "";
        EventTransaction.commit();
    }
    void goToReminder()
    {
        FragmentTransaction ReminderTransaction=fragmentManager.beginTransaction();
        ReminderTransaction.replace(R.id.container_frame, FragmentReminder.newInstance(fragmentManager));
        ReminderTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
