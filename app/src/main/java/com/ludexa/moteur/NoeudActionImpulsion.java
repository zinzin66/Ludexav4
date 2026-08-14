// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionImpulsion extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String valeurForce = "-15.0"; // Valeur par défaut pour un saut

    public NoeudActionImpulsion() {
        super(genererId(), "Appliquer Impulsion (Saut)", "Physique");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && valeurForce != null) {
            try {
                float force = Float.parseFloat(valeurForce.trim());
                cibleActuelle.vitesseY = force;
                cibleActuelle.estStatique = false; // L'objet devient libre
                cibleActuelle.estPhysique = true;  // On s'assure que la physique est active
            } catch (NumberFormatException e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Force Y (ex: -15 pour sauter)"); }

    @Override
    public String getValeurParametre(String nom) { return valeurForce; }

    @Override
    public void setValeurParametre(String nom, String valeur) { valeurForce = valeur; }

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

