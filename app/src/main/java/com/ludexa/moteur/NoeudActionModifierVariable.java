// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierVariable extends NoeudBase {

    private Variable cible;
    private String valeurSaisie = "";

    public NoeudActionModifierVariable() {
        super(genererId(), "Modifier Variable", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (cible != null) {
            if ("CHIFFRE".equals(cible.type)) {
                try {
                    cible.valeur = Float.parseFloat(valeurSaisie);
                } catch (NumberFormatException e) {
                    cible.valeur = 0f;
                }
            } else if ("BOOLEEN".equals(cible.type)) {
                String valLower = valeurSaisie.toLowerCase().trim();
                cible.valeur = valLower.equals("oui") || valLower.equals("vrai") || valLower.equals("true");
            } else {
                cible.valeur = valeurSaisie;
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Valeur");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Valeur".equals(nom)) return valeurSaisie;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Valeur".equals(nom)) valeurSaisie = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    @Override
    public ObjetBase getCibleObjet() { return null; }

    @Override
    public boolean requiertCibleVariable() { return true; }
    @Override
    public void setCibleVariable(Variable v) { this.cible = v; }
    @Override
    public Variable getCibleVariable() { return this.cible; }
}
// bas 1
