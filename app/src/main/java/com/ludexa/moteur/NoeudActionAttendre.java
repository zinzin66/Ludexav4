// haut 1
package com.ludexa.moteur;

import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import java.util.List;

public class NoeudActionAttendre extends NoeudBase {
    private String delaiMs = "1000";

    public NoeudActionAttendre() {
        super(genererId(), "Attendre (ms)", "Temps");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        long delai = 1000;
        try {
            delai = Long.parseLong(delaiMs);
        } catch (Exception e) {}

        if (delai <= 0) {
            propagerExecution("Suivant");
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    propagerExecution("Suivant");
                }
            }, delai);
        }
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Délai (ms)"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Délai (ms)".equals(nom)) return delaiMs;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Délai (ms)".equals(nom)) delaiMs = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParam) { return TYPE_NOMBRE; }

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
