// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    public String nom;
    public List<ObjetBase> objets;
    public List<NoeudBase> noeudsLogique;
    public List<Variable> variablesLocales;

    public Scene(String nom) {
        this.nom = nom;
        this.objets = new ArrayList<>();
        this.noeudsLogique = new ArrayList<>();
        this.variablesLocales = new ArrayList<>();
    }

    public void ajouterObjet(ObjetBase objet) {
        this.objets.add(objet);
    }

    public void ajouterNoeud(NoeudBase noeud) {
        this.noeudsLogique.add(noeud);
    }

    // NOUVEAU : Méthode de clonage profond
    public Scene clonerProfond() {
        Scene copie = new Scene(this.nom);
        
        // Clonage des objets
        for (ObjetBase obj : this.objets) {
            copie.ajouterObjet(obj.clonerProfond());
        }
        
        // Clonage des variables locales
        for (Variable var : this.variablesLocales) {
            copie.variablesLocales.add(var.clonerProfond());
        }
        
        // Copie des références des noeuds (le Blueprint reste partagé et non altéré par l'exécution)
        copie.noeudsLogique.addAll(this.noeudsLogique);
        
        return copie;
    }
}
// bas 1
