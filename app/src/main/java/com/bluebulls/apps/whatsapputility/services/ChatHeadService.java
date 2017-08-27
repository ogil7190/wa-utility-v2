package com.bluebulls.apps.whatsapputility.services;


import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.bluebulls.apps.whatsapputility.activities.ChatActivity;
import com.bluebulls.apps.whatsapputility.activities.SearchActivity;
import com.bluebulls.apps.whatsapputility.activities.SsCallActivity;
import com.bluebulls.apps.whatsapputility.adapters.ChatAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatMessage;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatUser;
import com.bluebulls.apps.whatsapputility.entity.actors.Reminder;
import com.bluebulls.apps.whatsapputility.util.CustomBridge;
import com.bluebulls.apps.whatsapputility.util.CustomLayout;
import com.bluebulls.apps.whatsapputility.util.DBHelper;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.bluebulls.apps.whatsapputility.activities.Intro.PREF_USER_KEY_NAME;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER_KEY_PHONE;
import static com.bluebulls.apps.whatsapputility.fragments.FragmentReminder.PREF_REM_ID_KEY;
import static com.bluebulls.apps.whatsapputility.fragments.FragmentReminder.addToReminder;
import static com.bluebulls.apps.whatsapputility.fragments.FragmentReminder.getAlarmTime;
import static com.bluebulls.apps.whatsapputility.fragments.FragmentReminder.saveReminder;
import static com.bluebulls.apps.whatsapputility.fragments.FragmentSettings.PREF_USER_CHAT_ICON;
import static com.bluebulls.apps.whatsapputility.util.CustomBridge.STOP_SELF;

public class ChatHeadService extends Service implements CustomLayout.BackButtonListener, CustomLayout.HomeButtonListener {
    public static final String LogTag = "ChatHead";
    public static boolean isRunning = false;
    private long alarmTime = 0;
    public static final String REGISTER_POLL_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/RegisterPoll.php";
    public static final String REGISTER_EVENT_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/RegisterEvent.php";

    public WindowManager windowManager;
    private RelativeLayout chatHeadView, removeView;
    private ImageView chatHead;

