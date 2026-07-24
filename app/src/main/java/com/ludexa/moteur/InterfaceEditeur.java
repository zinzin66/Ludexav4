package com.ludexa.moteur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import com.google.gson.Gson;

public class InterfaceEditeur extends Activity {

    public List<Scene> listeScenes = new ArrayList<>();
    public Scene sceneActive;
    private CanvasEditeur canvasEditeur;
    private PanneauRessources panneauRessources;
    private InspecteurProprietes menuInspecteur;
    
    public Stack<Commande> undoStack = new Stack<>();
    public Stack<Commande> redoStack = new Stack<>();

    // --- NOUVEAU : Variables pour gérer l'affichage ---
    private LinearLayout layoutPrincipal;
    private boolean enModeJeu = false;

    public void ajouterCommande(Commande c) {
        undoStack.push(c);
        redoStack.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        LinearLayout bandeauHaut = new LinearLayout(this);
        bandeauHaut.setOrientation(LinearLayout.HORIZONTAL);
        bandeauHaut.setPadding(10, 10, 10, 10);

        Button boutonQuitter = new Button(this);
        boutonQuitter.setText("Quitter");
        boutonQuitter.setOnClickListener(v -> finish());
        bandeauHaut.addView(boutonQuitter);

        TextView nomProjet = new TextView(this);
        nomProjet.setText("Projet sans nom");
        nomProjet.setTextSize(18f);
        nomProjet.setPadding(20, 0, 20, 0);
        bandeauHaut.addView(nomProjet);

        Button boutonSauvegarde = new Button(this);
        boutonSauvegarde.setText("Sauvegarde");
        boutonSauvegarde.setOnClickListener(v -> sauvegarderProjet());
        bandeauHaut.addView(boutonSauvegarde);

        Button boutonUndo = new Button(this);
        boutonUndo.setText("Undo");
        boutonUndo.setOnClickListener(v -> {
            if (!undoStack.isEmpty()) {
                Commande c = undoStack.pop();
                c.annuler();
                redoStack.push(c);
                canvasEditeur.invalidate();
                if (menuInspecteur != null) {
                    menuInspecteur.afficherObjet(canvasEditeur.getObjetSelectionne());
                }
            }
        });
        bandeauHaut.addView(boutonUndo);

        Button boutonRedo = new Button(this);
        boutonRedo.setText("Redo");
        boutonRedo.setOnClickListener(v -> {
            if (!redoStack.isEmpty()) {
                Commande c = redoStack.pop();
                c.executer();
                undoStack.push(c);
                canvasEditeur.invalidate();
                if (menuInspecteur != null) {
                    menuInspecteur.afficherObjet(canvasEditeur.getObjetSelectionne());
                }
            }
        });
        bandeauHaut.addView(boutonRedo);

        sceneActive = new Scene("SceneDepart");
        listeScenes.add(sceneActive);

        canvasEditeur = new CanvasEditeur(this);
        canvasEditeur.setScene(sceneActive);
        canvasEditeur.setEditeur(this);
        LinearLayout.LayoutParams paramsCentre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        canvasEditeur.setLayoutParams(paramsCentre);

        Button boutonZoomMoins = new Button(this);
        boutonZoomMoins.setText("[-]");
        boutonZoomMoins.setOnClickListener(v -> canvasEditeur.zoomMoins());
        bandeauHaut.addView(boutonZoomMoins);

        Button boutonZoomReset = new Button(this);
        boutonZoomReset.setText("[[]]");
        boutonZoomReset.setOnClickListener(v -> canvasEditeur.zoomReset());
        bandeauHaut.addView(boutonZoomReset);

        Button boutonZoomPlus = new Button(this);
        boutonZoomPlus.setText("[+]");
        boutonZoomPlus.setOnClickListener(v -> canvasEditeur.zoomPlus());
        bandeauHaut.addView(boutonZoomPlus);

        Button boutonDeplacerScene = new Button(this);
        boutonDeplacerScene.setText("Déplacer Scène");
        boutonDeplacerScene.setOnClickListener(v -> {
            boolean nouveauMode = !canvasEditeur.isPanMode();
            canvasEditeur.setPanMode(nouveauMode);
            boutonDeplacerScene.setText(nouveauMode ? "Mode: Déplacement" : "Déplacer Scène");
        });
        bandeauHaut.addView(boutonDeplacerScene);

        Button boutonBasculeBlueprint = new Button(this);
        boutonBasculeBlueprint.setText("Node Editor");
        boutonBasculeBlueprint.setOnClickListener(v -> {
            InterfaceBlueprint.sceneACharger = this.sceneActive;
            Intent intent = new Intent(InterfaceEditeur.this, InterfaceBlueprint.class);
            startActivity(intent);
        });
        bandeauHaut.addView(boutonBasculeBlueprint);

        Button boutonBuild = new Button(this);
        boutonBuild.setText("Build");
        bandeauHaut.addView(boutonBuild);

        Button boutonPlay = new Button(this);
        boutonPlay.setText("Play");
        boutonPlay.setOnClickListener(v -> basculerVersJeu());
        bandeauHaut.addView(boutonPlay);

        LinearLayout zoneMilieu = new LinearLayout(this);
        zoneMilieu.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsMilieu = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        zoneMilieu.setLayoutParams(paramsMilieu);

        panneauRessources = new PanneauRessources(this, canvasEditeur);
        menuInspecteur = new InspecteurProprietes(this, sceneActive, canvasEditeur);
        canvasEditeur.setInspecteur(menuInspecteur);
        
        zoneMilieu.addView(panneauRessources);
        zoneMilieu.addView(canvasEditeur);
        zoneMilieu.addView(menuInspecteur);

        layoutPrincipal.addView(bandeauHaut);
        layoutPrincipal.addView(zoneMilieu);

        setContentView(layoutPrincipal);
    }

