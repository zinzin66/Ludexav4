// haut 4
package com.ludexa.moteur;

import java.util.Arrays;

public class NoeudActionFiltre extends NoeudBase {

    public NoeudActionFiltre() {
        super(genererId(), "Filtre Couleur", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametreListe("Filtre", "Noir et Blanc", Arrays.asList("Aucun", "Noir et Blanc", "Sepia", "Inversion"));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.filtreCouleur = getValeurParametre("Filtre");
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
}
// bas 4
