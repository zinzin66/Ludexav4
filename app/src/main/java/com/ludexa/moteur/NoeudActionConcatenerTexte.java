// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionConcatenerTexte extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    private String texteAAjouter = "";

    public NoeudActionConcatenerTexte() {
        super(genererId(), "Ajouter au Texte", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable cibleActuelle = getCibleVariable();
        if (cibleActuelle != null) {
            // Récupère la valeur actuelle sous forme de chaîne (vide si null)
            String valeurActuelle = (cibleActuelle.valeur != null) ? cibleActuelle.valeur.toString() : "";
            String ajout = (texteAAjouter != null) ? texteAAjouter : "";
            
            // Concaténation simple
            cibleActuelle.valeur = valeurActuelle + ajout;
            
            // On force le type en TEXTE par sécurité pour la cohérence du moteur
            if (!"TEXTE".equals(cibleActuelle.type)) {
                cibleActuelle.type = "TEXTE";
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Texte à ajouter"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Texte à ajouter".equals(nom)) return texteAAjouter;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Texte à ajouter".equals(nom)) texteAAjouter = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        // CORRECTIF 2 : Forcer le clavier alphabétique pour la saisie de texte
        return TYPE_TEXTE_ALPHABETIQUE;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }
// bas 1
// haut 2
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
        // Logique de reconnexion dynamique calquée sur NoeudActionModifierVariable
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
}
// bas 2
