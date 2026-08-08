// haut 1
package com.ludexa.moteur;

import java.util.List;

public class MoteurLogique {
    private Blueprint blueprintActif;

    public MoteurLogique(Blueprint blueprint) {
        this.blueprintActif = blueprint;
    }

    public void executerDemarrage() {
        if (blueprintActif == null || blueprintActif.noeuds == null) return;
        for (NoeudBase noeud : blueprintActif.noeuds) {
            if (noeud instanceof NoeudEventStart) {
                noeud.executer();
            }
        }
    }

    public void executerEvenement(Class<? extends NoeudBase> typeEvenement) {
        if (blueprintActif == null || blueprintActif.noeuds == null) return;
        for (NoeudBase noeud : blueprintActif.noeuds) {
            if (typeEvenement.isInstance(noeud)) {
                noeud.executer();
            }
        }
    }

    public void executerEvenementSurObjet(Class<? extends NoeudBase> typeEvenement, ObjetBase objetTouche) {
        if (blueprintActif == null || blueprintActif.noeuds == null || objetTouche == null) return;
        for (NoeudBase noeud : blueprintActif.noeuds) {
            if (typeEvenement.isInstance(noeud) && noeud.requiertCibleObjet()) {
                ObjetBase cible = noeud.getCibleObjet();
                if (cible != null && cible.id.equals(objetTouche.id)) {
                    noeud.executer();
                }
            }
        }
    }

    // NOUVEAU : Méthode dédiée à la vérification des événements de collision (vérifie les noeuds "Collision A/B")
    public void verifierCollisions(VueJeu vueJeu, List<ObjetBase> objetsContexte) {
        if (blueprintActif == null || blueprintActif.noeuds == null || objetsContexte == null) return;
        
        for (NoeudBase noeud : blueprintActif.noeuds) {
            if (noeud instanceof NoeudEventCollisionAB) {
                NoeudEventCollisionAB noeudCol = (NoeudEventCollisionAB) noeud;
                ObjetBase objA = noeudCol.getCibleObjet();
                ObjetBase objB = noeudCol.getCibleObjetB();
                
                if (objA != null && objB != null) {
                    boolean enCollision = UtilCollision.rectanglesSeChevauchent(objA, objetsContexte, objB, objetsContexte, vueJeu);
                    
                    if (enCollision && !noeudCol.isEtaitEnCollision()) {
                        noeudCol.setEtaitEnCollision(true);
                        noeudCol.executer();
                    } else if (!enCollision && noeudCol.isEtaitEnCollision()) {
                        noeudCol.setEtaitEnCollision(false); // Reset pour le prochain déclenchement
                    }
                }
            }
        }
    }
}
// bas 1
