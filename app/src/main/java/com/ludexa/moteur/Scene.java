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
    
    private int compteurZOrderLocal = 0;

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

    public int prochainZOrder() {
        return compteurZOrderLocal++;
    }

    public void resynchroniserCompteurZOrder(int min) {
        if (min > compteurZOrderLocal) {
            compteurZOrderLocal = min;
        }
    }

    public Scene clonerProfond() {
        Scene copie = new Scene(this.nom);
        
        copie.id = this.id;
        copie.compteurZOrderLocal = this.compteurZOrderLocal;
        
        for (ObjetBase obj : this.objets) {
            copie.ajouterObjet(obj.clonerProfond());
        }
        
        for (Variable var : this.variablesLocales) {
            copie.variablesLocales.add(var.clonerProfond());
        }
        
        copie.noeudsLogique.addAll(this.noeudsLogique);
        
        return copie;
    }
}
// bas 1
