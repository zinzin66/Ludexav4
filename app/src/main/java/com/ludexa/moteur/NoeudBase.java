// haut 1
package com.ludexa.moteur;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public abstract class NoeudBase {
    public static Context contexteApplication;

    // NOUVEAU : références directes à la scène active, pour éviter toute réflexion
    // dans le contexte APK compilé. Mises à jour par VueJeu à chaque changement de scène.
    public static Scene sceneActiveCourante;
    public static Scene sceneHudActiveCourante;
    public static String cheminProjetCourant;

    public static final String TYPE_TEXTE_LIBRE = "TYPE_TEXTE_LIBRE";
    public static final String TYPE_TEXTE_ALPHABETIQUE = "TYPE_TEXTE_ALPHABETIQUE"; // CORRECTIF 2 : Nouveau type
    public static final String TYPE_NOMBRE = "TYPE_NOMBRE";
    public static final String TYPE_COULEUR = "TYPE_COULEUR";
    public static final String TYPE_CHOIX_LISTE = "TYPE_CHOIX_LISTE";
    public static final String TYPE_CHOIX_IMAGE = "TYPE_CHOIX_IMAGE";
    public static final String TYPE_CHOIX_DIALOGUE = "TYPE_CHOIX_DIALOGUE";
    public static final String TYPE_CHOIX_SON = "TYPE_CHOIX_SON";
    public static final String TYPE_CHOIX_FONCTION = "TYPE_CHOIX_FONCTION";

    public String id;
    public String nom;
    public String categorie;
    public ArrayList<Port> portsEntree;
    public ArrayList<Port> portsSortie;
    
    public String nomCibleObjet = null;
    public String nomCibleObjetB = null;

    // --- AJOUT POUR LA SAUVEGARDE DES VARIABLES (Bug C) ---
    public String nomCibleVariable = null;
    protected transient Variable cibleVariableResolue = null;

    // NOUVEAU : références directes posées UNE SEULE FOIS à l'instanciation d'un prefab
    // par VueJeu.instancierSceneInterne(). Transitoires : jamais sérialisées en JSON,
    // donc aucun impact sur la sauvegarde/le chargement des Blueprints.
    // Prioritaires sur la résolution par nom (nomCibleObjet) dans getCibleObjet()/getCibleObjetB().
    protected transient ObjetBase cibleObjetResolue = null;
    protected transient ObjetBase cibleObjetBResolue = null;

    public boolean estReplie = false;

    public static class InfoParametre {
        public String nom;
        public String valeur;
        public String typeEditeur;
        public List<String> optionsListe;

        public InfoParametre(String nom, String valeur, String typeEditeur) {
            this.nom = nom;
            this.valeur = valeur;
            this.typeEditeur = typeEditeur;
            this.optionsListe = new ArrayList<>();
        }
    }

    protected LinkedHashMap<String, InfoParametre> parametresDynamiques = new LinkedHashMap<>();

    public NoeudBase(String id, String nom, String categorie) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.portsEntree = new ArrayList<>();
        this.portsSortie = new ArrayList<>();
    }

    protected void ajouterParametre(String nom, String valeurInitiale, String typeEditeur) {
        parametresDynamiques.put(nom, new InfoParametre(nom, valeurInitiale, typeEditeur));
    }

    protected void ajouterParametreListe(String nom, String valeurInitiale, List<String> options) {
        InfoParametre p = new InfoParametre(nom, valeurInitiale, TYPE_CHOIX_LISTE);
        if (options != null) p.optionsListe.addAll(options);
        parametresDynamiques.put(nom, p);
    }

    public void ajouterPort(Port port) {
        if (port.type.equals(Port.TYPE_EXECUTION_ENTREE) || port.type.equals(Port.TYPE_DONNEE_ENTREE)) {
            this.portsEntree.add(port);
        } else {
            this.portsSortie.add(port);
        }
    }

    public void connecterPort(String nomPortSortie, NoeudBase noeudArrivee, String nomPortEntree) {
        Port portSortie = trouverPort(this.portsSortie, nomPortSortie);
        Port portEntree = trouverPort(noeudArrivee.portsEntree, nomPortEntree);
        if (portSortie != null && portEntree != null) {
            portSortie.noeudDestination = noeudArrivee;
            portSortie.portDestination = portEntree;
        }
    }

    protected Port trouverPort(ArrayList<Port> listePorts, String nomPort) {
        for (Port p : listePorts) {
            if (p.nom.equals(nomPort)) return p;
        }
        return null;
    }

    protected void propagerExecution(String nomPortSortie) {
        for (Port pSortie : this.portsSortie) {
            if (pSortie.type.equals(Port.TYPE_DONNEE_SORTIE) && pSortie.portDestination != null) {
                pSortie.portDestination.valeurSaisie = pSortie.valeurSaisie;
            }
        }
        Port port = trouverPort(this.portsSortie, nomPortSortie);
        if (port != null && port.noeudDestination != null) {
            port.noeudDestination.executer();
        }
    }

    protected static String genererId() { return UUID.randomUUID().toString(); }