    private CustomLayout options, chat;
    private LinearLayout txtView, txt_linearlayout, dateTimePicker;
    private TextView removeImg;
    private LinearLayout action_pol, action_eve, action_ss, action_rem,action_search, action_chat;
    private CircularProgressView chatheadImg;
    private TextView txt1;
    private Date xdate1,xdate2;
    int date2,year2,hour2,minute2;
    private String month2;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = false;
    private SharedPreferences pref;
    private CustomBridge bridge = new CustomBridge();
    private String user;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(getApplicationContext());
        pref = getSharedPreferences(PREF_USER,MODE_PRIVATE);
        registerReceiver(bridge, new IntentFilter(STOP_SELF));
        setPhoneNumber(pref.getString(PREF_USER_KEY_PHONE,"null"));
        isRunning = true;
    }

    private void handleStart() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        user = pref.getString(PREF_USER_KEY_NAME, "null");
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        removeView = (RelativeLayout) inflater.inflate(R.layout.remove, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
        removeImg = (TextView) removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);


        chatHeadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
        chatheadImg = (CircularProgressView) chatHeadView.findViewById(R.id.chathead_img);
        chatHead = (ImageView) chatHeadView.findViewById(R.id.chatHead);
        if(pref.getString(PREF_USER_CHAT_ICON, null) != null ) {
            chatHead.setImageURI(Uri.parse(pref.getString(PREF_USER_CHAT_ICON, "")));
        }
        chatheadImg.setVisibility(View.GONE);
        options = (CustomLayout) inflater.inflate(R.layout.new_options, null);

        WindowManager.LayoutParams paramOptions = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramOptions.gravity = Gravity.CENTER;
        options.setVisibility(View.GONE);

        options.setBackButtonListener(this);
        options.setHomeButtonListner(this);
        windowManager.addView(options, paramOptions);

        action_pol = (LinearLayout) options.findViewById(R.id.poll);
        action_eve = (LinearLayout) options.findViewById(R.id.event );
        action_rem = (LinearLayout) options.findViewById(R.id.reminder);
        action_ss = (LinearLayout) options.findViewById(R.id.screenshot);
        action_search=(LinearLayout)options.findViewById(R.id.search);
        action_chat = (LinearLayout) options.findViewById(R.id.Chat);
        poll = (CustomLayout) inflater.inflate(R.layout.activity_poll, null);
        poll.setBackButtonListener(this);
        poll.setHomeButtonListner(this);
        setupPoll();
        poll.setVisibility(View.GONE);

        event =(CustomLayout)inflater.inflate(R.layout.activity_event,null);
        event.setBackButtonListener(this);
        event.setHomeButtonListner(this);
        setupEvent();
        event.setVisibility(View.GONE);

        dateTimePicker = (LinearLayout) inflater.inflate(R.layout.date_time_picker, null);
        dateTimePicker.setVisibility(View.GONE);

        reminder=(CustomLayout)inflater.inflate(R.layout.activity_reminder,null);
        reminder.setBackButtonListener(this);
        reminder.setHomeButtonListner(this);
        setupReminder();
        reminder.setVisibility(View.GONE);

        chat = (CustomLayout) inflater.inflate(R.layout.activity_chat, null);
        chat.setBackButtonListener(this);
        chat.setVisibility(View.GONE);
        //setupChat();
        action_pol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poll.getVisibility() != View.VISIBLE) {
                    options.setVisibility(View.GONE);
                    poll.setVisibility(View.VISIBLE);
                    poll.requestFocusFromTouch();
                } else
                    poll.setVisibility(View.GONE);
            }
        });

        action_eve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getVisibility() != View.VISIBLE) {
                    options.setVisibility(View.GONE);
                    event.setVisibility(View.VISIBLE);
                    event.requestFocusFromTouch();
                } else
                    event.setVisibility(View.GONE);
            }
        });

        action_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminder.getVisibility() != View.VISIBLE) {
                    options.setVisibility(View.GONE);
                    reminder.setVisibility(View.VISIBLE);
                    reminder.requestFocusFromTouch();
                } else
                    reminder.setVisibility(View.GONE);
            }
        });
        action_ss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                options.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), SsCallActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));
            }
        });

        action_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK ));
            }
        });

        action_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (chat.getVisibility() != View.VISIBLE) {
                    options.setVisibility(View.GONE);
                    chat.setVisibility(View.VISIBLE);
                    chat.requestFocusFromTouch();
                } else
                    chat.setVisibility(View.GONE);
                connectSocket();*/
                startActivity(new Intent(getApplicationContext(), ChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK ));
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        setupChatHead();

        txtView = (LinearLayout) inflater.inflate(R.layout.txt, null);
        txt1 = (TextView) txtView.findViewById(R.id.txt1);
        txt_linearlayout = (LinearLayout) txtView.findViewById(R.id.txt_linearlayout);


        WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramsTxt.gravity = Gravity.TOP | Gravity.LEFT;

        txtView.setVisibility(View.GONE);
        windowManager.addView(txtView, paramsTxt);
        moveToRight(100);
    }


    private void setupChatHead() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 150;

        windowManager.addView(chatHeadView, params);

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;
            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {
                @Override
                public void run() {
                    isLongclick = true;
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatHeadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 350);

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        if (txtView != null) {
                            txtView.setVisibility(View.GONE);
                            myHandler.removeCallbacks(myRunnable);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        options.setVisibility(View.GONE);
                        optionsVisible = false;
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {
                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

                                if (removeImg.getLayoutParams().height == remove_img_height) {
                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - chatHeadView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - chatHeadView.getHeight())) / 2;

                                windowManager.updateViewLayout(chatHeadView, layoutParams);
                                break;
                            } else {
                                inBounded = false;
                                removeImg.getLayoutParams().height = remove_img_height;
                                removeImg.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                if (removeView.getWindowToken() != null) {
                                    windowManager.updateViewLayout(removeView, param_remove);
                                }
                            }
                        }

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;
                        if (chatHeadView.getWindowToken() != null)
                        windowManager.updateViewLayout(chatHeadView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongclick = false;
                        removeView.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {
                            stopService(new Intent(ChatHeadService.this, ChatHeadService.class));
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {
                                chathead_click();
                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (chatHeadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (chatHeadView.getHeight() + BarHeight);
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(x_cord);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setupEvent()
    {
        final WindowManager.LayoutParams paramOptions = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramOptions.gravity = Gravity.CENTER;


        final ImageButton pick_clock = (ImageButton) event.findViewById(R.id.pick_clock);
        pick_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setVisibility(View.GONE);
                dateTimePicker.setVisibility(View.VISIBLE);
                event.clearFocus();
                SingleDateAndTimePicker picker = (SingleDateAndTimePicker)dateTimePicker.findViewById(R.id.picker);
                Button timeSet=(Button)dateTimePicker.findViewById(R.id.setTime);
                final TextView datetxt=(TextView)event.findViewById(R.id.date);
                final TextView timetxt=(TextView)event.findViewById(R.id.time);
                timeSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(xdate1!=null) {
                            String test = xdate1.toString().replace("Mon", "").replace("Tue", "").replace("Wed", "").replace("Thu", "")
                                    .replace("Mon", "").replace("Fri", "").replace("Sat", "").replace("Sun", "")
                                    .replace("GMT+05:30", "");
                            hour2 = Integer.parseInt(String.valueOf(test.charAt(8)) + String.valueOf(test.charAt(9)));
                            minute2 = Integer.parseInt(String.valueOf(test.charAt(11)) + String.valueOf(test.charAt(12)));
                            date2 = Integer.parseInt(String.valueOf(test.charAt(5)) + String.valueOf(test.charAt(6)));
                            month2 = String.valueOf(test.charAt(1)) + String.valueOf(test.charAt(2)) + String.valueOf(test.charAt(3));
                            year2 = Integer.parseInt(String.valueOf(test.charAt(18)) + String.valueOf(test.charAt(19)) + String.valueOf(test.charAt(20)) + String.valueOf(test.charAt(21)));
                            datetxt.setText(date2 + " " + month2 + " " + year2);
                            if (hour2 < 10) {
                                if (minute2 < 10) {
                                    timetxt.setText("0" + hour2 + ":0" + minute2);
                                } else {
                                    timetxt.setText("0" + hour2 + ":" + minute2);
                                }
                            } else {
                                if (minute2 < 10) {
                                    timetxt.setText(hour2 + ":0" + minute2);
                                } else {
                                    timetxt.setText(hour2 + ":" + minute2);
                                }
                            }
                            datetxt.setVisibility(View.VISIBLE);
                            timetxt.setVisibility(View.VISIBLE);
                            dateTimePicker.setVisibility(View.GONE);
                            event.setVisibility(View.VISIBLE);
                            windowManager.removeViewImmediate(dateTimePicker);
                        }
                    }
                });
                picker.setListener(new SingleDateAndTimePicker.Listener() {
                    @Override
                    public void onDateChanged(String displayed, Date date) {
                        alarmTime = date.getTime();
                        if(alarmTime - System.currentTimeMillis() > 0) {
                            xdate1=date;
                        }
                        else
                            showToast("Select valid future date!");
                    }
                });

                windowManager.addView(dateTimePicker, paramOptions);
            }
        });

        Button closeEvent = (Button) event.findViewById(R.id.closeEvent);
        closeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.clearFocus();
                event.setVisibility(View.GONE);
            }
        });

        final Button submitEvent = (Button) event.findViewById(R.id.submitEvent);
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEvent())
                {
                    uploadEvent();
                }
            }
        });
        windowManager.addView(event, paramOptions);
    }
    private void setupReminder()
    {
        final WindowManager.LayoutParams paramOptions = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramOptions.gravity = Gravity.CENTER;

        final TextView datetxt=(TextView)reminder.findViewById(R.id.date);
        final TextView timetxt=(TextView)reminder.findViewById(R.id.time);

        final ImageButton pick_clock = (ImageButton) reminder.findViewById(R.id.pick_clock);
        pick_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reminder.setVisibility(View.GONE);
                dateTimePicker.setVisibility(View.VISIBLE);
                reminder.clearFocus();
                SingleDateAndTimePicker picker = (SingleDateAndTimePicker)dateTimePicker.findViewById(R.id.picker);
                Button timeSet=(Button)dateTimePicker.findViewById(R.id.setTime);

                timeSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (xdate2!=null) {
                            String test = xdate2.toString().replace("Mon", "").replace("Tue", "").replace("Wed", "").replace("Thu", "")
                                    .replace("Mon", "").replace("Fri", "").replace("Sat", "").replace("Sun", "")
                                    .replace("GMT+05:30", "");
                            hour2 = Integer.parseInt(String.valueOf(test.charAt(8)) + String.valueOf(test.charAt(9)));
                            minute2 = Integer.parseInt(String.valueOf(test.charAt(11)) + String.valueOf(test.charAt(12)));
                            date2 = Integer.parseInt(String.valueOf(test.charAt(5)) + String.valueOf(test.charAt(6)));
                            month2 = String.valueOf(test.charAt(1)) + String.valueOf(test.charAt(2)) + String.valueOf(test.charAt(3));
                            year2 = Integer.parseInt(String.valueOf(test.charAt(18)) + String.valueOf(test.charAt(19)) + String.valueOf(test.charAt(20)) + String.valueOf(test.charAt(21)));
                            datetxt.setText(date2 + " " + month2 + " " + year2);
                            if (hour2 < 10) {
                                if (minute2 < 10) {
                                    timetxt.setText("0" + hour2 + ":0" + minute2);
                                } else {
                                    timetxt.setText("0" + hour2 + ":" + minute2);
                                }
                            } else {
                                if (minute2 < 10) {
                                    timetxt.setText(hour2 + ":0" + minute2);
                                } else {
                                    timetxt.setText(hour2 + ":" + minute2);
                                }
                            }
                            datetxt.setVisibility(View.VISIBLE);
                            timetxt.setVisibility(View.VISIBLE);
                            dateTimePicker.setVisibility(View.GONE);
                            reminder.setVisibility(View.VISIBLE);
                            windowManager.removeViewImmediate(dateTimePicker);
                        }
                    }
                });

                picker.setListener(new SingleDateAndTimePicker.Listener() {
                    @Override
                    public void onDateChanged(String displayed, Date date) {
                        alarmTime = date.getTime();
                        if(alarmTime - System.currentTimeMillis() > 0) {
                            xdate2 = date;
                        }
                        else
                            showToast("Select valid future date!");
                    }
                });
                windowManager.addView(dateTimePicker,paramOptions);
            }
        });

        Button closeReminder = (Button) reminder.findViewById(R.id.closeReminder);
        closeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder.clearFocus();
                reminder.setVisibility(View.GONE);
            }
        });

        Button submitReminder = (Button) reminder.findViewById(R.id.submitReminder);
        submitReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (validateReminder()) {
                int rem_id = pref.getInt(PREF_REM_ID_KEY, -1) +1;
                date_time = datetxt.getText().toString() + "|" + timetxt.getText().toString();
                String eve = ((EditText)reminder.findViewById(R.id.reminderTopic)).getText().toString();
                String des = ((EditText)reminder.findViewById(R.id.reminderDescription)).getText().toString();
                saveReminder(pref, new Reminder(rem_id, eve, des, date_time));
                if(addToReminder(getApplicationContext(), rem_id, getAlarmTime(date_time)))
                    showToast("Alarm Set!");
                hideUIReminder();

            }
            }
        });
        windowManager.addView(reminder, paramOptions);
    }

    private ChatAdapter adapter;
    private ListView chatList;

    private void setupChat(){
        final WindowManager.LayoutParams paramOptions = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramOptions.gravity = Gravity.CENTER;
        options.setVisibility(View.GONE);
        chatList = (ListView) chat.findViewById(R.id.chat_list);
        adapter = new ChatAdapter(mssgs, getApplicationContext());
        chatList.setAdapter(adapter);
        final EditText mssg = (EditText)chat.findViewById(R.id.mssg);
        Button submit = (Button) chat.findViewById(R.id.sendbtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mssg.getText().toString();
                socket.emit("new_mssg", getMssg(message));
                mssg.setText("");
            }
        });
        windowManager.addView(chat, paramOptions);
    }

    private JSONObject getMssg(String message){
        JSONObject o = new JSONObject();
        try {
            o.put("name", user);
            o.put("mssg", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    private Socket socket;
    private void connectSocket(){
        try{
            socket = IO.socket("http://192.168.0.8:8080");
            socket.connect();
            socket.emit("data", getPlayerData());
            handleSocketEvents();
        }
        catch (Exception e) {
                System.out.println("Error:" + e);
        }
    }


    private ArrayList<ChatUser> users = new ArrayList<>();
    private ArrayList<ChatMessage> mssgs = new ArrayList<>();

    private void handleSocketEvents(){
        socket.on("data_join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                try {
                    String socket_id = object.getString("id");
                    String room = object.getString("room");
                    JSONArray a = new JSONArray(object.getString("users"));
                    for(int i=0; i<a.length(); i++){
                        users.add(getUsers(a.getJSONObject(i)));
                    }
                    JSONArray m = new JSONArray(object.getString("mssgs"));

                    for(int i=0; i<m.length(); i++){
                        mssgs.add(getMessage(m.getJSONObject(i)));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("new_user_join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                try {
                    users.add(getUsers(object));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("new_mssg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    mssgs.add(getMessage(new JSONObject((String)args[0])));
                    if(mssgs.size()>50){
                        mssgs.remove(0);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("user_disconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                try {
                    String socket_id = object.getString("id");
                    for(ChatUser user : users){
                        if(user.getSocket_id().equals(socket_id)){
                            users.remove(user);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ChatUser getUsers(JSONObject object) throws  JSONException{
        Log.d(LogTag, "USERS:"+object.toString());
        String id = object.getString("id");
        JSONObject data = object.getJSONObject("data");
        String name = data.getString("name");
        ChatUser user = new ChatUser(name, id);
        return user;
    }

    private ChatMessage getMessage(JSONObject object) throws JSONException{
        String name = String.valueOf(object.get("name"));
        String mssg = String.valueOf(object.get("mssg"));
        ChatMessage chatMessage = new ChatMessage(mssg, name, false);
        return chatMessage;
    }

    private JSONObject getPlayerData(){
        JSONObject o = new JSONObject();
        try {
            o.put("name", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    private CustomLayout poll;
    private int items = 2;

    private void setupPoll() {

        final WindowManager.LayoutParams paramOptions = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramOptions.gravity = Gravity.CENTER;
        options.setVisibility(View.GONE);

        StepperTouch options = (StepperTouch)poll.findViewById(R.id.option);
        options.enableSideTap(true);
        options.stepper.setMax(6);
        final int LENGTH = 6;
        options.stepper.setMin(2);
        options.stepper.setValue(2);
        options.stepper.addStepCallback(new OnStepCallback() {
            @Override
            public void onStep(int value, boolean positive) {
                Log.d(LogTag,"Value:"+value+" Bool:"+positive);
                items = value;
                RadioButton b = (RadioButton) poll.findViewById(R.id.radio1);
                b.setChecked(true);
                int count = value;
                for (int i = 1; i <= LENGTH; i++) {
                    RadioButton button = (RadioButton) poll.findViewById(getResources().getIdentifier("radio" + i, "id", getPackageName()));
                    EditText editText = (EditText) poll.findViewById(getResources().getIdentifier("option" + i, "id", getPackageName()));
                    button.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                }
                for (int i = 1; i <= count; i++) {
                    RadioButton button = (RadioButton) poll.findViewById(getResources().getIdentifier("radio" + i, "id", getPackageName()));
                    EditText editText = (EditText) poll.findViewById(getResources().getIdentifier("option" + i, "id", getPackageName()));
                    button.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);
                }
            }
        });

        Button close = (Button) poll.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poll.clearFocus();
                poll.setVisibility(View.GONE);
            }
        });

        Button submit = (Button) poll.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    uploadPoll();
                }
            }
        });
        windowManager.addView(poll, paramOptions);
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

    private CustomLayout reminder;

    public boolean validateEvent()
    {
        EditText eventTopic=(EditText)event.findViewById(R.id.event);
        EditText eventDescription=(EditText)event.findViewById(R.id.description);
        TextView date = (TextView)event.findViewById(R.id.date);
        TextView time = (TextView)event.findViewById(R.id.time);

        topic_msg = eventTopic.getText().toString().replaceFirst("\\s++$", "");
        description = eventDescription.getText().toString().replaceFirst("\\s++$", "");
        date_time = date.getText().toString().replaceFirst("\\s++$", "");
        date_time = date_time +"|"+ time.getText().toString().replaceFirst("\\s++$", "");

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

    private CustomLayout event;

    public boolean validateReminder()
    {
        EditText reminderTopic = (EditText)reminder.findViewById(R.id.reminderTopic);
        EditText reminderDescription = (EditText)reminder.findViewById(R.id.reminderDescription);
        TextView date = (TextView)reminder.findViewById(R.id.date);

        topic_msg = reminderTopic.getText().toString().replaceFirst("\\s++$", "");
        description = reminderDescription.getText().toString().replaceFirst("\\s++$", "");
        date_time = date.getText().toString().replaceFirst("\\s++$", "");

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

    private String topic_msg, description, date_time;
    private HashMap<String, String> options_map = new HashMap<>();
    private String ans = "";

    private boolean validateForm() {
        options_map.clear();
        RadioGroup radioGroup = (RadioGroup) poll.findViewById(R.id.radioGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        RadioButton button = (RadioButton) poll.findViewById(radioButtonID);
        String check = button.getResources().getResourceEntryName(radioButtonID);
        String res = check.replace("radio", "");

        EditText topic = (EditText) poll.findViewById(R.id.topic);

        String topicMssg = topic.getText().toString().replaceFirst("\\s++$", "");
        int n = Integer.parseInt(res);

        for (int i = 1; i <= items; i++) {
            EditText editText = (EditText) poll.findViewById(getResources().getIdentifier("option" + i, "id", getPackageName()));
            String data = editText.getText().toString().replaceFirst("\\s++$", "");
            if (!data.equals("")) {
                if (i == n) {
                    ans = data;
                    options_map.put(data, "1");
                }
                else
                    options_map.put(data,"0");
            } else {
                showToast("Check Option " + i);
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
            this.topic_msg = topicMssg;
            return true;
        }
    }
    private String options_str = "";
    private String getOptions(){
        JSONObject o = new JSONObject(options_map);
        options_str = o.toString();
        return options_str;
    }

    private void hideUIPoll() {
        options.setVisibility(View.GONE);
        poll.setVisibility(View.GONE);
    }

    private void hideUIEvent(){
        options.setVisibility(View.GONE);
        event.setVisibility(View.GONE);
    }

    private void hideUIReminder(){
        options.setVisibility(View.GONE);
        reminder.setVisibility(View.GONE);
    }

    private void uploadEvent() {
        showToast("Publishing Event");
        chatheadImg.setVisibility(View.VISIBLE);
        chatheadImg.startAnimation();
        hideUIEvent();

        final String KEY_PHONE = "phone";
        final String KEY_TOPIC = "topic";
        final String KEY_DESC = "description";
        final String KEY_DATE_TIME = "date_time";

        final String phone = this.phone;
        final String topic = this.topic_msg;
        final String description = this.description;
        final String date_time = this.date_time;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        chatheadImg.stopAnimation();
                        chatheadImg.setVisibility(View.GONE);
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
                        chatheadImg.stopAnimation();
                        chatheadImg.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    private void uploadPoll() {
        showToast("Publishing Poll");
        chatheadImg.setVisibility(View.VISIBLE);
        chatheadImg.startAnimation();
        hideUIPoll();

        final String KEY_PHONE = "phone";
        final String KEY_TOPIC = "topic";
        final String KEY_OPTIONS = "options";

        final String phone = this.phone;
        final String topic = this.topic_msg;
        final String options = options_str;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_POLL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        chatheadImg.stopAnimation();
                        chatheadImg.setVisibility(View.GONE);
                        try {
                            handleResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        chatheadImg.stopAnimation();
                        chatheadImg.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Check your network and try again!", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    private void handleEventResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        if (obj.get("error").equals(false)) {
            String event_id = obj.get("event_id").toString();
            saveEvent(event_id,phone,topic_msg, description);
            shareEvent(event_id);
        } else
            showToast("Something went wrong. Try again!");
    }

    private void handleResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        if (obj.get("error").equals(false)) {
            String poll_id = obj.get("poll_id").toString();
            String poll_reply = obj.getString("poll_reply");
            savePoll(poll_id,phone,topic_msg,options_str, poll_reply);
            sharePoll(poll_id);
        } else
            showToast("Something went wrong. Try again!");
    }

    private DBHelper dbHelper;

    private void saveEvent(String event_id, String user, String title, String description){
        dbHelper.insertEvent(event_id,title,description,"me",date_time,user,"yeah");
    }

    private void savePoll(String poll_id, String user, String title, String options, String reply){
        dbHelper.insertPoll(poll_id,title,user,options,ans,reply);
    }

    private void shareEvent(String event_id){
        String share = "Hurray! We have Got a new Event! .\n" + "Topic: " + topic_msg + "\n" + "https://wa.bluebulls/event_id/" + event_id + "\nJoin Now!\n" +
                "Download WhatsApp Utility for hot WhatsApp Features!";
        shareOnWhatsAppMessage(share);
    }

    private void sharePoll(String poll_id) {
        String share = "Participate in this poll on WhatsApp Utility.\n" + "Topic: " + topic_msg + "\n" + "https://wa.bluebulls/poll_id/" + poll_id + "\n" +
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


    private String phone = "";

    public void setPhoneNumber(String number) {
        phone = number;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatHeadView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }

            if (layoutParams.y + (chatHeadView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                layoutParams.y = szWindow.y - (chatHeadView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(chatHeadView, layoutParams);
            }

            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(LogTag, "ChatHeadService.onConfigurationChanged -> portrait");

            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }

            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x);
            }

        }

    }

    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true;
            moveToLeft(x_cord_now);

        } else {
            isLeft = false;
            moveToRight(x_cord_now);
        }
    }

    private void moveToLeft(final int x_cord_now) {
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatHeadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = 0 - (int) (double) bounceValue(step, x);
                windowManager.updateViewLayout(chatHeadView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(chatHeadView, mParams);
            }
        }.start();
    }

    private void moveToRight(final int x_cord_now) {
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatHeadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - chatHeadView.getWidth();
                if(chatHeadView.getWindowToken()!=null)
                    windowManager.updateViewLayout(chatHeadView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - chatHeadView.getWidth();
                if(chatHeadView.getWindowToken()!=null)
                    windowManager.updateViewLayout(chatHeadView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        value = value / 20f;
        return value;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private boolean optionsVisible = false;

    private void chathead_click() {
        if (!optionsVisible) {
            options.setVisibility(View.VISIBLE);
            if (poll != null)
                poll.setVisibility(View.GONE);
            optionsVisible = true;
        } else {
            if (poll != null)
                poll.setVisibility(View.GONE);
            options.setVisibility(View.GONE);
            optionsVisible = false;
        }
    }

    private void chathead_longclick() {
        setRemoveView();
    }

    private void setRemoveView() {
        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;
        if(removeView.getWindowToken() != null) {
            windowManager.updateViewLayout(removeView, param_remove);
        }
    }

    private void showMsg(String sMsg) {
        if (txtView != null && chatHeadView != null) {
            Log.d(LogTag, "ChatHeadService.showMsg -> sMsg=" + sMsg);
            txt1.setText(sMsg);
            myHandler.removeCallbacks(myRunnable);

            WindowManager.LayoutParams param_chathead = (WindowManager.LayoutParams) chatHeadView.getLayoutParams();
            WindowManager.LayoutParams param_txt = (WindowManager.LayoutParams) txtView.getLayoutParams();

            txt_linearlayout.getLayoutParams().height = chatHeadView.getHeight();
            txt_linearlayout.getLayoutParams().width = szWindow.x / 2;

            if (isLeft) {
                param_txt.x = param_chathead.x + chatheadImg.getWidth();
                param_txt.y = param_chathead.y;

                txt_linearlayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                param_txt.x = param_chathead.x - szWindow.x / 2;
                param_txt.y = param_chathead.y;

                txt_linearlayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            }

            txtView.setVisibility(View.VISIBLE);
            windowManager.updateViewLayout(txtView, param_txt);

            myHandler.postDelayed(myRunnable, 1000);

        }

    }

    Handler myHandler = new Handler();
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        } else {
            return Service.START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    public void destroy() {
        try {
            if (txtView != null) {
                windowManager.removeView(txtView);
            }
            if (removeView != null) {
                windowManager.removeView(removeView);
            }
            if (poll != null) {
                poll.clearFocus();
                windowManager.removeView(poll);
            }

            if (options != null) {
                windowManager.removeView(options);
            }
            if (chatHeadView != null) {
                windowManager.removeView(chatHeadView);
            }
            if(chat !=null){
                chat.clearFocus();
                //socket.disconnect();
                windowManager.removeView(chat);
            }
            if(event !=null){
                event.clearFocus();
                windowManager.removeView(event);
            }
            if(reminder !=null){
                reminder.clearFocus();
                windowManager.removeView(reminder);
            }
        } catch (Exception e){
            Log.d(LogTag, "Exception Error:"+e.toString());
        }

        unregisterReceiver(bridge);
        isRunning = false;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBackButtonPressed() {
        if (poll != null) {
            poll.setVisibility(View.GONE);
            poll.clearFocus();
        }
        if (options != null) {
            options.setVisibility(View.GONE);
            optionsVisible = false;
        }
        if(event!=null){
            event.setVisibility(View.GONE);
            event.clearFocus();
        }
        if(reminder!=null){
            reminder.setVisibility(View.GONE);
            reminder.clearFocus();
        }
        if(chat!=null){
            chat.setVisibility(View.GONE);
            chat.clearFocus();
            //socket.disconnect();
        }
    }

    @Override
    public void onHomeButtonPressed() {
        if (poll != null) {
            poll.setVisibility(View.GONE);
            poll.clearFocus();
        }
        if (options != null) {
            options.setVisibility(View.GONE);
            optionsVisible = false;
        }
    }
}