// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudEventVariableChange extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    
    // Pour mémoriser l'état précédent et détecter le changement
    private Object derniereValeurConnue = null;
    private boolean estInitialise = false;

    public NoeudEventVariableChange() {
        super(genererId(), "Quand Variable Change", "Événements");
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    public void verifierEtDeclencher() {
        Variable var = getCibleVariable();
        if (var != null) {
            Object valeurActuelle = var.valeur;
            
            if (!estInitialise) {
                // Première passe : on mémorise l'état initial sans déclencher l'événement
                derniereValeurConnue = copierValeurPourComparaison(valeurActuelle);
                estInitialise = true;
            } else {
                // Comparaison avec l'ancienne valeur
                if (!sontEgaux(derniereValeurConnue, valeurActuelle)) {
                    derniereValeurConnue = copierValeurPourComparaison(valeurActuelle);
                    executer(); // Le changement est détecté, on déclenche !
                }
            }
        }
    }

    private Object copierValeurPourComparaison(Object obj) {
        if (obj == null) return null;
        // On convertit en String pour s'assurer que la comparaison fonctionne quel que soit le type (Float, Int, String...)
        return String.valueOf(obj); 
    }

    private boolean sontEgaux(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null || o2 == null) return false;
        return String.valueOf(o1).equals(String.valueOf(o2));
    }

    @Override
    public void executer() {
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
    public List<String> getNomsParametres() { return null; }
    @Override
    public String getValeurParametre(String nom) { return ""; }
    @Override
    public void setValeurParametre(String nom, String valeur) {}
    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
