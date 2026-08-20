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
        ObjetBase enfant = getCibleObjet(); 
        ObjetBase parent = getCibleObjetB(); 
        
        if (enfant != null && parent != null) {
            if (!enfant.id.equals(parent.id)) {
                enfant.parentId = parent.id;
            }
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; } 
    @Override
    public void setCibleObjet(ObjetBase objet) { this.enfantObj = objet; }
    @Override
    public ObjetBase getCibleObjet() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;
        return this.enfantObj; 
    }
    
    @Override
    public boolean requiertCibleObjetB() { return true; } 
    @Override
    public void setCibleObjetB(ObjetBase objet) { this.parentObj = objet; }
    @Override
    public ObjetBase getCibleObjetB() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjetB)) return MoteurLogique.dernierObjetImplique;
        return this.parentObj; 
    }
}
// bas 1
