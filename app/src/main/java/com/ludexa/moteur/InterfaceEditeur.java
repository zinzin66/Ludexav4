// haut 1 08 08
package com.ludexa.moteur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InterfaceEditeur extends Activity {

    public static final List<Handler> handlersActifs = new ArrayList<>();

    public String cheminProjet; 

    public List<Scene> listeScenes = new ArrayList<>();
    public List<Variable> variablesGlobales = new ArrayList<>(); 
    public Scene sceneActive;
    
    // NOUVEAU : Ajout de la scène HUD active
    public Scene sceneHudActive = null;
    
    private VueJeu vueJeu;
    
    // VARIABLES DE SAUVEGARDE POUR L'ISOLEMENT DU PLAY
    private List<Scene> listeScenesBackup;
    private Scene sceneActiveBackup;
    private Scene sceneHudActiveBackup;
    private List<Variable> variablesGlobalesBackup;

    private CanvasEditeur canvasEditeur;
    private PanneauRessources panneauRessources;
    private InspecteurProprietes menuInspecteur;
    
    public Stack<Commande> undoStack = new Stack<>();
    public Stack<Commande> redoStack = new Stack<>();

    private LinearLayout layoutPrincipal;
    private boolean enModeJeu = false;

    public static final int REQUEST_CODE_IMPORT_ASSET = 1001;

    // ---------------------------------------------------------------- outils UI

    /** Conversion dp -> pixels. */
    private int dp(float valeur) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valeur, getResources().getDisplayMetrics()));
    }

    /** Fond arrondi réutilisable (panneaux, boutons). */
    private GradientDrawable fond(int couleur, int rayonDp, int couleurBordure, int epaisseurDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(couleur);
        g.setCornerRadius(dp(rayonDp));
        if (epaisseurDp > 0) {
            g.setStroke(dp(epaisseurDp), couleurBordure);
        }
        return g;
    }

    /** Applique le look "bouton icône" compact du bandeau. */
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

    /** Petit séparateur vertical du bandeau. */
    private View separateurVertical() {
        View s = new View(this);
        s.setBackgroundColor(Palette.bordure);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1), dp(26));
        lp.setMargins(dp(4), 0, dp(10), 0);
        lp.gravity = Gravity.CENTER_VERTICAL;
        s.setLayoutParams(lp);
        return s;
    }
    
    // NOUVEAU : Méthodes pour gérer le HUD
    public void ouvrirHUD(Scene scene) {
        this.sceneHudActive = scene;
        Blueprint blueprintHud = null;
        if (scene != null && cheminProjet != null) {
            try {
                File dossierLogique = new File(cheminProjet, "logique");
                File fileBlueprintHud = new File(dossierLogique, scene.id + ".json");
                if (fileBlueprintHud.exists()) {
                    BufferedReader brHud = new BufferedReader(new FileReader(fileBlueprintHud));
                    StringBuilder sbHud = new StringBuilder();
                    String ligneHud;
                    while ((ligneHud = brHud.readLine()) != null) {
                        sbHud.append(ligneHud);
                    }
                    brHud.close();
                    blueprintHud = Blueprint.fromJson(sbHud.toString(), scene);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (vueJeu != null) {
            vueJeu.ouvrirHudDynamique(scene, blueprintHud);
        }
        Toast.makeText(this, "HUD ouvert : " + (scene != null ? scene.nom : "null"), Toast.LENGTH_SHORT).show();
    }

    public void fermerHUD() {
        this.sceneHudActive = null;
        if (vueJeu != null) {
            vueJeu.setSceneHud(null);
        }
        Toast.makeText(this, "HUD fermé", Toast.LENGTH_SHORT).show();
    }

    public void ajouterCommande(Commande c) {
        undoStack.push(c);
        redoStack.clear();
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

        layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        layoutPrincipal.setBackgroundColor(Palette.fondNormal);
        layoutPrincipal.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setGravity(Gravity.CENTER_VERTICAL);
        bandeauHaut.setPadding(dp(6), dp(6), dp(6), dp(6));
        bandeauHaut.setBackground(fond(Palette.fondPanneaux, 8, Palette.bordure, 1));

        ImageButton boutonQuitter = new ImageButton(this);
        boutonQuitter.setImageResource(R.drawable.exit_to_app_24px);
        styliserBoutonBandeau(boutonQuitter);
        boutonQuitter.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonQuitter);

        TextView nomProjet = new TextView(this);
        
        String texteNomProjet = "Projet sans nom";
        if (cheminProjet != null) {
            try {
                File metaFile = new File(cheminProjet, "meta.json");
                if (metaFile.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(metaFile));
                    Type type = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> meta = new Gson().fromJson(br, type);
                    br.close();
                    if (meta != null && meta.containsKey("nom")) {
                        texteNomProjet = meta.get("nom");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nomProjet.setText(texteNomProjet);

        nomProjet.setTextSize(15f);
        nomProjet.setLetterSpacing(0.06f);
        nomProjet.setPadding(dp(6), 0, dp(14), 0);
        nomProjet.setGravity(Gravity.CENTER_VERTICAL);
        nomProjet.setTextColor(Palette.texteSelectionne);
        bandeauHaut.addView(nomProjet);

        bandeauHaut.addView(separateurVertical());

        ImageButton boutonSauvegarde = new ImageButton(this);
        boutonSauvegarde.setImageResource(R.drawable.save_24px);
        styliserBoutonBandeau(boutonSauvegarde);
        boutonSauvegarde.setOnClickListener(v -> sauvegarderProjet());
        bandeauHaut.addView(boutonSauvegarde);

        ImageButton boutonUndo = new ImageButton(this);
        boutonUndo.setImageResource(R.drawable.undo_24px);
        styliserBoutonBandeau(boutonUndo);
        boutonUndo.setOnClickListener(v -> {
            if (!undoStack.isEmpty()) {
                Commande c = undoStack.pop();
                c.annuler();
                redoStack.push(c);
                canvasEditeur.invalidate();
                if (menuInspecteur != null) {
                    menuInspecteur.setSceneActive(sceneActive); 
                    menuInspecteur.afficherObjet(canvasEditeur.getObjetSelectionne());
                }
            }
        });
        bandeauHaut.addView(boutonUndo);

        ImageButton boutonRedo = new ImageButton(this);
        boutonRedo.setImageResource(R.drawable.redo_24px);
        styliserBoutonBandeau(boutonRedo);
        boutonRedo.setOnClickListener(v -> {
            if (!redoStack.isEmpty()) {
                Commande c = redoStack.pop();
                c.executer();
                undoStack.push(c);
                canvasEditeur.invalidate();
                if (menuInspecteur != null) {
                    menuInspecteur.setSceneActive(sceneActive); 
                    menuInspecteur.afficherObjet(canvasEditeur.getObjetSelectionne());
                }
            }
        });
        bandeauHaut.addView(boutonRedo);

        bandeauHaut.addView(separateurVertical());
// bas 1

// haut 2
        listeScenes = new ArrayList<>();
        if (cheminProjet != null) {
            try {
                File fileProjet = new File(cheminProjet, "projet_sauvegarde.json");
                if (fileProjet.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(fileProjet));
                    Type listType = new TypeToken<ArrayList<Scene>>(){}.getType();
                    List<Scene> scenesChargees = new Gson().fromJson(br, listType);
                    br.close();
                    if (scenesChargees != null && !scenesChargees.isEmpty()) {
                        listeScenes.addAll(scenesChargees);
                        
                        for (Scene s : scenesChargees) {
                            if (s.variablesLocales != null) {
                                for (Variable v : s.variablesLocales) {
                                    v.corrigerTypeValeur();
                                }
                            }
                        }
                        
                        sceneActive = listeScenes.get(0);
                        
                        // DÉBUT DE LA CORRECTION : Compatibilité ascendante pour Scene.id
                        boolean sceneModifiee = false;
                        for (Scene scene : listeScenes) {
                            if (scene.id == null) {
                                scene.id = java.util.UUID.randomUUID().toString();
                                sceneModifiee = true;
                            }
                        }
                        
                        // Si au moins une scène a reçu un nouvel id, on sauvegarde directement
                        if (sceneModifiee) {
                            try {
                                Gson gson = new Gson();
                                String jsonProjet = gson.toJson(listeScenes);
                                FileWriter writerProjet = new FileWriter(fileProjet);
                                writerProjet.write(jsonProjet);
                                writerProjet.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // FIN DE LA CORRECTION
                    }
                }
                
                try {
                    File fileVariablesGlobales = new File(cheminProjet, "variables_globales.json");
                    if (fileVariablesGlobales.exists()) {
                        BufferedReader brVar = new BufferedReader(new FileReader(fileVariablesGlobales));
                        Type listTypeVar = new TypeToken<ArrayList<Variable>>(){}.getType();
                        List<Variable> variablesChargees = new Gson().fromJson(brVar, listTypeVar);
                        brVar.close();
                        if (variablesChargees != null) {
                            variablesGlobales = new ArrayList<>(variablesChargees);
                            
                            for (Variable v : variablesGlobales) {
                                v.corrigerTypeValeur();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (listeScenes.isEmpty()) {
            sceneActive = new Scene("SceneDepart");
            listeScenes.add(sceneActive);
        }

        canvasEditeur = new CanvasEditeur(this);
        canvasEditeur.setCheminProjet(cheminProjet); 
        canvasEditeur.setScene(sceneActive);
        canvasEditeur.setEditeur(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        paramsCentre.setMargins(dp(8), 0, dp(8), 0);
        canvasEditeur.setLayoutParams(paramsCentre);

        ImageButton boutonZoomMoins = new ImageButton(this);
        boutonZoomMoins.setImageResource(R.drawable.zoom_out_24px);
        styliserBoutonBandeau(boutonZoomMoins);
        boutonZoomMoins.setOnClickListener(v -> canvasEditeur.zoomMoins());
        bandeauHaut.addView(boutonZoomMoins);

        ImageButton boutonZoomReset = new ImageButton(this);
        boutonZoomReset.setImageResource(R.drawable.center_focus_weak_24px);
        styliserBoutonBandeau(boutonZoomReset);
        boutonZoomReset.setOnClickListener(v -> canvasEditeur.zoomReset());
        bandeauHaut.addView(boutonZoomReset);

        ImageButton boutonZoomPlus = new ImageButton(this);
        boutonZoomPlus.setImageResource(R.drawable.zoom_in_24px);
        styliserBoutonBandeau(boutonZoomPlus);
        boutonZoomPlus.setOnClickListener(v -> canvasEditeur.zoomPlus());
        bandeauHaut.addView(boutonZoomPlus);

        ImageButton boutonDeplacerScene = new ImageButton(this);
        boutonDeplacerScene.setImageResource(R.drawable.hand_gesture_24px);
        styliserBoutonBandeau(boutonDeplacerScene);
        boutonDeplacerScene.setOnClickListener(v -> {
            boolean nouveauMode = !canvasEditeur.isPanMode();
            canvasEditeur.setPanMode(nouveauMode);
            boutonDeplacerScene.setBackground(fond(
                    nouveauMode ? Palette.boutonSurvol : Palette.boutonNormal, 6, Palette.bordure, 1));
            Palette.appliquerCouleurIcone(boutonDeplacerScene,
                    nouveauMode ? Palette.iconeSurvol : Palette.iconeNormal);
        });
        bandeauHaut.addView(boutonDeplacerScene);

        bandeauHaut.addView(separateurVertical());

        ImageButton boutonBasculeBlueprint = new ImageButton(this);
        boutonBasculeBlueprint.setImageResource(R.drawable.account_tree_24px);
        styliserBoutonBandeau(boutonBasculeBlueprint);
        boutonBasculeBlueprint.setOnClickListener(v -> {
            InterfaceBlueprint.sceneACharger = this.sceneActive;
            InterfaceBlueprint.variablesGlobalesACharger = this.variablesGlobales; 
            InterfaceBlueprint.listeScenesACharger = this.listeScenes; 
            
            Intent intent = new Intent(InterfaceEditeur.this, InterfaceBlueprint.class);
            intent.putExtra("cheminProjet", cheminProjet); 
            startActivity(intent);
        });
        bandeauHaut.addView(boutonBasculeBlueprint);

        // Espace élastique : les actions de test restent alignées à droite.
        View espaceBandeau = new View(this);
        espaceBandeau.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1f));
        bandeauHaut.addView(espaceBandeau);

        ImageButton boutonBuild = new ImageButton(this);
        boutonBuild.setImageResource(R.drawable.build_24px);
        styliserBoutonBandeau(boutonBuild);
        bandeauHaut.addView(boutonBuild);

        ImageButton boutonPlay = new ImageButton(this);
        boutonPlay.setImageResource(R.drawable.play_circle_24px);
        styliserBoutonBandeau(boutonPlay);
        boutonPlay.setBackground(fond(Palette.boutonSurvol, 6, Palette.bordure, 1));
        boutonPlay.setOnClickListener(v -> basculerVersJeu());
        bandeauHaut.addView(boutonPlay);

        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        paramsMilieu.setMargins(0, dp(8), 0, 0);
        zoneMilieu.setLayoutParams(paramsMilieu);

        panneauRessources = new PanneauRessources(this, canvasEditeur, cheminProjet);
        menuInspecteur = new InspecteurProprietes(this, sceneActive, canvasEditeur);
        menuInspecteur.setCheminProjet(cheminProjet); 
        canvasEditeur.setInspecteur(menuInspecteur);
        
        zoneMilieu.addView(panneauRessources);
        zoneMilieu.addView(canvasEditeur);
        zoneMilieu.addView(menuInspecteur);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }

    public void lancerImportAsset(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        startActivityForResult(intent, REQUEST_CODE_IMPORT_ASSET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMPORT_ASSET && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                if (panneauRessources != null) {
                    panneauRessources.traiterImportAsset(data.getData());
                }
            }
        }
    }

    private void basculerVersJeu() {
        listeScenesBackup = new ArrayList<>(listeScenes);
        sceneActiveBackup = sceneActive;
        sceneHudActiveBackup = sceneHudActive;
        variablesGlobalesBackup = new ArrayList<>(variablesGlobales);
        
        listeScenes = new ArrayList<>();
        for (Scene s : listeScenesBackup) {
            Scene clone = s.clonerProfond();
            listeScenes.add(clone);
            
            if (s == sceneActiveBackup) {
                sceneActive = clone;
            }
            if (s == sceneHudActiveBackup) {
                sceneHudActive = clone;
            }
        }
        
        variablesGlobales = new ArrayList<>();
        for (Variable v : variablesGlobalesBackup) {
            variablesGlobales.add(v.clonerProfond());
        }

        Blueprint blueprintActif = new Blueprint();
        
        File dossierLogique = new File(cheminProjet, "logique");
        // CORRECTION 1 : Chargement basé sur l'id de la scène active
        File fileBlueprint = new File(dossierLogique, sceneActive.id + ".json");

        if (fileBlueprint.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileBlueprint));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                String json = sb.toString();
                blueprintActif = Blueprint.fromJson(json, sceneActive);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la lecture du Blueprint.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Aucun Blueprint sauvegardé. Cliquez sur Sauvegarde avant de faire Play.", Toast.LENGTH_LONG).show();
        }

        // CORRECTION 2 : Chargement pour le HUD
        Blueprint blueprintHud = null;
        if (sceneHudActive != null) {
            File fileBlueprintHud = new File(dossierLogique, sceneHudActive.id + ".json");
            if (fileBlueprintHud.exists()) {
                try {
                    BufferedReader brHud = new BufferedReader(new FileReader(fileBlueprintHud));
                    StringBuilder sbHud = new StringBuilder();
                    String ligneHud;
                    while ((ligneHud = brHud.readLine()) != null) {
                        sbHud.append(ligneHud);
                    }
                    brHud.close();
                    blueprintHud = Blueprint.fromJson(sbHud.toString(), sceneHudActive);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // CORRECTION 3 : Passage de blueprintHud et sceneHudActive au constructeur
        this.vueJeu = new VueJeu(this, sceneActive, blueprintActif, cheminProjet, sceneHudActive, blueprintHud);
        
        FrameLayout conteneurJeu = new FrameLayout(this);
        conteneurJeu.addView(this.vueJeu, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        ImageButton boutonStop = new ImageButton(this);
        boutonStop.setImageResource(R.drawable.stop_circle_24px);
        boutonStop.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        boutonStop.setPadding(dp(6), dp(6), dp(6), dp(6));
        boutonStop.setBackground(fond(Color.parseColor("#C0392B"), 6, Palette.bordure, 1));
        Palette.appliquerCouleurIcone(boutonStop, Palette.iconeNormal);
        boutonStop.setOnClickListener(v -> revenirAEditeur());

        FrameLayout.LayoutParams paramsStop = new FrameLayout.LayoutParams(dp(38), dp(38));
        paramsStop.gravity = Gravity.TOP | Gravity.END;
        paramsStop.setMargins(0, dp(12), dp(12), 0); 
        
        conteneurJeu.addView(boutonStop, paramsStop);

        setContentView(conteneurJeu);
        enModeJeu = true;
    }
    
    private void revenirAEditeur() {
        for (Handler handler : handlersActifs) {
            handler.removeCallbacksAndMessages(null);
        }
        handlersActifs.clear();

        if (enModeJeu) {
            setContentView(layoutPrincipal);
            enModeJeu = false;
            
            if (listeScenesBackup != null) {
                listeScenes = listeScenesBackup;
                listeScenesBackup = null;
            }
            if (sceneActiveBackup != null) {
                sceneActive = sceneActiveBackup;
                sceneActiveBackup = null;
            }
            if (sceneHudActiveBackup != null) {
                sceneHudActive = sceneHudActiveBackup;
                sceneHudActiveBackup = null;
            }
            if (variablesGlobalesBackup != null) {
                variablesGlobales = variablesGlobalesBackup;
                variablesGlobalesBackup = null;
            }
            
            canvasEditeur.setScene(sceneActive);
            
            if (menuInspecteur != null) {
                menuInspecteur.setSceneActive(sceneActive); 
            }
            
            panneauRessources.rafraichirScenes();

            canvasEditeur.invalidate();
            if (menuInspecteur != null) {
                menuInspecteur.afficherObjet(canvasEditeur.getObjetSelectionne());
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        if (enModeJeu) {
            revenirAEditeur();
        } else {
            super.onBackPressed();
        }
    }

    public void creerScene(String nom) {
        Scene nouvelleScene = new Scene(nom);
        listeScenes.add(nouvelleScene);
        changerScene(nouvelleScene);
    }

    public void changerScene(Scene scene) {
        this.sceneActive = scene;
        canvasEditeur.setScene(scene);
        canvasEditeur.deselectionner();
        if (menuInspecteur != null) {
            menuInspecteur.setSceneActive(scene); 
            menuInspecteur.afficherObjet(null);
        }
        panneauRessources.rafraichirScenes();
        panneauRessources.rafraichirVariables(); 
        canvasEditeur.invalidate();
    }

    public void rafraichirArborescence(ObjetBase objet) {
        if (panneauRessources != null) {
            panneauRessources.setObjetSelectionne(objet);
        }
    }

    private void sauvegarderProjet() {
        try {
            Gson gson = new Gson();
            String jsonProjet = gson.toJson(listeScenes);
            
            File fileProjet = new File(cheminProjet, "projet_sauvegarde.json");
            
            FileWriter writerProjet = new FileWriter(fileProjet);
            writerProjet.write(jsonProjet);
            writerProjet.close();

            try {
                File fileVariablesGlobales = new File(cheminProjet, "variables_globales.json");
                String jsonVariables = gson.toJson(variablesGlobales);
                FileWriter writerVariables = new FileWriter(fileVariablesGlobales);
                writerVariables.write(jsonVariables);
                writerVariables.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Projet sauvegardé avec succès.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }
}
// bas 2
