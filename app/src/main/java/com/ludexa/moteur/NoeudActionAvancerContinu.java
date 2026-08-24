// haut 1
package com.ludexa.moteur;

public class NoeudActionAvancerContinu extends NoeudBase {

    public NoeudActionAvancerContinu() {
        super(genererId(), "Avancer en continu", "Mouvements & IA");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Vitesse", "5.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            try {
                obj.vitesseAvanceContinue = Float.parseFloat(getValeurParametre("Vitesse"));
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
