// haut 1
package com.ludexa.moteur;

public class NoeudActionParallaxe extends NoeudBase {

    public NoeudActionParallaxe() {
        super(genererId(), Traducteur.get("noeud_parallaxe"), Traducteur.get("cat_apparence_objets"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Facteur (ex: 0.5)", "1.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            try {
                cible.facteurParallaxe = Float.parseFloat(getValeurParametre("Facteur (ex: 0.5)"));
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
