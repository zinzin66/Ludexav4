package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PanneauRessources extends ScrollView {

    public PanneauRessources(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Définition de l'apparence du panneau gauche
        setBackgroundColor(Color.parseColor("#333333"));
        setLayoutParams(new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // Ajout des 5 sections prévues dans l'accordéon
        layoutPrincipal.addView(creerSectionGenerique(context, "Scènes", "Ajouter / Supprimer / Renommer"));
        layoutPrincipal.addView(creerSectionGenerique(context, "Objets à placer", "Texte, Rond, Carré..."));
        layoutPrincipal.addView(creerSectionGenerique(context, "Arborescence", "Gestion de l'ordre Z"));
        layoutPrincipal.addView(creerSectionAssets(context)); // Section spécifique avec les boutons Renommer/Supprimer
        layoutPrincipal.addView(creerSectionGenerique(context, "Variables", "Locales / Globales"));

        addView(layoutPrincipal);
    }

    // Méthode pour construire les sections génériques de l'accordéon
    private View creerSectionGenerique(Context context, String titre, String contenuApercu) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(titre + " ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        TextView txt = new TextView(context);
        txt.setText("[ " + contenuApercu + " ]");
        txt.setTextColor(Color.LTGRAY);
        contenu.addView(txt);

        // Logique de bascule (Afficher/Masquer)
        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(titre + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(titre + " ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    // Méthode spécifique pour la section Assets (avec les boutons d'action)
    private View creerSectionAssets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Assets ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        Button btnRenommer = new Button(context);
        btnRenommer.setText("Renommer");
        btnRenommer.setOnClickListener(v -> afficherPopupRenommer(context, "Nom_Actuel_Asset"));

        Button btnSupprimer = new Button(context);
        btnSupprimer.setText("Supprimer");
        btnSupprimer.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer cet asset ?"));

        contenu.addView(btnRenommer);
        contenu.addView(btnSupprimer);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Assets ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Assets ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    // Fenêtre modale pour renommer
    private void afficherPopupRenommer(Context context, String nomActuel) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Renommer l'asset");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setText(nomActuel);
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnValider = new Button(context);
        btnValider.setText("Valider");
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString();
            Toast.makeText(context, "Renommé en : " + nouveauNom, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    // Fenêtre modale pour confirmer la suppression
    private void afficherPopupConfirmation(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Confirmation");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        TextView txtMessage = new TextView(context);
        txtMessage.setText(message);
        txtMessage.setPadding(0, 0, 0, 20);
        layoutDialog.addView(txtMessage);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnOui = new Button(context);
        btnOui.setText("Oui");
        btnOui.setOnClickListener(v -> {
            Toast.makeText(context, "Action confirmée", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Button btnNon = new Button(context);
        btnNon.setText("Non");
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
            }
