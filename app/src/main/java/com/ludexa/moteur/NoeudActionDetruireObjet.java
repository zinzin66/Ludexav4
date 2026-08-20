// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionDetruireObjet extends NoeudBase {

    private transient ObjetBase cible;

    public NoeudActionDetruireObjet() {
        super(genererId(), "Détruire Objet", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    private void neutraliserEtRetirer(ObjetBase obj) {
        if (obj == null || contexteApplication == null) return;
        
        Scene sceneParent = null;
        try {
            if (contexteApplication instanceof InterfaceEditeur) {
                InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                if (editeur.sceneActive != null && editeur.sceneActive.objets != null && editeur.sceneActive.objets.contains(obj)) {
                    sceneParent = editeur.sceneActive;
                } else if (editeur.sceneHudActive != null && editeur.sceneHudActive.objets != null && editeur.sceneHudActive.objets.contains(obj)) {
                    sceneParent = editeur.sceneHudActive;
                }
            } else {
                java.lang.reflect.Field fieldAct = contexteApplication.getClass().getField("sceneActive");
                Scene sAct = (Scene) fieldAct.get(contexteApplication);
                if (sAct != null && sAct.objets != null && sAct.objets.contains(obj)) sceneParent = sAct;
                else {
                    try {
                        java.lang.reflect.Field fieldHud = contexteApplication.getClass().getField("sceneHudActive");
                        Scene sHud = (Scene) fieldHud.get(contexteApplication);
                        if (sHud != null && sHud.objets != null && sHud.objets.contains(obj)) sceneParent = sHud;
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {}
        
        if (sceneParent == null) return;
        
        List<ObjetBase> aDetruire = new ArrayList<>();
        for (ObjetBase o : sceneParent.objets) {
            if (obj.id.equals(o.parentId)) aDetruire.add(o);
        }
        for (ObjetBase enfant : aDetruire) {
            neutraliserEtRetirer(enfant);
        }

        obj.visible = false;
        obj.estPhysique = false;
        obj.estZoneDeClic = false;
        obj.estRamassable = false;
        obj.x = -99999;
        obj.y = -99999;
        
        try {
            sceneParent.objets.remove(obj);
        } catch (Exception e) {}
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null) {
            neutraliserEtRetirer(cibleActuelle);
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) { }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet;
        // CORRECTION DU BUG : On protège le mot-clé spécial lors d'une assignation nulle
        if (objet != null) {
            this.nomCibleObjet = objet.nom;
        } else if (!"__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            this.nomCibleObjet = null;
        }
    }

    @Override
    public ObjetBase getCibleObjet() {
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }

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
}
// bas 1
