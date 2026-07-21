
package com.ludexa.moteur;

public class NoeudEventStart extends NoeudBase {

    public NoeudEventStart() {
        super(genererId(), "Au Démarrage", "Événement");
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Suivant");
    }
}
