// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class NoeudBase {
    public static Context contexteApplication;

    // NOUVEAU : Constantes définissant les types d'éditeurs possibles
    public static final String TYPE_TEXTE_LIBRE = "TYPE_TEXTE_LIBRE";
    public static final String TYPE_NOMBRE = "TYPE_NOMBRE";
    public static final String TYPE_COULEUR = "TYPE_COULEUR";
    public static final String TYPE_CHOIX_LISTE = "TYPE_CHOIX_LISTE";

    public String id;
    public String nom;
    public String categorie;
    public ArrayList<Port> portsEntree;
    public ArrayList<Port> portsSortie;

    public NoeudBase(String id, String nom, String categorie) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.portsEntree = new ArrayList<>();
        this.portsSortie = new ArrayList<>();
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
        } else {
            if (contexteApplication != null) {
                if (portSortie == null) {
                    Toast.makeText(contexteApplication, "ERREUR : port " + nomPortSortie + " introuvable sur " + this.nom, Toast.LENGTH_LONG).show();
                }
                if (portEntree == null) {
                    Toast.makeText(contexteApplication, "ERREUR : port " + nomPortEntree + " introuvable sur " + noeudArrivee.nom, Toast.LENGTH_LONG).show();
                }
            }
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

    protected static String genererId() {
        return UUID.randomUUID().toString();
    }

    public abstract void executer();

    public abstract List<String> getNomsParametres();
    public abstract String getValeurParametre(String nom);
    public abstract void setValeurParametre(String nom, String valeur);
    
    // Contrats existants
    public abstract boolean requiertCibleObjet();
    public abstract void setCibleObjet(ObjetBase objet);
    public abstract ObjetBase getCibleObjet();
    
    public boolean requiertCibleVariable() { return false; }
    public void setCibleVariable(Variable v) {}
    public Variable getCibleVariable() { return null; }
    
    // NOUVEAU : Méthode pour déterminer le type de clavier à afficher
    public boolean utiliseClavierTexte() { return false; }

    // NOUVEAU : Déclaration générique du type d'éditeur pour un paramètre
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_TEXTE_LIBRE; // Valeur par défaut
    }
    
    // NOUVEAU : Fournit les options pour un type TYPE_CHOIX_LISTE
    public List<String> getOptionsChoixListe(String nomParametre) {
        return new ArrayList<>();
    }
    
    public boolean aDesParametresEditables() {
        return (getNomsParametres() != null && !getNomsParametres().isEmpty()) || requiertCibleObjet() || requiertCibleVariable();
    }
}
// bas 1


