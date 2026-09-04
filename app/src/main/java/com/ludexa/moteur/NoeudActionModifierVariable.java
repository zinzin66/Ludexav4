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
        ObjetBase objTarget = getCibleObjet(); 
        
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
                            Variable v = trouverVariable(token, objTarget);
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
    private Variable trouverVariable(String nomVar, ObjetBase objTarget) {
        if (objTarget != null && objTarget.variablesLocales != null) {
            for (Variable v : objTarget.variablesLocales) {
                if (v.nom.equals(nomVar)) return v;
            }
        }
        
        if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.variablesLocales != null) {
            for (Variable v : NoeudBase.sceneActiveCourante.variablesLocales) {
                if (v.nom.equals(nomVar)) return v;
            }
        }
        if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.variablesLocales != null) {
            for (Variable v : NoeudBase.sceneHudActiveCourante.variablesLocales) {
                if (v.nom.equals(nomVar)) return v;
            }
        }

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
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if (cibleObjetResolue != null) return cibleObjetResolue;
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (nomCibleObjet != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
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
        if (nomCibleVariable == null) return null;

        ObjetBase objTarget = getCibleObjet();
        if (objTarget != null && objTarget.variablesLocales != null) {
            for (Variable v : objTarget.variablesLocales) {
                if (nomCibleVariable.equals(v.nom)) return v;
            }
        }

        if (cible != null && nomCibleVariable.equals(cible.nom)) return cible;

        if (contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                        for (Variable v : editeur.sceneActive.variablesLocales) if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                    }
                    if (editeur.variablesGlobales != null) {
                        for (Variable v : editeur.variablesGlobales) if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                    }
                } else {
                    if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.variablesLocales != null) {
                        for (Variable v : NoeudBase.sceneActiveCourante.variablesLocales) if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                    }
                    if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.variablesLocales != null) {
                        for (Variable v : NoeudBase.sceneHudActiveCourante.variablesLocales) if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                    }
                    
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.variablesLocales != null) {
                        for (Variable v : s.variablesLocales) if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                    }
                    java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                    List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                    if (globales != null) {
                        for (Variable v : globales) if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
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
