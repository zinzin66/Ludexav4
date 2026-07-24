// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionDeplacer extends NoeudBase {

    private ObjetBase cible;
    private float positionX; // Renommé pour plus de clarté
    private float positionY; // Renommé pour plus de clarté

    public NoeudActionDeplacer() {
        super(genererId(), "Déplacer Objet", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    public NoeudActionDeplacer(ObjetBase cible, float positionX, float positionY) {
        this(); 
        this.cible = cible;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    @Override
    public void executer() {
        if (cible != null) {
            // CORRECTION : Assignation directe (=) au lieu d'une addition (+=)
            // L'objet se déplace désormais vers les coordonnées exactes
            cible.x = positionX;
            cible.y = positionY;
        }
        propagerExecution("Suivant");
    }
    
    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("X", "Y");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("X".equals(nom)) return String.valueOf(positionX);
        if ("Y".equals(nom)) return String.valueOf(positionY);
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        try {
            if ("X".equals(nom)) positionX = Float.parseFloat(valeur);
            if ("Y".equals(nom)) positionY = Float.parseFloat(valeur);
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
