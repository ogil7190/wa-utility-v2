package com.bluebulls.apps.whatsapputility.fragments;

import android.content.DialogInterface;
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

import com.bluebulls.apps.whatsapputility.R;

/**
 * Created by dell on 8/20/2017.
 */

public class FragmentSettings extends Fragment {
    public static FragmentSettings newInstance() {

        Bundle args = new Bundle();

        FragmentSettings fragment = new FragmentSettings();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.settings_fragment,null);
        TextView changeName=(TextView)v.findViewById(R.id.changeName);
        LinearLayout l=(LinearLayout)inflater.inflate(R.layout.new_name_dialog,null);
        LinearLayout l2=(LinearLayout)inflater.inflate(R.layout.custom_name_title,null);
        final EditText newName=(EditText)l.findViewById(R.id.newName);
        final AlertDialog alertDialog=new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setCustomTitle(l2)
                .setView(l)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newName.setText("");
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
