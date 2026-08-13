// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoeudConditionDouble extends NoeudBase {

    private String varA = "";
    private String valA = "";
    private String lien = "ET"; 
    private String varB = "";
    private String valB = "";

    public NoeudConditionDouble() {
        super(genererId(), "Condition Double (ET/OU)", "Logique & Conditions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        boolean conditionA = evaluer(varA, valA);
        boolean conditionB = evaluer(varB, valB);
        
        boolean resultatFinal = false;
        if ("ET".equals(lien)) {
            resultatFinal = conditionA && conditionB;
        } else if ("OU".equals(lien)) {
            resultatFinal = conditionA || conditionB;
        }

        if (resultatFinal) {
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }
    
    private boolean evaluer(String nomVar, String valAttendue) {
        if (nomVar == null || nomVar.trim().isEmpty()) return false;
        
        Variable v = trouverVariable(nomVar.trim());
        if (v == null || v.valeur == null) return false;
        
        String valString = v.valeur.toString().toLowerCase().trim();
        String valAttendueString = valAttendue.toLowerCase().trim();
        
        if ("BOOLEEN".equals(v.type)) {
            boolean boolVar = valString.equals("true") || valString.equals("vrai") || valString.equals("oui");
            boolean boolAttendu = valAttendueString.equals("true") || valAttendueString.equals("vrai") || valAttendueString.equals("oui");
            return boolVar == boolAttendu;
        } else if ("CHIFFRE".equals(v.type) || "ENTIER".equals(v.type)) {
            try {
                float floatVar = Float.parseFloat(v.valeur.toString());
                float floatAttendu = Float.parseFloat(valAttendue);
                return floatVar == floatAttendu;
            } catch (Exception e) {
                return false;
            }
        } else {
            return v.valeur.toString().trim().equalsIgnoreCase(valAttendue.trim());
        }
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
    public List<String> getNomsParametres() { 
        return Arrays.asList("Variable A", "Valeur A", "Lien (ET/OU)", "Variable B", "Valeur B"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Variable A".equals(nom)) return varA;
        if ("Valeur A".equals(nom)) return valA;
        if ("Lien (ET/OU)".equals(nom)) return lien;
        if ("Variable B".equals(nom)) return varB;
        if ("Valeur B".equals(nom)) return valB;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Variable A".equals(nom)) varA = valeur;
        else if ("Valeur A".equals(nom)) valA = valeur;
        else if ("Lien (ET/OU)".equals(nom)) lien = valeur;
        else if ("Variable B".equals(nom)) varB = valeur;
        else if ("Valeur B".equals(nom)) valB = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        // Transforme "Variable A", "Variable B" et "Lien" en menus déroulants dynamiques
        if ("Lien (ET/OU)".equals(nomParametre) || "Variable A".equals(nomParametre) || "Variable B".equals(nomParametre)) {
            return TYPE_CHOIX_LISTE;
        }
        return TYPE_TEXTE_LIBRE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("Lien (ET/OU)".equals(nomParametre)) {
            return Arrays.asList("ET", "OU");
        }
        
        // Construction dynamique de la liste des variables disponibles pour A et B
        if ("Variable A".equals(nomParametre) || "Variable B".equals(nomParametre)) {
            List<String> options = new ArrayList<>();
            if (contexteApplication != null) {
                try {
                    if (contexteApplication instanceof InterfaceEditeur) {
                        InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                        if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                            for (Variable v : editeur.sceneActive.variablesLocales) options.add(v.nom);
                        }
                        if (editeur.variablesGlobales != null) {
                            for (Variable v : editeur.variablesGlobales) options.add(v.nom);
                        }
                    }
                } catch (Exception e) {}
            }
            if (options.isEmpty()) {
                options.add(""); // Empêche un crash si aucune variable n'existe encore
            }
            return options;
        }
        
        return new ArrayList<>();
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
            
