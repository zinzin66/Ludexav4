// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class NoeudBase {
    // Variable ajoutée pour permettre l'affichage des Toasts à l'écran
    public static Context contexteApplication;

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
            // Affichage direct sur l'écran de la tablette en cas de désynchronisation
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
    public abstract boolean requiertCibleObjet();
    public abstract void setCibleObjet(ObjetBase objet);
    public abstract ObjetBase getCibleObjet();
    
    public boolean aDesParametresEditables() {
        return (getNomsParametres() != null && !getNomsParametres().isEmpty()) || requiertCibleObjet();
    }
}
// bas 1