// bas 1

// haut 2
    public abstract void executer();

    public List<String> getNomsParametres() {
        return new ArrayList<>(parametresDynamiques.keySet());
    }

    public String getValeurParametre(String nom) {
        InfoParametre p = parametresDynamiques.get(nom);
        return p != null ? p.valeur : "";
    }

    public void setValeurParametre(String nom, String valeur) {
        InfoParametre p = parametresDynamiques.get(nom);
        if (p != null) p.valeur = valeur;
    }

    public String getTypeEditeurParametre(String nomParametre) {
        InfoParametre p = parametresDynamiques.get(nomParametre);
        return p != null ? p.typeEditeur : TYPE_TEXTE_LIBRE;
    }

    public List<String> getOptionsChoixListe(String nomParametre) {
        InfoParametre p = parametresDynamiques.get(nomParametre);
        return (p != null && p.optionsListe != null) ? p.optionsListe : new ArrayList<>();
    }

    public boolean requiertCibleObjet() { return false; }
    public void setCibleObjet(ObjetBase objet) {}

    public static java.util.List<Scene> getScenesDisponibles() {
        if (contexteApplication instanceof FournisseurDonneesJeu) {
            return ((FournisseurDonneesJeu) contexteApplication).getListeScenes();
        }
        return new ArrayList<>();
    }

    public static java.util.List<Variable> getVariablesGlobalesDisponibles() {
        if (contexteApplication instanceof FournisseurDonneesJeu) {
            return ((FournisseurDonneesJeu) contexteApplication).getVariablesGlobales();
        }
        return new ArrayList<>();
    }

    public static java.util.List<String> getTagsDisponibles() {
        java.util.Set<String> tagsUniques = new java.util.HashSet<>();
        tagsUniques.add("Joueur"); 
        
        java.util.List<Scene> toutesLesScenes = null;

        if (contexteApplication instanceof FournisseurDonneesJeu) {
            toutesLesScenes = ((FournisseurDonneesJeu) contexteApplication).getListeScenes();
        } else if (contexteApplication != null && "InterfaceBlueprint".equals(contexteApplication.getClass().getSimpleName())) {
            try {
                java.lang.reflect.Field field = contexteApplication.getClass().getField("listeScenesACharger");
                toutesLesScenes = (java.util.List<Scene>) field.get(null);
            } catch (Exception e) {}
        }

        if (toutesLesScenes != null) {
            for (Scene s : toutesLesScenes) {
                if (s.objets != null) {
                    for (ObjetBase obj : s.objets) {
                        if (obj.tag != null && !obj.tag.trim().isEmpty()) {
                            tagsUniques.add(obj.tag.trim());
                        }
                    }
                }
            }
        }
        
        java.util.List<String> listeFinale = new java.util.ArrayList<>(tagsUniques);
        java.util.Collections.sort(listeFinale);
        return listeFinale;
    }

    public void lierCibleObjetInstance(ObjetBase objet) {
        this.cibleObjetResolue = objet;
    }
    
    public ObjetBase getCibleObjet() { 
        if (cibleObjetResolue != null) return cibleObjetResolue;

        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }
        
        if (nomCibleObjet != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene scene = (Scene) sceneField.get(contexteApplication);
                
                if (scene != null && scene.objets != null) {
                    for (ObjetBase obj : scene.objets) {
                        if (nomCibleObjet.equals(obj.nom)) {
                            return obj;
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return null; 
    }
    
    public boolean requiertCibleObjetB() { return false; }
    public void setCibleObjetB(ObjetBase objet) {}

    public void lierCibleObjetBInstance(ObjetBase objet) {
        this.cibleObjetBResolue = objet;
    }
    
    public ObjetBase getCibleObjetB() { 
        if (cibleObjetBResolue != null) return cibleObjetBResolue;

        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjetB)) {
            return MoteurLogique.dernierObjetImplique;
        }
        
        if (nomCibleObjetB != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene scene = (Scene) sceneField.get(contexteApplication);
                
                if (scene != null && scene.objets != null) {
                    for (ObjetBase obj : scene.objets) {
                        if (nomCibleObjetB.equals(obj.nom)) {
                            return obj;
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return null; 
    }
    
    public boolean requiertCibleVariable() { return false; }
    
    // CORRECTION BUG C : Mémorisation prioritaire sur le Nom seul
    public void setCibleVariable(Variable v) {
        this.cibleVariableResolue = v;
        if (v != null) {
            this.nomCibleVariable = v.nom;
        } else {
            this.nomCibleVariable = null;
        }
    }
    
    public Variable getCibleVariable() { 
        if (cibleVariableResolue != null) return cibleVariableResolue;
        
        if (nomCibleVariable != null && !nomCibleVariable.isEmpty()) {
            Scene scene = null;
            if (contexteApplication != null) {
                try {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    scene = (Scene) sceneField.get(contexteApplication);
                } catch (Exception e) {}
            }
            if (scene == null) scene = sceneActiveCourante;
            
            if (scene != null) {
                // 1. Chercher d'abord sur l'objet cible du noeud (priorité absolue)
                ObjetBase cibleObj = getCibleObjet();
                if (cibleObj != null && cibleObj.variablesLocales != null) {
                    for (Variable v : cibleObj.variablesLocales) {
                        if (nomCibleVariable.equals(v.nom)) return v;
                    }
                }
                
                // 2. Chercher dans les variables de scène
                if (scene.variablesLocales != null) {
                    for (Variable v : scene.variablesLocales) {
                        if (nomCibleVariable.equals(v.nom)) return v;
                    }
                }
                
                // 3. Fallback : chercher dans n'importe quel objet de la scène
                if (scene.objets != null) {
                    for (ObjetBase obj : scene.objets) {
                        if (obj.variablesLocales != null) {
                            for (Variable v : obj.variablesLocales) {
                                if (nomCibleVariable.equals(v.nom)) return v;
                            }
                        }
                    }
                }
            }
            
            // 4. Variables Globales
            List<Variable> globales = getVariablesGlobalesDisponibles();
            if (globales != null) {
                for (Variable v : globales) {
                    if (nomCibleVariable.equals(v.nom)) return v;
                }
            }
        }
        return null;
    }
    
    public boolean requiertCibleScene() { return false; }
    public void setCibleScene(Scene s) {}
    public Scene getCibleScene() { return null; }
    
    public boolean utiliseClavierTexte() { return false; }
    
    public boolean aDesParametresEditables() {
        return (getNomsParametres() != null && !getNomsParametres().isEmpty()) || requiertCibleObjet() || requiertCibleObjetB() || requiertCibleVariable() || requiertCibleScene();
    }
}
// bas 2



