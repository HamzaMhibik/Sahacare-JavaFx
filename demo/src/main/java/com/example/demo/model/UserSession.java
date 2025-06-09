package com.example.demo.model;

import java.time.LocalDateTime;

public class UserSession {
    private static UserSession instance;
    private int id;  // Ajoutez un attribut id pour l'utilisateur
    private String username;
    private String role;
    private String email;
    private String password;
    private String phoneNumber;
    private String specialization;
    private String address;
    private LocalDateTime creationDate;

    // Modifiez le constructeur pour inclure l'ID
    private UserSession(int id, String username, String role, String email, String password, String phoneNumber, String specialization, String address) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.specialization = specialization;
        this.address = address;
        this.creationDate = LocalDateTime.now(); // Définit automatiquement la date de création
    }

    // Modifiez la méthode createSession pour inclure l'ID
    public static void createSession(int id, String username, String role, String email, String password, String phoneNumber, String specialization, String address) {
        System.out.println("Login successful! Welcome");
        instance = new UserSession(id, username, role, email, password, phoneNumber, specialization, address);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clearSession() {
        instance = null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}