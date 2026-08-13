// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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

    private int dp(float valeur) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valeur, getResources().getDisplayMetrics()));
    }

    private GradientDrawable fond(int couleur, int rayonDp, int couleurBordure, int epaisseurDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(couleur);
        g.setCornerRadius(dp(rayonDp));
        if (epaisseurDp > 0) {
            g.setStroke(dp(epaisseurDp), couleurBordure);
        }
        return g;
    }

    private void styliserBoutonBandeau(ImageButton b) {
        b.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        b.setBackground(fond(Palette.boutonNormal, 6, Palette.bordure, 1));
        b.setPadding(dp(6), dp(6), dp(6), dp(6));
        Palette.appliquerCouleurIcone(b, Palette.iconeNormal);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(38), dp(38));
        lp.setMargins(0, 0, dp(6), 0);
        lp.gravity = Gravity.CENTER_VERTICAL;
        b.setLayoutParams(lp);
    }

    private View separateurVertical() {
        View s = new View(this);
        s.setBackgroundColor(Palette.bordure);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1), dp(26));
        lp.setMargins(dp(4), 0, dp(10), 0);
        lp.gravity = Gravity.CENTER_VERTICAL;
        s.setLayoutParams(lp);
        return s;
    }

    private void styliserBoutonDialog(Button b) {
        b.setBackground(fond(Palette.boutonNormal, 6, Palette.bordure, 1));
        b.setTextColor(Palette.texteNormal);
        b.setAllCaps(false);
        b.setTextSize(14f);
        b.setPadding(dp(14), dp(6), dp(14), dp(6));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(8), 0);
        b.setLayoutParams(lp);
    }

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
        layoutPrincipal.setBackgroundColor(Palette.fondNormal);
        layoutPrincipal.setPadding(dp(8), dp(8), dp(8), dp(8));

        // ---- Bandeau du haut ----
        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setGravity(Gravity.CENTER_VERTICAL);
        bandeauHaut.setPadding(dp(6), dp(6), dp(6), dp(6));
        bandeauHaut.setBackground(fond(Palette.fondPanneaux, 8, Palette.bordure, 1));

        ImageButton boutonRetour = new ImageButton(this);
        boutonRetour.setImageResource(R.drawable.exit_to_app_24px);
        styliserBoutonBandeau(boutonRetour);
        boutonRetour.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonRetour);

        TextView titreBlueprint = new TextView(this);
        titreBlueprint.setText("Blueprint");
        titreBlueprint.setTextSize(15f);
        titreBlueprint.setTextColor(Palette.texteSelectionne);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTitre.setMargins(dp(4), 0, dp(10), 0);
        paramsTitre.gravity = Gravity.CENTER_VERTICAL;
        titreBlueprint.setLayoutParams(paramsTitre);
        bandeauHaut.addView(titreBlueprint);

        bandeauHaut.addView(separateurVertical());

        canvasBlueprint = new CanvasBlueprint(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        paramsCentre.setMargins(dp(8), 0, 0, 0);
        canvasBlueprint.setLayoutParams(paramsCentre);

        ImageButton boutonSauvegarder = new ImageButton(this);
        boutonSauvegarder.setImageResource(R.drawable.save_24px);
        styliserBoutonBandeau(boutonSauvegarder);
        boutonSauvegarder.setOnClickListener(v -> sauvegarderBlueprintLocal());
        bandeauHaut.addView(boutonSauvegarder);

        ImageButton boutonCharger = new ImageButton(this);
        boutonCharger.setImageResource(R.drawable.folder_open_24px);
        styliserBoutonBandeau(boutonCharger);
        boutonCharger.setOnClickListener(v -> chargerBlueprintLocal(false));
        bandeauHaut.addView(boutonCharger);

        bandeauHaut.addView(separateurVertical());

        ImageButton boutonZoomMoins = new ImageButton(this);
        boutonZoomMoins.setImageResource(R.drawable.zoom_out_24px);
        styliserBoutonBandeau(boutonZoomMoins);
        boutonZoomMoins.setOnClickListener(v -> canvasBlueprint.zoomMoins());
        bandeauHaut.addView(boutonZoomMoins);

        ImageButton boutonZoomReset = new ImageButton(this);
        boutonZoomReset.setImageResource(R.drawable.center_focus_weak_24px);
        styliserBoutonBandeau(boutonZoomReset);
        boutonZoomReset.setOnClickListener(v -> canvasBlueprint.zoomReset());
        bandeauHaut.addView(boutonZoomReset);

        ImageButton boutonZoomPlus = new ImageButton(this);
        boutonZoomPlus.setImageResource(R.drawable.zoom_in_24px);
        styliserBoutonBandeau(boutonZoomPlus);
        boutonZoomPlus.setOnClickListener(v -> canvasBlueprint.zoomPlus());
        bandeauHaut.addView(boutonZoomPlus);

        bandeauHaut.addView(separateurVertical());

        ImageButton boutonUndo = new ImageButton(this);
        boutonUndo.setImageResource(R.drawable.undo_24px);
        styliserBoutonBandeau(boutonUndo);
        bandeauHaut.addView(boutonUndo);

        ImageButton boutonRedo = new ImageButton(this);
        boutonRedo.setImageResource(R.drawable.redo_24px);
        styliserBoutonBandeau(boutonRedo);
        bandeauHaut.addView(boutonRedo);

        ImageButton boutonSupprimerNode = new ImageButton(this);
        boutonSupprimerNode.setImageResource(R.drawable.delete_24px);
        styliserBoutonBandeau(boutonSupprimerNode);
        boutonSupprimerNode.setOnClickListener(v -> canvasBlueprint.supprimerNoeudSelectionne());
        bandeauHaut.addView(boutonSupprimerNode);

        View espaceBandeau = new View(this);
        espaceBandeau.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1f));
        bandeauHaut.addView(espaceBandeau);

        ImageButton boutonCode = new ImageButton(this);
        boutonCode.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonBandeau(boutonCode);
        boutonCode.setBackground(fond(Palette.boutonSurvol, 6, Palette.bordure, 1));
        boutonCode.setOnClickListener(v -> afficherFenetreCode());
        bandeauHaut.addView(boutonCode);

        // ---- Zone Milieu ----
        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        paramsMilieu.setMargins(0, dp(8), 0, 0);
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
        layoutDialog.setPadding(dp(16), dp(16), dp(16), dp(16));
        layoutDialog.setBackground(fond(Palette.fondPanneaux, 8, Palette.bordure, 1));

        TextView enTeteDialog = new TextView(this);
        enTeteDialog.setText("RÉSUMÉ DU BLUEPRINT");
        enTeteDialog.setTextSize(14f);
        enTeteDialog.setTextColor(Palette.texteSelectionne);
        enTeteDialog.setBackground(fond(Palette.enTeteDialogues, 6, Palette.bordure, 1));
        enTeteDialog.setPadding(dp(10), dp(8), dp(10), dp(8));
        LinearLayout.LayoutParams paramsEnTete = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsEnTete.setMargins(0, 0, 0, dp(10));
        enTeteDialog.setLayoutParams(paramsEnTete);
        layoutDialog.addView(enTeteDialog);

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
        textViewCode.setPadding(dp(10), dp(10), dp(10), dp(10));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackground(fond(Palette.fondListe, 6, Palette.bordure, 1));
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollParams.setMargins(0, 0, 0, dp(12));
        scrollView.setLayoutParams(scrollParams);
        scrollView.addView(textViewCode);

        layoutDialog.addView(scrollView);

        LinearLayout boutonsDialog = new LinearLayout(this);
        boutonsDialog.setOrientation(LinearLayout.HORIZONTAL);
        boutonsDialog.setGravity(Gravity.END);

        Button btnCopier = new Button(this);
        btnCopier.setText("Copier");
        styliserBoutonDialog(btnCopier);
        btnCopier.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Code LUDEXA", textViewCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Résumé copié dans le presse-papier !", Toast.LENGTH_SHORT).show();
        });
        boutonsDialog.addView(btnCopier);

        Button btnQuitter = new Button(this);
        btnQuitter.setText("Quitter");
        styliserBoutonDialog(btnQuitter);
        btnQuitter.setOnClickListener(v -> dialog.dismiss());
        boutonsDialog.addView(btnQuitter);

        layoutDialog.addView(boutonsDialog);
        
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
// bas 1
                               
