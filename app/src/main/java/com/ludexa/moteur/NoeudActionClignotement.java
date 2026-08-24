// haut 1
package com.ludexa.moteur;

import java.util.Arrays;

public class NoeudActionClignotement extends NoeudBase {

    public NoeudActionClignotement() {
        super(genererId(), "Clignotement (Blink)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        // MIGRATION : Utilisation de la nouvelle architecture pour garantir 
        // la sérialisation Gson (DTO) et l'intégration de l'UI de l'éditeur.
        this.ajouterParametreListe("État", "Activer", Arrays.asList("Activer", "Désactiver"));
        this.ajouterParametre("Vitesse (ms)", "500", TYPE_NOMBRE);
        this.ajouterParametre("Durée (ms, 0=infini)", "0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        // Le ciblage contextuel est géré nativement par NoeudBase 
        // via MoteurLogique.dernierObjetImplique
        ObjetBase obj = getCibleObjet(); 
        
        if (obj != null) {
            // Récupération dynamique via la structure standardisée
            String etat = getValeurParametre("État");
            String vitesseMs = getValeurParametre("Vitesse (ms)");
            String dureeMs = getValeurParametre("Durée (ms, 0=infini)");

            if ("Désactiver".equals(etat)) {
                obj.clignotementActif = false;
                obj.etatVisibleClignotement = true; 
            } else {
                obj.clignotementActif = true;
                obj.tempsDebutClignotement = System.currentTimeMillis();
                try {
                    obj.clignotementVitesseMs = Long.parseLong(vitesseMs);
                    obj.clignotementDureeTotalMs = Long.parseLong(dureeMs);
                } catch (Exception e) {
                    obj.clignotementVitesseMs = 500;
                    obj.clignotementDureeTotalMs = 0;
                }
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { 
        return true; 
    }

    @Override
    public boolean requiertCibleObjet() { 
        return true; 
    }
}
// bas 1
