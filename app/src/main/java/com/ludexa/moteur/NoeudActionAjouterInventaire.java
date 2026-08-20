// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionAjouterInventaire extends NoeudBase {
    private ObjetBase objetCible;
    private transient Variable cible;
    private String nomCibleVariable;

    public NoeudActionAjouterInventaire() {
        super(genererId(), "Ajouter à l'inventaire", "Actions");
        this.ajouterPort(new Port("Entrée", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executer() {
        Variable var = getCibleVariable();
        ObjetBase objet = getCibleObjet();
        if (var != null && "LISTE_INVENTAIRE".equals(var.type) && var.valeur instanceof List && objet != null) {
            List<String> liste = (List<String>) var.valeur;
            if (!liste.contains(objet.id)) {
                liste.add(objet.id);
            }
        }
        propagerExecution("Sortie");
    }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }
    @Override
    public String getValeurParametre(String nom) { return null; }
    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return true; }
    @Override
    public void setCibleObjet(ObjetBase objet) { this.objetCible = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;
        return this.objetCible; 
    }

    @Override
    public boolean requiertCibleVariable() { return true; }
    @Override
    public void setCibleVariable(Variable v) {
        this.cible = v;
        this.nomCibleVariable = (v != null) ? v.nom : null;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Variable getCibleVariable() {
        if (cible == null && nomCibleVariable != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                        for (Variable v : editeur.sceneActive.variablesLocales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                    if (cible == null && editeur.variablesGlobales != null) {
                        for (Variable v : editeur.variablesGlobales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible;
    }
}
// bas 1
