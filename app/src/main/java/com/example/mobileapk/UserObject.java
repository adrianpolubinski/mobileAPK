package com.example.mobileapk;

import org.bson.types.ObjectId;

public class UserObject {

    private ObjectId id = new ObjectId();
    private String login;
    private String password;
    private String mail;
    private String data;
    private String avatar;
    private UserName userName;


    public UserObject() {
    }

    public UserObject(String login, String password, String mail, String name, String surname, String data, String avatar) {

        this.login = login;
        this.password = password;
        this.mail = mail;
        this.data = data;
        this.avatar = avatar;
        this.userName = new UserName(name, surname);
    }

    public ObjectId getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public String getData() {
        return data;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserName getUserName() {
        return userName;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUserName(UserName userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", data='" + data + '\'' +
                ", userName=" + userName +
                '}';
    }
}
