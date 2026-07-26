// haut 1
package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
        
        // --- CORRECTION CLAVIER : Déblocage de la fenêtre ---
        if (noeud.utiliseClavierTexte() && getWindow() != null) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                                   WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        
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
        champSaisie.setShowSoftInputOnFocus(noeud.utiliseClavierTexte());

        // --- CORRECTION CLAVIER : Appel explicite ---
        if (noeud.utiliseClavierTexte()) {
            champSaisie.setOnClickListener(v -> {
                champSaisie.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(champSaisie, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }

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
    
