package com.example.demo.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int discussionId;
    private int patientId;
    private int medecinId;
    private String content;
    private Timestamp createdAt; // Use java.sql.Timestamp
    private int envoyer;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(int discussionId) {
        this.discussionId = discussionId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(int medecinId) {
        this.medecinId = medecinId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) { // Use java.sql.Timestamp
        this.createdAt = createdAt;
    }

    public int getEnvoyer() {
        return envoyer;
    }

    public void setEnvoyer(int envoyer) {
        this.envoyer = envoyer;
    }
}