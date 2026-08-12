// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionJouerAnimation extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String nomAnimation = "Ouverture";
    private String vitesse = "8";
    private String boucle = "false";

    public NoeudActionJouerAnimation() {
        super(genererId(), "Jouer Animation", "Animations");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.animationActive = nomAnimation;
            obj.frameCourante = 0;
            obj.dernierTempsFrame = System.currentTimeMillis();
            try {
                obj.vitesseFps = Integer.parseInt(vitesse);
            } catch (Exception e) {
                obj.vitesseFps = 8;
            }
            obj.boucleAnimation = boucle.equalsIgnoreCase("true") || boucle.equalsIgnoreCase("oui");
            obj.animationEnCours = true;
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Nom Animation", "Vitesse (FPS)", "Boucle (true/false)"); }
    @Override
    public String getValeurParametre(String nom) {
        if ("Nom Animation".equals(nom)) return nomAnimation;
        if ("Vitesse (FPS)".equals(nom)) return vitesse;
        if ("Boucle (true/false)".equals(nom)) return boucle;
        return "";
    }
    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Nom Animation".equals(nom)) nomAnimation = valeur;
        if ("Vitesse (FPS)".equals(nom)) vitesse = valeur;
        if ("Boucle (true/false)".equals(nom)) boucle = valeur;
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
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1



