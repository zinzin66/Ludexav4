// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierTaille extends NoeudBase {
    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String scaleXStr = "1.0";
    private String scaleYStr = "1.0";

    public NoeudActionModifierTaille() {
        super(genererId(), "Définir la taille (Scale)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            try {
                obj.scaleX = Float.parseFloat(scaleXStr);
                obj.scaleY = Float.parseFloat(scaleYStr);
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Scale X", "Scale Y"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Scale X".equals(nom)) return scaleXStr;
        if ("Scale Y".equals(nom)) return scaleYStr;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Scale X".equals(nom)) scaleXStr = valeur;
        else if ("Scale Y".equals(nom)) scaleYStr = valeur;
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
