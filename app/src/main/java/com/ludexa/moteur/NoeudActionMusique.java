// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionMusique extends NoeudBase {

    private String actionMusique = "Jouer"; 
    private String cheminMusique = "";
    private String boucle = "true";

    public NoeudActionMusique() {
        super(genererId(), "Gérer Musique", "Audio");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            if ("Arrêter".equalsIgnoreCase(actionMusique)) {
                GestionnaireAudio.arreterMusique();
            } else if ("Jouer".equalsIgnoreCase(actionMusique) && cheminMusique != null && !cheminMusique.isEmpty()) {
                try {
                    java.lang.reflect.Field field = contexteApplication.getClass().getField("cheminProjet");
                    String projPath = (String) field.get(contexteApplication);
                    if (projPath != null) {
                        String cheminAbsolu = projPath + "/" + cheminMusique;
                        boolean enBoucle = boucle.equalsIgnoreCase("true") || boucle.equalsIgnoreCase("oui");
                        GestionnaireAudio.jouerMusique(cheminAbsolu, enBoucle);
                    }
                } catch(Exception e) {}
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Action (Jouer/Arrêter)", "Fichier Musique", "En boucle (true/false)"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Action (Jouer/Arrêter)".equals(nom)) return actionMusique;
        if ("Fichier Musique".equals(nom)) return cheminMusique;
        if ("En boucle (true/false)".equals(nom)) return boucle;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Action (Jouer/Arrêter)".equals(nom)) actionMusique = valeur;
        if ("Fichier Musique".equals(nom)) cheminMusique = valeur;
        if ("En boucle (true/false)".equals(nom)) boucle = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Fichier Musique".equals(nomParametre)) return TYPE_CHOIX_SON;
        if ("Action (Jouer/Arrêter)".equals(nomParametre)) return TYPE_CHOIX_LISTE;
        return super.getTypeEditeurParametre(nomParametre);
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        if ("Action (Jouer/Arrêter)".equals(nomParametre)) return Arrays.asList("Jouer", "Arrêter");
        return super.getOptionsChoixListe(nomParametre);
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
