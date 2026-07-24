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

    // Variable statique pour recevoir la scène depuis l'éditeur sans utiliser la sérialisation complexe
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
        boutonRetour.setOnClickListener(v -> finish()); // Revient à l'InterfaceEditeur
        bandeauHaut.addView(boutonRetour);

        // Instanciation du Canvas 
        canvasBlueprint = new CanvasBlueprint(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasBlueprint.setLayoutParams(paramsCentre);

        // --- Boutons Sauvegarde & Chargement (Tâche 9.2) ---
        Button boutonSauvegarder = new Button(this);
        boutonSauvegarder.setText("Sauvegarder");
        boutonSauvegarder.setOnClickListener(v -> sauvegarderBlueprintLocal());
        bandeauHaut.addView(boutonSauvegarder);

        Button boutonCharger = new Button(this);
        boutonCharger.setText("Charger");
        boutonCharger.setOnClickListener(v -> chargerBlueprintLocal());
        bandeauHaut.addView(boutonCharger);

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

        blueprintActif = new Blueprint();
        blueprintActif.ajouterNoeud(new NoeudEventStart(), 150f, 200f);
        blueprintActif.ajouterNoeud(new NoeudEventStart(), 500f, 350f);
        canvasBlueprint.setBlueprint(blueprintActif);
        
        // --- CORRECTION : AJOUT DE LA SCENE AU CANVAS ---
        // On récupère la vraie scène transmise par l'éditeur
        if (sceneACharger != null) {
            canvasBlueprint.sceneActive = sceneACharger;
        } else {
            // Sécurité : si on lance l'activité directement sans passer par l'éditeur
            canvasBlueprint.sceneActive = new Scene("Scène Vide (Fallback)");
        }
        // -------------------------------------

        // 2. Assemblage de la zone milieu
        zoneMilieu.addView(panneauNoeuds);
        zoneMilieu.addView(canvasBlueprint);

        // Assemblage final
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

    private void chargerBlueprintLocal() {
        try {
            File dir = new File(getFilesDir(), "logique");
            File file = new File(dir, "blueprint.json");
            if (!file.exists()) {
                Toast.makeText(this, "Aucune sauvegarde trouvée", Toast.LENGTH_SHORT).show();
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
            
            blueprintActif = Blueprint.fromJson(sb.toString());
            canvasBlueprint.setBlueprint(blueprintActif);
            canvasBlueprint.invalidate();
            Toast.makeText(this, "Blueprint chargé avec succès !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
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
