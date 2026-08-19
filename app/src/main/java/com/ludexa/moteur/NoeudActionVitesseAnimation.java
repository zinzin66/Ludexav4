// haut 1
package com.ludexa.moteur;

public class NoeudActionVitesseAnimation extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionVitesseAnimation() {
        super(genererId(), Traducteur.get("noeud_vitesse_anim"), Traducteur.get("cat_animations"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Vitesse (FPS)", "8", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            try {
                cible.vitesseFps = Integer.parseInt(getValeurParametre("Vitesse (FPS)"));
            } catch (Exception e) {}
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

