// haut 3
package com.ludexa.moteur;

public class NoeudActionRotation extends NoeudBase {

    public NoeudActionRotation() {
        super(genererId(), "Rotation", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Angle (degrés)", "90", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            try {
                obj.rotation = Float.parseFloat(getValeurParametre("Angle (degrés)"));
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 3
