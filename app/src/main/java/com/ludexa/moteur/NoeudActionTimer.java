// haut 1
package com.ludexa.moteur;

import android.os.Handler;
import android.os.Looper;

public class NoeudActionTimer extends NoeudBase {

    public NoeudActionTimer() {
        super(genererId(), "Timer", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Immédiat", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Après délai", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Délai (secondes)", "1.0", TYPE_NOMBRE);
        this.ajouterParametre("Répétitions", "1", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        propagerExecution("Immédiat");

        float delai = 0f;
        String delaiSaisi = getValeurParametre("Délai (secondes)");
        try {
            if (delaiSaisi != null && !delaiSaisi.isEmpty()) {
                delai = Float.parseFloat(delaiSaisi.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            delai = 0f; 
        }
        
        final long delaiMs = (long) (delai * 1000f);

        int maxRepetitions = 1;
        String repetitionsSaisies = getValeurParametre("Répétitions");
        try {
            if (repetitionsSaisies != null && !repetitionsSaisies.isEmpty()) {
                maxRepetitions = Integer.parseInt(repetitionsSaisies.trim());
            }
        } catch (NumberFormatException e) {
            maxRepetitions = 1; 
        }
        final int nbMax = maxRepetitions;

        final Handler handler = new Handler(Looper.getMainLooper());
        InterfaceEditeur.handlersActifs.add(handler);
        
        handler.postDelayed(new Runnable() {
            int compteur = 0;
            
            @Override
            public void run() {
                propagerExecution("Après délai");
                compteur++;
                
                if (nbMax == 0 || compteur < nbMax) {
                    handler.postDelayed(this, delaiMs);
                }
            }
        }, delaiMs);
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
