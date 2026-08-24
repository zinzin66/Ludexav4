// haut 2
package com.ludexa.moteur;

public class NoeudActionModifierTaille extends NoeudBase {

    public NoeudActionModifierTaille() {
        super(genererId(), "Définir la taille (Scale)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Scale X", "1.0", TYPE_NOMBRE);
        this.ajouterParametre("Scale Y", "1.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            try {
                obj.scaleX = Float.parseFloat(getValeurParametre("Scale X"));
                obj.scaleY = Float.parseFloat(getValeurParametre("Scale Y"));
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 2
