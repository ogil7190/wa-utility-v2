package com.bluebulls.apps.whatsapputility.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.ReminderAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.Data;
import com.bluebulls.apps.whatsapputility.entity.actors.Reminder;
import com.bluebulls.apps.whatsapputility.services.MyAlarmReceiver;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

/**
 * Created by dell on 8/5/2017.
 */

public class FragmentReminder extends Fragment {
    private ListView listView3;
    public static final String TAG = "FragmentReminder";
    private ArrayList<Reminder> reminderArrayList = new ArrayList<>();

    private static AlertDialog alertDialog;
    static FragmentManager manager;
    private SparkButton addReminder;

    private SharedPreferences pref;

    private String DATE="date";
    private String MONTH="month";
    private String YEAR="year";
    private String HOUR="hour";
    private String MINUTE="minute";
    private String EVENT="event";
    private String DESCRIPTION="description";
    private String SIZE="reminderArrayListSize";
    private String eve,des;
    private ReminderAdapter reminderAdapter;
    public static TextView datetxt,timetxt;
    public static int date2, year2;

    private String date_time = "";

    public static final String PREF_REM_ID_KEY = "rem_id-";
    public static final String PREF_REM_TITLE_KEY = "rem_title-";
    public static final String PRE_REM_DESC_KEY = "rem_desc-";
    public static final String PREF_REM_DATE_TIME_KEY = "rem_date_time-";
    public static final String PREF_REM_SIZE_KEY = "rem_size-";

    private long alarmTime;
    public static int hour2, minute2;
    private CircularProgressView chatHeadImg;

    public static String month2;
    private SingleDateAndTimePickerDialog singleDateAndTimePickerDialog;
    public static FragmentReminder newInstance(FragmentManager man) {
        Bundle args = new Bundle();
        manager = man;
        FragmentReminder fragment = new FragmentReminder();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences(PREF_USER,MODE_PRIVATE);

        View v = inflater.inflate(R.layout.reminder_fragment, null);
        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.reminder_dialog, null);
        LinearLayout l3 = (LinearLayout) inflater.inflate(R.layout.custom_dialog_title3, null);
        datetxt=(TextView)l.findViewById(R.id.date);
        timetxt=(TextView)l.findViewById(R.id.time);
        final EditText event = (EditText) l.findViewById(R.id.event);
        final EditText description = (EditText) l.findViewById(R.id.description);
        addReminder = (SparkButton) v.findViewById(R.id.reminderbtn);
        addReminder.setAnimationSpeed(1.5f);
        listView3 = (ListView) v.findViewById(R.id.list_view2);

