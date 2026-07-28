// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionComparaison extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    
    private String operateur = "=";
    private String valeurComparaison = "";

    public NoeudConditionComparaison() {
        super(genererId(), "Condition", "Logique");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        // DEUX sorties d'exécution : Vrai et Faux
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        boolean resultatCondition = false;
        Variable cibleActuelle = getCibleVariable();

        if (cibleActuelle != null && cibleActuelle.valeur != null) {
            try {
                if ("CHIFFRE".equals(cibleActuelle.type)) {
                    float valVar = 0f;
                    if (cibleActuelle.valeur instanceof Float) {
                        valVar = (Float) cibleActuelle.valeur;
                    } else {
                        valVar = Float.parseFloat(cibleActuelle.valeur.toString());
                    }
                    
                    float valComp = 0f;
                    try {
                        valComp = Float.parseFloat(valeurComparaison);
                    } catch (NumberFormatException e) {
                        valComp = 0f;
                    }

                    switch (operateur) {
                        case "=": resultatCondition = (valVar == valComp); break;
                        case "≠": resultatCondition = (valVar != valComp); break;
                        case ">": resultatCondition = (valVar > valComp); break;
                        case "<": resultatCondition = (valVar < valComp); break;
                        case "≥": resultatCondition = (valVar >= valComp); break;
                        case "≤": resultatCondition = (valVar <= valComp); break;
                    }
                } 
                else if ("BOOLEEN".equals(cibleActuelle.type)) {
                    boolean valVar = false;
                    if (cibleActuelle.valeur instanceof Boolean) {
                        valVar = (Boolean) cibleActuelle.valeur;
                    } else {
                        String vStr = cibleActuelle.valeur.toString().toLowerCase().trim();
                        valVar = vStr.equals("oui") || vStr.equals("vrai") || vStr.equals("true");
                    }

                    String valLower = valeurComparaison.toLowerCase().trim();
                    boolean valComp = valLower.equals("oui") || valLower.equals("vrai") || valLower.equals("true");

                    if ("=".equals(operateur)) {
                        resultatCondition = (valVar == valComp);
                    } else if ("≠".equals(operateur)) {
                        resultatCondition = (valVar != valComp);
                    }
                } 
                else {
                    // Type TEXTE ou autre
                    String valVar = cibleActuelle.valeur.toString();
                    String valComp = valeurComparaison != null ? valeurComparaison : "";

                    switch (operateur) {
                        case "=": resultatCondition = valVar.equals(valComp); break;
                        case "≠": resultatCondition = !valVar.equals(valComp); break;
                        default:
                            int cmp = valVar.compareTo(valComp);
                            if (">".equals(operateur)) resultatCondition = (cmp > 0);
                            else if ("<".equals(operateur)) resultatCondition = (cmp < 0);
                            else if ("≥".equals(operateur)) resultatCondition = (cmp >= 0);
                            else if ("≤".equals(operateur)) resultatCondition = (cmp <= 0);
                            break;
                    }
                }
            } catch (Exception e) {
                resultatCondition = false;
            }
        }

        // Routage dynamique de l'exécution selon le résultat
        if (resultatCondition) {
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Opérateur", "Valeur de comparaison");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Opérateur".equals(nom)) return operateur;
        if ("Valeur de comparaison".equals(nom)) return valeurComparaison;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Opérateur".equals(nom)) operateur = valeur;
        if ("Valeur de comparaison".equals(nom)) valeurComparaison = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) {
            return TYPE_CHOIX_LISTE;
        }
        return TYPE_TEXTE_LIBRE;
    }
    
    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) {
            return Arrays.asList("=", "≠", ">", "<", "≥", "≤");
        }
        return super.getOptionsChoixListe(nomParametre);
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }

    @Override
    public boolean requiertCibleVariable() { return true; }
    
    @Override
    public void setCibleVariable(Variable v) { 
        this.cible = v; 
        this.nomCibleVariable = (v != null) ? v.nom : null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Variable getCibleVariable() { 
        // Récupération dynamique copiée depuis NoeudActionModifierVariable
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
                    Object sceneObj = sceneField.get(contexteApplication);
                    if (sceneObj != null) {
                        java.lang.reflect.Field varsLocalesField = sceneObj.getClass().getField("variablesLocales");
                        List<Variable> varsLocales = (List<Variable>) varsLocalesField.get(sceneObj);
                        if (varsLocales != null) {
                            for (Variable v : varsLocales) if (v.nom.equals(nomCibleVariable)) cible = v;
                        }
                    }
                    if (cible == null) {
                        java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                        List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                        if (globales != null) {
                            for (Variable v : globales) if (v.nom.equals(nomCibleVariable)) cible = v;
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible; 
    }

    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}
// bas 1

