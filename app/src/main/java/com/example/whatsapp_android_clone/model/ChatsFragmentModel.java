package com.example.whatsapp_android_clone.model;

public class ChatsFragmentModel {

    String photoProfileUrl, username, lastMesaage, numberUnreadMessage, lastMessageDate;

    public ChatsFragmentModel(String photoProfileUrl, String username, String lastMesaage,
                              String numberUnreadMessage, String lastMessageDate) {
        this.photoProfileUrl = photoProfileUrl;
        this.username = username;
        this.lastMesaage = lastMesaage;
        this.numberUnreadMessage = numberUnreadMessage;
        this.lastMessageDate = lastMessageDate;
    }

    public String getPhotoProfileUrl() {
        return photoProfileUrl;
    }

    public void setPhotoProfileUrl(String photoProfileUrl) {
        this.photoProfileUrl = photoProfileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMesaage() {
        return lastMesaage;
    }

    public void setLastMesaage(String lastMesaage) {
        this.lastMesaage = lastMesaage;
    }

    public String getNumberUnreadMessage() {
        return numberUnreadMessage;
    }

    public void setNumberUnreadMessage(String numberUnreadMessage) {
        this.numberUnreadMessage = numberUnreadMessage;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

}
