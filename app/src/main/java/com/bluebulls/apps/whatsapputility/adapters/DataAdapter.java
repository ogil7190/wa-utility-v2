package com.bluebulls.apps.whatsapputility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.entity.actors.Data;
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

public class DataAdapter extends BaseAdapter {
    ArrayList<Data> dataArrayList;
    List<PieEntry> pieEntries;
    PieData pieData;
    Context c;
    FragmentPoll fragmentPoll;

    public int number;

    public DataAdapter(ArrayList<Data> dataArrayList, Context c, FragmentPoll fr) {
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
        final Data s=  dataArrayList.get(position);
        number = s.getNumber();
        ViewHolder viewHolder;
        if(convertView==null) {
            v = l.inflate(R.layout.single_list_poll, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.title= (TextView) v.findViewById(R.id.title);
            viewHolder.pollChart=(PieChart)v.findViewById(R.id.piechart);

            viewHolder.refresh = (ImageButton)v.findViewById(R.id.refresh_poll);
            viewHolder.reply = (ImageButton)v.findViewById(R.id.reply_poll);
            viewHolder.forward=(ImageButton)v.findViewById(R.id.forward_poll) ;

            //viewHolder.clear = (ImageButton)v.findViewById(R.id.clear_poll);

            setPieChart(number,s.getOptionData1(),s.getOptionData2(),s.getOptionData3(),s.getOptionData4(),s.getOptionData5(),s.getOptionData6());
            v.setTag(viewHolder);
            View.OnClickListener refreshListener = fragmentPoll.setListeners(1,null);
            View.OnClickListener replyListener = fragmentPoll.setListeners(2,viewHolder.refresh);
            View.OnClickListener forwardListener=fragmentPoll.setListeners(4,viewHolder.forward);

            //View.OnClickListener clearListener = fragmentPoll.setListeners(3,null);

            viewHolder.refresh.setOnClickListener(refreshListener);
            viewHolder.reply.setOnClickListener(replyListener);
            viewHolder.forward.setOnClickListener(forwardListener);

            //viewHolder.clear.setOnClickListener(clearListener);
        }
        else {
            v=convertView;
            viewHolder=(ViewHolder)v.getTag();
            setPieChart(number,s.getOptionData1(),s.getOptionData2(),s.getOptionData3(),s.getOptionData4(),s.getOptionData5(),s.getOptionData6());
        }
        viewHolder.pollChart.setData(pieData);
        viewHolder.pollChart.invalidate();
        viewHolder.pollChart.setCenterText("in %age");
        viewHolder.pollChart.setUsePercentValues(true);
        viewHolder.pollChart.getDescription().setEnabled(false);
        viewHolder.pollChart.setRotationEnabled(true);
        viewHolder.pollChart.setHighlightPerTapEnabled(true);
        viewHolder.title.setText(s.getTitle());
        if(s.isAnswered()) {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle_black_18dp,0);
        }
        else {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_lock_black_18dp,0);
        }
        return v;

    }

    public void setPieChart(int number, Option one, Option two, Option three, Option four, Option five, Option six)
    {
        pieEntries=new ArrayList<>();

        /* trimming option name if too large */

        switch (number)
        {
            case 1:
                one.setName(one.getName().length()>8 ? one.getName().substring(0,8) : one.getName());
                pieEntries.add(new PieEntry(one.getCount(), one.getName()));
                break;
            case 2:
                one.setName(one.getName().length()>8 ? one.getName().substring(0,8) : one.getName());
                two.setName(two.getName().length()>8 ? two.getName().substring(0,8) : two.getName());
                pieEntries.add(new PieEntry(one.getCount(), one.getName()));
                pieEntries.add(new PieEntry(two.getCount(), two.getName()));
                break;
            case 3:
                one.setName(one.getName().length()>8 ? one.getName().substring(0,8) : one.getName());
                two.setName(two.getName().length()>8 ? two.getName().substring(0,8) : two.getName());
                three.setName(three.getName().length()>8 ? three.getName().substring(0,8) : three.getName());
                pieEntries.add(new PieEntry(one.getCount(), one.getName()));
                pieEntries.add(new PieEntry(two.getCount(), two.getName()));
                pieEntries.add(new PieEntry(three.getCount(), three.getName()));
                break;
            case 4:
                one.setName(one.getName().length()>8 ? one.getName().substring(0,8) : one.getName());
                two.setName(two.getName().length()>8 ? two.getName().substring(0,8) : two.getName());
                three.setName(three.getName().length()>8 ? three.getName().substring(0,8) : three.getName());
                four.setName(four.getName().length()>8 ? four.getName().substring(0,8) : four.getName());
                pieEntries.add(new PieEntry(one.getCount(), one.getName()));
                pieEntries.add(new PieEntry(two.getCount(), two.getName()));
                pieEntries.add(new PieEntry(three.getCount(), three.getName()));
                pieEntries.add(new PieEntry(four.getCount(), four.getName()));
                break;
            case 5:
                one.setName(one.getName().length()>8 ? one.getName().substring(0,8) : one.getName());
                two.setName(two.getName().length()>8 ? two.getName().substring(0,8) : two.getName());
                three.setName(three.getName().length()>8 ? three.getName().substring(0,8) : three.getName());
                four.setName(four.getName().length()>8 ? four.getName().substring(0,8) : four.getName());
                five.setName(five.getName().length()>8 ? five.getName().substring(0,8) : five.getName());
                pieEntries.add(new PieEntry(one.getCount(), one.getName()));
                pieEntries.add(new PieEntry(two.getCount(), two.getName()));
                pieEntries.add(new PieEntry(three.getCount(), three.getName()));
                pieEntries.add(new PieEntry(four.getCount(), four.getName()));
                pieEntries.add(new PieEntry(five.getCount(), five.getName()));
                break;
            case 6:
                one.setName(one.getName().length()>8 ? one.getName().substring(0,8) : one.getName());
                two.setName(two.getName().length()>8 ? two.getName().substring(0,8) : two.getName());
                three.setName(three.getName().length()>8 ? three.getName().substring(0,8) : three.getName());
                four.setName(four.getName().length()>8 ? four.getName().substring(0,8) : four.getName());
                five.setName(five.getName().length()>8 ? five.getName().substring(0,8) : five.getName());
                six.setName(six.getName().length()>8 ? six.getName().substring(0,8) : six.getName());
                pieEntries.add(new PieEntry(one.getCount(), one.getName()));
                pieEntries.add(new PieEntry(two.getCount(), two.getName()));
                pieEntries.add(new PieEntry(three.getCount(), three.getName()));
                pieEntries.add(new PieEntry(four.getCount(), four.getName()));
                pieEntries.add(new PieEntry(five.getCount(), five.getName()));
                pieEntries.add(new PieEntry(six.getCount(), six.getName()));
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
        ImageButton refresh, reply, clear,forward;
        TextView title;
        PieChart pollChart;
    }
}
