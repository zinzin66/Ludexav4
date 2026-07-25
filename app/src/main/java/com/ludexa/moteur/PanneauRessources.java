// haut 1
package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;

public class PanneauRessources extends ScrollView {

    private CanvasEditeur canvasEditeur;
    private LinearLayout conteneurScenes;
    private LinearLayout conteneurArborescence;
    private LinearLayout conteneurVariables;
    private Variable variableSelectionnee;
    private ObjetBase objetSelectionne;

    public PanneauRessources(Context context, CanvasEditeur canvas) {
        super(context);
        this.canvasEditeur = canvas;
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.parseColor("#333333"));
        setLayoutParams(new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        layoutPrincipal.addView(creerSectionScenes(context));
        layoutPrincipal.addView(creerSectionObjets(context));
        layoutPrincipal.addView(creerSectionArborescence(context));
        layoutPrincipal.addView(creerSectionAssets(context));
        layoutPrincipal.addView(creerSectionVariables(context));

        addView(layoutPrincipal);
    }

    private View creerSectionArborescence(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Arborescence ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        conteneurArborescence = new LinearLayout(context);
        conteneurArborescence.setOrientation(LinearLayout.VERTICAL);
        contenu.addView(conteneurArborescence);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Arborescence ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Arborescence ▼");
                rafraichirArborescence();
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    public void rafraichirArborescence() {
        if (conteneurArborescence == null) return;
        conteneurArborescence.removeAllViews();
        
        InterfaceEditeur editeur = (InterfaceEditeur) getContext();
        if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
            for (int i = 0; i < editeur.sceneActive.objets.size(); i++) {
                ObjetBase obj = editeur.sceneActive.objets.get(i);
                
                TextView txtObjet = new TextView(getContext());
                txtObjet.setText("• " + obj.nom);
                txtObjet.setTextColor(obj == objetSelectionne ? Color.YELLOW : Color.WHITE);
                txtObjet.setPadding(10, 10, 10, 10);
                txtObjet.setTextSize(14f);
                
                txtObjet.setOnClickListener(v -> {
                    objetSelectionne = obj;
                    rafraichirArborescence();
                });
                
                conteneurArborescence.addView(txtObjet);
            }
        }
        
        if (conteneurArborescence.getChildCount() == 0) {
            TextView txtVide = new TextView(getContext());
            txtVide.setText("Aucun objet dans la scène");
            txtVide.setTextColor(Color.LTGRAY);
            txtVide.setPadding(10, 10, 10, 10);
            conteneurArborescence.addView(txtVide);
        }
    }

    private String genererNomUnique(String prefixe, Scene scene) {
        if (scene == null || scene.objets == null) return prefixe + "1";
        int index = 1;
        while (true) {
            String testNom = prefixe + index;
            boolean existe = false;
            for (ObjetBase obj : scene.objets) {
                if (obj.nom.equals(testNom)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) return testNom;
            index++;
        }
    }

    private View creerSectionObjets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Objets à placer ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        Button btnAjouterCarre = new Button(context);
        btnAjouterCarre.setText("+ Ajouter un Carré");
        btnAjouterCarre.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique("Carré", editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 80f, 80f);
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
            Toast.makeText(context, nomUnique + " ajouté à la scène", Toast.LENGTH_SHORT).show();
        });

        Button btnAjouterTexte = new Button(context);
        btnAjouterTexte.setText("+ Ajouter un Texte");
        btnAjouterTexte.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique("Texte", editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 200f, 100f, 120f, 40f);
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
            Toast.makeText(context, nomUnique + " ajouté à la scène", Toast.LENGTH_SHORT).show();
        });

        Button btnAjouterRond = new Button(context);
        btnAjouterRond.setText("+ Ajouter un Rond");
        btnAjouterRond.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique("Rond", editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 100f, 200f, 90f, 90f);
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
            Toast.makeText(context, nomUnique + " ajouté à la scène", Toast.LENGTH_SHORT).show();
        });

        contenu.addView(btnAjouterCarre);
        contenu.addView(btnAjouterTexte);
        contenu.addView(btnAjouterRond);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Objets à placer ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Objets à placer ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }
