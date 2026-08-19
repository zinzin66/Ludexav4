// haut 1
package com.ludexa.moteur;

public class NoeudActionLierObjets extends NoeudBase {
    private ObjetBase enfantObj;
    private ObjetBase parentObj;

    public NoeudActionLierObjets() {
        super(genererId(), Traducteur.get("noeud_lier_objets"), Traducteur.get("cat_apparence_objets"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase enfant = getCibleObjet(); // L'objet à lier
        ObjetBase parent = getCibleObjetB(); // L'objet de référence (le parent)
        
        if (enfant != null && parent != null) {
            // On s'assure qu'un objet ne devient pas parent de lui-même
            if (!enfant.id.equals(parent.id)) {
                enfant.parentId = parent.id;
            }
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; } // Cible A = Enfant
    @Override
    public void setCibleObjet(ObjetBase objet) { this.enfantObj = objet; }
    @Override
    public ObjetBase getCibleObjet() { return this.enfantObj; }
    
    @Override
    public boolean requiertCibleObjetB() { return true; } // Cible B = Parent
    @Override
    public void setCibleObjetB(ObjetBase objet) { this.parentObj = objet; }
    @Override
    public ObjetBase getCibleObjetB() { return this.parentObj; }
}
// bas 1
