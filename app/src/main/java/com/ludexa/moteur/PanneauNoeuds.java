// haut 1
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
        
        // Ajout du vrai nœud Événement
        TextView itemEventStart = creerItemNoeud(context, "Au Démarrage", "NoeudEventStart");
        conteneurEvenements.addView(itemEventStart);
        
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
        
        // Ajout du vrai nœud Action
        TextView itemActionDeplacer = creerItemNoeud(context, "Déplacer Objet", "NoeudActionDeplacer");
        conteneurActions.addView(itemActionDeplacer);
        
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

    /**
     * Crée un élément textuel stylisé, cliquable et glissable, représentant un nœud disponible.
     */
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

        // Gestion du clic long pour enclencher le drag & drop
        item.setOnLongClickListener(v -> {
            // On embarque le nom de la classe pour que le Canvas (cible du drop) sache quoi instancier
            ClipData data = ClipData.newPlainText("typeNoeud", typeClasse);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true; // L'événement est consommé
        });

        // Gestion du clic court pour guider l'utilisateur
        item.setOnClickListener(v -> {
            Toast.makeText(context, "Maintenez appuyé pour glisser ce nœud", Toast.LENGTH_SHORT).show();
        });

        return item;
    }
}
// bas 1
        
