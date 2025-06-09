package com.example.demo.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ArticleModel {
    private final StringProperty titre = new SimpleStringProperty();
    private final StringProperty categorie = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    public ArticleModel(String titre, String categorie, String description) {
        this.titre.set(titre);
        this.categorie.set(categorie);
        this.description.set(description);
    }

    // Getters and setters for JavaFX properties
    public String getTitre() { return titre.get(); }
    public StringProperty titreProperty() { return titre; }

    public String getCategorie() { return categorie.get(); }
    public StringProperty categorieProperty() { return categorie; }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
}