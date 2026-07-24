// haut 1
package com.ludexa.moteur;

import java.util.UUID;

public class ObjetBase {
    public String id;
    public String type;
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
}
// bas 1
