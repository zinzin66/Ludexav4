package com.ludexa.moteur;

import java.util.List;

public class NoeudActionChangerScene extends NoeudBase {

    private transient Scene cible;
    private String nomCibleScene;

    public NoeudActionChangerScene() {
        super(genererId(), "Changer de Scène", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        // Pas de port "Suivant" car changer de scène arrête l'exécution de la scène courante !
    }

    @Override
    public void executer() {
        Scene sceneCible = getCibleScene();
        if (sceneCible != null && contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            VueJeu vue = editeur.getVueJeu();
            if (vue != null) {
                // TODO: Nous connecterons ceci à la méthode de changement de scène dans VueJeu plus tard
                // vue.chargerNouvelleScene(sceneCible);
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
        // Reconnexion dynamique de la scène
        if (cible == null && nomCibleScene != null && contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            if (editeur.listeScenes != null) {
                for (Scene s : editeur.listeScenes) {
                    if (nomCibleScene.equals(s.nom)) {
                        cible = s;
                        break;
                    }
                }
            }
        }
        return cible;
    }

    // Paramètres vides car la cible est la Scène elle-même
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
