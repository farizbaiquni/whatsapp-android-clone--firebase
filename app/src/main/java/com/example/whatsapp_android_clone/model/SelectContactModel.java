package com.example.whatsapp_android_clone.model;

public class SelectContactModel {

    public static final int NEW_TYPE = 0;
    public static final int CONTACT_TYPE = 1;

    public int type;
    private String idProfile, photoProfileContact, usernameProfileContact, desctiptionProfileContact;

    public SelectContactModel(int type, String id, String photo, String username, String description) {
        this.type = type;
        this.idProfile = id;
        this.photoProfileContact = photo;
        this.usernameProfileContact = username;
        this.desctiptionProfileContact = description;
    }

    public static int getNewType() {
        return NEW_TYPE;
    }

    public static int getContactType() {
        return CONTACT_TYPE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(String idProfile) {
        this.idProfile = idProfile;
    }

    public String getPhotoProfileContact() {
        return photoProfileContact;
    }

    public void setPhotoProfileContact(String photoProfileContact) {
        this.photoProfileContact = photoProfileContact;
    }

    public String getUsernameProfileContact() {
        return usernameProfileContact;
    }

    public void setUsernameProfileContact(String usernameProfileContact) {
        this.usernameProfileContact = usernameProfileContact;
    }

    public String getDesctiptionProfileContact() {
        return desctiptionProfileContact;
    }

    public void setDesctiptionProfileContact(String desctiptionProfileContact) {
        this.desctiptionProfileContact = desctiptionProfileContact;
    }
}
