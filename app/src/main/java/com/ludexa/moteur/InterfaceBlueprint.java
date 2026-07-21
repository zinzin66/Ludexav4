package com.ludexa.moteur;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.*;

public class InterfaceBlueprint extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // ---- Bandeau du haut ----
        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setPadding(10, 10, 10, 10);

        Button boutonRetour = new Button(this);
        boutonRetour.setText("Retour Scène");
        boutonRetour.setOnClickListener(v -> finish()); // Revient à l'InterfaceEditeur
        bandeauHaut.addView(boutonRetour);

        // Instanciation du Canvas (nécessaire ici pour le lier aux boutons de zoom)
        CanvasBlueprint canvasBlueprint = new CanvasBlueprint(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasBlueprint.setLayoutParams(paramsCentre);

        // --- Boutons de Zoom ---
        Button boutonZoomMoins = new Button(this);
        boutonZoomMoins.setText("[-]");
        boutonZoomMoins.setOnClickListener(v -> canvasBlueprint.zoomMoins());
        bandeauHaut.addView(boutonZoomMoins);

        Button boutonZoomReset = new Button(this);
        boutonZoomReset.setText("[[]]");
        boutonZoomReset.setOnClickListener(v -> canvasBlueprint.zoomReset());
        bandeauHaut.addView(boutonZoomReset);

        Button boutonZoomPlus = new Button(this);
        boutonZoomPlus.setText("[+]");
        boutonZoomPlus.setOnClickListener(v -> canvasBlueprint.zoomPlus());
        bandeauHaut.addView(boutonZoomPlus);

        // --- Boutons Undo / Redo ---
        Button boutonUndo = new Button(this);
        boutonUndo.setText("[<]");
        bandeauHaut.addView(boutonUndo);

        Button boutonRedo = new Button(this);
        boutonRedo.setText("[>]");
        bandeauHaut.addView(boutonRedo);

        // --- Boutons Nœuds & Code ---
        Button boutonSupprimerNode = new Button(this);
        boutonSupprimerNode.setText("Supprimer le node");
        bandeauHaut.addView(boutonSupprimerNode);

        Button boutonCode = new Button(this);
        boutonCode.setText("Code");
        boutonCode.setOnClickListener(v -> afficherFenetreCode());
        bandeauHaut.addView(boutonCode);

        // ---- Zone Milieu (Panneau Gauche + Canvas) ----
        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        zoneMilieu.setLayoutParams(paramsMilieu);

        // 1. Panneau de gauche (Nœuds)
        PanneauNoeuds panneauNoeuds = new PanneauNoeuds(this);

        // 2. Zone centrale (Canvas intégré)
        // (canvasBlueprint est déjà instancié plus haut)

        // Assemblage de la zone milieu
        zoneMilieu.addView(panneauNoeuds);
        zoneMilieu.addView(canvasBlueprint);

        // Assemblage final
        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }

    // Fonction pour afficher la fenêtre modale contenant le code généré
    private void afficherFenetreCode() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Code Généré");

        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(30, 30, 30, 30);

        TextView textViewCode = new TextView(this);
        textViewCode.setText("// Exemple de code Java généré par les nœuds\npublic void executerLogique() {\n    System.out.println(\"Hello Blueprint\");\n}");
        textViewCode.setTextSize(16f);
        textViewCode.setPadding(0, 0, 0, 30);
        layoutDialog.addView(textViewCode);

        LinearLayout boutonsDialog = new LinearLayout(this);
        boutonsDialog.setOrientation(LinearLayout.HORIZONTAL);

        Button btnCopier = new Button(this);
        btnCopier.setText("Copier");
        btnCopier.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Code LUDEXA", textViewCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Code copié dans le presse-papier !", Toast.LENGTH_SHORT).show();
        });
        boutonsDialog.addView(btnCopier);

        Button btnQuitter = new Button(this);
        btnQuitter.setText("Quitter");
        btnQuitter.setOnClickListener(v -> dialog.dismiss());
        boutonsDialog.addView(btnQuitter);

        layoutDialog.addView(boutonsDialog);
        
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
