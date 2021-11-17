package com.example.mobileapk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.bson.types.ObjectId;

public class SessionManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;



    public SessionManager(Context context){
        this.context=context;
        preferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        editor = preferences.edit();

    }

    public void createLoginSession(String id, String login, String imie, String nazwisko, String avatar){
        editor.putBoolean("KEY_ISLOGIN",true);
        editor.putString("KEY_ID", id);
        editor.putString("KEY_LOGIN", login);
        editor.putString("KEY_IMIE",imie);
        editor.putString("KEY_NAZWISKO",nazwisko);
        editor.putString("KEY_AVATAR",avatar);
        editor.putBoolean("KEY_DARK", false);
        editor.commit();
    }



    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i =new Intent(context,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}