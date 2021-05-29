package com.example.whatsapp_android_clone;

public class SelectContactModel {

    public static final int NEW_TYPE = 0;
    public static final int CONTACT_TYPE = 1;

    public int type;
    protected String idProfile, photoProfileContact, usernameProfileContact, desctiptionProfileContact;

    public SelectContactModel(int type, String id, String photo, String username, String description) {
        this.type = type;
        this.idProfile = id;
        this.photoProfileContact = photo;
        this.usernameProfileContact = username;
        this.desctiptionProfileContact = description;
    }

}
