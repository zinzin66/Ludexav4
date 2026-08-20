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
            Scene s = null;
            try {
                // 1. On récupère la scène active
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                s = (Scene) sceneField.get(contexteApplication);
            } catch (Exception e) {}

            if (s != null) {
                try {
                    // 2. On cherche le champ "VueJeu" dans l'Activity proprement
                    for (java.lang.reflect.Field field : contexteApplication.getClass().getDeclaredFields()) {
                        if (field.getType() == VueJeu.class) {
                            field.setAccessible(true);
                            VueJeu vue = (VueJeu) field.get(contexteApplication);
                            if (vue != null) {
                                vue.chargerNouvelleScene(s);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {}
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
