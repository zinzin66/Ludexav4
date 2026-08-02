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
import java.util.List; 

public class InterfaceBlueprint extends Activity {

    public String cheminProjet; // NOUVEAU : Stockage du chemin

    public static Scene sceneACharger; 
    public static List<Variable> variablesGlobalesACharger; 
    public static List<Scene> listeScenesACharger;

    public List<Variable> variablesGlobales; 
    public List<Scene> listeScenes; 

    private Blueprint blueprintActif;
    private CanvasBlueprint canvasBlueprint;

    @Override
    protected void onResume() {
        super.onResume();
        NoeudBase.contexteApplication = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoeudBase.contexteApplication = this;

        // NOUVEAU : Récupération du chemin du projet
        cheminProjet = getIntent().getStringExtra("cheminProjet");
        
        this.variablesGlobales = variablesGlobalesACharger; 
        this.listeScenes = listeScenesACharger; 

        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        layoutPrincipal.setBackgroundColor(Palette.fondPanneaux);

        // ---- Bandeau du haut ----
        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setPadding(10, 10, 10, 10);
        bandeauHaut.setBackgroundColor(Palette.fondPanneaux);

        Button boutonRetour = new Button(this);
        boutonRetour.setText("Retour Scène");
        boutonRetour.setBackgroundColor(Palette.boutonNormal);
        boutonRetour.setTextColor(Palette.texteNormal);
        boutonRetour.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonRetour);

        canvasBlueprint = new CanvasBlueprint(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasBlueprint.setLayoutParams(paramsCentre);

        Button boutonSauvegarder = new Button(this);
        boutonSauvegarder.setText("Sauvegarder");
        boutonSauvegarder.setBackgroundColor(Palette.boutonNormal);
        boutonSauvegarder.setTextColor(Palette.texteNormal);
        boutonSauvegarder.setOnClickListener(v -> sauvegarderBlueprintLocal());
        bandeauHaut.addView(boutonSauvegarder);

        Button boutonCharger = new Button(this);
        boutonCharger.setText("Charger");
        boutonCharger.setBackgroundColor(Palette.boutonNormal);
        boutonCharger.setTextColor(Palette.texteNormal);
        boutonCharger.setOnClickListener(v -> chargerBlueprintLocal(false));
        bandeauHaut.addView(boutonCharger);

        Button boutonZoomMoins = new Button(this);
        boutonZoomMoins.setText("[-]");
        boutonZoomMoins.setBackgroundColor(Palette.boutonNormal);
        boutonZoomMoins.setTextColor(Palette.texteNormal);
        boutonZoomMoins.setOnClickListener(v -> canvasBlueprint.zoomMoins());
        bandeauHaut.addView(boutonZoomMoins);

        Button boutonZoomReset = new Button(this);
        boutonZoomReset.setText("[[]]");
        boutonZoomReset.setBackgroundColor(Palette.boutonNormal);
        boutonZoomReset.setTextColor(Palette.texteNormal);
        boutonZoomReset.setOnClickListener(v -> canvasBlueprint.zoomReset());
        bandeauHaut.addView(boutonZoomReset);

        Button boutonZoomPlus = new Button(this);
        boutonZoomPlus.setText("[+]");
        boutonZoomPlus.setBackgroundColor(Palette.boutonNormal);
        boutonZoomPlus.setTextColor(Palette.texteNormal);
        boutonZoomPlus.setOnClickListener(v -> canvasBlueprint.zoomPlus());
        bandeauHaut.addView(boutonZoomPlus);

        Button boutonUndo = new Button(this);
        boutonUndo.setText("[<]");
        boutonUndo.setBackgroundColor(Palette.boutonNormal);
        boutonUndo.setTextColor(Palette.texteNormal);
        bandeauHaut.addView(boutonUndo);

        Button boutonRedo = new Button(this);
        boutonRedo.setText("[>]");
        boutonRedo.setBackgroundColor(Palette.boutonNormal);
        boutonRedo.setTextColor(Palette.texteNormal);
        bandeauHaut.addView(boutonRedo);

        Button boutonSupprimerNode = new Button(this);
        boutonSupprimerNode.setText("Supprimer le node");
        boutonSupprimerNode.setBackgroundColor(Palette.boutonNormal);
        boutonSupprimerNode.setTextColor(Palette.texteNormal);
        boutonSupprimerNode.setOnClickListener(v -> canvasBlueprint.supprimerNoeudSelectionne());
        bandeauHaut.addView(boutonSupprimerNode);

        Button boutonCode = new Button(this);
        boutonCode.setText("Code");
        boutonCode.setBackgroundColor(Palette.boutonNormal);
        boutonCode.setTextColor(Palette.texteNormal);
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
            // NOUVEAU : Utilisation du chemin du projet
            File dir = new File(cheminProjet, "logique");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, canvasBlueprint.sceneActive.id + ".json");
            
