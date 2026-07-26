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

    public ObjetBase(String nom, float x, float y, float largeur, float hauteur) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.contenuTexte = ""; // Initialisé vide par défaut
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        
        // Déduction automatique du type pour ne pas casser le reste du projet
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

    // NOUVEAU : Méthode de clonage
    public ObjetBase clonerProfond() {
        ObjetBase copie = new ObjetBase(this.nom, this.x, this.y, this.largeur, this.hauteur);
        copie.id = this.id; // IMPORTANT : Garder le même ID pour que le moteur retrouve ses références
        copie.type = this.type;
        copie.contenuTexte = this.contenuTexte;
        return copie;
    }
}
// bas 1

