package com.example.mobileapk;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String login;
    private String password;
    private String mail;
    private String name;
    private String surname;
    private String data;

    public User() {
    }

    public User(ObjectId id, String login, String password, String mail, String name, String surname, String data) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.mail = mail;
        this.name = name;
        this.surname = surname;
        this.data = data;
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

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getData() {
        return data;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
