// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionDistance extends NoeudBase {
    private transient ObjetBase cible;
    private transient ObjetBase cibleB;
    private String nomCibleObjet;
    private String nomCibleObjetB;
    
    private String operateur = "<";
    private String distanceValeur = "100";

    public NoeudConditionDistance() {
        super(genererId(), "Distance entre A et B", "Logique Spatiale");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase objA = getCibleObjet();
        ObjetBase objB = getCibleObjetB();
        
        boolean resultat = false;

        if (objA != null && objB != null) {
            // Calcul à partir du centre des objets
            float centreAX = objA.x + (objA.largeur / 2f);
            float centreAY = objA.y + (objA.hauteur / 2f);
            float centreBX = objB.x + (objB.largeur / 2f);
            float centreBY = objB.y + (objB.hauteur / 2f);
            
            float dx = centreBX - centreAX;
            float dy = centreBY - centreAY;
            
            // Théorème de Pythagore pour la distance
            double distanceCalculee = Math.sqrt(dx * dx + dy * dy);
            
            double distanceCible = 0;
            try {
                distanceCible = Double.parseDouble(distanceValeur);
            } catch (Exception e) {}

            switch (operateur) {
                case "<": resultat = distanceCalculee < distanceCible; break;
                case ">": resultat = distanceCalculee > distanceCible; break;
                case "<=": resultat = distanceCalculee <= distanceCible; break;
                case ">=": resultat = distanceCalculee >= distanceCible; break;
                case "==": resultat = distanceCalculee == distanceCible; break;
                case "!=": resultat = distanceCalculee != distanceCible; break;
            }
        }
        
        if (resultat) {
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Opérateur", "Distance (pixels)"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Opérateur".equals(nom)) return operateur;
        if ("Distance (pixels)".equals(nom)) return distanceValeur;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Opérateur".equals(nom)) operateur = valeur;
        else if ("Distance (pixels)".equals(nom)) distanceValeur = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) return TYPE_CHOIX_LISTE;
        return TYPE_NOMBRE;
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("Opérateur".equals(nomParametre)) return Arrays.asList("<", ">", "<=", ">=", "==", "!=");
        return super.getOptionsChoixListe(nomParametre);
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
