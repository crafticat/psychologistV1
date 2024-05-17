package com.apptoeat.psychologist.history;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptoeat.psychologist.MainActivity;
import com.apptoeat.psychologist.WelcomeActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class History {

    public List<ChatData> datas;
    private final String SHARED_PREFS_NAME = "chatDataPrefs6";
    private final String CHAT_DATAS_KEY = "chatDatas6";

    public History() {
        datas = getAllChatDatas(WelcomeActivity.getInstance());
    }

    public void add(ChatData data) {
        datas.add(data);
    }

    public void applyChanges() {
        Context context = WelcomeActivity.getInstance();
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(datas); // Convert the updated list back to JSON
        editor.putString(CHAT_DATAS_KEY, json);
        editor.apply();
    }

    public List<ChatData> getAllChatDatas(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CHAT_DATAS_KEY, null);
        if (json == null) {
            return new ArrayList<>(); // Return an empty list if nothing is found
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<ChatData>>() {}.getType();
        return gson.fromJson(json, type); // Convert the JSON string back to a List of ChatData objects
    }
}
