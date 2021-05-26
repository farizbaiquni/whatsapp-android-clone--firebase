package com.example.whatsapp_android_clone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    Context context;
    List<ModelProfileContact> modelProfileContactList;

    public ContactAdapter(Context ctx, List<ModelProfileContact> modelProfileContactList) {
        this.context = ctx;
        this.modelProfileContactList = modelProfileContactList;
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.select_contact_profile_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position){
        try {
            Picasso.get().load(modelProfileContactList.get(position).photoProfileContact).into(holder.photoContact);
        } catch (Exception e){
            holder.photoContact.setImageResource(R.drawable.friends);
        }
        holder.usernameContact.setText(modelProfileContactList.get(position).usernameProfileContact);
        holder.descriptionContact.setText(modelProfileContactList.get(position).desctiptionProfileContact);

        holder.cardViewContact.setOnClickListener( v -> {
            Toast.makeText(context, modelProfileContactList.get(position).usernameProfileContact, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return modelProfileContactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{
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