            String json = blueprintActif.toJson();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();
            Toast.makeText(this, "Blueprint sauvegardé", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private void chargerBlueprintLocal(boolean estChargementAuto) {
        try {
            // NOUVEAU : Utilisation du chemin du projet
            File dir = new File(cheminProjet, "logique");
            File file = new File(dir, canvasBlueprint.sceneActive.id + ".json");
            
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
        dialog.setTitle("Résumé du Blueprint");

        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(30, 30, 30, 30);
        layoutDialog.setBackgroundColor(Palette.fondPanneaux);

        // --- Génération dynamique du texte du Blueprint ---
        StringBuilder res = new StringBuilder();
        res.append("=== RESUME DU BLUEPRINT ===\n\n");
        
        if (blueprintActif == null || blueprintActif.noeuds.isEmpty()) {
            res.append("Le Blueprint est vide.\n");
        } else {
            res.append("--- NOEUDS ---\n");
            for (NoeudBase noeud : blueprintActif.noeuds) {
                String shortId = (noeud.id != null && noeud.id.length() >= 8) ? noeud.id.substring(0, 8) : noeud.id;
                res.append("- [").append(noeud.nom).append("] (ID: ").append(shortId).append(")\n");
                
                if (noeud.requiertCibleObjet()) {
                    String cible = (noeud.getCibleObjet() != null) ? noeud.getCibleObjet().nom : "Aucune";
                    res.append("  Cible Objet : ").append(cible).append("\n");
                }
                
                if (noeud.requiertCibleVariable()) {
                    String cible = (noeud.getCibleVariable() != null) ? noeud.getCibleVariable().nom : "Aucune";
                    res.append("  Cible Variable : ").append(cible).append("\n");
                }
                
                if (noeud.getNomsParametres() != null && !noeud.getNomsParametres().isEmpty()) {
                    for (String param : noeud.getNomsParametres()) {
                        res.append("  Paramètre [").append(param).append("] : ").append(noeud.getValeurParametre(param)).append("\n");
                    }
                }
            }
            
            res.append("\n--- CONNEXIONS ---\n");
            if (blueprintActif.liens != null && !blueprintActif.liens.isEmpty()) {
                for (Blueprint.Lien lien : blueprintActif.liens) {
                    String nomDep = (lien.noeudDepart != null) ? lien.noeudDepart.nom : "Inconnu";
                    String nomArr = (lien.noeudArrivee != null) ? lien.noeudArrivee.nom : "Inconnu";
                    res.append(nomDep).append(" [").append(lien.portSortieNom).append("] -> ")
                       .append(nomArr).append(" [").append(lien.portEntreeNom).append("]\n");
                }
            } else {
                res.append("Aucune connexion.\n");
            }
        }

        TextView textViewCode = new TextView(this);
        textViewCode.setText(res.toString());
        textViewCode.setTextSize(14f); 
        textViewCode.setTextColor(Palette.texteNormal);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setPadding(0, 0, 0, 30);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollView.setLayoutParams(scrollParams);
        scrollView.addView(textViewCode);

        layoutDialog.addView(scrollView);

        LinearLayout boutonsDialog = new LinearLayout(this);
        boutonsDialog.setOrientation(LinearLayout.HORIZONTAL);

        Button btnCopier = new Button(this);
        btnCopier.setText("Copier");
        btnCopier.setBackgroundColor(Palette.boutonNormal);
        btnCopier.setTextColor(Palette.texteNormal);
        btnCopier.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Code LUDEXA", textViewCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Résumé copié dans le presse-papier !", Toast.LENGTH_SHORT).show();
        });
        boutonsDialog.addView(btnCopier);

        Button btnQuitter = new Button(this);
        btnQuitter.setText("Quitter");
        btnQuitter.setBackgroundColor(Palette.boutonNormal);
        btnQuitter.setTextColor(Palette.texteNormal);
        btnQuitter.setOnClickListener(v -> dialog.dismiss());
        boutonsDialog.addView(btnQuitter);

        layoutDialog.addView(boutonsDialog);
        
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
// bas 1
