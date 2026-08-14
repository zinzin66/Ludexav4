// haut 1
package com.ludexa.moteur;

public class NoeudActionFixerCamera extends NoeudBase {

    private ObjetBase cible;

    public NoeudActionFixerCamera() {
        super(genererId(), "Fixer Caméra", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (cible != null) {
            GestionnaireControles.cameraCibleId = cible.id;
        } else {
            GestionnaireControles.cameraCibleId = null; // Libère la caméra
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() {
        return true; 
    }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
    }

    @Override
    public ObjetBase getCibleObjet() {
        return this.cible;
    }
}
// bas 1
