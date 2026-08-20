// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class MoteurPhysique {
    private static final float GRAVITE = 0.8f;
    private static final float VITESSE_MAX_CHUTE = 25f;
    private static final float SEUIL_MINIMUM_REBOND = 1.5f;

    // Retourne la liste des objets ayant subi un choc lors de cette frame
    public List<ObjetBase> mettreAJour(List<ObjetBase> objets) {
        List<ObjetBase> objetsEnChoc = new ArrayList<>();

        // Phase 1 : Application de la gravité
        for (ObjetBase obj : objets) {
            if (obj.estPhysique && !obj.estStatique) {
                
                // Application de la gravité modulée par l'échelle de l'objet
                obj.vitesseY += (GRAVITE * obj.graviteScale);
                
                if (obj.vitesseY > VITESSE_MAX_CHUTE) {
                    obj.vitesseY = VITESSE_MAX_CHUTE;
                }
                obj.y += obj.vitesseY;
            }
        }

        // Phase 2 : Résolution des collisions (ancrage au centre)
        for (ObjetBase dynamique : objets) {
            if (dynamique.estPhysique && !dynamique.estStatique) {
                
                float dynDemiHauteur = (dynamique.hauteur * Math.abs(dynamique.scaleY)) / 2f;
                boolean aEuUnChoc = false;

                for (ObjetBase statique : objets) {
                    if (dynamique == statique) continue;

                    if (statique.estPhysique && statique.estStatique) {
                        if (testerCollisionAABB(dynamique, statique)) {
                            
                            // On ne déclenche l'arrêt type plateforme que si la gravité est active
                            if (dynamique.vitesseY >= 0 && dynamique.graviteScale != 0f) {
                                float statCentreY = statique.y + (statique.hauteur / 2f);
                                float statDemiHauteur = (statique.hauteur * Math.abs(statique.scaleY)) / 2f;
                                float statHaut = statCentreY - statDemiHauteur;

                                dynamique.y = statHaut - (dynamique.hauteur / 2f) - dynDemiHauteur;

                                // Rebond ou arrêt
                                if (dynamique.vitesseY > SEUIL_MINIMUM_REBOND) {
                                    dynamique.vitesseY = -dynamique.vitesseY * dynamique.rebond;
                                    aEuUnChoc = true; // Impact fort (rebond)
                                } else {
                                    if (dynamique.vitesseY > 0.1f) {
                                        aEuUnChoc = true; // Impact faible (arrêt final)
                                    }
                                    dynamique.vitesseY = 0f;
                                }
                            }
                        }
                    }
                }
                if (aEuUnChoc) {
                    objetsEnChoc.add(dynamique);
                }
            }
        }
        return objetsEnChoc;
    }

    private boolean testerCollisionAABB(ObjetBase a, ObjetBase b) {
        float aCentreX = a.x + (a.largeur / 2f);
        float aCentreY = a.y + (a.hauteur / 2f);
        float aDemiLargeur = (a.largeur * Math.abs(a.scaleX)) / 2f;
        float aDemiHauteur = (a.hauteur * Math.abs(a.scaleY)) / 2f;

        float aGauche = aCentreX - aDemiLargeur;
        float aDroite = aCentreX + aDemiLargeur;
        float aHaut = aCentreY - aDemiHauteur;
        float aBas = aCentreY + aDemiHauteur;

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
