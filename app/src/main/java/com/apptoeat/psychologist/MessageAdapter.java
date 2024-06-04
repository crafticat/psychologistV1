package com.apptoeat.psychologist;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private TextToSpeech tts;


    public MessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
        tts = new TextToSpeech(WelcomeActivity.getInstance(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US); // Or any other language
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle error
                    }
                } else {
                    // Initialization failed
                }
            }
        });
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        switch (message.getType()) {
            case ChatMessage.TYPE_USER:
            case ChatMessage.TYPE_TARGET:
                // Set target message text
                // Set user message text
                holder.messageTextView.setText(message.getMessage());
                break;
            case ChatMessage.TYPE_TARGET_TYPING:
                // Set typing indicator text
                holder.messageTextView.setText("...");
                break;
        }
    }

    public void showTypingIndicator() {
        // Add a typing indicator message to the list
        messages.add(new ChatMessage("", ChatMessage.TYPE_TARGET_TYPING));
        notifyItemInserted(messages.size() - 1);
    }

    public void hideTypingIndicator() {
        // Remove the typing indicator message from the list
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getType() == ChatMessage.TYPE_TARGET_TYPING) {
                messages.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }



    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.getType();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatMessage.TYPE_USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_message_item, parent, false);
        }
        return new MessageViewHolder(view, tts); // Pass tts here
    }

    // Ensure you shut down TextToSpeech to release resources
    public void releaseTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    // Existing methods...

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        releaseTTS(); // Ensure TTS is released when the adapter is garbage collected
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageButton playMessageButton;

        public MessageViewHolder(View itemView, TextToSpeech tts) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            playMessageButton = itemView.findViewById(R.id.playMessageButton);

            playMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textToSpeak = messageTextView.getText().toString();
                    tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        }
    }

}
