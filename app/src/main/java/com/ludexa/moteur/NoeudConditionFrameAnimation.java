// haut 1
package com.ludexa.moteur;

public class NoeudConditionFrameAnimation extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudConditionFrameAnimation() {
        super(genererId(), Traducteur.get("noeud_condition_frame"), Traducteur.get("cat_animations"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        // Ce noeud est une condition, il a donc deux sorties : Vrai et Faux
        ajouterPort(new Port(Traducteur.get("port_vrai"), Port.TYPE_EXECUTION_SORTIE));
        ajouterPort(new Port(Traducteur.get("port_faux"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Image numéro", "0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        boolean resultat = false;
        
        if (cible != null) {
            try {
                int frameCible = Integer.parseInt(getValeurParametre("Image numéro"));
                // On vérifie si la frame de l'objet correspond à la cible
                resultat = (cible.frameCourante == frameCible);
            } catch (Exception e) {}
        }
        
        if (resultat) {
            propagerExecution(Traducteur.get("port_vrai"));
        } else {
            propagerExecution(Traducteur.get("port_faux"));
        }
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleObj = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cibleObj; }
}
// bas 1

