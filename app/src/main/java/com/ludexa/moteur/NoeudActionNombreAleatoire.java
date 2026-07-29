package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionNombreAleatoire extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    private String valeurMinimum = "";
    private String valeurMaximum = "";

    public NoeudActionNombreAleatoire() {
        super(genererId(), "Nombre Aléatoire", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable cibleActuelle = getCibleVariable();
        float min = 0f;
        float max = 0f;

        // Parsing du minimum avec gestion d'erreur par défaut à 0f
        try {
            min = Float.parseFloat(valeurMinimum);
        } catch (NumberFormatException e) {
            min = 0f;
        }

        // Parsing du maximum avec gestion d'erreur par défaut à 0f
        try {
            max = Float.parseFloat(valeurMaximum);
        } catch (NumberFormatException e) {
            max = 0f;
        }

        // Inversion si max est inférieur à min
        if (max < min) {
            float temp = min;
            min = max;
            max = temp;
        }

        // Assignation si la cible est valide et de type "CHIFFRE"
        if (cibleActuelle != null && "CHIFFRE".equals(cibleActuelle.type)) {
            cibleActuelle.valeur = min + (float)(Math.random() * (max - min));
        }

        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Minimum", "Maximum"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Minimum".equals(nom)) return valeurMinimum;
        if ("Maximum".equals(nom)) return valeurMaximum;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Minimum".equals(nom)) valeurMinimum = valeur;
        if ("Maximum".equals(nom)) valeurMaximum = valeur;
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
            // Reconnexion dynamique[span_1](start_span)[span_1](end_span)
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

    // --- Autorise l'ouverture du clavier pour ce noeud ---
    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}

