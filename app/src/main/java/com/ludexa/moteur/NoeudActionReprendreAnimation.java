// haut 1
package com.ludexa.moteur;

public class NoeudActionReprendreAnimation extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionReprendreAnimation() {
        super(genererId(), Traducteur.get("noeud_reprendre_anim"), Traducteur.get("cat_animations"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            cible.animationEnCours = true; // Relance l'animation
            cible.dernierTempsFrame = System.currentTimeMillis(); // Empêche de sauter une image à la reprise
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
