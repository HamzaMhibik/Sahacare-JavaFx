package com.example.demo.model;

public class Reponse {
    private int id;
    private int questionId;
    private String response;
    private int userId;

    // Constructeur
    public Reponse(int id, int questionId, String response, int userId) {
        this.id = id;
        this.questionId = questionId;
        this.response = response;
        this.userId = userId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}