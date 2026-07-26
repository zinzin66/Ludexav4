// haut 1
package com.ludexa.moteur;

import java.util.UUID;

public class ObjetBase {
    public String id;
    public String type;
    public String nom;
    public String contenuTexte;
    public float x;
    public float y;
    public float largeur;
    public float hauteur;
    public float rotation;

    public ObjetBase(String nom, float x, float y, float largeur, float hauteur) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.contenuTexte = ""; 
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.rotation = 0f; 
        
        if (nom != null) {
            String nomMinuscule = nom.toLowerCase();
            if (nomMinuscule.contains("texte")) {
                this.type = "texte";
            } else if (nomMinuscule.contains("rond")) {
                this.type = "rond";
            } else {
                this.type = "carré";
            }
        } else {
            this.type = "carré";
        }
    }

    public ObjetBase clonerProfond() {
        ObjetBase copie = new ObjetBase(this.nom, this.x, this.y, this.largeur, this.hauteur);
        copie.id = this.id; 
        copie.type = this.type;
        copie.contenuTexte = this.contenuTexte;
        copie.rotation = this.rotation; 
        return copie;
    }
}
// bas 1
