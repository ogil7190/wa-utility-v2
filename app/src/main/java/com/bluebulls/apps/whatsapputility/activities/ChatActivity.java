package com.bluebulls.apps.whatsapputility.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.ChatAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatMessage;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatUser;
import com.bluebulls.apps.whatsapputility.util.ContextMenuLayout;
import com.bluebulls.apps.whatsapputility.util.ContextMenuManager;
import com.bluebulls.apps.whatsapputility.util.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

import static com.bluebulls.apps.whatsapputility.activities.Intro.PREF_USER_KEY_GENDER;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;

public class ChatActivity extends Activity {

    private ListView listview;
    private EditText msg;
    private ImageButton send;
    private String user;
    private SharedPreferences pref;
    private ChatAdapter adapter;
    public static final String PREF_USER_CHAT_NAME = "user_chat_name";
    private TextView chat_empty;
    private ImageButton closeChat;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.Theme_SearchFloat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pref = getSharedPreferences(PREF_USER, MODE_PRIVATE);
        user = pref.getString(PREF_USER_CHAT_NAME, "Chotu")+ "("+pref.getString(PREF_USER_KEY_GENDER,"0")+")";
        connectSocket();
        chat_empty =(TextView)findViewById(R.id.emptyChat);
        listview = (ListView)  findViewById(R.id.chat_list);
        closeChat = (ImageButton) findViewById(R.id.closeChat);

        closeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });

        listview.setDivider(null);
        listview.setDividerHeight(0);
        listview.setVerticalScrollBarEnabled(false);
        listview.setEmptyView(chat_empty);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mssgs.get(position).getType() == 2) {
                    ContextMenuManager.getInstance().toggleContextMenuFromView(view, position, new ContextMenuLayout.OnFeedContextMenuItemClickListener() {
                        @Override
                        public void onSendRequestClick(int feedItem) {
                            //sendRequest(users.get(position));
                        }

                        @Override
                        public void onCancelClick(int feedItem) {
                            ContextMenuManager.getInstance().hideContextMenu();
                        }
                    });
                }
            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                ContextMenuManager.getInstance().hideContextMenu();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        adapter = new ChatAdapter(mssgs, this);
        listview.setAdapter(adapter);
        msg = (EditText) findViewById(R.id.mssg);
        send = (ImageButton) findViewById(R.id.sendbtn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();
                if(message!=null && message.length()>0) {
                    socket.emit("new_mssg", getMssg(message));
                    mssgs.add(new ChatMessage(message, user, 1));
                    if (mssgs.size() > 50) {
                        mssgs.remove(0);
                    }
                    adapter.notifyDataSetChanged();
                    scrollToChatBottom();
                    msg.setText("");
                }
            }
        });
    }

    private void sendRequest(ChatUser user){
        Toast.makeText(getApplicationContext(), "Sending Request :"+user.getSocket_id(), Toast.LENGTH_SHORT).show();
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

    private io.socket.client.Socket socket;
    private void connectSocket(){
        try{
            socket = IO.socket("https://glochatv10.herokuapp.com/");
            //socket = IO.socket("http://192.168.0.8:8080");
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
                        if(mssgs.size()>50){
                            mssgs.remove(0);
                        }
                    }
                    mssgs.add(new ChatMessage("Chat Room: "+room.toUpperCase(), "", 3));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            scrollToChatBottom();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("new_user_join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                try {
                    ChatUser user = getUsers(object);
                    users.add(user);
                    mssgs.add(new ChatMessage(user.getName().toUpperCase()+" Connected", " ", 3));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            scrollToChatBottom();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("new_mssg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    mssgs.add(getMessage(new JSONObject(String.valueOf(args[0]))));
                    if(mssgs.size()>50){
                        mssgs.remove(0);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            scrollToChatBottom();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("chat_request", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                try {
                    String phoneNumber = object.getString("phone");
                    String name = object.getString("name");
                    showNotification("You have got a new Chat Request","Tap to see!",phoneNumber, name);
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
                            mssgs.add(new ChatMessage(user.getName().toUpperCase() +" DisConnected", " ", 3));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    scrollToChatBottom();
                                }
                            });
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private String LogTag = "TAG";

    private ChatUser getUsers(JSONObject object) throws  JSONException{
        Log.d(LogTag, "USERS:"+object.toString());
        String id = object.getString("id");
        JSONObject data = object.getJSONObject("data");
        String name = data.getString("name");
        ChatUser user = new ChatUser(name, id);
        return user;
    }

    private void scrollToChatBottom() {
        listview.post(new Runnable() {
            @Override
            public void run() {
                listview.setSelection(listview.getCount() - 1);
            }
        });
    }

    private ChatMessage getMessage(JSONObject object) throws JSONException{
        String name = String.valueOf(object.get("name"));
        String mssg = String.valueOf(object.get("mssg"));
        ChatMessage chatMessage = new ChatMessage(mssg, name, 2);
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
    private int noti_id = 0;
    private void showNotification(String title, String text, String phone, String name){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(text);
        Intent resultIntent = new Intent(this, PendingChatRequest.class);
        resultIntent.putExtra("phone",phone);
        resultIntent.putExtra("name", name);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        7190,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(++noti_id, mBuilder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket!=null){
            socket.disconnect();
        }
    }
}
