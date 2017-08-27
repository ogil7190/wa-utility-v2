package com.bluebulls.apps.whatsapputility.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.EventAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.Event;
import com.bluebulls.apps.whatsapputility.services.MyAlarmReceiver;
import com.bluebulls.apps.whatsapputility.util.DBHelper;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.FanEffect;
import com.varunest.sparkbutton.SparkButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;
import static com.bluebulls.apps.whatsapputility.services.ChatHeadService.REGISTER_EVENT_URL;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

/**
 * Created by dell on 8/5/2017.
 */

public class FragmentEvent extends Fragment {
    private static int position;
    private JazzyListView listView2;
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private static AlertDialog alertDialog;
    static FragmentManager manager;
    private SparkButton addEvent;
    private int date1,year1;
    private String month1;
    private TextView datetxt,timetxt,emptyText;
    private static int hour1,minute1;
    private SharedPreferences pref;
    private EventAdapter eventAdapter;
    private SingleDateAndTimePickerDialog singleDateAndTimePickerDialog;
    private CircularProgressView chatHeadImg;
    private String topic_msg, description_str, date_time;
    private static boolean dataComing = false;
    private static String event_data = "";
    private String phone = "";
    private EditText event, description;
    public static final String PREF_USER_KEY_PHONE = "user_phone";
    private LayoutInflater inflater;

    private long alarmTime = 0;
    public static final String TAG = "Fragement_Event";
    public static final String GET_EVENT_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/GetEvent.php";
    public static final String EVENT_REPLY_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/EventReply.php";

    public static final String PREF_REM_EVENT_ID = "event_rem -";

    public static FragmentEvent newInstance(FragmentManager man, boolean incomingData, String data,int pos) {
        Bundle args = new Bundle();
        manager = man;
        FragmentEvent fragment = new FragmentEvent();
        fragment.setArguments(args);
        dataComing = incomingData;
        event_data = data;
        position=pos;
        if(event_data.equals("") || event_data.equals(null)){
            dataComing = false; /* resetting on empty data */
        }

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new DBHelper(getContext());
        this.inflater = inflater;

        View v = inflater.inflate(R.layout.event_fragment, null);
        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.event_dialog, null);
        LinearLayout l3 = (LinearLayout) inflater.inflate(R.layout.custom_dialog_title2, null);
        datetxt=(TextView)l.findViewById(R.id.date);
        timetxt=(TextView)l.findViewById(R.id.time);
        emptyText=(TextView)v.findViewById(R.id.emptyEventText);
        event = (EditText) l.findViewById(R.id.event);
        description = (EditText) l.findViewById(R.id.description);
        addEvent = (SparkButton) v.findViewById(R.id.eventbtn);
        addEvent.setAnimationSpeed(1.5f);
        listView2 = (JazzyListView) v.findViewById(R.id.list_view2);
        eventAdapter = new EventAdapter(eventArrayList, getContext(),this);
        listView2.setAdapter(eventAdapter);
        listView2.setTransitionEffect(new FanEffect());
        listView2.setEmptyView(emptyText);
        chatHeadImg = (CircularProgressView)v.findViewById(R.id.chathead_img_main);
        chatHeadImg.setVisibility(View.GONE);

        pref = getContext().getSharedPreferences(PREF_USER,MODE_PRIVATE);
        phone=pref.getString(PREF_USER_KEY_PHONE,"null");

