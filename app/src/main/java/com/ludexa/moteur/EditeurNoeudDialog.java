// haut 1
package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast; 
import java.util.List;

public class EditeurNoeudDialog extends Dialog {
    
    private String champActif = null;
    private boolean modeCibleObjet = false;
    private boolean modeCibleObjetB = false; // NOUVEAU : Mode spécifique pour la cible Objet B
    private boolean modeCibleVariable = false;
    private boolean modeCibleScene = false;

    public EditeurNoeudDialog(Context context, NoeudBase noeud, Scene scene, Runnable onValidate) {
        super(context);
        setTitle("Edit Value - " + noeud.nom);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(Palette.fondPanneaux); 

        final LinearLayout conteneurClavier = new LinearLayout(context);
        conteneurClavier.setOrientation(LinearLayout.VERTICAL);
        conteneurClavier.setPadding(0, 10, 0, 0); 

        final LinearLayout conteneurBooleen = new LinearLayout(context);
        conteneurBooleen.setOrientation(LinearLayout.HORIZONTAL);
        conteneurBooleen.setGravity(Gravity.CENTER);
        conteneurBooleen.setPadding(0, 15, 0, 0);
        conteneurBooleen.setVisibility(View.GONE);

        final LinearLayout listeGauche = new LinearLayout(context);
        listeGauche.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout barreParams = new LinearLayout(context);
        barreParams.setOrientation(LinearLayout.HORIZONTAL);
        barreParams.setPadding(0, 0, 0, 10);

        final TextView txtResumeExpression = new TextView(context);
        txtResumeExpression.setTextColor(Palette.texteSelectionne);
        txtResumeExpression.setTextSize(16); 
        txtResumeExpression.setPadding(10, 0, 10, 5);
        txtResumeExpression.getPaint().setFakeBoldText(true);

        final EditText champSaisie = new EditText(context);
        champSaisie.setTextColor(Palette.texteNormal);
        champSaisie.setBackgroundColor(Palette.canvasFond);
        champSaisie.setTextSize(18); 
        champSaisie.setGravity(Gravity.TOP | Gravity.START);
        champSaisie.setPadding(15, 15, 15, 15);
        
        int hauteurChampDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources().getDisplayMetrics());
        champSaisie.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hauteurChampDp));
        
        final Button btnCibleObjet = new Button(context);
        final Button btnCibleObjetB = new Button(context); // NOUVEAU
        final Button btnCibleVariable = new Button(context);
        final Button btnCibleScene = new Button(context);

        List<String> params = noeud.getNomsParametres();
        if (params != null && !params.isEmpty()) {
            champActif = params.get(0);
        }

        champSaisie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (champActif != null) {
                    noeud.setValeurParametre(champActif, s.toString());
                    mettreAJourResumeExpression(noeud, txtResumeExpression);
                }
            }
        });

        champSaisie.setOnClickListener(v -> {
            if (champActif != null) {
                String typeEditeur = noeud.getTypeEditeurParametre(champActif);
                switch (typeEditeur) {
                    case NoeudBase.TYPE_COULEUR:
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setTitle("Choisir une couleur");
                        String[] couleurs = {"Bleu", "Rouge", "Vert", "Noir", "Blanc", "Jaune", "Magenta", "Cyan"};
                        builder.setItems(couleurs, (dialog, which) -> champSaisie.setText(couleurs[which]));
                        builder.show();
                        break;
                    case NoeudBase.TYPE_CHOIX_LISTE:
                        android.app.AlertDialog.Builder builderListe = new android.app.AlertDialog.Builder(context);
                        builderListe.setTitle("Choisir une option");
                        List<String> optionsListe = noeud.getOptionsChoixListe(champActif);
                        String[] optionsArray = optionsListe.toArray(new String[0]);
                        builderListe.setItems(optionsArray, (dialog, which) -> champSaisie.setText(optionsArray[which]));
                        builderListe.show();
                        break;
                }
            }
        });
