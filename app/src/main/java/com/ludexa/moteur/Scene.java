package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    public String nom;
    public List<ObjetBase> objets;
    public List<NoeudBase> noeudsLogique;
    public List<Variable> variablesLocales; // NOUVEAU: Stockage des variables locales

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
}
