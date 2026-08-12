// 12 08
// haut 1
package com.ludexa.moteur;

import android.graphics.Color;
import java.util.List;
import java.util.UUID;

public class ObjetBase {
    public String id;
    public String nom;
    public float x;
    public float y;
    public float largeur;
    public float hauteur;
    
    public int zOrder = 0;
    public boolean visible = true;
    public boolean estDeplacable = false;
    
    public int couleur = Color.BLUE;
    public String cheminImage = null;
    
    public String type = "carre"; 
    public boolean afficherFondColore = true;
    
    public String contenuTexte = "";
    public String cheminPolice = null;
    public float tailleFonte = 24f;
    
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float rotation = 0f;
    
    public String parentId = null;
    
    // Gestion de la transparence (1.0 = opaque, 0.0 = invisible)
    public float alpha = 1.0f;

    public ObjetBase() {
        this.id = UUID.randomUUID().toString();
    }

    public ObjetBase(String nom, float x, float y, float largeur, float hauteur) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
}
// bas 1
