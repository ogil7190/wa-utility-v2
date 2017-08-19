package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;

import java.util.ArrayList;

/**
 * Created by ogil on 19/08/17.
 */

public class AboutPollAdapter extends BaseAdapter {

    private ArrayList<String> participants;
    private Context context;

    public AboutPollAdapter(ArrayList<String> participants,  Context context) {
        this.context = context;
        this.participants = participants;
    }

    @Override
    public int getCount() {
        return participants.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater l= LayoutInflater.from(context);
        View v;
        final String q=  participants.get(position);
        ViewHolder viewHolder;

        if(convertView==null) {
            v = l.inflate(R.layout.single_layout_about_poll, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.person = (TextView) v.findViewById(R.id.person);
            v.setTag(viewHolder);
        }
        else {
            v=convertView;
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.person.setText(q);

        return v;
    }

    static class ViewHolder{
        private TextView person;
    }
}
