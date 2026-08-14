// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudEventPersonnalise extends NoeudBase {

    private String nomEvenement = "Mon_Calcul";

    public NoeudEventPersonnalise() {
        super(genererId(), "Événement Local", "Événements");
        // AUCUN port d'entrée : c'est un point de départ
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public boolean aDesParametresEditables() { return true; }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Nom de l'événement"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Nom de l'événement".equals(nom)) return nomEvenement;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Nom de l'événement".equals(nom)) nomEvenement = valeur;
    }

    @Override
    public void executer() {
        // Lance l'exécution des blocs connectés à sa sortie
        propagerExecution("Sortie");
    }

    // --- Méthodes obligatoires inutilisées ici ---
    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }

    // Getter utile pour que le nœud d'appel puisse le trouver
    public String getNomEvenement() { return nomEvenement; }
}
// bas 1
