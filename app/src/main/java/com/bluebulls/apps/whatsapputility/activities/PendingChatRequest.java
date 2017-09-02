package com.bluebulls.apps.whatsapputility.activities;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;

public class PendingChatRequest extends AppCompatActivity {

    private TextView title;
    private Button accept, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_chat_request);
        clearNotification();
        final Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        title = (TextView) findViewById(R.id.title);
        title.setText("You have got a new Chat Request from "+name);
        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Here you go!\n Mobile Number:"+bundle.getString("phone"));
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Contact", bundle.getString("phone"));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Contact Copied", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void clearNotification(){
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
