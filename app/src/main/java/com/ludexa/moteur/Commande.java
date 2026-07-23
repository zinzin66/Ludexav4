// haut 1
package com.ludexa.moteur;

public interface Commande {
    void executer();
    void annuler();
}

class CommandeDeplacement implements Commande {
    private ObjetBase objet;
    private float ancienX, ancienY;
    private float nouveauX, nouveauY;

    public CommandeDeplacement(ObjetBase objet, float ancienX, float ancienY, float nouveauX, float nouveauY) {
        this.objet = objet;
        this.ancienX = ancienX;
        this.ancienY = ancienY;
        this.nouveauX = nouveauX;
        this.nouveauY = nouveauY;
    }

    @Override
    public void executer() {
        objet.x = nouveauX;
        objet.y = nouveauY;
    }

    @Override
    public void annuler() {
        objet.x = ancienX;
        objet.y = ancienY;
    }
}
// bas 1
