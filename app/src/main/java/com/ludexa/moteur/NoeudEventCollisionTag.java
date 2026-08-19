// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventCollisionTag extends NoeudBase {
    
    private transient ObjetBase cible;
    private String nomCibleObjet;
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
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if (nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                        for (ObjetBase o : editeur.sceneActive.objets) if (nomCibleObjet.equals(o.nom)) return o;
                    }
                    if (editeur.sceneHudActive != null && editeur.sceneHudActive.objets != null) {
                        for (ObjetBase o : editeur.sceneHudActive.objets) if (nomCibleObjet.equals(o.nom)) return o;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene sAct = (Scene) sceneField.get(contexteApplication);
                    if (sAct != null && sAct.objets != null) {
                        for (ObjetBase o : sAct.objets) if (nomCibleObjet.equals(o.nom)) return o;
                    }
                    try {
                        java.lang.reflect.Field sceneHudField = contexteApplication.getClass().getField("sceneHudActive");
                        Scene sHud = (Scene) sceneHudField.get(contexteApplication);
                        if (sHud != null && sHud.objets != null) {
                            for (ObjetBase o : sHud.objets) if (nomCibleObjet.equals(o.nom)) return o;
                        }
                    } catch (Exception e) {}
                }
            } catch (Exception e) {}
        }
        return this.cible;
    }

    public boolean isEtaitEnCollision() { return etaitEnCollision; }
    public void setEtaitEnCollision(boolean etat) { this.etaitEnCollision = etat; }
    public String getTagCible() { return tagCible; }
}
// bas 1
