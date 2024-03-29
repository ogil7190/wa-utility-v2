package com.bluebulls.apps.whatsapputility.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bluebulls.apps.whatsapputility.adapters.AboutPollAdapter;
import com.bluebulls.apps.whatsapputility.adapters.PollAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.FadingTextViewAnimator;
import com.bluebulls.apps.whatsapputility.entity.actors.Option;
import com.bluebulls.apps.whatsapputility.entity.actors.Poll;
import com.bluebulls.apps.whatsapputility.util.DBHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.ZipperEffect;
import com.varunest.sparkbutton.SparkButton;
import com.wooplr.spotlight.SpotlightView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER_KEY_PHONE;
import static com.bluebulls.apps.whatsapputility.services.ChatHeadService.LogTag;
import static com.bluebulls.apps.whatsapputility.services.ChatHeadService.REGISTER_POLL_URL;

/**
 * Created by dell on 7/29/2017.
 */

public class FragmentPoll extends Fragment implements OnStepCallback{
    private static ViewPager viewPager;
    private JazzyListView listView;
    private SharedPreferences pref;

    private TextView ref_tag,emptyText;
    private AlertDialog alertDialog;

    private PollAdapter pollAdapter;
    private EditText option1,option2,option3,option4,option5,option6;


    private SparkButton addPole;
    private CircularProgressView chatHeadImg;
    private EditText title;
    private String pollTitle;
    private ArrayList<Poll> pollArrayList =new ArrayList<>();
    private static int number;

    public final int LENGTH = 6;
    private StepperTouch option;
    private static boolean isReply = false;
    private static String poll_id = "";
    private static int position;
    private LinearLayout l;
    private DialogInterface.OnClickListener submitClickListner;

    public static final  String TAG = "POLL FRAGMENT";

    public static String poll_id_refresh = "";


