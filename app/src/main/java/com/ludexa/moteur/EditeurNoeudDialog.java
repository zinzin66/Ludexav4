// haut 1
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

    // ---------- Helpers visuels (esthétique uniquement, repris de PanneauRessources) ----------

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
        setTitle("Edit Value - " + noeud.nom);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Palette.fondPanneaux);
        root.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 4));

        // =========================================================
        // DÉCLARATIONS COMMUNES
        // =========================================================
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

        champSaisie.setOnClickListener(v -> {
            if (champActif != null) {
                String typeEditeur = noeud.getTypeEditeurParametre(champActif);
                switch (typeEditeur) {
                    case NoeudBase.TYPE_COULEUR:
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setTitle("Choisir une couleur");
                        String[] couleurs = {"Bleu", "Rouge", "Vert", "Noir", "Blanc", "Jaune", "Magenta", "Cyan"};
                        builder.setItems(couleurs, (dialog, which) -> {
                            champSaisie.setText(couleurs[which]);
                        });
                        builder.show();
                        break;
                    case NoeudBase.TYPE_CHOIX_LISTE:
                        android.app.AlertDialog.Builder builderListe = new android.app.AlertDialog.Builder(context);
                        builderListe.setTitle("Choisir une option");
                        List<String> optionsListe = noeud.getOptionsChoixListe(champActif);
                        String[] optionsArray = optionsListe.toArray(new String[0]);
                        builderListe.setItems(optionsArray, (dialog, which) -> {
                            champSaisie.setText(optionsArray[which]);
                        });
                        builderListe.show();
                        break;
                }
            }
        });
