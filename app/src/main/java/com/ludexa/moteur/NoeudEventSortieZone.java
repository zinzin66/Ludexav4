// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventSortieZone extends NoeudBase {

    private transient ObjetBase cibleA;
    // SUPPRIMÉ ET RENOMMÉ : private String nomCibleObjetA; (Utilise désormais this.nomCibleObjet hérité de NoeudBase)
    
    private transient ObjetBase cibleB;
    // SUPPRIMÉ : private String nomCibleObjetB; (Utilise désormais this.nomCibleObjetB hérité de NoeudBase)
    
    private transient boolean etaitEnCollision = false;

    public NoeudEventSortieZone() {
        super(genererId(), "Sortie de zone", "Événements");
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }
    
    public boolean isEtaitEnCollision() { return etaitEnCollision; }
    public void setEtaitEnCollision(boolean val) { this.etaitEnCollision = val; }

    @Override
    public void executer() {
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) { }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cibleA = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjet() {
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (cibleA == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    Scene s = ((InterfaceEditeur) contexteApplication).sceneActive;
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cibleA = o;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cibleA = o;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cibleA;
    }
    
    @Override
    public boolean requiertCibleObjetB() { return true; }
    
    @Override
    public void setCibleObjetB(ObjetBase objet) { 
        this.cibleB = objet;
        this.nomCibleObjetB = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjetB() {
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjetB)) return MoteurLogique.dernierObjetImplique;

        if (cibleB == null && nomCibleObjetB != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    Scene s = ((InterfaceEditeur) contexteApplication).sceneActive;
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjetB)) cibleB = o;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjetB)) cibleB = o;
                    }
                }
            } catch (Exception e) {}
        }
        return this.cibleB;
    }
}
// bas 1
