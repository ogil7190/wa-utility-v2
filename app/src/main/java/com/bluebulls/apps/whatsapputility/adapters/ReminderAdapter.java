package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.entity.actors.Reminder;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;

/**
 * Created by dell on 8/5/2017.
 */

public class ReminderAdapter extends BaseAdapter {
    ArrayList<Reminder> reminderArrayList;
    Context c;

    public ReminderAdapter(ArrayList<Reminder> reminderArrayList, Context c) {
        this.reminderArrayList = reminderArrayList;
        this.c = c;
    }

    @Override
    public int getCount() {
        return reminderArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater l= LayoutInflater.from(c);
        View v;
        final Reminder s = reminderArrayList.get(position);
        ViewHolder viewHolder;
        if(convertView==null) {
            v = l.inflate(R.layout.single_list_reminder, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.eventTxt=(TextView)v.findViewById(R.id.eventTxt);
            viewHolder.descriptionTxt=(TextView)v.findViewById(R.id.eventDescription);
            viewHolder.date=(TextView)v.findViewById(R.id.date);
            viewHolder.time=(TextView)v.findViewById(R.id.time);
            v.setTag(viewHolder);
        }
        else {
            v=convertView;
            viewHolder=(ViewHolder)v.getTag();
        }
        viewHolder.eventTxt.setText(s.getReminder_title());
        viewHolder.descriptionTxt.setText(s.getReminder_desc());
        viewHolder.date.setText(getDate(s.getDate_time()));
        viewHolder.time.setText(getTime(s.getDate_time()));
        return v;
    }

    private String getDate(String date_time){
        return date_time.substring(0, date_time.indexOf("|"));
    }

    private String getTime(String date_time){
        return date_time.substring(date_time.indexOf("|") + 1, date_time.length());
    }

    static class ViewHolder{
        private TextView eventTxt,descriptionTxt,date,time;
        private SparkButton alarmButton;
    }
}
