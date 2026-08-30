// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionSiBoutonMaintenu extends NoeudBase {

    private ObjetBase cibleJoueur; 
    private ObjetBase cibleBouton; 
    private float vitesse = 5f;
    private String directionRequise = "➡️"; 

    public NoeudConditionSiBoutonMaintenu() {
        super(genererId(), Traducteur.get("noeud_si_bouton_maintenu"), "Condition");
        
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // CORRECTION : Appel de la variable directe au lieu de la méthode
        boolean boutonAppuye = (cibleBouton != null && cibleBouton.estTouche);

        if (boutonAppuye) {
            if (cibleJoueur != null) {
                if ("➡️".equals(directionRequise)) cibleJoueur.x += vitesse;
                else if ("⬅️".equals(directionRequise)) cibleJoueur.x -= vitesse;
                else if ("⬇️".equals(directionRequise)) cibleJoueur.y += vitesse; 
                else if ("⬆️".equals(directionRequise)) cibleJoueur.y -= vitesse;
            }
            propagerExecution("Vrai"); 
        } else {
            propagerExecution("Faux"); 
        }
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Vitesse", "Direction");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Vitesse".equals(nom)) return String.valueOf(vitesse);
        if ("Direction".equals(nom)) return directionRequise;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        try {
            if ("Vitesse".equals(nom)) vitesse = Float.parseFloat(valeur);
            if ("Direction".equals(nom)) directionRequise = valeur;
        } catch (NumberFormatException e) {}
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Direction".equals(nom)) return NoeudBase.TYPE_CHOIX_LISTE;
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public List<String> getOptionsChoixListe(String nom) {
        if ("Direction".equals(nom)) {
            return Arrays.asList("⬆️", "⬇️", "⬅️", "➡️");
        }
        return super.getOptionsChoixListe(nom);
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleJoueur = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cibleJoueur; }

    @Override
    public boolean requiertCibleObjetB() { return true; }
    
    @Override
    public void setCibleObjetB(ObjetBase objet) { this.cibleBouton = objet; }
    
    @Override
    public ObjetBase getCibleObjetB() { return this.cibleBouton; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
