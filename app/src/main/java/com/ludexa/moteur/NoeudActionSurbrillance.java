// haut 2
package com.ludexa.moteur;

import java.util.Arrays;

public class NoeudActionSurbrillance extends NoeudBase {

    public NoeudActionSurbrillance() {
        super(genererId(), "Surbrillance (Glow)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametreListe("État", "Activer", Arrays.asList("Activer", "Désactiver"));
        this.ajouterParametre("Couleur", "Jaune", TYPE_COULEUR);
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.surbrillanceActive = "Activer".equals(getValeurParametre("État"));
            obj.couleurSurbrillance = getValeurParametre("Couleur");
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
}
// bas 2
