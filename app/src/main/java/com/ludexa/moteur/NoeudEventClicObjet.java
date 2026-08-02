// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventClicObjet extends NoeudBase {

    private ObjetBase objetCible;

    public NoeudEventClicObjet() {
        // ID généré, nom, catégorie
        super(genererId(), "Au Clic sur Objet", "Événements");
        
        // Un nœud d'événement n'a généralement qu'un port de sortie d'exécution
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Suivant");
    }

    // --- IMPLÉMENTATION OBLIGATOIRE DES MÉTHODES DE NOEUDBASE ---
    
    @Override
    public List<String> getNomsParametres() {
        // Un nœud de fin de clic n'a pas de paramètres à éditer
        return new ArrayList<>(); 
    }

    @Override
    public String getValeurParametre(String nom) {
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        // Aucun paramètre à modifier
    }

    @Override
    public boolean requiertCibleObjet() {
        // Cet événement requiert un objet cible spécifique pour se déclencher
        return true; 
    }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.objetCible = objet;
    }

    @Override
    public ObjetBase getCibleObjet() {
        return this.objetCible;
    }
}
// bas 1
