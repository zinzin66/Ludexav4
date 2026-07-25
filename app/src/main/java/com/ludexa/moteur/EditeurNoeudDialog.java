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
    private boolean clavierTexteActif = false;

    public EditeurNoeudDialog(Context context, NoeudBase noeud, Scene scene, Runnable onValidate) {
        super(context);
        setTitle("Configurer : " + noeud.nom);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setPadding(30, 30, 30, 30);
        root.setBackgroundColor(Color.parseColor("#2A2A2A"));
        root.setLayoutParams(new ViewGroup.LayoutParams(1000, 700));

        LinearLayout zoneGauche = new LinearLayout(context);
        zoneGauche.setOrientation(LinearLayout.VERTICAL);
        zoneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f));
        zoneGauche.setPadding(0, 0, 20, 0);

        LinearLayout barreParams = new LinearLayout(context);
        barreParams.setOrientation(LinearLayout.HORIZONTAL);
        List<String> params = noeud.getNomsParametres();
        
        EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Color.WHITE);
        champSaisie.setTextSize(24);
        champSaisie.setGravity(Gravity.CENTER);

        // GESTION DU CLAVIER NATIF
        boolean isTextMode = noeud.utiliseClavierTexte();
        if (isTextMode) {
            champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            champSaisie.setShowSoftInputOnFocus(true);
        } else {
            champSaisie.setInputType(InputType.TYPE_NULL);
            champSaisie.setShowSoftInputOnFocus(false);
        }

        for (String param : params) {
            Button btnParam = new Button(context);
            btnParam.setText(param);
            btnParam.setOnClickListener(v -> {
                champActif = param;
                champSaisie.setText(noeud.getValeurParametre(param));
            });
            barreParams.addView(btnParam);
            if (champActif == null) champActif = param; 
        }

        if (!isTextMode) {
            Button btnToggleClavier = new Button(context);
            btnToggleClavier.setText("ABC/123");
            btnToggleClavier.setBackgroundColor(Color.parseColor("#555555"));
            btnToggleClavier.setTextColor(Color.WHITE);
            btnToggleClavier.setOnClickListener(v -> {
                clavierTexteActif = !clavierTexteActif;
                if (clavierTexteActif) {
                    champSaisie.setInputType(InputType.TYPE_CLASS_TEXT);
                    champSaisie.setShowSoftInputOnFocus(true);
                    champSaisie.requestFocus();
                } else {
                    champSaisie.setInputType(InputType.TYPE_NULL);
                    champSaisie.setShowSoftInputOnFocus(false);
                }
            });
            barreParams.addView(btnToggleClavier);
        }
        
        if (champActif != null) {
            champSaisie.setText(noeud.getValeurParametre(champActif));
        }

        zoneGauche.addView(barreParams);
        zoneGauche.addView(champSaisie);

        // AFFICHAGE CONDITIONNEL DU PAVÉ NUMÉRIQUE
        if (!isTextMode) {
            String[][] touches = {
                {"7", "8", "9"},
                {"4", "5", "6"},
                {"1", "2", "3"},
                {"-", "0", "."}, 
                {"", "DEL", ""}
            };

            for (String[] ligne : touches) {
                LinearLayout rowLayout = new LinearLayout(context);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setGravity(Gravity.CENTER);
                for (String touche : ligne) {
                    Button btn = new Button(context);
                    btn.setText(touche);
                    if (touche.isEmpty()) {
                        btn.setVisibility(android.view.View.INVISIBLE);
                    } else {
                        btn.setOnClickListener(v -> {
                            if (champActif == null) return;
                            String courant = champSaisie.getText().toString();
                            
                            if (touche.equals("DEL")) {
                                if (courant.length() > 0) {
                                    champSaisie.setText(courant.substring(0, courant.length() - 1));
                                }
                            } else {
                                champSaisie.setText(courant + touche);
                            }
                            noeud.setValeurParametre(champActif, champSaisie.getText().toString());
                        });
                    }
                    rowLayout.addView(btn);
                }
                zoneGauche.addView(rowLayout);
            }
        }

        Button btnValider = new Button(context);
        btnValider.setText("VALIDER");
        btnValider.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnValider.setTextColor(Color.WHITE);
        btnValider.setOnClickListener(v -> {
            if (champActif != null) {
                noeud.setValeurParametre(champActif, champSaisie.getText().toString());
            }
            if (onValidate != null) onValidate.run();
            dismiss();
        });
        zoneGauche.addView(btnValider);
// bas 1
    
