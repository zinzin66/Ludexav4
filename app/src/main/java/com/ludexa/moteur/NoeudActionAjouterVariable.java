// début 1 11 08

package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionAjouterVariable extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    private String valeurSaisie = "";

    public NoeudActionAjouterVariable() {
        super(genererId(), "Ajouter à Variable", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable cibleActuelle = getCibleVariable();
        if (cibleActuelle != null && "CHIFFRE".equals(cibleActuelle.type)) {
            float valeurCourante = 0f;
            if (cibleActuelle.valeur instanceof Float) {
                valeurCourante = (Float) cibleActuelle.valeur;
            } else if (cibleActuelle.valeur != null) {
                try {
                    valeurCourante = Float.parseFloat(cibleActuelle.valeur.toString());
                } catch (NumberFormatException e) {
                    valeurCourante = 0f;
                }
            }
            
            float valeurAJoindre = 0f;
            try {
                valeurAJoindre = Float.parseFloat(valeurSaisie);
            } catch (NumberFormatException e) {
                valeurAJoindre = 0f;
            }
            
            cibleActuelle.valeur = valeurCourante + valeurAJoindre;
        } else if (cibleActuelle != null && "ENTIER".equals(cibleActuelle.type)) {
            // CORRECTION : le type ENTIER n'était pas géré (seul CHIFFRE l'était), le nœud ne faisait rien silencieusement
            int valeurCourante = 0;
            if (cibleActuelle.valeur instanceof Integer) {
                valeurCourante = (Integer) cibleActuelle.valeur;
            } else if (cibleActuelle.valeur != null) {
                try {
                    valeurCourante = Integer.parseInt(cibleActuelle.valeur.toString().trim());
                } catch (NumberFormatException e) {
                    valeurCourante = 0;
                }
            }

            int valeurAJoindre = 0;
            try {
                valeurAJoindre = Integer.parseInt(valeurSaisie.trim());
            } catch (NumberFormatException e) {
                valeurAJoindre = 0;
            }

            cibleActuelle.valeur = valeurCourante + valeurAJoindre;
        }
        // Si le type n'est ni CHIFFRE ni ENTIER, on ne fait rien (pas de crash).
        
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Valeur à ajouter"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Valeur à ajouter".equals(nom)) return valeurSaisie;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Valeur à ajouter".equals(nom)) valeurSaisie = valeur;
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

    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
} 
