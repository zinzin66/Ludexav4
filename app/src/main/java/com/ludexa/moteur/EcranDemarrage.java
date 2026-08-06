// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.io.File;

public class EcranDemarrage extends Activity {

    private ListView listeProjets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Conteneur principal
        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.HORIZONTAL);
        layoutPrincipal.setBackgroundColor(Palette.fondNormal);

        // Colonne de gauche
        LinearLayout colonneGauche = new LinearLayout(this);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        colonneGauche.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams paramsGauche = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        colonneGauche.setLayoutParams(paramsGauche);

        // BOUTON LANGUE
        Button boutonLangue = new Button(this);
        boutonLangue.setText("Langue : Français");
        boutonLangue.setBackgroundColor(Palette.boutonNormal);
        boutonLangue.setTextColor(Palette.texteNormal);
        
        android.graphics.drawable.Drawable iconeLangue = getResources().getDrawable(R.drawable.language_24px).mutate();
        iconeLangue.setColorFilter(Palette.texteNormal, android.graphics.PorterDuff.Mode.SRC_IN);
        boutonLangue.setCompoundDrawablesWithIntrinsicBounds(iconeLangue, null, null, null);
        boutonLangue.setCompoundDrawablePadding(15);
        boutonLangue.setOnClickListener(v -> {});
        
        LinearLayout.LayoutParams margeBouton1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
        margeBouton1.gravity = Gravity.CENTER_HORIZONTAL;
        margeBouton1.setMargins(30, 15, 30, 15);
        colonneGauche.addView(boutonLangue, margeBouton1);

        // Colonne de droite
        LinearLayout colonneDroite = new LinearLayout(this);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams paramsDroite = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        colonneDroite.setLayoutParams(paramsDroite);

        // BOUTON CRÉER PROJET
        Button boutonCreerProjet = new Button(this);
        boutonCreerProjet.setText("Créer un projet");
        boutonCreerProjet.setBackgroundColor(Palette.boutonNormal);
        boutonCreerProjet.setTextColor(Palette.texteNormal);
        
        android.graphics.drawable.Drawable iconeCreer = getResources().getDrawable(R.drawable.add_24px).mutate();
        iconeCreer.setColorFilter(Palette.texteNormal, android.graphics.PorterDuff.Mode.SRC_IN);
        boutonCreerProjet.setCompoundDrawablesWithIntrinsicBounds(iconeCreer, null, null, null);
        boutonCreerProjet.setCompoundDrawablePadding(15);
        boutonCreerProjet.setOnClickListener(v -> { afficherDialogueCreationProjet(); });
        
        LinearLayout.LayoutParams margeBouton2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margeBouton2.gravity = Gravity.CENTER_HORIZONTAL;
        margeBouton2.setMargins(30, 15, 30, 15);
        colonneDroite.addView(boutonCreerProjet, margeBouton2);

        // BOUTON OUVRIR PROJET
        Button boutonOuvrirProjet = new Button(this);
        boutonOuvrirProjet.setText("Ouvrir un projet téléchargé");
        boutonOuvrirProjet.setBackgroundColor(Palette.boutonNormal);
        boutonOuvrirProjet.setTextColor(Palette.texteNormal);
        
        android.graphics.drawable.Drawable iconeOuvrir = getResources().getDrawable(R.drawable.folder_open_24px).mutate();
        iconeOuvrir.setColorFilter(Palette.texteNormal, android.graphics.PorterDuff.Mode.SRC_IN);
        boutonOuvrirProjet.setCompoundDrawablesWithIntrinsicBounds(iconeOuvrir, null, null, null);
        boutonOuvrirProjet.setCompoundDrawablePadding(15);
        boutonOuvrirProjet.setOnClickListener(v -> {});
        
        LinearLayout.LayoutParams margeBouton3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margeBouton3.gravity = Gravity.CENTER_HORIZONTAL;
        margeBouton3.setMargins(30, 15, 30, 15);
        colonneDroite.addView(boutonOuvrirProjet, margeBouton3);

        // BOUTON DEBUG VÉRIFIER
        Button boutonDebug = new Button(this);
        boutonDebug.setText("DEBUG Vérifier dossier projets");
        boutonDebug.setBackgroundColor(Palette.boutonNormal);
        boutonDebug.setTextColor(Palette.texteNormal);
        
        android.graphics.drawable.Drawable iconeDebug1 = getResources().getDrawable(R.drawable.bug_report_24px).mutate();
        iconeDebug1.setColorFilter(Palette.texteNormal, android.graphics.PorterDuff.Mode.SRC_IN);
        boutonDebug.setCompoundDrawablesWithIntrinsicBounds(iconeDebug1, null, null, null);
        boutonDebug.setCompoundDrawablePadding(15);
        boutonDebug.setOnClickListener(v -> { 
            File dossierProjets = new File(getFilesDir(), "projets");
            StringBuilder info = new StringBuilder();
            info.append("Chemin absolu : ").append(dossierProjets.getAbsolutePath()).append("\n");
            info.append("Existe : ").append(dossierProjets.exists()).append("\n");
            info.append("Est un dossier : ").append(dossierProjets.isDirectory()).append("\n\n");
            File[] sousDossiers = dossierProjets.listFiles();
            if (sousDossiers == null) {
                info.append("listFiles() a retourné null\n");
            } else if (sousDossiers.length == 0) {
                info.append("Aucun sous-dossier trouvé\n");
            } else {
                for (File sousDossier : sousDossiers) {
                    info.append("Dossier : ").append(sousDossier.getName()).append("\n");
                    File metaFile = new File(sousDossier, "meta.json");
                    boolean metaExiste = metaFile.exists();
                    info.append("  -> meta.json existe : ").append(metaExiste);
                    if (metaExiste) {
                        info.append(" (Taille : ").append(metaFile.length()).append(" octets)");
                    }
                    info.append("\n");
                }
            }
            new AlertDialog.Builder(EcranDemarrage.this)
                    .setTitle("Debug : Dossier projets")
                    .setMessage(info.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });
        
        LinearLayout.LayoutParams margeBouton4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margeBouton4.gravity = Gravity.CENTER_HORIZONTAL;
        margeBouton4.setMargins(30, 15, 30, 15);
        colonneDroite.addView(boutonDebug, margeBouton4);

        // BOUTON DEBUG MKDIRS
        Button boutonDebugMkdirs = new Button(this);
        boutonDebugMkdirs.setText("DEBUG Test mkdirs");
        boutonDebugMkdirs.setBackgroundColor(Palette.boutonNormal);
        boutonDebugMkdirs.setTextColor(Palette.texteNormal);
        
        android.graphics.drawable.Drawable iconeDebug2 = getResources().getDrawable(R.drawable.build_24px).mutate();
        iconeDebug2.setColorFilter(Palette.texteNormal, android.graphics.PorterDuff.Mode.SRC_IN);
        boutonDebugMkdirs.setCompoundDrawablesWithIntrinsicBounds(iconeDebug2, null, null, null);
        boutonDebugMkdirs.setCompoundDrawablePadding(15);
        boutonDebugMkdirs.setOnClickListener(v -> { 
            File dossierProjets = new File(getFilesDir(), "projets");
            boolean resultat = dossierProjets.mkdirs();
            StringBuilder info = new StringBuilder();
            info.append("Chemin absolu : ").append(dossierProjets.getAbsolutePath()).append("\n");
            info.append("Valeur de resultat (mkdirs) : ").append(resultat).append("\n");
            info.append("exists() : ").append(dossierProjets.exists()).append("\n");
            info.append("canWrite() : ").append(dossierProjets.canWrite()).append("\n");
            info.append("getFilesDir().canWrite() (parent) : ").append(getFilesDir().canWrite()).append("\n");
            new AlertDialog.Builder(EcranDemarrage.this)
                    .setTitle("Debug : Test mkdirs")
                    .setMessage(info.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });
        
        LinearLayout.LayoutParams margeBouton5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margeBouton5.gravity = Gravity.CENTER_HORIZONTAL;
        margeBouton5.setMargins(30, 15, 30, 15);
        colonneDroite.addView(boutonDebugMkdirs, margeBouton5);

        // LISTE DES PROJETS (Avec la nouvelle couleur de fond)
        listeProjets = new ListView(this);
        listeProjets.setBackgroundColor(Palette.fondListe);

        LinearLayout.LayoutParams paramsListe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        paramsListe.setMargins(20, 0, 20, 20); 
        listeProjets.setLayoutParams(paramsListe);
        colonneDroite.addView(listeProjets);

        // Ajout des colonnes au layout principal
        layoutPrincipal.addView(colonneGauche);
        layoutPrincipal.addView(colonneDroite);

        // Définition de la vue
        setContentView(layoutPrincipal);
    }

    private void afficherDialogueCreationProjet() {
        // La logique existante pour ta boite de dialogue reste ici
    }
}
// bas 1

        
                    
                                        
