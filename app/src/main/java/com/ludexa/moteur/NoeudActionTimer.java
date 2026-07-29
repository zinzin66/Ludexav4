// haut 1
package com.ludexa.moteur;

import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import java.util.List;

public class NoeudActionTimer extends NoeudBase {

    private String delaiSaisi = "1.0"; // Valeur par défaut

    public NoeudActionTimer() {
        super(genererId(), "Timer", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Immédiat", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Après délai", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // 1. Exécution immédiate du premier port
        propagerExecution("Immédiat");

        // 2. Conversion sécurisée du délai saisi en float
        float delai = 0f;
        try {
            if (delaiSaisi != null && !delaiSaisi.isEmpty()) {
                // Remplacement de la virgule par un point pour éviter un NumberFormatException
                delai = Float.parseFloat(delaiSaisi.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            delai = 0f; // En cas d'erreur de saisie (ex: texte au lieu de chiffres), on met 0
        }
        
        long delaiMs = (long) (delai * 1000f);

        // 3. Planification de l'exécution retardée sur le thread principal de l'UI
        Handler handler = new Handler(Looper.getMainLooper());
        InterfaceEditeur.handlersActifs.add(handler);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                propagerExecution("Après délai");
            }
        }, delaiMs);
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Délai (secondes)"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Délai (secondes)".equals(nom)) return delaiSaisi;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Délai (secondes)".equals(nom)) delaiSaisi = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Délai (secondes)".equals(nomParametre)) {
            return TYPE_NOMBRE; // Fait appel au clavier numérique si le moteur le gère
        }
        return super.getTypeEditeurParametre(nomParametre);
    }

    // Ce nœud n'a pas de cible objet
    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }

    // Ce nœud n'a pas de cible variable
    @Override
    public boolean requiertCibleVariable() { return false; }
    
    @Override
    public void setCibleVariable(Variable v) { }
    
    @Override
    public Variable getCibleVariable() { return null; }

    // Autorise l'ouverture du clavier pour éditer le paramètre de texte
    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}
// bas 1
