package com.example.demo.model;

public class Article {
    private int id;
    private String titre;
    private String categorie;
    private String description;
    private String username;

    // Constructeur
    public Article(int id, String titre, String categorie, String description, String username) {
        this.id = id;
        this.titre = titre;
        this.categorie = categorie;
        this.description = description;
        this.username = username;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }
}