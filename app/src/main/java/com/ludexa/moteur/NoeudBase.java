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

    public static final String TYPE_TEXTE_LIBRE = "TYPE_TEXTE_LIBRE";
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
// bas 1
