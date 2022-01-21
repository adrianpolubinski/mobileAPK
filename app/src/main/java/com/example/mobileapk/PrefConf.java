package com.example.mobileapk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import io.realm.mongodb.User;


public class PrefConf {

    public static void writeListPersons(Context context, ArrayList<UserObject> list){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("KEY_LIST_PERSONS", jsonString);
        editor.apply();
    }

    public static  ArrayList<UserObject> readListPersons(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("KEY_LIST_PERSONS","");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<UserObject>>() {}.getType();
        ArrayList<UserObject> list = gson.fromJson(jsonString, type);

        return list;
    }

    public static void writeListMessage(Context context, ArrayList<MessageObject> list, String key){
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, jsonString);
        editor.apply();
    }

    public static  ArrayList<MessageObject> readListMessage(Context context, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(key,"");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<MessageObject>>() {}.getType();
        ArrayList<MessageObject> list = gson.fromJson(jsonString, type);

        return list;
    }
}
