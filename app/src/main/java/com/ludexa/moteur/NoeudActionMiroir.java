// haut 1
package com.ludexa.moteur;

import java.util.Arrays;

public class NoeudActionMiroir extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionMiroir() {
        super(genererId(), Traducteur.get("noeud_miroir"), Traducteur.get("cat_apparence_objets"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametreListe("Axe", "Horizontal", Arrays.asList("Horizontal", "Vertical"));
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            String axe = getValeurParametre("Axe");
            
            if ("Horizontal".equals(axe)) {
                // Multiplier par -1 inverse l'image horizontalement
                cible.scaleX = -cible.scaleX;
            } else if ("Vertical".equals(axe)) {
                // Multiplier par -1 inverse l'image verticalement
                cible.scaleY = -cible.scaleY;
            }
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleObj = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cibleObj; }
}
// bas 1
