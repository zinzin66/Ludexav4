// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionFondu extends NoeudBase {

    public NoeudActionFondu() {
        super(genererId(), "Fondu (Alpha)", "Animations");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Cible Alpha (0 à 1)", "0.0", TYPE_NOMBRE);
        this.ajouterParametre("Durée (secondes)", "1.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null && contexteApplication != null) {
            try {
                String strAlpha = getValeurParametre("Cible Alpha (0 à 1)").replace(",", ".").trim();
                String strDuree = getValeurParametre("Durée (secondes)").replace(",", ".").trim();

                Variable varAlpha = trouverVariable(strAlpha);
                if (varAlpha != null && varAlpha.valeur != null) {
                    strAlpha = varAlpha.valeur.toString().replace(",", ".");
                }
                
                Variable varDuree = trouverVariable(strDuree);
                if (varDuree != null && varDuree.valeur != null) {
                    strDuree = varDuree.valeur.toString().replace(",", ".");
                }

                float rawTarget = Float.parseFloat(strAlpha);
                float rawTemps = Float.parseFloat(strDuree);
                
                if (rawTarget < 0f) rawTarget = 0f;
                if (rawTarget > 1f) rawTarget = 1f;
                if (rawTemps < 0.1f) rawTemps = 0.1f;
                
                final float finalTarget = rawTarget;
                final float finalTemps = rawTemps;
                
                android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
                handler.post(() -> {
                    android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofFloat(obj.alpha, finalTarget);
                    anim.setDuration((long)(finalTemps * 1000));
                    anim.addUpdateListener(a -> obj.alpha = (float) a.getAnimatedValue());
                    anim.start();
                });
            } catch (Exception e) {}
        }
        propagerExecution("Suivant"); 
    }

    @SuppressWarnings("unchecked")
    private Variable trouverVariable(String nomVar) {
        if (contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                for (Variable v : editeur.sceneActive.variablesLocales) if (v.nom.equals(nomVar)) return v;
            }
            if (editeur.variablesGlobales != null) {
                for (Variable v : editeur.variablesGlobales) if (v.nom.equals(nomVar)) return v;
            }
        } else if (contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.variablesLocales != null) {
                    for (Variable v : s.variablesLocales) if (v.nom.equals(nomVar)) return v;
                }
                java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                if (globales != null) {
                    for (Variable v : globales) if (v.nom.equals(nomVar)) return v;
                }
            } catch (Exception e) {}
        }
        return null;
    }
    
    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
