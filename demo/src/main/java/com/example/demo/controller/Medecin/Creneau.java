package com.example.demo.controller.Medecin;

public class Creneau {

    private String jour;
    private String creneau;

    public Creneau(String jour, String creneau) {
        this.jour = jour;
        this.creneau = creneau;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getCreneau() {
        return creneau;
    }

    public void setCreneau(String creneau) {
        this.creneau = creneau;
    }
}
