// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudEventPersonnalise extends NoeudBase {

    private String nomEvenement = "Mon_Calcul";

    public NoeudEventPersonnalise() {
        super(genererId(), "Événement Local", "Événements");
        // CORRECTION : On utilise le terme standard "Suivant"
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
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
    public String getTypeEditeurParametre(String nomParametre) {
        // CORRECTIF 2 : Forcer le clavier alphabétique pour le nom de l'événement
        return TYPE_TEXTE_ALPHABETIQUE;
    }

    @Override
    public void executer() {
        // Lance l'exécution des blocs connectés via "Suivant"
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }

    public String getNomEvenement() { return nomEvenement; }
}
// bas 1
