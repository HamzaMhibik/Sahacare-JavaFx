package com.example.demo.model;

public class Question {
    private int id;
    private String question;
    private String answer;
    private int userId;
    private String username; // Nouveau champ pour stocker le nom de l'utilisateur

    // Constructeur mis à jour
    public Question(int id, String question, String answer, int userId, String username) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.userId = userId;
        this.username = username; // Initialisation du nouveau champ
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username; // Getter pour le nouveau champ
    }
}