    // --- METHODE CORRIGEE : Basculer vers le mode Jeu ---
    private void basculerVersJeu() {
        ObjetBase objetCible = null;
        
        if (sceneActive != null && sceneActive.objets != null && !sceneActive.objets.isEmpty()) {
            objetCible = sceneActive.objets.get(0);
        }

        // 1. Charger le vrai Blueprint sauvegardé via le fichier JSON
        String jsonDuBlueprint = "";
        try {
            // Assurez-vous que le nom du fichier correspond à celui utilisé lors de la sauvegarde du Blueprint
            File file = new File(getFilesDir(), "blueprint.json"); 
            if (file.exists()) {
                Scanner scanner = new Scanner(file).useDelimiter("\\A");
                jsonDuBlueprint = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Création du Blueprint via la méthode statique avec la scène active
        Blueprint blueprintActif = Blueprint.fromJson(jsonDuBlueprint, sceneActive);

        // 2. Appeler MoteurLogique.executerDemarrage() SUR ce Blueprint chargé
        if (blueprintActif != null) {
            MoteurLogique moteur = new MoteurLogique(blueprintActif);
            moteur.executerDemarrage();
        }

        // 3. Basculer l'affichage vers VueJeu
        VueJeu vueJeu = new VueJeu(this, objetCible, blueprintActif);
        
        // Création du conteneur superposé
        FrameLayout conteneurJeu = new FrameLayout(this);
        conteneurJeu.addView(vueJeu, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Création du bouton Stop
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

        // Bascule de l'affichage
        setContentView(conteneurJeu);
        enModeJeu = true;
    }
    
    private void revenirAEditeur() {
        if (enModeJeu) {
            setContentView(layoutPrincipal);
            enModeJeu = false;
            
            // On rafraîchit l'éditeur au retour au cas où
            canvasEditeur.invalidate();
            if (menuInspecteur != null) {
                menuInspecteur.afficherObjet(canvasEditeur.getObjetSelectionne());
            }
        }
    }
    
    // --- GESTION DU BOUTON RETOUR PHYSIQUE ---
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
            menuInspecteur.afficherObjet(null);
        }
        panneauRessources.rafraichirScenes();
        canvasEditeur.invalidate();
    }

    private void sauvegarderProjet() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(listeScenes);
            File file = new File(getFilesDir(), "projet_sauvegarde.json");
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
            Toast.makeText(this, "Projet sauvegardé dans : " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }
}
