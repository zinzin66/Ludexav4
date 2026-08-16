// haut 1
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

public class EcranDemarrage extends Activity {

    public static String langueCourante = "fr";
    private TextView libelleLangue;

    private ListView listeProjets;
    private AdaptateurProjets adaptateurProjets;
    private final ArrayList<File> dossiersProjets = new ArrayList<>();
    private int positionSelectionnee = -1;

    private LinearLayout barreActions;
    private TextView etiquetteSelection;

    // ---------------------------------------------------------------- outils UI

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

    private int couleurSurface() { return Palette.canvasFond; }
    private int couleurFondListe() { return Palette.fondListe; }
    private int couleurBordure() { return Palette.bordure; }
    private int couleurSelection() { return Palette.boutonSurvol; }
    private int couleurTexteSecondaire() { return Palette.texteSelectionne; }

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
        Traducteur.initialiser(this, langueCourante); 
        RegistreNoeuds.initialiser(); 

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
        titre.setText(Traducteur.get("app_nom"));
        titre.setTextSize(28f);
        titre.setGravity(Gravity.CENTER);
        titre.setLetterSpacing(0.12f);
        titre.setPadding(0, dp(14), 0, 0);
        titre.setTextColor(Palette.texteNormal);
        colonneGauche.addView(titre);

        TextView baseline = new TextView(this);
        baseline.setText(Traducteur.get("demarrage_baseline"));
        baseline.setTextSize(13f);
        baseline.setGravity(Gravity.CENTER);
        baseline.setPadding(0, dp(6), 0, dp(20));
        baseline.setTextColor(couleurTexteSecondaire());
        colonneGauche.addView(baseline);

        LinearLayout rangeeLangue = new LinearLayout(this);
        rangeeLangue.setOrientation(LinearLayout.HORIZONTAL);
        rangeeLangue.setGravity(Gravity.CENTER);
        
        rangeeLangue.addView(boutonBandeau(R.drawable.language_24px, Traducteur.get("demarrage_langue"), v -> afficherDialogueLangue()));

        libelleLangue = new TextView(this);
        String labelCourant = Traducteur.get("langue_" + langueCourante);
        libelleLangue.setText(labelCourant);
        libelleLangue.setTextSize(13f);
        libelleLangue.setTextColor(couleurTexteSecondaire());
        libelleLangue.setGravity(Gravity.CENTER_VERTICAL);
        rangeeLangue.addView(libelleLangue);

        colonneGauche.addView(rangeeLangue);

        return colonneGauche;
    }

    private void afficherDialogueLangue() {
        String[] langues = {Traducteur.get("langue_fr"), Traducteur.get("langue_en"), Traducteur.get("langue_ru")};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Traducteur.get("demarrage_choisir_langue"));
        builder.setItems(langues, (dialog, which) -> {
            switch (which) {
                case 0: langueCourante = "fr"; break;
                case 1: langueCourante = "en"; break;
                case 2: langueCourante = "ru"; break;
            }
            Traducteur.initialiser(this, langueCourante);
            RegistreNoeuds.initialiser(); 
            recreate(); 
        });
        builder.show();
    }
// bas 1
        


// haut 2
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

    private View construireBandeauOutils() {
        LinearLayout bandeau = new LinearLayout(this);
        bandeau.setOrientation(LinearLayout.HORIZONTAL);
        bandeau.setGravity(Gravity.CENTER_VERTICAL);
        bandeau.setPadding(dp(6), dp(6), dp(6), dp(6));
        bandeau.setBackground(fond(Palette.fondPanneaux, 8, couleurBordure(), 1));

        bandeau.addView(boutonBandeau(R.drawable.add_24px, Traducteur.get("demarrage_creer_projet"),
                v -> afficherDialogueCreationProjet()));
        bandeau.addView(boutonBandeau(R.drawable.folder_open_24px, Traducteur.get("demarrage_ouvrir_projet"),
                v -> Toast.makeText(this, Traducteur.get("toast_import_a_venir"), Toast.LENGTH_SHORT).show()));

        bandeau.addView(separateurVertical());

        bandeau.addView(boutonBandeau(R.drawable.bug_report_24px, Traducteur.get("demarrage_diagnostic"),
                v -> afficherDiagnosticDossier()));
        bandeau.addView(boutonBandeau(R.drawable.build_24px, Traducteur.get("demarrage_test_mkdirs"),
                v -> afficherTestMkdirs()));

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
        titreListe.setText(Traducteur.get("demarrage_titre_projets"));
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

    private View construireBarreActions() {
        barreActions = new LinearLayout(this);
        barreActions.setOrientation(LinearLayout.HORIZONTAL);
        barreActions.setPadding(dp(6), dp(8), dp(0), dp(2));

        barreActions.addView(boutonAction(Traducteur.get("bouton_editer"), false, v -> actionEditer()));
        barreActions.addView(boutonAction(Traducteur.get("bouton_renommer"), false, v -> actionRenommer()));
        barreActions.addView(boutonAction(Traducteur.get("bouton_dupliquer"), false, v -> actionDupliquer()));
        barreActions.addView(boutonAction(Traducteur.get("bouton_exporter"), false, v -> actionExporter()));
        barreActions.addView(boutonAction(Traducteur.get("bouton_supprimer"), true, v -> actionSupprimer()));

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
                    ? Traducteur.get("demarrage_un_projet_select") + total + Traducteur.get("demarrage_au_total")
                    : total + " " + Traducteur.get("demarrage_projets"));
        }
    }

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
                        noms.add(metaJson.optString("nom", Traducteur.get("projet_sans_nom")));
                        sousTitres.add(Traducteur.get("projet_modifie_le") + " " + metaJson.optString("dateModif", Traducteur.get("date_inconnue")));
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
// bas 2


