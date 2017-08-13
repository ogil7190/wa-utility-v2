package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.entity.actors.Data;
import com.bluebulls.apps.whatsapputility.R;

import java.util.ArrayList;

/**
 * Created by dell on 8/5/2017.
 */

public class ReminderAdapter extends BaseAdapter {
    ArrayList<Data> reminderArrayList;
    Context c;

    public ReminderAdapter(ArrayList<Data> reminderArrayList, Context c) {
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
        final Data s=  reminderArrayList.get(position);
        ViewHolder viewHolder;
        if(convertView==null) {
            v = l.inflate(R.layout.single_list_event, parent, false);
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
        viewHolder.eventTxt.setText(s.getEvent());
        viewHolder.descriptionTxt.setText(s.getDescription());
        viewHolder.date.setText(s.getDate()+" "+s.getMonth()+" "+s.getYear());
        if(s.getHour()<10)
        {
            if(s.getMinute()<10)
            {
                viewHolder.time.setText("0"+s.getHour()+":0"+s.getMinute());
            }
            else
            {
                viewHolder.time.setText("0"+s.getHour()+":"+s.getMinute());
            }
        }
        else
        {
            if(s.getMinute()<10)
            {
                viewHolder.time.setText(s.getHour()+":0"+s.getMinute());
            }
            else
            {
                viewHolder.time.setText(s.getHour()+":"+s.getMinute());
            }
        }
        return v;
    }
    static class ViewHolder{
        TextView eventTxt,descriptionTxt,date,time;
    }
}
