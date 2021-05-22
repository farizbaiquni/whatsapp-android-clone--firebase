package com.example.whatsapp_android_clone;

public class ModelProfileContact {
    private int photoProfileContact;
    private String usernameProfileContact, desctiptionProfileContact;

    public ModelProfileContact(int photo, String username, String description) {
        this.photoProfileContact = photo;
        this.usernameProfileContact = username;
        this.desctiptionProfileContact = description;
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
