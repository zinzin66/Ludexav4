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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast; 
import java.util.List;

public class EditeurNoeudDialog extends Dialog {
    
    private String champActif = null;

    public EditeurNoeudDialog(Context context, NoeudBase noeud, Scene scene, Runnable onValidate) {
        super(context);
        setTitle("Edit Value - " + noeud.nom);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Palette.fondPanneaux); 
        root.setLayoutParams(new ViewGroup.LayoutParams(1200, 800));

        final LinearLayout conteneurClavier = new LinearLayout(context);
        conteneurClavier.setOrientation(LinearLayout.VERTICAL);
        conteneurClavier.setPadding(0, 20, 0, 0);

        final LinearLayout conteneurBooleen = new LinearLayout(context);
        conteneurBooleen.setOrientation(LinearLayout.HORIZONTAL);
        conteneurBooleen.setGravity(Gravity.CENTER);
        conteneurBooleen.setPadding(0, 20, 0, 0);
        conteneurBooleen.setVisibility(View.GONE);

        // =========================================================
        // PANNEAU GAUCHE : Zone de texte et Clavier Code
        // =========================================================
        LinearLayout zoneGauche = new LinearLayout(context);
        zoneGauche.setOrientation(LinearLayout.VERTICAL);
        zoneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f));
        zoneGauche.setPadding(20, 20, 20, 20);

        LinearLayout barreParams = new LinearLayout(context);
        barreParams.setOrientation(LinearLayout.HORIZONTAL);
        barreParams.setPadding(0, 0, 0, 15);
        zoneGauche.addView(barreParams);

        // NOUVEAU : Résumé de l'expression en cours
        final TextView txtResumeExpression = new TextView(context);
        txtResumeExpression.setTextColor(Palette.texteSelectionne);
        txtResumeExpression.setTextSize(18);
        txtResumeExpression.setPadding(15, 0, 15, 10);
        // CORRECTION ICI : passage par getPaint() pour le texte en gras
        txtResumeExpression.getPaint().setFakeBoldText(true);
        zoneGauche.addView(txtResumeExpression);

        EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Palette.texteNormal);
        champSaisie.setBackgroundColor(Palette.canvasFond);
        champSaisie.setTextSize(20);
        champSaisie.setGravity(Gravity.TOP | Gravity.START);
        champSaisie.setPadding(15, 15, 15, 15);
        champSaisie.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        
        List<String> params = noeud.getNomsParametres();
        if (params != null && !params.isEmpty()) {
            champActif = params.get(0);
        }

        // NOUVEAU : TextWatcher pour mise à jour dynamique du nœud et du résumé
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

            Button btnDepuisObjet = new Button(context);
            btnDepuisObjet.setText("Depuis objet...");
            btnDepuisObjet.setTextColor(Color.parseColor("#FFD700")); 
            btnDepuisObjet.setBackgroundColor(Palette.boutonNormal);
            btnDepuisObjet.setOnClickListener(v -> Toast.makeText(context, "À venir...", Toast.LENGTH_SHORT).show());
            barreParams.addView(btnDepuisObjet);
        }

        zoneGauche.addView(champSaisie);

        String[][] touchesCode = {
            {"1", "2", "3", "DEL"},
            {"4", "5", "6", "ESPACE"},
            {"7", "8", "9", "\""},
            {".", "0", "+", "-"},
            {"*", "/", "(", ")"},
            {">", "<", "=", "!"},
            {"||", "&&", "", ""}
        };

        for (String[] ligne : touchesCode) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            
            for (String touche : ligne) {
                Button btn = new Button(context);
                btn.setText(touche);
                btn.setTextColor(Palette.texteNormal);
                btn.setBackgroundColor(Palette.boutonNormal);
                
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                btnParams.setMargins(5, 5, 5, 5);
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
        zoneGauche.addView(conteneurClavier);

        Button btnVrai = new Button(context);
        btnVrai.setText("Vrai (true)");
        btnVrai.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnVrai.setTextColor(Palette.texteNormal);
        LinearLayout.LayoutParams paramVrai = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramVrai.setMargins(10, 10, 10, 10);
        btnVrai.setLayoutParams(paramVrai);
        btnVrai.setOnClickListener(v -> champSaisie.setText("true"));

        Button btnFaux = new Button(context);
        btnFaux.setText("Faux (false)");
        btnFaux.setBackgroundColor(Color.parseColor("#F44336"));
        btnFaux.setTextColor(Palette.texteNormal);
        LinearLayout.LayoutParams paramFaux = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramFaux.setMargins(10, 10, 10, 10);
        btnFaux.setLayoutParams(paramFaux);
        btnFaux.setOnClickListener(v -> champSaisie.setText("false"));

        conteneurBooleen.addView(btnVrai);
        conteneurBooleen.addView(btnFaux);
        zoneGauche.addView(conteneurBooleen);
// bas 2


// haut 3
        // =========================================================
        // PANNEAU DROIT : Listes (Items, Variables...)
        // =========================================================
        LinearLayout zoneDroite = new LinearLayout(context);
        zoneDroite.setOrientation(LinearLayout.VERTICAL);
        zoneDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
        zoneDroite.setBackgroundColor(Palette.fondPanneaux);
        
        ScrollView scrollDroite = new ScrollView(context);
        LinearLayout listeDroite = new LinearLayout(context);
        listeDroite.setOrientation(LinearLayout.VERTICAL);

        final TextView txtCibleActuelle = new TextView(context);
        txtCibleActuelle.setTextColor(Palette.texteSelectionne);
        txtCibleActuelle.setPadding(20, 20, 20, 20);
        txtCibleActuelle.setTextSize(16);
        
        if (noeud.requiertCibleObjet()) {
            txtCibleActuelle.setText("Cible : " + (noeud.getCibleObjet() != null ? noeud.getCibleObjet().nom : "Aucune"));
            listeDroite.addView(txtCibleActuelle);
        } else if (noeud.requiertCibleVariable()) {
            txtCibleActuelle.setText("Cible : " + (noeud.getCibleVariable() != null ? noeud.getCibleVariable().nom : "Aucune"));
            listeDroite.addView(txtCibleActuelle);
        }

        // --- RESTAURATION : Section ITEMS (Cible Objet) ---
        TextView titreItems = new TextView(context);
        titreItems.setText("Items (Cible Objet)");
        titreItems.setTextColor(Palette.texteNormal);
        titreItems.setGravity(Gravity.CENTER);
        titreItems.setBackgroundColor(Palette.enTeteDialogues); 
        titreItems.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreItems);

        if (scene != null && scene.objets != null) {
            for (ObjetBase obj : scene.objets) {
                Button btnObj = new Button(context);
                btnObj.setText(obj.nom);
                btnObj.setTextColor(Palette.texteNormal);
                btnObj.setBackgroundColor(Color.TRANSPARENT);
                btnObj.setOnClickListener(v -> {
                    if (noeud.requiertCibleObjet()) {
                        noeud.setCibleObjet(obj);
                        txtCibleActuelle.setText("Cible : " + obj.nom);
                        mettreAJourResumeExpression(noeud, txtResumeExpression); // Mise à jour du résumé
                    }
                });
                listeDroite.addView(btnObj);
            }
        }

        // Section : VARIABLES
        TextView titreVars = new TextView(context);
        titreVars.setText("Variables");
        titreVars.setTextColor(Palette.texteNormal);
        titreVars.setGravity(Gravity.CENTER);
        titreVars.setBackgroundColor(Palette.enTeteDialogues);
        titreVars.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreVars);

        View.OnClickListener varClickListener = v -> {
            Variable var = (Variable) v.getTag();
            if (noeud.requiertCibleVariable()) {
                noeud.setCibleVariable(var);
                txtCibleActuelle.setText("Cible : " + var.nom);
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
                listeDroite.addView(btnVar);
            }
        }

        // --- RESTAURATION : Section SCÈNES ---
        TextView titreScenes = new TextView(context);
        titreScenes.setText("Scènes");
        titreScenes.setTextColor(Palette.texteNormal);
        titreScenes.setGravity(Gravity.CENTER);
        titreScenes.setBackgroundColor(Palette.enTeteDialogues); 
        titreScenes.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreScenes);

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
                btnScene.setOnClickListener(v -> {
                    int start = Math.max(champSaisie.getSelectionStart(), 0);
                    int end = Math.max(champSaisie.getSelectionEnd(), 0);
                    champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), s.nom, 0, s.nom.length());
                });
                listeDroite.addView(btnScene);
            }
        }

        scrollDroite.addView(listeDroite);
        zoneDroite.addView(scrollDroite);

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

        root.addView(zoneGauche);
        root.addView(zoneDroite);

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        grandLayout.addView(bottomBar);

        setContentView(grandLayout);

        // Appels initiaux
        appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
        mettreAJourResumeExpression(noeud, txtResumeExpression);
    }

    // NOUVEAU : Méthode de mise à jour du résumé (Modifiée sans dépendance de classe stricte)
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
            
            if (noeud.requiertCibleVariable() && noeud.getCibleVariable() != null) {
                majInterfacePourVariable(noeud.getCibleVariable(), champSaisie, conteneurClavier, conteneurBooleen);
            } else if (!noeud.utiliseClavierTexte()) {
                champSaisie.setShowSoftInputOnFocus(false);
                champSaisie.setInputType(InputType.TYPE_NULL);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
                if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
            } else {
                champSaisie.setShowSoftInputOnFocus(true);
                champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
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




        


    
