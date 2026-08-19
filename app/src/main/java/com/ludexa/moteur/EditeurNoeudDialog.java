// haut 1 18 aout
package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class EditeurNoeudDialog extends Dialog {

    private String champActif = null;

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

    public EditeurNoeudDialog(Context context, NoeudBase noeud, Scene scene, Runnable onValidate) {
        super(context);
        setTitle(Traducteur.get("noeud_edit_valeur") + " - " + Traducteur.get(noeud.nom));

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Palette.fondPanneaux);
        root.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 4));

        final LinearLayout conteneurClavier = new LinearLayout(context);
        conteneurClavier.setOrientation(LinearLayout.VERTICAL);
        conteneurClavier.setBackground(fond(context, Palette.fondNormal, Palette.bordure, 12));
        conteneurClavier.setPadding(dp(context, 6), dp(context, 8), dp(context, 6), dp(context, 8));

        final LinearLayout conteneurBooleen = new LinearLayout(context);
        conteneurBooleen.setOrientation(LinearLayout.HORIZONTAL);
        conteneurBooleen.setGravity(Gravity.CENTER);
        conteneurBooleen.setBackground(fond(context, Palette.fondNormal, Palette.bordure, 12));
        conteneurBooleen.setPadding(dp(context, 8), dp(context, 10), dp(context, 8), dp(context, 10));
        conteneurBooleen.setVisibility(View.GONE);

        final LinearLayout listeGauche = new LinearLayout(context);
        listeGauche.setOrientation(LinearLayout.VERTICAL);
        listeGauche.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 12));

        final LinearLayout barreParams = new LinearLayout(context);
        barreParams.setOrientation(LinearLayout.HORIZONTAL);
        barreParams.setPadding(0, dp(context, 2), 0, dp(context, 8));

        final TextView txtResumeExpression = new TextView(context);
        txtResumeExpression.setTextColor(Palette.texteSelectionne);
        txtResumeExpression.setTextSize(15f);
        txtResumeExpression.setBackground(fond(context, Palette.enTeteDialogues, Palette.bordure, 10));
        txtResumeExpression.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
        txtResumeExpression.getPaint().setFakeBoldText(true);

        final EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Palette.texteNormal);
        champSaisie.setHintTextColor(Palette.bordure);
        champSaisie.setBackground(fond(context, Palette.canvasFond, Palette.bordure, 10));
        champSaisie.setTextSize(17f);
        champSaisie.setGravity(Gravity.TOP | Gravity.START);
        champSaisie.setPadding(dp(context, 12), dp(context, 12), dp(context, 12), dp(context, 12));

        int hauteurChampDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lpChamp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hauteurChampDp);
        lpChamp.setMargins(0, dp(context, 6), 0, dp(context, 8));
        champSaisie.setLayoutParams(lpChamp);

        List<String> params = noeud.getNomsParametres();
        if (params != null && !params.isEmpty()) {
            champActif = params.get(0);
        }

        champSaisie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (champActif != null) {
                    noeud.setValeurParametre(champActif, s.toString());
                    mettreAJourResumeExpression(noeud, txtResumeExpression);
                }
            }
        });
// bas 1

