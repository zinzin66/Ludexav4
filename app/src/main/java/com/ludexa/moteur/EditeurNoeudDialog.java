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

        // =========================================================
        // PANNEAU GAUCHE : Zone de texte et Clavier Code
        // =========================================================
        LinearLayout zoneGauche = new LinearLayout(context);
        zoneGauche.setOrientation(LinearLayout.VERTICAL);
        zoneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f));
        zoneGauche.setPadding(20, 20, 20, 20);

        // NOUVEAU : Barre des paramètres (X, Y, etc.)
        LinearLayout barreParams = new LinearLayout(context);
        barreParams.setOrientation(LinearLayout.HORIZONTAL);
        barreParams.setPadding(0, 0, 0, 15);

        // Champ de saisie principal (multiligne)
        EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Color.WHITE);
        champSaisie.setBackgroundColor(Color.parseColor("#2A2A2A"));
        champSaisie.setTextSize(20);
        champSaisie.setGravity(Gravity.TOP | Gravity.START);
        champSaisie.setPadding(15, 15, 15, 15);
        champSaisie.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        
        // C'est cette ligne qui gère l'ouverture du clavier selon le noeud
        champSaisie.setShowSoftInputOnFocus(noeud.utiliseClavierTexte());

        List<String> params = noeud.getNomsParametres();
        if (params != null && !params.isEmpty()) {
            champActif = params.get(0);
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
                                child.setBackgroundColor(Color.parseColor("#4CAF50")); // Vert = Actif
                            } else {
                                child.setBackgroundColor(Color.parseColor("#555555")); // Gris = Inactif
                            }
                        }
                    }
                });
                barreParams.addView(btnParam);
            }

            Button btnDepuisObjet = new Button(context);
            btnDepuisObjet.setText("Depuis un objet...");
            btnDepuisObjet.setTextColor(Color.parseColor("#FFD700")); 
            btnDepuisObjet.setBackgroundColor(Color.parseColor("#333333"));
            btnDepuisObjet.setOnClickListener(v -> {
                Toast.makeText(context, "À venir : Lier " + champActif + " à une propriété (ex: Objet.X)", Toast.LENGTH_SHORT).show();
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

        LinearLayout conteneurClavier = new LinearLayout(context);
        conteneurClavier.setOrientation(LinearLayout.VERTICAL);
        conteneurClavier.setPadding(0, 20, 0, 0);

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
            } catch (Exception e) {
            }
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
                    } else {
                        insererTexte(champSaisie, var.nom);
                    }
                });
                listeDroite.addView(btnVar);
            }
        }

        // --- NOUVEAU : Section SCÈNES ---
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
            } catch (Exception e) {
            }
        }

        if (scenesRecuperees != null) {
            for (Scene s : scenesRecuperees) {
                Button btnScene = new Button(context);
                btnScene.setText(s.nom + " (Scène)");
                btnScene.setTextColor(Color.WHITE);
                btnScene.setBackgroundColor(Color.parseColor("#6a1b9a")); 
                btnScene.setOnClickListener(v -> {
                    insererTexte(champSaisie, s.nom);
                });
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

        // Assemblage Final
        root.addView(zoneGauche);
        root.addView(zoneDroite);

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        grandLayout.addView(bottomBar);

        setContentView(grandLayout);
    }

    private void insererTexte(EditText champSaisie, String texteAInserer) {
        int start = Math.max(champSaisie.getSelectionStart(), 0);
        int end = Math.max(champSaisie.getSelectionEnd(), 0);
        champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), texteAInserer, 0, texteAInserer.length());
    }
}
// bas 2
                        

    
