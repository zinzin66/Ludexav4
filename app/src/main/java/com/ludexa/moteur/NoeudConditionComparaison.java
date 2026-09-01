// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionComparaison extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    
    private String operateur = "=";
    private String valeurComparaison = "";

    public NoeudConditionComparaison() {
        super(genererId(), "Condition", "Logique");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        // DEUX sorties d'exécution : Vrai et Faux
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        boolean resultatCondition = false;
        Variable cibleActuelle = getCibleVariable();

        if (cibleActuelle != null && cibleActuelle.valeur != null) {
            try {
                if ("CHIFFRE".equals(cibleActuelle.type)) {
                    float valVar = 0f;
                    if (cibleActuelle.valeur instanceof Float) {
                        valVar = (Float) cibleActuelle.valeur;
                    } else {
                        valVar = Float.parseFloat(cibleActuelle.valeur.toString());
                    }
                    
                    float valComp = 0f;
                    try {
                        valComp = Float.parseFloat(valeurComparaison);
                    } catch (NumberFormatException e) {
                        valComp = 0f;
                    }

                    switch (operateur) {
                        case "=": resultatCondition = (valVar == valComp); break;
                        case "≠": resultatCondition = (valVar != valComp); break;
                        case ">": resultatCondition = (valVar > valComp); break;
                        case "<": resultatCondition = (valVar < valComp); break;
                        case "≥": resultatCondition = (valVar >= valComp); break;
                        case "≤": resultatCondition = (valVar <= valComp); break;
                    }
                } 
                else if ("BOOLEEN".equals(cibleActuelle.type)) {
                    boolean valVar = false;
                    if (cibleActuelle.valeur instanceof Boolean) {
                        valVar = (Boolean) cibleActuelle.valeur;
                    } else {
                        String vStr = cibleActuelle.valeur.toString().toLowerCase().trim();
                        valVar = vStr.equals("oui") || vStr.equals("vrai") || vStr.equals("true");
                    }

                    String valLower = valeurComparaison.toLowerCase().trim();
                    boolean valComp = valLower.equals("oui") || valLower.equals("vrai") || valLower.equals("true");

                    if ("=".equals(operateur)) {
                        resultatCondition = (valVar == valComp);
                    } else if ("≠".equals(operateur)) {
                        resultatCondition = (valVar != valComp);
                    }
                } 
                else {
                    // Type TEXTE ou autre
                    String valVar = cibleActuelle.valeur.toString();
                    String valComp = valeurComparaison != null ? valeurComparaison : "";

                    switch (operateur) {
                        case "=": resultatCondition = valVar.equals(valComp); break;
                        case "≠": resultatCondition = !valVar.equals(valComp); break;
                        default:
                            int cmp = valVar.compareTo(valComp);
                            if (">".equals(operateur)) resultatCondition = (cmp > 0);
                            else if ("<".equals(operateur)) resultatCondition = (cmp < 0);
                            else if ("≥".equals(operateur)) resultatCondition = (cmp >= 0);
                            else if ("≤".equals(operateur)) resultatCondition = (cmp <= 0);
                            break;
                    }
                }
            } catch (Exception e) {
                resultatCondition = false;
            }
        }

        // Routage dynamique de l'exécution selon le résultat
        if (resultatCondition) {
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Opérateur", "Comparer à");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Opérateur".equals(nom)) return operateur;
        if ("Comparer à".equals(nom)) return valeurComparaison;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Opérateur".equals(nom)) operateur = valeur;
        if ("Comparer à".equals(nom)) valeurComparaison = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) {
            return TYPE_CHOIX_LISTE;
        }
        return TYPE_TEXTE_LIBRE;
    }
    
    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) {
            return Arrays.asList("=", "≠", ">", "<", "≥", "≤");
        }
        return super.getOptionsChoixListe(nomParametre);
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

        // 1. Priorité à la variable locale de l'objet ciblé
        ObjetBase objTarget = getCibleObjet();
        if (objTarget != null && objTarget.variablesLocales != null) {
            for (Variable v : objTarget.variablesLocales) {
                if (nomCibleVariable.equals(v.nom)) return v; // Résolution dynamique (pas de cache pour éviter les conflits entre clones)
            }
        }

        // 2. Fallback Scène/Globale
        if (cible != null && nomCibleVariable.equals(cible.nom)) return cible;

        if (contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                        for (Variable v : editeur.sceneActive.variablesLocales) {
                            if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                        }
                    }
                    if (editeur.variablesGlobales != null) {
                        for (Variable v : editeur.variablesGlobales) {
                            if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                        }
                    }
                } else {
                    if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.variablesLocales != null) {
                        for (Variable v : NoeudBase.sceneActiveCourante.variablesLocales) {
                            if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                        }
                    }
                    if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.variablesLocales != null) {
                        for (Variable v : NoeudBase.sceneHudActiveCourante.variablesLocales) {
                            if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                        }
                    }

                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Object sceneObj = sceneField.get(contexteApplication);
                    if (sceneObj != null) {
                        java.lang.reflect.Field varsLocalesField = sceneObj.getClass().getField("variablesLocales");
                        List<Variable> varsLocales = (List<Variable>) varsLocalesField.get(sceneObj);
                        if (varsLocales != null) {
                            for (Variable v : varsLocales) {
                                if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                            }
                        }
                    }
                    java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                    List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                    if (globales != null) {
                        for (Variable v : globales) {
                            if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
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
// bas 1
