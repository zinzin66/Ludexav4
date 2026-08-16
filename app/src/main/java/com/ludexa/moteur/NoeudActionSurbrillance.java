// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionSurbrillance extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String etat = "Activer";
    private String couleur = "Jaune";

    public NoeudActionSurbrillance() {
        super(genererId(), "Surbrillance (Glow)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.surbrillanceActive = "Activer".equals(etat);
            obj.couleurSurbrillance = couleur;
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("État", "Couleur");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("État".equals(nom)) return etat;
        if ("Couleur".equals(nom)) return couleur;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("État".equals(nom)) etat = valeur;
        else if ("Couleur".equals(nom)) couleur = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("État".equals(nomParametre)) return TYPE_CHOIX_LISTE;
        if ("Couleur".equals(nomParametre)) return TYPE_COULEUR;
        return super.getTypeEditeurParametre(nomParametre);
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("État".equals(nomParametre)) return Arrays.asList("Activer", "Désactiver");
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
}
// bas 1

