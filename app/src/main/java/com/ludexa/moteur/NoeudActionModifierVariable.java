// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionModifierVariable extends NoeudBase {

    private transient Variable cible;
    public String nomCibleVariable;

    public NoeudActionModifierVariable() {
        super(genererId(), "Modifier Variable", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Valeur", "", TYPE_TEXTE_LIBRE);
    }

    @Override
    public void executer() {
        Variable cibleActuelle = getCibleVariable();
        String valeurSaisie = getValeurParametre("Valeur");
        
        if (cibleActuelle != null) {
            if ("CHIFFRE".equals(cibleActuelle.type)) {
                if (valeurSaisie != null) {
                    String[] tokens = valeurSaisie.split("\\+");
                    float somme = 0f;
                    
                    for (String t : tokens) {
                        String token = t.trim();
                        if (token.isEmpty()) continue;
                        
                        try {
                            somme += Float.parseFloat(token);
                        } catch (NumberFormatException e) {
                            Variable v = trouverVariable(token);
                            if (v != null && v.valeur != null) {
                                try {
                                    somme += Float.parseFloat(v.valeur.toString());
                                } catch (NumberFormatException ex) {}
                            }
                        }
                    }
                    cibleActuelle.valeur = somme;
                } else {
                    cibleActuelle.valeur = 0f;
                }
            } else if ("BOOLEEN".equals(cibleActuelle.type)) {
                String valLower = valeurSaisie.toLowerCase().trim();
                cibleActuelle.valeur = valLower.equals("oui") || valLower.equals("vrai") || valLower.equals("true");
            } else {
                cibleActuelle.valeur = valeurSaisie;
            }
        }
        propagerExecution("Suivant");
    }

    @SuppressWarnings("unchecked")
    private Variable trouverVariable(String nomVar) {
        if (contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                for (Variable v : editeur.sceneActive.variablesLocales) {
                    if (v.nom.equals(nomVar)) return v;
                }
            }
            if (editeur.variablesGlobales != null) {
                for (Variable v : editeur.variablesGlobales) {
                    if (v.nom.equals(nomVar)) return v;
                }
            }
        } else if (contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene scene = (Scene) sceneField.get(contexteApplication);
                if (scene != null && scene.variablesLocales != null) {
                    for (Variable v : scene.variablesLocales) {
                        if (v.nom.equals(nomVar)) return v;
                    }
                }
                java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                if (globales != null) {
                    for (Variable v : globales) {
                        if (v.nom.equals(nomVar)) return v;
                    }
                }
            } catch (Exception e) {}
        }
        return null;
    }

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
                        for (Variable v : s.variablesLocales) if (v.nom.equals(nomCibleVariable)) cible = v;
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
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
