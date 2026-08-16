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
            try {
                if (contexteApplication instanceof VueJeu) {
                    VueJeu vue = (VueJeu) contexteApplication;
                    java.lang.reflect.Field sceneField = VueJeu.class.getDeclaredField("sceneActive");
                    sceneField.setAccessible(true);
                    Scene s = (Scene) sceneField.get(vue);
                    if (s != null) {
                        vue.chargerNouvelleScene(s);
                    }
                }
            } catch (Exception e) {}
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
