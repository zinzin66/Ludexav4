// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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

    // Projets Locaux
    private ListView listeProjets;
    private AdaptateurProjets adaptateurProjets;
    private final ArrayList<File> dossiersProjets = new ArrayList<>();
    private final ArrayList<ItemProjet> itemsProjets = new ArrayList<>();
    private int positionSelectionnee = -1;

    // Projets d'Exemples
    private ListView listeExemples;
    private AdaptateurProjets adaptateurExemples;
    private final ArrayList<ItemProjet> itemsExemples = new ArrayList<>();

    private LinearLayout barreActions;
    private TextView etiquetteSelection;
    
    private static final int REQUEST_CODE_EXPORT_PROJET = 2001;
    private static final int REQUEST_CODE_EXPORT_APK = 2002;
    private File projetAExporter = null;
    
    // NOUVEAU : On garde en mémoire le nom final choisi par l'utilisateur
    public static String nomJeuAExporter = ""; 

    // Structure pour unifier l'affichage des deux listes
    private class ItemProjet {
        String nom;
        String sousTitre;
        File dossier;
        String nomZipExemple;
        boolean estExemple;
    }

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

        // --- NOUVEAU : LE SYSTÈME D'AIGUILLAGE (ROUTING) ---
        try {
            InputStream is = getAssets().open("jeu_exporte.zip");
            is.close();
            // Si on arrive ici, c'est un jeu exporté ! On lance le jeu et on ferme l'éditeur.
            Intent intent = new Intent(this, RunnerActivity.class);
            startActivity(intent);
            finish();
            return; 
        } catch (Exception e) {
            // Le fichier n'existe pas, c'est le moteur Yop2D normal. On continue l'initialisation !
        }
        // ---------------------------------------------------

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
        chargerListeExemples();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerListeProjets();
        chargerListeExemples();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null && projetAExporter != null) {
            
            // CAS 1 : EXPORT DU PROJET BRUT (.ZIP)
            if (requestCode == REQUEST_CODE_EXPORT_PROJET) {
                try {
                    OutputStream out = getContentResolver().openOutputStream(data.getData());
                    ZipOutputStream zip = new ZipOutputStream(out);
                    zipperRecursif(projetAExporter, "", zip);
                    zip.close();
                    if (out != null) out.close();

                    new AlertDialog.Builder(this)
                            .setTitle(Traducteur.get("export_termine"))
                            .setMessage(Traducteur.get("archive_creee"))
                            .setPositiveButton(Traducteur.get("bouton_ok"), null)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, Traducteur.get("erreur_export"), Toast.LENGTH_SHORT).show();
                }
                projetAExporter = null;
            }
            
            // CAS 2 : BUILD DE L'APK FINAL (.APK)
            else if (requestCode == REQUEST_CODE_EXPORT_APK) {
                AlertDialog dialogueProgression = new AlertDialog.Builder(this)
                        .setTitle(Traducteur.get("export_apk_titre"))
                        .setMessage(Traducteur.get("export_apk_init"))
                        .setCancelable(false)
                        .show();

                ExportateurAPK.exporterJeu(this, projetAExporter, new ExportateurAPK.InterfaceExport() {
                    @Override
                    public void surProgression(String message) {
                        dialogueProgression.setMessage(message);
                    }

                    @Override
                    public void surSucces(File apkFinal) {
                        try {
                            InputStream in = new FileInputStream(apkFinal);
                            OutputStream out = getContentResolver().openOutputStream(data.getData());
                            byte[] buffer = new byte[8192];
                            int lus;
                            while ((lus = in.read(buffer)) > 0) {
                                out.write(buffer, 0, lus);
                            }
                            in.close();
                            if (out != null) out.close();

                            dialogueProgression.dismiss();
                            projetAExporter = null;

                            new AlertDialog.Builder(EcranDemarrage.this)
                                    .setTitle(Traducteur.get("export_termine"))
                                    .setMessage(Traducteur.get("export_apk_succes"))
                                    .setPositiveButton(Traducteur.get("bouton_ok"), null)
                                    .show();

                        } catch (Exception e) {
                            surErreur(Traducteur.get("erreur_sauvegarde") + " : " + e.getMessage());
                        }
                    }

                    @Override
                    public void surErreur(String erreur) {
                        dialogueProgression.dismiss();
                        projetAExporter = null;
                        new AlertDialog.Builder(EcranDemarrage.this)
                                .setTitle(Traducteur.get("erreur_export"))
                                .setMessage(erreur)
                                .setPositiveButton(Traducteur.get("bouton_ok"), null)
                                .show();
                    }
                });
            }
        }
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

        // --- NOUVEAUX BOUTONS (Mise à jour et Réseaux) ---
        
        // 1. Bouton de mise à jour
        TextView btnMaj = new TextView(this);
        btnMaj.setText(Traducteur.get("demarrage_maj_verifier"));
        btnMaj.setTextSize(13f);
        btnMaj.setGravity(Gravity.CENTER);
        btnMaj.setPadding(dp(12), dp(9), dp(12), dp(9));
        btnMaj.setTextColor(Palette.texteNormal);
        btnMaj.setBackground(fond(Palette.boutonNormal, 6, couleurBordure(), 1));
        btnMaj.setClickable(true);
        btnMaj.setOnClickListener(v -> verifierMiseAJour());
        
        LinearLayout.LayoutParams lpMaj = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpMaj.setMargins(0, dp(30), 0, dp(10));
        colonneGauche.addView(btnMaj, lpMaj);

        // 2. Ligne pour Discord et Telegram
        LinearLayout rangeeReseaux = new LinearLayout(this);
        rangeeReseaux.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView btnDiscord = new TextView(this);
        btnDiscord.setText("Discord");
        btnDiscord.setTextSize(13f);
        btnDiscord.setGravity(Gravity.CENTER);
        btnDiscord.setPadding(dp(12), dp(9), dp(12), dp(9));
        btnDiscord.setTextColor(Palette.texteNormal);
        btnDiscord.setBackground(fond(Palette.boutonNormal, 6, couleurBordure(), 1));
        btnDiscord.setClickable(true);
        btnDiscord.setOnClickListener(v -> ouvrirLien("https://discord.gg/nHqCcqHZNQ"));
        
        LinearLayout.LayoutParams lpDiscord = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lpDiscord.setMargins(0, 0, dp(6), 0);
        rangeeReseaux.addView(btnDiscord, lpDiscord);

        TextView btnTelegram = new TextView(this);
        btnTelegram.setText("Telegram");
        btnTelegram.setTextSize(13f);
        btnTelegram.setGravity(Gravity.CENTER);
        btnTelegram.setPadding(dp(12), dp(9), dp(12), dp(9));
        btnTelegram.setTextColor(Palette.texteNormal);
        btnTelegram.setBackground(fond(Palette.boutonNormal, 6, couleurBordure(), 1));
        btnTelegram.setClickable(true);
        btnTelegram.setOnClickListener(v -> ouvrirLien("https://t.me/+7PQ9WKw7n645Y2Zk"));
        
        LinearLayout.LayoutParams lpTelegram = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        rangeeReseaux.addView(btnTelegram, lpTelegram);

        colonneGauche.addView(rangeeReseaux, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return colonneGauche;
    }

    private void afficherDialogueLangue() {
        String[] langues = {
            Traducteur.get("langue_fr"), 
            Traducteur.get("langue_en"), 
            Traducteur.get("langue_es"), 
            Traducteur.get("langue_pt"), 
            Traducteur.get("langue_ru")
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Traducteur.get("demarrage_choisir_langue"));
        builder.setItems(langues, (dialog, which) -> {
            switch (which) {
                case 0: langueCourante = "fr"; break;
                case 1: langueCourante = "en"; break;
                case 2: langueCourante = "es"; break;
                case 3: langueCourante = "pt"; break;
                case 4: langueCourante = "ru"; break;
            }
            Traducteur.initialiser(this, langueCourante);
            RegistreNoeuds.initialiser(); 
            recreate(); 
        });
        builder.show();
    }
