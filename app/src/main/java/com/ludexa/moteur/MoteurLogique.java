// haut 1
package com.ludexa.moteur;

import java.util.List;

public class MoteurLogique {
    
    // NOUVEAU : Mémoire contextuelle
    public static ObjetBase dernierObjetImplique = null;
    
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
                        noeudCol.setEtaitEnCollision(false);
                    }
                }
            } else if (noeud instanceof NoeudEventSortieZone) {
                NoeudEventSortieZone noeudSortie = (NoeudEventSortieZone) noeud;
                ObjetBase objA = noeudSortie.getCibleObjet();
                ObjetBase objB = noeudSortie.getCibleObjetB();
                
                if (objA != null && objB != null) {
                    boolean enCollision = UtilCollision.rectanglesSeChevauchent(objA, objetsContexte, objB, objetsContexte, vueJeu);
                    
                    if (enCollision && !noeudSortie.isEtaitEnCollision()) {
                        noeudSortie.setEtaitEnCollision(true);
                    } else if (!enCollision && noeudSortie.isEtaitEnCollision()) {
                        noeudSortie.setEtaitEnCollision(false);
                        noeudSortie.executer();
                    }
                }
            } 
            // GESTION DE LA COLLISION PAR TAG
            else if (noeud instanceof NoeudEventCollisionTag) {
                NoeudEventCollisionTag noeudTag = (NoeudEventCollisionTag) noeud;
                ObjetBase objA = noeudTag.getCibleObjet();
                String cibleTag = noeudTag.getTagCible();

                if (objA != null && cibleTag != null && !cibleTag.trim().isEmpty()) {
                    boolean enCollision = false;
                    ObjetBase objetCloneTouche = null; 
                    
                    for (ObjetBase objB : objetsContexte) {
                        if (objA != objB && objB.tag != null && cibleTag.trim().equalsIgnoreCase(objB.tag.trim())) {
                            if (UtilCollision.rectanglesSeChevauchent(objA, objetsContexte, objB, objetsContexte, vueJeu)) {
                                enCollision = true;
                                objetCloneTouche = objB; 
                                break; 
                            }
                        }
                    }

                    if (enCollision && !noeudTag.isEtaitEnCollision()) {
                        noeudTag.setEtaitEnCollision(true);
                        
                        // NOUVEAU : Sauvegarde de l'objet touché avant exécution
                        MoteurLogique.dernierObjetImplique = objetCloneTouche;
                        
                        noeudTag.executer();
                    } else if (!enCollision && noeudTag.isEtaitEnCollision()) {
                        noeudTag.setEtaitEnCollision(false);
                    }
                }
            }
        }
    }

    public void verifierVariablesChangees() {
        if (blueprintActif == null || blueprintActif.noeuds == null) return;
        
        for (NoeudBase noeud : blueprintActif.noeuds) {
            if (noeud instanceof NoeudEventVariableChange) {
                ((NoeudEventVariableChange) noeud).verifierEtDeclencher();
            }
        }
    }
}
// bas 1
