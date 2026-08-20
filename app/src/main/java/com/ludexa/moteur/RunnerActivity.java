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

    // On prépare les variables globales pour respecter l'architecture de ton éditeur
    public List<Variable> variablesGlobales = new ArrayList<>();
    public List<Scene> listeScenes = new ArrayList<>();
    public Scene sceneActive;

    private VueJeu vueJeu;
    private String cheminProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Mode plein écran immersif pour le joueur final
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cacherBarresSysteme();

        // 2. Initialisation des fondations (identique à EcranDemarrage)
        NoeudBase.contexteApplication = this;
        Traducteur.initialiser(this, "fr");
        RegistreNoeuds.initialiser();

        // Le dossier où le jeu sera extrait sur le téléphone du joueur
        File dossierJeu = new File(getFilesDir(), "donnees_jeu_exporte");
        cheminProjet = dossierJeu.getAbsolutePath();

        // 3. Extraction (Se produit uniquement au tout premier lancement du jeu)
        if (!new File(dossierJeu, "projet_sauvegarde.json").exists()) {
            if (!extraireAssetsZIP(dossierJeu)) {
                Toast.makeText(this, "Erreur critique : Fichiers du jeu introuvables.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 4. Chargement des données (Calqué sur ta méthode basculerVersJeu)
        chargerDonneesJeu();

        // 5. Lancement de la VueJeu
        if (sceneActive != null) {
            Blueprint blueprintActif = chargerBlueprint(sceneActive.id);
            
            // Instanciation stricte sans HUD par défaut pour la coquille vide
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
            // Lecture des scènes avec Gson
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

            // Lecture des variables globales
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
            // Ce fichier sera injecté dans l'APK par l'Exportateur à l'Étape 3
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
            return false; // Échoue silencieusement si lancé depuis l'éditeur (car le fichier n'y est pas)
        }
    }

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
        // Désactive le bouton retour pour éviter de quitter le jeu par erreur en pleine partie
    }
}
// bas 2
