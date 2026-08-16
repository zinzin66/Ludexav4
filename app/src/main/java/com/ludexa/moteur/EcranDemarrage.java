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

    // --- NOUVEAUTÉ : Variable globale pour la langue ---
    public static String langueCourante = "fr";
    private TextView libelleLangue;
    // ---------------------------------------------------

    private ListView listeProjets;
    private AdaptateurProjets adaptateurProjets;
    private final ArrayList<File> dossiersProjets = new ArrayList<>();
    private int positionSelectionnee = -1;

    private LinearLayout barreActions;
    private TextView etiquetteSelection;

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
        
        // Clic sur l'icône Langue ouvre la fenêtre de choix
        rangeeLangue.addView(boutonBandeau(R.drawable.language_24px, "Langue", v -> afficherDialogueLangue()));

        libelleLangue = new TextView(this);
        libelleLangue.setText("Français");
        libelleLangue.setTextSize(13f);
        libelleLangue.setTextColor(couleurTexteSecondaire());
        libelleLangue.setGravity(Gravity.CENTER_VERTICAL);
        rangeeLangue.addView(libelleLangue);

        colonneGauche.addView(rangeeLangue);

        return colonneGauche;
    }

    // --- NOUVEAUTÉ : Dialogue de sélection de la langue ---
    private void afficherDialogueLangue() {
        String[] langues = {"Français", "English", "Русский"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisir une langue");
        builder.setItems(langues, (dialog, which) -> {
            switch (which) {
                case 0: langueCourante = "fr"; libelleLangue.setText("Français"); break;
                case 1: langueCourante = "en"; libelleLangue.setText("English"); break;
                case 2: langueCourante = "ru"; libelleLangue.setText("Русский"); break;
            }
        });
        builder.show();
    }
    // ------------------------------------------------------

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

        bandeau.addView(boutonBandeau(R.drawable.add_24px, "Créer un projet",
                v -> afficherDialogueCreationProjet()));
        bandeau.addView(boutonBandeau(R.drawable.folder_open_24px, "Ouvrir un projet téléchargé",
                v -> Toast.makeText(this, "Import de projet : à venir", Toast.LENGTH_SHORT).show()));

        bandeau.addView(separateurVertical());

        bandeau.addView(boutonBandeau(R.drawable.bug_report_24px, "Diagnostic du dossier projets",
                v -> afficherDiagnosticDossier()));
        bandeau.addView(boutonBandeau(R.drawable.build_24px, "Test mkdirs",
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
        
