// haut 1 ggg
package com.ludexa.moteur;

public class NoeudActionArreterAnimation extends NoeudBase {

    private transient ObjetBase cible;

    public NoeudActionArreterAnimation() {
        super(
                genererId(),
                Traducteur.get("noeud_arreter_anim"),
                "Animations"
        );

        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Sortir", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {

        /*
         * On utilise getCibleObjet() plutôt que directement le champ
         * cible afin de récupérer la même instance que le nœud
         * Jouer Animation, notamment pour les clones de scène.
         */
        ObjetBase obj = getCibleObjet();

        if (obj != null) {

            // Arrêt de la lecture de l'animation
            obj.animationEnCours = false;

            // Retour à la première image
            obj.frameCourante = 0;

            /*
             * On efface également le nom de l'animation active.
             * Ainsi, lorsque Jouer Animation sera rappelé, il
             * considérera correctement cela comme un nouveau départ.
             */
            obj.animationActive = null;

            // Valeur de sécurité pour le prochain démarrage
            obj.dernierTempsFrame = 0;
        }

        propagerExecution("Sortir");
    }

    @Override
    public boolean requiertCibleObjet() {
        return true;
    }

    @Override
    public void setCibleObjet(ObjetBase objet) {

        this.cible = objet;

        /*
         * Même mécanisme de liaison que pour Jouer Animation.
         * Le champ nomCibleObjet est celui utilisé par NoeudBase
         * et par le système de liaison des clones.
         */
        if (objet != null) {
            this.nomCibleObjet = objet.nom;

        } else if (!"__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            this.nomCibleObjet = null;
        }
    }

    @Override
    public ObjetBase getCibleObjet() {

        /*
         * Priorité à la référence directe du clone.
         */
        if (cibleObjetResolue != null) {
            return cibleObjetResolue;
        }

        /*
         * Gestion de l'objet impliqué.
         */
        if ("__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }

        /*
         * Recherche par nom dans la scène active.
         */
        if (this.nomCibleObjet != null) {

            if (NoeudBase.sceneActiveCourante != null
                    && NoeudBase.sceneActiveCourante.objets != null) {

                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {

                    if (this.nomCibleObjet.equals(o.nom)) {
                        return o;
                    }
                }
            }

            /*
             * Recherche par nom dans le HUD.
             */
            if (NoeudBase.sceneHudActiveCourante != null
                    && NoeudBase.sceneHudActiveCourante.objets != null) {

                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {

                    if (this.nomCibleObjet.equals(o.nom)) {
                        return o;
                    }
                }
            }
        }

        /*
         * Référence locale de secours.
         */
        return this.cible;
    }

    @Override
    public boolean utiliseClavierTexte() {
        return false;
    }
}

// bas 1
