// haut 1
package com.ludexa.moteur;

public class NoeudActionArreter extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionArreter() {
        super(genererId(), Traducteur.get("noeud_arreter"), Traducteur.get("cat_mouvements_ia"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            cible.vitesseAvanceContinue = 0f;
            cible.vitessePoursuite = 0f;
            cible.intentionDeplacementX = 0f;
            cible.intentionDeplacementY = 0f;
            cible.vitesseY = 0f; 
            cible.sautillementActif = false; 
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
