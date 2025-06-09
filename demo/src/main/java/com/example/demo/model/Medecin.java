package com.example.demo.model;

import java.sql.Timestamp;

public class Medecin {
    private int id;
    private String nomComplet;  // Changed from username to nomComplet
    private String specialite;  // Changed from specialization to specialite
    private String email;
    private String phone;
    private String address;
    private String city;
    private Timestamp createdAt;

    public Medecin(String nomComplet, int id, String specialite, String email, String phone, String address, String city, Timestamp createdAt) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.specialite = specialite;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.createdAt = createdAt;
    }

    // Getters and setters for all properties
    public int getId() {
        return id;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public String getSpecialite() {
        return specialite;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}