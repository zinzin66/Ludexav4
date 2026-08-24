// haut 1
package com.ludexa.moteur;

import android.os.Handler;
import android.os.Looper;

public class NoeudActionAttendre extends NoeudBase {

    public NoeudActionAttendre() {
        super(genererId(), "Attendre (ms)", "Temps");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Délai (ms)", "1000", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        long delai = 1000;
        try {
            delai = Long.parseLong(getValeurParametre("Délai (ms)"));
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
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
