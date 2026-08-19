// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionAvancerContinu extends NoeudBase {
    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String vitesseStr = "5.0";

    public NoeudActionAvancerContinu() {
        super(genererId(), "Avancer en continu", "Mouvements & IA");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            try {
                obj.vitesseAvanceContinue = Float.parseFloat(vitesseStr);
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Vitesse"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Vitesse".equals(nom)) return vitesseStr;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Vitesse".equals(nom)) vitesseStr = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) { return TYPE_NOMBRE; }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

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

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
