// haut 1
package com.ludexa.moteur;

public class NoeudActionTremblement extends NoeudBase {

    public NoeudActionTremblement() {
        super(genererId(), "Tremblement de Caméra", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Intensité", "10.0", TYPE_NOMBRE);
        this.ajouterParametre("Durée (ms)", "500", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        try {
            VueJeu.tremblementIntensite = Float.parseFloat(getValeurParametre("Intensité"));
            VueJeu.tremblementFin = System.currentTimeMillis() + Long.parseLong(getValeurParametre("Durée (ms)"));
        } catch (Exception e) {}
        
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
