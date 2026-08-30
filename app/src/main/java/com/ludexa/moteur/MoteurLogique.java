// haut 1
package com.ludexa.moteur;

import java.util.List;

public class MoteurLogique {
    
    public static ObjetBase dernierObjetImplique = null;
    
    private Blueprint blueprintActif;
    private String cheminProjet; // AJOUT : nécessaire pour écrire les logs de diagnostic

    public MoteurLogique(Blueprint blueprint) {
        this.blueprintActif = blueprint;
    }

    // AJOUT : permet à VueJeu de fournir le chemin projet pour le logging de diagnostic
    public void setCheminProjet(String cheminProjet) {
        this.cheminProjet = cheminProjet;
    }

    private void logDiag(String message) {
        if (cheminProjet != null) DiagLogger.log(cheminProjet, message);
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

                    // CORRECTIF : on ne s'arrête plus au premier objet du tag trouvé en collision
                    // (l'ancien "break" masquait les autres instances du même tag). On teste
                    // TOUS les objets du tag et on déclenche un événement par objet dont l'état
                    // de collision passe de "hors contact" à "en contact", indépendamment des
                    // autres instances du même tag.
                    java.util.Set<String> idsEnCollisionCetteFrame = new java.util.HashSet<>();

                    for (ObjetBase objB : objetsContexte) {
                        if (objA != objB && objB.tag != null && cibleTag.trim().equalsIgnoreCase(objB.tag.trim())) {
                            if (UtilCollision.rectanglesSeChevauchent(objA, objetsContexte, objB, objetsContexte, vueJeu)) {
                                idsEnCollisionCetteFrame.add(objB.id);

                                logDiag("COLLISION_TAG frame: objA=" + objA.nom + " objB=" + objB.nom
                                    + " (id=" + objB.id + ") tag=" + cibleTag
                                    + " dejaEnCollision=" + noeudTag.isEnCollisionAvec(objB.id));

                                if (!noeudTag.isEnCollisionAvec(objB.id)) {
                                    noeudTag.marquerEnCollision(objB.id);
                                    MoteurLogique.dernierObjetImplique = objB;

                                    logDiag("COLLISION_TAG DECLENCHEMENT: objA=" + objA.nom
                                        + " objB=" + objB.nom + " (id=" + objB.id + ") -> executer()");

                                    noeudTag.executer();
                                }
                            }
                        }
                    }

                    // Nettoyage : tout id précédemment marqué "en collision" mais absent cette
                    // frame (sorti de contact OU détruit) repasse "hors collision".
                    java.util.Iterator<String> it = noeudTag.getObjetsEnCollisionActuels().iterator();
                    while (it.hasNext()) {
                        String idSuivi = it.next();
                        if (!idsEnCollisionCetteFrame.contains(idSuivi)) {
                            logDiag("COLLISION_TAG FIN_CONTACT: id=" + idSuivi + " tag=" + cibleTag);
                            it.remove();
                        }
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
