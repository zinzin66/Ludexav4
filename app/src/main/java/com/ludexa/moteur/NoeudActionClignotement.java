package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionClignotement extends NoeudBase {

    public NoeudActionClignotement() {
        // Déclaration de l'identité du nœud selon les règles d'architecture
        super(genererId(), "Clignotement", "Action");
        
        // Déclaration immédiate des ports d'entrée et de sortie
        this.ajouterPort(new Port(this, "Entrée", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port(this, "Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // 1. Récupération sécurisée de la cible via la méthode de la classe mère
        // Cela permet l'interception automatique de OBJET_IMPLIQUE
        ObjetBase cibleReelle = getCibleObjet();
        
        if (cibleReelle != null) {
            // 2. Logique de clignotement à appliquer sur cibleReelle
            // (ex: modifier un attribut de visibilité ou d'opacité/alpha)
        }
        
        // 3. Règle d'or : On propage l'exécution pour ne pas briser la chaîne logique
        propagerExecution("Suivant");
    }
}
