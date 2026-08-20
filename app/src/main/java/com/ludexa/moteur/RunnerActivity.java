// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RunnerActivity extends Activity {

    public List<Variable> variablesGlobales = new ArrayList<>();
    public List<Scene> listeScenes = new ArrayList<>();
    public Scene sceneActive;
    public Scene sceneHudActive = null;

    private VueJeu vueJeu;
    private String cheminProjet;

    public VueJeu getVueJeu() {
        return this.vueJeu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cacherBarresSysteme();

        NoeudBase.contexteApplication = this;
        Traducteur.initialiser(this, "fr");
        RegistreNoeuds.initialiser();

        File dossierJeu = new File(getFilesDir(), "donnees_jeu_exporte");
        cheminProjet = dossierJeu.getAbsolutePath();

        if (!new File(dossierJeu, "projet_sauvegarde.json").exists()) {
            if (!extraireAssetsZIP(dossierJeu)) {
                Toast.makeText(this, "Erreur : Fichiers du jeu introuvables.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        chargerDonneesJeu();

        if (sceneActive != null) {
            Blueprint blueprintActif = chargerBlueprint(sceneActive.id);
            this.vueJeu = new VueJeu(this, sceneActive, blueprintActif, cheminProjet, null, null);
            
            FrameLayout conteneurJeu = new FrameLayout(this);
            conteneurJeu.setBackgroundColor(Color.BLACK);
            conteneurJeu.addView(this.vueJeu, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));

            setContentView(conteneurJeu);
        } else {
            Toast.makeText(this, "Erreur : Impossible de charger la scène.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
// bas 1
// haut 2
    private void chargerDonneesJeu() {
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
                            for (Variable v : s.variablesLocales) v.corrigerTypeValeur();
                        }
                    }
                    sceneActive = listeScenes.get(0);
                }
            }

            File fileVar = new File(cheminProjet, "variables_globales.json");
            if (fileVar.exists()) {
                BufferedReader brVar = new BufferedReader(new FileReader(fileVar));
                Type listTypeVar = new TypeToken<ArrayList<Variable>>(){}.getType();
                List<Variable> variablesChargees = new Gson().fromJson(brVar, listTypeVar);
                brVar.close();
                if (variablesChargees != null) {
                    variablesGlobales = new ArrayList<>(variablesChargees);
                    for (Variable v : variablesGlobales) v.corrigerTypeValeur();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Blueprint chargerBlueprint(String idScene) {
        Blueprint bp = new Blueprint();
        try {
            File fileBlueprint = new File(cheminProjet + "/logique", idScene + ".json");
            if (fileBlueprint.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(fileBlueprint));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                bp = Blueprint.fromJson(sb.toString(), sceneActive);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bp;
    }

    private boolean extraireAssetsZIP(File dossierCible) {
        try {
            dossierCible.mkdirs();
            InputStream is = getAssets().open("jeu_exporte.zip");
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry;
            byte[] buffer = new byte[8192];

            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(dossierCible, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(outFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zis.closeEntry();
            }
            zis.close();
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
// bas 2

// haut 3
    private void cacherBarresSysteme() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    
    @Override
    public void onBackPressed() {
        // Désactive le bouton retour pour éviter de quitter le jeu par erreur
    }

    // --- METHODES ESSENTIELLES POUR LES NOEUDS (HUD & SCENES) ---

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
    }

    public void fermerHUD() {
        this.sceneHudActive = null;
        if (vueJeu != null) {
            vueJeu.setSceneHud(null);
        }
    }

    public void changerScene(Scene scene) {
        this.sceneActive = scene;
        if (vueJeu != null) {
            vueJeu.chargerNouvelleScene(scene);
        }
    }
}
// bas 3




    




