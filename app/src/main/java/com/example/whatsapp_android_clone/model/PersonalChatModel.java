package com.example.whatsapp_android_clone.model;

public class PersonalChatModel {
    public static final int CHAT_CURRENT_USER = 1;
    public static final int CHAT_OPPONENT_USER = 2;

    private String idUser, message, timestamps;
    private int typeUser;
    private Long timestampsEpoch, messageStatus;
    private boolean deleteStatus;

    public PersonalChatModel(String idUser, String message, String timestamps, Long timestampsEpoch, boolean deleteStatus, Long messageStatus,
                             int typeUser) {
        this.idUser = idUser;
        this.message = message;
        this.timestamps = timestamps;
        this.timestampsEpoch = timestampsEpoch;
        this.deleteStatus = deleteStatus;
        this.messageStatus = messageStatus;
        this.typeUser = typeUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(String timestamps) {
        this.timestamps = timestamps;
    }

    public int getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(int typeUser) {
        this.typeUser = typeUser;
    }

    public Long getTimestampsEpoch() {
        return timestampsEpoch;
    }

    public void setTimestampsEpoch(Long timestampsEpoch) {
        this.timestampsEpoch = timestampsEpoch;
    }

    public Long getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Long messageStatus) {
        this.messageStatus = messageStatus;
    }

    public boolean isDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
}