package com.apptoeat.psychologist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apptoeat.psychologist.history.ChatData;
import com.apptoeat.psychologist.history.History;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class MainActivity extends AppCompatActivity {
    private EditText messageEditText;
    private MessageAdapter messageAdapter;
    private Psychologist psychologist;
    @Getter
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        psychologist = new Psychologist(WelcomeActivity.selected);

        History history = WelcomeActivity.getHistory();
        instance = this;

        history.applyChanges();

        System.out.println(history.datas.size());
        messageEditText = findViewById(R.id.messageEditText);
        RecyclerView messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageAdapter(psychologist.getData().getMessages());
        messageRecyclerView.setAdapter(messageAdapter);

        ImageButton sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                psychologist.getData().getMessages().add(new ChatMessage(message,0));
                messageAdapter.notifyItemInserted(psychologist.getData().getMessages().size() - 1);
                messageEditText.setText("");

                simulateResponse(message);
            }
        });

        ImageButton dia = findViewById(R.id.diagnose_start);
        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiagnoseMenu();
            }
        });

        ImageButton back = findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WelcomeActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void simulateResponse(String msg) {
        // Show typing indicator
        if (psychologist.ask(msg)) {
            addResponse("Slow down there! (Please let me finish my sentence)");
        } else
            messageAdapter.showTypingIndicator();
    }

    public void addResponse(String response) {
        messageAdapter.hideTypingIndicator();
        psychologist.getData().getMessages().add(new ChatMessage(response, ChatMessage.TYPE_TARGET));
        messageAdapter.notifyItemInserted(psychologist.getData().getMessages().size() - 1);
    }

    public void openDiagnoseMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.diagnose, null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.show();

        Button btnClose = dialogView.findViewById(R.id.close);
        TextView tx = dialogView.findViewById(R.id.diagnose_data);
        TextView tx2 = dialogView.findViewById(R.id.solution_data);
        psychologist.calculateSum(() -> {
            tx.setText(psychologist.getDiagnoses());
            tx2.setText(psychologist.getSolutions());
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
}