// haut 3
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
            Toast.makeText(this, Traducteur.get("toast_select_projet"), Toast.LENGTH_SHORT).show();
            return null;
        }
        return dossiersProjets.get(positionSelectionnee);
    }

    private String nomProjet(File dossier) {
        try {
            return lireJson(new File(dossier, "meta.json")).optString("nom", Traducteur.get("projet_sans_nom"));
        } catch (Exception e) {
            return Traducteur.get("projet_sans_nom");
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
                        noms.add(lireJson(metaFile).optString("nom", Traducteur.get("projet_sans_nom")));
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
                .setTitle(Traducteur.get("dialogue_renommer_titre"))
                .setView(champ)
                .setPositiveButton(Traducteur.get("bouton_valider"), null)
                .setNegativeButton(Traducteur.get("bouton_annuler"), (d, w) -> d.cancel())
                .create();
        dialogue.show();

        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nouveauNom = champ.getText().toString().trim();
            if (nouveauNom.isEmpty()) {
                Toast.makeText(this, Traducteur.get("erreur_nom_vide"), Toast.LENGTH_SHORT).show();
                return;
            }
            if (nomDejaUtilise(nouveauNom, nomActuel)) {
                Toast.makeText(this, Traducteur.get("erreur_nom_utilise"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, Traducteur.get("erreur_renommage"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actionDupliquer() {
        File source = dossierSelectionne();
        if (source == null) return;

        String base = nomProjet(source) + " " + Traducteur.get("projet_copie");
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
            Toast.makeText(this, Traducteur.get("toast_projet_duplique") + " " + nomFinal, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            supprimerDossierRecursif(cible);
            Toast.makeText(this, Traducteur.get("erreur_duplication"), Toast.LENGTH_SHORT).show();
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
                    .setTitle(Traducteur.get("export_termine"))
                    .setMessage(Traducteur.get("archive_creee") + "\n" + archive.getAbsolutePath())
                    .setPositiveButton(Traducteur.get("bouton_ok"), null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, Traducteur.get("erreur_export"), Toast.LENGTH_SHORT).show();
        }
    }

    private void actionSupprimer() {
        File dossier = dossierSelectionne();
        if (dossier == null) return;
        String nom = nomProjet(dossier);

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setTitle(Traducteur.get("dialogue_supprimer_titre"))
                .setMessage(Traducteur.get("dialogue_supprimer_msg1") + nom + Traducteur.get("dialogue_supprimer_msg2"))
                .setPositiveButton(Traducteur.get("bouton_supprimer"), (d, w) -> {
                    supprimerDossierRecursif(dossier);
                    positionSelectionnee = -1;
                    chargerListeProjets();
                })
                .setNegativeButton(Traducteur.get("bouton_annuler"), (d, w) -> d.cancel())
                .create();
        dialogue.show();
        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    // ---------------------------------------------------------------- création projet

    private void afficherDialogueCreationProjet() {
        final EditText champ = new EditText(this);
        champ.setHint(Traducteur.get("hint_nom_projet"));

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setTitle(Traducteur.get("dialogue_nouveau_titre"))
                .setView(champ)
                .setPositiveButton(Traducteur.get("bouton_creer"), null)
                .setNegativeButton(Traducteur.get("bouton_annuler"), (d, w) -> d.cancel())
                .create();
        dialogue.show();

        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nomProjet = champ.getText().toString().trim();
            if (nomProjet.isEmpty()) {
                Toast.makeText(this, Traducteur.get("erreur_nom_vide"), Toast.LENGTH_SHORT).show();
                return;
            }
            if (nomDejaUtilise(nomProjet, null)) {
                Toast.makeText(this, Traducteur.get("erreur_nom_utilise"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, Traducteur.get("erreur_creation_fichiers"), Toast.LENGTH_SHORT).show();
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
// bas 3
                            

    


