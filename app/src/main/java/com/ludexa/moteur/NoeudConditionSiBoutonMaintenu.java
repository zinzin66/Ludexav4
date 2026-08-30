package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionSiBoutonMaintenu extends NoeudBase {

    private ObjetBase cibleJoueur; // Objet A (L'entité qui se déplace)
    private ObjetBase cibleBouton; // Objet B (L'élément d'interface tactile)
    private float vitesse = 5f;
    private String directionRequise = "➡️"; 

    public NoeudConditionSiBoutonMaintenu() {
        // Le nom s'adapte automatiquement à la langue choisie dans les paramètres
        super(genererId(), Traducteur.get("noeud_si_bouton_maintenu"), "Condition");
        
        // Note : On garde les noms de ports en dur ("Entrer", "Vrai", "Faux") 
        // pour ne pas corrompre les liaisons de sauvegarde des blueprints si tu changes de langue.
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // Vérifie si le doigt est posé sur l'Objet B
        // (Assure-toi que la méthode s'appelle bien estTouche() ou adapte-la avec le nom exact de ta méthode dans ObjetBase)
        boolean boutonAppuye = (cibleBouton != null && cibleBouton.estTouche());

        if (boutonAppuye) {
            // Si le bouton est enfoncé, on déplace physiquement l'Objet A
            if (cibleJoueur != null) {
                if ("➡️".equals(directionRequise)) cibleJoueur.x += vitesse;
                else if ("⬅️".equals(directionRequise)) cibleJoueur.x -= vitesse;
                else if ("⬇️".equals(directionRequise)) cibleJoueur.y += vitesse; 
                else if ("⬆️".equals(directionRequise)) cibleJoueur.y -= vitesse;
            }

            // On valide la condition pour déclencher l'animation associée
            propagerExecution("Vrai"); 
        } else {
            // Le bouton n'est pas touché, on passe au bouton suivant dans la cascade
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
        } catch (NumberFormatException e) {
            // Ignore silencieusement l'erreur de conversion
        }
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Direction".equals(nom)) return NoeudBase.TYPE_CHOIX_LISTE;
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public List<String> getOptionsChoixListe(String nom) {
        if ("Direction".equals(nom)) {
            // Interface universelle sans traduction supplémentaire
            return Arrays.asList("⬆️", "⬇️", "⬅️", "➡️");
        }
        return super.getOptionsChoixListe(nom);
    }

    // --- Configuration de la Cible A (Le joueur) ---
    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleJoueur = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { return this.cibleJoueur; }

    // --- Configuration de la Cible B (Le bouton) ---
    @Override
    public boolean requiertCibleObjetB() { return true; }
    
    @Override
    public void setCibleObjetB(ObjetBase objet) { this.cibleBouton = objet; }
    
    @Override
    public ObjetBase getCibleObjetB() { return this.cibleBouton; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
