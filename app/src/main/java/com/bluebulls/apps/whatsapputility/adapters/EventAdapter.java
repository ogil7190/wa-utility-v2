package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.entity.actors.Event;
import com.bluebulls.apps.whatsapputility.fragments.FragmentEvent;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;

/**
 * Created by dell on 8/5/2017.
 */

public class EventAdapter extends BaseAdapter {
    ArrayList<Event> eventArrayList;
    Context c;
    FragmentEvent event;

    public EventAdapter(ArrayList<Event> eventArrayList, Context c, FragmentEvent event) {
        this.eventArrayList = eventArrayList;
        this.c = c;
        this.event = event;

    }

    @Override
    public int getCount() {
        return eventArrayList.size();
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
        final Event e=  eventArrayList.get(position);
        ViewHolder viewHolder;
        if(convertView==null) {
            v = l.inflate(R.layout.single_list_event, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.eventTxt=(TextView)v.findViewById(R.id.eventTxt);
            viewHolder.descriptionTxt=(TextView)v.findViewById(R.id.eventDescription);
            viewHolder.date=(TextView)v.findViewById(R.id.date);
            viewHolder.time=(TextView)v.findViewById(R.id.time);
            viewHolder.imageButton=(SparkButton) v.findViewById(R.id.addToReminder);
            viewHolder.imageButton.setOnClickListener(event.getListener(1));
            //viewHolder.imageButton.setInactiveImage(R.drawable.ic_alarm_off_black_18dp);
            v.setTag(viewHolder);
        }
        else {
            v=convertView;
            viewHolder=(ViewHolder)v.getTag();
        }

        viewHolder.eventTxt.setText(e.getTopic());
        viewHolder.descriptionTxt.setText(e.getDescriptioin());
        viewHolder.date.setText(e.getTime().substring(0,e.getTime().indexOf("|")));
        viewHolder.time.setText(e.getTime().substring(e.getTime().indexOf("|") + 1,e.getTime().length()));
        return v;
    }
    static class ViewHolder{
        TextView eventTxt,descriptionTxt,date,time;
        SparkButton imageButton;
    }
}
