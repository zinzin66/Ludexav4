// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionJouerSon extends NoeudBase {

    private String cheminSon = "";

    public NoeudActionJouerSon() {
        super(genererId(), "Jouer un Son", "Audio");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null && cheminSon != null && !cheminSon.isEmpty()) {
            try {
                java.lang.reflect.Field field = contexteApplication.getClass().getField("cheminProjet");
                String projPath = (String) field.get(contexteApplication);
                if (projPath != null) {
                    String cheminAbsolu = projPath + "/" + cheminSon;
                    GestionnaireAudio.jouerSon(cheminAbsolu);
                }
            } catch(Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Fichier Son"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Fichier Son".equals(nom)) return cheminSon;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Fichier Son".equals(nom)) cheminSon = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Fichier Son".equals(nomParametre)) return TYPE_CHOIX_SON;
        return super.getTypeEditeurParametre(nomParametre);
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
