package com.bluebulls.apps.whatsapputility.util;
import android.content.SharedPreferences;

import com.bluebulls.apps.whatsapputility.ogil.Serializer;

import java.io.IOException;

/**
 * Created by ogil on 29/08/17.
 */

public class Inventory {
    private SharedPreferences preferences;

    public Inventory(SharedPreferences preferences){
        this.preferences = preferences;
    }

    public void putObject(String tag, Object list){
        try {
            String data = Serializer.serializeObjectToString(list);
            preferences.edit().putString(tag, data).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getObject(String tag){
        try {
            Object object = Serializer.deserializeObjectFromString(preferences.getString(tag, null));
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
