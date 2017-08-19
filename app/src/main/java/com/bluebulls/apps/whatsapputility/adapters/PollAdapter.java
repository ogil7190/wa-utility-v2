package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.entity.actors.Poll;
import com.bluebulls.apps.whatsapputility.fragments.FragmentPoll;
import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.entity.actors.Option;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 7/22/2017.
 */

public class PollAdapter extends BaseAdapter {
    private ArrayList<Poll> dataArrayList;
    private List<PieEntry> pieEntries;
    private PieData pieData;
    private Context c;
    private FragmentPoll fragmentPoll;

    public PollAdapter(ArrayList<Poll> dataArrayList, Context c, FragmentPoll fr) {
        this.dataArrayList = dataArrayList;
        this.c = c;
        this.fragmentPoll = fr;
        colors = getColors();
    }

    @Override
    public int getCount() {
        return dataArrayList.size();
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
        LayoutInflater l= LayoutInflater.from(c);
        View v;
        final Poll s=  dataArrayList.get(position);
        final Option[] o = s.getOptions();
        ViewHolder viewHolder;
        if(convertView==null) {
            v = l.inflate(R.layout.single_list_poll, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.title= (TextView) v.findViewById(R.id.title);
            viewHolder.pollChart=(PieChart)v.findViewById(R.id.piechart);

            viewHolder.refresh = (ImageButton)v.findViewById(R.id.refresh_poll);
            viewHolder.reply = (ImageButton)v.findViewById(R.id.reply_poll);
            viewHolder.forward = (ImageButton)v.findViewById(R.id.forward_poll);
            viewHolder.about = (ImageButton)v.findViewById(R.id.about_poll);
            setPieChart(o);
            v.setTag(viewHolder);
            View.OnClickListener refreshListener = fragmentPoll.setListeners(1,null);
            View.OnClickListener replyListener = fragmentPoll.setListeners(2,viewHolder.refresh);
            View.OnClickListener forwardListener=fragmentPoll.setListeners(4, null);
            View.OnClickListener aboutListener = fragmentPoll.setListeners(5, null);

            viewHolder.refresh.setOnClickListener(refreshListener);
            viewHolder.reply.setOnClickListener(replyListener);
            viewHolder.forward.setOnClickListener(forwardListener);
            viewHolder.about.setOnClickListener(aboutListener);
        }
        else {
            v=convertView;
            viewHolder=(ViewHolder)v.getTag();
            setPieChart(o);
        }
        viewHolder.pollChart.setData(pieData);
        viewHolder.pollChart.invalidate();
        viewHolder.pollChart.setCenterText("in %age");
        viewHolder.pollChart.setUsePercentValues(true);
        viewHolder.pollChart.getDescription().setEnabled(false);
        viewHolder.pollChart.setRotationEnabled(true);
        viewHolder.pollChart.setHighlightPerTapEnabled(true);
        viewHolder.title.setText(s.getTitle());
        if(!s.getAns().equals("-1")) {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle_black_18dp,0);
        }
        else {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_lock_black_18dp,0);
        }
        return v;
    }

    public void setPieChart(Option[] o)
    {
        pieEntries=new ArrayList<>();
        switch (o.length)
        {
            case 1:
                o[0].setName(o[0].getName().length()>8 ? o[0].getName().substring(0,8) : o[0].getName());
                pieEntries.add(new PieEntry(o[0].getCount(), o[0].getName()));
                break;
            case 2:
                o[0].setName(o[0].getName().length()>8 ? o[0].getName().substring(0,8) : o[0].getName());
                o[1].setName(o[1].getName().length()>8 ? o[1].getName().substring(0,8) : o[1].getName());
                pieEntries.add(new PieEntry(o[0].getCount(), o[0].getName()));
                pieEntries.add(new PieEntry(o[1].getCount(), o[1].getName()));
                break;
            case 3:
                o[0].setName(o[0].getName().length()>8 ? o[0].getName().substring(0,8) : o[0].getName());
                o[1].setName(o[1].getName().length()>8 ? o[1].getName().substring(0,8) : o[1].getName());
                o[2].setName(o[2].getName().length()>8 ? o[2].getName().substring(0,8) : o[2].getName());
                pieEntries.add(new PieEntry(o[0].getCount(), o[0].getName()));
                pieEntries.add(new PieEntry(o[1].getCount(), o[1].getName()));
                pieEntries.add(new PieEntry(o[2].getCount(), o[2].getName()));
                break;
            case 4:
                o[0].setName(o[0].getName().length()>8 ? o[0].getName().substring(0,8) : o[0].getName());
                o[1].setName(o[1].getName().length()>8 ? o[1].getName().substring(0,8) : o[1].getName());
                o[2].setName(o[2].getName().length()>8 ? o[2].getName().substring(0,8) : o[2].getName());
                o[3].setName(o[3].getName().length()>8 ? o[3].getName().substring(0,8) : o[3].getName());
                pieEntries.add(new PieEntry(o[0].getCount(), o[0].getName()));
                pieEntries.add(new PieEntry(o[1].getCount(), o[1].getName()));
                pieEntries.add(new PieEntry(o[2].getCount(), o[2].getName()));
                pieEntries.add(new PieEntry(o[3].getCount(), o[3].getName()));
                break;
            case 5:
                o[0].setName(o[0].getName().length()>8 ? o[0].getName().substring(0,8) : o[0].getName());
                o[1].setName(o[1].getName().length()>8 ? o[1].getName().substring(0,8) : o[1].getName());
                o[2].setName(o[2].getName().length()>8 ? o[2].getName().substring(0,8) : o[2].getName());
                o[3].setName(o[3].getName().length()>8 ? o[3].getName().substring(0,8) : o[3].getName());
                o[4].setName(o[4].getName().length()>8 ? o[4].getName().substring(0,8) : o[4].getName());
                pieEntries.add(new PieEntry(o[0].getCount(), o[0].getName()));
                pieEntries.add(new PieEntry(o[1].getCount(), o[1].getName()));
                pieEntries.add(new PieEntry(o[2].getCount(), o[2].getName()));
                pieEntries.add(new PieEntry(o[3].getCount(), o[3].getName()));
                pieEntries.add(new PieEntry(o[4].getCount(), o[4].getName()));
                break;
            case 6:
                o[0].setName(o[0].getName().length()>8 ? o[0].getName().substring(0,8) : o[0].getName());
                o[1].setName(o[1].getName().length()>8 ? o[1].getName().substring(0,8) : o[1].getName());
                o[2].setName(o[2].getName().length()>8 ? o[2].getName().substring(0,8) : o[2].getName());
                o[3].setName(o[3].getName().length()>8 ? o[3].getName().substring(0,8) : o[3].getName());
                o[4].setName(o[4].getName().length()>8 ? o[4].getName().substring(0,8) : o[4].getName());
                o[5].setName(o[5].getName().length()>8 ? o[5].getName().substring(0,8) : o[5].getName());
                pieEntries.add(new PieEntry(o[0].getCount(), o[0].getName()));
                pieEntries.add(new PieEntry(o[1].getCount(), o[1].getName()));
                pieEntries.add(new PieEntry(o[2].getCount(), o[2].getName()));
                pieEntries.add(new PieEntry(o[3].getCount(), o[3].getName()));
                pieEntries.add(new PieEntry(o[4].getCount(), o[4].getName()));
                pieEntries.add(new PieEntry(o[5].getCount(), o[5].getName()));
                break;
        }
        PieDataSet set =new PieDataSet(pieEntries,"");
        set.setColors(colors);
        pieData=new PieData(set);
    }
    static ArrayList<Integer> colors = new ArrayList<>();

    private static ArrayList<Integer> getColors(){

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        return colors;
    }

    static class ViewHolder{
        ImageButton refresh, reply,forward, about;
        TextView title;
        PieChart pollChart;
    }
}
