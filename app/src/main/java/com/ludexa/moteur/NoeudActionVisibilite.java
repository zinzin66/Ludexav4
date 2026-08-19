// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionVisibilite extends NoeudBase {

    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String valeurVisible = "true"; 

    public NoeudActionVisibilite() {
        super(genererId(), "Modifier Visibilité", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && valeurVisible != null) {
            String val = valeurVisible.trim().toLowerCase();
            boolean estVisible = val.equals("true") || val.equals("vrai") || val.equals("oui");
            cibleActuelle.visible = estVisible;
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Visible"); 
    }

    @Override
    public String getValeurParametre(String nom) { 
        return valeurVisible; 
    }

    @Override
    public void setValeurParametre(String nom, String valeur) { 
        valeurVisible = valeur; 
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
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    Scene s = ((InterfaceEditeur) contexteApplication).sceneActive;
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cible = o;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cible = o;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible;
    }
}
// bas 1
