package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventStart extends NoeudBase {

    public NoeudEventStart() {
        // ID généré, nom, catégorie
        super(genererId(), "Début", "Événement");
        
        // Un nœud de départ n'a généralement qu'un port de sortie d'exécution
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Suivant");
    }

    // --- IMPLÉMENTATION OBLIGATOIRE DES MÉTHODES DE NOEUDBASE ---
    
    @Override
    public List<String> getNomsParametres() {
        // Un nœud de départ n'a pas de paramètres à éditer (X, Y, etc.)
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
        // L'événement de départ n'a pas besoin de cibler un objet spécifique sur la scène
        return false; 
    }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        // Rien à assigner
    }

    @Override
    public ObjetBase getCibleObjet() {
        return null;
    }
}
