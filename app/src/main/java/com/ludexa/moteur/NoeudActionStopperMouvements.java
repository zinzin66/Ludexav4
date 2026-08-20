// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudActionStopperMouvements extends NoeudBase {
    private transient ObjetBase cible;
    private String nomCibleObjet;

    public NoeudActionStopperMouvements() {
        super(genererId(), "Stopper les mouvements", "Mouvements & IA");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.vitesseAvanceContinue = 0f;
            obj.idCiblePoursuite = null;
            obj.vitessePoursuite = 0f;
            obj.fuiteActive = false;
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
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    for (ObjetBase o : s.objets) {
                        if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                    }
                }
            } catch (Exception e) {}
        }
        return cible;
    }
}
// bas 1
