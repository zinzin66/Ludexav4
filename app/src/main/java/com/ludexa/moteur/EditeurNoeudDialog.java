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
import android.widget.TextView;
import android.widget.Toast; 
import java.util.List;

public class EditeurNoeudDialog extends Dialog {
    
    private String champActif = null;
    private boolean modeCible = false; // Mode de ciblage

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
        
        final Button btnCible = new Button(context);

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
        // PANNEAU DROIT (Édition)
        // =========================================================
        LinearLayout wrapperDroite = new LinearLayout(context);
        wrapperDroite.setOrientation(LinearLayout.VERTICAL);
        // MODIFICATION : Poids augmenté (de 1.2f à 1.5f) pour élargir la zone de droite
        wrapperDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f));

        ScrollView scrollDroit = new ScrollView(context);
        scrollDroit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollDroit.setFillViewport(true);

        LinearLayout colonneDroite = new LinearLayout(context);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setPadding(15, 15, 15, 15); 

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
                    modeCible = false;
                    champActif = paramName;
                    String val = noeud.getValeurParametre(champActif);
                    champSaisie.setText(val != null ? val : "");
                    
                    btnCible.setBackgroundColor(Palette.boutonNormal);
                    btnCible.setTextColor(Color.parseColor("#FFD700"));

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
// bas 2
   // haut 3
        // =========================================================
        // PANNEAU GAUCHE (Listes/Cibles)
        // =========================================================
        LinearLayout colonneGauche = new LinearLayout(context);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        // MODIFICATION : Poids réduit (de 0.8f à 0.5f) pour affiner la colonne des listes
        colonneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
        colonneGauche.setBackgroundColor(Palette.fondPanneaux);
        
        ScrollView scrollGauche = new ScrollView(context);
        scrollGauche.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollGauche.setFillViewport(true);

        // AJOUT : Prise en compte de requiertCibleScene
        if (noeud.requiertCibleObjet() || noeud.requiertCibleVariable() || noeud.requiertCibleScene()) {
            btnCible.setText("Cible");
            btnCible.setTextColor(Color.parseColor("#FFD700"));
            btnCible.setBackgroundColor(Palette.boutonNormal);
            
            LinearLayout.LayoutParams btnCibleLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnCibleLayout.setMargins(20, 20, 20, 10);
            btnCible.setLayoutParams(btnCibleLayout);
            
            btnCible.setOnClickListener(v -> {
                modeCible = true;
                btnCible.setBackgroundColor(Color.parseColor("#FFD700"));
                btnCible.setTextColor(Color.BLACK); 
                
                for (int i = 0; i < barreParams.getChildCount(); i++) {
                    View child = barreParams.getChildAt(i);
                    if (child instanceof Button && child != btnCible) {
                        child.setBackgroundColor(Palette.boutonNormal);
                    }
                }
            });
            listeGauche.addView(btnCible);
        }

        final TextView txtCibleActuelle = new TextView(context);
        txtCibleActuelle.setTextColor(Palette.texteSelectionne);
        txtCibleActuelle.setPadding(20, 0, 20, 20);
        txtCibleActuelle.setTextSize(16);
        
        if (noeud.requiertCibleObjet()) {
            txtCibleActuelle.setText("Cible : " + (noeud.getCibleObjet() != null ? noeud.getCibleObjet().nom : "Aucune"));
            listeGauche.addView(txtCibleActuelle);
        } else if (noeud.requiertCibleVariable()) {
            txtCibleActuelle.setText("Cible : " + (noeud.getCibleVariable() != null ? noeud.getCibleVariable().nom : "Aucune"));
            listeGauche.addView(txtCibleActuelle);
        // AJOUT : Affichage de la cible actuelle Scene
        } else if (noeud.requiertCibleScene()) {
            txtCibleActuelle.setText("Cible : " + (noeud.getCibleScene() != null ? noeud.getCibleScene().nom : "Aucune"));
            listeGauche.addView(txtCibleActuelle);
        }

        TextView titreItems = new TextView(context);
        titreItems.setText("Items (Cible Objet)");
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
                    if (modeCible && noeud.requiertCibleObjet()) {
                        noeud.setCibleObjet(obj);
                        txtCibleActuelle.setText("Cible : " + obj.nom);
                        modeCible = false; 
                        
                        btnCible.setBackgroundColor(Palette.boutonNormal);
                        btnCible.setTextColor(Color.parseColor("#FFD700"));
                        
                        if (champActif != null) {
                            for (int i = 0; i < barreParams.getChildCount(); i++) {
                                View child = barreParams.getChildAt(i);
                                if (child instanceof Button && champActif.equals(((Button)child).getText().toString())) {
                                    child.setBackgroundColor(Color.parseColor("#4CAF50"));
                                }
                            }
                        }
                        appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
                        mettreAJourResumeExpression(noeud, txtResumeExpression);
                    } else {
                        int start = Math.max(champSaisie.getSelectionStart(), 0);
                        int end = Math.max(champSaisie.getSelectionEnd(), 0);
                        champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), obj.nom, 0, obj.nom.length());
                    }
                });
                listeGauche.addView(btnObj);
            }
        }

        TextView titreVars = new TextView(context);
        titreVars.setText("Variables");
        titreVars.setTextColor(Palette.texteNormal);
        titreVars.setGravity(Gravity.CENTER);
        titreVars.setBackgroundColor(Palette.enTeteDialogues);
        titreVars.setPadding(10, 30, 10, 15);
        listeGauche.addView(titreVars);

        View.OnClickListener varClickListener = v -> {
            Variable var = (Variable) v.getTag();
            if (modeCible && noeud.requiertCibleVariable()) {
                noeud.setCibleVariable(var);
                txtCibleActuelle.setText("Cible : " + var.nom);
                modeCible = false; 
                
                btnCible.setBackgroundColor(Palette.boutonNormal);
                btnCible.setTextColor(Color.parseColor("#FFD700"));
                
                if (champActif != null) {
                    for (int i = 0; i < barreParams.getChildCount(); i++) {
                        View child = barreParams.getChildAt(i);
                        if (child instanceof Button && champActif.equals(((Button)child).getText().toString())) {
                            child.setBackgroundColor(Color.parseColor("#4CAF50"));
                        }
                    }
                }
                appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
                mettreAJourResumeExpression(noeud, txtResumeExpression);
            } else {
                int start = Math.max(champSaisie.getSelectionStart(), 0);
                int end = Math.max(champSaisie.getSelectionEnd(), 0);
                champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), var.nom, 0, var.nom.length());
            }
        };

        if (scene != null && scene.variablesLocales != null) {
            for (Variable var : scene.variablesLocales) {
                Button btnVar = new Button(context);
                btnVar.setText(var.nom + " (Locale)");
                btnVar.setTextColor(Palette.texteNormal);
                btnVar.setBackgroundColor(Color.TRANSPARENT); 
                btnVar.setTag(var);
                btnVar.setOnClickListener(varClickListener);
                listeGauche.addView(btnVar);
            }
        }

        TextView titreVarsGlobales = new TextView(context);
        titreVarsGlobales.setText("Variables Globales");
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
                btnVarGlobale.setText(var.nom + " (Globale)");
                btnVarGlobale.setTextColor(Palette.texteNormal);
                btnVarGlobale.setBackgroundColor(Color.TRANSPARENT);
                btnVarGlobale.setTag(var);
                btnVarGlobale.setOnClickListener(varClickListener);
                listeGauche.addView(btnVarGlobale);
            }
        }

        TextView titreScenes = new TextView(context);
        titreScenes.setText("Scènes");
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
                btnScene.setText(s.nom + " (Scène)");
                btnScene.setTextColor(Palette.texteNormal);
                btnScene.setBackgroundColor(Color.TRANSPARENT); 
                
                // AJOUT : Remplacement du comportement au clic
                btnScene.setOnClickListener(v -> {
                    if (modeCible && noeud.requiertCibleScene()) {
                        noeud.setCibleScene(s);
                        txtCibleActuelle.setText("Cible : " + s.nom);
                        modeCible = false;
                        btnCible.setBackgroundColor(Palette.boutonNormal);
                        btnCible.setTextColor(Color.parseColor("#FFD700"));
                    } else {
                        int start = Math.max(champSaisie.getSelectionStart(), 0);
                        int end = Math.max(champSaisie.getSelectionEnd(), 0);
                        champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), s.nom, 0, s.nom.length());
                    }
                });
                
                listeGauche.addView(btnScene);
            }
        }

        scrollGauche.addView(listeGauche);
        colonneGauche.addView(scrollGauche);

        // =========================================================
        // AJOUT AU ROOT
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

    private void mettreAJourResumeExpression(NoeudBase noeud, TextView txtResume) {
        boolean estComparaisonGenerique = false;
        if (noeud.requiertCibleVariable() && noeud.getNomsParametres() != null) {
            estComparaisonGenerique = noeud.getNomsParametres().contains("Opérateur") 
                                   && noeud.getNomsParametres().contains("Valeur de comparaison");
        }

        if (noeud.nom.equals("Condition") || estComparaisonGenerique) {
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

    private void majInterfacePourVariable(Variable var, EditText champSaisie, View conteneurClavier, View conteneurBooleen) {
        if (var == null) return;
        
        if ("CHIFFRE".equals(var.type)) {
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
            if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
        } else if ("TEXTE".equals(var.type)) {
            champSaisie.setShowSoftInputOnFocus(true);
            champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
            if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
            champSaisie.requestFocus();
        } else if ("BOOLEEN".equals(var.type)) {
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
            if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.VISIBLE);
        }
    }
}
// bas 3
                


    
