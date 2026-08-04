// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    // Champ d'instance pour accéder à la liste dans onResume()
    private ListView listeProjets;

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

// 1. Définir une taille personnalisée (ex: 300 pixels de large et de haut)
LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    300, // Largeur
    300  // Hauteur
);

// 2. Centrer le logo si nécessaire
params.gravity = Gravity.CENTER;

// 3. Appliquer les paramètres à l'image
logo.setLayoutParams(params);

// 4. Demander à l'image de conserver ses proportions pour ne pas être écrasée
logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

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

        // --- DÉBUT AJOUT BOUTON DEBUG ---
        Button boutonDebug = new Button(this);
        boutonDebug.setText("DEBUG Vérifier dossier projets");
        boutonDebug.setOnClickListener(v -> {
            File dossierProjets = new File(getFilesDir(), "projets");
            StringBuilder info = new StringBuilder();
            
            info.append("Chemin absolu : ").append(dossierProjets.getAbsolutePath()).append("\n");
            info.append("Existe : ").append(dossierProjets.exists()).append("\n");
            info.append("Est un dossier : ").append(dossierProjets.isDirectory()).append("\n\n");

            File[] sousDossiers = dossierProjets.listFiles();
            if (sousDossiers == null) {
                info.append("listFiles() a retourné null\n");
            } else if (sousDossiers.length == 0) {
                info.append("Aucun sous-dossier trouvé\n");
            } else {
                for (File sousDossier : sousDossiers) {
                    info.append("Dossier : ").append(sousDossier.getName()).append("\n");
                    File metaFile = new File(sousDossier, "meta.json");
                    boolean metaExiste = metaFile.exists();
                    info.append("  -> meta.json existe : ").append(metaExiste);
                    if (metaExiste) {
                        info.append(" (Taille : ").append(metaFile.length()).append(" octets)");
                    }
                    info.append("\n");
                }
            }

            new AlertDialog.Builder(EcranDemarrage.this)
                    .setTitle("Debug : Dossier projets")
                    .setMessage(info.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });
        colonneDroite.addView(boutonDebug);
        // --- FIN AJOUT BOUTON DEBUG ---

        // --- DÉBUT AJOUT BOUTON DEBUG MKDIRS ---
        Button boutonDebugMkdirs = new Button(this);
        boutonDebugMkdirs.setText("DEBUG Test mkdirs");
        boutonDebugMkdirs.setOnClickListener(v -> {
            File dossierProjets = new File(getFilesDir(), "projets");
            boolean resultat = dossierProjets.mkdirs();
            
            StringBuilder info = new StringBuilder();
            info.append("Chemin absolu : ").append(dossierProjets.getAbsolutePath()).append("\n");
            info.append("Valeur de resultat (mkdirs) : ").append(resultat).append("\n");
            info.append("exists() : ").append(dossierProjets.exists()).append("\n");
            info.append("canWrite() : ").append(dossierProjets.canWrite()).append("\n");
            info.append("getFilesDir().canWrite() (parent) : ").append(getFilesDir().canWrite()).append("\n");

            new AlertDialog.Builder(EcranDemarrage.this)
                    .setTitle("Debug : Test mkdirs")
                    .setMessage(info.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });
        colonneDroite.addView(boutonDebugMkdirs);
        // --- FIN AJOUT BOUTON DEBUG MKDIRS ---

        TextView titreListe = new TextView(this);
        titreListe.setText("Projets existants :");
        titreListe.setTextSize(18f);
        titreListe.setPadding(0, 30, 0, 10);
        // Utilisation de ta classe Palette pour le titre
        titreListe.setTextColor(Palette.texteNormal);
        colonneDroite.addView(titreListe);

        // Initialisation de la variable d'instance (plus de redéclaration locale)
        listeProjets = new ListView(this);
        LinearLayout.LayoutParams paramsListe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        listeProjets.setLayoutParams(paramsListe);
        colonneDroite.addView(listeProjets);

        // Charger les projets existants dans la liste au premier lancement
        chargerListeProjets(listeProjets);

        layoutPrincipal.addView(colonneGauche);
        layoutPrincipal.addView(colonneDroite);

        setContentView(layoutPrincipal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharge la liste à chaque fois que l'utilisateur revient sur cet écran
        if (listeProjets != null) {
            chargerListeProjets(listeProjets);
        }
    }

    private ArrayList<String> listeNomsProjetsExistants() {
        ArrayList<String> noms = new ArrayList<>();
        File dossierProjets = new File(getFilesDir(), "projets");
        
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
                                noms.add(metaJson.optString("nom", "Projet Sans Nom"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return noms;
    }

    private void supprimerDossierRecursif(File dossier) {
        if (dossier.isDirectory()) {
            File[] enfants = dossier.listFiles();
            if (enfants != null) {
                for (File enfant : enfants) {
                    supprimerDossierRecursif(enfant);
                }
            }
        }
        dossier.delete();
    }

    private void afficherDialogueCreationProjet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nouveau projet");
        
        final EditText input = new EditText(this);
        input.setHint("Nom du projet");
        builder.setView(input);

        // On assigne 'null' au listener initial pour empêcher la fermeture automatique
        builder.setPositiveButton("Créer", null);
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        
        AlertDialog dialog = builder.create();
        dialog.show();

        // On redéfinit le comportement du bouton Créer après l'affichage
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nomProjet = input.getText().toString().trim();
            if (nomProjet.isEmpty()) {
                Toast.makeText(this, "Le nom du projet ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> existants = listeNomsProjetsExistants();
            for (String existant : existants) {
                if (existant.equalsIgnoreCase(nomProjet)) {
                    Toast.makeText(this, "Ce nom de projet est déjà utilisé.", Toast.LENGTH_SHORT).show();
                    return; // Stoppe l'exécution, le dialogue reste ouvert
                }
            }

            creerNouveauProjet(nomProjet);
            dialog.dismiss();
        });
    }
// bas 1
    
// haut 2
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, affichageList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    textView = new TextView(getContext());
                    textView.setPadding(20, 20, 20, 20);
                    textView.setTextSize(16f);
                    textView.setTextColor(Palette.texteNormal);
                } else {
                    textView = (TextView) convertView;
                }
                textView.setText(getItem(position));
                return textView;
            }
        };
        listeProjets.setAdapter(adapter);

        // Clic court : Ouvre le projet existant
        listeProjets.setOnItemClickListener((parent, view, position, id) -> {
            File dossierChoisi = dossiersList.get(position);
            Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
            intent.putExtra("cheminProjet", dossierChoisi.getAbsolutePath());
            startActivity(intent);
        });

        // Clic long : Renommer ou Supprimer
        listeProjets.setOnItemLongClickListener((parent, view, position, id) -> {
            File dossierChoisi = dossiersList.get(position);
            File metaFile = new File(dossierChoisi, "meta.json");
            
            // Récupérer le nom actuel directement depuis le JSON
            String nomActuel = "";
            try {
                StringBuilder sb = new StringBuilder();
                Scanner scanner = new Scanner(metaFile);
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine());
                }
                scanner.close();
                JSONObject metaJson = new JSONObject(sb.toString());
                nomActuel = metaJson.optString("nom", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String nomFinal = nomActuel;
            String[] options = {"Renommer", "Supprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Action Renommer
                    AlertDialog.Builder builderRenommer = new AlertDialog.Builder(this);
                    builderRenommer.setTitle("Renommer le projet");
                    
                    final EditText input = new EditText(this);
                    input.setText(nomFinal);
                    builderRenommer.setView(input);

                    builderRenommer.setPositiveButton("Valider", null);
                    builderRenommer.setNegativeButton("Annuler", (d, w) -> d.cancel());
                    
                    AlertDialog dialogRenommer = builderRenommer.create();
                    dialogRenommer.show();

                    dialogRenommer.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                        String nouveauNom = input.getText().toString().trim();
                        if (nouveauNom.isEmpty()) {
                            Toast.makeText(this, "Le nom du projet ne peut pas être vide", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayList<String> existants = listeNomsProjetsExistants();
                        for (String existant : existants) {
                            if (existant.equalsIgnoreCase(nouveauNom) && !existant.equalsIgnoreCase(nomFinal)) {
                                Toast.makeText(this, "Ce nom de projet est déjà utilisé.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        try {
                            // Lecture du JSON existant
                            StringBuilder sb = new StringBuilder();
                            Scanner scanner = new Scanner(metaFile);
                            while (scanner.hasNextLine()) {
                                sb.append(scanner.nextLine());
                            }
                            scanner.close();
                            
                            JSONObject metaJson = new JSONObject(sb.toString());
                            metaJson.put("nom", nouveauNom);
                            String dateActuelle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                            metaJson.put("dateModif", dateActuelle);
                            
                            // Réécriture
                            FileWriter fwMeta = new FileWriter(metaFile);
                            fwMeta.write(metaJson.toString(4));
                            fwMeta.close();

                            chargerListeProjets(listeProjets);
                            dialogRenommer.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erreur lors du renommage", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (which == 1) {
                    // Action Supprimer
                    AlertDialog.Builder builderSupprimer = new AlertDialog.Builder(this);
                    builderSupprimer.setMessage("Supprimer définitivement le projet " + nomFinal + " ? Cette action est irréversible.");
                    
                    builderSupprimer.setPositiveButton("Supprimer", (d, w) -> {
                        supprimerDossierRecursif(dossierChoisi);
                        chargerListeProjets(listeProjets);
                    });
                    builderSupprimer.setNegativeButton("Annuler", (d, w) -> d.cancel());
                    
                    AlertDialog dialogSupprimer = builderSupprimer.create();
                    dialogSupprimer.show();
                    // Passage du bouton en rouge pour marquer la destruction
                    dialogSupprimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                }
            });
            builder.show();
            return true;
        });
    }
}
// bas 2
