package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierTexte extends NoeudBase {

    private ObjetBase cible;
    private String texteSaisi = "";

    public NoeudActionModifierTexte() {
        super(genererId(), "Modifier Texte", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (cible != null && texteSaisi != null) {
            String input = texteSaisi.trim();
            StringBuilder resultatFinal = new StringBuilder();
            boolean dansGuillemets = false;
            StringBuilder tokenCourant = new StringBuilder();

            // Mini-interpréteur pour syntaxe : "Texte" + Variable
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                
                if (c == '"') {
                    if (dansGuillemets) {
                        // Fin d'une chaîne de texte littérale
                        resultatFinal.append(tokenCourant.toString());
                        tokenCourant.setLength(0);
                        dansGuillemets = false;
                    } else {
                        // Début d'une chaîne de texte littérale
                        dansGuillemets = true;
                        tokenCourant.setLength(0);
                    }
                } else if (!dansGuillemets && c == '+') {
                    // On rencontre un +, on évalue ce qu'il y avait avant si ce n'est pas vide
                    String nomVar = tokenCourant.toString().trim();
                    if (!nomVar.isEmpty()) {
                        Variable v = trouverVariable(nomVar);
                        resultatFinal.append(v != null ? v.valeur.toString() : "");
                        tokenCourant.setLength(0);
                    }
                } else {
                    // On accumule les caractères
                    tokenCourant.append(c);
                }
            }
            
            // Évaluer le dernier bout de texte s'il reste une variable après le dernier +
            if (!dansGuillemets) {
                String nomVar = tokenCourant.toString().trim();
                if (!nomVar.isEmpty()) {
                    Variable v = trouverVariable(nomVar);
                    resultatFinal.append(v != null ? v.valeur.toString() : "");
                }
            } else {
                // Si l'utilisateur a oublié de fermer le guillemet, on ajoute quand même le texte
                resultatFinal.append(tokenCourant.toString());
            }

            cible.nom = resultatFinal.toString();
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
    public boolean utiliseClavierTexte() { return true; }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Code/Texte"); }

    @Override
    public String getValeurParametre(String nom) {
        return texteSaisi;
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        texteSaisi = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cible = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cible; }
}
