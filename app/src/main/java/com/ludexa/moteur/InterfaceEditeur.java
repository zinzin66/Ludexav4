package com.ludexa.moteur;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class InterfaceEditeur extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // ---- Bandeau du haut ----
        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setPadding(10, 10, 10, 10);

        Button boutonQuitter = new Button(this);
        boutonQuitter.setText("Quitter");
        boutonQuitter.setOnClickListener(v -> {
            finish(); // Retour à l'écran précédent (Démarrage)
        });
        bandeauHaut.addView(boutonQuitter);

        TextView nomProjet = new TextView(this);
        nomProjet.setText("Projet sans nom");
        nomProjet.setTextSize(18f);
        nomProjet.setPadding(20, 0, 20, 0);
        bandeauHaut.addView(nomProjet);

        Button boutonSauvegarde = new Button(this);
        boutonSauvegarde.setText("Sauvegarde");
        boutonSauvegarde.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonSauvegarde);

        Button boutonUndo = new Button(this);
        boutonUndo.setText("Undo");
        boutonUndo.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonUndo);

        Button boutonRedo = new Button(this);
        boutonRedo.setText("Redo");
        boutonRedo.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonRedo);

        Button boutonZoomMoins = new Button(this);
        boutonZoomMoins.setText("-");
        boutonZoomMoins.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonZoomMoins);

        Button boutonZoomPlus = new Button(this);
        boutonZoomPlus.setText("+");
        boutonZoomPlus.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonZoomPlus);

        Button boutonDeplacerScene = new Button(this);
        boutonDeplacerScene.setText("Déplacer Scène");
        boutonDeplacerScene.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonDeplacerScene);

        Button boutonBasculeBlueprint = new Button(this);
        boutonBasculeBlueprint.setText("Blueprint");
        boutonBasculeBlueprint.setOnClickListener(v -> {
            // À implémenter : ouvrir InterfaceBlueprint
        });
        bandeauHaut.addView(boutonBasculeBlueprint);

        Button boutonBuild = new Button(this);
        boutonBuild.setText("Build");
        boutonBuild.setOnClickListener(v -> {
            // À implémenter
        });
        bandeauHaut.addView(boutonBuild);

        // ---- Zone centrale : réservée pour le Canvas, vide pour l'instant ----
        TextView zoneCentraleProvisoire = new TextView(this);
        zoneCentraleProvisoire.setText("[ Canvas — à venir ]");
        zoneCentraleProvisoire.setTextSize(20f);
        zoneCentraleProvisoire.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        zoneCentraleProvisoire.setLayoutParams(paramsCentre);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneCentraleProvisoire);

        setContentView(layoutPrincipal);
    }
}
