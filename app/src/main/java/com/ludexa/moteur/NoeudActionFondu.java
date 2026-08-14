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
                // Remplacement des virgules par des points pour éviter les crashs de parsing
                String strAlpha = cibleAlpha.replace(",", ".").trim();
                String strDuree = duree.replace(",", ".").trim();

                // Tentative de lire une variable si on a saisi un nom de variable plutôt qu'un chiffre
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
                
                // Sécurité des limites pour l'alpha (0 à 1) et le temps
                if (rawTarget < 0f) rawTarget = 0f;
                if (rawTarget > 1f) rawTarget = 1f;
                if (rawTemps < 0.1f) rawTemps = 0.1f;
                
                // Création de variables finales pour que le lambda Java soit satisfait
                final float finalTarget = rawTarget;
                final float finalTemps = rawTemps;
                
                android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
                handler.post(() -> {
                    android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofFloat(obj.alpha, finalTarget);
                    anim.setDuration((long)(finalTemps * 1000));
                    anim.addUpdateListener(a -> obj.alpha = (float) a.getAnimatedValue());
                    anim.start();
                });
            } catch (Exception e) {
                // En cas d'erreur de frappe inexplicable, on ignore silencieusement
            }
        }
        propagerExecution("Suivant"); // Poursuit la logique sans attendre la fin de l'animation
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
