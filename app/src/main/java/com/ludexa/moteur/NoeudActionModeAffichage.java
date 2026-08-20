// haut 1
package com.ludexa.moteur;

import java.util.Arrays;

public class NoeudActionModeAffichage extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionModeAffichage() {
        super(genererId(), Traducteur.get("noeud_mode_affichage"), Traducteur.get("cat_apparence_objets"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametreListe("Filtre", "Aucun", Arrays.asList("Aucun", "Additif", "Multiplicatif", "Ecran", "Inversion"));
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            cible.filtreCouleur = getValeurParametre("Filtre");
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleObj = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;
        return this.cibleObj; 
    }
}
// bas 1
