// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionSiJoystick extends NoeudBase {

    private ObjetBase cible;
    private float vitesse = 5f;

    public NoeudConditionSiJoystick() {
        super(genererId(), "Si Joystick Actif", "Condition");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (cible != null && (GestionnaireControles.joyDirX != 0 || GestionnaireControles.joyDirY != 0)) {
            cible.x += GestionnaireControles.joyDirX * vitesse;
            cible.y += GestionnaireControles.joyDirY * vitesse;
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Vitesse");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Vitesse".equals(nom)) return String.valueOf(vitesse);
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        try {
            if ("Vitesse".equals(nom)) vitesse = Float.parseFloat(valeur);
        } catch (NumberFormatException e) {}
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { this.cible = objet; }

    @Override
    public ObjetBase getCibleObjet() { return this.cible; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
