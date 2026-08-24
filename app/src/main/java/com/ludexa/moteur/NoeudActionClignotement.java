package com.ludexa.moteur;

public class NoeudActionClignotement extends NoeudBase {

    public NoeudActionClignotement() {
        super(genererId(), "Clignotement", "Action");
        
        // CORRECTION : On passe uniquement les deux chaînes de caractères (String, String)
        this.ajouterPort(new Port("Entrée", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleReelle = getCibleObjet();
        
        if (cibleReelle != null) {
            // Ta logique pour faire clignoter la cibleReelle ira ici
        }
        
        propagerExecution("Suivant");
    }
}
