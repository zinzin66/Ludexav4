// haut 1
package com.ludexa.moteur;

public class NoeudActionParametresCamera extends NoeudBase {

    public NoeudActionParametresCamera() {
        super(genererId(), "Élasticité Caméra", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Vitesse Suivi (0.01 à 1.0)", "0.1", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        try {
            float v = Float.parseFloat(getValeurParametre("Vitesse Suivi (0.01 à 1.0)"));
            if (v < 0.001f) v = 0.001f;
            if (v > 1.0f) v = 1.0f;
            VueJeu.vitesseSuiviCamera = v;
        } catch (Exception e) {}
        
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
