package com.example.demo.model;

import java.time.LocalDateTime;

public class Discussion {
    private int id;
    private int userId; // ID du patient
    private int medecinId; // ID du médecin
    private LocalDateTime createdAt;
    private String patientName; // Nom d'utilisateur du patient
    private String medecinName; // Nom complet du médecin

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(int medecinId) {
        this.medecinId = medecinId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMedecinName() {
        return medecinName;
    }

    public void setMedecinName(String medecinName) {
        this.medecinName = medecinName;
    }
}