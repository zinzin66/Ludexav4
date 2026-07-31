// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierVariable extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    private String valeurSaisie = "";

    public NoeudActionModifierVariable() {
        super(genererId(), "Modifier Variable", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable cibleActuelle = getCibleVariable();
        if (cibleActuelle != null) {
            if ("CHIFFRE".equals(cibleActuelle.type)) {
                if (valeurSaisie != null) {
                    String[] tokens = valeurSaisie.split("\\+");
                    float somme = 0f;
                    
                    for (String t : tokens) {
                        String token = t.trim();
                        if (token.isEmpty()) continue;
                        
                        try {
                            // Tente de parser en tant que nombre littéral
                            somme += Float.parseFloat(token);
                        } catch (NumberFormatException e) {
                            // Si ce n'est pas un nombre, on cherche une variable
                            Variable v = trouverVariable(token);
                            if (v != null && v.valeur != null) {
                                try {
                                    somme += Float.parseFloat(v.valeur.toString());
                                } catch (NumberFormatException ex) {
                                    // Impossible à parser (ni nombre ni variable valide), traité comme 0
                                }
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
    public List<String> getNomsParametres() { return Arrays.asList("Valeur"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Valeur".equals(nom)) return valeurSaisie;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Valeur".equals(nom)) valeurSaisie = valeur;
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
        if (cible == null && nomCibleVariable != null && contexteApplication != null) {
            // Reconnexion dynamique
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

    // --- CORRECTION : Autorise l'ouverture du clavier pour ce noeud ---
    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}
// bas 1

