package com.seu.domian;

public class NormalUser {
    private String userId;
    private String userName;
    private String phone;
    private String idCard;
    private String password;
    private String role;

    public NormalUser() {
    }

    public NormalUser(String userId, String userName, String phone, String idCard, String password,String role) {
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.idCard = idCard;
        this.password = password;
        this.role=role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
