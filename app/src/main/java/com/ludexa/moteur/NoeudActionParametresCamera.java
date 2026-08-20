// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionParametresCamera extends NoeudBase {

    private String vitesseStr = "0.1";

    public NoeudActionParametresCamera() {
        super(genererId(), "Élasticité Caméra", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        try {
            float v = Float.parseFloat(vitesseStr);
            if (v < 0.001f) v = 0.001f;
            if (v > 1.0f) v = 1.0f;
            VueJeu.vitesseSuiviCamera = v;
        } catch (Exception e) {}
        
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Vitesse Suivi (0.01 à 1.0)");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Vitesse Suivi (0.01 à 1.0)".equals(nom)) return vitesseStr;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Vitesse Suivi (0.01 à 1.0)".equals(nom)) vitesseStr = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_NOMBRE;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
