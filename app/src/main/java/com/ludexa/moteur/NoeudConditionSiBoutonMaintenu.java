// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudConditionSiBoutonMaintenu extends NoeudBase {

    private ObjetBase cibleBouton; // L'Objet A (Le bouton de l'interface)

    public NoeudConditionSiBoutonMaintenu() {
        super(genererId(), Traducteur.get("noeud_si_bouton_maintenu"), "Condition");
        
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // On vérifie simplement l'état du bouton
        boolean boutonAppuye = (cibleBouton != null && cibleBouton.estTouche);

        if (boutonAppuye) {
            propagerExecution("Vrai"); 
        } else {
            propagerExecution("Faux"); 
        }
    }

    // Le nœud n'a plus besoin de paramètres (plus de liste déroulante ni de vitesse)
    @Override
    public List<String> getNomsParametres() {
        return null;
    }

    @Override
    public String getValeurParametre(String nom) {
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        // Plus rien à paramétrer
    }

    // On garde uniquement la sélection de l'Objet A (Le bouton)
    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleBouton = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cibleBouton; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; }
}
// bas 1
