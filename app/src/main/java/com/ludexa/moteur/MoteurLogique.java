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
}
// bas 1
