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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterfaceBlueprint extends Activity {

    public String cheminProjet; 

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

        // --- NOUVEAU BOUTON COPIER UN NOEUD ---
        ImageButton boutonCopierNode = new ImageButton(this);
        boutonCopierNode.setImageResource(R.drawable.add_24px);
        styliserBoutonBandeau(boutonCopierNode);
        boutonCopierNode.setOnClickListener(v -> canvasBlueprint.dupliquerNoeudSelectionne());
        bandeauHaut.addView(boutonCopierNode);

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

        if (sceneACharger != null) {
            canvasBlueprint.sceneActive = sceneACharger;
        } else {
            canvasBlueprint.sceneActive = new Scene("Scène Vide (Fallback)");
        }

        chargerBlueprintLocal(true);

        zoneMilieu.addView(panneauNoeuds);
        zoneMilieu.addView(canvasBlueprint);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }
// bas 1

// haut 2
    private void sauvegarderBlueprintLocal() {
        try {
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
            File dir = new File(cheminProjet, "logique");
            File file = new File(dir, canvasBlueprint.sceneActive.id + ".json");
            
            if (!file.exists()) {
                if (!estChargementAuto) {
                    Toast.makeText(this, "Aucune sauvegarde trouvée", Toast.LENGTH_SHORT).show();
                }
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

    private String formaterNoeud(NoeudBase noeud) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(noeud.nom).append("]");
        
        List<String> details = new ArrayList<>();
        if (noeud.requiertCibleObjet() && noeud.getCibleObjet() != null) {
            details.add("Cible : " + noeud.getCibleObjet().nom);
        }
        if (noeud.requiertCibleObjetB() && noeud.getCibleObjetB() != null) {
            details.add("Objet B : " + noeud.getCibleObjetB().nom);
        }
        if (noeud.requiertCibleVariable() && noeud.getCibleVariable() != null) {
            details.add("Var : " + noeud.getCibleVariable().nom);
        }
        if (noeud.requiertCibleScene() && noeud.getCibleScene() != null) {
            details.add("Scène : " + noeud.getCibleScene().nom);
        }
        
        if (noeud.getNomsParametres() != null) {
            for (String param : noeud.getNomsParametres()) {
                String val = noeud.getValeurParametre(param);
                if (val != null && !val.isEmpty()) {
                    details.add(param + " : " + val);
                }
            }
        }
        
        if (!details.isEmpty()) {
            sb.append(" | ").append(String.join(" | ", details));
        }
        return sb.toString();
    }

    private void genererCheminLogique(NoeudBase noeudDepart, String portDeclencheur, String indentation, StringBuilder res, Set<String> noeudsVisites) {
        if (noeudDepart == null || blueprintActif.liens == null) return;
        
        for (Blueprint.Lien lien : blueprintActif.liens) {
            if (lien.noeudDepart == noeudDepart && lien.portSortieNom.equals(portDeclencheur)) {
                NoeudBase noeudSuivant = lien.noeudArrivee;
                if (noeudSuivant != null) {
                    noeudsVisites.add(noeudSuivant.id);
                    
                    String prefixe = indentation + "-> ";
                    if (!portDeclencheur.equals("Suivant") && !portDeclencheur.equals("Sortie")) {
                        prefixe = indentation + "(Si " + portDeclencheur + ") -> ";
                    }
                    
                    res.append(prefixe).append(formaterNoeud(noeudSuivant)).append("\n");
                    
                    genererCheminLogique(noeudSuivant, "Suivant", indentation + "  ", res, noeudsVisites);
                    genererCheminLogique(noeudSuivant, "Vrai", indentation + "  ", res, noeudsVisites);
                    genererCheminLogique(noeudSuivant, "Faux", indentation + "  ", res, noeudsVisites);
                }
            }
        }
    }
// bas 2


// haut 3
    private void afficherFenetreCode() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Résumé du Blueprint");

        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(dp(16), dp(16), dp(16), dp(16));
        layoutDialog.setBackground(fond(Palette.fondPanneaux, 8, Palette.bordure, 1));

        TextView enTeteDialog = new TextView(this);
        enTeteDialog.setText("SCRIPT BLUEPRINT (Pseudo-Code)");
        enTeteDialog.setTextSize(14f);
        enTeteDialog.setTextColor(Palette.texteSelectionne);
        enTeteDialog.setBackground(fond(Palette.enTeteDialogues, 6, Palette.bordure, 1));
        enTeteDialog.setPadding(dp(10), dp(8), dp(10), dp(8));
        LinearLayout.LayoutParams paramsEnTete = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsEnTete.setMargins(0, 0, 0, dp(10));
        enTeteDialog.setLayoutParams(paramsEnTete);
        layoutDialog.addView(enTeteDialog);

        StringBuilder res = new StringBuilder();
        res.append("=== SCRIPT BLUEPRINT ===\n\n");
        
        if (blueprintActif == null || blueprintActif.noeuds.isEmpty()) {
            res.append("Le Blueprint est vide.\n");
        } else {
            Set<String> noeudsVisites = new HashSet<>();
            List<NoeudBase> evenements = new ArrayList<>();
            
            for (NoeudBase noeud : blueprintActif.noeuds) {
                if (noeud.getClass().getSimpleName().startsWith("NoeudEvent") || noeud.nom.equals("Début")) {
                    evenements.add(noeud);
                }
            }
            
            if (evenements.isEmpty()) {
                res.append("⚠️ Aucun Événement de départ détecté.\n\n");
            } else {
                for (NoeudBase evt : evenements) {
                    noeudsVisites.add(evt.id);
                    res.append("ÉVÉNEMENT : ").append(formaterNoeud(evt)).append("\n");
                    genererCheminLogique(evt, "Sortie", "  ", res, noeudsVisites);
                    genererCheminLogique(evt, "Suivant", "  ", res, noeudsVisites);
                    res.append("\n");
                }
            }
            
            boolean aOrphelins = false;
            StringBuilder orphelinsRes = new StringBuilder();
            orphelinsRes.append("--- Nœuds Orphelins (Non reliés) ---\n");
            for (NoeudBase noeud : blueprintActif.noeuds) {
                if (!noeudsVisites.contains(noeud.id)) {
                    aOrphelins = true;
                    orphelinsRes.append("- ").append(formaterNoeud(noeud)).append("\n");
                }
            }
            if (aOrphelins) {
                res.append(orphelinsRes);
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
            Toast.makeText(this, "Script copié dans le presse-papier !", Toast.LENGTH_SHORT).show();
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
// bas 3




    



