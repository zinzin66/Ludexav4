// haut 1
package com.ludexa.moteur;

public class NoeudActionArreterAnimation extends NoeudBase {

    private ObjetBase cible;

    public NoeudActionArreterAnimation() {
        super(genererId(), Traducteur.get("noeud_arreter_anim"), "Animations");
        
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Sortir", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (cible != null) {
            // On stoppe la lecture de l'animation
            cible.animationEnCours = false;
            // On remet la frame à 0 pour revenir à l'image de base
            cible.frameCourante = 0; 
        }
        propagerExecution("Sortir");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { this.cible = objet; }

    @Override
    public ObjetBase getCibleObjet() { return this.cible; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; }
}
// bas 1
