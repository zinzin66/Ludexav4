// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionChangerScene extends NoeudBase {

    private transient Scene cible;
    private String nomCibleScene;

    public NoeudActionChangerScene() {
        super(genererId(), "Changer de Scène", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
    }

    @Override
    public void executer() {
        Scene sceneCible = getCibleScene();
        if (sceneCible != null) {
            if (contexteApplication instanceof InterfaceEditeur) {
                VueJeu vue = ((InterfaceEditeur) contexteApplication).getVueJeu();
                if (vue != null) vue.chargerNouvelleScene(sceneCible);
            } else if (contexteApplication instanceof RunnerActivity) {
                // NOUVEAU : Changement de décor dans l'APK !
                VueJeu vue = ((RunnerActivity) contexteApplication).getVueJeu();
                if (vue != null) vue.chargerNouvelleScene(sceneCible);
            }
        }
    }

    @Override
    public boolean requiertCibleScene() { return true; }
    
    @Override
    public void setCibleScene(Scene s) {
        this.cible = s;
        this.nomCibleScene = (s != null) ? s.nom : null;
    }

    @Override
    public Scene getCibleScene() {
        if (cible == null && nomCibleScene != null) {
            List<Scene> scenesPossibles = null;
            
            if (contexteApplication instanceof InterfaceEditeur) {
                scenesPossibles = ((InterfaceEditeur) contexteApplication).listeScenes;
            } else if (contexteApplication instanceof RunnerActivity) {
                scenesPossibles = ((RunnerActivity) contexteApplication).listeScenes;
            }
            
            if (scenesPossibles != null) {
                for (Scene s : scenesPossibles) {
                    if (nomCibleScene.equals(s.nom)) {
                        cible = s;
                        break;
                    }
                }
            }
        }
        return cible;
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
