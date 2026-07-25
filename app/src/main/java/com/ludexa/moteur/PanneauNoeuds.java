package com.ludexa.moteur;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PanneauNoeuds extends ScrollView {

    public PanneauNoeuds(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.parseColor("#2A2A2A")); 
        setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // ---- Section Événements ----
        Button btnEvenements = new Button(context);
        btnEvenements.setText("Événements ▼");
        
        LinearLayout conteneurEvenements = new LinearLayout(context);
        conteneurEvenements.setOrientation(LinearLayout.VERTICAL);
        conteneurEvenements.setPadding(20, 10, 10, 20);
        
        TextView itemEventStart = creerItemNoeud(context, "Au Démarrage", "NoeudEventStart");
        conteneurEvenements.addView(itemEventStart);
        
        btnEvenements.setOnClickListener(v -> {
            if (conteneurEvenements.getVisibility() == View.VISIBLE) {
                conteneurEvenements.setVisibility(View.GONE);
                btnEvenements.setText("Événements ▶");
            } else {
                conteneurEvenements.setVisibility(View.VISIBLE);
                btnEvenements.setText("Événements ▼");
            }
        });

        // ---- Section Actions ----
        Button btnActions = new Button(context);
        btnActions.setText("Actions ▼");
        
        LinearLayout conteneurActions = new LinearLayout(context);
        conteneurActions.setOrientation(LinearLayout.VERTICAL);
        conteneurActions.setPadding(20, 10, 10, 20);
        
        TextView itemActionDeplacer = creerItemNoeud(context, "Déplacer Objet", "NoeudActionDeplacer");
        conteneurActions.addView(itemActionDeplacer);

        TextView itemActionModifierVariable = creerItemNoeud(context, "Modifier Variable", "NoeudActionModifierVariable");
        conteneurActions.addView(itemActionModifierVariable);
        
        // NOUVEAU : Ajout du nœud Modifier Texte
        TextView itemActionModifierTexte = creerItemNoeud(context, "Modifier Texte", "NoeudActionModifierTexte");
        conteneurActions.addView(itemActionModifierTexte);
        
        btnActions.setOnClickListener(v -> {
            if (conteneurActions.getVisibility() == View.VISIBLE) {
                conteneurActions.setVisibility(View.GONE);
                btnActions.setText("Actions ▶");
            } else {
                conteneurActions.setVisibility(View.VISIBLE);
                btnActions.setText("Actions ▼");
            }
        });

        layoutPrincipal.addView(btnEvenements);
        layoutPrincipal.addView(conteneurEvenements);
        layoutPrincipal.addView(btnActions);
        layoutPrincipal.addView(conteneurActions);

        addView(layoutPrincipal);
    }

    private TextView creerItemNoeud(Context context, String libelle, String typeClasse) {
        TextView item = new TextView(context);
        item.setText(libelle);
        item.setTextColor(Color.WHITE);
        item.setPadding(20, 20, 20, 20);
        item.setBackgroundColor(Color.parseColor("#444444"));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 15);
        item.setLayoutParams(params);

        item.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("typeNoeud", typeClasse);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        item.setOnClickListener(v -> {
            Toast.makeText(context, "Maintenez appuyé pour glisser ce nœud", Toast.LENGTH_SHORT).show();
        });

        return item;
    }
}
