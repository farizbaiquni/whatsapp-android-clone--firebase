package com.example.whatsapp_android_clone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp_android_clone.AddContactActivity;
import com.example.whatsapp_android_clone.R;
import com.example.whatsapp_android_clone.model.SelectContactModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectContactAdapter extends RecyclerView.Adapter {

    Context context;
    List<SelectContactModel> selectContactModel;

    public SelectContactAdapter(Context ctx, List<SelectContactModel> selectContactModel) {
        this.context = ctx;
        this.selectContactModel = selectContactModel;
    }

    @Override
    public int getItemViewType(int position) {
        switch (selectContactModel.get(position).type){
            case 0:
                return SelectContactModel.NEW_TYPE;
            case 1:
                return SelectContactModel.CONTACT_TYPE;
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
            case SelectContactModel.NEW_TYPE:
                view = layoutInflater.inflate(R.layout.select_contact_profile_new_option_layout, parent, false);
                return new NewViewHolder(view);

            case SelectContactModel.CONTACT_TYPE:
                view = layoutInflater.inflate(R.layout.select_contact_profile_layout, parent, false);
                return new ContactViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){

        final SelectContactModel object = selectContactModel.get(position);

        if(object != null){
            switch (object.type){
                case SelectContactModel.NEW_TYPE:
                    NewViewHolder newViewHolder = (NewViewHolder) holder;
                    newViewHolder.circleImageViewNew.setImageResource(R.drawable.friends);
                    newViewHolder.textViewTitleNew.setText(object.getUsernameProfileContact());

                    if(object.getUsernameProfileContact() == "New Contact"){
                        newViewHolder.cardViewNew.setOnClickListener( v -> {
                            Intent intent = new Intent(context, AddContactActivity.class);
                            context.startActivity(intent);
//                            Toast.makeText(context, "Intent", Toast.LENGTH_SHORT).show();
                        });
                    }

                    break;

                    case SelectContactModel.CONTACT_TYPE:
                        ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
                        try {
                            Picasso.get().load(object.getPhotoProfileContact()).into(contactViewHolder.photoContact);
                        } catch (Exception e){
                            contactViewHolder.photoContact.setImageResource(R.drawable.friends);
                        }
                        contactViewHolder.usernameContact.setText(object.getUsernameProfileContact());
                        contactViewHolder.descriptionContact.setText(object.getDesctiptionProfileContact());

                        contactViewHolder.cardViewContact.setOnClickListener( v -> {
                            Toast.makeText(context, selectContactModel.get(position).getUsernameProfileContact(), Toast.LENGTH_SHORT).show();
                        });
                        break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return selectContactModel.size();
    }


    //======================= VIEW HOLDER =======================
    public static class NewViewHolder extends RecyclerView.ViewHolder{
        CardView cardViewNew;
        CircleImageView circleImageViewNew;
        TextView textViewTitleNew;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewNew = itemView.findViewById(R.id.cv_new_option);
            circleImageViewNew = itemView.findViewById(R.id.civ_image_new_option);
            textViewTitleNew = itemView.findViewById(R.id.tv_title_new_profie);
        }
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        CardView cardViewContact;
        CircleImageView photoContact;
        TextView usernameContact, descriptionContact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewContact = itemView.findViewById(R.id.card_view_profile_contact);
            photoContact = itemView.findViewById(R.id.civ_image_profile_contact);
            usernameContact = itemView.findViewById(R.id.tv_username_profile_contact);
            descriptionContact = itemView.findViewById(R.id.tv_description_profile_contact);
        }
    }
}
