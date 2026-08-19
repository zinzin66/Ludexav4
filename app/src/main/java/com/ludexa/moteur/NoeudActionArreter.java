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
            // Remise à zéro de tous les vecteurs de mouvement de Yop2D
            cible.vitesseAvanceContinue = 0f;
            cible.vitessePoursuite = 0f;
            cible.intentionDeplacementX = 0f;
            cible.intentionDeplacementY = 0f;
            cible.vitesseY = 0f; // Stoppe la chute physique
            cible.sautillementActif = false; // Stoppe l'animation de saut
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
