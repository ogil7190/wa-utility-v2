package com.bluebulls.apps.whatsapputility.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.util.DBHelper;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bluebulls.apps.whatsapputility.activities.Intro.CONTACT_PUSH_URL;
import static com.bluebulls.apps.whatsapputility.activities.Intro.PREF_USER_KEY_COUNTRY;
import static com.bluebulls.apps.whatsapputility.activities.Intro.PREF_USER_KEY_NAME;
import static com.bluebulls.apps.whatsapputility.services.ChatHeadService.LogTag;
import static com.bluebulls.apps.whatsapputility.services.ChatHeadService.REGISTER_POLL_URL;

/**
 * Created by ogil on 28/07/17.
 */

public class LoginActivity extends AppCompatActivity {
    private AccessToken accessToken = AccountKit.getCurrentAccessToken();
    public static final String REGISTER_USER_URL = "http://syncx.16mb.com/android/whatsapp-utility/v1/Login.php";
    public static final String PREF_USER = "wa-user-data";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences(PREF_USER,MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dbHelper = new DBHelper(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
              saveContacts();
            }
        }).start();

        if (accessToken != null) {
            goToMyLoggedInActivity();
        } else {
            phoneLogin(getCurrentFocus());
        }
    }

    public static int APP_REQUEST_CODE = 99;

    public void phoneLogin(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";

            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                accessToken = loginResult.getAccessToken();
                getNumberAndSave();
            }
            Log.d("Login",toastMessage);
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    int count = 0;
    private void goToMyLoggedInActivity(){
        if(!isUserStored()){
            pushUserToServer();
        }
        else if(isNumberAvailable()) {
            startMainActivity();
        }
        else {
            count++;
            Toast.makeText(getApplicationContext(),"Unable to get your number!",Toast.LENGTH_SHORT).show();
            getNumberAndSave();
            if(count < 3)
                goToMyLoggedInActivity();
            else {
                pref.edit().putString(PREF_USER_KEY_PHONE,"").commit();
                finish();
            }
        }
    }
    public static final String PREF_USER_KEY_PHONE = "user_phone";

    private void savePhoneNumber(String phone){
        pref.edit().putString(PREF_USER_KEY_PHONE, phone).commit();
    }

    private boolean isUserStored(){
        return pref.getBoolean(PREF_USER_KEY_USER_STORED,false);
    }

    private boolean isNumberAvailable(){
        return !(pref.getString(PREF_USER_KEY_PHONE,"").equals(""));
    }

    private void getNumberAndSave() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                PhoneNumber phoneNumber = account.getPhoneNumber();
                String res = phoneNumber.toString();
                savePhoneNumber(res);
                goToMyLoggedInActivity();
            }
            @Override
            public void onError(final AccountKitError error) {
                Log.d(LogTag, error.toString());
            }
        });
    }

    private void saveContacts(){
        int i = 0, factor = 1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage("Getting ready! Please wait!\nIt can take few minutes please have patience!");
                dialog.show();
            }
        });
        String code = pref.getString(PREF_USER_KEY_COUNTRY, "");
        ArrayList<String[]> contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        i++;
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phoneNo.length() > 5)
                            contacts.add(new String[]{ name.toLowerCase(), samplePhoneNo(phoneNo, code) });
                        if(i/500 == factor){
                            factor++;
                            saveToJson(contacts, i);
                            contacts.clear();
                        }
                    }
                    pCur.close();
                }
            }
        }

        if(cur!=null) {
            cur.close();
            saveToJson(contacts, 0);
        }
    }

    private DBHelper dbHelper;
    private boolean contactDone = false, userDone = false;
    private void saveToJson(ArrayList<String[]> contacts, int par){
        String json = dbHelper.pushContacts(contacts, par);
        Log.d("App",json);
        if(json!=null)
            uploadContacts(json);
        if(par==0) {
            dialog.dismiss();
            contactDone = true;
            if(userDone)
                startMainActivity();
        }
    }

    private String samplePhoneNo(String phone, String code) {
        phone = phone.replaceFirst("^0+(?!$)", ""); /* removing leading zeros */
        phone =  phone.replaceAll("[^\\d+]", ""); /* removing non numeric characters */
        if(phone.contains("+")){
            return phone;
        }
        else
            phone = code + phone; /* adding code to numbers*/
        return phone;
    }

    private ProgressDialog dialog;

    private void pushUserToServer(){
        dialog.setMessage("Setting up for you!\nPlease wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String KEY_PHONE = "phone";
        final String KEY_NAME = "name";
        final String KEY_FB_TOKEN = "fb_token";

        final String phone = pref.getString(PREF_USER_KEY_PHONE, "NULL");
        final String name = pref.getString(PREF_USER_KEY_NAME, "NULL");
        final String fb_token = "NULL"; /* FireBase token will go here */

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            handleResponse(response);
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                        pref.edit().putBoolean(PREF_USER_KEY_USER_STORED,false).commit();
                        Log.d(LogTag,"Network-Error:"+error);
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_PHONE, phone);
                params.put(KEY_NAME, name);
                params.put(KEY_FB_TOKEN, fb_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    public static final String PREF_USER_KEY_CONTACTS_JSON = "contacts_json";

    private void handleResponse(String response) throws JSONException{
        Log.d("RES:",response);
        JSONObject obj = new JSONObject(response);
        if (obj.get("error").equals(false)) {
            pref.edit().putBoolean(PREF_USER_KEY_USER_STORED,true).commit();
        }
        else if(obj.get("error_type").equals(2)){
                pref.edit().putBoolean(PREF_USER_KEY_USER_STORED,true).commit();
            }
        else
            pref.edit().putBoolean(PREF_USER_KEY_USER_STORED,false).commit();
        userDone = true;
        if(contactDone)
            startMainActivity();
    }

    private void uploadContacts(final String json){
        dialog.show();
        final String KEY_DATA = "data";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONTACT_PUSH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LOGIN",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LogTag,"Network-Error:"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_DATA, json);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    public static final String PREF_USER_KEY_USER_STORED = "user_stored";
}
