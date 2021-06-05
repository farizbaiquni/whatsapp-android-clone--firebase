package com.example.whatsapp_android_clone.model;

public class AddContactModel {

    private String id, photoProfile, contactName, description;

    public AddContactModel(String id, String photoProfile, String contactName, String description) {
        this.id = id;
        this.photoProfile = photoProfile;
        this.contactName = contactName;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
