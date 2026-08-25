// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

    public Scene sceneActive;

    public static Scene sceneACharger; 
    public static List<Variable> variablesGlobalesACharger; 
    public static List<Scene> listeScenesACharger;

    public List<Variable> variablesGlobales; 
    public List<Scene> listeScenes; 

    private Blueprint blueprintActif;
    private CanvasBlueprint canvasBlueprint;
    
    private boolean estModeFonction = false;
    private String nomFonctionActive = null;

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
        estModeFonction = getIntent().getBooleanExtra("modeFonction", false);
        nomFonctionActive = getIntent().getStringExtra("nomFonction");
        
        this.variablesGlobales = variablesGlobalesACharger; 
        this.listeScenes = listeScenesACharger; 

        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        layoutPrincipal.setBackgroundColor(Palette.fondNormal);
        layoutPrincipal.setPadding(dp(8), dp(8), dp(8), dp(8));

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
// bas 1

// haut 2
        TextView titreBlueprint = new TextView(this);
        if (estModeFonction) {
            titreBlueprint.setText(Traducteur.get("titre_fonction") + nomFonctionActive);
        } else {
            titreBlueprint.setText(Traducteur.get("titre_blueprint"));
        }
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

        ImageButton boutonCopierNode = new ImageButton(this);
        boutonCopierNode.setImageResource(R.drawable.add_24px);
        styliserBoutonBandeau(boutonCopierNode);
        boutonCopierNode.setOnClickListener(v -> canvasBlueprint.dupliquerNoeudSelectionne());
        bandeauHaut.addView(boutonCopierNode);

        ImageButton boutonReplierNode = new ImageButton(this);
        boutonReplierNode.setImageResource(R.drawable.hide_image_24px);
        styliserBoutonBandeau(boutonReplierNode);
        boutonReplierNode.setOnClickListener(v -> canvasBlueprint.basculerRepliNoeudSelectionne());
        bandeauHaut.addView(boutonReplierNode);

        View espaceBandeau = new View(this);
        espaceBandeau.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1f));
        bandeauHaut.addView(espaceBandeau);

        ImageButton boutonCode = new ImageButton(this);
        boutonCode.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonBandeau(boutonCode);
        boutonCode.setBackground(fond(Palette.boutonSurvol, 6, Palette.bordure, 1));
        boutonCode.setOnClickListener(v -> afficherFenetreCode());
        bandeauHaut.addView(boutonCode);

        Button boutonAide = new Button(this);
        boutonAide.setText("?");
        boutonAide.setTextSize(18f);
        boutonAide.setTextColor(Palette.texteNormal);
        boutonAide.setTypeface(null, android.graphics.Typeface.BOLD);
        boutonAide.setBackground(fond(Palette.boutonNormal, 6, Palette.bordure, 1));
        boutonAide.setPadding(0, 0, 0, 0); 
        LinearLayout.LayoutParams lpAide = new LinearLayout.LayoutParams(dp(38), dp(38));
        lpAide.setMargins(0, 0, dp(6), 0);
        lpAide.gravity = Gravity.CENTER_VERTICAL;
        boutonAide.setLayoutParams(lpAide);
        boutonAide.setOnClickListener(v -> afficherFenetreAide());
        bandeauHaut.addView(boutonAide);

        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        paramsMilieu.setMargins(0, dp(8), 0, 0);
        zoneMilieu.setLayoutParams(paramsMilieu);

        PanneauNoeuds panneauNoeuds = new PanneauNoeuds(this);

        if (estModeFonction) {
            canvasBlueprint.sceneActive = new Scene(nomFonctionActive);
        } else if (sceneACharger != null) {
            canvasBlueprint.sceneActive = sceneACharger;
        } else {
            canvasBlueprint.sceneActive = new Scene(Traducteur.get("scene_vide_fallback"));
        }
        this.sceneActive = canvasBlueprint.sceneActive;

        chargerBlueprintLocal(true);

        zoneMilieu.addView(panneauNoeuds);
        zoneMilieu.addView(canvasBlueprint);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }
// bas 2

