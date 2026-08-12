// haut 1
package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import java.util.Arrays;
import java.util.List;

public class NoeudActionAfficherDialogue extends NoeudBase {

    private String titreDialogue = "Message";
    private String cleMessageDialogue = "";

    public NoeudActionAfficherDialogue() {
        super(genererId(), "Afficher Dialogue", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            
            // 1. On cherche le chemin du projet pour lire le fichier texte
            String projPath = null;
            try {
                java.lang.reflect.Field field = contexteApplication.getClass().getField("cheminProjet");
                projPath = (String) field.get(contexteApplication);
            } catch(Exception e) {}

            // 2. On traduit la clé en vrai texte
            String vraiMessage = cleMessageDialogue;
            if (projPath != null && cleMessageDialogue != null && !cleMessageDialogue.isEmpty()) {
                java.io.File fichierDialogues = new java.io.File(projPath, "assets_ludexa/Textes/dialogues.txt");
                if (fichierDialogues.exists()) {
                    try {
                        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierDialogues));
                        String ligne;
                        while ((ligne = br.readLine()) != null) {
                            ligne = ligne.trim();
                            if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                            int idxEgal = ligne.indexOf('=');
                            if (idxEgal > 0) {
                                String cle = ligne.substring(0, idxEgal).trim();
                                if (cle.equals(cleMessageDialogue)) {
                                    vraiMessage = ligne.substring(idxEgal + 1).trim();
                                    break;
                                }
                            }
                        }
                        br.close();
                    } catch (Exception e) {}
                }
            }
            
            // 3. On affiche la vraie phrase finale
            final String messageAAfficher = vraiMessage;
            android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
            handler.post(() -> {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexteApplication);
                    if (titreDialogue != null && !titreDialogue.isEmpty()) {
                        builder.setTitle(titreDialogue);
                    }
                    builder.setMessage(messageAAfficher != null ? messageAAfficher : "");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } catch (Exception e) {
                    android.widget.Toast.makeText(contexteApplication, messageAAfficher, android.widget.Toast.LENGTH_LONG).show();
                }
            });
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Titre", "Clé du Dialogue"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Titre".equals(nom)) return titreDialogue;
        if ("Clé du Dialogue".equals(nom)) return cleMessageDialogue;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Titre".equals(nom)) titreDialogue = valeur;
        if ("Clé du Dialogue".equals(nom)) cleMessageDialogue = valeur;
    }
    
    // NOUVEAU : Appel du sélecteur spécifique pour le message
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Clé du Dialogue".equals(nomParametre)) return TYPE_CHOIX_DIALOGUE;
        return super.getTypeEditeurParametre(nomParametre);
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
