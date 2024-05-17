package com.apptoeat.psychologist;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_TARGET = 1;
    public static final int TYPE_TARGET_TYPING = 2; // New type for typing indicator


    private String message;
    private int type;

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
