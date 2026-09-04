// haut 1 ggg
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

            /*
             * IMPORTANT :
             *
             * Avant, l'animation était remise à la frame 0 à chaque
             * exécution du nœud.
             *
             * Comme le joystick appelle ce nœud à chaque image,
             * l'animation redémarrait constamment et n'avait pas
             * le temps d'avancer.
             *
             * Maintenant, on réinitialise uniquement si l'animation
             * demandée est différente de celle déjà active.
             */
            boolean nouvelleAnimation =
                    obj.animationActive == null
                    || !nomAnimation.equals(obj.animationActive);

            if (nouvelleAnimation) {
                obj.animationActive = nomAnimation;
                obj.frameCourante = 0;
                obj.dernierTempsFrame = System.currentTimeMillis();
            }

            /*
             * La vitesse et le mode boucle peuvent continuer à être
             * actualisés à chaque appel sans redémarrer l'animation.
             */
            try {
                obj.vitesseFps = Integer.parseInt(vitesse);

                if (obj.vitesseFps <= 0) {
                    obj.vitesseFps = 8;
                }

            } catch (Exception e) {
                obj.vitesseFps = 8;
            }

            obj.boucleAnimation =
                    boucle.equalsIgnoreCase("true")
                    || boucle.equalsIgnoreCase("oui");

            obj.animationEnCours = true;
        }

        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList(
                "Nom Animation",
                "Vitesse (FPS)",
                "Boucle (true/false)"
        );
    }

    @Override
    public String getValeurParametre(String nom) {

        if ("Nom Animation".equals(nom)) {
            return nomAnimation;
        }

        if ("Vitesse (FPS)".equals(nom)) {
            return vitesse;
        }

        if ("Boucle (true/false)".equals(nom)) {
            return boucle;
        }

        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {

        if ("Nom Animation".equals(nom)) {
            nomAnimation = valeur;
        }

        if ("Vitesse (FPS)".equals(nom)) {
            vitesse = valeur;
        }

        if ("Boucle (true/false)".equals(nom)) {
            boucle = valeur;
        }
    }

    @Override
    public String getTypeEditeurParametre(String nom) {

        if ("Nom Animation".equals(nom)) {
            return "CHOIX_ANIMATION";
        }

        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public boolean requiertCibleObjet() {
        return true;
    }

    @Override
    public void setCibleObjet(ObjetBase objet) {

        this.cible = objet;

        /*
         * On utilise le champ nomCibleObjet hérité de NoeudBase.
         * Cela permet à VueJeu de relier correctement chaque clone
         * du nœud à l'instance réelle de l'objet.
         */
        if (objet != null) {
            this.nomCibleObjet = objet.nom;

        } else if (!"__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            this.nomCibleObjet = null;
        }
    }

    /*
     * Résolution de la cible :
     *
     * 1. Instance directe résolue lors de l'instanciation du clone
     * 2. Objet impliqué
     * 3. Recherche dans la scène active
     * 4. Recherche dans le HUD
     * 5. Référence locale de secours
     */
    @Override
    public ObjetBase getCibleObjet() {

        if (cibleObjetResolue != null) {
            return cibleObjetResolue;
        }

        if ("__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }

        if (this.nomCibleObjet != null) {

            if (NoeudBase.sceneActiveCourante != null
                    && NoeudBase.sceneActiveCourante.objets != null) {

                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {

                    if (this.nomCibleObjet.equals(o.nom)) {
                        return o;
                    }
                }
            }

            if (NoeudBase.sceneHudActiveCourante != null
                    && NoeudBase.sceneHudActiveCourante.objets != null) {

                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {

                    if (this.nomCibleObjet.equals(o.nom)) {
                        return o;
                    }
                }
            }
        }

        return this.cible;
    }

    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}

// bas 1
