package com.bluebulls.apps.whatsapputility.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER_KEY_PHONE;

public class Intro extends AppCompatActivity {
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        pref = getSharedPreferences(PREF_USER,MODE_PRIVATE);
        requestPermissions();
    }

    @Override
    protected void onResume() {
        requestPermissions();
        super.onResume();
    }

    private String[] permission = new String[]{ Manifest.permission.RECEIVE_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS };
    private void requestPermissions(){
        Dexter.withActivity(this)
                .withPermissions(permission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(!report.areAllPermissionsGranted()){showDialog();
                        }
                        else if(!pref.getString(PREF_USER_KEY_PHONE,"").equals("")){
                            /* existing user */
                            startActivity(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        else {
                            /* new user */
                            startLogin();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void startLogin(){
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.login_dialog,null);
        View title = inflater.inflate(R.layout.custom_title_login_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setCustomTitle(title);
        dialogBuilder.setView(view);
        final CheckBox tnc = (CheckBox)view.findViewById(R.id.tnc);
        final EditText name = (EditText)view.findViewById(R.id.et_login_name);
        Button phoneVer = (Button) view.findViewById(R.id.btn_phone_ver);
        phoneVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tnc.isChecked()){
                    if(name.getText().length()>4){
                        saveUserName(name.getText().toString());
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Please enter valid name!",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Please check for terms and Conditions!",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog d = dialogBuilder.create();

        if(!pref.getString(PREF_USER_KEY_NAME,"").equals("")){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else
            d.show();
    }

    public static final String PREF_USER_KEY_NAME = "user_name";

    private void saveUserName(String name){
        pref.edit().putString(PREF_USER_KEY_NAME, name).commit();
    }

    private void showDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("This permission is very important for the proper functioning of app.\nPlease allow it!\nYou can also enable it by going to\nSettings ->Installed Apps ->App Permissions");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        requestPermissions();
                    }
                });
        builder1.setNeutralButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
