// haut 1
package com.ludexa.moteur;

public class MoteurLogique {
    private Blueprint blueprintActif;

    public MoteurLogique(Blueprint blueprint) {
        this.blueprintActif = blueprint;
    }

    public void executerDemarrage() {
        // Sécurité : on s'assure qu'un Blueprint valide est chargé
        if (blueprintActif == null || blueprintActif.noeuds == null) {
            return;
        }

        // On parcourt la liste réelle des nœuds en mémoire
        for (NoeudBase noeud : blueprintActif.noeuds) {
            // Si on trouve un nœud de type "Au Démarrage", on lance son exécution
            if (noeud instanceof NoeudEventStart) {
                noeud.executer();
            }
        }
    }

    public void executerEvenement(Class<? extends NoeudBase> typeEvenement) {
        // Sécurité : on s'assure qu'un Blueprint valide est chargé
        if (blueprintActif == null || blueprintActif.noeuds == null) {
            return;
        }

        // On parcourt la liste réelle des nœuds en mémoire
        for (NoeudBase noeud : blueprintActif.noeuds) {
            // Si le nœud correspond à la classe d'événement recherchée
            if (typeEvenement.isInstance(noeud)) {
                noeud.executer();
            }
        }
    }

    // NOUVELLE MÉTHODE
    public void executerEvenementSurObjet(Class<? extends NoeudBase> typeEvenement, ObjetBase objetTouche) {
        if (blueprintActif == null || blueprintActif.noeuds == null || objetTouche == null) {
            return;
        }
        for (NoeudBase noeud : blueprintActif.noeuds) {
            if (typeEvenement.isInstance(noeud) && noeud.requiertCibleObjet()) {
                ObjetBase cible = noeud.getCibleObjet();
                if (cible != null && cible.id.equals(objetTouche.id)) {
                    noeud.executer();
                }
            }
        }
    }
}
// bas 1
