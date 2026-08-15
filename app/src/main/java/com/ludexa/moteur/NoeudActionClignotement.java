// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionClignotement extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String etat = "Activer";
    private String vitesseMs = "500";
    private String dureeMs = "0";

    public NoeudActionClignotement() {
        super(genererId(), "Clignotement (Blink)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            if ("Désactiver".equals(etat)) {
                obj.clignotementActif = false;
                obj.etatVisibleClignotement = true; // Force la réapparition
            } else {
                obj.clignotementActif = true;
                obj.tempsDebutClignotement = System.currentTimeMillis();
                try {
                    obj.clignotementVitesseMs = Long.parseLong(vitesseMs);
                    obj.clignotementDureeTotalMs = Long.parseLong(dureeMs);
                } catch (Exception e) {
                    obj.clignotementVitesseMs = 500;
                    obj.clignotementDureeTotalMs = 0;
                }
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("État", "Vitesse (ms)", "Durée (ms, 0=infini)");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("État".equals(nom)) return etat;
        if ("Vitesse (ms)".equals(nom)) return vitesseMs;
        if ("Durée (ms, 0=infini)".equals(nom)) return dureeMs;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("État".equals(nom)) etat = valeur;
        else if ("Vitesse (ms)".equals(nom)) vitesseMs = valeur;
        else if ("Durée (ms, 0=infini)".equals(nom)) dureeMs = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("État".equals(nomParametre)) return TYPE_CHOIX_LISTE;
        return TYPE_NOMBRE;
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("État".equals(nomParametre)) return Arrays.asList("Activer", "Désactiver");
        return super.getOptionsChoixListe(nomParametre);
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }

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
