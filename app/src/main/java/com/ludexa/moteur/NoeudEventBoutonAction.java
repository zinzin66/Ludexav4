// haut 1
package com.ludexa.moteur;

public class NoeudEventBoutonAction extends NoeudBase {
    public NoeudEventBoutonAction() {
        super(genererId(), "Au Clic Action", "Evenement");
        this.ajouterPort(new Port("Executer", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Executer");
    }
}
// bas 1

