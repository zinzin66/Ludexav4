// haut 1
package com.ludexa.moteur;

import java.util.List;

public class MoteurPhysique {
    // Constantes d'ajustement du "jus" physique (à affiner selon le feeling souhaité)
    private static final float GRAVITE = 0.8f;
    private static final float VITESSE_MAX_CHUTE = 25f;
    private static final float SEUIL_MINIMUM_REBOND = 1.5f;

    /**
     * Doit être appelée à chaque frame dans VueJeu.java
     */
    public void mettreAJour(List<ObjetBase> objets) {
        // Phase 1 : Application de la gravité
        for (ObjetBase obj : objets) {
            if (obj.estPhysique && !obj.estStatique) {
                obj.vitesseY += GRAVITE;
                
                // Plafonner la vitesse de chute
                if (obj.vitesseY > VITESSE_MAX_CHUTE) {
                    obj.vitesseY = VITESSE_MAX_CHUTE;
                }
                
                obj.y += obj.vitesseY;
            }
        }

        // Phase 2 : Résolution basique des collisions (arrêt sur les solides)
        for (ObjetBase dynamique : objets) {
            if (dynamique.estPhysique && !dynamique.estStatique) {
                for (ObjetBase statique : objets) {
                    if (dynamique == statique) continue;

                    // On teste uniquement contre d'autres objets physiques statiques (murs, sols)
                    if (statique.estPhysique && statique.estStatique) {
                        if (testerCollisionAABB(dynamique, statique)) {
                            
                            // Replacer l'objet dynamique exactement au-dessus du statique (comportement sol)
                            float hauteurReelleDynamique = dynamique.hauteur * dynamique.scaleY;
                            dynamique.y = statique.y - hauteurReelleDynamique;

                            // Appliquer le rebond si la vitesse d'impact est suffisante
                            if (dynamique.vitesseY > SEUIL_MINIMUM_REBOND) {
                                dynamique.vitesseY = -dynamique.vitesseY * dynamique.rebond;
                            } else {
                                // Arrêt complet pour éviter que l'objet ne tremble indéfiniment
                                dynamique.vitesseY = 0f;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Détection de collision basique de type Axis-Aligned Bounding Box (AABB)
     * Prend en compte le scaleX et scaleY définis dans ObjetBase.
     */
    private boolean testerCollisionAABB(ObjetBase a, ObjetBase b) {
        float aGauche = a.x;
        float aDroite = a.x + (a.largeur * a.scaleX);
        float aHaut = a.y;
        float aBas = a.y + (a.hauteur * a.scaleY);

        float bGauche = b.x;
        float bDroite = b.x + (b.largeur * b.scaleX);
        float bHaut = b.y;
        float bBas = b.y + (b.hauteur * b.scaleY);

        return (aGauche < bDroite &&
                aDroite > bGauche &&
                aHaut < bBas &&
                aBas > bHaut);
    }
}
// bas 1

