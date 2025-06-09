package com.example.demo.model;

import java.sql.Timestamp;

public class User {
    private String email;
    private String username;
    private String phone;
    private String address;
    private Timestamp createdAt;
    private String city;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}