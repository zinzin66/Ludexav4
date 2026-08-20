// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionOrienterVers extends NoeudBase {
    private transient ObjetBase cible;
    private transient ObjetBase cibleB;
    private String nomCibleObjet;
    private String nomCibleObjetB;
    
    private String decalageAngle = "0";

    public NoeudActionOrienterVers() {
        super(genererId(), "Orienter vers (Look At)", "Logique Spatiale");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase objA = getCibleObjet();
        ObjetBase objB = getCibleObjetB();

        if (objA != null && objB != null) {
            float centreAX = objA.x + (objA.largeur / 2f);
            float centreAY = objA.y + (objA.hauteur / 2f);
            float centreBX = objB.x + (objB.largeur / 2f);
            float centreBY = objB.y + (objB.hauteur / 2f);
            
            float dx = centreBX - centreAX;
            float dy = centreBY - centreAY;
            
            // Calcul de l'angle en radians puis conversion en degrés
            double angleRadian = Math.atan2(dy, dx);
            double angleDegre = Math.toDegrees(angleRadian);
            
            float decalage = 0f;
            try {
                decalage = Float.parseFloat(decalageAngle);
            } catch (Exception e) {}
            
            objA.rotation = (float) (angleDegre + decalage);
        }
        
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Décalage Angle (degrés)"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Décalage Angle (degrés)".equals(nom)) return decalageAngle;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Décalage Angle (degrés)".equals(nom)) decalageAngle = valeur;
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
    public boolean requiertCibleObjetB() { return true; }
    
    @Override
    public void setCibleObjetB(ObjetBase objet) {
        this.cibleB = objet;
        this.nomCibleObjetB = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjetB() {
        if (cibleB == null && nomCibleObjetB != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    for (ObjetBase o : s.objets) {
                        if (nomCibleObjetB.equals(o.nom)) { cibleB = o; break; }
                    }
                }
            } catch (Exception e) {}
        }
        return cibleB;
    }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
