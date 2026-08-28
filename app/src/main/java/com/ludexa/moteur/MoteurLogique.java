// haut 1
package com.ludexa.moteur;

import java.util.List;

public class MoteurLogique {
    
    public static ObjetBase dernierObjetImplique = null;
    
    private Blueprint blueprintActif;

    public MoteurLogique(Blueprint blueprint) {
        this.blueprintActif = blueprint;
    }
    
    // NOUVEAU : Méthodes pour injecter et nettoyer la logique des Prefabs dynamiquement
    public void ajouterNoeudAuBlueprintActif(NoeudBase noeud) {
        if (blueprintActif != null && blueprintActif.noeuds != null) {
            blueprintActif.noeuds.add(noeud);
        }
    }
    
    public void nettoyerNoeudsParTag(String tag) {
        if (blueprintActif == null || blueprintActif.noeuds == null || tag == null) return;
        java.util.Iterator<NoeudBase> it = blueprintActif.noeuds.iterator();
        while (it.hasNext()) {
            NoeudBase n = it.next();
            if (n.categorie != null && n.categorie.contains(tag)) {
                it.remove();
            }
        }
    }
    // --------------------------------------------------------------------------------

    public void executerDemarrage() {
        if (blueprintActif == null || blueprintActif.noeuds == null) return;
        // Pour éviter un crash de modification concurrente (ConcurrentModificationException)
        // si un "NoeudEventStart" déclenche une instanciation qui ajoute des nœuds :
        java.util.List<NoeudBase> copieNoeuds = new java.util.ArrayList<>(blueprintActif.noeuds);
        for (NoeudBase noeud : copieNoeuds) {
            if (noeud instanceof NoeudEventStart) {
                noeud.executer();
            }
        }
    }

    public void executerEvenement(Class<? extends NoeudBase> typeEvenement) {
        if (blueprintActif == null || blueprintActif.noeuds == null) return;
        java.util.List<NoeudBase> copieNoeuds = new java.util.ArrayList<>(blueprintActif.noeuds);
        for (NoeudBase noeud : copieNoeuds) {
            if (typeEvenement.isInstance(noeud)) {
                noeud.executer();
            }
        }
    }

    public void executerEvenementSurObjet(Class<? extends NoeudBase> typeEvenement, ObjetBase objetTouche) {
        if (blueprintActif == null || blueprintActif.noeuds == null || objetTouche == null) return;
        java.util.List<NoeudBase> copieNoeuds = new java.util.ArrayList<>(blueprintActif.noeuds);
        for (NoeudBase noeud : copieNoeuds) {
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
        
        java.util.List<NoeudBase> copieNoeuds = new java.util.ArrayList<>(blueprintActif.noeuds);
        for (NoeudBase noeud : copieNoeuds) {
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
        
        java.util.List<NoeudBase> copieNoeuds = new java.util.ArrayList<>(blueprintActif.noeuds);
        for (NoeudBase noeud : copieNoeuds) {
            if (noeud instanceof NoeudEventVariableChange) {
                ((NoeudEventVariableChange) noeud).verifierEtDeclencher();
            }
        }
    }
}
// bas 1
                        
