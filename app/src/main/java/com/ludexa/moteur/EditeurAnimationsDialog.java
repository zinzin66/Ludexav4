// haut 1
package com.ludexa.moteur;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditeurAnimationsDialog extends Dialog {

    private ObjetBase objet;
    private String cheminProjet;
    private String animationSelectionnee = null;
    
    private LinearLayout conteneurAnimations;
    private LinearLayout conteneurFrames;
    private TextView titreFrames;

    private int dp(Context c, int valeur) {
        return (int) (valeur * c.getResources().getDisplayMetrics().density);
    }

    private android.graphics.drawable.GradientDrawable fond(Context c, int couleurFond, int couleurBordure, int rayon) {
        android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
        g.setColor(couleurFond);
        g.setCornerRadius(dp(c, rayon));
        g.setStroke(dp(c, 1), couleurBordure);
        return g;
    }

    public EditeurAnimationsDialog(Context context, ObjetBase objet, String cheminProjet) {
        super(context);
        this.objet = objet;
        this.cheminProjet = cheminProjet;
        setTitle("Éditeur d'Animations - " + objet.nom);

        if (this.objet.animations == null) {
            this.objet.animations = new java.util.HashMap<>();
        }

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Palette.fondPanneaux);
        root.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 8));

        // COLONNE GAUCHE (Liste des animations)
        LinearLayout colonneGauche = new LinearLayout(context);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpGauche = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        lpGauche.setMargins(0, 0, dp(context, 8), 0);
        colonneGauche.setLayoutParams(lpGauche);
        colonneGauche.setBackground(fond(context, Palette.fondNormal, Palette.bordure, 12));
        colonneGauche.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 8));

        Button btnNouvelleAnim = new Button(context);
        btnNouvelleAnim.setText("+ Nouvelle Animation");
        btnNouvelleAnim.setAllCaps(false);
        btnNouvelleAnim.setBackground(fond(context, Color.parseColor("#4CAF50"), Palette.bordure, 8));
        btnNouvelleAnim.setTextColor(Palette.texteNormal);
        btnNouvelleAnim.setOnClickListener(v -> demanderNomNouvelleAnimation(context));
        colonneGauche.addView(btnNouvelleAnim);

        ScrollView scrollGauche = new ScrollView(context);
        scrollGauche.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        conteneurAnimations = new LinearLayout(context);
        conteneurAnimations.setOrientation(LinearLayout.VERTICAL);
        scrollGauche.addView(conteneurAnimations);
        colonneGauche.addView(scrollGauche);

        // COLONNE DROITE (Liste des frames/images)
        LinearLayout colonneDroite = new LinearLayout(context);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f));
        colonneDroite.setBackground(fond(context, Palette.fondNormal, Palette.bordure, 12));
        colonneDroite.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 8));

        titreFrames = new TextView(context);
        titreFrames.setText("Sélectionnez une animation");
        titreFrames.setTextColor(Palette.texteSelectionne);
        titreFrames.setTextSize(16f);
        titreFrames.setPadding(0, 0, 0, dp(context, 8));
        colonneDroite.addView(titreFrames);

        Button btnAjouterFrame = new Button(context);
        btnAjouterFrame.setText("+ Ajouter Image (Frame)");
        btnAjouterFrame.setAllCaps(false);
        btnAjouterFrame.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
        btnAjouterFrame.setTextColor(Palette.texteNormal);
        btnAjouterFrame.setOnClickListener(v -> afficherSelecteurImage(context));
        colonneDroite.addView(btnAjouterFrame);

        ScrollView scrollDroit = new ScrollView(context);
        scrollDroit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        conteneurFrames = new LinearLayout(context);
        conteneurFrames.setOrientation(LinearLayout.VERTICAL);
        scrollDroit.addView(conteneurFrames);
        colonneDroite.addView(scrollDroit);

        root.addView(colonneGauche);
        root.addView(colonneDroite);

        // BOUTON FERMER EN BAS
        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.setBackgroundColor(Palette.fondPanneaux);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        Button btnFermer = new Button(context);
        btnFermer.setText("Fermer");
        btnFermer.setAllCaps(false);
        btnFermer.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
        btnFermer.setTextColor(Palette.texteNormal);
        LinearLayout.LayoutParams lpFermer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpFermer.setMargins(dp(context, 8), 0, dp(context, 8), dp(context, 8));
        btnFermer.setLayoutParams(lpFermer);
        btnFermer.setOnClickListener(v -> dismiss());
        grandLayout.addView(btnFermer);

        setContentView(grandLayout);

        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.90);
            int height = (int) (metrics.heightPixels * 0.85);
            window.setLayout(width, height);
        }

        rafraichirListeAnimations(context);
    }

    private void rafraichirListeAnimations(Context context) {
        conteneurAnimations.removeAllViews();
        for (String nomAnim : objet.animations.keySet()) {
            LinearLayout ligneAnim = new LinearLayout(context);
            ligneAnim.setOrientation(LinearLayout.HORIZONTAL);
            ligneAnim.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams lpLigne = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpLigne.setMargins(0, dp(context, 4), 0, 0);
            ligneAnim.setLayoutParams(lpLigne);

            Button btnAnim = new Button(context);
            btnAnim.setText(nomAnim);
            btnAnim.setAllCaps(false);
            btnAnim.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            if (nomAnim.equals(animationSelectionnee)) {
                btnAnim.setBackground(fond(context, Color.parseColor("#3F51B5"), Palette.bordure, 8));
            } else {
                btnAnim.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
            }
            btnAnim.setTextColor(Palette.texteNormal);
            btnAnim.setOnClickListener(v -> {
                animationSelectionnee = nomAnim;
                rafraichirListeAnimations(context);
            });

            Button btnSupprimer = new Button(context);
            btnSupprimer.setText("X");
            btnSupprimer.setTextColor(Color.WHITE);
            btnSupprimer.setBackground(fond(context, Color.parseColor("#F44336"), Palette.bordure, 8));
            btnSupprimer.setOnClickListener(v -> {
                objet.animations.remove(nomAnim);
                if (nomAnim.equals(animationSelectionnee)) animationSelectionnee = null;
                rafraichirListeAnimations(context);
            });

            ligneAnim.addView(btnAnim);
            ligneAnim.addView(btnSupprimer);
            conteneurAnimations.addView(ligneAnim);
        }
        rafraichirListeFrames(context);
    }

    private void rafraichirListeFrames(Context context) {
        conteneurFrames.removeAllViews();
        if (animationSelectionnee == null) {
            titreFrames.setText("Sélectionnez une animation");
            return;
        }
        titreFrames.setText("Images (Frames) pour : " + animationSelectionnee);
        List<String> frames = objet.animations.get(animationSelectionnee);
        if (frames != null) {
            for (int i = 0; i < frames.size(); i++) {
                final int index = i;
                String chemin = frames.get(i);
                
                LinearLayout ligneFrame = new LinearLayout(context);
                ligneFrame.setOrientation(LinearLayout.HORIZONTAL);
                ligneFrame.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams lpLigne = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpLigne.setMargins(0, dp(context, 4), 0, 0);
                ligneFrame.setLayoutParams(lpLigne);

                TextView txtFrame = new TextView(context);
                txtFrame.setText("[" + i + "] " + chemin.replace("assets_ludexa/Images/", ""));
                txtFrame.setTextColor(Palette.texteNormal);
                txtFrame.setPadding(dp(context, 8), dp(context, 12), dp(context, 8), dp(context, 12));
                txtFrame.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                txtFrame.setBackground(fond(context, Palette.canvasFond, Palette.bordure, 8));

                Button btnSupprimer = new Button(context);
                btnSupprimer.setText("X");
                btnSupprimer.setTextColor(Color.WHITE);
                btnSupprimer.setBackground(fond(context, Color.parseColor("#F44336"), Palette.bordure, 8));
                btnSupprimer.setOnClickListener(v -> {
                    frames.remove(index);
                    rafraichirListeFrames(context);
                });

                ligneFrame.addView(txtFrame);
                ligneFrame.addView(btnSupprimer);
                conteneurFrames.addView(ligneFrame);
            }
        }
    }

    private void demanderNomNouvelleAnimation(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nouvelle Animation");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Ex: Ouverture");
        builder.setView(input);
        builder.setPositiveButton("Créer", (dialog, which) -> {
            String nom = input.getText().toString().trim();
            if (!nom.isEmpty() && !objet.animations.containsKey(nom)) {
                objet.animations.put(nom, new ArrayList<>());
                animationSelectionnee = nom;
                rafraichirListeAnimations(context);
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void afficherSelecteurImage(Context context) {
        if (animationSelectionnee == null || cheminProjet == null) return;
        
        File dossierImages = new File(cheminProjet, "assets_ludexa/Images");
        List<String> images = new ArrayList<>();
        if (dossierImages.exists() && dossierImages.isDirectory()) {
            File[] fichiers = dossierImages.listFiles();
            if (fichiers != null) {
                for (File f : fichiers) {
                    if (!f.isDirectory() && (f.getName().toLowerCase().endsWith(".png") || f.getName().toLowerCase().endsWith(".jpg"))) {
                        images.add("assets_ludexa/Images/" + f.getName());
                    }
                }
            }
        }
        if (images.isEmpty()) {
            images.add("Aucune image trouvée");
        }
        
        AlertDialog.Builder builderImage = new AlertDialog.Builder(context);
        builderImage.setTitle("Choisir une image pour la frame");
        String[] imagesArray = images.toArray(new String[0]);
        builderImage.setItems(imagesArray, (dialog, which) -> {
            if (!imagesArray[which].equals("Aucune image trouvée")) {
                objet.animations.get(animationSelectionnee).add(imagesArray[which]);
                rafraichirListeFrames(context);
            }
        });
        builderImage.show();
    }
}
// bas 1
