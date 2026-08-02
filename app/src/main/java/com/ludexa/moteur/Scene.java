// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scene {
    public String id;
    public String nom;
    public List<ObjetBase> objets;
    public List<NoeudBase> noeudsLogique;
    public List<Variable> variablesLocales;

    public Scene(String nom) {
        this.id = java.util.UUID.randomUUID().toString();
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
        
        // Le clone doit garder le même id que l'original (Sandbox Play)
        copie.id = this.id;
        
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