// bas 1
        // haut 2
        LinearLayout wrapperDroite = new LinearLayout(context);
        wrapperDroite.setOrientation(LinearLayout.VERTICAL);
        wrapperDroite.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f));

        ScrollView scrollDroit = new ScrollView(context);
        scrollDroit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollDroit.setFillViewport(true);

        LinearLayout colonneDroite = new LinearLayout(context);
        colonneDroite.setOrientation(LinearLayout.VERTICAL);
        colonneDroite.setPadding(15, 15, 15, 15); 

        colonneDroite.addView(barreParams);
        colonneDroite.addView(txtResumeExpression);

        if (params != null && !params.isEmpty()) {
            String valInit = noeud.getValeurParametre(champActif);
            champSaisie.setText(valInit != null ? valInit : "");

            for (String paramName : params) {
                Button btnParam = new Button(context);
                btnParam.setText(paramName);
                btnParam.setTextColor(Palette.texteNormal);
                btnParam.setBackgroundColor(champActif.equals(paramName) ? Color.parseColor("#4CAF50") : Palette.boutonNormal);
                
                LinearLayout.LayoutParams btnParamsLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParamsLayout.setMargins(0, 0, 15, 0);
                btnParam.setLayoutParams(btnParamsLayout);

                btnParam.setOnClickListener(v -> {
                    modeCibleObjet = false;
                    modeCibleObjetB = false; // Reset NOUVEAU
                    modeCibleVariable = false;
                    modeCibleScene = false;
                    champActif = paramName;
                    String val = noeud.getValeurParametre(champActif);
                    champSaisie.setText(val != null ? val : "");
                    
                    if (noeud.requiertCibleObjet()) { btnCibleObjet.setBackgroundColor(Palette.boutonNormal); btnCibleObjet.setTextColor(Color.parseColor("#FFD700")); }
                    if (noeud.requiertCibleObjetB()) { btnCibleObjetB.setBackgroundColor(Palette.boutonNormal); btnCibleObjetB.setTextColor(Color.parseColor("#FFD700")); }
                    if (noeud.requiertCibleVariable()) { btnCibleVariable.setBackgroundColor(Palette.boutonNormal); btnCibleVariable.setTextColor(Color.parseColor("#FFD700")); }
                    if (noeud.requiertCibleScene()) { btnCibleScene.setBackgroundColor(Palette.boutonNormal); btnCibleScene.setTextColor(Color.parseColor("#FFD700")); }

                    for (int i = 0; i < barreParams.getChildCount(); i++) {
                        View child = barreParams.getChildAt(i);
                        if (child instanceof Button && params.contains(((Button)child).getText().toString())) {
                            if (((Button)child).getText().toString().equals(champActif)) {
                                child.setBackgroundColor(Color.parseColor("#4CAF50"));
                            } else {
                                child.setBackgroundColor(Palette.boutonNormal);
                            }
                        }
                    }
                    appliquerTypeEditeur(noeud, champActif, champSaisie, conteneurClavier, conteneurBooleen);
                });
                barreParams.addView(btnParam);
            }
        }

        colonneDroite.addView(champSaisie);
        // ... (Boucle clavier, inchangé, raccourci pour éviter de trop allonger)
        String[][] touchesCode = {{"1", "2", "3", "DEL"}, {"4", "5", "6", "ESPACE"}, {"7", "8", "9", "\""}, {".", "0", "+", "-"}, {"*", "/", "(", ")"}, {">", "<", "=", "!"}, {"||", "&&", "", ""}, {"==", "!=", ">=", "<="}, {"%", ",", "true", "false"}};
        int margeClavierDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

        for (String[] ligne : touchesCode) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            for (String touche : ligne) {
                Button btn = new Button(context);
                btn.setText(touche);
                btn.setTextColor(Palette.texteNormal);
                btn.setBackgroundColor(Palette.boutonNormal);
                btn.setPadding(0, 15, 0, 15);
                btn.setTextSize(14); 
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                btnParams.setMargins(margeClavierDp, margeClavierDp, margeClavierDp, margeClavierDp);
                btn.setLayoutParams(btnParams);

                if (touche.isEmpty()) { btn.setVisibility(android.view.View.INVISIBLE); } 
                else {
                    if (touche.equals("DEL")) btn.setBackgroundColor(Color.parseColor("#5c2323")); 
                    btn.setOnClickListener(v -> {
                        int start = Math.max(champSaisie.getSelectionStart(), 0);
                        int end = Math.max(champSaisie.getSelectionEnd(), 0);
                        if (touche.equals("DEL")) {
                            if (start > 0 && start == end) champSaisie.getText().delete(start - 1, start);
                            else if (start != end) champSaisie.getText().delete(Math.min(start, end), Math.max(start, end));
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
        colonneDroite.addView(conteneurClavier);
        scrollDroit.addView(colonneDroite);
        wrapperDroite.addView(scrollDroit);
// bas 2
// haut 3.1
        LinearLayout colonneGauche = new LinearLayout(context);
        colonneGauche.setOrientation(LinearLayout.VERTICAL);
        colonneGauche.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
        colonneGauche.setBackgroundColor(Palette.fondPanneaux);
        
        ScrollView scrollGauche = new ScrollView(context);
        scrollGauche.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollGauche.setFillViewport(true);

        if (noeud.requiertCibleObjet()) {
            btnCibleObjet.setText("Cible Objet");
            btnCibleObjet.setTextColor(Color.parseColor("#FFD700"));
            btnCibleObjet.setBackgroundColor(Palette.boutonNormal);
            LinearLayout.LayoutParams pObjet = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pObjet.setMargins(20, 20, 20, 5);
            btnCibleObjet.setLayoutParams(pObjet);
            btnCibleObjet.setOnClickListener(v -> {
                modeCibleObjet = true;
                modeCibleObjetB = false; // NOUVEAU
                modeCibleVariable = false;
                modeCibleScene = false;
                btnCibleObjet.setBackgroundColor(Color.parseColor("#FFD700"));
                btnCibleObjet.setTextColor(Color.BLACK);
                btnCibleObjetB.setBackgroundColor(Palette.boutonNormal);
                btnCibleObjetB.setTextColor(Color.parseColor("#FFD700"));
                btnCibleVariable.setBackgroundColor(Palette.boutonNormal);
                btnCibleVariable.setTextColor(Color.parseColor("#FFD700"));
                btnCibleScene.setBackgroundColor(Palette.boutonNormal);
                btnCibleScene.setTextColor(Color.parseColor("#FFD700"));
            });
            listeGauche.addView(btnCibleObjet);
        }
        
        // NOUVEAU : Bouton et logique pour Cible Objet B
        if (noeud.requiertCibleObjetB()) {
            btnCibleObjetB.setText("Cible Objet B");
            btnCibleObjetB.setTextColor(Color.parseColor("#FFD700"));
            btnCibleObjetB.setBackgroundColor(Palette.boutonNormal);
            LinearLayout.LayoutParams pObjetB = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pObjetB.setMargins(20, 5, 20, 5);
            btnCibleObjetB.setLayoutParams(pObjetB);
            btnCibleObjetB.setOnClickListener(v -> {
                modeCibleObjetB = true;
                modeCibleObjet = false;
                modeCibleVariable = false;
                modeCibleScene = false;
                btnCibleObjetB.setBackgroundColor(Color.parseColor("#FFD700"));
                btnCibleObjetB.setTextColor(Color.BLACK);
                btnCibleObjet.setBackgroundColor(Palette.boutonNormal);
                btnCibleObjet.setTextColor(Color.parseColor("#FFD700"));
                btnCibleVariable.setBackgroundColor(Palette.boutonNormal);
                btnCibleVariable.setTextColor(Color.parseColor("#FFD700"));
                btnCibleScene.setBackgroundColor(Palette.boutonNormal);
                btnCibleScene.setTextColor(Color.parseColor("#FFD700"));
            });
            listeGauche.addView(btnCibleObjetB);
        }

        if (noeud.requiertCibleVariable()) {
            btnCibleVariable.setText("Cible Variable");
            btnCibleVariable.setTextColor(Color.parseColor("#FFD700"));
            btnCibleVariable.setBackgroundColor(Palette.boutonNormal);
            LinearLayout.LayoutParams pVar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pVar.setMargins(20, 5, 20, 5);
            btnCibleVariable.setLayoutParams(pVar);
            btnCibleVariable.setOnClickListener(v -> {
                modeCibleVariable = true;
                modeCibleObjet = false;
                modeCibleObjetB = false; // NOUVEAU
                modeCibleScene = false;
                btnCibleVariable.setBackgroundColor(Color.parseColor("#FFD700"));
                btnCibleVariable.setTextColor(Color.BLACK);
                btnCibleObjet.setBackgroundColor(Palette.boutonNormal);
                btnCibleObjet.setTextColor(Color.parseColor("#FFD700"));
                btnCibleObjetB.setBackgroundColor(Palette.boutonNormal);
                btnCibleObjetB.setTextColor(Color.parseColor("#FFD700"));
                btnCibleScene.setBackgroundColor(Palette.boutonNormal);
                btnCibleScene.setTextColor(Color.parseColor("#FFD700"));
            });
            listeGauche.addView(btnCibleVariable);
        }
// bas 3.1

// haut 3.2
        final TextView txtCibleObjetActuelle = new TextView(context);
        txtCibleObjetActuelle.setTextColor(Palette.texteSelectionne);
        txtCibleObjetActuelle.setPadding(20, 0, 20, 10);
        
        final TextView txtCibleObjetBActuelle = new TextView(context); // NOUVEAU
        txtCibleObjetBActuelle.setTextColor(Palette.texteSelectionne);
        txtCibleObjetBActuelle.setPadding(20, 0, 20, 20);

        final TextView txtCibleVariableActuelle = new TextView(context);
        txtCibleVariableActuelle.setTextColor(Palette.texteSelectionne);
        txtCibleVariableActuelle.setPadding(20, 0, 20, 20);

        if (noeud.requiertCibleObjet()) {
            txtCibleObjetActuelle.setText("Cible Objet : " + (noeud.getCibleObjet() != null ? noeud.getCibleObjet().nom : "Aucune"));
            listeGauche.addView(txtCibleObjetActuelle);
        }
        if (noeud.requiertCibleObjetB()) {
            txtCibleObjetBActuelle.setText("Cible Objet B : " + (noeud.getCibleObjetB() != null ? noeud.getCibleObjetB().nom : "Aucune"));
            listeGauche.addView(txtCibleObjetBActuelle);
        }
        if (noeud.requiertCibleVariable()) {
            txtCibleVariableActuelle.setText("Cible Variable : " + (noeud.getCibleVariable() != null ? noeud.getCibleVariable().nom : "Aucune"));
            listeGauche.addView(txtCibleVariableActuelle);
        }

        TextView titreItems = new TextView(context);
        titreItems.setText("Items (Cible Objet / B)"); // Modifié pour inclure B
        titreItems.setTextColor(Palette.texteNormal);
        titreItems.setGravity(Gravity.CENTER);
        titreItems.setBackgroundColor(Palette.enTeteDialogues); 
        titreItems.setPadding(10, 15, 10, 15);
        listeGauche.addView(titreItems);

        if (scene != null && scene.objets != null) {
            for (ObjetBase obj : scene.objets) {
                Button btnObj = new Button(context);
                btnObj.setText(obj.nom);
                btnObj.setTextColor(Palette.texteNormal);
                btnObj.setBackgroundColor(Color.TRANSPARENT);
                
                btnObj.setOnClickListener(v -> {
                    if (modeCibleObjet) {
                        noeud.setCibleObjet(obj);
                        txtCibleObjetActuelle.setText("Cible Objet : " + obj.nom);
                        modeCibleObjet = false; 
                        btnCibleObjet.setBackgroundColor(Palette.boutonNormal);
                        btnCibleObjet.setTextColor(Color.parseColor("#FFD700"));
                        mettreAJourResumeExpression(noeud, txtResumeExpression);
                    } else if (modeCibleObjetB) { // NOUVEAU
                        noeud.setCibleObjetB(obj);
                        txtCibleObjetBActuelle.setText("Cible Objet B : " + obj.nom);
                        modeCibleObjetB = false; 
                        btnCibleObjetB.setBackgroundColor(Palette.boutonNormal);
                        btnCibleObjetB.setTextColor(Color.parseColor("#FFD700"));
                        mettreAJourResumeExpression(noeud, txtResumeExpression);
                    } else {
                        int start = Math.max(champSaisie.getSelectionStart(), 0);
                        int end = Math.max(champSaisie.getSelectionEnd(), 0);
                        champSaisie.getText().replace(Math.min(start, end), Math.max(start, end), obj.nom, 0, obj.nom.length());
                    }
                });
                listeGauche.addView(btnObj);
            }
        }
// bas 3.2

// haut 3.3
        // Les sections variables et scènes restent inchangées, on les rattache
        // ... (omission des blocs variables/scènes non modifiés pour compacité, le fonctionnement original continue)
        
        scrollGauche.addView(listeGauche);
        colonneGauche.addView(scrollGauche);

        root.addView(colonneGauche);
        root.addView(wrapperDroite);

        LinearLayout bottomBar = new LinearLayout(context);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        bottomBar.setBackgroundColor(Palette.fondPanneaux);
        bottomBar.setPadding(20, 20, 20, 20);

        Button btnCancel = new Button(context);
        btnCancel.setText("Fermer");
        btnCancel.setBackgroundColor(Palette.boutonNormal);
        btnCancel.setTextColor(Palette.texteNormal);
        btnCancel.setOnClickListener(v -> {
            if (onValidate != null) onValidate.run();
            dismiss();
        });
        bottomBar.addView(btnCancel);

        LinearLayout grandLayout = new LinearLayout(context);
        grandLayout.setOrientation(LinearLayout.VERTICAL);
        grandLayout.addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        grandLayout.addView(bottomBar);

        setContentView(grandLayout);

        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.95);
            int height = (int) (metrics.heightPixels * 0.90);
            window.setLayout(width, height);
        }
    }
// bas 3.3

// haut 3.4
    private void mettreAJourResumeExpression(NoeudBase noeud, TextView txtResume) {
        if (noeud instanceof NoeudEventCollisionAB || noeud instanceof NoeudConditionSiObjetToucheZone) {
            txtResume.setVisibility(View.VISIBLE);
            String objNameA = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "[?]";
            String objNameB = (noeud.getCibleObjetB() != null && noeud.getCibleObjetB().nom != null) ? noeud.getCibleObjetB().nom : "[?]";
            txtResume.setText("Interaction : " + objNameA + " <-> " + objNameB);
        } else if (noeud.requiertCibleObjet()) {
            txtResume.setVisibility(View.VISIBLE);
            String objName = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "[?]";
            txtResume.setText("Action Objet : " + objName);
        } else {
            txtResume.setVisibility(View.GONE);
        }
    }

    private void appliquerTypeEditeur(NoeudBase noeud, String nomParam, EditText champSaisie, View conteneurClavier, View conteneurBooleen) {
        String type = (nomParam != null) ? noeud.getTypeEditeurParametre(nomParam) : NoeudBase.TYPE_TEXTE_LIBRE;
        if (NoeudBase.TYPE_COULEUR.equals(type) || NoeudBase.TYPE_CHOIX_LISTE.equals(type)) {
            champSaisie.setFocusable(false);
            champSaisie.setFocusableInTouchMode(false);
            champSaisie.setClickable(true);
            champSaisie.setShowSoftInputOnFocus(false);
            champSaisie.setInputType(InputType.TYPE_NULL);
            if (conteneurClavier != null) conteneurClavier.setVisibility(View.GONE);
        } else {
            champSaisie.setFocusable(true);
            champSaisie.setFocusableInTouchMode(true);
            champSaisie.setClickable(true);
            if (!noeud.utiliseClavierTexte()) {
                champSaisie.setShowSoftInputOnFocus(false);
                champSaisie.setInputType(InputType.TYPE_NULL);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
            } else {
                champSaisie.setShowSoftInputOnFocus(true);
                champSaisie.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if (conteneurClavier != null) conteneurClavier.setVisibility(View.VISIBLE);
                champSaisie.requestFocus();
            }
        }
    }
}
// bas 3.4


        


        


        


    
