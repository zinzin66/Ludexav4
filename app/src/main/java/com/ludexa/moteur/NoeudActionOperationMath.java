// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionOperationMath extends NoeudBase {
    private transient Variable cible;
    private String nomCibleVariable;
    
    private String operateur = "+";
    private String valeurParam = "1";

    public NoeudActionOperationMath() {
        super(genererId(), "Opération Mathématique", "Variables & Inventaire");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable var = getCibleVariable();
        if (var != null && var.valeur != null) {
            try {
                float val1 = Float.parseFloat(var.valeur);
                float val2 = Float.parseFloat(valeurParam);
                float resultat = val1;
                
                switch (operateur) {
                    case "+": resultat = val1 + val2; break;
                    case "-": resultat = val1 - val2; break;
                    case "*": resultat = val1 * val2; break;
                    case "/": 
                        if (val2 != 0) resultat = val1 / val2; 
                        break;
                }
                
                var.valeur = String.valueOf(resultat);
                // Nettoyage visuel pour éviter d'afficher "100.0" au lieu de "100"
                if (var.valeur.endsWith(".0")) {
                    var.valeur = var.valeur.substring(0, var.valeur.length() - 2);
                }
            } catch (Exception e) {
                // Si la variable contient du texte et non un nombre, on ignore silencieusement
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Opérateur", "Valeur"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Opérateur".equals(nom)) return operateur;
        if ("Valeur".equals(nom)) return valeurParam;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Opérateur".equals(nom)) operateur = valeur;
        else if ("Valeur".equals(nom)) valeurParam = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) return TYPE_CHOIX_LISTE;
        return TYPE_NOMBRE;
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) return Arrays.asList("+", "-", "*", "/");
        return super.getOptionsChoixListe(nomParametre);
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
