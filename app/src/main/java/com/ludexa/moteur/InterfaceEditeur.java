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

        // ... (Boutons précédents conservés)
        Button boutonQuitter = new Button(this);
        boutonQuitter.setText("Quitter");
        boutonQuitter.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonQuitter);

        // ... (Autres boutons de navigation/outils)
        Button boutonDeplacerScene = new Button(this);
        boutonDeplacerScene.setText("Déplacer Scène");
        
        // --- Intégration du Canvas ---
        CanvasEditeur canvasEditeur = new CanvasEditeur(this);
        
        // Logique de bascule
        boutonDeplacerScene.setOnClickListener(v -> {
            boolean nouveauMode = !canvasEditeur.isPanMode();
            canvasEditeur.setPanMode(nouveauMode);
            boutonDeplacerScene.setText(nouveauMode ? "Mode: Déplacement" : "Déplacer Scène");
        });
        bandeauHaut.addView(boutonDeplacerScene);

        // ... (Suite des boutons : Blueprint, Build, etc.)

        // ---- Zone Milieu ----
        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        zoneMilieu.setLayoutParams(paramsMilieu);

        PanneauRessources panneauRessources = new PanneauRessources(this);
        InspecteurProprietes menuInspecteur = new InspecteurProprietes(this);

        // Intégration du Canvas au lieu du TextView[span_0](start_span)[span_0](end_span)
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasEditeur.setLayoutParams(paramsCentre);

        zoneMilieu.addView(panneauRessources);
        zoneMilieu.addView(canvasEditeur);
        zoneMilieu.addView(menuInspecteur);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }
}