// bas 1
// haut 2
    private View creerSectionScenes(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Scènes ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        conteneurScenes = new LinearLayout(context);
        conteneurScenes.setOrientation(LinearLayout.VERTICAL);
        conteneurScenes.setPadding(0, 0, 0, 20);
        contenu.addView(conteneurScenes);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnCreer = new Button(context);
        btnCreer.setText("Créer");
        btnCreer.setOnClickListener(v -> afficherPopupCreerScene(context));

        Button btnRenommer = new Button(context);
        btnRenommer.setText("Renommer");
        btnRenommer.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            afficherPopupRenommerScene(context, editeur.sceneActive);
        });

        Button btnSupprimer = new Button(context);
        btnSupprimer.setText("Supprimer");
        btnSupprimer.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            afficherPopupSupprimerScene(context, editeur.sceneActive);
        });

        zoneBoutons.addView(btnCreer);
        zoneBoutons.addView(btnRenommer);
        zoneBoutons.addView(btnSupprimer);

        contenu.addView(zoneBoutons);

        rafraichirScenes();

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Scènes ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Scènes ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    public void rafraichirScenes() {
        if (conteneurScenes == null) return;
        conteneurScenes.removeAllViews();
        InterfaceEditeur editeur = (InterfaceEditeur) getContext();

        if (editeur.listeScenes != null) {
            for (Scene s : editeur.listeScenes) {
                TextView nomScene = new TextView(getContext());
                nomScene.setText(s.nom);
                if (s == editeur.sceneActive) {
                    nomScene.setTextColor(Color.YELLOW);
                } else {
                    nomScene.setTextColor(Color.WHITE);
                }
                nomScene.setPadding(10, 15, 10, 15);
                nomScene.setTextSize(16f);
                
                nomScene.setOnClickListener(v -> {
                    editeur.changerScene(s);
                    rafraichirArborescence();
                });

                conteneurScenes.addView(nomScene);
            }
        }
    }

    private View creerSectionAssets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Assets ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        LinearLayout itemAsset = new LinearLayout(context);
        itemAsset.setOrientation(LinearLayout.VERTICAL);
        itemAsset.setPadding(0, 10, 0, 30);

        TextView nomAsset = new TextView(context);
        nomAsset.setText("Sprite_Joueur");
        nomAsset.setTextColor(Color.WHITE);
        nomAsset.setPadding(10, 0, 0, 10);
        nomAsset.setTextSize(16f);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnRenommer = new Button(context);
        btnRenommer.setText("Renommer");
        btnRenommer.setOnClickListener(v -> afficherPopupRenommer(context, "Sprite_Joueur"));

        Button btnSupprimer = new Button(context);
        btnSupprimer.setText("Supprimer");
        btnSupprimer.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer l'asset 'Sprite_Joueur' ?"));

        zoneBoutons.addView(btnRenommer);
        zoneBoutons.addView(btnSupprimer);

        itemAsset.addView(nomAsset);
        itemAsset.addView(zoneBoutons);

        contenu.addView(itemAsset);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Assets ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Assets ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    private View creerSectionVariables(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Variables ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        conteneurVariables = new LinearLayout(context);
        conteneurVariables.setOrientation(LinearLayout.VERTICAL);
        conteneurVariables.setPadding(0, 0, 0, 20);
        contenu.addView(conteneurVariables);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnCreer = new Button(context);
        btnCreer.setText("Créer");
        btnCreer.setOnClickListener(v -> afficherPopupCreerVariable(context));

        Button btnRenommer = new Button(context);
        btnRenommer.setText("Renommer");
        btnRenommer.setOnClickListener(v -> {
            if (variableSelectionnee != null) {
                afficherPopupRenommerVariable(context, variableSelectionnee);
            } else {
                Toast.makeText(context, "Veuillez d'abord sélectionner une variable", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSupprimer = new Button(context);
        btnSupprimer.setText("Supprimer");
        btnSupprimer.setOnClickListener(v -> {
            if (variableSelectionnee != null) {
                afficherPopupSupprimerVariable(context, variableSelectionnee);
            } else {
                Toast.makeText(context, "Veuillez d'abord sélectionner une variable", Toast.LENGTH_SHORT).show();
            }
        });

        zoneBoutons.addView(btnCreer);
        zoneBoutons.addView(btnRenommer);
        zoneBoutons.addView(btnSupprimer);

        contenu.addView(zoneBoutons);
        rafraichirVariables();

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Variables ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Variables ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    public void rafraichirVariables() {
        if (conteneurVariables == null) return;
        conteneurVariables.removeAllViews();
        InterfaceEditeur editeur = (InterfaceEditeur) getContext();

        if (editeur.variablesGlobales != null) {
            for (Variable var : editeur.variablesGlobales) {
                ajouterVueVariable(var);
            }
        }

        if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
            for (Variable var : editeur.sceneActive.variablesLocales) {
                ajouterVueVariable(var);
            }
        }
    }

    private void ajouterVueVariable(Variable var) {
        Context context = getContext();
        
        LinearLayout conteneurLigne = new LinearLayout(context);
        conteneurLigne.setOrientation(LinearLayout.VERTICAL);
        conteneurLigne.setPadding(0, 5, 0, 15);
        
        TextView nomVariable = new TextView(context);
        String labelType = var.type.equals("BOOLEEN") ? "Oui/Non" : (var.type.equals("CHIFFRE") ? "Chiffre" : "Texte");
        String labelScope = var.scope.equals("GLOBALE") ? "Globale" : "Locale";
        
        nomVariable.setText(var.nom + " [" + labelScope + ", " + labelType + "] = " + var.valeur);
        
        if (var == variableSelectionnee) {
            nomVariable.setTextColor(Color.YELLOW); 
        } else {
            if (var.scope.equals("GLOBALE")) {
                nomVariable.setTextColor(Color.parseColor("#ADD8E6")); 
            } else {
                nomVariable.setTextColor(Color.parseColor("#90EE90")); 
            }
        }
        
        nomVariable.setPadding(10, 5, 10, 5);
        nomVariable.setTextSize(16f);

        nomVariable.setOnClickListener(v -> {
            variableSelectionnee = var;
            rafraichirVariables();
        });

        conteneurLigne.addView(nomVariable);

        if (var == variableSelectionnee) {
            Button btnModifierVal = new Button(context);
            btnModifierVal.setText("Modifier la valeur");
            btnModifierVal.setOnClickListener(v -> Toast.makeText(context, "Modif. valeur bientôt disponible", Toast.LENGTH_SHORT).show());
            conteneurLigne.addView(btnModifierVal);
        }

        conteneurVariables.addView(conteneurLigne);
    }
// bas 2
   // haut 3
    private void afficherPopupCreerVariable(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Créer une variable");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setHint("Nom de la variable");
        layoutDialog.addView(champTexte);

        TextView txtScope = new TextView(context);
        txtScope.setText("Portée (Scope) :");
        layoutDialog.addView(txtScope);
        
        Spinner spinnerScope = new Spinner(context);
        ArrayAdapter<String> adapterScope = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new String[]{"Locale", "Globale"});
        adapterScope.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScope.setAdapter(adapterScope);
        layoutDialog.addView(spinnerScope);

        TextView txtType = new TextView(context);
        txtType.setText("Type :");
        layoutDialog.addView(txtType);

        Spinner spinnerType = new Spinner(context);
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new String[]{"Chiffre", "Texte", "Oui/Non"});
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        layoutDialog.addView(spinnerType);

        EditText champValeurInit = new EditText(context);
        champValeurInit.setHint("Valeur initiale (optionnel)");
        layoutDialog.addView(champValeurInit);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnValider = new Button(context);
        btnValider.setText("Valider");
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString().trim();
            if(nom.isEmpty()) {
                Toast.makeText(context, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            String scopeSelect = spinnerScope.getSelectedItem().toString().equals("Globale") ? "GLOBALE" : "LOCALE";
            String typeSelectText = spinnerType.getSelectedItem().toString();
            String typeSelect = "CHIFFRE";
            if (typeSelectText.equals("Texte")) typeSelect = "TEXTE";
            if (typeSelectText.equals("Oui/Non")) typeSelect = "BOOLEEN";

            InterfaceEditeur editeur = (InterfaceEditeur) context;
            
            if (scopeSelect.equals("GLOBALE")) {
                for (Variable vExistant : editeur.variablesGlobales) {
                    if (vExistant.nom.equals(nom)) {
                        Toast.makeText(context, "Une variable globale avec ce nom existe déjà", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else {
                for (Variable vExistant : editeur.sceneActive.variablesLocales) {
                    if (vExistant.nom.equals(nom)) {
                        Toast.makeText(context, "Une variable locale avec ce nom existe déjà dans cette scène", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            Variable nouvelleVar = new Variable(nom, scopeSelect, typeSelect);
            
            String valInitTexte = champValeurInit.getText().toString().trim();
            if (!valInitTexte.isEmpty()) {
                if (typeSelect.equals("CHIFFRE")) {
                    try {
                        nouvelleVar.valeur = Float.parseFloat(valInitTexte);
                    } catch (NumberFormatException e) {
                        nouvelleVar.valeur = 0f;
                    }
                } else if (typeSelect.equals("TEXTE")) {
                    nouvelleVar.valeur = valInitTexte;
                } else if (typeSelect.equals("BOOLEEN")) {
                    String cleanVal = valInitTexte.toLowerCase();
                    nouvelleVar.valeur = (cleanVal.equals("oui") || cleanVal.equals("vrai") || cleanVal.equals("true"));
                }
            }

            if (scopeSelect.equals("GLOBALE")) {
                editeur.variablesGlobales.add(nouvelleVar);
            } else {
                editeur.sceneActive.variablesLocales.add(nouvelleVar);
            }

            rafraichirVariables();
            dialog.dismiss();
        });

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerVariable(Context context, Variable var) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Renommer la variable");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setText(var.nom);
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnValider = new Button(context);
        btnValider.setText("Valider");
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if(nouveauNom.isEmpty()) {
                Toast.makeText(context, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            InterfaceEditeur editeur = (InterfaceEditeur) context;
            
            if (var.scope.equals("GLOBALE")) {
                for (Variable vExistant : editeur.variablesGlobales) {
                    if (vExistant != var && vExistant.nom.equals(nouveauNom)) {
                        Toast.makeText(context, "Une variable globale avec ce nom existe déjà", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else {
                for (Variable vExistant : editeur.sceneActive.variablesLocales) {
                    if (vExistant != var && vExistant.nom.equals(nouveauNom)) {
                        Toast.makeText(context, "Une variable locale avec ce nom existe déjà", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            var.nom = nouveauNom;
            rafraichirVariables();
            dialog.dismiss();
        });

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerVariable(Context context, Variable var) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Supprimer la variable");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        TextView txtMessage = new TextView(context);
        txtMessage.setText("Voulez-vous vraiment supprimer la variable '" + var.nom + "' ?");
        txtMessage.setPadding(0, 0, 0, 20);
        layoutDialog.addView(txtMessage);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnOui = new Button(context);
        btnOui.setText("Oui");
        btnOui.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            if (var.scope.equals("GLOBALE")) {
                editeur.variablesGlobales.remove(var);
            } else {
                editeur.sceneActive.variablesLocales.remove(var);
            }
            
            if (var == variableSelectionnee) {
                variableSelectionnee = null;
            }
            
            rafraichirVariables();
            dialog.dismiss();
        });

        Button btnNon = new Button(context);
        btnNon.setText("Non");
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupCreerScene(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Créer une scène");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setHint("Entrez le nom...");
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnValider = new Button(context);
        btnValider.setText("Valider");
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString();
            if(!nom.isEmpty()) {
                ((InterfaceEditeur)context).creerScene(nom);
                Toast.makeText(context, "Scène créée : " + nom, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerScene(Context context, Scene scene) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Renommer la scène");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setText(scene.nom);
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnValider = new Button(context);
        btnValider.setText("Valider");
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString();
            if(!nouveauNom.isEmpty()) {
                scene.nom = nouveauNom;
                rafraichirScenes();
                Toast.makeText(context, "Scène renommée", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerScene(Context context, Scene scene) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Supprimer la scène");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        TextView txtMessage = new TextView(context);
        txtMessage.setText("Voulez-vous vraiment supprimer la scène '" + scene.nom + "' ?");
        txtMessage.setPadding(0, 0, 0, 20);
        layoutDialog.addView(txtMessage);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnOui = new Button(context);
        btnOui.setText("Oui");
        btnOui.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            
            if (editeur.listeScenes.size() <= 1) {
                Toast.makeText(context, "Impossible de supprimer la seule scène du projet.", Toast.LENGTH_SHORT).show();
            } else {
                editeur.listeScenes.remove(scene);
                if (editeur.sceneActive == scene) {
                    editeur.changerScene(editeur.listeScenes.get(0));
                } else {
                    rafraichirScenes();
                }
                rafraichirArborescence();
                Toast.makeText(context, "Scène supprimée", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        Button btnNon = new Button(context);
        btnNon.setText("Non");
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommer(Context context, String nomActuel) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Renommer");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setText(nomActuel);
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnValider = new Button(context);
        btnValider.setText("Valider");
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString();
            Toast.makeText(context, "Renommé en : " + nouveauNom, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupConfirmation(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Confirmation");

        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        TextView txtMessage = new TextView(context);
        txtMessage.setText(message);
        txtMessage.setPadding(0, 0, 0, 20);
        layoutDialog.addView(txtMessage);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button btnOui = new Button(context);
        btnOui.setText("Oui");
        btnOui.setOnClickListener(v -> {
            Toast.makeText(context, "Action confirmée", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Button btnNon = new Button(context);
        btnNon.setText("Non");
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
// bas 3
                                   
