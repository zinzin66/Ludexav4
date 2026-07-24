// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class InterfaceBlueprint extends Activity {

    public static Scene sceneACharger; 

    private Blueprint blueprintActif;
    private CanvasBlueprint canvasBlueprint;

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
        boutonRetour.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonRetour);

        canvasBlueprint = new CanvasBlueprint(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasBlueprint.setLayoutParams(paramsCentre);

        Button boutonSauvegarder = new Button(this);
        boutonSauvegarder.setText("Sauvegarder");
        boutonSauvegarder.setOnClickListener(v -> sauvegarderBlueprintLocal());
        bandeauHaut.addView(boutonSauvegarder);

        Button boutonCharger = new Button(this);
        boutonCharger.setText("Charger");
        boutonCharger.setOnClickListener(v -> chargerBlueprintLocal(false));
        bandeauHaut.addView(boutonCharger);

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

        Button boutonUndo = new Button(this);
        boutonUndo.setText("[<]");
        bandeauHaut.addView(boutonUndo);

        Button boutonRedo = new Button(this);
        boutonRedo.setText("[>]");
        bandeauHaut.addView(boutonRedo);

        Button boutonSupprimerNode = new Button(this);
        boutonSupprimerNode.setText("Supprimer le node");
        boutonSupprimerNode.setOnClickListener(v -> canvasBlueprint.supprimerNoeudSelectionne());
        bandeauHaut.addView(boutonSupprimerNode);

        Button boutonCode = new Button(this);
        boutonCode.setText("Code");
        boutonCode.setOnClickListener(v -> afficherFenetreCode());
        bandeauHaut.addView(boutonCode);

        // ---- Zone Milieu ----
        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        zoneMilieu.setLayoutParams(paramsMilieu);

        PanneauNoeuds panneauNoeuds = new PanneauNoeuds(this);

        // --- AJOUT DE LA SCENE AU CANVAS ---
        if (sceneACharger != null) {
            canvasBlueprint.sceneActive = sceneACharger;
        } else {
            canvasBlueprint.sceneActive = new Scene("Scène Vide (Fallback)");
        }

        // --- CHARGEMENT AUTOMATIQUE SILENCIEUX ---
        chargerBlueprintLocal(true);

        zoneMilieu.addView(panneauNoeuds);
        zoneMilieu.addView(canvasBlueprint);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }

    private void sauvegarderBlueprintLocal() {
        try {
            File dir = new File(getFilesDir(), "logique");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "blueprint.json");
            
            String json = blueprintActif.toJson();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();
            Toast.makeText(this, "Blueprint sauvegardé (logique/blueprint.json)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private void chargerBlueprintLocal(boolean estChargementAuto) {
        try {
            File dir = new File(getFilesDir(), "logique");
            File file = new File(dir, "blueprint.json");
            if (!file.exists()) {
                if (!estChargementAuto) {
                    Toast.makeText(this, "Aucune sauvegarde trouvée", Toast.LENGTH_SHORT).show();
                }
                // Initialisation d'un blueprint VRAIMENT VIDE
                if (blueprintActif == null) {
                    blueprintActif = new Blueprint();
                    canvasBlueprint.setBlueprint(blueprintActif);
                }
                return;
            }
            
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            
            // CORRECTION : Transmission de la scène active pour permettre la reconnexion des cibles
            blueprintActif = Blueprint.fromJson(sb.toString(), canvasBlueprint.sceneActive);
            
            canvasBlueprint.setBlueprint(blueprintActif);
            canvasBlueprint.invalidate();
            
            if (!estChargementAuto) {
                Toast.makeText(this, "Blueprint chargé avec succès !", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!estChargementAuto) {
                Toast.makeText(this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
            }
            if (blueprintActif == null) {
                blueprintActif = new Blueprint();
                canvasBlueprint.setBlueprint(blueprintActif);
            }
        }
    }

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
// bas 1
