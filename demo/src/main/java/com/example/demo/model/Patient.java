package com.example.demo.model;

public class Patient {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String createdAt; // Changé en String

    public Patient(int id, String username, String email, String phone, String address, String city, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}