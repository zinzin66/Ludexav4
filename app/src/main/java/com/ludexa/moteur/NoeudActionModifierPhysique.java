// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierPhysique extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    // Par défaut, l'activation rend l'objet dynamique (soumis à la gravité)
    private String valeurDynamique = "true"; 

    public NoeudActionModifierPhysique() {
        super(genererId(), "Activer Physique (Chute)", "Physique");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && valeurDynamique != null) {
            String val = valeurDynamique.trim().toLowerCase();
            boolean estDynamique = val.equals("true") || val.equals("vrai") || val.equals("oui");
            
            cibleActuelle.estPhysique = true; // On garantit que le moteur physique le prend en compte
            cibleActuelle.estStatique = !estDynamique; // Si Dynamique = vrai, Statique = faux (il tombe)
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { 
        return true; 
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Dynamique (Vrai = Tombe)"); 
    }

    @Override
    public String getValeurParametre(String nom) { 
        return valeurDynamique; 
    }

    @Override
    public void setValeurParametre(String nom, String valeur) { 
        valeurDynamique = valeur; 
    }

    @Override
    public boolean requiertCibleObjet() { 
        return true; 
    }
    
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