    public static FragmentPoll newInstance(boolean reply, String pollid, int pos, ViewPager pager) {
        Bundle args = new Bundle();
        FragmentPoll fragment = new FragmentPoll();
        fragment.setArguments(args);
        isReply = reply;
        poll_id = pollid;
        position=pos;
        viewPager=pager;
        if(pollid.equals("") || pollid.equals(null)){
            isReply = false; /* resetting on empty poll */
        }
        //poll_id = poll_id.replace("poll_id/","");
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new DBHelper(getContext());
        pref = getContext().getSharedPreferences(PREF_USER,MODE_PRIVATE);

        submitClickListner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(validateForm()){
                    alertDialog.hide();
                    pollTitle = title.getText().toString();
                    if(isReply && (!poll_id.equals("")))
                        submitPollReply();
                    else
                        uploadPoll();
                }
            }
        };

        final View v=inflater.inflate(R.layout.poll_fragment,container,false);
        l =(LinearLayout)inflater.inflate(R.layout.poll_dialog,null);
        LinearLayout l3=(LinearLayout) inflater.inflate(R.layout.custom_dialog_title1,null);
        option = (StepperTouch) l.findViewById(R.id.option);
        option.stepper.addStepCallback(this);
        option.enableSideTap(true);
        option.stepper.setMax(6);
        option.stepper.setMin(2);
        option.stepper.setValue(2);

        title=(EditText)l.findViewById(R.id.topic);
        option1=(EditText)l.findViewById(R.id.option1);
        option2=(EditText)l.findViewById(R.id.option2);
        option3=(EditText)l.findViewById(R.id.option3);
        option4=(EditText)l.findViewById(R.id.option4);
        option5=(EditText)l.findViewById(R.id.option5);
        option6=(EditText)l.findViewById(R.id.option6);
        emptyText=(TextView)v.findViewById(R.id.emptyPollText);
        listView =(JazzyListView) v.findViewById(R.id.list_view);
        listView.setTransitionEffect(new ZipperEffect());
        listView.setEmptyView(emptyText);
        ref_tag = (TextView) v.findViewById(R.id.refresh_tag);
        ref_tag.setVisibility(View.GONE);

        addPole=(SparkButton) v.findViewById(R.id.polebtn);
        addPole.setAnimationSpeed(1.5f);

        chatHeadImg = (CircularProgressView)v.findViewById(R.id.chathead_img_main);
        chatHeadImg.setVisibility(View.GONE);
        if(position==0) {
            SpotlightView spotlightView1 = new SpotlightView.Builder(getActivity())
                    .introAnimationDuration(400)
                    .enableRevealAnimation(true)
                    .performClick(true)
                    .fadeinTextDuration(400)
                    .headingTvColor(Color.parseColor("#eb273f"))
                    .headingTvSize(32)
                    .headingTvText("Poll")
                    .subHeadingTvColor(Color.parseColor("#ffffff"))
                    .subHeadingTvSize(16)
                    .subHeadingTvText("Click to add an Poll")
                    .maskColor(Color.parseColor("#dc000000"))
                    .target(addPole)
                    .lineAnimDuration(400)
                    .lineAndArcColor(Color.parseColor("#eb273f"))
                    .dismissOnTouch(true)
                    .dismissOnBackPress(true)
                    .enableDismissAfterShown(true)
                    .usageId("addPole") //UNIQUE ID
                    .show();
        }
        alertDialog=new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setView(l)
                .setCustomTitle(l3)
                .setPositiveButton("Submit",submitClickListner)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        title.setText("");
                        title.setText("");
                        option1.setText("");
                        option2.setText("");
                        option3.setText("");
                        option4.setText("");
                        option5.setText("");
                        option6.setText("");
                    }
                })
                .create();
        addPole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPole.playAnimation();
                if(!isReply)
                    enableEditText(title);
                alertDialog.show();
            }
        });

        refreshTopPolls();
        pollAdapter =new PollAdapter(pollArrayList,getContext(),this);
        listView.setAdapter(pollAdapter);
        alertDialog.hide();
        if(isReply) {
            setupReply();
        }
        return v;
    }

    private void setupReply() { /* setting reply on coming from WhatsApp */
        ProgressDialog d = new ProgressDialog(getContext());
        d.setMessage("Loading Poll...");
        d.setCanceledOnTouchOutside(false);
        if (!dbHelper.pollExists(poll_id)) {
            d.show();
            getPoll(d);
        } else if (isPollAnswered(poll_id)) {
            Toast.makeText(getContext(), "You have answered this poll already!", Toast.LENGTH_SHORT).show();
            isReply = false;
        } else {
            isReply = true;
            Poll poll = dbHelper.getPoll(poll_id);
            setOptions(poll.getOptions());
            title.setText(poll.getTitle());
            disableEditText(title);
            alertDialog.show();
        }
    }

    private boolean isPollAnswered(String poll_id){
        return !dbHelper.getPoll(poll_id).getAns().equals("-1");
    }

    private void setupReply(String poll_id){
        this.poll_id = poll_id;
        ProgressDialog d = new ProgressDialog(getContext());
        d.setMessage("Loading Poll...");
        d.setCanceledOnTouchOutside(false);
        if (!dbHelper.pollExists(poll_id)) {
            d.show();
            getPoll(d);
        } else if (isPollAnswered(poll_id)) {
            Toast.makeText(getContext(), "You have answered this poll already!", Toast.LENGTH_SHORT).show();
            isReply = false;
        } else {
            if (dbHelper.pollExists(poll_id)) {
                isReply = true;
                Poll poll = dbHelper.getPoll(poll_id);
                setOptions(poll.getOptions());
                title.setText(poll.getTitle());
                disableEditText(title);
                alertDialog.show();
            }
        }
    }

    public static final String GET_POLL_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/GetPoll.php";

    private void getPoll(final ProgressDialog d){
        final String KEY_POLL_ID = "poll_id";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POLL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Poll", "ResponsePoll:"+response+" PollID:"+poll_id);
                            handlePoll(response);
                            d.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                        }
                        d.dismiss();
                        Log.d(LogTag,"Network-Error:"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_POLL_ID, poll_id);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private String getAns(String reply) {
        String ans = "-1";
        String phone = pref.getString(PREF_USER_KEY_PHONE,"null");
        try {
            JSONObject o = new JSONObject(reply);
            for (int i = 0; i < o.names().length(); i++) {
                String key = o.names().getString(i);
                if(key.equals(phone)){
                    return o.getString(key);
                }
            }
            return ans;
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    private void handlePoll(String response) throws JSONException{
        JSONObject o = new JSONObject(response);
        if(o.get("error").equals(false)){
            poll_id = o.getString("poll_id");
            String opt = o.getString("options").replace("\\","");
            String topic = o.getString("topic");
            String reply = o.getString("poll_reply");
            String ans = getAns(reply);
            if(!dbHelper.pollExists(poll_id)){
                savePoll(poll_id,o.getString("user"), topic, opt, ans, reply);
                updatePolls();
            }

            if(!isPollAnswered(poll_id)) {
                title.setText(topic);
                disableEditText(title);
                Option[] op = retrieveOptions(opt);
                setOptions(op);
                alertDialog.show();
            }
            else
                Toast.makeText(getContext(),"You have answered this poll already!",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(),"Error getting poll\nTry again!",Toast.LENGTH_SHORT).show();
    }

    private String getAnsForReply(){
        options.clear();
        options.put(ans,"1");
        JSONObject o = new JSONObject(options);
        return o.toString();
    }


    public static final String POLL_REPLY_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/PollReply.php";

    private void submitPollReply(){
        final String KEY_POLL_ID = "poll_id";
        final String KEY_POLL_USER = "user";
        final String KEY_POLL_ANSWER = "ans";
        final String user = pref.getString(PREF_USER_KEY_PHONE,"null");
        ans = getAnsForReply();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POLL_REPLY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            handleReply(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                        }
                        Log.d(LogTag,"Network-Error:"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_POLL_ID, poll_id);
                params.put(KEY_POLL_USER, user);
                params.put(KEY_POLL_ANSWER, ans);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void handleReply(String response) throws JSONException {
        JSONObject o = new JSONObject(response);
        if(o.get("error").equals(false)) {
            dbHelper.updatePollAns(poll_id,ans);
            Toast.makeText(getContext(),"Poll Submitted!",Toast.LENGTH_SHORT).show();
            if(currentReply!=null)
                refreshCurrentPoll();
            reset();
            isReply = false;
        }

        if(o.get("error").equals(true)){
            if(o.get("error_type").equals(1)){
                dbHelper.updatePollAns(poll_id, o.getString("ans"));
                Toast.makeText(getContext(),"Poll Already Answered!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setOptions(Option[] opt) {
        while(true)
        if (isAdded()) {
            items = opt.length;
            RadioButton b = (RadioButton) l.findViewById(R.id.radio1);
            b.setChecked(true);
            option.stepper.setValue(items);
            option.stepper.setMin(items);
            option.stepper.setMax(items);
            number = items;

            for (int i = 1; i <=LENGTH; i++) {
                RadioButton button = (RadioButton) l.findViewById(getResources().getIdentifier("radio" + i, "id", getContext().getPackageName()));
                EditText editText = (EditText) l.findViewById(getResources().getIdentifier("option" + i, "id", getContext().getPackageName()));
                button.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
            }
            for (int i = 1; i <= number; i++) {
                RadioButton button = (RadioButton) l.findViewById(getResources().getIdentifier("radio" + i, "id", getContext().getPackageName()));
                EditText editText = (EditText) l.findViewById(getResources().getIdentifier("option" + i, "id", getContext().getPackageName()));
                button.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                editText.setText(opt[i-1].getName());
                disableEditText(editText);
            }
            break;
        }
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

    private int items = 0;

    private void showToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    private HashMap<String, String> options = new HashMap<>();
    private String ans = "";

    private boolean validateForm() {
        options.clear();
        RadioGroup radioGroup = (RadioGroup) l.findViewById(R.id.radioGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        RadioButton button = (RadioButton) l.findViewById(radioButtonID);
        String check = button.getResources().getResourceEntryName(radioButtonID);
        String res = check.replace("radio", "");

        EditText topic = (EditText) l.findViewById(R.id.topic);

        String topicMssg = topic.getText().toString().replaceFirst("\\s++$", "");
        int n = Integer.parseInt(res);

        for (int i = 1; i <= items; i++) {
            EditText editText = (EditText) l.findViewById(getResources().getIdentifier("option" + i, "id", getContext().getPackageName()));
            String data = editText.getText().toString().replaceFirst("\\s++$", "");
            if (!data.equals("")) {
                if (i == n) {
                    ans = data;
                    options.put(data,"1");
                }
                else {
                    options.put(data,"0");
                }
            } else {
                showToast("Check option:" + i);
                return false;
            }
        }

        if(items<1){
         showToast("Enter more than one option");
            return false;
        }

        if (topicMssg.equals("")) {
            showToast("Check your Topic!");
            return false;
        } else if (topicMssg.length() <= 5) {
            showToast("Enter valid topic");
            return false;
        } else {
            getOptions();
            return true;
        }

    }
    private String options_str = "";
    private String getOptions(){
        JSONObject o = new JSONObject(options);
        options_str = o.toString();
        return options_str;
    }

    private void sharePoll(String poll_id, String topic_msg) {
        String share = "Participate in this poll on WhatsAppUtility.\n" + "Topic: " + topic_msg + "\n" + "https://wa.bluebulls/poll_id/" + poll_id + "\n" +
                "Download WhatsApp Utility for hot WhatsApp Features!";
        shareOnWhatsAppMessage(share);
    }

    private void uploadPoll() {
        chatHeadImg.setVisibility(View.VISIBLE);
        chatHeadImg.startAnimation();
        final String KEY_PHONE = "phone";
        final String KEY_TOPIC = "topic";
        final String KEY_OPTIONS = "options";

        final String phone = pref.getString(PREF_USER_KEY_PHONE,"null");
        final String topic = pollTitle;
        final String options = options_str;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_POLL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            handleResponse(response);
                            chatHeadImg.stopAnimation();
                            chatHeadImg.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                        }
                        chatHeadImg.stopAnimation();
                        chatHeadImg.setVisibility(View.GONE);
                        Log.d(LogTag,"Network-Error:"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_PHONE, phone);
                params.put(KEY_OPTIONS, options);
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

    public void shareOnWhatsAppMessage(String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
    }
    private void handleResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        if (obj.get("error").equals(false)) {
            String poll_id = obj.get("poll_id").toString();
            pollTitle = title.getText().toString();
            savePoll(poll_id,pref.getString(PREF_USER_KEY_PHONE,"null"), pollTitle, options_str, ans, obj.getString("poll_reply"));
            sharePoll(poll_id, pollTitle);
        }
        else
            showToast("Something went wrong. Try again!");
    }

    private DBHelper dbHelper;
    private void savePoll(String poll_id, String user, String title, String options,String ans, String reply){
        dbHelper.insertPoll(poll_id,title,user,options,ans, reply);
    }

    int poll_count = 0;
    private void updatePolls(){
        pollArrayList.clear();
        polls = dbHelper.getAllPolls();
        for(Poll poll : polls)
        {
            pollArrayList.add(poll);
            poll_count++;
        }
        if(pollAdapter !=null)
            pollAdapter.notifyDataSetChanged();
    }

    public static Option[] retrieveOptions(String options){
        try {
            JSONObject o = new JSONObject(options);
            Option[] opt = new Option[o.names().length()];
            for(int i = 0; i<o.names().length(); i++){
                String key = o.names().getString(i);
                Option option = new Option();
                option.setName(key);
                option.setCount(Integer.valueOf(o.getString(key)));
                opt[i] = option;
            }
            return opt;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void reset(){
        option.stepper.setValue(2);
        option.stepper.setMax(6);
        option.stepper.setMin(2);

        title.setText("");
        option1.setText("");
        option2.setText("");
        option3.setText("");
        option4.setText("");
        option5.setText("");
        option6.setText("");
    }

    private View currentRefresh, currentReply;

    public View.OnClickListener setListeners(int x, final View ref){
        if(x == 1) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentRefresh = v;
                    pos = listView.getPositionForView(v);
                    RotateAnimation rotate = new RotateAnimation(0, -360f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(1000);
                    rotate.setRepeatCount(Animation.INFINITE);
                    rotate.setRepeatMode(Animation.INFINITE);
                    rotate.setInterpolator(new LinearInterpolator());
                    currentRefresh.startAnimation(rotate);
                    getPoll(pollArrayList.get(pos).getPoll_id(), currentRefresh);
                    Toast.makeText(getContext(), "Poll Refreshed", Toast.LENGTH_LONG).show();
                }
            };
            return listener;
        }
        if(x == 2){
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = listView.getPositionForView(v);
                    currentReply = v;
                    currentRefresh = ref;
                    if(pollArrayList.get(pos).getAns().equals("-1"))
                        setupReply(pollArrayList.get(pos).getPoll_id());
                    else
                        Toast.makeText(getContext(), "Poll already answered!", Toast.LENGTH_SHORT).show();
                }
            };
            return listener;
        }
        if(x == 3){
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog d = new ProgressDialog(getContext());
                    d.setMessage("Removing Poll!");
                    d.show();
                    pos = listView.getPositionForView(v);
                    deletePoll(pollArrayList.get(pos).getPoll_id());
                    d.dismiss();
                }
            };
            return listener;
        }

        if(x==4){
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = listView.getPositionForView(v);
                    sharePoll(pollArrayList.get(pos).getPoll_id(), pollArrayList.get(pos).getTitle());
                }
            };
            return listener;
        }

        if(x==5){
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = listView.getPositionForView(v);
                    showAboutPoll(pollArrayList.get(pos));
                }
            };
            return listener;
        }
        else
            return null;
    }

    private void showAboutPoll(Poll poll){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.poll_show, null);
        View title = inflater.inflate(R.layout.custom_title_poll_show, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setCancelable(true);
        dialogBuilder.setCustomTitle(title);
        TextView pollShowTitle = (TextView)view.findViewById(R.id.pollShowTitle);
        pollShowTitle.setText(poll.getTitle());
        dialogBuilder.setView(view);
        ListView listView = (ListView)view.findViewById(R.id.poll_about_listview);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        AboutPollAdapter aboutPollAdapter = new AboutPollAdapter(getParticipants(poll.getPoll_reply()),getContext());
        listView.setAdapter(aboutPollAdapter);
        dialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog d = dialogBuilder.create();
        d.show();
    }

    private ArrayList<String> getParticipants(String poll_reply){
        ArrayList<String> participants  = new ArrayList<>();
        try {
            JSONObject o = new JSONObject(poll_reply);
            for (int i = 0; i < o.names().length(); i++) {
                String key = o.names().getString(i);
                participants.add(key);
            }
        }

        catch (JSONException e){
            e.printStackTrace();
        }
        return participants;
    }

    private boolean deletePoll(String poll_id){
        dbHelper.deletePoll(poll_id);
        updatePolls();
        return true;
    }

    private int pos = 0;

    private void getPoll(final String poll_id, final View refresh){
        poll_id_refresh = poll_id;
        final String KEY_POLL_ID = "poll_id";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POLL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            handleRefreshPoll(response);
                            refresh.clearAnimation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                        }
                        Log.d(LogTag,"Network-Error:"+error);
                        refresh.clearAnimation();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_POLL_ID, poll_id);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private boolean handleRefreshPoll(String response) throws JSONException {
        JSONObject o = new JSONObject(response);
        if (o.get("error").equals(false)) {
            String opt = o.getString("options").replace("\\", "");
            String reply = o.getString("poll_reply");
            dbHelper.updatePollOptions(poll_id_refresh,opt);
            dbHelper.updatePollReply(poll_id_refresh, reply);
            updatePolls();
            listView.smoothScrollToPosition(pos);
            return true;
        }
        else {
            Toast.makeText(getContext(), "Try again next time!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void refreshCurrentPoll(){ /* refreshing poll after reply submission */
        currentRefresh.performClick();
    }

    private String current_poll_id = "";
    private ArrayList<Poll> polls;
    private int count = 0;
    private FadingTextViewAnimator animator;

    private void refreshTopPolls(){  /* Top 3 Polls Update */
        polls = dbHelper.getAllPolls();
        if(polls.size()>count) {
            if (count == 0) {
                animator = new FadingTextViewAnimator(ref_tag, new String[]{ "Refreshing Polls","Please Wait!" });
                ref_tag.setVisibility(View.VISIBLE);
                animator.startAnimation();

            } else if (count == LIMIT) {
                animator.stopAnimation();
                ref_tag.setVisibility(View.GONE);
                updatePolls();
            }
            if (count < LIMIT) {
                current_poll_id = polls.get(count).getPoll_id();
                getPoll(current_poll_id);
            }
        }
        else
            ref_tag.setVisibility(View.GONE);
    }

    private final int LIMIT = 4; /* poll update limit */

    private void getPoll(final String poll_id){
        final String KEY_POLL_ID = "poll_id";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_POLL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Activity activity = getActivity();
                            if(activity!=null && isAdded()) {
                                if (handlePollUpdate(response)) {
                                    count++;
                                    refreshTopPolls();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Something went wrong in refreshing polls!",Toast.LENGTH_SHORT).show();
                            count = LIMIT;
                            refreshTopPolls();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
                            count = LIMIT;
                            refreshTopPolls();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_POLL_ID, poll_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private boolean handlePollUpdate(String response) throws JSONException{
        JSONObject o = new JSONObject(response);
        if(o.get("error").equals(false)){
            String poll_id = o.getString("poll_id");
            String opt = o.getString("options").replace("\\","");
            String reply = o.getString("poll_reply");
            dbHelper.updatePollOptions(poll_id,opt);
            dbHelper.updatePollReply(poll_id, reply);
            return true;
        }
        else
            Toast.makeText(getContext(),"Error getting poll\nTry again!",Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onResume() {
        updatePolls();
        super.onResume();
    }

    @Override
    public void onStep(int value, boolean bool) {
        items = value;
        RadioButton b = (RadioButton) l.findViewById(R.id.radio1);
        b.setChecked(true);
        int count = value;
        for (int i = 1; i <= LENGTH; i++) {
            RadioButton button = (RadioButton) l.findViewById(getResources().getIdentifier("radio" + i, "id", getContext().getPackageName()));
            EditText editText = (EditText) l.findViewById(getResources().getIdentifier("option" + i, "id", getContext().getPackageName()));
            button.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
        }
        for (int i = 1; i <= count; i++) {
            RadioButton button = (RadioButton) l.findViewById(getResources().getIdentifier("radio" + i, "id", getContext().getPackageName()));
            EditText editText = (EditText) l.findViewById(getResources().getIdentifier("option" + i, "id", getContext().getPackageName()));
            button.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
        }
    }
}
