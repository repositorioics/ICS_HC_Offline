package com.ics.ics_hc_offline.dto;

public class InfoSessionWSDTO implements java.io.Serializable {
    private static final long serialVersionUID = 7301833333255795271L;

    private int userId;
    private String user;
    private String nameUser;

    public InfoSessionWSDTO(){
        this.userId = 0;
        this.user = "";
        this.nameUser = "";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
