// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionGlisserVers extends NoeudBase {

    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String cibleX = "0";
    private String cibleY = "0";
    private String duree = "1.0";

    public NoeudActionGlisserVers() {
        super(genererId(), "Glisser Vers", "Animations");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null && contexteApplication != null) {
            try {
                float targetX = Float.parseFloat(cibleX);
                float targetY = Float.parseFloat(cibleY);
                float temps = Float.parseFloat(duree);
                
                android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
                handler.post(() -> {
                    android.animation.PropertyValuesHolder pvhX = android.animation.PropertyValuesHolder.ofFloat("x", obj.x, targetX);
                    android.animation.PropertyValuesHolder pvhY = android.animation.PropertyValuesHolder.ofFloat("y", obj.y, targetY);
                    
                    android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                    anim.setDuration((long)(temps * 1000));
                    anim.addUpdateListener(a -> {
                        obj.x = (float) a.getAnimatedValue("x");
                        obj.y = (float) a.getAnimatedValue("y");
                    });
                    anim.start();
                });
            } catch (Exception e) {}
        }
        propagerExecution("Suivant"); // Poursuit la logique sans attendre la fin de l'animation
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Cible X", "Cible Y", "Durée (secondes)"); }
    @Override
    public String getValeurParametre(String nom) {
        if ("Cible X".equals(nom)) return cibleX;
        if ("Cible Y".equals(nom)) return cibleY;
        if ("Durée (secondes)".equals(nom)) return duree;
        return "";
    }
    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Cible X".equals(nom)) cibleX = valeur;
        if ("Cible Y".equals(nom)) cibleY = valeur;
        if ("Durée (secondes)".equals(nom)) duree = valeur;
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
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                        for (ObjetBase o : editeur.sceneActive.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return cible;
    }
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
