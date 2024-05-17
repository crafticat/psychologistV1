package com.apptoeat.psychologist.history;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptoeat.psychologist.ChatMessage;
import com.google.ai.client.generativeai.type.Content;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatData {

    public List<ContentData> c1Contents, c2Contents;
    private List<ChatMessage> messages;
    private String lastAns;
    public String name;

    public ChatData() {
        c1Contents = new ArrayList<>();
        c2Contents = new ArrayList<>();
        messages = new ArrayList<>();
        name = "New chat";
        lastAns = "";
    }

    public void loadC1(List<Content> list) {
        c1Contents = new ArrayList<>();
        for (var elem : list) {
            c1Contents.add(new ContentData(elem));
        }
    }

    public void loadC2(List<Content> list) {
        c2Contents = new ArrayList<>();
        for (var elem : list) {
            c2Contents.add(new ContentData(elem));
        }
    }

    public List<Content> getC1() {
        List<Content> c1 = new ArrayList<>();
        for (var elem : c1Contents) {
            c1.add(elem.getContent());
        }
        return c1;
    }
    public List<Content> getC2() {
        List<Content> c2 = new ArrayList<>();
        for (var elem : c2Contents) {
            c2.add(elem.getContent());
        }
        return c2;
    }
}
