// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionRotation extends NoeudBase {
    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String angleRotation = "90";

    public NoeudActionRotation() {
        super(genererId(), "Rotation", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            try {
                obj.rotation = Float.parseFloat(angleRotation);
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Angle (degrés)"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Angle (degrés)".equals(nom)) return angleRotation;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Angle (degrés)".equals(nom)) angleRotation = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParam) { return TYPE_NOMBRE; }

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

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
