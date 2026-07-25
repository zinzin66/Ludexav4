// haut 1
package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;

public class EditeurNoeudDialog extends Dialog {
    
    private String champActif = null;

    public EditeurNoeudDialog(Context context, NoeudBase noeud, Scene scene, Runnable onValidate) {
        super(context);
        setTitle("Edit Value - " + noeud.nom);

        // Layout Principal : Horizontal
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Color.parseColor("#1E1E1E")); // Fond sombre type IDE
        root.setLayoutParams(new ViewGroup.LayoutParams(1200, 800));

        // =========================================================
        // PANNEAU GAUCHE : Zone de texte et Clavier Code
        // =========================================================
        LinearLayout zoneGauche = new LinearLayout(context);
        zoneGauche.setOrientation(LinearLayout.VERTICAL);
        zoneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f));
        zoneGauche.setPadding(20, 20, 20, 20);

        // Champ de saisie principal (multiligne)
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
            champSaisie.setText(noeud.getValeurParametre(champActif));
        }

        // Configuration du clavier logiciel (désactivé par défaut au profit du clavier custom)
        champSaisie.setShowSoftInputOnFocus(noeud.utiliseClavierTexte());
        
        zoneGauche.addView(champSaisie);

        // Clavier customisé type "Code" (inspiré de la maquette)
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
                    if (touche.equals("DEL")) btn.setBackgroundColor(Color.parseColor("#5c2323")); // Rouge sombre
                    
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

        // =========================================================
        // PANNEAU DROIT : Listes (Items, Variables...)
        // =========================================================
// bas 1
// haut 2
        LinearLayout zoneDroite = new LinearLayout(context);
        zoneDroite.setOrientation(LinearLayout.VERTICAL);
        zoneDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
        zoneDroite.setBackgroundColor(Color.parseColor("#151515"));
        
        ScrollView scrollDroite = new ScrollView(context);
        LinearLayout listeDroite = new LinearLayout(context);
        listeDroite.setOrientation(LinearLayout.VERTICAL);

        // Section : ITEMS (Objets de la scène)
        TextView titreItems = new TextView(context);
        titreItems.setText("Items (Cible)");
        titreItems.setTextColor(Color.WHITE);
        titreItems.setGravity(Gravity.CENTER);
        titreItems.setBackgroundColor(Color.parseColor("#1a435c")); // Bleu sombre
        titreItems.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreItems);

        TextView txtCibleActuelle = new TextView(context);
        txtCibleActuelle.setTextColor(Color.parseColor("#44AAFF"));
        ObjetBase cibleActuelle = noeud.getCibleObjet();
        txtCibleActuelle.setText("Cible Actuelle: " + (cibleActuelle != null ? cibleActuelle.nom : "Aucune"));
        txtCibleActuelle.setPadding(20, 10, 20, 20);
        listeDroite.addView(txtCibleActuelle);

        if (scene != null && scene.objets != null) {
            for (ObjetBase obj : scene.objets) {
                Button btnObj = new Button(context);
                btnObj.setText(obj.nom);
                btnObj.setTextColor(Color.LTGRAY);
                btnObj.setBackgroundColor(Color.parseColor("#222222"));
                btnObj.setOnClickListener(v -> {
                    noeud.setCibleObjet(obj);
                    txtCibleActuelle.setText("Cible Actuelle: " + obj.nom);
                });
                listeDroite.addView(btnObj);
            }
        }

        // Section : VARIABLES (Pour insertion dans le code)
        TextView titreVars = new TextView(context);
        titreVars.setText("Variables");
        titreVars.setTextColor(Color.WHITE);
        titreVars.setGravity(Gravity.CENTER);
        titreVars.setBackgroundColor(Color.parseColor("#1a435c"));
        titreVars.setPadding(10, 15, 10, 15);
        listeDroite.addView(titreVars);

        if (context instanceof InterfaceEditeur) {
            List<Variable> globales = ((InterfaceEditeur) context).variablesGlobales;
            if (globales != null) {
                for (Variable var : globales) {
                    Button btnVar = new Button(context);
                    btnVar.setText(var.nom + " (Globale)");
                    btnVar.setTextColor(Color.WHITE);
                    btnVar.setBackgroundColor(Color.parseColor("#2e4a2e")); // Vert sombre
                    btnVar.setOnClickListener(v -> {
                        if (noeud.requiertCibleVariable() && !noeud.utiliseClavierTexte()) {
                            noeud.setCibleVariable(var);
                        } else {
                            insererTexte(champSaisie, var.nom);
                        }
                    });
                    listeDroite.addView(btnVar);
                }
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
                    } else {
                        insererTexte(champSaisie, var.nom);
                    }
                });
                listeDroite.addView(btnVar);
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
