// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionTremblement extends NoeudBase {

    private String intensiteStr = "10.0";
    private String dureeStr = "500";

    public NoeudActionTremblement() {
        super(genererId(), "Tremblement de Caméra", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        try {
            VueJeu.tremblementIntensite = Float.parseFloat(intensiteStr);
            VueJeu.tremblementFin = System.currentTimeMillis() + Long.parseLong(dureeStr);
        } catch (Exception e) {}
        
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Intensité", "Durée (ms)");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Intensité".equals(nom)) return intensiteStr;
        if ("Durée (ms)".equals(nom)) return dureeStr;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Intensité".equals(nom)) intensiteStr = valeur;
        else if ("Durée (ms)".equals(nom)) dureeStr = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_NOMBRE;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
