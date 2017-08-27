package com.bluebulls.apps.whatsapputility.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bluebulls.apps.whatsapputility.R;

public class PendingChatRequest extends AppCompatActivity {

    private TextView title;
    private Button accept, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_chat_request);
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
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
