// haut 1 07
package com.ludexa.moteur;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Écran de démarrage YOP.2D — mise en page façon Godot :
 *  - colonne gauche : identité (logo, baseline, langue)
 *  - colonne droite : bandeau d'outils compact en haut, liste des projets au centre,
 *                     barre d'actions contextuelles en bas (Éditer / Renommer /
 *                     Dupliquer / Exporter / Supprimer).
 */
public class EcranDemarrage extends Activity {

    private ListView listeProjets;
    private AdaptateurProjets adaptateurProjets;
    private final ArrayList<File> dossiersProjets = new ArrayList<>();
    private int positionSelectionnee = -1;

    private LinearLayout barreActions;
    private TextView etiquetteSelection;

    // ---------------------------------------------------------------- outils UI

    /** Conversion dp -> pixels : indispensable pour rester net sur toutes les tablettes. */
    private int dp(float valeur) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valeur, getResources().getDisplayMetrics()));
    }

    /** Fond arrondi réutilisable (panneaux, boutons, champs). */
    private GradientDrawable fond(int couleur, int rayonDp, int couleurBordure, int epaisseurDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(couleur);
        g.setCornerRadius(dp(rayonDp));
        if (epaisseurDp > 0) {
            g.setStroke(dp(epaisseurDp), couleurBordure);
        }
        return g;
    }

    // --- Couleurs dérivées directement de Palette.java (aucune valeur en dur) ---
    private int couleurSurface() {
        return Palette.canvasFond;      // panneau de droite
    }

    private int couleurFondListe() {
        return Palette.fondListe;       // zone de liste des projets
    }

    private int couleurBordure() {
        return Palette.bordure;
    }

    private int couleurSelection() {
        return Palette.boutonSurvol;    // ligne sélectionnée
    }

    private int couleurTexteSecondaire() {
        return Palette.texteSelectionne;
    }

    /** Petit bouton icône du bandeau (style Godot : compact, carré, discret). */
    private ImageButton boutonBandeau(int idIcone, String description, View.OnClickListener action) {
        ImageButton b = new ImageButton(this);
        b.setImageResource(idIcone);
        b.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        b.setBackground(fond(Palette.boutonNormal, 6, couleurBordure(), 1));
        b.setPadding(dp(6), dp(6), dp(6), dp(6));
        Palette.appliquerCouleurIcone(b, Palette.iconeNormal);
        b.setContentDescription(description);
        b.setOnClickListener(action);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(38), dp(38));
        lp.setMargins(0, 0, dp(6), 0);
        b.setLayoutParams(lp);
        return b;
    }

    /** Bouton texte compact de la barre d'actions du bas. */
    private TextView boutonAction(String libelle, boolean destructif, View.OnClickListener action) {
        TextView t = new TextView(this);
        t.setText(libelle);
        t.setTextSize(13f);
        t.setAllCaps(false);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(12), dp(9), dp(12), dp(9));
        t.setTextColor(destructif ? Color.parseColor("#FF6B6B") : Palette.texteNormal);
        t.setBackground(fond(Palette.boutonNormal, 6, couleurBordure(), 1));
        t.setClickable(true);
        t.setOnClickListener(action);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(0, 0, dp(6), 0);
        t.setLayoutParams(lp);
        return t;
    }

    private TextView separateurVertical() {
        TextView s = new TextView(this);
        s.setBackgroundColor(couleurBordure());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1), dp(26));
        lp.setMargins(dp(4), 0, dp(10), 0);
        lp.gravity = Gravity.CENTER_VERTICAL;
        s.setLayoutParams(lp);
        return s;
    }

    // ---------------------------------------------------------------- cycle de vie

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NoeudBase.contexteApplication = this;

        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.HORIZONTAL);
        layoutPrincipal.setPadding(dp(16), dp(16), dp(16), dp(16));
        layoutPrincipal.setBackgroundColor(Palette.fondNormal);

        layoutPrincipal.addView(construireColonneGauche());
        layoutPrincipal.addView(construireColonneDroite());

        setContentView(layoutPrincipal);

        chargerListeProjets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerListeProjets();
    }

    // ---------------------------------------------------------------- colonne gauche

    private View construireColonneGauche() {
        LinearLayout colonneGauche = new LinearLayout(this);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        colonneGauche.setGravity(Gravity.CENTER);
        colonneGauche.setPadding(dp(24), dp(24), dp(24), dp(24));
        colonneGauche.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 0.9f));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_ludexa);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams pLogo = new LinearLayout.LayoutParams(dp(140), dp(140));
        pLogo.gravity = Gravity.CENTER;
        colonneGauche.addView(logo, pLogo);

        TextView titre = new TextView(this);
        titre.setText("YOP.2D");
        titre.setTextSize(28f);
        titre.setGravity(Gravity.CENTER);
        titre.setLetterSpacing(0.12f);
        titre.setPadding(0, dp(14), 0, 0);
        titre.setTextColor(Palette.texteNormal);
        colonneGauche.addView(titre);

        TextView baseline = new TextView(this);
        baseline.setText("Moteur de jeu 2D — créez sans coder.");
        baseline.setTextSize(13f);
        baseline.setGravity(Gravity.CENTER);
        baseline.setPadding(0, dp(6), 0, dp(20));
        baseline.setTextColor(couleurTexteSecondaire());
        colonneGauche.addView(baseline);

        LinearLayout rangeeLangue = new LinearLayout(this);
        rangeeLangue.setOrientation(LinearLayout.HORIZONTAL);
        rangeeLangue.setGravity(Gravity.CENTER);
        rangeeLangue.addView(boutonBandeau(R.drawable.language_24px, "Langue : Français", v ->
                Toast.makeText(this, "Langue : Français", Toast.LENGTH_SHORT).show()));

        TextView libelleLangue = new TextView(this);
        libelleLangue.setText("Français");
        libelleLangue.setTextSize(13f);
        libelleLangue.setTextColor(couleurTexteSecondaire());
        libelleLangue.setGravity(Gravity.CENTER_VERTICAL);
        rangeeLangue.addView(libelleLangue);

        colonneGauche.addView(rangeeLangue);

        return colonneGauche;
    }

    // ---------------------------------------------------------------- colonne droite

    private View construireColonneDroite() {
        LinearLayout colonneDroite = new LinearLayout(this);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setBackground(fond(couleurSurface(), 10, couleurBordure(), 1));
        colonneDroite.setPadding(dp(10), dp(8), dp(10), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1.4f);
        lp.setMargins(dp(10), 0, 0, 0);
        colonneDroite.setLayoutParams(lp);

        colonneDroite.addView(construireBandeauOutils());
        colonneDroite.addView(construireEnteteListe());
        colonneDroite.addView(construireListe());
        colonneDroite.addView(construireBarreActions());

        return colonneDroite;
    }

    /** Bandeau supérieur type Godot : petits boutons icônes alignés à gauche. */
    private View construireBandeauOutils() {
        LinearLayout bandeau = new LinearLayout(this);
        bandeau.setOrientation(LinearLayout.HORIZONTAL);
        bandeau.setGravity(Gravity.CENTER_VERTICAL);
        bandeau.setPadding(dp(6), dp(6), dp(6), dp(6));
        bandeau.setBackground(fond(Palette.fondPanneaux, 8, couleurBordure(), 1));

        bandeau.addView(boutonBandeau(R.drawable.add_24px, "Créer un projet",
                v -> afficherDialogueCreationProjet()));
        bandeau.addView(boutonBandeau(R.drawable.folder_open_24px, "Ouvrir un projet téléchargé",
                v -> Toast.makeText(this, "Import de projet : à venir", Toast.LENGTH_SHORT).show()));

        bandeau.addView(separateurVertical());

        bandeau.addView(boutonBandeau(R.drawable.bug_report_24px, "Diagnostic du dossier projets",
                v -> afficherDiagnosticDossier()));
        bandeau.addView(boutonBandeau(R.drawable.build_24px, "Test mkdirs",
                v -> afficherTestMkdirs()));

        // Espace élastique puis compteur de projets, aligné à droite.
        View espace = new View(this);
        espace.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1f));
        bandeau.addView(espace);

        etiquetteSelection = new TextView(this);
        etiquetteSelection.setTextSize(12f);
        etiquetteSelection.setTextColor(couleurTexteSecondaire());
        etiquetteSelection.setPadding(dp(8), 0, dp(4), 0);
        bandeau.addView(etiquetteSelection);

        return bandeau;
    }

    private View construireEnteteListe() {
        TextView titreListe = new TextView(this);
        titreListe.setText("PROJETS");
        titreListe.setTextSize(11f);
        titreListe.setLetterSpacing(0.18f);
        titreListe.setPadding(dp(4), dp(14), 0, dp(6));
        titreListe.setTextColor(couleurTexteSecondaire());
        return titreListe;
    }

    private View construireListe() {
        FrameLayout conteneur = new FrameLayout(this);
        conteneur.setBackground(fond(couleurFondListe(), 8, couleurBordure(), 1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        conteneur.setLayoutParams(lp);

        listeProjets = new ListView(this);
        listeProjets.setDivider(null);
        listeProjets.setDividerHeight(0);
        listeProjets.setPadding(dp(6), dp(6), dp(6), dp(6));
        listeProjets.setClipToPadding(false);
        listeProjets.setSelector(new GradientDrawable());
        conteneur.addView(listeProjets, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        return conteneur;
    }

    /** Barre d'actions contextuelles, en bas de la colonne des projets. */
    private View construireBarreActions() {
        barreActions = new LinearLayout(this);
        barreActions.setOrientation(LinearLayout.HORIZONTAL);
        barreActions.setPadding(dp(6), dp(8), dp(0), dp(2));

        barreActions.addView(boutonAction("Éditer", false, v -> actionEditer()));
        barreActions.addView(boutonAction("Renommer", false, v -> actionRenommer()));
        barreActions.addView(boutonAction("Dupliquer", false, v -> actionDupliquer()));
        barreActions.addView(boutonAction("Exporter", false, v -> actionExporter()));
        barreActions.addView(boutonAction("Supprimer", true, v -> actionSupprimer()));

        majEtatActions();
        return barreActions;
    }

    private void majEtatActions() {
        boolean actif = positionSelectionnee >= 0 && positionSelectionnee < dossiersProjets.size();
        if (barreActions != null) {
            for (int i = 0; i < barreActions.getChildCount(); i++) {
                View enfant = barreActions.getChildAt(i);
                enfant.setEnabled(actif);
                enfant.setAlpha(actif ? 1f : 0.4f);
            }
        }
        if (etiquetteSelection != null) {
            int total = dossiersProjets.size();
            etiquetteSelection.setText(actif
                    ? "1 projet sélectionné · " + total + " au total"
                    : total + " projet(s)");
        }
    }
// bas 1
            
// haut 2

    // ---------------------------------------------------------------- adaptateur liste

    private class AdaptateurProjets extends ArrayAdapter<String> {
        private final ArrayList<String> sousTitres;

        AdaptateurProjets(ArrayList<String> noms, ArrayList<String> sousTitres) {
            super(EcranDemarrage.this, 0, noms);
            this.sousTitres = sousTitres;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ligne;
            TextView titre;
            TextView sousTitre;

            if (convertView == null) {
                ligne = new LinearLayout(EcranDemarrage.this);
                ligne.setOrientation(LinearLayout.VERTICAL);
                ligne.setPadding(dp(12), dp(10), dp(12), dp(10));

                titre = new TextView(EcranDemarrage.this);
                titre.setTextSize(15f);
                titre.setTag("titre");
                ligne.addView(titre);

                sousTitre = new TextView(EcranDemarrage.this);
                sousTitre.setTextSize(11f);
                sousTitre.setPadding(0, dp(2), 0, 0);
                sousTitre.setTag("sousTitre");
                ligne.addView(sousTitre);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, dp(4));
                ligne.setLayoutParams(lp);
            } else {
                ligne = (LinearLayout) convertView;
                titre = (TextView) ligne.findViewWithTag("titre");
                sousTitre = (TextView) ligne.findViewWithTag("sousTitre");
            }

            boolean choisi = position == positionSelectionnee;
            ligne.setBackground(choisi
                    ? fond(couleurSelection(), 6, Palette.bordure, 1)
                    : fond(Color.TRANSPARENT, 6, Color.TRANSPARENT, 0));

            titre.setText(getItem(position));
            titre.setTextColor(choisi ? Palette.texteSelectionne : Palette.texteNormal);
            sousTitre.setText(sousTitres.get(position));
            sousTitre.setTextColor(couleurTexteSecondaire());

            return ligne;
        }
    }

    // ---------------------------------------------------------------- données

    private void chargerListeProjets() {
        if (listeProjets == null) return;

        File dossierRacine = new File(getFilesDir(), "projets");
        ArrayList<String> noms = new ArrayList<>();
        ArrayList<String> sousTitres = new ArrayList<>();
        dossiersProjets.clear();

        if (dossierRacine.exists() && dossierRacine.isDirectory()) {
            File[] sousDossiers = dossierRacine.listFiles();
            if (sousDossiers != null) {
                for (File sousDossier : sousDossiers) {
                    if (!sousDossier.isDirectory()) continue;
                    File metaFile = new File(sousDossier, "meta.json");
                    if (!metaFile.exists()) continue;
                    try {
                        JSONObject metaJson = lireJson(metaFile);
                        noms.add(metaJson.optString("nom", "Projet Sans Nom"));
                        sousTitres.add("Modifié le " + metaJson.optString("dateModif", "date inconnue"));
                        dossiersProjets.add(sousDossier);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (positionSelectionnee >= noms.size()) {
            positionSelectionnee = -1;
        }

        adaptateurProjets = new AdaptateurProjets(noms, sousTitres);
        listeProjets.setAdapter(adaptateurProjets);

        listeProjets.setOnItemClickListener((parent, view, position, id) -> {
            positionSelectionnee = position;
            adaptateurProjets.notifyDataSetChanged();
            majEtatActions();
        });

        listeProjets.setOnItemLongClickListener((parent, view, position, id) -> {
            positionSelectionnee = position;
            adaptateurProjets.notifyDataSetChanged();
            majEtatActions();
            actionEditer();
            return true;
        });

        majEtatActions();
    }

    private JSONObject lireJson(File fichier) throws Exception {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(fichier);
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        scanner.close();
        return new JSONObject(sb.toString());
    }

    private File dossierSelectionne() {
        if (positionSelectionnee < 0 || positionSelectionnee >= dossiersProjets.size()) {
            Toast.makeText(this, "Sélectionnez d'abord un projet", Toast.LENGTH_SHORT).show();
            return null;
        }
        return dossiersProjets.get(positionSelectionnee);
    }

    private String nomProjet(File dossier) {
        try {
            return lireJson(new File(dossier, "meta.json")).optString("nom", "Projet Sans Nom");
        } catch (Exception e) {
            return "Projet Sans Nom";
        }
    }

    private String dateMaintenant() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private ArrayList<String> listeNomsProjetsExistants() {
        ArrayList<String> noms = new ArrayList<>();
        File dossierProjets = new File(getFilesDir(), "projets");
        File[] sousDossiers = dossierProjets.listFiles();
        if (sousDossiers != null) {
            for (File sousDossier : sousDossiers) {
                File metaFile = new File(sousDossier, "meta.json");
                if (sousDossier.isDirectory() && metaFile.exists()) {
                    try {
                        noms.add(lireJson(metaFile).optString("nom", "Projet Sans Nom"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return noms;
    }

    private boolean nomDejaUtilise(String nom, String nomIgnore) {
        for (String existant : listeNomsProjetsExistants()) {
            if (existant.equalsIgnoreCase(nom) && !existant.equalsIgnoreCase(nomIgnore)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- actions

    private void actionEditer() {
        File dossier = dossierSelectionne();
        if (dossier == null) return;
        Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
        intent.putExtra("cheminProjet", dossier.getAbsolutePath());
        startActivity(intent);
    }

    private void actionRenommer() {
        File dossier = dossierSelectionne();
        if (dossier == null) return;
        final File metaFile = new File(dossier, "meta.json");
        final String nomActuel = nomProjet(dossier);

        final EditText champ = new EditText(this);
        champ.setText(nomActuel);
        champ.setSelectAllOnFocus(true);

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setTitle("Renommer le projet")
                .setView(champ)
                .setPositiveButton("Valider", null)
                .setNegativeButton("Annuler", (d, w) -> d.cancel())
                .create();
        dialogue.show();

        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nouveauNom = champ.getText().toString().trim();
            if (nouveauNom.isEmpty()) {
                Toast.makeText(this, "Le nom du projet ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nomDejaUtilise(nouveauNom, nomActuel)) {
                Toast.makeText(this, "Ce nom de projet est déjà utilisé.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject metaJson = lireJson(metaFile);
                metaJson.put("nom", nouveauNom);
                metaJson.put("dateModif", dateMaintenant());
                FileWriter fw = new FileWriter(metaFile);
                fw.write(metaJson.toString(4));
                fw.close();
                chargerListeProjets();
                dialogue.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du renommage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actionDupliquer() {
        File source = dossierSelectionne();
        if (source == null) return;

        String base = nomProjet(source) + " (copie)";
        String nomFinal = base;
        int index = 2;
        while (nomDejaUtilise(nomFinal, null)) {
            nomFinal = base + " " + index++;
        }

        File cible = new File(new File(getFilesDir(), "projets"), UUID.randomUUID().toString());
        try {
            copierRecursif(source, cible);
            File metaFile = new File(cible, "meta.json");
            JSONObject metaJson = lireJson(metaFile);
            metaJson.put("nom", nomFinal);
            metaJson.put("dateCreation", dateMaintenant());
            metaJson.put("dateModif", dateMaintenant());
            FileWriter fw = new FileWriter(metaFile);
            fw.write(metaJson.toString(4));
            fw.close();
            chargerListeProjets();
            Toast.makeText(this, "Projet dupliqué : " + nomFinal, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            supprimerDossierRecursif(cible);
            Toast.makeText(this, "Erreur lors de la duplication", Toast.LENGTH_SHORT).show();
        }
    }

    private void actionExporter() {
        File source = dossierSelectionne();
        if (source == null) return;

        String nom = nomProjet(source).replaceAll("[^a-zA-Z0-9-_ ]", "_").trim();
        File dossierExports = new File(getExternalFilesDir(null), "exports");
        dossierExports.mkdirs();
        File archive = new File(dossierExports, nom + ".zip");

        try {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archive));
            zipperRecursif(source, "", zip);
            zip.close();

            new AlertDialog.Builder(this)
                    .setTitle("Export terminé")
                    .setMessage("Archive créée :\n" + archive.getAbsolutePath())
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de l'export", Toast.LENGTH_SHORT).show();
        }
    }

        private void actionSupprimer() {
        File dossier = dossierSelectionne();
        if (dossier == null) return;
        String nom = nomProjet(dossier);

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setTitle("Supprimer le projet")
                .setMessage("Voulez-vous vraiment supprimer le projet " + nom + " ?")
                .setPositiveButton("Supprimer", (d, w) -> {
                    supprimerDossierRecursif(dossier);
                    positionSelectionnee = -1;
                    chargerListeProjets();
                })
                .setNegativeButton("Annuler", (d, w) -> d.cancel())
                .create();
        dialogue.show();
        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }


    // ---------------------------------------------------------------- création projet

    private void afficherDialogueCreationProjet() {
        final EditText champ = new EditText(this);
        champ.setHint("Nom du projet");

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setTitle("Nouveau projet")
                .setView(champ)
                .setPositiveButton("Créer", null)
                .setNegativeButton("Annuler", (d, w) -> d.cancel())
                .create();
        dialogue.show();

        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nomProjet = champ.getText().toString().trim();
            if (nomProjet.isEmpty()) {
                Toast.makeText(this, "Le nom du projet ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nomDejaUtilise(nomProjet, null)) {
                Toast.makeText(this, "Ce nom de projet est déjà utilisé.", Toast.LENGTH_SHORT).show();
                return;
            }
            creerNouveauProjet(nomProjet);
            dialogue.dismiss();
        });
    }

    private void creerNouveauProjet(String nomProjet) {
        String uuid = UUID.randomUUID().toString();
        File dossierNouveauProjet = new File(new File(getFilesDir(), "projets"), uuid);

        dossierNouveauProjet.mkdirs();
        new File(dossierNouveauProjet, "logique").mkdirs();
        File dossierAssets = new File(dossierNouveauProjet, "assets_ludexa");
        new File(dossierAssets, "Images").mkdirs();
        new File(dossierAssets, "Sons").mkdirs();

        String dateActuelle = dateMaintenant();

        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("nom", nomProjet);
            metaJson.put("dateCreation", dateActuelle);
            metaJson.put("dateModif", dateActuelle);
            FileWriter fwMeta = new FileWriter(new File(dossierNouveauProjet, "meta.json"));
            fwMeta.write(metaJson.toString(4));
            fwMeta.close();

            FileWriter fwSauvegarde = new FileWriter(new File(dossierNouveauProjet, "projet_sauvegarde.json"));
            fwSauvegarde.write("{ \"scenes\": [] }");
            fwSauvegarde.close();

            FileWriter fwBlueprint = new FileWriter(new File(new File(dossierNouveauProjet, "logique"), "blueprint.json"));
            fwBlueprint.write("{}");
            fwBlueprint.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création des fichiers", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
        intent.putExtra("cheminProjet", dossierNouveauProjet.getAbsolutePath());
        startActivity(intent);
    }

    // ---------------------------------------------------------------- fichiers

    private void supprimerDossierRecursif(File dossier) {
        if (dossier == null || !dossier.exists()) return;
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

    private void copierRecursif(File source, File cible) throws Exception {
        if (source.isDirectory()) {
            cible.mkdirs();
            File[] enfants = source.listFiles();
            if (enfants != null) {
                for (File enfant : enfants) {
                    copierRecursif(enfant, new File(cible, enfant.getName()));
                }
            }
        } else {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(cible);
            byte[] tampon = new byte[8192];
            int lus;
            while ((lus = in.read(tampon)) > 0) {
                out.write(tampon, 0, lus);
            }
            in.close();
            out.close();
        }
    }

    private void zipperRecursif(File source, String chemin, ZipOutputStream zip) throws Exception {
        if (source.isDirectory()) {
            File[] enfants = source.listFiles();
            if (enfants != null) {
                for (File enfant : enfants) {
                    zipperRecursif(enfant, chemin.isEmpty() ? enfant.getName() : chemin + "/" + enfant.getName(), zip);
                }
            }
        } else {
            zip.putNextEntry(new ZipEntry(chemin));
            InputStream in = new FileInputStream(source);
            byte[] tampon = new byte[8192];
            int lus;
            while ((lus = in.read(tampon)) > 0) {
                zip.write(tampon, 0, lus);
            }
            in.close();
            zip.closeEntry();
        }
    }

    // ---------------------------------------------------------------- debug

    private void afficherDiagnosticDossier() {
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

        new AlertDialog.Builder(this)
                .setTitle("Debug : Dossier projets")
                .setMessage(info.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void afficherTestMkdirs() {
        File dossierProjets = new File(getFilesDir(), "projets");
        boolean resultat = dossierProjets.mkdirs();

        StringBuilder info = new StringBuilder();
        info.append("Chemin absolu : ").append(dossierProjets.getAbsolutePath()).append("\n");
        info.append("Valeur de resultat (mkdirs) : ").append(resultat).append("\n");
        info.append("exists() : ").append(dossierProjets.exists()).append("\n");
        info.append("canWrite() : ").append(dossierProjets.canWrite()).append("\n");
        info.append("getFilesDir().canWrite() (parent) : ").append(getFilesDir().canWrite()).append("\n");

        new AlertDialog.Builder(this)
                .setTitle("Debug : Test mkdirs")
                .setMessage(info.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}
// bas 2


