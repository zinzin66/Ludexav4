// haut 1
package com.ludexa.moteur;

public class NoeudActionOpacite extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionOpacite() {
        super(genererId(), Traducteur.get("noeud_opacite"), Traducteur.get("cat_apparence_objets"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Niveau (0.0 a 1.0)", "1.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            try {
                float val = Float.parseFloat(getValeurParametre("Niveau (0.0 a 1.0)"));
                // On s'assure que la valeur reste strictement comprise entre 0.0 (invisible) et 1.0 (opaque)
                cible.alpha = Math.max(0f, Math.min(1f, val));
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

