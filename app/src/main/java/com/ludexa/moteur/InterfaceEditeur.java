package com.ludexa.moteur;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
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
        boutonQuitter.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonQuitter);

        TextView nomProjet = new TextView(this);
        nomProjet.setText("Projet sans nom");
        nomProjet.setTextSize(18f);
        nomProjet.setPadding(20, 0, 20, 0);
        bandeauHaut.addView(nomProjet);

        Button boutonSauvegarde = new Button(this);
        boutonSauvegarde.setText("Sauvegarde");
        bandeauHaut.addView(boutonSauvegarde);

        Button boutonUndo = new Button(this);
        boutonUndo.setText("Undo");
        bandeauHaut.addView(boutonUndo);

        Button boutonRedo = new Button(this);
        boutonRedo.setText("Redo");
        bandeauHaut.addView(boutonRedo);

        Button boutonZoomMoins = new Button(this);
        boutonZoomMoins.setText("-");
        bandeauHaut.addView(boutonZoomMoins);

        Button boutonZoomPlus = new Button(this);
        boutonZoomPlus.setText("+");
        bandeauHaut.addView(boutonZoomPlus);

        // --- Instanciation du Canvas (nécessaire ici pour le relier au bouton) ---
        CanvasEditeur canvasEditeur = new CanvasEditeur(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasEditeur.setLayoutParams(paramsCentre);

        Button boutonDeplacerScene = new Button(this);
        boutonDeplacerScene.setText("Déplacer Scène");
        boutonDeplacerScene.setOnClickListener(v -> {
            boolean nouveauMode = !canvasEditeur.isPanMode();
            canvasEditeur.setPanMode(nouveauMode);
            boutonDeplacerScene.setText(nouveauMode ? "Mode: Déplacement" : "Déplacer Scène");
        });
        bandeauHaut.addView(boutonDeplacerScene);

        Button boutonBasculeBlueprint = new Button(this);
        boutonBasculeBlueprint.setText("Blueprint");
        bandeauHaut.addView(boutonBasculeBlueprint);

        Button boutonBuild = new Button(this);
        boutonBuild.setText("Build");
        bandeauHaut.addView(boutonBuild);

        // ---- Zone Milieu (Menu Gauche, Canvas, Inspecteur) ----
        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        zoneMilieu.setLayoutParams(paramsMilieu);

        // 1. Menu de Gauche (Accordéon / Ressources)
        PanneauRessources panneauRessources = new PanneauRessources(this);

        // 2. Zone centrale (Canvas remplace le texte provisoire)
        // (canvasEditeur est déjà instancié et configuré plus haut)

        // 3. Menu Inspecteur (Droite)
        InspecteurProprietes menuInspecteur = new InspecteurProprietes(this);

        // Assemblage final de la zone milieu (Respect de l'ordre visuel)
        zoneMilieu.addView(panneauRessources);
        zoneMilieu.addView(canvasEditeur);
        zoneMilieu.addView(menuInspecteur);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }
}
