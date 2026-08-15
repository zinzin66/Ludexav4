// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventEntreeZone extends NoeudBase {

    private ObjetBase cible;

    public NoeudEventEntreeZone() {
        super(genererId(), "Entrée de zone", "Evenement");
        this.ajouterPort(new Port("Executer", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Executer");
    }

    @Override
    public boolean requiertCibleObjet() { 
        return true; 
    }

    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet; 
    }

    @Override
    public ObjetBase getCibleObjet() { 
        return this.cible; 
    }

    // --- Méthodes obligatoires de NoeudBase ---
    @Override
    public List<String> getNomsParametres() { 
        return new ArrayList<>(); 
    }

    @Override
    public String getValeurParametre(String nom) { 
        return ""; 
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
    }

    @Override
    public boolean utiliseClavierTexte() { 
        return false; 
    }
}
// bas 1

