package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class InspecteurProprietes extends LinearLayout {

    private ScrollView scrollInspecteur;
    private TextView titreInspecteur;
    private Button boutonMasquer;
    private LinearLayout.LayoutParams paramsOuvert;
    private LinearLayout.LayoutParams paramsFerme;

    private TextView texteInfo;
    private LinearLayout blocProprietes;
    private EditText champNom;
    private EditText champX;
    private EditText champY;
    private Button boutonSupprimer;

    private Scene sceneActive;
    private CanvasEditeur canvasEditeur;
    private ObjetBase objetCourant;
    private boolean miseAJourEnCours = false; // évite les boucles TextWatcher pendant qu'on remplit les champs

    public InspecteurProprietes(Context context, Scene scene, CanvasEditeur canvas) {
        super(context);
        this.sceneActive = scene;
        this.canvasEditeur = canvas;
        initialiserInterface(context);
    }

    private void initialiserInterface(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(0xFFE0E0E0);

        paramsOuvert = new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        LinearLayout enteteInspecteur = new LinearLayout(context);
        enteteInspecteur.setOrientation(LinearLayout.HORIZONTAL);
        enteteInspecteur.setPadding(10, 10, 10, 10);
        enteteInspecteur.setBackgroundColor(0xFFCCCCCC);

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

        scrollInspecteur = new ScrollView(context);
        LinearLayout contenuInspecteur = new LinearLayout(context);
        contenuInspecteur.setOrientation(LinearLayout.VERTICAL);
        contenuInspecteur.setPadding(15, 15, 15, 15);

        texteInfo = new TextView(context);
        texteInfo.setText("Sélectionnez un objet sur la scène pour afficher et modifier ses propriétés.");
        texteInfo.setPadding(0, 0, 0, 30);
        contenuInspecteur.addView(texteInfo);

        // ---- Bloc des propriétés (caché tant qu'aucun objet n'est sélectionné) ----
        blocProprietes = new LinearLayout(context);
        blocProprietes.setOrientation(LinearLayout.VERTICAL);
        blocProprietes.setVisibility(View.GONE);

        TextView labelNom = new TextView(context);
        labelNom.setText("Nom");
        blocProprietes.addView(labelNom);
        champNom = new EditText(context);
        blocProprietes.addView(champNom);

        TextView labelX = new TextView(context);
        labelX.setText("X");
        blocProprietes.addView(labelX);
        champX = new EditText(context);
        champX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        blocProprietes.addView(champX);

        TextView labelY = new TextView(context);
        labelY.setText("Y");
        blocProprietes.addView(labelY);
        champY = new EditText(context);
        champY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        blocProprietes.addView(champY);

        contenuInspecteur.addView(blocProprietes);

        boutonSupprimer = new Button(context);
        boutonSupprimer.setText("Supprimer l'objet");
        boutonSupprimer.setBackgroundColor(0xFFFFCCCC);
        boutonSupprimer.setOnClickListener(v -> {
            if (objetCourant == null) {
                Toast.makeText(context, "Aucun objet sélectionné", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer cet objet de la scène ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        sceneActive.objets.remove(objetCourant);
                        canvasEditeur.deselectionner();
                        afficherObjet(null);
                        canvasEditeur.invalidate();
                        Toast.makeText(context, "Objet supprimé", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
        contenuInspecteur.addView(boutonSupprimer);

        scrollInspecteur.addView(contenuInspecteur);
        this.addView(scrollInspecteur);

        boutonMasquer.setOnClickListener(v -> {
            if (scrollInspecteur.getVisibility() == View.VISIBLE) {
                scrollInspecteur.setVisibility(View.GONE);
                titreInspecteur.setVisibility(View.GONE);
                boutonMasquer.setText("<");
                this.setLayoutParams(paramsFerme);
            } else {
                scrollInspecteur.setVisibility(View.VISIBLE);
                titreInspecteur.setVisibility(View.VISIBLE);
                boutonMasquer.setText(">");
                this.setLayoutParams(paramsOuvert);
            }
        });

        // ---- Synchronisation champs -> objet en temps réel ----
        champNom.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) objetCourant.nom = texte;
        }));
        champX.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try {
                    objetCourant.x = Float.parseFloat(texte);
                    canvasEditeur.invalidate();
                } catch (NumberFormatException ignored) {}
            }
        }));
        champY.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try {
                    objetCourant.y = Float.parseFloat(texte);
                    canvasEditeur.invalidate();
                } catch (NumberFormatException ignored) {}
            }
        }));
    }

    // Affiche les propriétés de l'objet donné, ou revient au message d'info si null
    public void afficherObjet(ObjetBase objet) {
        this.objetCourant = objet;
        miseAJourEnCours = true;

        if (objet == null) {
            texteInfo.setVisibility(View.VISIBLE);
            blocProprietes.setVisibility(View.GONE);
        } else {
            texteInfo.setVisibility(View.GONE);
            blocProprietes.setVisibility(View.VISIBLE);
            champNom.setText(objet.nom);
            champX.setText(String.valueOf((int) objet.x));
            champY.setText(String.valueOf((int) objet.y));
        }

        miseAJourEnCours = false;
    }

    private TextWatcher creerWatcherSimple(java.util.function.Consumer<String> action) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!miseAJourEnCours) {
                    action.accept(s.toString());
                }
            }
        };
    }
}
