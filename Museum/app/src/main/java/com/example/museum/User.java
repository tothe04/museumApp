package com.example.museum;

public class User {
    private String userName;
    private String email;
    private String phone;
    private String address;

    public User() {}

    public User(String userName, String email, String phone, String address) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}