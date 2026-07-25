package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String resultat = texteSaisi;
            
            Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher matcher = pattern.matcher(resultat);
            StringBuffer sb = new StringBuffer();
            
            while (matcher.find()) {
                String nomVar = matcher.group(1);
                Variable var = trouverVariable(nomVar);
                String valeurRemplacement = (var != null && var.valeur != null) ? var.valeur.toString() : "{" + nomVar + "}";
                matcher.appendReplacement(sb, Matcher.quoteReplacement(valeurRemplacement));
            }
            matcher.appendTail(sb);
            
            cible.nom = sb.toString();
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
            // Lecture sécurisée pour VueJeu sans dépendance forte
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
            } catch (Exception e) {
                // Ignore silencieusement si le contexte n'a pas ces champs
            }
        }
        return null;
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Texte"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Texte".equals(nom)) return texteSaisi;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Texte".equals(nom)) texteSaisi = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cible = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cible; }
}
