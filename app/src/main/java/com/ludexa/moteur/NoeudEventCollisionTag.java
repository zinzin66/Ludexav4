// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventCollisionTag extends NoeudBase {
    
    private transient ObjetBase cible;
    private String tagCible = "";
    private transient boolean etaitEnCollision = false;

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

    public boolean isEtaitEnCollision() { return etaitEnCollision; }
    public void setEtaitEnCollision(boolean etat) { this.etaitEnCollision = etat; }
    public String getTagCible() { return tagCible; }
}
// bas 1
