// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionInstancierScene extends NoeudBase {

    private Scene sceneCible;
    private String paramX = "0";
    private String paramY = "0";

    public NoeudActionInstancierScene() {
        super(genererId(), "noeud_instancier_scene", "cat_scene_hud");
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
    public boolean aDesParametresEditables() {
        return true;
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("param_x", "param_y");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("param_x".equals(nom)) return paramX;
        if ("param_y".equals(nom)) return paramY;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("param_x".equals(nom)) paramX = valeur;
        if ("param_y".equals(nom)) paramY = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_TEXTE_LIBRE;
    }

    @Override
    public void executer() {
        if (contexteApplication instanceof InterfaceEditeur) {
            // L'instanciation de la scène sera implémentée ici
        } else if (contexteApplication instanceof RunnerActivity) {
            // L'instanciation de la scène sera implémentée ici
        }
        propagerExecution("Sortie");
    }
}
// bas 1
