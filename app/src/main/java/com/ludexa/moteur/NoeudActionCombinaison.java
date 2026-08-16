// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionCombinaison extends NoeudBase {

    private transient ObjetBase cibleA;
    private transient ObjetBase cibleB;
    private String nomCibleObjet;
    private String nomCibleObjetB;
    
    private String nomObjetResultat = "";

    public NoeudActionCombinaison() {
        super(genererId(), "Combinaison d'Objets", "Variables & Inventaire");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase objA = getCibleObjet();
        ObjetBase objB = getCibleObjetB();

        if (objA != null && objB != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    
                    ObjetBase modeleResultat = null;
                    for (ObjetBase o : s.objets) {
                        if (nomObjetResultat.equals(o.nom)) {
                            modeleResultat = o;
                            break;
                        }
                    }
                    
                    if (modeleResultat != null) {
                        ObjetBase nouvelObjet = modeleResultat.clonerProfond();
                        nouvelObjet.x = objA.x; 
                        nouvelObjet.y = objA.y;
                        nouvelObjet.visible = true; 
                        
                        try {
                            java.lang.reflect.Method methodZ = s.getClass().getMethod("prochainZOrder");
                            nouvelObjet.zOrder = (int) methodZ.invoke(s);
                        } catch (Exception e) {
                            nouvelObjet.zOrder = 999; 
                        }
                        
                        s.objets.add(nouvelObjet);
                        
                        s.objets.remove(objA);
                        s.objets.remove(objB);
                    }
                }
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Nom Objet Résultat");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Nom Objet Résultat".equals(nom)) return nomObjetResultat;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Nom Objet Résultat".equals(nom)) nomObjetResultat = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_TEXTE_LIBRE;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cibleA = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if (cibleA == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    for (ObjetBase o : s.objets) {
                        if (nomCibleObjet.equals(o.nom)) { cibleA = o; break; }
                    }
                }
            } catch (Exception e) {}
        }
        return cibleA;
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
