// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierTexte extends NoeudBase {

    private transient ObjetBase cible;
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
                        Variable v = trouverVariable(nomVar, cibleActuelle);
                        DiagLogger.log(NoeudBase.cheminProjetCourant, "MODIFIER_TEXTE token(+): nomVar=" + nomVar + " trouve=" + (v != null) + " valeur=" + (v != null ? v.valeur : "null"));
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
                    Variable v = trouverVariable(nomVar, cibleActuelle);
                    DiagLogger.log(NoeudBase.cheminProjetCourant, "MODIFIER_TEXTE token(fin): nomVar=" + nomVar + " trouve=" + (v != null) + " valeur=" + (v != null ? v.valeur : "null"));
                    resultatFinal.append(v != null && v.valeur != null ? v.valeur.toString() : "");
                }
            } else {
                resultatFinal.append(tokenCourant.toString());
            }

            DiagLogger.log(NoeudBase.cheminProjetCourant, "MODIFIER_TEXTE resultat final pour " + cibleActuelle.nom + " : \"" + resultatFinal.toString() + "\"");

            cibleActuelle.contenuTexte = resultatFinal.toString();
        }
        propagerExecution("Suivant");
    }

    @SuppressWarnings("unchecked")
    private Variable trouverVariable(String nomVar, ObjetBase cibleActuelle) {
        // Résolution prioritaire sur l'instance de l'objet CIBLE DU NŒUD (ex: TexteVar)
        if (cibleActuelle != null && cibleActuelle.variablesLocales != null) {
            for (Variable v : cibleActuelle.variablesLocales) {
                if (v.nom.equals(nomVar)) return v;
            }
        }

        // CORRECTIF : résolution fiable via les références statiques posées par VueJeu
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

        // CORRECTIF BUG VARIABLE D'INSTANCE (ex: "viealien" appartenant à l'objet "alien",
        // alors que la cible DE CE NŒUD est un autre objet, ex: "TexteVar").
        // AVANT : seule cibleActuelle.variablesLocales était vérifiée pour les variables
        // d'objet -> si le token désigne une variable d'un AUTRE objet de la scène,
        // elle n'était jamais trouvée (trouve=false) alors qu'elle existe bien en mémoire.
        // MAINTENANT : on parcourt les variablesLocales de TOUS les objets de la scène
        // active et de la scène HUD active avant d'abandonner.
        if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
            for (ObjetBase obj : NoeudBase.sceneActiveCourante.objets) {
                if (obj == cibleActuelle || obj.variablesLocales == null) continue;
                for (Variable v : obj.variablesLocales) {
                    if (v.nom.equals(nomVar)) {
                        DiagLogger.log(NoeudBase.cheminProjetCourant, "MODIFIER_TEXTE trouverVariable: '" + nomVar + "' trouve sur objet '" + obj.nom + "' (scene active, hors cible du noeud)");
                        return v;
                    }
                }
            }
        }
        if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
            for (ObjetBase obj : NoeudBase.sceneHudActiveCourante.objets) {
                if (obj == cibleActuelle || obj.variablesLocales == null) continue;
                for (Variable v : obj.variablesLocales) {
                    if (v.nom.equals(nomVar)) {
                        DiagLogger.log(NoeudBase.cheminProjetCourant, "MODIFIER_TEXTE trouverVariable: '" + nomVar + "' trouve sur objet '" + obj.nom + "' (scene HUD active, hors cible du noeud)");
                        return v;
                    }
                }
            }
        }

        // Fallback compatibilité éditeur (inchangé)
        if (contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                for (Variable v : editeur.sceneActive.variablesLocales) {
                    if (v.nom.equals(nomVar)) return v;
                }
            }
            if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                for (ObjetBase obj : editeur.sceneActive.objets) {
                    if (obj.variablesLocales == null) continue;
                    for (Variable v : obj.variablesLocales) {
                        if (v.nom.equals(nomVar)) return v;
                    }
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
                if (scene != null && scene.objets != null) {
                    for (ObjetBase obj : scene.objets) {
                        if (obj.variablesLocales == null) continue;
                        for (Variable v : obj.variablesLocales) {
                            if (v.nom.equals(nomVar)) return v;
                        }
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
        return this.cible;
    }
}
// bas 1
