// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionRechargerScene extends NoeudBase {

    public NoeudActionRechargerScene() {
        super(genererId(), "Recharger Scène actuelle", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            if (contexteApplication instanceof InterfaceEditeur) {
                InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                if (editeur.sceneActive != null && editeur.getVueJeu() != null) {
                    editeur.getVueJeu().chargerNouvelleScene(editeur.sceneActive);
                }
            } else if (contexteApplication instanceof RunnerActivity) {
                RunnerActivity runner = (RunnerActivity) contexteApplication;
                if (runner.sceneActive != null && runner.getVueJeu() != null) {
                    runner.getVueJeu().chargerNouvelleScene(runner.sceneActive);
                }
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return null; }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