// haut 2
        champSaisie.setOnClickListener(v -> {
            if (champActif != null) {
                String typeEditeur = noeud.getTypeEditeurParametre(champActif);
                String cheminProj = null;
                if (context instanceof InterfaceBlueprint) cheminProj = ((InterfaceBlueprint) context).cheminProjet;
                else if (context instanceof InterfaceEditeur) cheminProj = ((InterfaceEditeur) context).cheminProjet;
                        
                switch (typeEditeur) {
                    case NoeudBase.TYPE_COULEUR:
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setTitle(Traducteur.get("noeud_choisir_couleur"));
                        String[] couleurs = {"Bleu", "Rouge", "Vert", "Noir", "Blanc", "Jaune", "Magenta", "Cyan"};
                        builder.setItems(couleurs, (dialog, which) -> {
                            champSaisie.setText(couleurs[which]);
                            champSaisie.setSelection(champSaisie.getText().length());
                        });
                        builder.show();
                        break;
                    case NoeudBase.TYPE_CHOIX_LISTE:
                        android.app.AlertDialog.Builder builderListe = new android.app.AlertDialog.Builder(context);
                        builderListe.setTitle(Traducteur.get("noeud_choisir_option"));
                        List<String> optionsListe = noeud.getOptionsChoixListe(champActif);
                        String[] optionsArray = optionsListe.toArray(new String[0]);
                        builderListe.setItems(optionsArray, (dialog, which) -> {
                            champSaisie.setText(optionsArray[which]);
                            champSaisie.setSelection(champSaisie.getText().length());
                        });
                        builderListe.show();
                        break;
                    case NoeudBase.TYPE_CHOIX_IMAGE:
                        if (cheminProj != null) {
                            java.io.File dossierImages = new java.io.File(cheminProj, "assets_ludexa/Images");
                            List<String> images = new ArrayList<>();
                            images.add(Traducteur.get("noeud_aucune_image")); 
                            if (dossierImages.exists() && dossierImages.isDirectory()) {
                                java.io.File[] fichiers = dossierImages.listFiles();
                                if (fichiers != null) {
                                    for (java.io.File f : fichiers) {
                                        if (!f.isDirectory() && (f.getName().toLowerCase().endsWith(".png") || f.getName().toLowerCase().endsWith(".jpg"))) {
                                            images.add("assets_ludexa/Images/" + f.getName());
                                        }
                                    }
                                }
                            }
                            android.app.AlertDialog.Builder builderImage = new android.app.AlertDialog.Builder(context);
                            builderImage.setTitle(Traducteur.get("noeud_choisir_image"));
                            String[] imagesArray = images.toArray(new String[0]);
                            builderImage.setItems(imagesArray, (dialog, which) -> {
                                if (which == 0) champSaisie.setText("");
                                else champSaisie.setText(imagesArray[which]);
                                champSaisie.setSelection(champSaisie.getText().length());
                            });
                            builderImage.show();
                        }
                        break;
                    case NoeudBase.TYPE_CHOIX_DIALOGUE:
                        if (cheminProj != null) {
                            java.io.File fichierDialogues = new java.io.File(cheminProj, "assets_ludexa/Textes/dialogues.txt");
                            List<String> clesDialogue = new ArrayList<>();
                            if (fichierDialogues.exists()) {
                                try {
                                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierDialogues));
                                    String ligne;
                                    while ((ligne = br.readLine()) != null) {
                                        ligne = ligne.trim();
                                        if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                                        int idxEgal = ligne.indexOf('=');
                                        if (idxEgal > 0) {
                                            String cle = ligne.substring(0, idxEgal).trim();
                                            clesDialogue.add(cle);
                                        }
                                    }
                                    br.close();
                                } catch (Exception e) {}
                            }
                            if (clesDialogue.isEmpty()) clesDialogue.add(Traducteur.get("noeud_aucune_cle_dialogue"));
                            android.app.AlertDialog.Builder builderDial = new android.app.AlertDialog.Builder(context);
                            builderDial.setTitle(Traducteur.get("noeud_choisir_dialogue"));
                            String[] arrayDial = clesDialogue.toArray(new String[0]);
                            builderDial.setItems(arrayDial, (dialog, which) -> {
                                if (!arrayDial[which].equals(Traducteur.get("noeud_aucune_cle_dialogue"))) {
                                    champSaisie.setText(arrayDial[which]);
                                    champSaisie.setSelection(champSaisie.getText().length());
                                }
                            });
                            builderDial.show();
                        }
                        break;
                    case NoeudBase.TYPE_CHOIX_SON:
                        if (cheminProj != null) {
                            java.io.File dossierSons = new java.io.File(cheminProj, "assets_ludexa/Sons");
                            List<String> sons = new ArrayList<>();
                            sons.add(Traducteur.get("noeud_aucun_fichier_audio")); 
                            if (dossierSons.exists() && dossierSons.isDirectory()) {
                                java.io.File[] fichiers = dossierSons.listFiles();
                                if (fichiers != null) {
                                    for (java.io.File f : fichiers) {
                                        if (!f.isDirectory() && (f.getName().toLowerCase().endsWith(".mp3") || f.getName().toLowerCase().endsWith(".wav") || f.getName().toLowerCase().endsWith(".ogg"))) {
                                            sons.add("assets_ludexa/Sons/" + f.getName());
                                        }
                                    }
                                }
                            }
                            android.app.AlertDialog.Builder builderSon = new android.app.AlertDialog.Builder(context);
                            builderSon.setTitle(Traducteur.get("noeud_choisir_audio"));
                            String[] sonsArray = sons.toArray(new String[0]);
                            builderSon.setItems(sonsArray, (dialog, which) -> {
                                if (which == 0) champSaisie.setText("");
                                else champSaisie.setText(sonsArray[which]);
                                champSaisie.setSelection(champSaisie.getText().length());
                            });
                            builderSon.show();
                        }
                        break;
                    case NoeudBase.TYPE_CHOIX_FONCTION: 
                        if (cheminProj != null) {
                            java.io.File dossierFonctions = new java.io.File(cheminProj, "fonctions");
                            List<String> fonctions = new ArrayList<>();
                            if (dossierFonctions.exists() && dossierFonctions.isDirectory()) {
                                java.io.File[] fichiers = dossierFonctions.listFiles((dir, name) -> name.endsWith(".json"));
                                if (fichiers != null) {
                                    for (java.io.File f : fichiers) {
                                        fonctions.add(f.getName().replace(".json", ""));
                                    }
                                }
                            }
                            if (fonctions.isEmpty()) fonctions.add(Traducteur.get("noeud_aucune_fonction"));
                            android.app.AlertDialog.Builder builderFonc = new android.app.AlertDialog.Builder(context);
                            builderFonc.setTitle(Traducteur.get("noeud_choisir_fonction"));
                            String[] arrayFonc = fonctions.toArray(new String[0]);
                            builderFonc.setItems(arrayFonc, (dialog, which) -> {
                                if (!arrayFonc[which].equals(Traducteur.get("noeud_aucune_fonction"))) {
                                    champSaisie.setText(arrayFonc[which]);
                                    champSaisie.setSelection(champSaisie.getText().length());
                                }
                            });
                            builderFonc.show();
                        }
                        break;
                    case "CHOIX_ANIMATION":
                        if (cheminProj != null) {
                            java.io.File fichierAnimations = new java.io.File(cheminProj, "assets_ludexa/Textes/animations.txt");
                            List<String> clesAnimation = new ArrayList<>();
                            if (fichierAnimations.exists()) {
                                try {
                                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierAnimations));
                                    String ligne;
                                    while ((ligne = br.readLine()) != null) {
                                        ligne = ligne.trim();
                                        if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                                        int idxEgal = ligne.indexOf('=');
                                        if (idxEgal > 0) {
                                            String cle = ligne.substring(0, idxEgal).trim();
                                            clesAnimation.add(cle);
                                        }
                                    }
                                    br.close();
                                } catch (Exception e) {}
                            }
                            if (clesAnimation.isEmpty()) clesAnimation.add(Traducteur.get("noeud_aucune_animation"));
                            android.app.AlertDialog.Builder builderAnim = new android.app.AlertDialog.Builder(context);
                            builderAnim.setTitle(Traducteur.get("noeud_choisir_animation"));
                            String[] arrayAnim = clesAnimation.toArray(new String[0]);
                            builderAnim.setItems(arrayAnim, (dialog, which) -> {
                                if (!arrayAnim[which].equals(Traducteur.get("noeud_aucune_animation"))) {
                                    champSaisie.setText(arrayAnim[which]);
                                    champSaisie.setSelection(champSaisie.getText().length());
                                }
                            });
                            builderAnim.show();
                        }
                        break;
                }
            }
        });
