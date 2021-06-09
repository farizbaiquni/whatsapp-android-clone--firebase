package com.example.whatsapp_android_clone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp_android_clone.R;
import com.example.whatsapp_android_clone.model.ChatsFragmentModel;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.ChatsViewHolder> {

    List<List<ChatsFragmentModel>> chatsModelList = new ArrayList<>();

    public ChatsFragmentAdapter(List<List<ChatsFragmentModel>> model) {
        this.chatsModelList = model;
    }

    @NonNull
    @Override
    public ChatsFragmentAdapter.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_profile, parent, false);

        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsFragmentAdapter.ChatsViewHolder holder, int position) {
        holder.username.setText(chatsModelList.get(position).get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return chatsModelList.size();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewChat;
        TextView username;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewChat = itemView.findViewById(R.id.cv_chat_profile);
            username = itemView.findViewById(R.id.tv_chat_username);
        }
    }

}
