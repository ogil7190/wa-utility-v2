package com.bluebulls.apps.whatsapputility.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.services.CustomNotificationListener;
import com.wooplr.spotlight.SpotlightView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import gun0912.tedbottompicker.TedBottomPicker;

import static android.content.Context.MODE_PRIVATE;
import static com.bluebulls.apps.whatsapputility.activities.ChatActivity.PREF_USER_CHAT_NAME;
import static com.bluebulls.apps.whatsapputility.activities.LoginActivity.PREF_USER;
import static com.bluebulls.apps.whatsapputility.util.CustomBridge.STOP_SELF;

/**
 * Created by dell on 8/20/2017.
 */

public class FragmentSettings extends Fragment {
    private static android.support.v4.app.FragmentManager manager;
    private SharedPreferences pref;
    private ImageView image;
    private Bitmap bitmap;
    public static final String PREF_USER_CHAT_ICON ="imageUri";
    private SwitchCompat forAll;
    private TedBottomPicker tedBottomPicker;
    public static FragmentSettings newInstance(android.support.v4.app.FragmentManager man) {
        Bundle args = new Bundle();
        FragmentSettings fragment = new FragmentSettings();
        fragment.setArguments(args);
        manager=man;
        return fragment;
    }

    public static final String PREF_USER_KEY_FOR_ALL = "for_all_bool";

    public static final String STOP_SELF_PACK = "package_stop";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences(PREF_USER, MODE_PRIVATE);
        View v=inflater.inflate(R.layout.settings_fragment,null);
        LinearLayout setImage=(LinearLayout)v.findViewById(R.id.setImage);
        TextView changeName=(TextView)v.findViewById(R.id.changeName);
        final TextView currentName = (TextView) v.findViewById(R.id.currentName);
        currentName.setText(pref.getString(PREF_USER_CHAT_NAME, "Chotu"));
        LinearLayout l=(LinearLayout)inflater.inflate(R.layout.new_name_dialog,null);
        LinearLayout l2=(LinearLayout)inflater.inflate(R.layout.custom_name_title,null);
        final EditText newName=(EditText)l.findViewById(R.id.newName);
        image=(ImageView)v.findViewById(R.id.icon);
        forAll = (SwitchCompat) v.findViewById(R.id.for_all);
        forAll.setChecked(pref.getBoolean(PREF_USER_KEY_FOR_ALL, false));
        forAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pref.edit().putBoolean(PREF_USER_KEY_FOR_ALL, isChecked).commit();
                getContext().stopService(new Intent(getContext(), CustomNotificationListener.class));
                getContext().startService(new Intent(getContext(), CustomNotificationListener.class));
            }
        });
        image.setImageURI(Uri.parse(pref.getString(PREF_USER_CHAT_ICON, "android.resource://com.bluebulls.apps.whatsapputility/drawable/icon")));
        SpotlightView spotlightView2 = new SpotlightView.Builder(getActivity())
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Reminder")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Click the button below to add a Reminder")
                .maskColor(Color.parseColor("#dc000000"))
                .target(currentName)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("addReminder") //UNIQUE ID
                .show();
        tedBottomPicker = new TedBottomPicker.Builder(getContext())
                .setPreviewMaxCount(500)
                .setTitle("Choose Icon")
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        bitmap=getCroppedBitmap(getBitmapFromUri(uri));
                        pref.edit().putString(PREF_USER_CHAT_ICON,saveChatHead(bitmap)).commit();
                        image.setImageBitmap(bitmap);
                    }
                })
                .create();
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tedBottomPicker.show(manager);
            }
        });
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
    public Bitmap getBitmapFromUri(Uri uri)
    {
        Bitmap getBitmap=null;

        try{
            InputStream input_stream;
            try {
                input_stream = getContext().getContentResolver().openInputStream(uri);
                getBitmap= BitmapFactory.decodeStream(input_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(getBitmap!=null) {
            getBitmap=Bitmap.createScaledBitmap(getBitmap,128,128,false);
        }
        return getBitmap;
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    private String saveChatHead(Bitmap bitmap)
    {
        File folder=new File(Environment.getExternalStorageDirectory()+File.separator+"System");
        String path="";
        boolean success=true;
        if(!folder.exists())
        {
            success=folder.mkdir();
        }
        if(success)
        {
            File img=new File(folder,"chat_head_img.png");
            try {
                FileOutputStream outputStream=new FileOutputStream(img);
                bitmap.compress(Bitmap.CompressFormat.PNG,40,outputStream);
                outputStream.flush();
                outputStream.close();
                path=Uri.parse(img.toString()).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }
}