// bas 2
// haut 3
        LinearLayout wrapperDroite = new LinearLayout(context);
        wrapperDroite.setOrientation(LinearLayout.VERTICAL);
        wrapperDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f));

        ScrollView scrollDroit = new ScrollView(context);
        scrollDroit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollDroit.setFillViewport(true);

        LinearLayout colonneDroite = new LinearLayout(context);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setBackground(fond(context, Palette.fondPanneaux, Palette.bordure, 12));
        colonneDroite.setPadding(dp(context, 12), dp(context, 12), dp(context, 12), dp(context, 12));

        HorizontalScrollView scrollCibles = new HorizontalScrollView(context);
        LinearLayout rangeeCibles = new LinearLayout(context);
        rangeeCibles.setOrientation(LinearLayout.HORIZONTAL);
        rangeeCibles.setPadding(0, 0, 0, dp(context, 10));

        if (noeud.requiertCibleObjet()) {
            TextView txtAfficheur = creerTextViewAfficheurCible(context);
            Button btnCible = creerBoutonSelectionCible(context, Traducteur.get("noeud_cible_objet_a"));
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleObjet() != null ? noeud.getCibleObjet().nom : null);
            btnCible.setOnClickListener(v -> {
                if (scene != null && scene.objets != null) {
                    String[] noms = new String[scene.objets.size()];
                    for (int i = 0; i < scene.objets.size(); i++) noms[i] = scene.objets.get(i).nom;
                    new android.app.AlertDialog.Builder(context).setTitle(Traducteur.get("noeud_choisir_cible_objet_a"))
                        .setItems(noms, (d, which) -> {
                            ObjetBase obj = scene.objets.get(which);
                            noeud.setCibleObjet(obj);
                            mettreAJourAfficheurCible(txtAfficheur, obj.nom);
                            mettreAJourResumeExpression(noeud, txtResumeExpression);
                        }).show();
                }
            });
            ajouterCoupleALaRangee(context, rangeeCibles, btnCible, txtAfficheur);
        }

        if (noeud.requiertCibleObjetB()) {
            TextView txtAfficheur = creerTextViewAfficheurCible(context);
            Button btnCible = creerBoutonSelectionCible(context, Traducteur.get("noeud_cible_objet_b"));
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleObjetB() != null ? noeud.getCibleObjetB().nom : null);
            btnCible.setOnClickListener(v -> {
                if (scene != null && scene.objets != null) {
                    String[] noms = new String[scene.objets.size()];
                    for (int i = 0; i < scene.objets.size(); i++) noms[i] = scene.objets.get(i).nom;
                    new android.app.AlertDialog.Builder(context).setTitle(Traducteur.get("noeud_choisir_cible_objet_b"))
                        .setItems(noms, (d, which) -> {
                            ObjetBase obj = scene.objets.get(which);
                            noeud.setCibleObjetB(obj);
                            mettreAJourAfficheurCible(txtAfficheur, obj.nom);
                            mettreAJourResumeExpression(noeud, txtResumeExpression);
                        }).show();
                }
            });
            ajouterCoupleALaRangee(context, rangeeCibles, btnCible, txtAfficheur);
        }

        if (noeud.requiertCibleVariable()) {
            TextView txtAfficheur = creerTextViewAfficheurCible(context);
            Button btnCible = creerBoutonSelectionCible(context, Traducteur.get("noeud_cible_variable"));
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleVariable() != null ? noeud.getCibleVariable().nom : null);
            btnCible.setOnClickListener(v -> {
                List<String> nomsVars = new ArrayList<>();
                List<Variable> refsVars = new ArrayList<>();
                if (scene != null && scene.variablesLocales != null) {
                    for (Variable var : scene.variablesLocales) {
                        nomsVars.add(var.nom + " (" + Traducteur.get("variable_locale") + ")");
                        refsVars.add(var);
                    }
                }
                List<Variable> globales = null;
                if (NoeudBase.contexteApplication != null) {
                    try {
                        java.lang.reflect.Field globalesField = NoeudBase.contexteApplication.getClass().getField("variablesGlobales");
                        globales = (List<Variable>) globalesField.get(NoeudBase.contexteApplication);
                    } catch (Exception e) {}
                }
                if (globales != null) {
                    for (Variable var : globales) {
                        nomsVars.add(var.nom + " (" + Traducteur.get("variable_globale") + ")");
                        refsVars.add(var);
                    }
                }
                if (!nomsVars.isEmpty()) {
                    new android.app.AlertDialog.Builder(context).setTitle(Traducteur.get("noeud_choisir_cible_variable"))
                        .setItems(nomsVars.toArray(new String[0]), (d, which) -> {
                            Variable var = refsVars.get(which);
                            noeud.setCibleVariable(var);
                            mettreAJourAfficheurCible(txtAfficheur, var.nom);
                            mettreAJourResumeExpression(noeud, txtResumeExpression);
                        }).show();
                }
            });
            ajouterCoupleALaRangee(context, rangeeCibles, btnCible, txtAfficheur);
        }

        if (noeud.requiertCibleScene()) {
            TextView txtAfficheur = creerTextViewAfficheurCible(context);
            Button btnCible = creerBoutonSelectionCible(context, Traducteur.get("noeud_cible_scene"));
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleScene() != null ? noeud.getCibleScene().nom : null);
            btnCible.setOnClickListener(v -> {
                List<Scene> tempScenes = null;
                if (NoeudBase.contexteApplication != null) {
                    try {
                        java.lang.reflect.Field scenesField = NoeudBase.contexteApplication.getClass().getField("listeScenes");
                        tempScenes = (List<Scene>) scenesField.get(NoeudBase.contexteApplication);
                    } catch (Exception e) {}
                }
                final List<Scene> scenesRecuperees = tempScenes;
                if (scenesRecuperees != null && !scenesRecuperees.isEmpty()) {
                    String[] noms = new String[scenesRecuperees.size()];
                    for (int i = 0; i < scenesRecuperees.size(); i++) noms[i] = scenesRecuperees.get(i).nom;
                    new android.app.AlertDialog.Builder(context).setTitle(Traducteur.get("noeud_choisir_cible_scene"))
                        .setItems(noms, (d, which) -> {
                            Scene s = scenesRecuperees.get(which);
                            noeud.setCibleScene(s);
                            mettreAJourAfficheurCible(txtAfficheur, s.nom);
                            mettreAJourResumeExpression(noeud, txtResumeExpression);
                        }).show();
                }
            });
            ajouterCoupleALaRangee(context, rangeeCibles, btnCible, txtAfficheur);
        }

        scrollCibles.addView(rangeeCibles);
        colonneDroite.addView(scrollCibles);
