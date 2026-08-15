// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.os.Vibrator;
import android.os.Build;
import android.os.VibrationEffect;
import java.util.Arrays;
import java.util.List;

public class NoeudActionVibration extends NoeudBase {

    private String dureeMs = "100";

    public NoeudActionVibration() {
        super(genererId(), "Vibration", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            try {
                long duree = Long.parseLong(dureeMs);
                if (duree > 0) {
                    Vibrator vibrator = (Vibrator) contexteApplication.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null && vibrator.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(duree, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(duree);
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore en cas d'erreur de parsing ou d'absence de permission
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Durée (ms)");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Durée (ms)".equals(nom)) return dureeMs;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Durée (ms)".equals(nom)) dureeMs = valeur;
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
