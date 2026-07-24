package com.ludexa.moteur;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class EcranDemarrage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoeudBase.contexteApplication = this;   // ← ajoute cette ligne
        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.HORIZONTAL);
        layoutPrincipal.setPadding(40, 40, 40, 40);

        LinearLayout colonneGauche = new LinearLayout(this);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        colonneGauche.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams paramsGauche = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        colonneGauche.setLayoutParams(paramsGauche);

        TextView logo = new TextView(this);
        logo.setText("[LOGO]");
        logo.setTextSize(48f);
        logo.setGravity(Gravity.CENTER);
        colonneGauche.addView(logo);

        TextView nomApp = new TextView(this);
        nomApp.setText("LUDEXA");
        nomApp.setTextSize(32f);
        nomApp.setGravity(Gravity.CENTER);
        colonneGauche.addView(nomApp);

        TextView texteBienvenue = new TextView(this);
        texteBienvenue.setText("Bienvenue dans LUDEXA — créez vos jeux sans coder.");
        texteBienvenue.setTextSize(16f);
        texteBienvenue.setGravity(Gravity.CENTER);
        texteBienvenue.setPadding(0, 20, 0, 20);
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
            Intent intent = new Intent(EcranDemarrage.this, InterfaceEditeur.class);
            startActivity(intent);
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
        colonneDroite.addView(titreListe);

        ListView listeProjets = new ListView(this);
        LinearLayout.LayoutParams paramsListe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        listeProjets.setLayoutParams(paramsListe);
        colonneDroite.addView(listeProjets);

        layoutPrincipal.addView(colonneGauche);
        layoutPrincipal.addView(colonneDroite);

        setContentView(layoutPrincipal);
    }
}
