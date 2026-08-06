package com.ludexa.moteur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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
    
    // NOUVEAU : Méthodes pour gérer le HUD
    public void ouvrirHUD(Scene scene) {
        this.sceneHudActive = scene;
        if (vueJeu != null) {
            vueJeu.setSceneHud(scene);
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
        layoutPrincipal.setBackgroundColor(Palette.fondPanneaux);

        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setPadding(10, 10, 10, 10);
        bandeauHaut.setBackgroundColor(Palette.fondPanneaux);

        Button boutonQuitter = new Button(this);
        boutonQuitter.setText("Quitter");
        boutonQuitter.setBackgroundColor(Palette.boutonNormal);
        boutonQuitter.setTextColor(Palette.texteNormal);
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

        nomProjet.setTextSize(18f);
        nomProjet.setPadding(20, 0, 20, 0);
        nomProjet.setTextColor(Palette.texteNormal);
        bandeauHaut.addView(nomProjet);

        Button boutonSauvegarde = new Button(this);
        boutonSauvegarde.setText("Sauvegarde");
        boutonSauvegarde.setBackgroundColor(Palette.boutonNormal);
        boutonSauvegarde.setTextColor(Palette.texteNormal);
        boutonSauvegarde.setOnClickListener(v -> sauvegarderProjet());
        bandeauHaut.addView(boutonSauvegarde);

        Button boutonUndo = new Button(this);
        boutonUndo.setText("Undo");
        boutonUndo.setBackgroundColor(Palette.boutonNormal);
        boutonUndo.setTextColor(Palette.texteNormal);
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

        Button boutonRedo = new Button(this);
        boutonRedo.setText("Redo");
        boutonRedo.setBackgroundColor(Palette.boutonNormal);
        boutonRedo.setTextColor(Palette.texteNormal);
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
        canvasEditeur.setLayoutParams(paramsCentre);

        Button boutonZoomMoins = new Button(this);
        boutonZoomMoins.setText("[-]");
        boutonZoomMoins.setBackgroundColor(Palette.boutonNormal);
        boutonZoomMoins.setTextColor(Palette.texteNormal);
        boutonZoomMoins.setOnClickListener(v -> canvasEditeur.zoomMoins());
        bandeauHaut.addView(boutonZoomMoins);

        Button boutonZoomReset = new Button(this);
        boutonZoomReset.setText("[[]]");
        boutonZoomReset.setBackgroundColor(Palette.boutonNormal);
        boutonZoomReset.setTextColor(Palette.texteNormal);
        boutonZoomReset.setOnClickListener(v -> canvasEditeur.zoomReset());
        bandeauHaut.addView(boutonZoomReset);

        Button boutonZoomPlus = new Button(this);
        boutonZoomPlus.setText("[+]");
        boutonZoomPlus.setBackgroundColor(Palette.boutonNormal);
        boutonZoomPlus.setTextColor(Palette.texteNormal);
        boutonZoomPlus.setOnClickListener(v -> canvasEditeur.zoomPlus());
        bandeauHaut.addView(boutonZoomPlus);

        Button boutonDeplacerScene = new Button(this);
        boutonDeplacerScene.setText("Déplacer Scène");
        boutonDeplacerScene.setBackgroundColor(Palette.boutonNormal);
        boutonDeplacerScene.setTextColor(Palette.texteNormal);
        boutonDeplacerScene.setOnClickListener(v -> {
            boolean nouveauMode = !canvasEditeur.isPanMode();
            canvasEditeur.setPanMode(nouveauMode);
            boutonDeplacerScene.setText(nouveauMode ? "Mode: Déplacement" : "Déplacer Scène");
        });
        bandeauHaut.addView(boutonDeplacerScene);

        Button boutonBasculeBlueprint = new Button(this);
        boutonBasculeBlueprint.setText("Node Editor");
        boutonBasculeBlueprint.setBackgroundColor(Palette.boutonNormal);
        boutonBasculeBlueprint.setTextColor(Palette.texteNormal);
        boutonBasculeBlueprint.setOnClickListener(v -> {
            InterfaceBlueprint.sceneACharger = this.sceneActive;
            InterfaceBlueprint.variablesGlobalesACharger = this.variablesGlobales; 
            InterfaceBlueprint.listeScenesACharger = this.listeScenes; 
            
            Intent intent = new Intent(InterfaceEditeur.this, InterfaceBlueprint.class);
            intent.putExtra("cheminProjet", cheminProjet); 
            startActivity(intent);
        });
        bandeauHaut.addView(boutonBasculeBlueprint);

        Button boutonBuild = new Button(this);
        boutonBuild.setText("Build");
        boutonBuild.setBackgroundColor(Palette.boutonNormal);
        boutonBuild.setTextColor(Palette.texteNormal);
        bandeauHaut.addView(boutonBuild);

        Button boutonPlay = new Button(this);
        boutonPlay.setText("Play");
        boutonPlay.setBackgroundColor(Palette.boutonNormal);
        boutonPlay.setTextColor(Palette.texteNormal);
        boutonPlay.setOnClickListener(v -> basculerVersJeu());
        bandeauHaut.addView(boutonPlay);

// fin1
        // debut 2
        
        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
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

        Button boutonStop = new Button(this);
        boutonStop.setText("⏹ STOP");
        boutonStop.setBackgroundColor(Color.RED);
        boutonStop.setTextColor(Color.WHITE);
        boutonStop.setOnClickListener(v -> revenirAEditeur());

        FrameLayout.LayoutParams paramsStop = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        paramsStop.gravity = Gravity.TOP | Gravity.END;
        paramsStop.setMargins(0, 30, 30, 0); 
        
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
// fin 2

    
