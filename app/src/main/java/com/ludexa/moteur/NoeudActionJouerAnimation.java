// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionJouerAnimation extends NoeudBase {

    private transient ObjetBase cible;
    private String nomAnimation = "Ouverture";
    private String vitesse = "8";
    private String boucle = "false";

    public NoeudActionJouerAnimation() {
        super(genererId(), "Jouer Animation", "Animations");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.animationActive = nomAnimation;
            obj.frameCourante = 0;
            obj.dernierTempsFrame = System.currentTimeMillis();
            try {
                obj.vitesseFps = Integer.parseInt(vitesse);
            } catch (Exception e) {
                obj.vitesseFps = 8;
            }
            obj.boucleAnimation = boucle.equalsIgnoreCase("true") || boucle.equalsIgnoreCase("oui");
            obj.animationEnCours = true;
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Nom Animation", "Vitesse (FPS)", "Boucle (true/false)"); }
    
    @Override
    public String getValeurParametre(String nom) {
        if ("Nom Animation".equals(nom)) return nomAnimation;
        if ("Vitesse (FPS)".equals(nom)) return vitesse;
        if ("Boucle (true/false)".equals(nom)) return boucle;
        return "";
    }
    
    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Nom Animation".equals(nom)) nomAnimation = valeur;
        if ("Vitesse (FPS)".equals(nom)) vitesse = valeur;
        if ("Boucle (true/false)".equals(nom)) boucle = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Nom Animation".equals(nom)) {
            return "CHOIX_ANIMATION";
        }
        return super.getTypeEditeurParametre(nom);
    }
    
    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        // CORRECTION : on écrit désormais le champ nomCibleObjet HÉRITÉ de NoeudBase
        // (public), et non plus un champ privé du même nom qui le masquait silencieusement.
        // C'est ce champ public que VueJeu.instancierSceneInterne lit pour lier
        // correctement chaque clone à l'exécution (lierCibleObjetInstance).
        // L'ancien champ privé faisait que ce nœud échappait totalement au système
        // de liaison directe par référence, et retombait sur une résolution par nom
        // fragile, dépendante de l'état de sceneActive au moment précis de l'exécution.
        if (objet != null) {
            this.nomCibleObjet = objet.nom;
        } else if (!"__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            this.nomCibleObjet = null;
        }
    }
    
    // SIMPLIFIÉ : référence directe d'instance en priorité (posée par VueJeu à l'instanciation),
    // puis __OBJET_IMPLIQUE__, puis résolution par nom via les scènes courantes, puis fallback
    // sur le champ local. Toute la réflexion agressive sur contexteApplication a été supprimée.
    @Override
    public ObjetBase getCibleObjet() {
        if (cibleObjetResolue != null) return cibleObjetResolue;

        if ("__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }

        if (this.nomCibleObjet != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (this.nomCibleObjet.equals(o.nom)) return o;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {
                    if (this.nomCibleObjet.equals(o.nom)) return o;
                }
            }
        }
        return this.cible;
    }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
