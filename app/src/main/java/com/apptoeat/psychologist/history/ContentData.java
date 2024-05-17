package com.apptoeat.psychologist.history;

import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.TextPart;

public class ContentData {
    private String role, text;

    public ContentData(Content content) {
        text = ((TextPart) content.getParts().get(0)).getText();
        role = content.getRole();
    }

    public Content getContent() {
        var content = new Content.Builder().addText(text);
        content.setRole(role);
        return content.build();
    }
}
