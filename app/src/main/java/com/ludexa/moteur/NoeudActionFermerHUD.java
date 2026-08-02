// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionFermerHUD extends NoeudBase {

    public NoeudActionFermerHUD() {
        super(genererId(), "Fermer HUD", "UI");
        this.ajouterPort(new Port(this, "Entrée", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port(this, "Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication instanceof InterfaceEditeur) {
            ((InterfaceEditeur) contexteApplication).fermerHUD();
        }
        propagerExecution("Sortie");
    }

    @Override
    public boolean requiertCibleScene() { return false; }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }

    @Override
    public String getValeurParametre(String nom) { return null; }

    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
