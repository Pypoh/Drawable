package com.example.pypoh.drawable.Model;

public class NotifModel {
    private String battleTag;
    private int status; // 0 = terkirim, 1 = terTOLAK, 2 = diterima :)
    private String roomId;

    public NotifModel() {

    }

    public NotifModel(String battleTag, int status, String roomId) {
        this.battleTag = battleTag;
        this.status = status;
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBattleTag() {
        return battleTag;
    }

    public void setBattleTag(String battleTag) {
        this.battleTag = battleTag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
