// haut 1
package com.ludexa.moteur;

public class NoeudEventChaqueImage extends NoeudBase {

    public NoeudEventChaqueImage() {
        // Le libellé "À chaque image" utilisera ta clé de traduction habituelle
        super(genererId(), Traducteur.get("noeud_chaque_image"), Traducteur.get("cat_evenements"));
        
        // Étant un événement de déclenchement, il ne possède qu'un port de sortie
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // La boucle principale (Game Loop) de Yop2D appellera cette méthode
        // à chaque rafraîchissement (frame). Le nœud se contente de lancer la suite.
        propagerExecution(Traducteur.get("port_sortie"));
    }
}
// bas 1
