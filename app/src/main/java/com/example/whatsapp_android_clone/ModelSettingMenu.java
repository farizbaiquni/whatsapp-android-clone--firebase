package com.example.whatsapp_android_clone;

public class ModelSettingMenu {

    public static final int PROFILE_TYPE = 0;
    public static final int MENU_TYPE = 1 ;
    public static final int INVITE_FRIEND_TYPE = 2 ;

    public int type;
    public int image;
    public int barcode;
    public String title, description;
    public String photoProfile;

    public ModelSettingMenu (int type, int image, String title, String description, int barcode, String photoProfile){
        this.type = type;
        this.image = image;
        this.barcode = barcode;
        this.title = title;
        this.description = description;
        this.barcode = barcode;
        this.photoProfile = photoProfile;
    }

}
