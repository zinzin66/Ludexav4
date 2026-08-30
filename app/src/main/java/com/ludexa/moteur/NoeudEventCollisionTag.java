// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoeudEventCollisionTag extends NoeudBase {
    
    private transient ObjetBase cible;
    private String tagCible = "";
    private transient Set<String> objetsEnCollision = new HashSet<>();

    public NoeudEventCollisionTag() {
        super(genererId(), "Si objet touche Tag", "Événements");
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Sortie");
    }

    @Override
    public List<String> getNomsParametres() {
        List<String> params = new ArrayList<>();
        params.add("Tag");
        return params;
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Tag".equals(nom)) return tagCible;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Tag".equals(nom)) this.tagCible = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Tag".equals(nom)) return "TYPE_CHOIX_TAG";
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        if (objet != null) {
            this.nomCibleObjet = objet.nom;
        }
    }

    // SIMPLIFIÉ : référence directe d'instance en priorité (posée par VueJeu à l'instanciation),
    // puis __OBJET_IMPLIQUE__, puis fallback sur le champ cible local (utilisé par l'éditeur).
    // Toute la réflexion agressive sur contexteApplication a été supprimée : elle est
    // remplacée par le mécanisme de liaison directe cibleObjetResolue de NoeudBase.
    @Override
    public ObjetBase getCibleObjet() {
        if (cibleObjetResolue != null) return cibleObjetResolue;
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (nomCibleObjet != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
        }
        return this.cible;
    }

    // CORRECTIF : remplace l'ancien booléen unique etaitEnCollision (qui ne pouvait mémoriser
    // qu'UN SEUL état de collision pour tout le tag, quel que soit le nombre d'instances
    // portant ce tag) par un état par-objet-touché (via son id). C'est ce qui causait les
    // pertes de vie excessives quand plusieurs objets du même tag (ex: plusieurs "bee")
    // étaient proches/simultanés : le flag global oscillait vrai/faux entre eux et
    // redéclenchait l'événement plusieurs fois en quelques frames.
    public boolean isEnCollisionAvec(String idObjet) {
        return idObjet != null && objetsEnCollision.contains(idObjet);
    }

    public void marquerEnCollision(String idObjet) {
        if (idObjet != null) objetsEnCollision.add(idObjet);
    }

    public void marquerHorsCollision(String idObjet) {
        if (idObjet != null) objetsEnCollision.remove(idObjet);
    }

    public Set<String> getObjetsEnCollisionActuels() {
        return objetsEnCollision;
    }

    public String getTagCible() { return tagCible; }
}
// bas 1
