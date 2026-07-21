package com.ludexa.moteur;

public class NoeudActionDeplacer extends NoeudBase {

    private ObjetBase cible;
    private float deplacementX;
    private float deplacementY;

    public NoeudActionDeplacer(ObjetBase cible, float deplacementX, float deplacementY) {
        super(genererId(), "Déplacer Objet", "Action");

        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("X", Port.TYPE_DONNEE_ENTREE));
        this.ajouterPort(new Port("Y", Port.TYPE_DONNEE_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));

        this.cible = cible;
        this.deplacementX = deplacementX;
        this.deplacementY = deplacementY;
    }

    @Override
    public void executer() {
        cible.x += deplacementX;
        cible.y += deplacementY;
        propagerExecution("Suivant");
    }
}
