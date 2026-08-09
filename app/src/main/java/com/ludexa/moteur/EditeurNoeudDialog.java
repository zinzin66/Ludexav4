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

    public EditeurNoeudDialog(Context context, NoeudBase noeud, Scene scene, Runnable onValidate) {
        super(context);
        setTitle("Edit Value - " + noeud.nom);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Palette.fondPanneaux); 

        // =========================================================
        // DÉCLARATIONS COMMUNES
        // =========================================================
        final LinearLayout conteneurClavier = new LinearLayout(context);
        conteneurClavier.setOrientation(LinearLayout.VERTICAL);
        conteneurClavier.setPadding(0, 10, 0, 0); 

        final LinearLayout conteneurBooleen = new LinearLayout(context);
        conteneurBooleen.setOrientation(LinearLayout.HORIZONTAL);
        conteneurBooleen.setGravity(Gravity.CENTER);
        conteneurBooleen.setPadding(0, 15, 0, 0);
        conteneurBooleen.setVisibility(View.GONE);

        final LinearLayout listeGauche = new LinearLayout(context);
        listeGauche.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout barreParams = new LinearLayout(context);
        barreParams.setOrientation(LinearLayout.HORIZONTAL);
        barreParams.setPadding(0, 0, 0, 10);

        final TextView txtResumeExpression = new TextView(context);
        txtResumeExpression.setTextColor(Palette.texteSelectionne);
        txtResumeExpression.setTextSize(16); 
        txtResumeExpression.setPadding(10, 0, 10, 5);
        txtResumeExpression.getPaint().setFakeBoldText(true);

        final EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Palette.texteNormal);
        champSaisie.setBackgroundColor(Palette.canvasFond);
        champSaisie.setTextSize(18); 
        champSaisie.setGravity(Gravity.TOP | Gravity.START);
        champSaisie.setPadding(15, 15, 15, 15);
        
        int hauteurChampDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources().getDisplayMetrics());
        champSaisie.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hauteurChampDp));

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
        // fin 1
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
        colonneDroite.setPadding(15, 15, 15, 15); 

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
        rangeeCibles.setPadding(0, 0, 0, 20);

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
                List<Scene> scenesRecuperees = null;
                if (NoeudBase.contexteApplication != null) {
                    try {
                        java.lang.reflect.Field scenesField = NoeudBase.contexteApplication.getClass().getField("listeScenes");
                        scenesRecuperees = (List<Scene>) scenesField.get(NoeudBase.contexteApplication);
                    } catch (Exception e) {}
                }
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
        
        
        // haut3

                // Ajout du reste de l'interface droite
        colonneDroite.addView(barreParams);
        colonneDroite.addView(txtResumeExpression);

        if (params != null && !params.isEmpty()) {
            String valInit = noeud.getValeurParametre(champActif);
            champSaisie.setText(valInit != null ? valInit : "");

            for (String paramName : params) {
                Button btnParam = new Button(context);
                btnParam.setText(paramName);
                btnParam.setTextColor(Palette.texteNormal);
                btnParam.setBackgroundColor(champActif.equals(paramName) ? Color.parseColor("#4CAF50") : Palette.boutonNormal);
                
                LinearLayout.LayoutParams btnParamsLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParamsLayout.setMargins(0, 0, 15, 0);
                btnParam.setLayoutParams(btnParamsLayout);

                btnParam.setOnClickListener(v -> {
                    champActif = paramName;
                    String val = noeud.getValeurParametre(champActif);
                    champSaisie.setText(val != null ? val : "");
                    
                    for (int i = 0; i < barreParams.getChildCount(); i++) {
                        View child = barreParams.getChildAt(i);
                        if (child instanceof Button && params.contains(((Button)child).getText().toString())) {
                            if (((Button)child).getText().toString().equals(champActif)) {
                                child.setBackgroundColor(Color.parseColor("#4CAF50"));
                            } else {
                                child.setBackgroundColor(Palette.boutonNormal);
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

        int margeClavierDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

        for (String[] ligne : touchesCode) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            
            for (String touche : ligne) {
                Button btn = new Button(context);
                btn.setText(touche);
                btn.setTextColor(Palette.texteNormal);
                btn.setBackgroundColor(Palette.boutonNormal);
                
                btn.setMinHeight(0);
                btn.setMinimumHeight(0);
                btn.setPadding(0, 15, 0, 15);
                btn.setTextSize(14); 
                
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                btnParams.setMargins(margeClavierDp, margeClavierDp, margeClavierDp, margeClavierDp);
                btn.setLayoutParams(btnParams);

                if (touche.isEmpty()) {
                    btn.setVisibility(android.view.View.INVISIBLE);
                } else {
                    if (touche.equals("DEL")) btn.setBackgroundColor(Color.parseColor("#5c2323")); 
                    
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
        btnVrai.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnVrai.setTextColor(Palette.texteNormal);
        LinearLayout.LayoutParams paramVrai = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramVrai.setMargins(margeBooleenDp, margeBooleenDp, margeBooleenDp, margeBooleenDp);
        btnVrai.setLayoutParams(paramVrai);
        btnVrai.setOnClickListener(v -> champSaisie.setText("true"));

        Button btnFaux = new Button(context);
        btnFaux.setText("Faux (false)");
        btnFaux.setBackgroundColor(Color.parseColor("#F44336"));
        btnFaux.setTextColor(Palette.texteNormal);
        LinearLayout.LayoutParams paramFaux = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramFaux.setMargins(margeBooleenDp, margeBooleenDp, margeBooleenDp, margeBooleenDp);
        btnFaux.setLayoutParams(paramFaux);
        btnFaux.setOnClickListener(v -> champSaisie.setText("false"));

        conteneurBooleen.addView(btnVrai);
        conteneurBooleen.addView(btnFaux);
        colonneDroite.addView(conteneurBooleen);

        scrollDroit.addView(colonneDroite);
        wrapperDroite.addView(scrollDroit);

// haut 4

        // =========================================================
        // PANNEAU GAUCHE (Uniquement Insertion Texte)
        // =========================================================
        LinearLayout colonneGauche = new LinearLayout(context);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        colonneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
        colonneGauche.setBackgroundColor(Palette.fondPanneaux);
        
        ScrollView scrollGauche = new ScrollView(context);
        scrollGauche.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollGauche.setFillViewport(true);

        TextView titreItems = new TextView(context);
        titreItems.setText("Objets (Insertion)");
        titreItems.setTextColor(Palette.texteNormal);
        titreItems.setGravity(Gravity.CENTER);
        titreItems.setBackgroundColor(Palette.enTeteDialogues); 
        titreItems.setPadding(10, 15, 10, 15);
        listeGauche.addView(titreItems);

        if (scene != null && scene.objets != null) {
            for (ObjetBase obj : scene.objets) {
                Button btnObj = new Button(context);
                btnObj.setText(obj.nom);
                btnObj.setTextColor(Palette.texteNormal);
                btnObj.setBackgroundColor(Color.TRANSPARENT);
                
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
        titreVars.setTextColor(Palette.texteNormal);
        titreVars.setGravity(Gravity.CENTER);
        titreVars.setBackgroundColor(Palette.enTeteDialogues);
        titreVars.setPadding(10, 30, 10, 15);
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
                btnVar.setTextColor(Palette.texteNormal);
                btnVar.setBackgroundColor(Color.TRANSPARENT); 
                btnVar.setTag(var.nom);
                btnVar.setOnClickListener(insertionListener);
                listeGauche.addView(btnVar);
            }
        }

        TextView titreVarsGlobales = new TextView(context);
        titreVarsGlobales.setText("Variables Globales (Insertion)");
        titreVarsGlobales.setTextColor(Palette.texteNormal);
        titreVarsGlobales.setGravity(Gravity.CENTER);
        titreVarsGlobales.setBackgroundColor(Palette.enTeteDialogues);
        titreVarsGlobales.setPadding(10, 15, 10, 15);
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
                btnVarGlobale.setTextColor(Palette.texteNormal);
                btnVarGlobale.setBackgroundColor(Color.TRANSPARENT);
                btnVarGlobale.setTag(var.nom);
                btnVarGlobale.setOnClickListener(insertionListener);
                listeGauche.addView(btnVarGlobale);
            }
        }

        TextView titreScenes = new TextView(context);
        titreScenes.setText("Scènes (Insertion)");
        titreScenes.setTextColor(Palette.texteNormal);
        titreScenes.setGravity(Gravity.CENTER);
        titreScenes.setBackgroundColor(Palette.enTeteDialogues); 
        titreScenes.setPadding(10, 30, 10, 15);
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
                btnScene.setTextColor(Palette.texteNormal);
                btnScene.setBackgroundColor(Color.TRANSPARENT); 
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
        bottomBar.setPadding(20, 20, 20, 20);

        Button btnCancel = new Button(context);
        btnCancel.setText("Fermer");
        btnCancel.setBackgroundColor(Palette.boutonNormal);
        btnCancel.setTextColor(Palette.texteNormal);
        btnCancel.setOnClickListener(v -> {
            if (onValidate != null) onValidate.run();
            dismiss();
        });
        bottomBar.addView(btnCancel);

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
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


    // haut 5

    // =========================================================
    // NOUVELLES MÉTHODES UTILITAIRES POUR LES CIBLES
    // =========================================================
    private Button creerBoutonSelectionCible(Context context, String texte) {
        Button btn = new Button(context);
        btn.setText(texte);
        btn.setBackgroundColor(Palette.boutonNormal);
        btn.setTextColor(Color.parseColor("#FFD700"));
        return btn;
    }

    private TextView creerTextViewAfficheurCible(Context context) {
        TextView txt = new TextView(context);
        txt.setPadding(15, 0, 30, 0);
        txt.setTextSize(16);
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


    
        
        
        





        
        
        
        
