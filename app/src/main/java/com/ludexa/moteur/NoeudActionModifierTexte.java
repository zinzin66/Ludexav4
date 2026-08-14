// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierTexte extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String texteSaisi = "";

    public NoeudActionModifierTexte() {
        super(genererId(), "Modifier Texte", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && texteSaisi != null) {
            String input = texteSaisi.trim();
            StringBuilder resultatFinal = new StringBuilder();
            boolean dansGuillemets = false;
            StringBuilder tokenCourant = new StringBuilder();

            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                
                if (c == '"') {
                    if (dansGuillemets) {
                        resultatFinal.append(tokenCourant.toString());
                        tokenCourant.setLength(0);
                        dansGuillemets = false;
                    } else {
                        dansGuillemets = true;
                        tokenCourant.setLength(0);
                    }
                } else if (!dansGuillemets && c == '+') {
                    String nomVar = tokenCourant.toString().trim();
                    if (!nomVar.isEmpty()) {
                        Variable v = trouverVariable(nomVar);
                        resultatFinal.append(v != null && v.valeur != null ? v.valeur.toString() : "");
                    }
                    tokenCourant.setLength(0);
                } else {
                    tokenCourant.append(c);
                }
            }
            
            if (!dansGuillemets) {
                String nomVar = tokenCourant.toString().trim();
                if (!nomVar.isEmpty()) {
                    Variable v = trouverVariable(nomVar);
                    resultatFinal.append(v != null && v.valeur != null ? v.valeur.toString() : "");
                }
            } else {
                resultatFinal.append(tokenCourant.toString());
            }

            cibleActuelle.contenuTexte = resultatFinal.toString();
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
    public List<String> getNomsParametres() { return Arrays.asList("Nouveau texte"); }

    @Override
    public String getValeurParametre(String nom) { 
        if ("Nouveau texte".equals(nom)) return texteSaisi;
        return texteSaisi; 
    }

    @Override
    public void setValeurParametre(String nom, String valeur) { 
        if ("Nouveau texte".equals(nom)) texteSaisi = valeur; 
        else texteSaisi = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjet() {
        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    Scene s = ((InterfaceEditeur) contexteApplication).sceneActive;
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cible = o;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cible = o;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible;
    }
}
// bas 1
