package com.example.whatsapp_android_clone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp_android_clone.R;
import com.example.whatsapp_android_clone.model.PersonalChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonalChatAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<PersonalChatModel> personalChatModelList;

    public PersonalChatAdapter(Context ctx, Map<Number, PersonalChatModel> model){
        List<PersonalChatModel> tempModel = new ArrayList<>(model.values());
        this.context = ctx;
        this.personalChatModelList = tempModel;
        //Log.d("SIZE", Integer.toString(personalChatModelList.size()));
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("INDEX POSITION", Integer.toString(position));
        Log.d("INDEX VALUE", personalChatModelList.get(position).getMessage());
        //Toast.makeText(context, Integer.toString(personalChatModelList.get(position).getTypeUser()), Toast.LENGTH_SHORT).show();
        switch (personalChatModelList.get(position).getTypeUser()){
            case 1:
                return PersonalChatModel.CHAT_CURRENT_USER;
            case 2:
                return PersonalChatModel.CHAT_OPPONENT_USER;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        switch (viewType){
            case PersonalChatModel.CHAT_CURRENT_USER:
                view = layoutInflater.inflate(R.layout.message_personal_current_user, parent, false);
                return new PersonalChatAdapter.CurrentUserViewHolder(view);

            case PersonalChatModel.CHAT_OPPONENT_USER:
                view = layoutInflater.inflate(R.layout.message_personal_other_user, parent, false);
                return new PersonalChatAdapter.OpponentUserViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Log.d("INDEX POSITION 2", Integer.toString(position));
        final PersonalChatModel object = personalChatModelList.get(position);
        if(object != null){
            switch (object.getTypeUser()){
                case PersonalChatModel.CHAT_CURRENT_USER:
                    CurrentUserViewHolder currentUserViewHolder = (CurrentUserViewHolder) holder;
                    currentUserViewHolder.textViewCurrentUserMessage.setText(object.getMessage());
                    break;
                case PersonalChatModel.CHAT_OPPONENT_USER:
                    OpponentUserViewHolder opponentUserViewHolder = (OpponentUserViewHolder) holder;
                    opponentUserViewHolder.textViewOpponentUserMessage.setText(object.getMessage());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return personalChatModelList.size();
    }

    // ========================= VIEW HOLDER =========================
    public static class CurrentUserViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewCurrentUser;
        TextView textViewCurrentUserMessage, textViewCurrentUserDate;

        public CurrentUserViewHolder(@NonNull View itemView){
            super(itemView);
            cardViewCurrentUser = itemView.findViewById(R.id.card_view_chat_current_user);
            textViewCurrentUserMessage = itemView.findViewById(R.id.text_view_chat_current_user_message);
            textViewCurrentUserDate = itemView.findViewById(R.id.text_view_chat_current_user_date);
        }
    }


    public static class OpponentUserViewHolder extends RecyclerView.ViewHolder{
        CardView cardViewOpponentUser;
        TextView textViewOpponentUserMessage, textViewOpponentUserDate;

        public OpponentUserViewHolder(@NonNull View itemView){
            super(itemView);
            cardViewOpponentUser = itemView.findViewById(R.id.card_view_chat_current_user);
            textViewOpponentUserMessage = itemView.findViewById(R.id.text_view_chat_opponnet_user_message);
            textViewOpponentUserDate = itemView.findViewById(R.id.text_view_chat_opponent_user_date);
        }
    }
}