// bas 3

// haut 4
        colonneDroite.addView(barreParams);
        colonneDroite.addView(txtResumeExpression);

        if (params != null && !params.isEmpty()) {
            String valInit = noeud.getValeurParametre(champActif);
            champSaisie.setText(valInit != null ? valInit : "");
            champSaisie.setSelection(champSaisie.getText().length()); // Repositionnement sécurité

            for (String paramName : params) {
                Button btnParam = new Button(context);
                btnParam.setText(Traducteur.get(paramName)); 
                btnParam.setTag(paramName);
                
                btnParam.setAllCaps(false);
                btnParam.setTextSize(13f);
                btnParam.setTextColor(Palette.texteNormal);
                btnParam.setBackground(fond(context,
                        champActif.equals(paramName) ? Color.parseColor("#4CAF50") : Palette.boutonNormal,
                        Palette.bordure, 8));
                btnParam.setMinHeight(0);
                btnParam.setMinimumHeight(0);
                btnParam.setPadding(dp(context, 14), dp(context, 8), dp(context, 14), dp(context, 8));

                LinearLayout.LayoutParams btnParamsLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParamsLayout.setMargins(0, 0, dp(context, 8), 0);
                btnParam.setLayoutParams(btnParamsLayout);

                btnParam.setOnClickListener(v -> {
                    champActif = paramName;
                    String val = noeud.getValeurParametre(champActif);
                    champSaisie.setText(val != null ? val : "");
                    champSaisie.setSelection(champSaisie.getText().length()); // Place le curseur en fin de mot !

                    for (int i = 0; i < barreParams.getChildCount(); i++) {
                        View child = barreParams.getChildAt(i);
                        if (child instanceof Button && child.getTag() != null) {
                            if (child.getTag().toString().equals(champActif)) {
                                child.setBackground(fond(context, Color.parseColor("#4CAF50"), Palette.bordure, 8));
                            } else {
                                child.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                            }
                        }
                    }

                    appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
                    String type = noeud.getTypeEditeurParametre(champActif);
                    
                    if (NoeudBase.TYPE_COULEUR.equals(type) || NoeudBase.TYPE_CHOIX_LISTE.equals(type) || 
                        NoeudBase.TYPE_CHOIX_IMAGE.equals(type) || NoeudBase.TYPE_CHOIX_DIALOGUE.equals(type) ||
                        NoeudBase.TYPE_CHOIX_SON.equals(type) || NoeudBase.TYPE_CHOIX_FONCTION.equals(type) ||
                        "CHOIX_ANIMATION".equals(type)) { 
                        champSaisie.performClick();
                    }
                });
                barreParams.addView(btnParam);
            }
        }

        colonneDroite.addView(champSaisie);

        String[][] touchesCode = {
            {"1", "2", "3", "DEL"},
            {"4", "5", "6", "ESPACE"},
            {"7", "8", "9", "\""},
            {".", "0", "+", "-"},
            {"*", "/", "(", ")"},
            {">", "<", "=", "!"},
            {"||", "&&", "", ""},
            {"==", "!=", ">=", "<="},
            {"%", ",", "true", "false"}
        };

        int margeClavierDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, context.getResources().getDisplayMetrics());

        for (String[] ligne : touchesCode) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            for (String touche : ligne) {
                Button btn = new Button(context);
                btn.setText(touche);
                btn.setAllCaps(false);
                btn.setTextColor(Palette.texteNormal);
                btn.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btn.setMinHeight(0);
                btn.setMinimumHeight(0);
                btn.setPadding(0, dp(context, 12), 0, dp(context, 12));
                btn.setTextSize(14f);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                btnParams.setMargins(margeClavierDp, margeClavierDp, margeClavierDp, margeClavierDp);
                btn.setLayoutParams(btnParams);

                if (touche.isEmpty()) {
                    btn.setVisibility(android.view.View.INVISIBLE);
                } else {
                    if (touche.equals("DEL")) btn.setBackground(fond(context, Color.parseColor("#5c2323"), Palette.bordure, 8));

                    btn.setOnClickListener(v -> {
                        int start = champSaisie.getSelectionStart();
                        int end = champSaisie.getSelectionEnd();
                        
                        // Sécurité Android pour palier à la perte de curseur sur certains claviers virtuels
                        if (start < 0 || end < 0) {
                            start = champSaisie.getText().length();
                            end = champSaisie.getText().length();
                        }

                        if (touche.equals("DEL")) {
                            if (start > 0 && start == end) {
                                champSaisie.getText().delete(start - 1, start);
                            } else if (start != end) {
                                champSaisie.getText().delete(Math.min(start, end), Math.max(start, end));
                            }
                        } else {
                            String insert = touche.equals("ESPACE") ? " " : touche;
                            champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), insert, 0, insert.length());
                        }
                    });
                }
                rowLayout.addView(btn);
            }
            conteneurClavier.addView(rowLayout);
        }
        colonneDroite.addView(conteneurClavier);

        int margeBooleenDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());

        Button btnVrai = new Button(context);
        btnVrai.setText("Vrai (true)");
        btnVrai.setAllCaps(false);
        btnVrai.setBackground(fond(context, Color.parseColor("#4CAF50"), Palette.bordure, 10));
        btnVrai.setTextColor(Palette.texteNormal);
        btnVrai.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10));
        LinearLayout.LayoutParams paramVrai = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramVrai.setMargins(margeBooleenDp, margeBooleenDp, margeBooleenDp, margeBooleenDp);
        btnVrai.setLayoutParams(paramVrai);
        btnVrai.setOnClickListener(v -> {
            champSaisie.setText("true");
            champSaisie.setSelection(champSaisie.getText().length());
        });

        Button btnFaux = new Button(context);
        btnFaux.setText("Faux (false)");
        btnFaux.setAllCaps(false);
        btnFaux.setBackground(fond(context, Color.parseColor("#F44336"), Palette.bordure, 10));
        btnFaux.setTextColor(Palette.texteNormal);
        btnFaux.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10));
        LinearLayout.LayoutParams paramFaux = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramFaux.setMargins(margeBooleenDp, margeBooleenDp, margeBooleenDp, margeBooleenDp);
        btnFaux.setLayoutParams(paramFaux);
        btnFaux.setOnClickListener(v -> {
            champSaisie.setText("false");
            champSaisie.setSelection(champSaisie.getText().length());
        });

        conteneurBooleen.addView(btnVrai);
        conteneurBooleen.addView(btnFaux);
        colonneDroite.addView(conteneurBooleen);

        scrollDroit.addView(colonneDroite);
        wrapperDroite.addView(scrollDroit);
