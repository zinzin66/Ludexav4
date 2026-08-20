package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionPousser extends NoeudBase {

    private ObjetBase cible;
    private float ajoutX;
    private float ajoutY;

    public NoeudActionPousser() {
        super(genererId(), "Pousser Objet", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    public NoeudActionPousser(ObjetBase cible, float ajoutX, float ajoutY) {
        this(); 
        this.cible = cible;
        this.ajoutX = ajoutX;
        this.ajoutY = ajoutY;
    }

    @Override
    public void executer() {
        if (cible != null) {
            // On ajoute la valeur à l'intention au lieu de forcer la position
            cible.intentionDeplacementX += ajoutX;
            cible.intentionDeplacementY += ajoutY;
        }
        propagerExecution("Suivant");
    }
    
    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Ajout X", "Ajout Y");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Ajout X".equals(nom)) return String.valueOf(ajoutX);
        if ("Ajout Y".equals(nom)) return String.valueOf(ajoutY);
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        try {
            if ("Ajout X".equals(nom)) ajoutX = Float.parseFloat(valeur);
            if ("Ajout Y".equals(nom)) ajoutY = Float.parseFloat(valeur);
        } catch (NumberFormatException e) {
            // Ignorer si la saisie est en cours ou invalide
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

    @Override
    public boolean utiliseClavierTexte() {
        // Retourner false affichera ton clavier numérique personnalisé dans l'éditeur
        return false;
    }
}
