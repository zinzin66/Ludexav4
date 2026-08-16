// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionCheckpoint extends NoeudBase {

    public NoeudActionCheckpoint() {
        super(genererId(), "Point de Sauvegarde", "Variables & Inventaire");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null) {
                    GestionnaireEtat.sauvegarderEtat(s);
                }
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return null; }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    
    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