// bas 1

// haut 2
    // ---------------------------------------------------------------- colonne droite (DIVISÉE EN DEUX)

    private View construireColonneDroite() {
        LinearLayout colonneDroite = new LinearLayout(this);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setBackground(fond(couleurSurface(), 10, couleurBordure(), 1));
        colonneDroite.setPadding(dp(10), dp(8), dp(10), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1.4f);
        lp.setMargins(dp(10), 0, 0, 0);
        colonneDroite.setLayoutParams(lp);

        // --- BLOC HAUT : MES PROJETS ---
        LinearLayout blocHaut = new LinearLayout(this);
        blocHaut.setOrientation(LinearLayout.VERTICAL);
        blocHaut.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        blocHaut.addView(construireBandeauOutils());
        blocHaut.addView(construireEnteteListe(Traducteur.get("demarrage_titre_projets")));
        blocHaut.addView(construireListeProjets());
        blocHaut.addView(construireBarreActions());

        // --- SEPARATEUR HORIZONTAL ---
        View separateurH = new View(this);
        separateurH.setBackgroundColor(couleurBordure());
        LinearLayout.LayoutParams lpH = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        lpH.setMargins(0, dp(6), 0, dp(6));
        separateurH.setLayoutParams(lpH);

        // --- BLOC BAS : PROJETS D'EXEMPLE ---
        LinearLayout blocBas = new LinearLayout(this);
        blocBas.setOrientation(LinearLayout.VERTICAL);
        blocBas.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        blocBas.addView(construireEnteteListe(Traducteur.get("demarrage_modeles_exemples")));
        blocBas.addView(construireListeExemples());

        colonneDroite.addView(blocHaut);
        colonneDroite.addView(separateurH);
        colonneDroite.addView(blocBas);

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

    private View construireEnteteListe(String titre) {
        TextView titreListe = new TextView(this);
        titreListe.setText(titre);
        titreListe.setTextSize(11f);
        titreListe.setLetterSpacing(0.18f);
        titreListe.setPadding(dp(4), dp(10), 0, dp(6));
        titreListe.setTextColor(couleurTexteSecondaire());
        return titreListe;
    }

    private View construireListeProjets() {
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

    private View construireListeExemples() {
        FrameLayout conteneur = new FrameLayout(this);
        conteneur.setBackground(fond(couleurFondListe(), 8, couleurBordure(), 1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        conteneur.setLayoutParams(lp);

        listeExemples = new ListView(this);
        listeExemples.setDivider(null);
        listeExemples.setDividerHeight(0);
        listeExemples.setPadding(dp(6), dp(6), dp(6), dp(6));
        listeExemples.setClipToPadding(false);
        listeExemples.setSelector(new GradientDrawable());
        conteneur.addView(listeExemples, new FrameLayout.LayoutParams(
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
        barreActions.addView(boutonAction(Traducteur.get("bouton_exporter"), false, v -> actionExporterProjet()));
        barreActions.addView(boutonAction(Traducteur.get("bouton_build_apk"), false, v -> actionExporterAPK()));
        barreActions.addView(boutonAction(Traducteur.get("bouton_supprimer"), true, v -> actionSupprimer()));

        majEtatActions();
        return barreActions;
    }

    private void majEtatActions() {
        boolean actif = positionSelectionnee >= 0 && positionSelectionnee < itemsProjets.size();
        if (barreActions != null) {
            for (int i = 0; i < barreActions.getChildCount(); i++) {
                View enfant = barreActions.getChildAt(i);
                enfant.setEnabled(actif);
                enfant.setAlpha(actif ? 1f : 0.4f);
            }
        }
        if (etiquetteSelection != null) {
            int total = itemsProjets.size();
            etiquetteSelection.setText(actif
                    ? Traducteur.get("demarrage_un_projet_select") + total + Traducteur.get("demarrage_au_total")
                    : total + " " + Traducteur.get("demarrage_projets"));
        }
    }

    // ---------------------------------------------------------------- adaptateur liste AVEC IMAGE

    private class AdaptateurProjets extends ArrayAdapter<ItemProjet> {
        
        AdaptateurProjets(ArrayList<ItemProjet> items) {
            super(EcranDemarrage.this, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ligne;
            ImageView vignette;
            TextView titre;
            TextView sousTitre;

            if (convertView == null) {
                ligne = new LinearLayout(EcranDemarrage.this);
                ligne.setOrientation(LinearLayout.HORIZONTAL);
                ligne.setGravity(Gravity.CENTER_VERTICAL);
                ligne.setPadding(dp(8), dp(8), dp(8), dp(8));

                // L'image de la vignette
                vignette = new ImageView(EcranDemarrage.this);
                vignette.setTag("vignette");
                LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(dp(54), dp(54));
                lpImg.setMargins(0, 0, dp(12), 0);
                vignette.setLayoutParams(lpImg);
                
                // Fond de la vignette (arrondi et bordure)
                GradientDrawable fondImg = new GradientDrawable();
                fondImg.setCornerRadius(dp(6));
                fondImg.setColor(Palette.fondPanneaux);
                fondImg.setStroke(dp(1), Palette.bordure);
                vignette.setBackground(fondImg);
                vignette.setClipToOutline(true); // Coupe l'image pour respecter les bords arrondis
                ligne.addView(vignette);

                // Conteneur Texte
                LinearLayout textLayout = new LinearLayout(EcranDemarrage.this);
                textLayout.setOrientation(LinearLayout.VERTICAL);

                titre = new TextView(EcranDemarrage.this);
                titre.setTextSize(15f);
                titre.setTag("titre");
                textLayout.addView(titre);

                sousTitre = new TextView(EcranDemarrage.this);
                sousTitre.setTextSize(11f);
                sousTitre.setPadding(0, dp(2), 0, 0);
                sousTitre.setTag("sousTitre");
                textLayout.addView(sousTitre);

                ligne.addView(textLayout);

                LinearLayout.LayoutParams lpLigne = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpLigne.setMargins(0, 0, 0, dp(4));
                ligne.setLayoutParams(lpLigne);
            } else {
                ligne = (LinearLayout) convertView;
                vignette = (ImageView) ligne.findViewWithTag("vignette");
                titre = (TextView) ligne.findViewWithTag("titre");
                sousTitre = (TextView) ligne.findViewWithTag("sousTitre");
            }

            ItemProjet item = getItem(position);
            boolean choisi = (!item.estExemple && position == positionSelectionnee);

            ligne.setBackground(choisi
                    ? fond(couleurSelection(), 6, Palette.bordure, 1)
                    : fond(Color.TRANSPARENT, 6, Color.TRANSPARENT, 0));

            titre.setText(item.nom);
            titre.setTextColor(choisi ? Palette.texteSelectionne : Palette.texteNormal);
            sousTitre.setText(item.sousTitre);
            sousTitre.setTextColor(couleurTexteSecondaire());

            // Chargement de l'image sécurisé
            android.graphics.Bitmap bmp = null;
            if (item.estExemple) {
                String nomFichierSansExt = item.nomZipExemple.substring(0, item.nomZipExemple.lastIndexOf('.'));
                try {
                    InputStream is = getAssets().open("exemples/" + nomFichierSansExt + ".png");
                    bmp = android.graphics.BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception ignored) {}
            } else {
                File fVignette = new File(item.dossier, "vignette.png");
                if (fVignette.exists()) {
                    bmp = android.graphics.BitmapFactory.decodeFile(fVignette.getAbsolutePath());
                }
            }

            // Application de l'image ou de l'icône par défaut
            if (bmp != null) {
                vignette.setImageBitmap(bmp);
                vignette.setScaleType(ImageView.ScaleType.CENTER_CROP);
                vignette.setPadding(0,0,0,0);
            } else {
                vignette.setImageBitmap(null);
                vignette.setImageResource(R.drawable.folder_open_24px);
                vignette.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                vignette.setPadding(dp(12), dp(12), dp(12), dp(12));
                Palette.appliquerCouleurIcone(vignette, Palette.iconeNormal);
            }

            return ligne;
        }
    }
// bas 2

// haut 3
    // ---------------------------------------------------------------- chargement des données

    private void chargerListeProjets() {
        if (listeProjets == null) return;

        File dossierRacine = new File(getFilesDir(), "projets");
        itemsProjets.clear();
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
                        ItemProjet item = new ItemProjet();
                        item.estExemple = false;
                        item.dossier = sousDossier;
                        item.nom = metaJson.optString("nom", Traducteur.get("projet_sans_nom"));
                        item.sousTitre = Traducteur.get("projet_modifie_le") + " " + metaJson.optString("dateModif", Traducteur.get("date_inconnue"));
                        
                        itemsProjets.add(item);
                        dossiersProjets.add(sousDossier);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (positionSelectionnee >= itemsProjets.size()) {
            positionSelectionnee = -1;
        }

        adaptateurProjets = new AdaptateurProjets(itemsProjets);
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

    private void chargerListeExemples() {
        if (listeExemples == null) return;
        itemsExemples.clear();

        try {
            String[] fichiersAssets = getAssets().list("exemples");
            if (fichiersAssets != null) {
                for (String fichier : fichiersAssets) {
                    if (fichier.toLowerCase().endsWith(".zip")) {
                        String nomSansExt = fichier.substring(0, fichier.lastIndexOf('.'));
                        ItemProjet item = new ItemProjet();
                        item.estExemple = true;
                        item.nomZipExemple = fichier;
                        // On formate le nom proprement (ex: escape_game -> Escape Game)
                        item.nom = nomSansExt.replace("_", " ");
                        if (item.nom.length() > 0) {
                            item.nom = item.nom.substring(0, 1).toUpperCase() + item.nom.substring(1);
                        }
                        item.sousTitre = Traducteur.get("demarrage_modele_pret");
                        itemsExemples.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adaptateurExemples = new AdaptateurProjets(itemsExemples);
        listeExemples.setAdapter(adaptateurExemples);

        listeExemples.setOnItemClickListener((parent, view, position, id) -> {
            ItemProjet item = itemsExemples.get(position);
            new AlertDialog.Builder(this)
                .setTitle(Traducteur.get("dialogue_ouvrir_exemple_titre"))
                .setMessage(Traducteur.get("dialogue_ouvrir_exemple_msg1") + item.nom + Traducteur.get("dialogue_ouvrir_exemple_msg2"))
                .setPositiveButton(Traducteur.get("bouton_oui"), (dialog, which) -> actionImporterExemple(item.nomZipExemple, item.nom))
                .setNegativeButton(Traducteur.get("bouton_annuler"), null)
                .show();
        });
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
// bas 3
// haut 4
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

    private void actionExporterProjet() {
        File source = dossierSelectionne();
        if (source == null) return;
        
        projetAExporter = source;
        String nom = nomProjet(source).replaceAll("[^a-zA-Z0-9-_ ]", "_").trim();
        
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_TITLE, nom + ".zip");
        startActivityForResult(intent, REQUEST_CODE_EXPORT_PROJET);
    }

    private void actionExporterAPK() {
        File source = dossierSelectionne();
        if (source == null) return;
        
        String nomActuel = nomProjet(source);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(20), dp(20), dp(20));

        TextView labelNom = new TextView(this);
        labelNom.setText(Traducteur.get("export_apk_label_nom"));
        labelNom.setTextColor(Palette.texteNormal);
        labelNom.setTextSize(14f);
        labelNom.setPadding(0, 0, 0, dp(10));
        layout.addView(labelNom);

        final EditText champNom = new EditText(this);
        champNom.setText(nomActuel);
        champNom.setSingleLine(true);
        layout.addView(champNom);

        TextView labelLogo = new TextView(this);
        labelLogo.setText(Traducteur.get("export_apk_label_logo"));
        labelLogo.setTextColor(Palette.texteSelectionne);
        labelLogo.setTextSize(12f);
        labelLogo.setPadding(0, dp(10), 0, 0);
        layout.addView(labelLogo);

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setTitle(Traducteur.get("export_apk_params_titre"))
                .setView(layout)
                .setPositiveButton(Traducteur.get("bouton_continuer"), null)
                .setNegativeButton(Traducteur.get("bouton_annuler"), (d, w) -> d.cancel())
                .create();
        
        dialogue.show();

        dialogue.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nomJeu = champNom.getText().toString().trim();
            if (nomJeu.isEmpty()) {
                Toast.makeText(this, Traducteur.get("erreur_nom_vide"), Toast.LENGTH_SHORT).show();
                return;
            }
            
            projetAExporter = source;
            nomJeuAExporter = nomJeu;
            
            String nomFichier = nomJeu.replaceAll("[^a-zA-Z0-9-_ ]", "_").trim();
            
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_TITLE, nomFichier + ".apk");
            startActivityForResult(intent, REQUEST_CODE_EXPORT_APK);
            
            dialogue.dismiss();
        });
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

    private void actionImporterExemple(String nomZip, String nomAffiche) {
        try {
            InputStream is = getAssets().open("exemples/" + nomZip);
            String uuid = UUID.randomUUID().toString();
            File cible = new File(new File(getFilesDir(), "projets"), uuid);
            cible.mkdirs();
            
            java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(is);
            java.util.zip.ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(cible, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(outFile);
                    byte[] buffer = new byte[8192];
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
            
            File metaFile = new File(cible, "meta.json");
            if (metaFile.exists()) {
                JSONObject metaJson = lireJson(metaFile);
                String nomUnique = nomAffiche + " " + Traducteur.get("projet_copie");
                int index = 2;
                while (nomDejaUtilise(nomUnique, null)) {
                    nomUnique = nomAffiche + " " + Traducteur.get("projet_copie") + " " + index;
                    index++;
                }
                metaJson.put("nom", nomUnique);
                metaJson.put("dateModif", dateMaintenant());
                FileWriter fw = new FileWriter(metaFile);
                fw.write(metaJson.toString(4));
                fw.close();
            }
            
            chargerListeProjets();
            Toast.makeText(this, Traducteur.get("toast_exemple_importe"), Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
            intent.putExtra("cheminProjet", cible.getAbsolutePath());
            startActivity(intent);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, Traducteur.get("erreur_import_exemple"), Toast.LENGTH_SHORT).show();
        }
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

    // ---------------------------------------------------------------- utilitaires web & maj

    private void ouvrirLien(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, Traducteur.get("erreur_ouvrir_lien"), Toast.LENGTH_SHORT).show();
        }
    }

    private void verifierMiseAJour() {
        Toast.makeText(this, Traducteur.get("maj_recherche"), Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("https://api.github.com/repos/zinzin66/Ludexav4/releases/latest");
                java.net.HttpURLConnection connexion = (java.net.HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("GET");
                connexion.setRequestProperty("Accept", "application/vnd.github.v3+json");

                if (connexion.getResponseCode() == 200) {
                    InputStream is = connexion.getInputStream();
                    Scanner scanner = new Scanner(is).useDelimiter("\\A");
                    String reponse = scanner.hasNext() ? scanner.next() : "";
                    is.close();

                    JSONObject json = new JSONObject(reponse);
                    String nomVersion = json.getString("tag_name");
                    String notes = json.optString("body", Traducteur.get("maj_dispo"));
                    
                    org.json.JSONArray assets = json.getJSONArray("assets");
                    String urlTelechargement = null;
                    for (int i = 0; i < assets.length(); i++) {
                        JSONObject asset = assets.getJSONObject(i);
                        if (asset.getString("name").endsWith(".apk")) {
                            urlTelechargement = asset.getString("browser_download_url");
                            break;
                        }
                    }

                    if (urlTelechargement != null) {
                        final String finalUrl = urlTelechargement;
                        runOnUiThread(() -> afficherAlerteMiseAJour(nomVersion, notes, finalUrl));
                    } else {
                        runOnUiThread(() -> Toast.makeText(EcranDemarrage.this, Traducteur.get("maj_aucun_apk"), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(EcranDemarrage.this, Traducteur.get("maj_a_jour"), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EcranDemarrage.this, Traducteur.get("erreur_reseau") + " : " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void afficherAlerteMiseAJour(String version, String notes, String url) {
        new AlertDialog.Builder(this)
            .setTitle(Traducteur.get("maj_titre") + " : " + version)
            .setMessage(notes)
            .setPositiveButton(Traducteur.get("bouton_telecharger"), (dialog, which) -> telechargerMiseAJour(url))
            .setNegativeButton(Traducteur.get("bouton_plus_tard"), null)
            .show();
    }

    private void telechargerMiseAJour(String url) {
        try {
            android.app.DownloadManager.Request requete = new android.app.DownloadManager.Request(Uri.parse(url));
            requete.setTitle(Traducteur.get("maj_titre_dl"));
            requete.setDescription(Traducteur.get("maj_desc_dl"));
            requete.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            requete.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, "Yop2D_update.apk");

            android.app.DownloadManager gestionnaire = (android.app.DownloadManager) getSystemService(android.content.Context.DOWNLOAD_SERVICE);
            gestionnaire.enqueue(requete);
            
            Toast.makeText(this, Traducteur.get("maj_dl_demarre"), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, Traducteur.get("maj_echec_natif"), Toast.LENGTH_SHORT).show();
            ouvrirLien(url);
        }
    }
}
// bas 4
 