// haut 3
    private void sauvegarderBlueprintLocal() {
        try {
            File file;
            if (estModeFonction && nomFonctionActive != null) {
                File dir = new File(cheminProjet, "fonctions");
                if (!dir.exists()) dir.mkdirs();
                file = new File(dir, nomFonctionActive + ".json");
            } else {
                File dir = new File(cheminProjet, "logique");
                if (!dir.exists()) dir.mkdirs();
                file = new File(dir, canvasBlueprint.sceneActive.id + ".json");
            }
            
            String json = blueprintActif.toJson();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();
            Toast.makeText(this, Traducteur.get("toast_blueprint_sauvegarde"), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, Traducteur.get("erreur_sauvegarde"), Toast.LENGTH_SHORT).show();
        }
    }

    private void chargerBlueprintLocal(boolean estChargementAuto) {
        try {
            File file;
            if (estModeFonction && nomFonctionActive != null) {
                File dir = new File(cheminProjet, "fonctions");
                file = new File(dir, nomFonctionActive + ".json");
            } else {
                File dir = new File(cheminProjet, "logique");
                file = new File(dir, canvasBlueprint.sceneActive.id + ".json");
            }
            
            if (!file.exists()) {
                if (!estChargementAuto) {
                    Toast.makeText(this, Traducteur.get("toast_aucune_sauvegarde"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, Traducteur.get("toast_blueprint_charge"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!estChargementAuto) {
                Toast.makeText(this, Traducteur.get("erreur_chargement"), Toast.LENGTH_SHORT).show();
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
        
        if (noeud.requiertCibleObjet()) {
            if ("__OBJET_IMPLIQUE__".equals(noeud.nomCibleObjet)) {
                details.add(Traducteur.get("noeud_format_cible") + Traducteur.get("noeud_objet_implique_long"));
            } else if (noeud.nomCibleObjet != null && !noeud.nomCibleObjet.isEmpty()) {
                details.add(Traducteur.get("noeud_format_cible") + noeud.nomCibleObjet);
            } else if (noeud.getCibleObjet() != null) {
                details.add(Traducteur.get("noeud_format_cible") + noeud.getCibleObjet().nom);
            }
        }
        
        if (noeud.requiertCibleObjetB()) {
            if ("__OBJET_IMPLIQUE__".equals(noeud.nomCibleObjetB)) {
                details.add(Traducteur.get("noeud_format_objet_b") + Traducteur.get("noeud_objet_implique_long"));
            } else if (noeud.nomCibleObjetB != null && !noeud.nomCibleObjetB.isEmpty()) {
                details.add(Traducteur.get("noeud_format_objet_b") + noeud.nomCibleObjetB);
            } else if (noeud.getCibleObjetB() != null) {
                details.add(Traducteur.get("noeud_format_objet_b") + noeud.getCibleObjetB().nom);
            }
        }
        
        if (noeud.requiertCibleVariable() && noeud.getCibleVariable() != null) {
            details.add(Traducteur.get("noeud_format_var") + noeud.getCibleVariable().nom);
        }
        if (noeud.requiertCibleScene() && noeud.getCibleScene() != null) {
            details.add(Traducteur.get("noeud_format_scene") + noeud.getCibleScene().nom);
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
// bas 3
// haut 4
    private void genererCheminLogique(NoeudBase noeudDepart, String portDeclencheur, String indentation, StringBuilder res, Set<String> noeudsVisites) {
        if (noeudDepart == null || blueprintActif.liens == null) return;
        
        for (Blueprint.Lien lien : blueprintActif.liens) {
            if (lien.noeudDepart == noeudDepart && lien.portSortieNom.equals(portDeclencheur)) {
                NoeudBase noeudSuivant = lien.noeudArrivee;
                if (noeudSuivant != null) {
                    noeudsVisites.add(noeudSuivant.id);
                    
                    String prefixe = indentation + "-> ";
                    if (!portDeclencheur.equals("Suivant") && !portDeclencheur.equals("Sortie")) {
                        prefixe = indentation + "(" + Traducteur.get("blueprint_si") + " " + portDeclencheur + ") -> ";
                    }
                    
                    res.append(prefixe).append(formaterNoeud(noeudSuivant)).append("\n");
                    
                    for (Port pSortieSuivant : noeudSuivant.portsSortie) {
                        if (Port.TYPE_EXECUTION_SORTIE.equals(pSortieSuivant.type)) {
                            genererCheminLogique(noeudSuivant, pSortieSuivant.nom, indentation + "  ", res, noeudsVisites);
                        }
                    }
                }
            }
        }
    }

    private void afficherFenetreAide() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle(Traducteur.get("titre_aide_blueprint"));

        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(dp(16), dp(16), dp(16), dp(16));
        layoutDialog.setBackground(fond(Palette.fondPanneaux, 8, Palette.bordure, 1));

        TextView enTeteDialog = new TextView(this);
        enTeteDialog.setText(Traducteur.get("doc_noeuds_titre_1") + EcranDemarrage.langueCourante.toUpperCase() + Traducteur.get("doc_noeuds_titre_2"));
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
        try {
            String nomFichier = "doc_noeuds_" + EcranDemarrage.langueCourante + ".txt";
            java.io.InputStream is = getAssets().open(nomFichier);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                res.append(line).append("\n");
            }
            br.close();
        } catch (Exception e) {
            res.append(Traducteur.get("erreur_doc_1"))
               .append(EcranDemarrage.langueCourante)
               .append(Traducteur.get("erreur_doc_2"))
               .append(Traducteur.get("erreur_doc_3"))
               .append(EcranDemarrage.langueCourante)
               .append(Traducteur.get("erreur_doc_4"));
        }

        TextView textViewAide = new TextView(this);
        textViewAide.setText(res.toString());
        textViewAide.setTextSize(14f); 
        textViewAide.setTextColor(Palette.texteNormal);
        textViewAide.setPadding(dp(10), dp(10), dp(10), dp(10));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackground(fond(Palette.fondListe, 6, Palette.bordure, 1));
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollParams.setMargins(0, 0, 0, dp(12));
        scrollView.setLayoutParams(scrollParams);
        scrollView.addView(textViewAide);

        layoutDialog.addView(scrollView);

        LinearLayout boutonsDialog = new LinearLayout(this);
        boutonsDialog.setOrientation(LinearLayout.HORIZONTAL);
        boutonsDialog.setGravity(Gravity.END);

        Button btnQuitter = new Button(this);
        btnQuitter.setText(Traducteur.get("bouton_fermer"));
        styliserBoutonDialog(btnQuitter);
        btnQuitter.setOnClickListener(v -> dialog.dismiss());
        boutonsDialog.addView(btnQuitter);

        layoutDialog.addView(boutonsDialog);
        
        dialog.setContentView(layoutDialog);
        
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            android.util.DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.85);
            int height = (int) (metrics.heightPixels * 0.85);
            window.setLayout(width, height);
        }
        
        dialog.show();
    }
// bas 4

// haut 5
    private void afficherFenetreCode() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle(Traducteur.get("titre_resume_blueprint"));

        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(dp(16), dp(16), dp(16), dp(16));
        layoutDialog.setBackground(fond(Palette.fondPanneaux, 8, Palette.bordure, 1));

        TextView enTeteDialog = new TextView(this);
        enTeteDialog.setText(Traducteur.get("titre_script_blueprint"));
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
        res.append(Traducteur.get("en_tete_script"));
        
        if (blueprintActif == null || blueprintActif.noeuds.isEmpty()) {
            res.append(Traducteur.get("msg_blueprint_vide"));
        } else {
            Set<String> noeudsVisites = new HashSet<>();
            List<NoeudBase> evenements = new ArrayList<>();
            
            for (NoeudBase noeud : blueprintActif.noeuds) {
                if (noeud.getClass().getSimpleName().startsWith("NoeudEvent") || noeud.nom.equals("Début")) {
                    evenements.add(noeud);
                }
            }
            
            if (evenements.isEmpty()) {
                res.append(Traducteur.get("msg_aucun_evenement"));
            } else {
                for (NoeudBase evt : evenements) {
                    noeudsVisites.add(evt.id);
                    res.append(Traducteur.get("msg_evenement")).append(formaterNoeud(evt)).append("\n");
                    for (Port pSortie : evt.portsSortie) {
                        if (Port.TYPE_EXECUTION_SORTIE.equals(pSortie.type)) {
                            genererCheminLogique(evt, pSortie.nom, "  ", res, noeudsVisites);
                        }
                    }
                    res.append("\n");
                }
            }
            
            boolean aOrphelins = false;
            StringBuilder orphelinsRes = new StringBuilder();
            orphelinsRes.append(Traducteur.get("msg_noeuds_orphelins"));
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
        btnCopier.setText(Traducteur.get("bouton_copier"));
        styliserBoutonDialog(btnCopier);
        btnCopier.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(Traducteur.get("code_presse_papier"), textViewCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, Traducteur.get("toast_script_copie"), Toast.LENGTH_SHORT).show();
        });
        boutonsDialog.addView(btnCopier);

        Button btnQuitter = new Button(this);
        btnQuitter.setText(Traducteur.get("bouton_quitter"));
        styliserBoutonDialog(btnQuitter);
        btnQuitter.setOnClickListener(v -> dialog.dismiss());
        boutonsDialog.addView(btnQuitter);

        layoutDialog.addView(boutonsDialog);
        
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
// bas 5







    


    


    



        
