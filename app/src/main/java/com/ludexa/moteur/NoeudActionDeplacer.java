package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionDeplacer extends NoeudBase {

    private ObjetBase cible;
    private float deplacementX;
    private float deplacementY;

    public NoeudActionDeplacer() {
        super(genererId(), "Déplacer Objet", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        // Les ports X et Y ont été retirés ici
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    public NoeudActionDeplacer(ObjetBase cible, float deplacementX, float deplacementY) {
        this(); 
        this.cible = cible;
        this.deplacementX = deplacementX;
        this.deplacementY = deplacementY;
    }

    @Override
    public void executer() {
        if (cible != null) {
            cible.x += deplacementX;
            cible.y += deplacementY;
        }
        propagerExecution("Suivant");
    }

    // --- IMPLEMENTATION DES PARAMETRES POUR L'EDITEUR ---
    
    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("X", "Y");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("X".equals(nom)) return String.valueOf(deplacementX);
        if ("Y".equals(nom)) return String.valueOf(deplacementY);
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        try {
            if ("X".equals(nom)) deplacementX = Float.parseFloat(valeur);
            if ("Y".equals(nom)) deplacementY = Float.parseFloat(valeur);
        } catch (NumberFormatException e) {
            // La saisie peut être temporairement vide ou inclure seulement un ".", on ignore l'erreur
        }
    }

    @Override
    public boolean requiertCibleObjet() {
        return true; 
    }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
    }

    @Override
    public ObjetBase getCibleObjet() {
        return this.cible;
    }
}
