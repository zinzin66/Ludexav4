// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public class EcranDemarrage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialisation du contexte
        NoeudBase.contexteApplication = this;

        // TEST DE VÉRIFICATION : Si tu vois ce message au lancement, 
        // le système de Toast est bien fonctionnel sur ta tablette.
        Toast.makeText(this, "Test affichage OK : Système Toast actif", Toast.LENGTH_LONG).show();

        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.HORIZONTAL);
        layoutPrincipal.setPadding(40, 40, 40, 40);
        // Utilisation de ta classe Palette pour le fond
        layoutPrincipal.setBackgroundColor(Palette.fondPanneaux);

        LinearLayout colonneGauche = new LinearLayout(this);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        colonneGauche.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams paramsGauche = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        colonneGauche.setLayoutParams(paramsGauche);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_ludexa);
        colonneGauche.addView(logo);

        TextView texteBienvenue = new TextView(this);
        texteBienvenue.setText("Bienvenue dans LUDEXA — créez vos jeux sans coder.");
        texteBienvenue.setTextSize(16f);
        texteBienvenue.setGravity(Gravity.CENTER);
        texteBienvenue.setPadding(0, 20, 0, 20);
        // Utilisation de ta classe Palette pour le texte
        texteBienvenue.setTextColor(Palette.texteNormal);
        colonneGauche.addView(texteBienvenue);

        Button boutonLangue = new Button(this);
        boutonLangue.setText("Langue : Français");
        boutonLangue.setOnClickListener(v -> {
            // À implémenter : sélection de la langue
        });
        colonneGauche.addView(boutonLangue);

        LinearLayout colonneDroite = new LinearLayout(this);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paramsDroite = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        colonneDroite.setLayoutParams(paramsDroite);

        Button boutonCreerProjet = new Button(this);
        boutonCreerProjet.setText("Créer un projet");
        boutonCreerProjet.setOnClickListener(v -> {
            afficherDialogueCreationProjet();
        });
        colonneDroite.addView(boutonCreerProjet);

        Button boutonOuvrirProjet = new Button(this);
        boutonOuvrirProjet.setText("Ouvrir un projet téléchargé");
        boutonOuvrirProjet.setOnClickListener(v -> {
            // À implémenter : sélecteur de fichiers Android
        });
        colonneDroite.addView(boutonOuvrirProjet);

        TextView titreListe = new TextView(this);
        titreListe.setText("Projets existants :");
        titreListe.setTextSize(18f);
        titreListe.setPadding(0, 30, 0, 10);
        // Utilisation de ta classe Palette pour le titre
        titreListe.setTextColor(Palette.texteNormal);
        colonneDroite.addView(titreListe);

        ListView listeProjets = new ListView(this);
        LinearLayout.LayoutParams paramsListe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        listeProjets.setLayoutParams(paramsListe);
        colonneDroite.addView(listeProjets);

        // Charger les projets existants dans la liste
        chargerListeProjets(listeProjets);

        layoutPrincipal.addView(colonneGauche);
        layoutPrincipal.addView(colonneDroite);

        setContentView(layoutPrincipal);
    }

    private void afficherDialogueCreationProjet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nouveau projet");
        
        final EditText input = new EditText(this);
        input.setHint("Nom du projet");
        builder.setView(input);

        builder.setPositiveButton("Créer", (dialog, which) -> {
            String nomProjet = input.getText().toString().trim();
            if (!nomProjet.isEmpty()) {
                creerNouveauProjet(nomProjet);
            } else {
                Toast.makeText(this, "Le nom du projet ne peut pas être vide", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void creerNouveauProjet(String nomProjet) {
        String uuid = UUID.randomUUID().toString();
        File dossierProjets = new File(getFilesDir(), "projets");
        File dossierNouveauProjet = new File(dossierProjets, uuid);
        
        // Création de l'arborescence
        dossierNouveauProjet.mkdirs();
        new File(dossierNouveauProjet, "logique").mkdirs();
        File dossierAssets = new File(dossierNouveauProjet, "assets_ludexa");
        new File(dossierAssets, "Images").mkdirs();
        new File(dossierAssets, "Sons").mkdirs();

        String dateActuelle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        try {
            // Création de meta.json
            File metaFile = new File(dossierNouveauProjet, "meta.json");
            JSONObject metaJson = new JSONObject();
            metaJson.put("nom", nomProjet);
            metaJson.put("dateCreation", dateActuelle);
            metaJson.put("dateModif", dateActuelle);
            FileWriter fwMeta = new FileWriter(metaFile);
            fwMeta.write(metaJson.toString(4));
            fwMeta.close();

            // Création de projet_sauvegarde.json
            File sauvegardeFile = new File(dossierNouveauProjet, "projet_sauvegarde.json");
            FileWriter fwSauvegarde = new FileWriter(sauvegardeFile);
            fwSauvegarde.write("{ \"scenes\": [] }");
            fwSauvegarde.close();

            // Création de logique/blueprint.json
            File blueprintFile = new File(new File(dossierNouveauProjet, "logique"), "blueprint.json");
            FileWriter fwBlueprint = new FileWriter(blueprintFile);
            fwBlueprint.write("{}");
            fwBlueprint.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création des fichiers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lancement de l'éditeur
        Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
        intent.putExtra("cheminProjet", dossierNouveauProjet.getAbsolutePath());
        startActivity(intent);
    }

    private void chargerListeProjets(ListView listeProjets) {
        File dossierProjets = new File(getFilesDir(), "projets");
        ArrayList<String> affichageList = new ArrayList<>();
        final ArrayList<File> dossiersList = new ArrayList<>();

        if (dossierProjets.exists() && dossierProjets.isDirectory()) {
            File[] sousDossiers = dossierProjets.listFiles();
            if (sousDossiers != null) {
                for (File sousDossier : sousDossiers) {
                    if (sousDossier.isDirectory()) {
                        File metaFile = new File(sousDossier, "meta.json");
                        if (metaFile.exists()) {
                            try {
                                StringBuilder sb = new StringBuilder();
                                Scanner scanner = new Scanner(metaFile);
                                while (scanner.hasNextLine()) {
                                    sb.append(scanner.nextLine());
                                }
                                scanner.close();

                                JSONObject metaJson = new JSONObject(sb.toString());
                                String nom = metaJson.optString("nom", "Projet Sans Nom");
                                String dateModif = metaJson.optString("dateModif", "Date inconnue");

                                affichageList.add(nom + " (Modifié le : " + dateModif + ")");
                                dossiersList.add(sousDossier);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, affichageList);
        listeProjets.setAdapter(adapter);

        listeProjets.setOnItemClickListener((parent, view, position, id) -> {
            File dossierChoisi = dossiersList.get(position);
            Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
            intent.putExtra("cheminProjet", dossierChoisi.getAbsolutePath());
            startActivity(intent);
        });
    }
}
// bas 1
