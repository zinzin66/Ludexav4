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
        if (obj == null) return;
        
        obj.visible = false;
        obj.estPhysique = false;
        obj.estZoneDeClic = false;
        obj.estRamassable = false;
        obj.x = -99999;
        obj.y = -99999;
        
        if (contexteApplication == null) return;
        
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
                // RECHERCHE AGRESSIVE : On force la lecture même si c'est privé
                try {
                    java.lang.reflect.Field sf = contexteApplication.getClass().getDeclaredField("sceneActive");
                    sf.setAccessible(true);
                    Scene sAct = (Scene) sf.get(contexteApplication);
                    if (sAct != null && sAct.objets != null && sAct.objets.contains(obj)) sceneParent = sAct;
                } catch(Exception e) {}
                
                if (sceneParent == null) {
                    try {
                        java.lang.reflect.Field vf = contexteApplication.getClass().getDeclaredField("vueJeu");
                        vf.setAccessible(true);
                        Object vueObj = vf.get(contexteApplication);
                        if (vueObj != null) {
                            java.lang.reflect.Field sf = vueObj.getClass().getDeclaredField("sceneActive");
                            sf.setAccessible(true);
                            Scene sAct = (Scene) sf.get(vueObj);
                            if (sAct != null && sAct.objets != null && sAct.objets.contains(obj)) sceneParent = sAct;
                            else {
                                try {
                                    java.lang.reflect.Field hf = vueObj.getClass().getDeclaredField("sceneHudActive");
                                    hf.setAccessible(true);
                                    Scene sHud = (Scene) hf.get(vueObj);
                                    if (sHud != null && sHud.objets != null && sHud.objets.contains(obj)) sceneParent = sHud;
                                } catch(Exception e2) {}
                            }
                        }
                    } catch(Exception e) {}
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
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet;
        if (objet != null) {
            this.nomCibleObjet = objet.nom;
        } else if (!"__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            this.nomCibleObjet = null;
        }
    }

    @Override
    public ObjetBase getCibleObjet() {
        if ("__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }

        if (this.nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                        for (ObjetBase o : editeur.sceneActive.objets) if (this.nomCibleObjet.equals(o.nom)) return o;
                    }
                    if (editeur.sceneHudActive != null && editeur.sceneHudActive.objets != null) {
                        for (ObjetBase o : editeur.sceneHudActive.objets) if (this.nomCibleObjet.equals(o.nom)) return o;
                    }
                } else {
                    Scene sAct = null;
                    Scene sHud = null;
                    
                    try {
                        java.lang.reflect.Field sf = contexteApplication.getClass().getDeclaredField("sceneActive");
                        sf.setAccessible(true);
                        sAct = (Scene) sf.get(contexteApplication);
                    } catch(Exception e) {}
                    
                    if (sAct == null) {
                        try {
                            java.lang.reflect.Field vf = contexteApplication.getClass().getDeclaredField("vueJeu");
                            vf.setAccessible(true);
                            Object vueObj = vf.get(contexteApplication);
                            if (vueObj != null) {
                                java.lang.reflect.Field sf = vueObj.getClass().getDeclaredField("sceneActive");
                                sf.setAccessible(true);
                                sAct = (Scene) sf.get(vueObj);
                                
                                try {
                                    java.lang.reflect.Field hf = vueObj.getClass().getDeclaredField("sceneHudActive");
                                    hf.setAccessible(true);
                                    sHud = (Scene) hf.get(vueObj);
                                } catch(Exception e2) {}
                            }
                        } catch(Exception e) {}
                    }
                    
                    if (sAct != null && sAct.objets != null) {
                        for (ObjetBase o : sAct.objets) if (this.nomCibleObjet.equals(o.nom)) return o;
                    }
                    if (sHud != null && sHud.objets != null) {
                        for (ObjetBase o : sHud.objets) if (this.nomCibleObjet.equals(o.nom)) return o;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible;
    }
}
// bas 1
