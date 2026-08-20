// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionOuvrirHUD extends NoeudBase {
    private Scene sceneCible;

    public NoeudActionOuvrirHUD() {
        super(genererId(), "Ouvrir HUD", "UI");
        this.ajouterPort(new Port("Entrée", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication instanceof InterfaceEditeur) {
            ((InterfaceEditeur) contexteApplication).ouvrirHUD(sceneCible);
        } else if (contexteApplication instanceof RunnerActivity) {
            // NOUVEAU : Le HUD s'ouvre aussi dans le jeu exporté !
            ((RunnerActivity) contexteApplication).ouvrirHUD(sceneCible);
        }
        propagerExecution("Sortie");
    }

    @Override
    public boolean requiertCibleScene() { return true; }

    @Override
    public void setCibleScene(Scene s) { this.sceneCible = s; }

    @Override
    public Scene getCibleScene() { return this.sceneCible; }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }

    @Override
    public String getValeurParametre(String nom) { return null; }

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
