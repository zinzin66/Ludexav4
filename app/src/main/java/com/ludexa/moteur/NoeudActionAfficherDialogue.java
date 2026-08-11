// haut 1
package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import java.util.Arrays;
import java.util.List;

public class NoeudActionAfficherDialogue extends NoeudBase {

    private String titreDialogue = "Message";
    private String messageDialogue = "";

    public NoeudActionAfficherDialogue() {
        super(genererId(), "Afficher Dialogue", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
            handler.post(() -> {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexteApplication);
                    if (titreDialogue != null && !titreDialogue.isEmpty()) {
                        builder.setTitle(titreDialogue);
                    }
                    builder.setMessage(messageDialogue != null ? messageDialogue : "");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } catch (Exception e) {
                    android.widget.Toast.makeText(contexteApplication, messageDialogue, android.widget.Toast.LENGTH_LONG).show();
                }
            });
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Titre", "Message"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Titre".equals(nom)) return titreDialogue;
        if ("Message".equals(nom)) return messageDialogue;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Titre".equals(nom)) titreDialogue = valeur;
        if ("Message".equals(nom)) messageDialogue = valeur;
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




