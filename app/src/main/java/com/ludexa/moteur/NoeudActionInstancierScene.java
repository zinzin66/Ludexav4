// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionInstancierScene extends NoeudBase {

    private Scene sceneCible;

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
    public boolean requiertCibleObjet() {
        return true; // Active automatiquement le sélecteur d'objet dans l'éditeur
    }

    @Override
    public boolean aDesParametresEditables() {
        return false; // Les paramètres X et Y manuels sont supprimés
    }

    @Override
    public List<String> getNomsParametres() {
        return new ArrayList<>();
    }

    @Override
    public String getValeurParametre(String nom) {
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_TEXTE_LIBRE;
    }

    @Override
    public void executer() {
        if (sceneCible != null) {
            float xVal = 0f;
            float yVal = 0f;
            
            // Résolution dynamique de la cible
            ObjetBase cible = getCibleObjet();
            if (cible == null && "__OBJET_IMPLIQUE__".equals(nomCibleObjet)) {
                cible = MoteurLogique.dernierObjetImplique;
            }
            
            // Récupération des coordonnées en temps réel
            if (cible != null) {
                xVal = cible.x;
                yVal = cible.y;
            }

            if (contexteApplication instanceof InterfaceEditeur) {
                ((InterfaceEditeur) contexteApplication).getVueJeu().instancierScene(sceneCible, xVal, yVal);
            } else if (contexteApplication instanceof RunnerActivity) {
                ((RunnerActivity) contexteApplication).getVueJeu().instancierScene(sceneCible, xVal, yVal);
            }
        }
        propagerExecution("Sortie");
    }
}
// bas 1
