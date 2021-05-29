package com.example.whatsapp_android_clone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingMenuAdapter extends RecyclerView.Adapter {

    Context context;
    List<SettingMenuModel> settingMenuModels;

    public SettingMenuAdapter(Context ctx, List<SettingMenuModel> settingMenuModels) {
        this.context = ctx;
        this.settingMenuModels = settingMenuModels;
    }

    @Override
    public int getItemViewType(int position) {
        switch (settingMenuModels.get(position).type){
            case 0:
                return SettingMenuModel.PROFILE_TYPE;
            case 1:
                return SettingMenuModel.MENU_TYPE;
            case 2:
                return SettingMenuModel.INVITE_FRIEND_TYPE;
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
            case SettingMenuModel.PROFILE_TYPE:
                view = layoutInflater.inflate(R.layout.setting_menu_profile, parent, false);
                return new ProfileViewHolder(view);

            case SettingMenuModel.MENU_TYPE:
                view = layoutInflater.inflate(R.layout.setting_menu_row, parent, false);
                return new MenuViewHolder(view);

            case SettingMenuModel.INVITE_FRIEND_TYPE:
                view = layoutInflater.inflate(R.layout.setting_menu_invite_friend, parent, false);
                return new InviteFriendViewHolder(view);

            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SettingMenuModel object = settingMenuModels.get(position);
        if(object != null){
            switch (object.type){
                case SettingMenuModel.PROFILE_TYPE:
                    ProfileViewHolder profileViewHolder = (ProfileViewHolder) holder;
                    try {
                        Picasso.get().load(object.photoProfile).into(profileViewHolder.circleImageViewImageProfile);
                    } catch (Exception e){
                        profileViewHolder.circleImageViewImageProfile.setImageResource(R.drawable.friends);
                    }
                    profileViewHolder.textViewUsernameProfile.setText(object.title);
                    profileViewHolder.textViewDescriptionProfile.setText(object.description);
                    profileViewHolder.imageViewBarcodeProfile.setImageResource(object.barcode);
                    ((ProfileViewHolder) holder).relativeLayoutProfileLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, EditProfileActivity.class);
                            context.startActivity(intent);
                        }
                    });
                    break;

                case SettingMenuModel.MENU_TYPE:
                    MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
                    menuViewHolder.imageViewSettingImage.setImageResource(object.image);
                    menuViewHolder.textViewSettingName.setText(object.title);
                    menuViewHolder.getTextViewSettingDescription.setText(object.description);
                    break;

                case SettingMenuModel.INVITE_FRIEND_TYPE:
                    InviteFriendViewHolder inviteFriendViewHolder = (InviteFriendViewHolder) holder;
                    inviteFriendViewHolder.imageViewImageInviteFriend.setImageResource(object.image);
                    inviteFriendViewHolder.textViewImageInviteFriend.setText(object.title);
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return settingMenuModels.size();
    }



    //======================= VIEW HOLDER =======================
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageViewImageProfile;
        TextView textViewUsernameProfile, textViewDescriptionProfile;
        ImageView imageViewBarcodeProfile;
        RelativeLayout relativeLayoutProfileLayout;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageViewImageProfile = itemView.findViewById(R.id.image_profile_setting_menu);
            textViewUsernameProfile = itemView.findViewById(R.id.username_profile_setting_menu);
            textViewDescriptionProfile = itemView.findViewById(R.id.description_profile_setting_menu);
            imageViewBarcodeProfile = itemView.findViewById(R.id.scan_profile_setting_menu);
            relativeLayoutProfileLayout = itemView.findViewById(R.id.profile_setting_menu_layout);
        }
    }


    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSettingImage;
        TextView textViewSettingName, getTextViewSettingDescription;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSettingImage = itemView.findViewById(R.id.image_setting_menu);
            textViewSettingName = itemView.findViewById(R.id.title_setting_menu);
            getTextViewSettingDescription = itemView.findViewById(R.id.description_setting_menu);
        }
    }


    public static class InviteFriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewImageInviteFriend;
        TextView textViewImageInviteFriend;

        public InviteFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewImageInviteFriend = itemView.findViewById(R.id.image_invite_friend_setting_menu);
            textViewImageInviteFriend = itemView.findViewById(R.id.title_invite_friend_setting_menu);
        }
    }

}
