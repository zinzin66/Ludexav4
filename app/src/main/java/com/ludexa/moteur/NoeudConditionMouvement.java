// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudConditionMouvement extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudConditionMouvement() {
        super(genererId(), Traducteur.get("noeud_si_mouvement"), Traducteur.get("cat_logique_conditions"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_vrai"), Port.TYPE_EXECUTION_SORTIE));
        ajouterPort(new Port(Traducteur.get("port_faux"), Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        boolean enMouvement = false;
        
        if (cible != null) {
            // Compare la position actuelle avec celle de la frame précédente
            if (Math.abs(cible.x - cible.ancienneX) > 0.5f || Math.abs(cible.y - cible.ancienneY) > 0.5f) {
                enMouvement = true;
            }
        }
        
        if (enMouvement) {
            propagerExecution(Traducteur.get("port_vrai"));
        } else {
            propagerExecution(Traducteur.get("port_faux"));
        }
    }

    @Override
    public List<String> getNomsParametres() { return null; }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { cibleObj = objet; }

    @Override
    public ObjetBase getCibleObjet() { return cibleObj; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; }
}
// bas 1
