package com.example.natterchatapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private final SharedPreferences sharedPreferences;

    public Preference(Context c) {
        this.sharedPreferences = c.getSharedPreferences(KEYS.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void puBoolean(String key, Boolean value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
