// haut 1
package com.ludexa.moteur;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Map;

public class PanneauNoeuds extends ScrollView {

    private LinearLayout conteneurSections;
    private Button boutonMasquer;
    private TextView titrePanneau;
    private boolean estOuvert = true;

    public PanneauNoeuds(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Palette.fondPanneaux); 
        setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        LinearLayout enTete = new LinearLayout(context);
        enTete.setOrientation(LinearLayout.HORIZONTAL);
        enTete.setGravity(Gravity.CENTER_VERTICAL);
        enTete.setBackgroundColor(Palette.fondPanneaux);
        enTete.setPadding(10, 10, 10, 10);

        titrePanneau = new TextView(context);
        titrePanneau.setText(Traducteur.get("panneau_noeuds_titre"));
        titrePanneau.setTextColor(Palette.texteSelectionne);
        titrePanneau.setTextSize(14);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        titrePanneau.setLayoutParams(paramsTitre);

        boutonMasquer = new Button(context);
        boutonMasquer.setText("<");
        boutonMasquer.setTextColor(Palette.texteNormal);
        boutonMasquer.setBackgroundColor(Color.TRANSPARENT);
        boutonMasquer.setPadding(0, 0, 0, 0);
        boutonMasquer.setLayoutParams(new LinearLayout.LayoutParams(80, 80));

        enTete.addView(titrePanneau);
        enTete.addView(boutonMasquer);
        layoutPrincipal.addView(enTete);

        conteneurSections = new LinearLayout(context);
        conteneurSections.setOrientation(LinearLayout.VERTICAL);

        Map<String, List<RegistreNoeuds.InfoNoeud>> categories = RegistreNoeuds.getNoeudsParCategorie();
        
        for (Map.Entry<String, List<RegistreNoeuds.InfoNoeud>> entry : categories.entrySet()) {
            String nomCat = entry.getKey();
            List<RegistreNoeuds.InfoNoeud> noeuds = entry.getValue();
            
            Button btnCat = new Button(context);
            btnCat.setText(nomCat + " ▼");
            
            LinearLayout conteneurCat = new LinearLayout(context);
            conteneurCat.setOrientation(LinearLayout.VERTICAL);
            conteneurCat.setPadding(20, 10, 10, 20);
            
            for (RegistreNoeuds.InfoNoeud info : noeuds) {
                TextView item = creerItemNoeud(context, info.libelle, info.classeType);
                conteneurCat.addView(item);
            }
            
            btnCat.setOnClickListener(v -> {
                if (conteneurCat.getVisibility() == View.VISIBLE) {
                    conteneurCat.setVisibility(View.GONE);
                    btnCat.setText(nomCat + " ▶");
                } else {
                    conteneurCat.setVisibility(View.VISIBLE);
                    btnCat.setText(nomCat + " ▼");
                }
            });
            
            conteneurSections.addView(btnCat);
            conteneurSections.addView(conteneurCat);
        }

        layoutPrincipal.addView(conteneurSections);

        boutonMasquer.setOnClickListener(v -> {
            estOuvert = !estOuvert;
            if (estOuvert) {
                conteneurSections.setVisibility(View.VISIBLE);
                titrePanneau.setVisibility(View.VISIBLE);
                boutonMasquer.setText("<");
                setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT));
            } else {
                conteneurSections.setVisibility(View.GONE);
                titrePanneau.setVisibility(View.GONE);
                boutonMasquer.setText(">");
                setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            }
            requestLayout();
        });

        addView(layoutPrincipal);
    }

    private TextView creerItemNoeud(Context context, String libelle, String typeClasse) {
        TextView item = new TextView(context);
        item.setText(libelle);
        item.setTextColor(Palette.texteNormal);
        item.setPadding(20, 20, 20, 20);
        item.setBackgroundColor(Color.TRANSPARENT);
        
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
            Toast.makeText(context, Traducteur.get("toast_glisser_noeud"), Toast.LENGTH_SHORT).show();
        });

        return item;
    }
}
// bas 1
