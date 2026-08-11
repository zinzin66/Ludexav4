// haut 1
package com.ludexa.moteur;

import java.util.List;

public class NoeudConditionSiObjetVisible extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;

    public NoeudConditionSiObjetVisible() {
        super(genererId(), "Si Objet Visible", "Logique");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            if (obj.visible) {
                propagerExecution("Vrai");
            } else {
                propagerExecution("Faux");
            }
        } else {
            // Sécurité : si l'objet n'existe plus, on considère qu'il n'est pas visible
            propagerExecution("Faux");
        }
    }

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
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                        for (ObjetBase o : editeur.sceneActive.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return cible;
    }

    // Ce nœud n'a pas besoin de paramètres texte supplémentaires
    @Override
    public List<String> getNomsParametres() { return null; }
    @Override
    public String getValeurParametre(String nom) { return ""; }
    @Override
    public void setValeurParametre(String nom, String valeur) {}
}
// bas 1
