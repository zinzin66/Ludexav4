// haut 1
package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
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

        // Layout Principal : Horizontal
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Color.parseColor("#1E1E1E")); 
        root.setLayoutParams(new ViewGroup.LayoutParams(1200, 800));

        // Déclaration avancée des conteneurs pour y avoir accès dans les listeners
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

        // Champ de saisie principal
        EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Color.WHITE);
        champSaisie.setBackgroundColor(Color.parseColor("#2A2A2A"));
        champSaisie.setTextSize(20);
        champSaisie.setGravity(Gravity.TOP | Gravity.START);
        champSaisie.setPadding(15, 15, 15, 15);
        champSaisie.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        
        List<String> params = noeud.getNomsParametres();
        if (params != null && !params.isEmpty()) {
            champActif = params.get(0);
        }

        // Action générique sur le clic (switch selon le type d'éditeur)
        champSaisie.setOnClickListener(v -> {
            if (champActif != null) {
                String typeEditeur = noeud.getTypeEditeurParametre(champActif);
                switch (typeEditeur) {
                    case NoeudBase.TYPE_COULEUR:
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setTitle("Choisir une couleur");
                        String[] couleurs = {"Bleu", "Rouge", "Vert", "Noir", "Blanc", "Jaune", "Magenta", "Cyan"};
                        builder.setItems(couleurs, (dialog, which) -> {
                            String choix = couleurs[which];
                            champSaisie.setText(choix);
                            noeud.setValeurParametre(champActif, choix);
                        });
                        builder.show();
                        break;
                    case NoeudBase.TYPE_CHOIX_LISTE:
                        android.app.AlertDialog.Builder builderListe = new android.app.AlertDialog.Builder(context);
                        builderListe.setTitle("Choisir une option");
                        List<String> optionsListe = noeud.getOptionsChoixListe(champActif);
                        String[] optionsArray = optionsListe.toArray(new String[0]);
                        builderListe.setItems(optionsArray, (dialog, which) -> {
                            String choix = optionsArray[which];
                            champSaisie.setText(choix);
                            noeud.setValeurParametre(champActif, choix);
                        });
                        builderListe.show();
                        break;
                    // Futurs types (sons, assets, etc.)
                }
            }
        });

        if (params != null && !params.isEmpty()) {
            champSaisie.setText(noeud.getValeurParametre(champActif));

            for (String paramName : params) {
                Button btnParam = new Button(context);
                btnParam.setText(paramName);
                btnParam.setTextColor(Color.WHITE);
                btnParam.setBackgroundColor(champActif.equals(paramName) ? Color.parseColor("#4CAF50") : Color.parseColor("#555555"));
                
                LinearLayout.LayoutParams btnParamsLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParamsLayout.setMargins(0, 0, 15, 0);
                btnParam.setLayoutParams(btnParamsLayout);

                btnParam.setOnClickListener(v -> {
                    if (champActif != null) {
                        noeud.setValeurParametre(champActif, champSaisie.getText().toString());
                    }
                    champActif = paramName;
                    champSaisie.setText(noeud.getValeurParametre(champActif));
                    
                    for (int i = 0; i < barreParams.getChildCount(); i++) {
                        View child = barreParams.getChildAt(i);
                        if (child instanceof Button && params.contains(((Button)child).getText().toString())) {
                            if (((Button)child).getText().toString().equals(champActif)) {
                                child.setBackgroundColor(Color.parseColor("#4CAF50"));
                            } else {
                                child.setBackgroundColor(Color.parseColor("#555555"));
                            }
                        }
                    }
                    
                    // On adapte l'interface au type du nouveau paramètre cliqué
                    appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);

                    // Auto-ouvrir la popup si le paramètre requiert une interaction directe
                    String type = noeud.getTypeEditeurParametre(champActif);
                    if (NoeudBase.TYPE_COULEUR.equals(type) || NoeudBase.TYPE_CHOIX_LISTE.equals(type)) {
                        champSaisie.performClick();
                    }
                });
                barreParams.addView(btnParam);
            }

            Button btnDepuisObjet = new Button(context);
            btnDepuisObjet.setText("Depuis un objet...");
            btnDepuisObjet.setTextColor(Color.parseColor("#FFD700")); 
            btnDepuisObjet.setBackgroundColor(Color.parseColor("#333333"));
            btnDepuisObjet.setOnClickListener(v -> {
                Toast.makeText(context, "À venir : Lier " + champActif + " à une propriété", Toast.LENGTH_SHORT).show();
            });
            barreParams.addView(btnDepuisObjet);
        }

        zoneGauche.addView(barreParams);
        zoneGauche.addView(champSaisie);

        // CLAVIER CODE
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
                btn.setTextColor(Color.WHITE);
                btn.setBackgroundColor(Color.parseColor("#333333"));
                
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

        // CONTENEUR BOOLEEN
        Button btnVrai = new Button(context);
        btnVrai.setText("Vrai (true)");
        btnVrai.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnVrai.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams paramVrai = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramVrai.setMargins(10, 10, 10, 10);
        btnVrai.setLayoutParams(paramVrai);
        btnVrai.setOnClickListener(v -> {
            champSaisie.setText("true");
            if (champActif != null) noeud.setValeurParametre(champActif, "true");
        });

        Button btnFaux = new Button(context);
        btnFaux.setText("Faux (false)");
        btnFaux.setBackgroundColor(Color.parseColor("#F44336"));
        btnFaux.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams paramFaux = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        paramFaux.setMargins(10, 10, 10, 10);
        btnFaux.setLayoutParams(paramFaux);
        btnFaux.setOnClickListener(v -> {
            champSaisie.setText("false");
            if (champActif != null) noeud.setValeurParametre(champActif, "false");
        });

        conteneurBooleen.addView(btnVrai);
        conteneurBooleen.addView(btnFaux);
        zoneGauche.addView(conteneurBooleen);
