// haut 1
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
// bas 1
