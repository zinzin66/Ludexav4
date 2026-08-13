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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditeurAnimationsDialog extends Dialog {

    private String cheminProjet;
    private String animationSelectionnee = null;
    
    // Base de données globale des animations
    private Map<String, List<String>> animationsGlobales = new LinkedHashMap<>();
    
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

    public EditeurAnimationsDialog(Context context, String cheminProjet) {
        super(context);
        this.cheminProjet = cheminProjet;
        setTitle("Gestionnaire Global des Animations");

        chargerFichierAnimations();

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
        btnNouvelleAnim.setOnClickListener(v -> demanderNomNouvelleAnimation(context, null));
        colonneGauche.addView(btnNouvelleAnim);

        ScrollView scrollGauche = new ScrollView(context);
        scrollGauche.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        conteneurAnimations = new LinearLayout(context);
        conteneurAnimations.setOrientation(LinearLayout.VERTICAL);
        scrollGauche.addView(conteneurAnimations);
        colonneGauche.addView(scrollGauche);

        // COLONNE DROITE (Liste des frames)
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

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.setBackgroundColor(Palette.fondPanneaux);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        Button btnFermer = new Button(context);
        btnFermer.setText("Fermer & Sauvegarder");
        btnFermer.setAllCaps(false);
        btnFermer.setBackground(fond(context, Color.parseColor("#3F51B5"), Palette.bordure, 8));
        btnFermer.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lpFermer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpFermer.setMargins(dp(context, 8), 0, dp(context, 8), dp(context, 8));
        btnFermer.setLayoutParams(lpFermer);
        btnFermer.setOnClickListener(v -> {
            sauvegarderFichierAnimations();
            dismiss();
        });
        grandLayout.addView(btnFermer);

        setContentView(grandLayout);

        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.95);
            int height = (int) (metrics.heightPixels * 0.90);
            window.setLayout(width, height);
        }

        rafraichirListeAnimations(context);
    }

    private void chargerFichierAnimations() {
        animationsGlobales.clear();
        if (cheminProjet == null) return;
        File fichierAnim = new File(cheminProjet, "assets_ludexa/Textes/animations.txt");
        if (fichierAnim.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(fichierAnim));
                String ligne;
                while ((ligne = br.readLine()) != null) {
                    ligne = ligne.trim();
                    if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                    int idxEgal = ligne.indexOf('=');
                    if (idxEgal > 0) {
                        String cle = ligne.substring(0, idxEgal).trim();
                        String valeurs = ligne.substring(idxEgal + 1).trim();
                        List<String> images = new ArrayList<>();
                        if (!valeurs.isEmpty()) {
                            String[] parts = valeurs.split(",");
                            for (String p : parts) images.add(p.trim());
                        }
                        animationsGlobales.put(cle, images);
                    }
                }
                br.close();
            } catch (Exception e) {}
        }
    }

    private void sauvegarderFichierAnimations() {
        if (cheminProjet == null) return;
        File dossier = new File(cheminProjet, "assets_ludexa/Textes");
        if (!dossier.exists()) dossier.mkdirs();
        File fichierAnim = new File(dossier, "animations.txt");
        try {
            FileWriter fw = new FileWriter(fichierAnim);
            fw.write("// Fichier généré automatiquement pour les animations (Nom=image1.png,image2.png)\n");
            for (Map.Entry<String, List<String>> entry : animationsGlobales.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getKey()).append("=");
                for (int i = 0; i < entry.getValue().size(); i++) {
                    sb.append(entry.getValue().get(i));
                    if (i < entry.getValue().size() - 1) sb.append(",");
                }
                sb.append("\n");
                fw.write(sb.toString());
            }
            fw.close();
        } catch (Exception e) {}
    }

    private void rafraichirListeAnimations(Context context) {
        conteneurAnimations.removeAllViews();
        for (String nomAnim : animationsGlobales.keySet()) {
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

            Button btnRenommer = new Button(context);
            btnRenommer.setText("R");
            btnRenommer.setTextColor(Color.WHITE);
            btnRenommer.setBackground(fond(context, Color.parseColor("#FF9800"), Palette.bordure, 8));
            btnRenommer.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 45), ViewGroup.LayoutParams.WRAP_CONTENT));
            btnRenommer.setOnClickListener(v -> demanderNomNouvelleAnimation(context, nomAnim));

            Button btnSupprimer = new Button(context);
            btnSupprimer.setText("X");
            btnSupprimer.setTextColor(Color.WHITE);
            btnSupprimer.setBackground(fond(context, Color.parseColor("#F44336"), Palette.bordure, 8));
            btnSupprimer.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 45), ViewGroup.LayoutParams.WRAP_CONTENT));
            btnSupprimer.setOnClickListener(v -> {
                animationsGlobales.remove(nomAnim);
                if (nomAnim.equals(animationSelectionnee)) animationSelectionnee = null;
                rafraichirListeAnimations(context);
            });

            ligneAnim.addView(btnAnim);
            ligneAnim.addView(btnRenommer);
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
        titreFrames.setText("Séquence de l'animation : " + animationSelectionnee);
        List<String> frames = animationsGlobales.get(animationSelectionnee);
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

                Button btnUp = new Button(context);
                btnUp.setText("↑");
                btnUp.setTextColor(Palette.texteNormal);
                btnUp.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btnUp.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 45), ViewGroup.LayoutParams.WRAP_CONTENT));
                btnUp.setOnClickListener(v -> {
                    if (index > 0) {
                        Collections.swap(frames, index, index - 1);
                        rafraichirListeFrames(context);
                    }
                });

                Button btnDown = new Button(context);
                btnDown.setText("↓");
                btnDown.setTextColor(Palette.texteNormal);
                btnDown.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btnDown.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 45), ViewGroup.LayoutParams.WRAP_CONTENT));
                btnDown.setOnClickListener(v -> {
                    if (index < frames.size() - 1) {
                        Collections.swap(frames, index, index + 1);
                        rafraichirListeFrames(context);
                    }
                });

                Button btnSupprimer = new Button(context);
                btnSupprimer.setText("X");
                btnSupprimer.setTextColor(Color.WHITE);
                btnSupprimer.setBackground(fond(context, Color.parseColor("#F44336"), Palette.bordure, 8));
                btnSupprimer.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 45), ViewGroup.LayoutParams.WRAP_CONTENT));
                btnSupprimer.setOnClickListener(v -> {
                    frames.remove(index);
                    rafraichirListeFrames(context);
                });

                ligneFrame.addView(txtFrame);
                ligneFrame.addView(btnUp);
                ligneFrame.addView(btnDown);
                ligneFrame.addView(btnSupprimer);
                conteneurFrames.addView(ligneFrame);
            }
        }
    }

    private void demanderNomNouvelleAnimation(Context context, String ancienNom) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(ancienNom == null ? "Nouvelle Animation" : "Renommer l'animation");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (ancienNom != null) input.setText(ancienNom);
        else input.setHint("Ex: PorteOuverture");
        builder.setView(input);
        
        builder.setPositiveButton("Valider", (dialog, which) -> {
            String nom = input.getText().toString().trim();
            if (!nom.isEmpty() && !animationsGlobales.containsKey(nom)) {
                if (ancienNom != null) {
                    List<String> frames = animationsGlobales.remove(ancienNom);
                    animationsGlobales.put(nom, frames);
                } else {
                    animationsGlobales.put(nom, new ArrayList<>());
                }
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
        if (images.isEmpty()) images.add("Aucune image trouvée");
        
        AlertDialog.Builder builderImage = new AlertDialog.Builder(context);
        builderImage.setTitle("Choisir une image");
        String[] imagesArray = images.toArray(new String[0]);
        builderImage.setItems(imagesArray, (dialog, which) -> {
            if (!imagesArray[which].equals("Aucune image trouvée")) {
                animationsGlobales.get(animationSelectionnee).add(imagesArray[which]);
                rafraichirListeFrames(context);
            }
        });
        builderImage.show();
    }
}
// bas 1