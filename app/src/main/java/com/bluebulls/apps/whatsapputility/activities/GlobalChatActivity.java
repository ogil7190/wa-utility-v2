package com.bluebulls.apps.whatsapputility.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.entity.actors.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.bluebulls.apps.whatsapputility.activities.Intro.PREF_USER_KEY_NAME;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;

public class GlobalChatActivity extends Activity {

    private Button send;
    private EditText mssg;
    private SharedPreferences pref;
    private ListView chatList;
    private FirebaseListAdapter adapter;
    private int count = 20;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SearchFloat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_chat);
        send = (Button) findViewById(R.id.sendbtn);
        mssg = (EditText) findViewById(R.id.mssg);
        pref = getSharedPreferences(PREF_USER, MODE_PRIVATE);
        chatList = (ListView) findViewById(R.id.chat_list);
        query =  FirebaseDatabase.getInstance().getReference().limitToLast(count);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.single_layout_chat_mssg, query) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView name = (TextView) v.findViewById(R.id.name);
                TextView mssg = (TextView) v.findViewById(R.id.mssg);
                name.setText(model.getMessageUser());
                mssg.setText(model.getMessageText());
            }
        };
        chatList.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference().push()
                        .setValue(new ChatMessage(mssg.getText().toString(), pref.getString(PREF_USER_KEY_NAME,"")));
                mssg.setText("");
                scrollMyListViewToBottom();
            }
        });
    }

    private void scrollMyListViewToBottom() {
        chatList.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                chatList.setSelection(chatList.getCount() - 1);
            }
        });
    }
}
