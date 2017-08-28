package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.SearchActivity;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatMessage;
import com.bluebulls.apps.whatsapputility.entity.actors.Query;

import java.util.ArrayList;


/**
 * Created by ogil on 10/08/17.
 */

public class ChatAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> mssgs;
    private Context context;

    public ChatAdapter(ArrayList<ChatMessage> mssgs, Context context) {
        this.context = context;
        this.mssgs = mssgs;
    }

    @Override
    public int getCount() {
        return mssgs.size();
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
        final ChatMessage msg = mssgs.get(position);
        ViewHolder viewHolder;

        switch (msg.getType()){
            case 1: /* mine */
                v = l.inflate(R.layout.single_chat_msg_me, parent, false);
                break;
            case 2: /* others */
                v = l.inflate(R.layout.single_chat_msg_r, parent, false);
                break;
            case 3: /* events */
                v = l.inflate(R.layout.single_chat_msg_e, parent, false);
                break;
            default:
                v = l.inflate(R.layout.single_chat_msg_r, parent, false);
                break;
        }

        viewHolder=new ViewHolder();
        viewHolder.name = (TextView) v.findViewById(R.id.name);
        viewHolder.mssg = (TextView) v.findViewById(R.id.mssg);
        v.setTag(viewHolder);

        viewHolder.mssg.setText(msg.getMessageText());
        viewHolder.name.setText(msg.getMessageUser());

        return v;
    }

    static class ViewHolder {
        private TextView name, mssg;
    }
}
