// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class NoeudAppelFonction extends NoeudBase {

    private String nomFonction = "";

    public NoeudAppelFonction() {
        super(genererId(), "Appeler Fonction", "Logique & Conditions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public boolean aDesParametresEditables() {
        return true; 
    }
    
    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Fonction à appeler");
    }
    
    @Override
    public String getValeurParametre(String nom) {
        if ("Fonction à appeler".equals(nom)) return nomFonction;
        return "";
    }
    
    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Fonction à appeler".equals(nom)) nomFonction = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Fonction à appeler".equals(nom)) return NoeudBase.TYPE_CHOIX_FONCTION;
        return NoeudBase.TYPE_TEXTE_LIBRE;
    }
}
// bas 1
