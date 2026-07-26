// haut 1
package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class InspecteurProprietes extends LinearLayout {

    private ScrollView scrollInspecteur;
    private TextView titreInspecteur;
    private Button boutonMasquer;
    private LinearLayout.LayoutParams paramsOuvert;
    private LinearLayout.LayoutParams paramsFerme;

    private TextView texteInfo;
    private LinearLayout blocProprietes;
    private EditText champNom;
    private Button btnValiderNom;
    private EditText champX;
    private EditText champY;
    private Button boutonSupprimer;

    private TextView valeurType;
    private EditText champLargeur, champHauteur, champRotation, champAlpha, champZOrder;
    private CheckBox cbVisible, cbVerrouille;
    private Button btnCouleur;
    
    private LinearLayout blocTexte;
    private EditText champContenu, champTaille;
    private Button btnCouleurTexte, btnPolice;

    private Scene sceneActive;
    private CanvasEditeur canvasEditeur;
    private ObjetBase objetCourant;
    private boolean miseAJourEnCours = false;

    public InspecteurProprietes(Context context, Scene scene, CanvasEditeur canvas) {
        super(context);
        this.sceneActive = scene;
        this.canvasEditeur = canvas;
        initialiserInterface(context);
    }

    private void initialiserInterface(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(0xFFE0E0E0);

        paramsOuvert = new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        LinearLayout enteteInspecteur = new LinearLayout(context);
        enteteInspecteur.setOrientation(LinearLayout.HORIZONTAL);
        enteteInspecteur.setPadding(10, 10, 10, 10);
        enteteInspecteur.setBackgroundColor(0xFFCCCCCC);

        titreInspecteur = new TextView(context);
        titreInspecteur.setText("Inspecteur");
        titreInspecteur.setTextSize(16f);
        titreInspecteur.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titreInspecteur.setLayoutParams(paramsTitre);

        boutonMasquer = new Button(context);
        boutonMasquer.setText(">");

        enteteInspecteur.addView(titreInspecteur);
        enteteInspecteur.addView(boutonMasquer);
        this.addView(enteteInspecteur);

        scrollInspecteur = new ScrollView(context);
        LinearLayout contenuInspecteur = new LinearLayout(context);
        contenuInspecteur.setOrientation(LinearLayout.VERTICAL);
        contenuInspecteur.setPadding(15, 15, 15, 15);

        texteInfo = new TextView(context);
        texteInfo.setText("Sélectionnez un objet sur la scène pour afficher et modifier ses propriétés.");
        texteInfo.setPadding(0, 0, 0, 30);
        contenuInspecteur.addView(texteInfo);

        blocProprietes = new LinearLayout(context);
        blocProprietes.setOrientation(LinearLayout.VERTICAL);
        blocProprietes.setVisibility(View.GONE);

        valeurType = new TextView(context);
        valeurType.setPadding(0, 0, 0, 15);
        valeurType.setTextColor(0xFF555555);
        valeurType.setTextSize(14f);
        blocProprietes.addView(valeurType);

        TextView labelNom = new TextView(context);
        labelNom.setText("Nom");
        blocProprietes.addView(labelNom);

        // NOUVEAU : Un layout horizontal pour aligner le champ Nom et le bouton OK
        LinearLayout layoutNom = new LinearLayout(context);
        layoutNom.setOrientation(LinearLayout.HORIZONTAL);
        
        champNom = new EditText(context);
        champNom.setSingleLine(true); // Force sur une seule ligne
        champNom.setImeOptions(EditorInfo.IME_ACTION_DONE); // Affiche le bouton valider sur le clavier
        champNom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        layoutNom.addView(champNom);
        
        btnValiderNom = new Button(context);
        btnValiderNom.setText("OK");
        layoutNom.addView(btnValiderNom);
        
        blocProprietes.addView(layoutNom);

        LinearLayout layoutPos = new LinearLayout(context);
        layoutPos.setOrientation(LinearLayout.HORIZONTAL);
        
        champX = new EditText(context);
        champX.setHint("X");
        champX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champX.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        
        champY = new EditText(context);
        champY.setHint("Y");
        champY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champY.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        
        layoutPos.addView(champX);
        layoutPos.addView(champY);
        blocProprietes.addView(layoutPos);

        View.OnClickListener toastListener = v -> Toast.makeText(context, "Réglage bientôt disponible", Toast.LENGTH_SHORT).show();

        LinearLayout layoutDim = new LinearLayout(context);
        layoutDim.setOrientation(LinearLayout.HORIZONTAL);
        
        champLargeur = new EditText(context);
        champLargeur.setHint("Largeur");
        champLargeur.setFocusable(false);
        champLargeur.setOnClickListener(toastListener);
        champLargeur.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        
        champHauteur = new EditText(context);
        champHauteur.setHint("Hauteur");
        champHauteur.setFocusable(false);
        champHauteur.setOnClickListener(toastListener);
        champHauteur.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        
        layoutDim.addView(champLargeur);
        layoutDim.addView(champHauteur);
        blocProprietes.addView(layoutDim);

        champRotation = new EditText(context);
        champRotation.setHint("Rotation (°)");
        champRotation.setFocusable(false);
        champRotation.setOnClickListener(toastListener);
        blocProprietes.addView(champRotation);

        btnCouleur = new Button(context);
        btnCouleur.setText("Couleur : Sélecteur");
        btnCouleur.setOnClickListener(toastListener);
        blocProprietes.addView(btnCouleur);

        champAlpha = new EditText(context);
        champAlpha.setHint("Transparence (0-1)");
        champAlpha.setFocusable(false);
        champAlpha.setOnClickListener(toastListener);
        blocProprietes.addView(champAlpha);

        cbVisible = new CheckBox(context);
        cbVisible.setText("Visible");
        cbVisible.setOnClickListener(toastListener);
        blocProprietes.addView(cbVisible);

        cbVerrouille = new CheckBox(context);
        cbVerrouille.setText("Verrouillé (empêche l'édition)");
        cbVerrouille.setOnClickListener(toastListener);
        blocProprietes.addView(cbVerrouille);

        champZOrder = new EditText(context);
        champZOrder.setHint("Calque (Z-Order)");
        champZOrder.setFocusable(false);
        champZOrder.setOnClickListener(toastListener);
        blocProprietes.addView(champZOrder);
// bas 1

// haut 2
        blocTexte = new LinearLayout(context);
        blocTexte.setOrientation(LinearLayout.VERTICAL);
        blocTexte.setPadding(0, 15, 0, 0);

        TextView sepTexte = new TextView(context);
        sepTexte.setText("--- Propriétés Spécifiques Texte ---");
        sepTexte.setTextColor(0xFF888888);
        sepTexte.setPadding(0, 10, 0, 10);
        blocTexte.addView(sepTexte);

        champContenu = new EditText(context);
        champContenu.setHint("Contenu du texte");
        champContenu.setFocusable(false);
        champContenu.setOnClickListener(v -> {
            if (objetCourant == null) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modifier le texte");
            
            final EditText input = new EditText(context);
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setSingleLine(false);
            input.setLines(5);
            input.setGravity(Gravity.TOP | Gravity.START);
            input.setText(objetCourant.contenuTexte);
            
            builder.setView(input);
            builder.setPositiveButton("Valider", (dialog, which) -> {
                String nouveauTexte = input.getText().toString();
                objetCourant.contenuTexte = nouveauTexte;
                miseAJourEnCours = true;
                champContenu.setText(nouveauTexte);
                miseAJourEnCours = false;
                canvasEditeur.invalidate();
            });
            builder.setNegativeButton("Annuler", null);
            builder.show();
        });
        blocTexte.addView(champContenu);

        champTaille = new EditText(context);
        champTaille.setHint("Taille de police");
        champTaille.setFocusable(false);
        champTaille.setOnClickListener(toastListener);
        blocTexte.addView(champTaille);

        btnCouleurTexte = new Button(context);
        btnCouleurTexte.setText("Couleur du texte");
        btnCouleurTexte.setOnClickListener(toastListener);
        blocTexte.addView(btnCouleurTexte);

        btnPolice = new Button(context);
        btnPolice.setText("Police : Sélecteur");
        btnPolice.setOnClickListener(toastListener);
        blocTexte.addView(btnPolice);

        blocProprietes.addView(blocTexte);
        contenuInspecteur.addView(blocProprietes);

        boutonSupprimer = new Button(context);
        boutonSupprimer.setText("Supprimer l'objet");
        boutonSupprimer.setBackgroundColor(0xFFFFCCCC);
        boutonSupprimer.setOnClickListener(v -> {
            if (objetCourant == null) {
                Toast.makeText(context, "Aucun objet sélectionné", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer cet objet de la scène ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        sceneActive.objets.remove(objetCourant);
                        canvasEditeur.deselectionner();
                        afficherObjet(null);
                        canvasEditeur.invalidate();
                        Toast.makeText(context, "Objet supprimé", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
        
        LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsBtn.setMargins(0, 30, 0, 0);
        boutonSupprimer.setLayoutParams(paramsBtn);
        
        contenuInspecteur.addView(boutonSupprimer);

        scrollInspecteur.addView(contenuInspecteur);
        this.addView(scrollInspecteur);

        boutonMasquer.setOnClickListener(v -> {
            if (scrollInspecteur.getVisibility() == View.VISIBLE) {
                scrollInspecteur.setVisibility(View.GONE);
                titreInspecteur.setVisibility(View.GONE);
                boutonMasquer.setText("<");
                this.setLayoutParams(paramsFerme);
            } else {
                scrollInspecteur.setVisibility(View.VISIBLE);
                titreInspecteur.setVisibility(View.VISIBLE);
                boutonMasquer.setText(">");
                this.setLayoutParams(paramsOuvert);
            }
        });

        // NOUVEAU : Validation propre avec le bouton OK et le clavier
        champNom.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifierEtConfirmerRenommage(context);
                cacherClavier(context, v);
                return true;
            }
            return false;
        });

        btnValiderNom.setOnClickListener(v -> {
            verifierEtConfirmerRenommage(context);
            cacherClavier(context, champNom);
        });

        champX.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try {
                    objetCourant.x = Float.parseFloat(texte);
                    canvasEditeur.invalidate();
                } catch (NumberFormatException ignored) {}
            }
        }));
        champY.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try {
                    objetCourant.y = Float.parseFloat(texte);
                    canvasEditeur.invalidate();
                } catch (NumberFormatException ignored) {}
            }
        }));
    }

    private void cacherClavier(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void verifierEtConfirmerRenommage(Context context) {
        if (objetCourant == null) return;
        String nouveauNom = champNom.getText().toString();
        String ancienNom = objetCourant.nom;
        
        if (!nouveauNom.equals(ancienNom) && !miseAJourEnCours) {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Renommer " + ancienNom + " en " + nouveauNom + " ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        objetCourant.nom = nouveauNom;
                        canvasEditeur.invalidate();
                    })
                    .setNegativeButton("Non", (dialog, which) -> {
                        miseAJourEnCours = true;
                        champNom.setText(ancienNom);
                        miseAJourEnCours = false;
                    })
                    .setOnCancelListener(dialog -> {
                        miseAJourEnCours = true;
                        champNom.setText(ancienNom);
                        miseAJourEnCours = false;
                    })
                    .show();
        }
    }

    public void afficherObjet(ObjetBase objet) {
        this.objetCourant = objet;
        miseAJourEnCours = true;

        if (objet == null) {
            texteInfo.setVisibility(View.VISIBLE);
            blocProprietes.setVisibility(View.GONE);
            boutonSupprimer.setVisibility(View.GONE);
        } else {
            texteInfo.setVisibility(View.GONE);
            blocProprietes.setVisibility(View.VISIBLE);
            boutonSupprimer.setVisibility(View.VISIBLE);
            
            champNom.setText(objet.nom);
            champX.setText(String.valueOf((int) objet.x));
            champY.setText(String.valueOf((int) objet.y));
            
            String nomType = objet.type != null ? objet.type.substring(0, 1).toUpperCase() + objet.type.substring(1) : "Inconnu";
            valeurType.setText("Type : " + nomType);
            champLargeur.setText(String.valueOf((int) objet.largeur));
            champHauteur.setText(String.valueOf((int) objet.hauteur));
            cbVisible.setChecked(true);
            
            if ("texte".equals(objet.type)) {
                blocTexte.setVisibility(View.VISIBLE);
                champContenu.setText(objet.contenuTexte);
            } else {
                blocTexte.setVisibility(View.GONE);
            }
        }

        miseAJourEnCours = false;
    }

    private TextWatcher creerWatcherSimple(java.util.function.Consumer<String> action) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!miseAJourEnCours) {
                    action.accept(s.toString());
                }
            }
        };
    }
}
// bas 2
                
        
    
