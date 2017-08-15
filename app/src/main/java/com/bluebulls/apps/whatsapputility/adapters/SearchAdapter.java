package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;
import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.SearchActivity;
import com.bluebulls.apps.whatsapputility.entity.actors.Query;


import java.util.ArrayList;


/**
 * Created by ogil on 10/08/17.
 */

public class SearchAdapter extends BaseAdapter {

    private ArrayList<Query> queries;
    private Context context;

    public SearchAdapter(ArrayList<Query> queries, SearchActivity activity) {
        this.context = activity.getApplicationContext();
        this.queries = queries;
    }

    @Override
    public int getCount() {
        return queries.size();
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
        final Query q=  queries.get(position);
        ViewHolder viewHolder;

        if(convertView==null) {
            v = l.inflate(R.layout.single_layout_search_suggestion, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.query= (TextView) v.findViewById(R.id.query);
            v.setTag(viewHolder);
        }
        else {
            v=convertView;
            viewHolder=(ViewHolder)v.getTag();
        }

        viewHolder.query.setText(q.getQuery());

        return v;
    }

    static class ViewHolder{
        private TextView query;
    }
}
