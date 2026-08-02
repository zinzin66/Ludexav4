// haut 1
package com.ludexa.moteur;

import android.graphics.Color;
import java.util.UUID;
import java.util.List;

public class ObjetBase {
    private static int compteurZOrderGlobal = 0;

    public String id;
    public String type;
    public String nom;
    public String contenuTexte;
    public float x;
    public float y;
    public float largeur;
    public float hauteur;
    public float rotation;
    
    public float scaleX = 1f;
    public float scaleY = 1f;

    public int couleur = Color.BLUE; 
    public int zOrder;           
    public boolean visible = true;   
    
    public String parentId; 
    
    // Nouveaux champs pour l'image
    public String cheminImage = null;
    public boolean afficherFondColore = true;
    public boolean estRamassable = false;
    public boolean estZoneDeClic = false;

    public ObjetBase(String nom, float x, float y, float largeur, float hauteur) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.contenuTexte = ""; 
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.rotation = 0f; 
        this.scaleX = 1f;
        this.scaleY = 1f;
        
        this.zOrder = compteurZOrderGlobal++;
        
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
    
    public void detacherParent() {
        this.parentId = null;
    }

    public ObjetBase clonerProfond() {
        ObjetBase copie = new ObjetBase(this.nom, this.x, this.y, this.largeur, this.hauteur);
        copie.id = this.id; 
        copie.type = this.type;
        copie.contenuTexte = this.contenuTexte;
        copie.rotation = this.rotation; 
        copie.scaleX = this.scaleX;
        copie.scaleY = this.scaleY;
        
        copie.couleur = this.couleur;
        copie.zOrder = this.zOrder;
        copie.visible = this.visible;
        copie.parentId = this.parentId; 
        
        // Copie des nouvelles propriétés d'image
        copie.cheminImage = this.cheminImage;
        copie.afficherFondColore = this.afficherFondColore;
        copie.estRamassable = this.estRamassable;
        copie.estZoneDeClic = this.estZoneDeClic;
        
        return copie;
    }
    
    public static boolean verifierBoucleParent(String enfantId, String parentPotentielId, List<ObjetBase> sceneObjets) {
        if (parentPotentielId == null) return true; 
        if (enfantId.equals(parentPotentielId)) return false; 
        
        String curParentId = parentPotentielId;
        while (curParentId != null) {
            if (curParentId.equals(enfantId)) return false; 
            
            ObjetBase parent = null;
            for (ObjetBase o : sceneObjets) {
                if (o.id.equals(curParentId)) { 
                    parent = o; 
                    break; 
                }
            }
            if (parent != null) {
                curParentId = parent.parentId; 
            } else {
                break; 
            }
        }
        return true;
    }
}
// bas 1
