// haut 1
package com.ludexa.moteur;

public class NoeudActionPoursuivre extends NoeudBase {

    public NoeudActionPoursuivre() {
        super(genererId(), "Poursuivre un objet", "Mouvements & IA");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Vitesse", "5.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase objA = getCibleObjet();
        ObjetBase objB = getCibleObjetB();

        if (objA != null && objB != null) {
            objA.idCiblePoursuite = objB.id;
            objA.fuiteActive = false;
            try {
                objA.vitessePoursuite = Float.parseFloat(getValeurParametre("Vitesse"));
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public boolean requiertCibleObjetB() { return true; }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
