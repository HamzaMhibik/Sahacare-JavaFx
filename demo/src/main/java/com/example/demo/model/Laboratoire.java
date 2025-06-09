package com.example.demo.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Laboratoire {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty city = new SimpleStringProperty();

    // Constructeur
    public Laboratoire(int id, String username, String email, String phone, String city) {
        this.id.set(id);
        this.username.set(username);
        this.email.set(email);
        this.phone.set(phone);
        this.city.set(city);
    }

    // Getters et Setters pour les propriétés
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }
}