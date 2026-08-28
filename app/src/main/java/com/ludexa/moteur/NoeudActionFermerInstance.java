// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionFermerInstance extends NoeudBase {

    private Scene sceneCible;

    public NoeudActionFermerInstance() {
        super(genererId(), "noeud_fermer_instance", "cat_scene_hud");
        this.ajouterPort(new Port("Entrée", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public boolean requiertCibleScene() {
        return true;
    }

    @Override
    public Scene getCibleScene() {
        return sceneCible;
    }

    @Override
    public void setCibleScene(Scene scene) {
        this.sceneCible = scene;
    }

    @Override
    public List<String> getNomsParametres() {
        return new ArrayList<>();
    }

    @Override
    public String getValeurParametre(String nom) {
        return null;
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
    }

    @Override
    public void executer() {
        if (contexteApplication instanceof InterfaceEditeur) {
            // La fermeture/destruction de l'instance sera implémentée ici
        } else if (contexteApplication instanceof RunnerActivity) {
            // La fermeture/destruction de l'instance sera implémentée ici
        }
        propagerExecution("Sortie");
    }
}
// bas 1