        reminderAdapter = new ReminderAdapter(reminderArrayList, getContext());
        listView3.setAdapter(reminderAdapter);
        chatHeadImg = (CircularProgressView)v.findViewById(R.id.chathead_img_main);
        chatHeadImg.setVisibility(View.GONE);
        loadReminder();
        singleDateAndTimePickerDialog=new SingleDateAndTimePickerDialog.Builder(getContext())
                .curved()
                .minutesStep(1)
                .titleTextColor(Color.WHITE)
                .mainColor(Color.rgb(255,140,0))
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {

                    }
                })
                .title("Pick Time")
                .defaultDate(Calendar.getInstance().getTime())
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        alarmTime = date.getTime();
                        if(alarmTime - System.currentTimeMillis() > 0) {
                            String test = date.toString().replace("Mon", "").replace("Tue", "").replace("Wed", "").replace("Thu", "")
                                    .replace("Mon", "").replace("Fri", "").replace("Sat", "").replace("Sun", "")
                                    .replace("GMT+05:30", "");
                            hour2 = Integer.parseInt(String.valueOf(test.charAt(8)) + String.valueOf(test.charAt(9)));
                            minute2 = Integer.parseInt(String.valueOf(test.charAt(11)) + String.valueOf(test.charAt(12)));
                            date2 = Integer.parseInt(String.valueOf(test.charAt(5)) + String.valueOf(test.charAt(6)));
                            month2 = String.valueOf(test.charAt(1)) + String.valueOf(test.charAt(2)) + String.valueOf(test.charAt(3));
                            year2 = Integer.parseInt(String.valueOf(test.charAt(18)) + String.valueOf(test.charAt(19)) + String.valueOf(test.charAt(20)) + String.valueOf(test.charAt(21)));
                            datetxt.setText(date2 + " " + month2 + " " + year2);
                            if (hour2 < 10) {
                                if (minute2 < 10) {
                                    timetxt.setText("0" + hour2 + ":0" + minute2);
                                } else {
                                    timetxt.setText("0" + hour2 + ":" + minute2);
                                }
                            } else {
                                if (minute2 < 10) {
                                    timetxt.setText(hour2 + ":0" + minute2);
                                } else {
                                    timetxt.setText(hour2 + ":" + minute2);
                                }
                            }
                            Log.e("TAG", "onDateSelected: " + test);
                            alertDialog.show();
                        } else
                            Toast.makeText(getContext(),"Must be a valid date time",Toast.LENGTH_SHORT).show();
                    }
                }).build();

        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setView(l)
                .setCustomTitle(l3)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eve = event.getText().toString();
                        des = description.getText().toString();
                        date_time= datetxt.getText().toString()+"|"+timetxt.getText().toString();
                        int rem_id = pref.getInt(PREF_REM_ID_KEY, -1) +1;
                        pref.edit().putInt(PREF_REM_ID_KEY, rem_id).commit();
                        saveReminder(new Reminder(rem_id, eve, des, date_time));
                        addToReminder(rem_id, getAlarmTime(date_time));
                        loadReminder();
                    }
                })
                .setNegativeButton("Set Date & Time", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDateAndTimePickerDialog.display();

                    }
                })
                .create();
        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder.playAnimation();
                event.setText(null);
                description.setText(null);
                alertDialog.show();
            }
        });
        return v;
    }

    private long getAlarmTime(String date_time){
        String date = date_time.substring(0, date_time.indexOf("|"));
        String time = date_time.substring(date_time.indexOf("|") + 1, date_time.length());
        Calendar calendar = Calendar.getInstance();
        String dates[] = date.split(" ");
        String times[] = time.split(":");
        calendar.set(Calendar.DAY_OF_MONTH,Integer.valueOf(dates[0]));
        calendar.set(Calendar.MONTH, getMonth(dates[1]));
        calendar.set(Calendar.YEAR, Integer.valueOf(dates[2]));
        calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(times[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(times[1]));
        return calendar.getTime().getTime();
    }

    private int getMonth(String month){
        switch (month.toLowerCase()){
            case "jan": return 0;
            case "feb": return 1;
            case "mar": return 2;
            case "apr": return 3;
            case "may": return 4;
            case "jun": return 5;
            case "jul": return 6;
            case "aug": return 7;
            case "sep": return 8;
            case "oct": return 9;
            case "nov": return 10;
            case "dec": return 11;
            default: return -1;
        }
    }

    private boolean addToReminder(int rem_id, long alarmTime){
        Intent i = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        i.setAction("NEW_REM_ALARM#"+rem_id);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        long time = alarmTime - System.currentTimeMillis();

        if(time<0){
            Toast.makeText(getContext(), "Reminder Already gone!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
            return true;
        }
    }

    private boolean saveReminder(Reminder reminder)
    {
        SharedPreferences.Editor editor = pref.edit();
        int i = reminder.getRem_id();
        editor.putInt(PREF_REM_ID_KEY + i, reminder.getRem_id());
        editor.putString(PREF_REM_TITLE_KEY + i, reminder.getReminder_title());
        editor.putString(PRE_REM_DESC_KEY + i, reminder.getReminder_desc());
        editor.putString(PREF_REM_DATE_TIME_KEY + i, reminder.getDate_time());
        editor.putInt(PREF_REM_SIZE_KEY, reminder.getRem_id() + 1);
       return editor.commit();
    }
    private void loadReminder()
    {
        reminderArrayList.clear();
        int size = pref.getInt(PREF_REM_SIZE_KEY, 0);
        Log.e("TAG", "loadReminder: "+size );

        if(size!=0)
            for(int i=0; i<size; i++)
            {
                Reminder reminder = new Reminder(pref.getInt(PREF_REM_ID_KEY + i, 0), pref.getString(PREF_REM_TITLE_KEY + i, ""), pref.getString(PRE_REM_DESC_KEY + i, ""), pref.getString(PREF_REM_DATE_TIME_KEY + i, ""));
                reminderArrayList.add(reminder);
            }

        Collections.reverse(reminderArrayList);
        reminderAdapter.notifyDataSetChanged();
    }

}
