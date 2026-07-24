// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
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
        // 1. Transmettre automatiquement les valeurs des ports de données sortants vers les ports entrants connectés
        for (Port pSortie : this.portsSortie) {
            if (pSortie.type.equals(Port.TYPE_DONNEE_SORTIE) && pSortie.portDestination != null) {
                pSortie.portDestination.valeurSaisie = pSortie.valeurSaisie;
            }
        }

        // 2. Propager l'exécution au nœud suivant
        Port port = trouverPort(this.portsSortie, nomPortSortie);
        if (port != null && port.noeudDestination != null) {
            port.noeudDestination.executer();
        }
    }

    protected static String genererId() {
        return UUID.randomUUID().toString();
    }

    public abstract void executer();
}
// bas 1
