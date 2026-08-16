// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionClampVariable extends NoeudBase {
    private transient Variable cible;
    private String nomCibleVariable;
    
    private String minValeur = "0";
    private String maxValeur = "100";

    public NoeudActionClampVariable() {
        super(genererId(), "Limiter Valeur (Clamp)", "Variables & Inventaire");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable var = getCibleVariable();
        if (var != null && var.valeur != null) {
            try {
                float val = Float.parseFloat(var.valeur);
                float min = Float.parseFloat(minValeur);
                float max = Float.parseFloat(maxValeur);
                
                if (val < min) val = min;
                if (val > max) val = max;
                
                var.valeur = String.valueOf(val);
                if (var.valeur.endsWith(".0")) {
                    var.valeur = var.valeur.substring(0, var.valeur.length() - 2);
                }
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Minimum", "Maximum"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Minimum".equals(nom)) return minValeur;
        if ("Maximum".equals(nom)) return maxValeur;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Minimum".equals(nom)) minValeur = valeur;
        else if ("Maximum".equals(nom)) maxValeur = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_NOMBRE;
    }

    @Override
    public boolean requiertCibleVariable() { return true; }

    @Override
    public void setCibleVariable(Variable var) {
        this.cible = var;
        this.nomCibleVariable = (var != null) ? var.nom : null;
    }

    @Override
    public Variable getCibleVariable() {
        if (cible == null && nomCibleVariable != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.variablesLocales != null) {
                    for (Variable v : s.variablesLocales) {
                        if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                    }
                }
                java.lang.reflect.Field globalesField = contexteApplication.getClass().getField("variablesGlobales");
                @SuppressWarnings("unchecked")
                List<Variable> globales = (List<Variable>) globalesField.get(contexteApplication);
                if (globales != null) {
                    for (Variable v : globales) {
                        if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
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
