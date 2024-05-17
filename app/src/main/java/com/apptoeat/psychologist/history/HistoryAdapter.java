package com.apptoeat.psychologist.history;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apptoeat.psychologist.ChatMessage;
import com.apptoeat.psychologist.MainActivity;
import com.apptoeat.psychologist.R;
import com.apptoeat.psychologist.WelcomeActivity;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DataViewAdapter> {


    public HistoryAdapter() {
    }

    public List<ChatData> getHistory() {
        return WelcomeActivity.getHistory().datas;
    }

    @Override
    public void onBindViewHolder(DataViewAdapter holder, int position) {
        ChatData data = getHistory().get(position);
        holder.messageTextView.setText("Chat #" + (position + 1)); //todo custom names
        int p = position;
        holder.messageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WelcomeActivity.getInstance() != null) {
                    // Store the selected ChatData in your WelcomeActivity instance for later retrieval.
                    WelcomeActivity.getInstance().selected = getHistory().get(p);
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    public void removeItem(int position) {
        List<ChatData> history = getHistory();
        if (history != null && position < history.size()) {
            history.remove(position);
            notifyItemRemoved(position);
        }
        WelcomeActivity.getHistory().applyChanges();
    }

    @Override
    public DataViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        return new DataViewAdapter(view);
    }

    @Override
    public int getItemCount() {
        return getHistory().size();
    }

    public static class DataViewAdapter extends RecyclerView.ViewHolder {
        Button messageTextView;

        public DataViewAdapter(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
