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
import java.util.List;
import java.util.Map;

public class PanneauNoeuds extends ScrollView {

    public PanneauNoeuds(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Palette.fondPanneaux); 
        setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // Récupération dynamique depuis le RegistreNoeuds
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
                // info.classeType est transmis au Drag & Drop pour CanvasBlueprint
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
            
            layoutPrincipal.addView(btnCat);
            layoutPrincipal.addView(conteneurCat);
        }

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
            Toast.makeText(context, "Maintenez appuyé pour glisser ce nœud", Toast.LENGTH_SHORT).show();
        });

        return item;
    }
}
// bas 1