// bas 1

// haut 2
        // =========================================================
        // PANNEAU DROIT : Listes (Items, Variables...)
        // =========================================================
        LinearLayout zoneDroite = new LinearLayout(context);
        zoneDroite.setOrientation(LinearLayout.VERTICAL);
        zoneDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
        zoneDroite.setBackgroundColor(Color.parseColor("#151515"));
        
        ScrollView scrollDroite = new ScrollView(context);
        LinearLayout listeDroite = new LinearLayout(context);
        listeDroite.setOrientation(LinearLayout.VERTICAL);

        TextView txtCibleActuelle = new TextView(context);
        txtCibleActuelle.setTextColor(Color.parseColor("#44AAFF"));
        txtCibleActuelle.setPadding(20, 20, 20, 20);
        txtCibleActuelle.setTextSize(16);
        
        if (noeud.requiertCibleObjet()) {
            ObjetBase cible = noeud.getCibleObjet();
            txtCibleActuelle.setText("Cible Actuelle : " + (cible != null ? cible.nom : "Aucune"));
        } else if (noeud.requiertCibleVariable()) {
            Variable cibleVar = noeud.getCibleVariable();
            txtCibleActuelle.setText("Cible Actuelle : " + (cibleVar != null ? cibleVar.nom : "Aucune"));
        } else {
            txtCibleActuelle.setText("Cible Actuelle : Aucune");
            txtCibleActuelle.setVisibility(View.GONE); 
        }
        
        if (noeud.requiertCibleObjet() || noeud.requiertCibleVariable()) {
            listeDroite.addView(txtCibleActuelle);
        }

        // Section : ITEMS
        TextView titreItems = new TextView(context);
        titreItems.setText("Items (Cible Objet)");
        titreItems.setTextColor(Color.WHITE);
        titreItems.setGravity(Gravity.CENTER);
        titreItems.setBackgroundColor(Color.parseColor("#1a435c")); 
        titreItems.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreItems);

        if (scene != null && scene.objets != null) {
            for (ObjetBase obj : scene.objets) {
                Button btnObj = new Button(context);
                btnObj.setText(obj.nom);
                btnObj.setTextColor(Color.LTGRAY);
                btnObj.setBackgroundColor(Color.parseColor("#222222"));
                btnObj.setOnClickListener(v -> {
                    if (noeud.requiertCibleObjet()) {
                        noeud.setCibleObjet(obj);
                        txtCibleActuelle.setText("Cible Actuelle : " + obj.nom);
                    }
                });
                listeDroite.addView(btnObj);
            }
        }

        // Section : VARIABLES
        TextView titreVars = new TextView(context);
        titreVars.setText("Variables");
        titreVars.setTextColor(Color.WHITE);
        titreVars.setGravity(Gravity.CENTER);
        titreVars.setBackgroundColor(Color.parseColor("#1a435c"));
        titreVars.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreVars);

        List<Variable> variablesGlobalesRecuperees = null;
        if (NoeudBase.contexteApplication != null) {
            try {
                java.lang.reflect.Field varsField = NoeudBase.contexteApplication.getClass().getField("variablesGlobales");
                @SuppressWarnings("unchecked")
                List<Variable> globales = (List<Variable>) varsField.get(NoeudBase.contexteApplication);
                variablesGlobalesRecuperees = globales;
            } catch (Exception e) {}
        }

        if (variablesGlobalesRecuperees != null) {
            for (Variable var : variablesGlobalesRecuperees) {
                Button btnVar = new Button(context);
                btnVar.setText(var.nom + " (Globale)");
                btnVar.setTextColor(Color.WHITE);
                btnVar.setBackgroundColor(Color.parseColor("#2e4a2e")); 
                btnVar.setOnClickListener(v -> {
                    if (noeud.requiertCibleVariable() && !noeud.utiliseClavierTexte()) {
                        noeud.setCibleVariable(var);
                        txtCibleActuelle.setText("Cible Actuelle : " + var.nom);
                        majInterfacePourVariable(var, noeud, champSaisie, conteneurClavier, conteneurBooleen);
                    } else {
                        insererTexte(champSaisie, var.nom);
                    }
                });
                listeDroite.addView(btnVar);
            }
        }

        if (scene != null && scene.variablesLocales != null) {
            for (Variable var : scene.variablesLocales) {
                Button btnVar = new Button(context);
                btnVar.setText(var.nom + " (Locale)");
                btnVar.setTextColor(Color.WHITE);
                btnVar.setBackgroundColor(Color.parseColor("#2e4a2e")); 
                btnVar.setOnClickListener(v -> {
                    if (noeud.requiertCibleVariable() && !noeud.utiliseClavierTexte()) {
                        noeud.setCibleVariable(var);
                        txtCibleActuelle.setText("Cible Actuelle : " + var.nom);
                        majInterfacePourVariable(var, noeud, champSaisie, conteneurClavier, conteneurBooleen);
                    } else {
                        insererTexte(champSaisie, var.nom);
                    }
                });
                listeDroite.addView(btnVar);
            }
        }

        // Section SCÈNES
        TextView titreScenes = new TextView(context);
        titreScenes.setText("Scènes");
        titreScenes.setTextColor(Color.WHITE);
        titreScenes.setGravity(Gravity.CENTER);
        titreScenes.setBackgroundColor(Color.parseColor("#1a435c")); 
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
                btnScene.setTextColor(Color.WHITE);
                btnScene.setBackgroundColor(Color.parseColor("#6a1b9a")); 
                btnScene.setOnClickListener(v -> insererTexte(champSaisie, s.nom));
                listeDroite.addView(btnScene);
            }
        }

        scrollDroite.addView(listeDroite);
        zoneDroite.addView(scrollDroite);

        // =========================================================
        // BANDEAU INFERIEUR : Boutons Save / Cancel
        // =========================================================
        LinearLayout bottomBar = new LinearLayout(context);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        bottomBar.setBackgroundColor(Color.parseColor("#1E1E1E"));
        bottomBar.setPadding(20, 20, 20, 20);

        Button btnCancel = new Button(context);
        btnCancel.setText("Cancel");
        btnCancel.setBackgroundColor(Color.parseColor("#444444"));
        btnCancel.setTextColor(Color.WHITE);
        btnCancel.setOnClickListener(v -> dismiss());

        Button btnSave = new Button(context);
        btnSave.setText("Save");
        btnSave.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnSave.setTextColor(Color.WHITE);
        btnSave.setOnClickListener(v -> {
            if (champActif != null) {
                noeud.setValeurParametre(champActif, champSaisie.getText().toString());
            }
            if (onValidate != null) onValidate.run();
            dismiss();
        });

        bottomBar.addView(btnCancel);
        
        View spacer = new View(context);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(20, 1));
        bottomBar.addView(spacer);
        
        bottomBar.addView(btnSave);

        root.addView(zoneGauche);
        root.addView(zoneDroite);

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        grandLayout.addView(bottomBar);

        setContentView(grandLayout);

        // Initialisation de l'état de l'interface en fonction du premier paramètre actif
        appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
    }

    // NOUVEAU : Méthode générique pour appliquer le bon comportement graphique selon le type de l'éditeur du paramètre courant
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
            // Comportement normal par défaut (texte, code...)
            champSaisie.setFocusable(true);
            champSaisie.setFocusableInTouchMode(true);
            champSaisie.setClickable(true);
            champSaisie.setShowSoftInputOnFocus(noeud.utiliseClavierTexte());
            
            // On conserve l'override existant pour les cibles de type Variable
            if (noeud instanceof NoeudActionModifierVariable) {
                majInterfacePourVariable(noeud.getCibleVariable(), noeud, champSaisie, conteneurClavier, conteneurBooleen);
            } else if (!noeud.utiliseClavierTexte()) {
                champSaisie.setInputType(InputType.TYPE_NULL);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
                if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
            } else {
                champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
                if (conteneurBooleen != null) conteneurBooleen.setVisibility(View.GONE);
            }
        }
    }

    private void majInterfacePourVariable(Variable var, NoeudBase noeud, EditText champSaisie, View conteneurClavier, View conteneurBooleen) {
        if (!(noeud instanceof NoeudActionModifierVariable)) return; 
        
        if (var == null) {
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            conteneurClavier.setVisibility(View.VISIBLE);
            conteneurBooleen.setVisibility(View.GONE);
            return;
        }

        if ("CHIFFRE".equals(var.type)) {
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            conteneurClavier.setVisibility(View.VISIBLE);
            conteneurBooleen.setVisibility(View.GONE);
        } else if ("TEXTE".equals(var.type)) {
            champSaisie.setShowSoftInputOnFocus(true);
            champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            conteneurClavier.setVisibility(View.GONE);
            conteneurBooleen.setVisibility(View.GONE);
            champSaisie.requestFocus();
        } else if ("BOOLEEN".equals(var.type)) {
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            conteneurClavier.setVisibility(View.GONE);
            conteneurBooleen.setVisibility(View.VISIBLE);
        }
    }

    private void insererTexte(EditText champSaisie, String texteAInserer) {
        int start = Math.max(champSaisie.getSelectionStart(), 0);
        int end = Math.max(champSaisie.getSelectionEnd(), 0);
        champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), texteAInserer, 0, texteAInserer.length());
    }
}
// bas 2

    
