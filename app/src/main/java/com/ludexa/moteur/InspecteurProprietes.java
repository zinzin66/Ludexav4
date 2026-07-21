package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class InspecteurProprietes extends LinearLayout {

    private ScrollView scrollInspecteur;
    private TextView titreInspecteur;
    private Button boutonMasquer;
    private LinearLayout.LayoutParams paramsOuvert;
    private LinearLayout.LayoutParams paramsFerme;

    public InspecteurProprietes(Context context) {
        super(context);
        initialiserInterface(context);
    }

    private void initialiserInterface(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(0xFFE0E0E0); // Fond gris clair

        // Paramètres de taille pour l'état ouvert et fermé
        paramsOuvert = new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        // ---- Entête de l'Inspecteur ----
        LinearLayout enteteInspecteur = new LinearLayout(context);
        enteteInspecteur.setOrientation(LinearLayout.HORIZONTAL);
        enteteInspecteur.setPadding(10, 10, 10, 10);
        enteteInspecteur.setBackgroundColor(0xFFCCCCCC); // Gris plus foncé

        titreInspecteur = new TextView(context);
        titreInspecteur.setText("Inspecteur");
        titreInspecteur.setTextSize(16f);
        titreInspecteur.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titreInspecteur.setLayoutParams(paramsTitre);

        boutonMasquer = new Button(context);
        boutonMasquer.setText(">");
        
        enteteInspecteur.addView(titreInspecteur);
        enteteInspecteur.addView(boutonMasquer);
        this.addView(enteteInspecteur);

        // ---- Contenu défilant (ScrollView) ----
        scrollInspecteur = new ScrollView(context);
        LinearLayout contenuInspecteur = new LinearLayout(context);
        contenuInspecteur.setOrientation(LinearLayout.VERTICAL);
        contenuInspecteur.setPadding(15, 15, 15, 15);

        TextView texteInfo = new TextView(context);
        texteInfo.setText("Sélectionnez un objet sur la scène pour afficher et modifier ses propriétés.");
        texteInfo.setPadding(0, 0, 0, 30);
        contenuInspecteur.addView(texteInfo);

        // Bouton Supprimer
        Button boutonSupprimer = new Button(context);
        boutonSupprimer.setText("Supprimer l'objet");
        boutonSupprimer.setBackgroundColor(0xFFFFCCCC); // Rouge clair
        boutonSupprimer.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer cet objet de la scène ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        Toast.makeText(context, "Objet supprimé (action validée)", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
        contenuInspecteur.addView(boutonSupprimer);

        scrollInspecteur.addView(contenuInspecteur);
        this.addView(scrollInspecteur);

        // ---- Logique de masquage ----
        boutonMasquer.setOnClickListener(v -> {
            if (scrollInspecteur.getVisibility() == View.VISIBLE) {
                // Fermer
                scrollInspecteur.setVisibility(View.GONE);
                titreInspecteur.setVisibility(View.GONE);
                boutonMasquer.setText("<");
                this.setLayoutParams(paramsFerme);
            } else {
                // Ouvrir
                scrollInspecteur.setVisibility(View.VISIBLE);
                titreInspecteur.setVisibility(View.VISIBLE);
                boutonMasquer.setText(">");
                this.setLayoutParams(paramsOuvert);
            }
        });
    }
}
