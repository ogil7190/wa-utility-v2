package com.bluebulls.apps.whatsapputility.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.ReminderAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.Data;
import com.bluebulls.apps.whatsapputility.services.MyAlarmReceiver;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dell on 8/5/2017.
 */

public class FragmentReminder extends Fragment {
    private ListView listView3;
    private ArrayList<Data> reminderArrayList = new ArrayList<>();
    private static AlertDialog alertDialog;
    static FragmentManager manager;
    private SparkButton addReminder;

    public static TextView datetxt,timetxt;
    public static int date2, year2;
    private PendingIntent pi;
    long alarmTime;
    private AlarmManager alarmManager;
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
        alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        final Intent intent=new Intent(getContext(),MyAlarmReceiver.class);
        pi=PendingIntent.getBroadcast(getContext(),0,intent,0);
        final ReminderAdapter reminderAdapter = new ReminderAdapter(reminderArrayList, getContext());
        listView3.setAdapter(reminderAdapter);
        chatHeadImg = (CircularProgressView)v.findViewById(R.id.chathead_img_main);
        chatHeadImg.setVisibility(View.GONE);

        singleDateAndTimePickerDialog=new SingleDateAndTimePickerDialog.Builder(getContext())
                //.bottomSheet()
                .curved()
                .minutesStep(1)
                //.mustBeOnFuture()
                .titleTextColor(Color.WHITE)
                .mainColor(Color.rgb(255,140,0))
                //.displayHours(false)
                //.displayMinutes(false)
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {
                        //retrieve the SingleDateAndTimePicker
                    }
                })

                .title("Pick Time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        String test=date.toString().replace("Mon","").replace("Tue","").replace("Wed","").replace("Thu","")
                                .replace("Mon","").replace("Fri","").replace("Sat","").replace("Sun","")
                                .replace("GMT+05:30","");
                        hour2=Integer.parseInt(String.valueOf(test.charAt(8))+String.valueOf(test.charAt(9)));
                        minute2=Integer.parseInt(String.valueOf(test.charAt(11))+String.valueOf(test.charAt(12)));
                        date2=Integer.parseInt(String.valueOf(test.charAt(5))+String.valueOf(test.charAt(6)));
                        month2=String.valueOf(test.charAt(1))+String.valueOf(test.charAt(2))+String.valueOf(test.charAt(3));
                        year2=Integer.parseInt(String.valueOf(test.charAt(18))+String.valueOf(test.charAt(19))+String.valueOf(test.charAt(20))+String.valueOf(test.charAt(21)));
                        datetxt.setText(date2+" "+month2+" "+year2);
                        if(hour2<10)
                        {
                            if(minute2<10)
                            {
                                timetxt.setText("0"+hour2+":0"+minute2);
                            }
                            else
                            {
                                timetxt.setText("0"+hour2+":"+minute2);
                            }
                        }
                        else
                        {
                            if(minute2<10)
                            {
                                timetxt.setText(hour2+":0"+minute2);                            }
                            else
                            {
                                timetxt.setText(hour2+":"+minute2);                            }
                        }
                        alarmTime=date.getTime();
                        Log.e("TAG", "onDateSelected: "+test);
                        alertDialog.show();
                    }
                }).build();
        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setView(l)
                .setCustomTitle(l3)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String eve = event.getText().toString();
                        String des = description.getText().toString();
                        reminderArrayList.add(new Data(eve, des, date2, month2, year2, hour2, minute2));
                        reminderAdapter.notifyDataSetChanged();
                        event.setText(null);
                        description.setText(null);
                        Log.e("TAG", "onClick: "+(alarmTime-System.currentTimeMillis()) );
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,alarmTime,pi);
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
                alertDialog.show();
            }
        });
        return v;
    }
}
