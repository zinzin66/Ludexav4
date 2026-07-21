package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.widget.*;

public class PanneauRessources extends LinearLayout {

    private ScrollView zoneDefilante;
    private boolean estOuvert = true;

    public PanneauRessources(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(Color.parseColor("#EEEEEE"));

        // Conteneur principal de l'accordéon (à gauche)
        LinearLayout conteneurAccordeon = new LinearLayout(context);
        conteneurAccordeon.setOrientation(LinearLayout.VERTICAL);
        conteneurAccordeon.setLayoutParams(new LayoutParams(400, LayoutParams.MATCH_PARENT));

        zoneDefilante = new ScrollView(context);
        zoneDefilante.addView(conteneurAccordeon);

        // Bouton pour masquer/afficher le menu (à droite de l'accordéon)
        Button btnToggle = new Button(context);
        btnToggle.setText("<");
        btnToggle.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        btnToggle.setOnClickListener(v -> {
            estOuvert = !estOuvert;
            zoneDefilante.setVisibility(estOuvert ? VISIBLE : GONE);
            btnToggle.setText(estOuvert ? "<" : ">");
        });

        // Ajout des 4 sections de l'accordéon
        conteneurAccordeon.addView(creerSectionScene(context));
        conteneurAccordeon.addView(creerSectionObjet(context));
        conteneurAccordeon.addView(creerSectionAsset(context));
        conteneurAccordeon.addView(creerSectionVariable(context));

        // Ajout au layout principal de la classe
        addView(zoneDefilante);
        addView(btnToggle);
    }

    private View creerSectionScene(Context context) {
        return creerSectionGenerique(context, "SCÈNES", contenu -> {
            LinearLayout ligneBoutons = new LinearLayout(context);
            
            Button btnAjouter = new Button(context);
            btnAjouter.setText("+");
            
            Button btnSupprimer = new Button(context);
            btnSupprimer.setText("-");
            
            Button btnRenommer = new Button(context);
            btnRenommer.setText("Renommer");
            btnRenommer.setOnClickListener(v -> afficherPopupTexte(context, "Renommer la scène"));

            ligneBoutons.addView(btnAjouter);
            ligneBoutons.addView(btnSupprimer);
            ligneBoutons.addView(btnRenommer);
            contenu.addView(ligneBoutons);

            ScrollView liste = creerListeGenerique(context, "Liste des scènes...");
            contenu.addView(liste);
        });
    }

    private View creerSectionObjet(Context context) {
        return creerSectionGenerique(context, "OBJETS", contenu -> {
            String[] objets = {"Carré", "Rond", "Texte", "Entrée Texte", "Barre défilement"};
            
            LinearLayout conteneurBoutons = new LinearLayout(context);
            conteneurBoutons.setOrientation(LinearLayout.VERTICAL);
            
            for (String nomObj : objets) {
                Button btnObj = new Button(context);
                btnObj.setText("+ " + nomObj);
                conteneurBoutons.addView(btnObj);
            }
            contenu.addView(conteneurBoutons);

            ScrollView liste = creerListeGenerique(context, "Objets sur la scène...");
            contenu.addView(liste);
        });
    }

    private View creerSectionAsset(Context context) {
        return creerSectionGenerique(context, "ASSETS", contenu -> {
            LinearLayout ligneBoutons = new LinearLayout(context);
            
            Button btnRenommer = new Button(context);
            btnRenommer.setText("Renommer");
            
            Button btnSupprimer = new Button(context);
            btnSupprimer.setText("Supprimer");
            btnSupprimer.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer cet asset ?"));

            ligneBoutons.addView(btnRenommer);
            ligneBoutons.addView(btnSupprimer);
            contenu.addView(ligneBoutons);

            ScrollView liste = creerListeGenerique(context, "[Image] - Liste des assets...");
            contenu.addView(liste);
        });
    }

    private View creerSectionVariable(Context context) {
        return creerSectionGenerique(context, "VARIABLES", contenu -> {
            LinearLayout ligneBoutons = new LinearLayout(context);
            
            Button btnCreer = new Button(context);
            btnCreer.setText("Créer");
            btnCreer.setOnClickListener(v -> afficherPopupCreationVariable(context));
            
            Button btnSupprimer = new Button(context);
            btnSupprimer.setText("Supprimer");
            btnSupprimer.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer cette variable ?"));

            ligneBoutons.addView(btnCreer);
            ligneBoutons.addView(btnSupprimer);
            contenu.addView(ligneBoutons);

            ScrollView liste = creerListeGenerique(context, "Liste des variables...");
            contenu.addView(liste);
        });
    }

    // --- METHODES UTILITAIRES POUR L'INTERFACE ---

    private interface RemplisseurSection {
        void remplir(LinearLayout contenu);
    }

    private View creerSectionGenerique(Context context, String titre, RemplisseurSection remplisseur) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(titre);
        btnTitre.setBackgroundColor(Color.parseColor("#CCCCCC"));

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(10, 10, 10, 10);
        
        remplisseur.remplir(contenu);

        btnTitre.setOnClickListener(v -> {
            boolean estVisible = contenu.getVisibility() == VISIBLE;
            contenu.setVisibility(estVisible ? GONE : VISIBLE);
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    private ScrollView creerListeGenerique(Context context, String textePlaceholder) {
        ScrollView scrollView = new ScrollView(context);
        TextView tv = new TextView(context);
        tv.setText(textePlaceholder);
        tv.setPadding(0, 20, 0, 20);
        scrollView.addView(tv);
        return scrollView;
    }

    // --- POPUPS NATIVES ---

    private void afficherPopupTexte(Context context, String titre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titre);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void afficherPopupConfirmation(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("Oui", null);
        builder.setNegativeButton("Non", null);
        builder.show();
    }

    private void afficherPopupCreationVariable(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Créer une variable");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText nomInput = new EditText(context);
        nomInput.setHint("Nom de la variable");
        layout.addView(nomInput);

        final Spinner typeSpinner = new Spinner(context);
        String[] types = {"Locale", "Globale"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, types);
        typeSpinner.setAdapter(adapter);
        layout.addView(typeSpinner);

        builder.setView(layout);
        builder.setPositiveButton("Créer", null);
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
}
