// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionCreerObjet extends NoeudBase {

    private transient ObjetBase cibleTemplate;
    // SUPPRIMÉ : private String nomCibleObjet;[span_6](start_span)[span_6](end_span)
    
    private String posX = "";
    private String posY = "";

    public NoeudActionCreerObjet() {
        super(genererId(), "Créer Objet (Cloner)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executer() {
        ObjetBase template = getCibleObjet();
        if (template != null && contexteApplication != null) {
            ObjetBase clone = template.clonerProfond();
            clone.id = java.util.UUID.randomUUID().toString();
            
            try {
                if (!posX.trim().isEmpty()) clone.x = Float.parseFloat(posX.trim());
                if (!posY.trim().isEmpty()) clone.y = Float.parseFloat(posY.trim());
            } catch (NumberFormatException e) {
                // Reste à la position par défaut du template si les champs sont invalides
            }

            try {
                Scene sceneCible = null;
                if (contexteApplication instanceof InterfaceEditeur) {
                    sceneCible = ((InterfaceEditeur) contexteApplication).sceneActive;
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    sceneCible = (Scene) sceneField.get(contexteApplication);
                }
                
                if (sceneCible != null && sceneCible.objets != null) {
                    clone.zOrder = sceneCible.prochainZOrder();
                    sceneCible.ajouterObjet(clone);
                }
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Position X", "Position Y"); }

    @Override
    public String getValeurParametre(String nom) { 
        if ("Position X".equals(nom)) return posX;
        if ("Position Y".equals(nom)) return posY;
        return ""; 
    }

    @Override
    public void setValeurParametre(String nom, String valeur) { 
        if ("Position X".equals(nom)) posX = valeur;
        else if ("Position Y".equals(nom)) posY = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_NOMBRE;
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cibleTemplate = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjet() {
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (cibleTemplate == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    Scene s = ((InterfaceEditeur) contexteApplication).sceneActive;
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cibleTemplate = o;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cibleTemplate = o;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cibleTemplate;
    }
}
// bas 1
