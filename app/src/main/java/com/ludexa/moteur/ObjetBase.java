package com.ludexa.moteur;

import java.util.UUID;

public class ObjetBase {
    public String id;
    public String nom;
    public float x;
    public float y;
    public float largeur;
    public float hauteur;

    public ObjetBase(String nom, float x, float y, float largeur, float hauteur) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
}
