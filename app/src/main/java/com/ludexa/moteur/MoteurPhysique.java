// haut 1
package com.ludexa.moteur;

import java.util.List;

public class MoteurPhysique {
    // Constantes d'ajustement du "jus" physique
    private static final float GRAVITE = 0.8f;
    private static final float VITESSE_MAX_CHUTE = 25f;
    private static final float SEUIL_MINIMUM_REBOND = 1.5f;

    public void mettreAJour(List<ObjetBase> objets) {
        // Phase 1 : Application de la gravité
        for (ObjetBase obj : objets) {
            if (obj.estPhysique && !obj.estStatique) {
                obj.vitesseY += GRAVITE;
                
                if (obj.vitesseY > VITESSE_MAX_CHUTE) {
                    obj.vitesseY = VITESSE_MAX_CHUTE;
                }
                obj.y += obj.vitesseY;
            }
        }

        // Phase 2 : Résolution des collisions (ancrage au centre pour correspondre au rendu visuel)
        for (ObjetBase dynamique : objets) {
            if (dynamique.estPhysique && !dynamique.estStatique) {
                
                float dynDemiHauteur = (dynamique.hauteur * Math.abs(dynamique.scaleY)) / 2f;

                for (ObjetBase statique : objets) {
                    if (dynamique == statique) continue;

                    if (statique.estPhysique && statique.estStatique) {
                        if (testerCollisionAABB(dynamique, statique)) {
                            
                            // On ne déclenche l'arrêt que si l'objet chute vers le bas (Sol)
                            if (dynamique.vitesseY >= 0) {
                                // Calcul du bord Haut visuel exact de l'objet statique
                                float statCentreY = statique.y + (statique.hauteur / 2f);
                                float statDemiHauteur = (statique.hauteur * Math.abs(statique.scaleY)) / 2f;
                                float statHaut = statCentreY - statDemiHauteur;

                                // On replace l'objet dynamique pour que son bord bas touche le bord haut du statique
                                dynamique.y = statHaut - (dynamique.hauteur / 2f) - dynDemiHauteur;

                                // Rebond ou arrêt
                                if (dynamique.vitesseY > SEUIL_MINIMUM_REBOND) {
                                    dynamique.vitesseY = -dynamique.vitesseY * dynamique.rebond;
                                } else {
                                    dynamique.vitesseY = 0f;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Détection de collision (AABB) calculée à partir des CENTRES 
     * pour correspondre à la matrice de rendu de VueJeu.java
     */
    private boolean testerCollisionAABB(ObjetBase a, ObjetBase b) {
        // Centres réels
        float aCentreX = a.x + (a.largeur / 2f);
        float aCentreY = a.y + (a.hauteur / 2f);
        float aDemiLargeur = (a.largeur * Math.abs(a.scaleX)) / 2f;
        float aDemiHauteur = (a.hauteur * Math.abs(a.scaleY)) / 2f;

        float aGauche = aCentreX - aDemiLargeur;
        float aDroite = aCentreX + aDemiLargeur;
        float aHaut = aCentreY - aDemiHauteur;
        float aBas = aCentreY + aDemiHauteur;

        // Centres réels
        float bCentreX = b.x + (b.largeur / 2f);
        float bCentreY = b.y + (b.hauteur / 2f);
        float bDemiLargeur = (b.largeur * Math.abs(b.scaleX)) / 2f;
        float bDemiHauteur = (b.hauteur * Math.abs(b.scaleY)) / 2f;

        float bGauche = bCentreX - bDemiLargeur;
        float bDroite = bCentreX + bDemiLargeur;
        float bHaut = bCentreY - bDemiHauteur;
        float bBas = bCentreY + bDemiHauteur;

        return (aGauche < bDroite &&
                aDroite > bGauche &&
                aHaut < bBas &&
                aBas > bHaut);
    }
}
// bas 1
