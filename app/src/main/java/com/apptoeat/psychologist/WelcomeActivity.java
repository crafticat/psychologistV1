package com.apptoeat.psychologist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptoeat.psychologist.history.ChatData;
import com.apptoeat.psychologist.history.History;
import com.apptoeat.psychologist.history.HistoryAdapter;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WelcomeActivity extends AppCompatActivity {
    @Getter private static History history;
    @Getter private static WelcomeActivity instance;
    @Setter
    public static ChatData selected;

    public static WelcomeActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        instance = this;
        history = new History();

        RecyclerView messageRecyclerView = findViewById(R.id.recView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        HistoryAdapter historyAdapter = new HistoryAdapter();
        messageRecyclerView.setAdapter(historyAdapter);

        Button createButton = findViewById(R.id.newChat);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.getInstance().selected = new ChatData();
                history.add(selected);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // Remove item from your data set
                        int position = viewHolder.getAdapterPosition(); // Get position of the swiped item
                        historyAdapter.removeItem(position); // Call the method in your adapter to remove the item
                    }
                }
        );

        itemTouchHelper.attachToRecyclerView(messageRecyclerView);
    }
}