// bas 4

// haut 5
        LinearLayout colonneGauche = new LinearLayout(context);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpGauche = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
        lpGauche.setMargins(0, 0, dp(context, 8), 0);
        colonneGauche.setLayoutParams(lpGauche);
        colonneGauche.setBackground(fond(context, Palette.fondNormal, Palette.bordure, 12));

        ScrollView scrollGauche = new ScrollView(context);
        scrollGauche.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollGauche.setFillViewport(true);

        TextView titreItems = new TextView(context);
        titreItems.setText(Traducteur.get("noeud_objets_insertion"));
        titreItems.setTextColor(Palette.texteSelectionne);
        titreItems.setTextSize(15f);
        titreItems.setTypeface(null, android.graphics.Typeface.BOLD);
        titreItems.setGravity(Gravity.CENTER);
        titreItems.setBackground(fond(context, Palette.enTeteDialogues, Palette.bordure, 10));
        titreItems.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
        listeGauche.addView(titreItems);

        if (scene != null && scene.objets != null) {
            for (ObjetBase obj : scene.objets) {
                Button btnObj = new Button(context);
                btnObj.setText(obj.nom);
                btnObj.setAllCaps(false);
                btnObj.setTextSize(14f);
                btnObj.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                btnObj.setTextColor(Palette.texteNormal);
                btnObj.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btnObj.setMinHeight(0);
                btnObj.setMinimumHeight(0);
                btnObj.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
                LinearLayout.LayoutParams lpObj = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpObj.setMargins(0, dp(context, 4), 0, 0);
                btnObj.setLayoutParams(lpObj);

                btnObj.setOnClickListener(v -> {
                    int start = Math.max(champSaisie.getSelectionStart(), 0);
                    int end = Math.max(champSaisie.getSelectionEnd(), 0);
                    champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), obj.nom, 0, obj.nom.length());
                });
                listeGauche.addView(btnObj);
            }
        }

        TextView titreVars = new TextView(context);
        titreVars.setText(Traducteur.get("noeud_vars_locales_insertion"));
        titreVars.setTextColor(Palette.texteSelectionne);
        titreVars.setTextSize(15f);
        titreVars.setTypeface(null, android.graphics.Typeface.BOLD);
        titreVars.setGravity(Gravity.CENTER);
        titreVars.setBackground(fond(context, Palette.enTeteDialogues, Palette.bordure, 10));
        titreVars.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
        LinearLayout.LayoutParams lpTitreVars = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitreVars.setMargins(0, dp(context, 14), 0, 0);
        titreVars.setLayoutParams(lpTitreVars);
        listeGauche.addView(titreVars);

        View.OnClickListener insertionListener = v -> {
            String texteAInserer = v.getTag().toString();
            int start = Math.max(champSaisie.getSelectionStart(), 0);
            int end = Math.max(champSaisie.getSelectionEnd(), 0);
            champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), texteAInserer, 0, texteAInserer.length());
        };

        if (scene != null && scene.variablesLocales != null) {
            for (Variable var : scene.variablesLocales) {
                Button btnVar = new Button(context);
                btnVar.setText(var.nom);
                btnVar.setAllCaps(false);
                btnVar.setTextSize(14f);
                btnVar.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                btnVar.setTextColor(Palette.texteNormal);
                btnVar.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btnVar.setMinHeight(0);
                btnVar.setMinimumHeight(0);
                btnVar.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
                LinearLayout.LayoutParams lpVar = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpVar.setMargins(0, dp(context, 4), 0, 0);
                btnVar.setLayoutParams(lpVar);
                btnVar.setTag(var.nom);
                btnVar.setOnClickListener(insertionListener);
                listeGauche.addView(btnVar);
            }
        }

        TextView titreVarsGlobales = new TextView(context);
        titreVarsGlobales.setText(Traducteur.get("noeud_vars_globales_insertion"));
        titreVarsGlobales.setTextColor(Palette.texteSelectionne);
        titreVarsGlobales.setTextSize(15f);
        titreVarsGlobales.setTypeface(null, android.graphics.Typeface.BOLD);
        titreVarsGlobales.setGravity(Gravity.CENTER);
        titreVarsGlobales.setBackground(fond(context, Palette.enTeteDialogues, Palette.bordure, 10));
        titreVarsGlobales.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
        LinearLayout.LayoutParams lpTitreVarsG = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitreVarsG.setMargins(0, dp(context, 14), 0, 0);
        titreVarsGlobales.setLayoutParams(lpTitreVarsG);
        listeGauche.addView(titreVarsGlobales);

        List<Variable> variablesGlobalesRecuperees = null;
        if (NoeudBase.contexteApplication != null) {
            try {
                java.lang.reflect.Field globalesField = NoeudBase.contexteApplication.getClass().getField("variablesGlobales");
                @SuppressWarnings("unchecked")
                List<Variable> globales = (List<Variable>) globalesField.get(NoeudBase.contexteApplication);
                variablesGlobalesRecuperees = globales;
            } catch (Exception e) {}
        }

        if (variablesGlobalesRecuperees != null) {
            for (Variable var : variablesGlobalesRecuperees) {
                Button btnVarGlobale = new Button(context);
                btnVarGlobale.setText(var.nom);
                btnVarGlobale.setAllCaps(false);
                btnVarGlobale.setTextSize(14f);
                btnVarGlobale.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                btnVarGlobale.setTextColor(Palette.texteNormal);
                btnVarGlobale.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btnVarGlobale.setMinHeight(0);
                btnVarGlobale.setMinimumHeight(0);
                btnVarGlobale.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
                LinearLayout.LayoutParams lpVarG = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpVarG.setMargins(0, dp(context, 4), 0, 0);
                btnVarGlobale.setLayoutParams(lpVarG);
                btnVarGlobale.setTag(var.nom);
                btnVarGlobale.setOnClickListener(insertionListener);
                listeGauche.addView(btnVarGlobale);
            }
        }

        scrollGauche.addView(listeGauche);
        colonneGauche.addView(scrollGauche);

        root.addView(colonneGauche);
        root.addView(wrapperDroite);

        LinearLayout bottomBar = new LinearLayout(context);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        bottomBar.setBackgroundColor(Palette.fondPanneaux);
        bottomBar.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 12));

        Button btnCancel = new Button(context);
        btnCancel.setText(Traducteur.get("bouton_fermer"));
        btnCancel.setAllCaps(false);
        btnCancel.setTextSize(15f);
        btnCancel.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 10));
        btnCancel.setTextColor(Palette.texteNormal);
        btnCancel.setMinHeight(0);
        btnCancel.setMinimumHeight(0);
        btnCancel.setPadding(dp(context, 24), dp(context, 12), dp(context, 24), dp(context, 12));
        btnCancel.setOnClickListener(v -> {
            if (onValidate != null) onValidate.run();
            dismiss();
        });
        bottomBar.addView(btnCancel);

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.setBackgroundColor(Palette.fondPanneaux);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        grandLayout.addView(bottomBar);

        setContentView(grandLayout);

        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.95);
            int height = (int) (metrics.heightPixels * 0.90);
            window.setLayout(width, height);
        }

        appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
        mettreAJourResumeExpression(noeud, txtResumeExpression);
    }

    private Button creerBoutonSelectionCible(Context context, String texte) {
        Button btn = new Button(context);
        btn.setText(texte);
        btn.setAllCaps(false);
        btn.setTextSize(14f);
        btn.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
        btn.setTextColor(Color.parseColor("#FFD700"));
        btn.setMinHeight(0);
        btn.setMinimumHeight(0);
        btn.setPadding(dp(context, 14), dp(context, 9), dp(context, 14), dp(context, 9));
        return btn;
    }

    private TextView creerTextViewAfficheurCible(Context context) {
        TextView txt = new TextView(context);
        txt.setPadding(dp(context, 8), dp(context, 4), dp(context, 16), dp(context, 4));
        txt.setTextSize(15f);
        return txt;
    }

    private void mettreAJourAfficheurCible(TextView txt, String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            txt.setText(Traducteur.get("valeur_aucune"));
            txt.setTextColor(Color.parseColor("#888888"));
            txt.setTypeface(null, android.graphics.Typeface.ITALIC);
        } else {
            txt.setText(valeur);
            txt.setTextColor(Palette.texteSelectionne);
            txt.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    private void ajouterCoupleALaRangee(Context context, LinearLayout rangee, Button btn, TextView txt) {
        LinearLayout couple = new LinearLayout(context);
        couple.setOrientation(LinearLayout.HORIZONTAL);
        couple.setGravity(Gravity.CENTER_VERTICAL);
        couple.setBackground(fond(context, Palette.fondNormal, Palette.bordure, 10));
        couple.setPadding(dp(context, 6), dp(context, 6), dp(context, 6), dp(context, 6));
        LinearLayout.LayoutParams lpCouple = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpCouple.setMargins(0, 0, dp(context, 8), 0);
        couple.setLayoutParams(lpCouple);
        couple.addView(btn);
        couple.addView(txt);
        rangee.addView(couple);
    }

    private void mettreAJourResumeExpression(NoeudBase noeud, TextView txtResume) {
        boolean estComparaisonGenerique = false;
        if (noeud.requiertCibleVariable() && noeud.getNomsParametres() != null) {
            estComparaisonGenerique = noeud.getNomsParametres().contains("Opérateur")
                                   && noeud.getNomsParametres().contains("Valeur de comparaison");
        }

        if (noeud instanceof NoeudEventCollisionAB || noeud instanceof NoeudConditionSiObjetToucheZone) {
            txtResume.setVisibility(View.VISIBLE);
            String objNameA = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "[?]";
            String objNameB = (noeud.getCibleObjetB() != null && noeud.getCibleObjetB().nom != null) ? noeud.getCibleObjetB().nom : "[?]";
            txtResume.setText(Traducteur.get("resume_interaction") + " : " + objNameA + " <-> " + objNameB);
        }
        else if (noeud.nom.equals("Condition") || estComparaisonGenerique) {
            txtResume.setVisibility(View.VISIBLE);
            String varName = (noeud.getCibleVariable() != null && noeud.getCibleVariable().nom != null) ? noeud.getCibleVariable().nom : "[?]";
            String op = noeud.getValeurParametre("Opérateur");
            if (op == null || op.isEmpty()) op = "=";
            String val = noeud.getValeurParametre("Valeur de comparaison");
            if (val == null) val = "";
            txtResume.setText(Traducteur.get("resume_expression") + " : " + varName + " " + op + " " + val);
        } else if (noeud.requiertCibleVariable()) {
            txtResume.setVisibility(View.VISIBLE);
            String varName = (noeud.getCibleVariable() != null && noeud.getCibleVariable().nom != null) ? noeud.getCibleVariable().nom : "[?]";
            StringBuilder sb = new StringBuilder();
            if (noeud.getNomsParametres() != null) {
                for (String p : noeud.getNomsParametres()) {
                    String val = noeud.getValeurParametre(p);
                    if (val != null && !val.isEmpty()) {
                        if (sb.length() > 0) sb.append(" | ");
                        sb.append(Traducteur.get(p)).append(": ").append(val);
                    }
                }
            }
            txtResume.setText(Traducteur.get("resume_action") + " : " + varName + " = " + sb.toString());
        } else if (noeud.requiertCibleObjet()) {
            txtResume.setVisibility(View.VISIBLE);
            String objName = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "[?]";
            StringBuilder sb = new StringBuilder();
            if (noeud.getNomsParametres() != null) {
                for (String p : noeud.getNomsParametres()) {
                    String val = noeud.getValeurParametre(p);
                    if (val != null && !val.isEmpty()) {
                        if (sb.length() > 0) sb.append(" | ");
                        sb.append(Traducteur.get(p)).append(": ").append(val);
                    }
                }
            }
            txtResume.setText(Traducteur.get("resume_action_objet") + " : " + objName + (sb.length() == 0 ? "" : " -> " + sb.toString()));
        } else {
            txtResume.setVisibility(View.GONE);
        }
    }

    private void appliquerTypeEditeur(NoeudBase noeud, String nomParam, EditText champSaisie, View conteneurClavier, View conteneurBooleen) {
        String type = (nomParam != null) ? noeud.getTypeEditeurParametre(nomParam) : NoeudBase.TYPE_TEXTE_LIBRE;

        if (NoeudBase.TYPE_COULEUR.equals(type) || NoeudBase.TYPE_CHOIX_LISTE.equals(type) || 
            NoeudBase.TYPE_CHOIX_IMAGE.equals(type) || NoeudBase.TYPE_CHOIX_DIALOGUE.equals(type) ||
            NoeudBase.TYPE_CHOIX_SON.equals(type) || NoeudBase.TYPE_CHOIX_FONCTION.equals(type) ||
            "CHOIX_ANIMATION".equals(type)) { 
            
            champSaisie.setFocusable(false);
            champSaisie.setFocusableInTouchMode(false);
            champSaisie.setClickable(true);
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
            if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
            
        } else if ("TYPE_BOOLEEN".equals(type)) {
            champSaisie.setFocusable(false);
            champSaisie.setFocusableInTouchMode(false);
            champSaisie.setClickable(true);
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
            if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.VISIBLE);
            
        } else {
            champSaisie.setFocusable(true);
            champSaisie.setFocusableInTouchMode(true);
            champSaisie.setClickable(true);

            if (!noeud.utiliseClavierTexte()) {
                champSaisie.setShowSoftInputOnFocus(false);
                champSaisie.setInputType(InputType.TYPE_NULL);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
                if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
            } else {
                champSaisie.setShowSoftInputOnFocus(true);
                champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
                if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
                champSaisie.requestFocus();
            }
        }
    }
}
// bas 5



        



        




        


    
