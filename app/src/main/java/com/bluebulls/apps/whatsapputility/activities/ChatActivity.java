package com.bluebulls.apps.whatsapputility.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.ChatAdapter;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatMessage;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

import static com.bluebulls.apps.whatsapputility.activities.Intro.PREF_USER_KEY_NAME;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;

public class ChatActivity extends Activity {

    private ListView listview;
    private EditText msg;
    private Button send;
    private String user;
    private SharedPreferences pref;
    private ChatAdapter adapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.Theme_SearchFloat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pref = getSharedPreferences(PREF_USER, MODE_PRIVATE);
        user = pref.getString(PREF_USER_KEY_NAME, "null");
        connectSocket();
        listview = (ListView)  findViewById(R.id.chat_list);
        listview.setDivider(null);
        listview.setDividerHeight(0);
        adapter = new ChatAdapter(mssgs, this);
        listview.setAdapter(adapter);
        msg = (EditText) findViewById(R.id.mssg);
        send = (Button) findViewById(R.id.sendbtn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();
                socket.emit("new_mssg", getMssg(message));
                mssgs.add(new ChatMessage(message, user, true));
                adapter.notifyDataSetChanged();
                scrollToChatBottom();
                msg.setText("");
            }
        });
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
                    mssgs.add(new ChatMessage(user.getName().toUpperCase(),"Connected", false));
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
        }).on("user_disconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                try {
                    String socket_id = object.getString("id");
                    for(ChatUser user : users){
                        if(user.getSocket_id().equals(socket_id)){
                            users.remove(user);
                            mssgs.add(new ChatMessage(user.getName().toUpperCase(), "Disconnected", false));
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

    @Override
    protected void onPause() {
        super.onPause();
        socket.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
