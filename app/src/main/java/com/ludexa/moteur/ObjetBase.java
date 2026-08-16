// haut 1
package com.ludexa.moteur;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    public boolean estVerrouille = false;
    public boolean estRamassable = false;
    public boolean estZoneDeClic = false;
    
    public int couleur = Color.BLUE;
    public String cheminImage = null;
    
    public String cheminImagePresse = null;
    public String cheminImageDesactive = null;
    public boolean estDesactive = false;
    
    public String cibleJoystickId = null;
    
    public String filtreCouleur = "Aucun";
    
    public boolean clignotementActif = false;
    public long clignotementVitesseMs = 500;
    public long clignotementDureeTotalMs = 0;
    public long tempsDebutClignotement = 0;
    public transient boolean etatVisibleClignotement = true; 
    
    public boolean surbrillanceActive = false;
    public String couleurSurbrillance = "Jaune";
    
    public float vitesseAvanceContinue = 0f;
    public String idCiblePoursuite = null;
    public float vitessePoursuite = 0f;
    public boolean fuiteActive = false;
    
    // --- NOUVEAUTÉ : GESTION DE MASSE ---
    public String tag = ""; // Permet de regrouper les objets (ex: "Ennemi", "Projectile")
    // ------------------------------------

    public String type = "carre"; 
    public boolean afficherFondColore = true;
    
    public String contenuTexte = "";
    public String cheminPolice = null;
    public float tailleFonte = 24f;
    
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float rotation = 0f;
    
    public String parentId = null;
    public float alpha = 1.0f;

    public boolean estPhysique = false;
    public boolean estStatique = true;
    public float vitesseY = 0f;
    public float rebond = 0.4f;
    public float graviteScale = 1.0f;

    public HashMap<String, List<String>> animations = new HashMap<>();
    public String animationActive = null;
    public int frameCourante = 0;
    public long dernierTempsFrame = 0;
    public int vitesseFps = 8;
    public boolean boucleAnimation = false;
    public boolean animationEnCours = false;

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

    public ObjetBase clonerProfond() {
        ObjetBase copie = new ObjetBase();
        copie.id = this.id; 
        copie.nom = this.nom;
        copie.x = this.x;
        copie.y = this.y;
        copie.largeur = this.largeur;
        copie.hauteur = this.hauteur;
        copie.zOrder = this.zOrder;
        copie.visible = this.visible;
        copie.estDeplacable = this.estDeplacable;
        copie.estVerrouille = this.estVerrouille;
        copie.estRamassable = this.estRamassable;
        copie.estZoneDeClic = this.estZoneDeClic;
        copie.couleur = this.couleur;
        copie.cheminImage = this.cheminImage;
        
        copie.cheminImagePresse = this.cheminImagePresse;
        copie.cheminImageDesactive = this.cheminImageDesactive;
        copie.estDesactive = this.estDesactive;
        copie.cibleJoystickId = this.cibleJoystickId;
        
        copie.filtreCouleur = this.filtreCouleur;
        copie.clignotementActif = this.clignotementActif;
        copie.clignotementVitesseMs = this.clignotementVitesseMs;
        copie.clignotementDureeTotalMs = this.clignotementDureeTotalMs;
        copie.tempsDebutClignotement = this.tempsDebutClignotement;
        copie.etatVisibleClignotement = this.etatVisibleClignotement;
        copie.surbrillanceActive = this.surbrillanceActive;
        copie.couleurSurbrillance = this.couleurSurbrillance;
        
        copie.vitesseAvanceContinue = this.vitesseAvanceContinue;
        copie.idCiblePoursuite = this.idCiblePoursuite;
        copie.vitessePoursuite = this.vitessePoursuite;
        copie.fuiteActive = this.fuiteActive;
        
        copie.tag = this.tag; // Copie du Tag

        copie.type = this.type;
        copie.afficherFondColore = this.afficherFondColore;
        copie.contenuTexte = this.contenuTexte;
        copie.cheminPolice = this.cheminPolice;
        copie.tailleFonte = this.tailleFonte;
        copie.scaleX = this.scaleX;
        copie.scaleY = this.scaleY;
        copie.rotation = this.rotation;
        copie.parentId = this.parentId;
        copie.alpha = this.alpha;
        
        copie.estPhysique = this.estPhysique;
        copie.estStatique = this.estStatique;
        copie.vitesseY = this.vitesseY;
        copie.rebond = this.rebond;
        copie.graviteScale = this.graviteScale;
        
        for (Map.Entry<String, List<String>> entry : this.animations.entrySet()) {
            copie.animations.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        copie.animationActive = this.animationActive;
        copie.frameCourante = this.frameCourante;
        copie.dernierTempsFrame = this.dernierTempsFrame;
        copie.vitesseFps = this.vitesseFps;
        copie.boucleAnimation = this.boucleAnimation;
        copie.animationEnCours = this.animationEnCours;

        return copie;
    }

    public static boolean verifierBoucleParent(String idEnfant, String idParentPropose, List<ObjetBase> objets) {
        if (idParentPropose == null) return false;
        if (idEnfant.equals(idParentPropose)) return true;
        
        String curParentId = idParentPropose;
        while (curParentId != null) {
            ObjetBase parentObj = null;
            for (ObjetBase o : objets) {
                if (o.id.equals(curParentId)) {
                    parentObj = o;
                    break;
                }
            }
            if (parentObj != null) {
                if (idEnfant.equals(parentObj.parentId)) {
                    return true;
                }
                curParentId = parentObj.parentId;
            } else {
                break;
            }
        }
        return false;
    }
}
// bas 1