        singleDateAndTimePickerDialog=new SingleDateAndTimePickerDialog.Builder(getContext())
                .curved()
                .minutesStep(1)
                .defaultDate(Calendar.getInstance().getTime())
                .titleTextColor(Color.WHITE)
                .mainColor(Color.rgb(255,140,0))
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {

                    }
                })
                .title("Pick Time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        alarmTime = date.getTime();
                        if(alarmTime - System.currentTimeMillis() > 0) {
                            String test = date.toString().replace("Mon", "").replace("Tue", "").replace("Wed", "").replace("Thu", "")
                                    .replace("Mon", "").replace("Fri", "").replace("Sat", "").replace("Sun", "")
                                    .replace("GMT+05:30", "");
                            hour1 = Integer.parseInt(String.valueOf(test.charAt(8)) + String.valueOf(test.charAt(9)));
                            minute1 = Integer.parseInt(String.valueOf(test.charAt(11)) + String.valueOf(test.charAt(12)));
                            date1 = Integer.parseInt(String.valueOf(test.charAt(5)) + String.valueOf(test.charAt(6)));
                            month1 = String.valueOf(test.charAt(1)) + String.valueOf(test.charAt(2)) + String.valueOf(test.charAt(3));
                            year1 = Integer.parseInt(String.valueOf(test.charAt(18)) + String.valueOf(test.charAt(19)) + String.valueOf(test.charAt(20)) + String.valueOf(test.charAt(21)));
                            datetxt.setText(date1 + " " + month1 + " " + year1);
                            if (hour1 < 10) {
                                if (minute1 < 10) {
                                    timetxt.setText("0" + hour1 + ":0" + minute1);
                                } else {
                                    timetxt.setText("0" + hour1 + ":" + minute1);
                                }
                            } else {
                                if (minute1 < 10) {
                                    timetxt.setText(hour1 + ":0" + minute1);
                                } else {
                                    timetxt.setText(hour1 + ":" + minute1);
                                }
                            }
                            Log.e("TAG", "onDateSelected: " + test);
                            alertDialog.show();
                        }

                        else
                            Toast.makeText(getContext(),"Must be a valid date time",Toast.LENGTH_SHORT).show();
                    }
                }).build();

        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setView(l)
                .setCustomTitle(l3)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        topic_msg = event.getText().toString();
                        description_str = description.getText().toString();
                        date_time= datetxt.getText().toString()+"|"+timetxt.getText().toString();
                        if(validateEvent())
                            uploadEvent();
                        event.setText(null);
                        description.setText(null);
                    }
                })
                .setNegativeButton("Set Date & Time", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleDateAndTimePickerDialog.display();
                    }
                })
                .create();
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent.playAnimation();
                alertDialog.show();
            }
        });
        events();
        if(dataComing){
            welcomeEvent();
        }
        return v;
    }

    public boolean validateEvent()
    {
        topic_msg = event.getText().toString().replaceFirst("\\s++$", "");
        description_str = description.getText().toString().replaceFirst("\\s++$", "");
        date_time = datetxt.getText().toString().replaceFirst("\\s++$", "");
        date_time = date_time +"|"+ timetxt.getText().toString().replaceFirst("\\s++$", "");

        if(topic_msg.equals("") || topic_msg.equals(null)){
            showToast("Check Topic!");
            return false;
        }
        else if(topic_msg.length()<5){
            showToast("Enter valid topic!");
            return false;
        }
        else if(description.equals("") || description.equals(null)){
            showToast("Check Description!");
            return false;
        }
        else if(description.length()<5){
            showToast("Enter Valid Description!");
        }
        else if(date_time.equals("") || date_time.equals(null)){
            showToast("Choose Time and date first!");
            return false;
        }
        else
            return true;

        return false;
    }

    private void events(){
        eventArrayList.clear();
        ArrayList<Event> events = dbHelper.getAllEvents();
        for(Event event : events){
            eventArrayList.add(event);
        }
        eventAdapter.notifyDataSetChanged();
    }

    private DBHelper dbHelper;
    private void welcomeEvent(){
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching event details!");
        dialog.setCanceledOnTouchOutside(false);
        if(!dbHelper.eventExists(event_data)){
            getEvent(dialog);
        }
    }

    private void getEvent(final ProgressDialog d){
        d.show();
        final String KEY_EVENT_ID = "event_id";
        final String event_id = event_data;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG,"RES:"+response);

                            handleEvent(response);
                            d.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            d.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                            d.dismiss();
                        }
                        Log.d(TAG,"Network-Error:"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_EVENT_ID, event_id);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void handleEvent(String response) throws JSONException{
        JSONObject o = new JSONObject(response);
        if(o.get("error").equals(false)) {
            String date_time = o.getString("date_time");
            String title = o.getString("topic");
            String description = o.getString("description");
            String user = o.getString("user");
            String joined = user.equals(pref.getString(PREF_USER_KEY_PHONE,"")) ? "joined" : "" ;
            String reply = o.getString("reply").replace("\\","");
            String participants = getParticipants(reply);
            Event event = new Event(event_data, title, description,participants,user,date_time);

            showJoinAlert(event);
        }
        events();
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText){
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void showJoinAlert(final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.event_dialog,null);
        EditText topic = (EditText)linearLayout.findViewById(R.id.event);
        EditText desc = (EditText) linearLayout.findViewById(R.id.description);
        TextView time = (TextView) linearLayout.findViewById(R.id.time);
        TextView date = (TextView) linearLayout.findViewById(R.id.date);

        date.setText(event.getTime().substring(0,event.getTime().indexOf("|")));
        time.setText(event.getTime().substring(event.getTime().indexOf("|") + 1,event.getTime().length()));

        topic.setText("Topic: "+ event.getTopic());
        desc.setText("Description: "+ event.getDescriptioin());
        disableEditText(topic);
        disableEditText(desc);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        builder.setCustomTitle(inflater.inflate(R.layout.cutom_title_event_join,null));
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateEvent(event);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateEvent(final Event event){

        final String KEY_EVENT_ID = "event_id";
        final String KEY_USER = "user";
        final String event_id = event_data;
        final String user = pref.getString(PREF_USER_KEY_PHONE,"");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EVENT_REPLY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG,"RES:"+response);
                            handleEventReply(response, event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null)
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Network-Error:"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_EVENT_ID, event_id);
                params.put(KEY_USER, user);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void handleEventReply(String response, Event event) throws JSONException{
        JSONObject o = new JSONObject(response);
        if(o.get("error").equals(false)) {
            saveEvent(event,"joined");
            events();
        }
    }

    private String getParticipants(String reply){
        try {
            JSONObject o = new JSONObject(reply);
            for (int i = 0; i < o.names().length(); i++) {
                String key = o.names().getString(i);
                Log.d(TAG,"Keys:"+key);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return "participants";
    }

    private int pos = 0;
    public View.OnClickListener getListener(int x){
        if(x==1){
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = listView2.getPositionForView(v);
                    SparkButton b = (SparkButton) v;
                    if(!b.isChecked())
                    {
                        b.setActiveImage(R.drawable.ic_alarm_on_black_18dp);
                        if(pref.getBoolean(PREF_REM_EVENT_ID + eventArrayList.get(pos).getEvent_id(), true)) {
                            boolean  alarm = addToReminder(eventArrayList.get(pos).getEvent_id(), getAlarmTime(eventArrayList.get(pos).getTime()));
                            pref.edit().putBoolean(PREF_REM_EVENT_ID + eventArrayList.get(pos).getEvent_id(), false).commit(); /* to set reminder is false now */
                            if(alarm)
                                Toast.makeText(getContext(), "Alarm Set!", Toast.LENGTH_SHORT).show();
                        }
                        b.setChecked(true);
                        b.playAnimation();
                    }
                    else {
                        b.setInactiveImage(R.drawable.ic_alarm_on_black_18dp);
                        b.setChecked(true);
                    }
                }
            };
            return listener;
        }

        else
            return null;
    }

    private long getAlarmTime(String date_time){
        String date = date_time.substring(0, date_time.indexOf("|"));
        String time = date_time.substring(date_time.indexOf("|") + 1, date_time.length());
        Calendar calendar = Calendar.getInstance();
        String dates[] = date.split(" ");
        String times[] = time.split(":");
        calendar.set(Calendar.DAY_OF_MONTH,Integer.valueOf(dates[0]));
        calendar.set(Calendar.MONTH, getMonth(dates[1]));
        calendar.set(Calendar.YEAR, Integer.valueOf(dates[2]));
        calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(times[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(times[1]));
        return calendar.getTime().getTime();
    }

    private int getMonth(String month){
        switch (month.toLowerCase()) {
            case "jan": return 0;
            case "feb": return 1;
            case "mar": return 2;
            case "apr": return 3;
            case "may": return 4;
            case "jun": return 5;
            case "jul": return 6;
            case "aug": return 7;
            case "sep": return 8;
            case "oct": return 9;
            case "nov": return 10;
            case "dec": return 11;
            default: return -1;
        }
    }

    private void showToast(final String mssg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mssg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadEvent() {
        showToast("Publishing Event");
        chatHeadImg.setVisibility(View.VISIBLE);
        chatHeadImg.startAnimation();

        final String KEY_PHONE = "phone";
        final String KEY_TOPIC = "topic";
        final String KEY_DESC = "description";
        final String KEY_DATE_TIME = "date_time";

        final String phone = this.phone;
        final String topic = this.topic_msg;
        final String description = this.description_str;
        final String date_time = this.date_time;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        chatHeadImg.stopAnimation();
                        chatHeadImg.setVisibility(View.GONE);
                        try {
                            handleEventResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        chatHeadImg.stopAnimation();
                        chatHeadImg.setVisibility(View.GONE);
                        if (getContext() != null) {
                            Toast.makeText(getApplicationContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_PHONE, phone);
                params.put(KEY_DATE_TIME, date_time);
                params.put(KEY_DESC, description);
                params.put(KEY_TOPIC, topic);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
    private void handleEventResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        if (obj.get("error").equals(false)) {
            String event_id = obj.get("event_id").toString();
            saveEvent(event_id,phone,topic_msg, description_str);
            events();
            shareEvent(event_id);
        } else
            showToast("Something went wrong. Try again!");
    }

    private boolean addToReminder(String event_id, long alarmTime){
        Intent i = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        i.setAction("NEW_EVENT_ALARM#"+event_id);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        long time = alarmTime - System.currentTimeMillis();

        if(time<0){
            Toast.makeText(getContext(), "Event Already gone!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
            return true;
        }
    }

    private void saveEvent(String event_id, String user, String title, String description){
        dbHelper.insertEvent(event_id,title,description,"me",date_time,user,"joined");
    }

    private void saveEvent(Event event, String joined){
        dbHelper.insertEvent(event, joined);
    }
    private void shareEvent(String event_id){
        String share = "Hurray! We have Got a new Event! .\n" + "Topic: " + topic_msg + "\n" + "https://wa.bluebulls/event_id/" + event_id + "\nJoin Now!\n" +
                "Download WhatsApp Utility for hot WhatsApp Features!";
        shareOnWhatsAppMessage(share);
    }
    public void shareOnWhatsAppMessage(String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
    }
}
