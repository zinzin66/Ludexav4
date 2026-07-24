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
        champSaisie.setInputType(InputType.TYPE_NULL);
        champSaisie.setShowSoftInputOnFocus(false);
        champSaisie.setGravity(Gravity.CENTER);

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
        
        if (champActif != null) {
            champSaisie.setText(noeud.getValeurParametre(champActif));
        }

        zoneGauche.addView(barreParams);
        zoneGauche.addView(champSaisie);

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

        Button btnValider = new Button(context);
        btnValider.setText("VALIDER");
        btnValider.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnValider.setTextColor(Color.WHITE);
        btnValider.setOnClickListener(v -> {
            if (onValidate != null) onValidate.run();
            dismiss();
        });
        zoneGauche.addView(btnValider);

        LinearLayout zoneDroite = new LinearLayout(context);
        zoneDroite.setOrientation(LinearLayout.VERTICAL);
        zoneDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        zoneDroite.setBackgroundColor(Color.parseColor("#1E1E1E"));
        zoneDroite.setPadding(20, 20, 20, 20);

        TextView titreDroite = new TextView(context);
        titreDroite.setText("Objets de la Scène");
        titreDroite.setTextColor(Color.LTGRAY);
        titreDroite.setTextSize(18);
        zoneDroite.addView(titreDroite);

        if (noeud.requiertCibleObjet()) {
            TextView txtCibleActuelle = new TextView(context);
            txtCibleActuelle.setTextColor(Color.parseColor("#44AAFF"));
            ObjetBase cible = noeud.getCibleObjet();
            txtCibleActuelle.setText("Cible : " + (cible != null ? cible.nom : "Aucune"));
            txtCibleActuelle.setPadding(0, 10, 0, 20);
            zoneDroite.addView(txtCibleActuelle);

            ScrollView scrollObjets = new ScrollView(context);
            LinearLayout listeObjets = new LinearLayout(context);
            listeObjets.setOrientation(LinearLayout.VERTICAL);

            if (scene != null && scene.objets != null) {
                for (ObjetBase obj : scene.objets) {
                    Button btnObj = new Button(context);
                    btnObj.setText(obj.nom);
                    btnObj.setOnClickListener(v -> {
                        noeud.setCibleObjet(obj);
                        txtCibleActuelle.setText("Cible : " + obj.nom);
                    });
                    listeObjets.addView(btnObj);
                }
            }
            scrollObjets.addView(listeObjets);
            zoneDroite.addView(scrollObjets);
        } else {
            TextView info = new TextView(context);
            info.setText("Ce nœud n'utilise pas de cible.");
            info.setTextColor(Color.DKGRAY);
            zoneDroite.addView(info);
        }

        root.addView(zoneGauche);
        root.addView(zoneDroite);
        setContentView(root);
    }
}
