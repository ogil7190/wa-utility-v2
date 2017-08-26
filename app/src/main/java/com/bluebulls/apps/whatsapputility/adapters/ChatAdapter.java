package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.text.Layout;
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
        if(convertView==null) {
            v = l.inflate(R.layout.single_layout_chat_mssg, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.name = (TextView) v.findViewById(R.id.name);
            viewHolder.mssg = (TextView) v.findViewById(R.id.mssg);
            viewHolder.chatCard=(CardView)v.findViewById(R.id.chat_card);
            v.setTag(viewHolder);
        }
        else {
            v=convertView;
            viewHolder=(ViewHolder)v.getTag();
        }
        viewHolder.mssg.setText(msg.getMessageText());
        viewHolder.name.setText(msg.getMessageUser());
        LinearLayout.LayoutParams p1 = (LinearLayout.LayoutParams)viewHolder.mssg.getLayoutParams();

        if(msg.isMine()){
            //viewHolder.chatCard.setBackground();
            p1.gravity = Gravity.RIGHT;
            viewHolder.name.setGravity(Gravity.RIGHT);
        }
        else
            viewHolder.chatCard.setBackgroundColor(Color.BLUE);
        viewHolder.mssg.setLayoutParams(p1);
        return v;
    }

    static class ViewHolder {
        CardView chatCard;
        private TextView name, mssg;
    }
}
