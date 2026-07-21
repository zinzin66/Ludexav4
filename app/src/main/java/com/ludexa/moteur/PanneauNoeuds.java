package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PanneauNoeuds extends ScrollView {

    public PanneauNoeuds(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Fond légèrement plus clair que le CanvasBlueprint pour détacher visuellement le menu
        setBackgroundColor(Color.parseColor("#2A2A2A")); 

        // Fixer une largeur pour ce panneau gauche (300 pixels)
        setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // ---- Section Événements ----
        Button btnEvenements = new Button(context);
        btnEvenements.setText("Événements ▼");
        
        LinearLayout conteneurEvenements = new LinearLayout(context);
        conteneurEvenements.setOrientation(LinearLayout.VERTICAL);
        conteneurEvenements.setPadding(20, 10, 10, 20);
        
        TextView txtEvtAvenir = new TextView(context);
        txtEvtAvenir.setText("[ Nœuds Événements à venir ]");
        txtEvtAvenir.setTextColor(Color.LTGRAY);
        conteneurEvenements.addView(txtEvtAvenir);
        
        // Logique de l'accordéon (masquer/afficher)
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
        
        TextView txtActAvenir = new TextView(context);
        txtActAvenir.setText("[ Nœuds Actions à venir ]");
        txtActAvenir.setTextColor(Color.LTGRAY);
        conteneurActions.addView(txtActAvenir);
        
        // Logique de l'accordéon (masquer/afficher)
        btnActions.setOnClickListener(v -> {
            if (conteneurActions.getVisibility() == View.VISIBLE) {
                conteneurActions.setVisibility(View.GONE);
                btnActions.setText("Actions ▶");
            } else {
                conteneurActions.setVisibility(View.VISIBLE);
                btnActions.setText("Actions ▼");
            }
        });

        // Assemblage final du panneau
        layoutPrincipal.addView(btnEvenements);
        layoutPrincipal.addView(conteneurEvenements);
        layoutPrincipal.addView(btnActions);
        layoutPrincipal.addView(conteneurActions);

        addView(layoutPrincipal);
    }
}
