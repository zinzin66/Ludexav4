// haut 1
package com.ludexa.moteur;

import android.graphics.Color;
import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierCouleur extends NoeudBase {

    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String couleurChoisie = "Noir"; 

    public NoeudActionModifierCouleur() {
        super(genererId(), "Modifier Couleur", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && couleurChoisie != null) {
            int c = Color.BLACK;
            switch (couleurChoisie.trim().toUpperCase()) {
                case "BLEU": c = Color.BLUE; break;
                case "ROUGE": c = Color.RED; break;
                case "VERT": c = Color.GREEN; break;
                case "NOIR": c = Color.BLACK; break;
                case "BLANC": c = Color.WHITE; break;
                case "JAUNE": c = Color.YELLOW; break;
                case "MAGENTA": c = Color.MAGENTA; break;
                case "CYAN": c = Color.CYAN; break;
            }
            cibleActuelle.couleur = c;
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return false; } 

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Couleur".equals(nom)) {
            return NoeudBase.TYPE_COULEUR;
        }
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Couleur"); }

    @Override
    public String getValeurParametre(String nom) { return couleurChoisie; }

    @Override
    public void setValeurParametre(String nom, String valeur) { couleurChoisie = valeur; }

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
