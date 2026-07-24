package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class NoeudBase {
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

    // --- NOUVELLES METHODES POUR L'EDITION DYNAMIQUE ---
    public abstract List<String> getNomsParametres();
    public abstract String getValeurParametre(String nom);
    public abstract void setValeurParametre(String nom, String valeur);
    public abstract boolean requiertCibleObjet();
    public abstract void setCibleObjet(ObjetBase objet);
    public abstract ObjetBase getCibleObjet();
}
