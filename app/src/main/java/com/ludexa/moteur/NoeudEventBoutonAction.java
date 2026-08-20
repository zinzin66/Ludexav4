// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventBoutonAction extends NoeudBase {
    
    public NoeudEventBoutonAction() {
        super(genererId(), "Au Clic Action (Aventure)", "Evenement");
        this.ajouterPort(new Port("Executer", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Executer");
    }

    // --- Méthodes obligatoires de NoeudBase (même vides) ---
    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }

    @Override
    public boolean utiliseClavierTexte() { return false; }
}
// bas 1
