// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionFondu extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String cibleAlpha = "0.0";
    private String duree = "1.0";

    public NoeudActionFondu() {
        super(genererId(), "Fondu (Alpha)", "Animations");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null && contexteApplication != null) {
            try {
                float target = Float.parseFloat(cibleAlpha);
                float temps = Float.parseFloat(duree);
                
                android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
                handler.post(() -> {
                    android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofFloat(obj.alpha, target);
                    anim.setDuration((long)(temps * 1000));
                    anim.addUpdateListener(a -> obj.alpha = (float) a.getAnimatedValue());
                    anim.start();
                });
            } catch (Exception e) {
                obj.alpha = 1.0f;
            }
        }
        propagerExecution("Suivant"); // Poursuit la logique sans attendre la fin de l'animation
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Cible Alpha (0 à 1)", "Durée (secondes)"); }
    @Override
    public String getValeurParametre(String nom) {
        if ("Cible Alpha (0 à 1)".equals(nom)) return cibleAlpha;
        if ("Durée (secondes)".equals(nom)) return duree;
        return "";
    }
    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Cible Alpha (0 à 1)".equals(nom)) cibleAlpha = valeur;
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
