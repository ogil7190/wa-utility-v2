package com.bluebulls.apps.whatsapputility.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;

import static android.content.Context.MODE_PRIVATE;
import static com.bluebulls.apps.whatsapputility.activities.ChatActivity.PREF_USER_CHAT_NAME;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;

/**
 * Created by dell on 8/20/2017.
 */

public class FragmentSettings extends Fragment {
    private SharedPreferences pref;
    public static FragmentSettings newInstance() {
        Bundle args = new Bundle();
        FragmentSettings fragment = new FragmentSettings();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences(PREF_USER, MODE_PRIVATE);
        View v=inflater.inflate(R.layout.settings_fragment,null);
        TextView changeName=(TextView)v.findViewById(R.id.changeName);
        final TextView currentName = (TextView) v.findViewById(R.id.currentName);
        currentName.setText(pref.getString(PREF_USER_CHAT_NAME, "Chotu"));
        LinearLayout l=(LinearLayout)inflater.inflate(R.layout.new_name_dialog,null);
        LinearLayout l2=(LinearLayout)inflater.inflate(R.layout.custom_name_title,null);
        final EditText newName=(EditText)l.findViewById(R.id.newName);
        final AlertDialog alertDialog=new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setCustomTitle(l2)
                .setView(l)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(newName.getText().toString()==null){
                            Toast.makeText(getContext(), "Choose Valid Name!", Toast.LENGTH_SHORT).show();
                        }
                        else if(newName.getText().toString().length()<3){
                            Toast.makeText(getContext(), "Choose Valid Name!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dialog.dismiss();
                            pref.edit().putString(PREF_USER_CHAT_NAME, newName.getText().toString()).commit();
                            currentName.setText(pref.getString(PREF_USER_CHAT_NAME, "Chotu"));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        newName.setText("");
                    }
                }).create();
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        return v;
    }
}
