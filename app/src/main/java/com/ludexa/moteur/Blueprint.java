
// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blueprint {
    public List<NoeudBase> noeuds;
    public List<Lien> liens;
    
    // On stocke les coordonnées X et Y dans le Blueprint,
    // ce qui respecte la règle absolue de ne pas modifier NoeudBase.java
    public Map<String, Float> noeudsX;
    public Map<String, Float> noeudsY;

    public Blueprint() {
        this.noeuds = new ArrayList<>();
        this.liens = new ArrayList<>();
        this.noeudsX = new HashMap<>();
        this.noeudsY = new HashMap<>();
    }

    public void ajouterNoeud(NoeudBase noeud, float x, float y) {
        this.noeuds.add(noeud);
        this.noeudsX.put(noeud.id, x);
        this.noeudsY.put(noeud.id, y);
    }

    // Classe interne pour représenter un lien (prêt pour la tâche 8.5)
    public static class Lien {
        public NoeudBase noeudDepart;
        public String portSortieNom;
        public NoeudBase noeudArrivee;
        public String portEntreeNom;
        
        public Lien(NoeudBase depart, String portS, NoeudBase arrivee, String portE) {
            this.noeudDepart = depart;
            this.portSortieNom = portS;
            this.noeudArrivee = arrivee;
            this.portEntreeNom = portE;
        }
    }
}
// bas 1