// bas 1
// haut 2
        // =========================================================
        // PANNEAU DROIT (Édition & Cibles)
        // =========================================================
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

        // ---------------------------------------------------------
        // NOUVEAU : RANGÉE HORIZONTALE DES CIBLES (GÉNÉRIQUE)
        // ---------------------------------------------------------
        /*
         * GARANTIE : Ce mécanisme est désormais GÉNÉRIQUE.
         * L'ajout d'un futur nœud requérant 1 ou 2 cibles de type existant (Objet, Objet B, Variable, Scène)
         * construira dynamiquement cette rangée sans jamais avoir besoin de modifier ce fichier.
         * Seul un tout nouveau TYPE de cible (ex: Cible Fichier) nécessiterait l'ajout d'un nouveau bloc if ici.
         */
        HorizontalScrollView scrollCibles = new HorizontalScrollView(context);
        LinearLayout rangeeCibles = new LinearLayout(context);
        rangeeCibles.setOrientation(LinearLayout.HORIZONTAL);
        rangeeCibles.setPadding(0, 0, 0, dp(context, 10));

        if (noeud.requiertCibleObjet()) {
            TextView txtAfficheur = creerTextViewAfficheurCible(context);
            Button btnCible = creerBoutonSelectionCible(context, "Objet A");
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleObjet() != null ? noeud.getCibleObjet().nom : null);

            btnCible.setOnClickListener(v -> {
                if (scene != null && scene.objets != null) {
                    String[] noms = new String[scene.objets.size()];
                    for (int i = 0; i < scene.objets.size(); i++) noms[i] = scene.objets.get(i).nom;
                    new android.app.AlertDialog.Builder(context).setTitle("Choisir Cible Objet A")
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
            Button btnCible = creerBoutonSelectionCible(context, "Objet B");
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleObjetB() != null ? noeud.getCibleObjetB().nom : null);

            btnCible.setOnClickListener(v -> {
                if (scene != null && scene.objets != null) {
                    String[] noms = new String[scene.objets.size()];
                    for (int i = 0; i < scene.objets.size(); i++) noms[i] = scene.objets.get(i).nom;
                    new android.app.AlertDialog.Builder(context).setTitle("Choisir Cible Objet B")
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
            Button btnCible = creerBoutonSelectionCible(context, "Variable");
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleVariable() != null ? noeud.getCibleVariable().nom : null);

            btnCible.setOnClickListener(v -> {
                List<String> nomsVars = new ArrayList<>();
                List<Variable> refsVars = new ArrayList<>();

                if (scene != null && scene.variablesLocales != null) {
                    for (Variable var : scene.variablesLocales) {
                        nomsVars.add(var.nom + " (Locale)");
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
                        nomsVars.add(var.nom + " (Globale)");
                        refsVars.add(var);
                    }
                }
                if (!nomsVars.isEmpty()) {
                    new android.app.AlertDialog.Builder(context).setTitle("Choisir Cible Variable")
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
            Button btnCible = creerBoutonSelectionCible(context, "Scène");
            mettreAJourAfficheurCible(txtAfficheur, noeud.getCibleScene() != null ? noeud.getCibleScene().nom : null);

            btnCible.setOnClickListener(v -> {
                List<Scene> tempScenes = null;
                if (NoeudBase.contexteApplication != null) {
                    try {
                        java.lang.reflect.Field scenesField = NoeudBase.contexteApplication.getClass().getField("listeScenes");
                        tempScenes = (List<Scene>) scenesField.get(NoeudBase.contexteApplication);
                    } catch (Exception e) {}
                }

                // On crée une référence finale pour la lambda
                final List<Scene> scenesRecuperees = tempScenes;

                if (scenesRecuperees != null && !scenesRecuperees.isEmpty()) {
                    String[] noms = new String[scenesRecuperees.size()];
                    for (int i = 0; i < scenesRecuperees.size(); i++) noms[i] = scenesRecuperees.get(i).nom;
                    new android.app.AlertDialog.Builder(context).setTitle("Choisir Cible Scène")
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
// bas 2

// haut 3
        // Ajout du reste de l'interface droite
        colonneDroite.addView(barreParams);
        colonneDroite.addView(txtResumeExpression);

        if (params != null && !params.isEmpty()) {
            String valInit = noeud.getValeurParametre(champActif);
            champSaisie.setText(valInit != null ? valInit : "");

            for (String paramName : params) {
                Button btnParam = new Button(context);
                btnParam.setText(paramName);
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

                    for (int i = 0; i < barreParams.getChildCount(); i++) {
                        View child = barreParams.getChildAt(i);
                        if (child instanceof Button && params.contains(((Button)child).getText().toString())) {
                            if (((Button)child).getText().toString().equals(champActif)) {
                                child.setBackground(fond(context, Color.parseColor("#4CAF50"), Palette.bordure, 8));
                            } else {
                                child.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                            }
                        }
                    }

                    appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
                    String type = noeud.getTypeEditeurParametre(champActif);
                    if (NoeudBase.TYPE_COULEUR.equals(type) || NoeudBase.TYPE_CHOIX_LISTE.equals(type)) {
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
                        int start = Math.max(champSaisie.getSelectionStart(), 0);
                        int end = Math.max(champSaisie.getSelectionEnd(), 0);

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
        btnVrai.setOnClickListener(v -> champSaisie.setText("true"));

        Button btnFaux = new Button(context);
        btnFaux.setText("Faux (false)");
        btnFaux.setAllCaps(false);
        btnFaux.setBackground(fond(context, Color.parseColor("#F44336"), Palette.bordure, 10));
        btnFaux.setTextColor(Palette.texteNormal);
        btnFaux.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10));
        LinearLayout.LayoutParams paramFaux = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramFaux.setMargins(margeBooleenDp, margeBooleenDp, margeBooleenDp, margeBooleenDp);
        btnFaux.setLayoutParams(paramFaux);
        btnFaux.setOnClickListener(v -> champSaisie.setText("false"));

        conteneurBooleen.addView(btnVrai);
        conteneurBooleen.addView(btnFaux);
        colonneDroite.addView(conteneurBooleen);

        scrollDroit.addView(colonneDroite);
        wrapperDroite.addView(scrollDroit);
// bas 3

// haut 4
        // =========================================================
        // PANNEAU GAUCHE (Uniquement Insertion Texte)
        // =========================================================
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
        titreItems.setText("Objets (Insertion)");
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
        titreVars.setText("Variables Locales (Insertion)");
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
        titreVarsGlobales.setText("Variables Globales (Insertion)");
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

        TextView titreScenes = new TextView(context);
        titreScenes.setText("Scènes (Insertion)");
        titreScenes.setTextColor(Palette.texteSelectionne);
        titreScenes.setTextSize(15f);
        titreScenes.setTypeface(null, android.graphics.Typeface.BOLD);
        titreScenes.setGravity(Gravity.CENTER);
        titreScenes.setBackground(fond(context, Palette.enTeteDialogues, Palette.bordure, 10));
        titreScenes.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
        LinearLayout.LayoutParams lpTitreScenes = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitreScenes.setMargins(0, dp(context, 14), 0, 0);
        titreScenes.setLayoutParams(lpTitreScenes);
        listeGauche.addView(titreScenes);

        List<Scene> scenesRecuperees = null;
        if (NoeudBase.contexteApplication != null) {
            try {
                java.lang.reflect.Field scenesField = NoeudBase.contexteApplication.getClass().getField("listeScenes");
                @SuppressWarnings("unchecked")
                List<Scene> scenes = (List<Scene>) scenesField.get(NoeudBase.contexteApplication);
                scenesRecuperees = scenes;
            } catch (Exception e) {}
        }

        if (scenesRecuperees != null) {
            for (Scene s : scenesRecuperees) {
                Button btnScene = new Button(context);
                btnScene.setText(s.nom);
                btnScene.setAllCaps(false);
                btnScene.setTextSize(14f);
                btnScene.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                btnScene.setTextColor(Palette.texteNormal);
                btnScene.setBackground(fond(context, Palette.boutonNormal, Palette.bordure, 8));
                btnScene.setMinHeight(0);
                btnScene.setMinimumHeight(0);
                btnScene.setPadding(dp(context, 12), dp(context, 9), dp(context, 12), dp(context, 9));
                LinearLayout.LayoutParams lpScene = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpScene.setMargins(0, dp(context, 4), 0, 0);
                btnScene.setLayoutParams(lpScene);
                btnScene.setTag(s.nom);
                btnScene.setOnClickListener(insertionListener);
                listeGauche.addView(btnScene);
            }
        }

        scrollGauche.addView(listeGauche);
        colonneGauche.addView(scrollGauche);

        // =========================================================
        // AJOUT AU ROOT ET BARRE DU BAS
        // =========================================================
        root.addView(colonneGauche);
        root.addView(wrapperDroite);

        LinearLayout bottomBar = new LinearLayout(context);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        bottomBar.setBackgroundColor(Palette.fondPanneaux);
        bottomBar.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 12));

        Button btnCancel = new Button(context);
        btnCancel.setText("Fermer");
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

    // =========================================================
    // NOUVELLES MÉTHODES UTILITAIRES POUR LES CIBLES
    // =========================================================
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
            txt.setText("Aucune");
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

    // =========================================================
    // MÉTHODES UTILITAIRES DE LA DIALOG
    // =========================================================
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
            txtResume.setText("Interaction : " + objNameA + " <-> " + objNameB);
        }
        else if (noeud.nom.equals("Condition") || estComparaisonGenerique) {
            txtResume.setVisibility(View.VISIBLE);
            String varName = (noeud.getCibleVariable() != null && noeud.getCibleVariable().nom != null) ? noeud.getCibleVariable().nom : "[?]";
            String op = noeud.getValeurParametre("Opérateur");
            if (op == null || op.isEmpty()) op = "=";
            String val = noeud.getValeurParametre("Valeur de comparaison");
            if (val == null) val = "";
            txtResume.setText("Expression : " + varName + " " + op + " " + val);
        } else if (noeud.requiertCibleVariable()) {
            txtResume.setVisibility(View.VISIBLE);
            String varName = (noeud.getCibleVariable() != null && noeud.getCibleVariable().nom != null) ? noeud.getCibleVariable().nom : "[?]";
            String val = "";
            if (noeud.getNomsParametres() != null && !noeud.getNomsParametres().isEmpty()) {
                val = noeud.getValeurParametre(noeud.getNomsParametres().get(0));
            }
            txtResume.setText("Action : " + varName + " = " + (val != null ? val : ""));
        } else if (noeud.requiertCibleObjet()) {
            txtResume.setVisibility(View.VISIBLE);
            String objName = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "[?]";
            String val = "";
            if (noeud.getNomsParametres() != null && !noeud.getNomsParametres().isEmpty()) {
                val = noeud.getValeurParametre(noeud.getNomsParametres().get(0));
            }
            txtResume.setText("Action Objet : " + objName + (val.isEmpty() ? "" : " -> " + val));
        } else {
            txtResume.setVisibility(View.GONE);
        }
    }

    private void appliquerTypeEditeur(NoeudBase noeud, String nomParam, EditText champSaisie, View conteneurClavier, View conteneurBooleen) {
        String type = (nomParam != null) ? noeud.getTypeEditeurParametre(nomParam) : NoeudBase.TYPE_TEXTE_LIBRE;

        if (NoeudBase.TYPE_COULEUR.equals(type) || NoeudBase.TYPE_CHOIX_LISTE.equals(type)) {
            champSaisie.setFocusable(false);
            champSaisie.setFocusableInTouchMode(false);
            champSaisie.setClickable(true);
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
            if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
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
// bas 4
                





        




        



    
