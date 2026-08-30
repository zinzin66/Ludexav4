// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionAjouterVariable extends NoeudBase {

    private transient Variable cible;
    public String nomCibleVariable;

    public NoeudActionAjouterVariable() {
        super(genererId(), "Ajouter à Variable", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Valeur à ajouter", "", TYPE_NOMBRE);
    }

    @Override
public void executer() {
    Variable cibleActuelle = getCibleVariable();
    String valeurSaisie = getValeurParametre("Valeur à ajouter");

    if (NoeudBase.sceneActiveCourante != null) {
        DiagLogger.log(NoeudBase.cheminProjetCourant, "AJOUTER_VARIABLE: nomCibleVariable=" + nomCibleVariable
            + " cibleTrouvee=" + (cibleActuelle != null)
            + (cibleActuelle != null ? " valeurAvant=" + cibleActuelle.valeur + " type=" + cibleActuelle.type : ""));
    }
    
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

    if (cibleActuelle != null) {
        DiagLogger.log(NoeudBase.cheminProjetCourant, "AJOUTER_VARIABLE: nomCibleVariable=" + nomCibleVariable
            + " valeurApres=" + cibleActuelle.valeur);
    }
    
    propagerExecution("Suivant");
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
        if (cible != null) return cible;

        if (nomCibleVariable != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.variablesLocales != null) {
                for (Variable v : NoeudBase.sceneActiveCourante.variablesLocales) {
                    if (nomCibleVariable.equals(v.nom)) return v;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.variablesLocales != null) {
                for (Variable v : NoeudBase.sceneHudActiveCourante.variablesLocales) {
                    if (nomCibleVariable.equals(v.nom)) return v;
                }
            }
            if (contexteApplication != null) {
                try {
                    if (contexteApplication instanceof InterfaceEditeur) {
                        InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                        if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                            for (Variable v : editeur.sceneActive.variablesLocales) if (v.nom.equals(nomCibleVariable)) return v;
                        }
                        if (editeur.variablesGlobales != null) {
                            for (Variable v : editeur.variablesGlobales) if (v.nom.equals(nomCibleVariable)) return v;
                        }
                    } else {
                        java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                        Scene s = (Scene) sceneField.get(contexteApplication);
                        if (s != null && s.variablesLocales != null) {
                            for (Variable v : s.variablesLocales) if (v.nom.equals(nomCibleVariable)) return v;
                        }
                        java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                        List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                        if (globales != null) {
                            for (Variable v : globales) if (v.nom.equals(nomCibleVariable)) return v;
                        }
                    }
                } catch (Exception e) {}
            }
        }
        return this.cible; 
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
