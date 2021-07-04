package com.example.whatsapp_android_clone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp_android_clone.PersonalChatActivity;
import com.example.whatsapp_android_clone.R;
import com.example.whatsapp_android_clone.model.ChatsFragmentModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.ChatsViewHolder> {

    List<ChatsFragmentModel> chatsModelList = new ArrayList<>();
    Context context;

    public ChatsFragmentAdapter(Context ctx, List<ChatsFragmentModel> model) {
        this.chatsModelList = model;
        this.context = ctx;
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
        try {
            Picasso.get().load(chatsModelList.get(position).getPhotoProfileUrl()).into(holder.photoProfile);
        } catch (Exception e){
            Picasso.get().load(R.drawable.friends).into(holder.photoProfile);
        }
        holder.username.setText(chatsModelList.get(position).getUsername());
        holder.lastMessage.setText(chatsModelList.get(position).getLastMesaage());
        holder.cardViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalChatActivity.class);
                intent.putExtra("idRoom", chatsModelList.get(position).getIdRoom());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatsModelList.size();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewChat;
        CircleImageView photoProfile;
        TextView username;
        TextView lastMessage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewChat = itemView.findViewById(R.id.cv_chat_profile);
            photoProfile = itemView.findViewById(R.id.civ_chat_image_profile);
            username = itemView.findViewById(R.id.tv_chat_username);
            lastMessage = itemView.findViewById(R.id.tv_chat_last_message);
        }
    }

}
