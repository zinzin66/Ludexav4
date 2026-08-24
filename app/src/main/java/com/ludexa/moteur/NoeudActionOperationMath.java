// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionOperationMath extends NoeudBase {
    
    private transient Variable cible;
    public String nomCibleVariable;

    public NoeudActionOperationMath() {
        super(genererId(), "Opération Mathématique", "Variables & Inventaire");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametreListe("Opérateur", "+", Arrays.asList("+", "-", "*", "/"));
        this.ajouterParametre("Valeur", "1", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        Variable var = getCibleVariable();
        if (var != null && var.valeur != null) {
            try {
                String valStr = var.valeur.toString();
                float val1 = Float.parseFloat(valStr);
                float val2 = Float.parseFloat(getValeurParametre("Valeur"));
                float resultat = val1;
                
                String operateur = getValeurParametre("Opérateur");
                
                switch (operateur) {
                    case "+": resultat = val1 + val2; break;
                    case "-": resultat = val1 - val2; break;
                    case "*": resultat = val1 * val2; break;
                    case "/": 
                        if (val2 != 0) resultat = val1 / val2; 
                        break;
                }
                
                String resFinal = String.valueOf(resultat);
                if (resFinal.endsWith(".0")) {
                    resFinal = resFinal.substring(0, resFinal.length() - 2);
                }
                var.valeur = resFinal;
                
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleVariable() { return true; }

    @Override
    public void setCibleVariable(Variable var) {
        this.cible = var;
        this.nomCibleVariable = (var != null) ? var.nom : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Variable getCibleVariable() {
        if (cible == null && nomCibleVariable != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                        for (Variable v : editeur.sceneActive.variablesLocales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                    if (cible == null && editeur.variablesGlobales != null) {
                        for (Variable v : editeur.variablesGlobales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.variablesLocales != null) {
                        for (Variable v : s.variablesLocales) {
                            if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                        }
                    }
                    java.lang.reflect.Field globalesField = contexteApplication.getClass().getField("variablesGlobales");
                    List<Variable> globales = (List<Variable>) globalesField.get(contexteApplication);
                    if (globales != null) {
                        for (Variable v : globales) {
                            if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
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
