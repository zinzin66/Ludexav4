// haut 1 12 août 
package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

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
    private EditText champScaleX, champScaleY;
    private CheckBox cbVisible, cbVerrouille;
    private Button btnCouleur;
    private Button btnParent;

    private LinearLayout blocTexte;
    private EditText champContenu, champTaille;
    private Button btnCouleurTexte, btnPolice;

    private LinearLayout blocImage;
    private Button btnChargerImage, btnSupprimerImage;
    private CheckBox cbFondColore;
    private CheckBox cbRamassable, cbZoneDeClic, cbDeplacable;

    private Scene sceneActive;
    private CanvasEditeur canvasEditeur;
    private ObjetBase objetCourant;
    private boolean miseAJourEnCours = false;

    private String cheminProjet;

    public InspecteurProprietes(Context context, Scene scene, CanvasEditeur canvas) {
        super(context);
        this.sceneActive = scene;
        this.canvasEditeur = canvas;
        initialiserInterface(context);
    }

    public void setCheminProjet(String cheminProjet) {
        this.cheminProjet = cheminProjet;
    }

    public void setSceneActive(Scene scene) {
        this.sceneActive = scene;
    }

    private int dp(int valeur) {
        return (int) (valeur * getResources().getDisplayMetrics().density);
    }

    private android.graphics.drawable.GradientDrawable fond(int couleurFond, int couleurBordure, int rayon) {
        android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
        g.setColor(couleurFond);
        g.setCornerRadius(dp(rayon));
        g.setStroke(dp(1), couleurBordure);
        return g;
    }

    private void styliserSection(LinearLayout contenu) {
        contenu.setBackground(fond(Palette.fondNormal, Palette.bordure, 10));
        contenu.setPadding(dp(10), dp(10), dp(10), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(6), 0, dp(10));
        contenu.setLayoutParams(lp);
    }

    private void styliserSousTitre(TextView t) {
        t.setTextColor(Palette.texteSelectionne);
        t.setTextSize(15f);
        t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setPadding(dp(12), dp(9), dp(12), dp(9));
        t.setBackground(fond(Palette.enTeteDialogues, Palette.bordure, 10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(8), 0, dp(2));
        t.setLayoutParams(lp);
    }

    private void styliserLabel(TextView t) {
        t.setTextColor(Palette.texteNormal);
        t.setTextSize(13f);
        t.setPadding(dp(2), dp(8), dp(2), dp(4));
    }

    private void styliserChamp(EditText champ) {
        champ.setTextColor(Palette.texteNormal);
        champ.setHintTextColor(Palette.bordure);
        champ.setBackground(fond(Palette.fondNormal, Palette.bordure, 8));
        champ.setPadding(dp(12), dp(10), dp(12), dp(10));
        champ.setTextSize(15f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(3), dp(3), dp(3), dp(3));
        champ.setLayoutParams(lp);
    }

    private void styliserChampFlexible(EditText champ) {
        styliserChamp(champ);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(3), dp(3), dp(3), dp(3));
        champ.setLayoutParams(lp);
    }

    private void styliserBouton(Button b) {
        b.setAllCaps(false);
        b.setTextColor(Palette.texteNormal);
        b.setTextSize(14f);
        b.setBackground(fond(Palette.boutonNormal, Palette.bordure, 8));
        b.setPadding(dp(14), dp(9), dp(14), dp(9));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(3), dp(4), dp(3), dp(4));
        b.setLayoutParams(lp);
    }

    private void styliserCase(CheckBox cb) {
        cb.setTextColor(Palette.texteNormal);
        cb.setTextSize(14f);
        cb.setButtonTintList(android.content.res.ColorStateList.valueOf(Palette.texteSelectionne));
        cb.setPadding(dp(8), dp(6), dp(8), dp(6));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(3), dp(2), dp(3), dp(2));
        cb.setLayoutParams(lp);
    }

    private void initialiserInterface(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(Palette.fondPanneaux);

        paramsOuvert = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        LinearLayout enteteInspecteur = new LinearLayout(context);
        enteteInspecteur.setOrientation(LinearLayout.HORIZONTAL);
        enteteInspecteur.setPadding(dp(12), dp(10), dp(12), dp(10));
        enteteInspecteur.setBackground(fond(Palette.enTeteDialogues, Palette.bordure, 0));
        enteteInspecteur.setGravity(Gravity.CENTER_VERTICAL);

        titreInspecteur = new TextView(context);
        titreInspecteur.setText("INSPECTEUR");
        titreInspecteur.setTextSize(17f);
        titreInspecteur.setLetterSpacing(0.08f);
        titreInspecteur.setTypeface(null, android.graphics.Typeface.BOLD);
        titreInspecteur.setGravity(Gravity.CENTER_VERTICAL);
        titreInspecteur.setTextColor(Palette.texteSelectionne);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titreInspecteur.setLayoutParams(paramsTitre);

        boutonMasquer = new Button(context);
        boutonMasquer.setText(">");
        boutonMasquer.setAllCaps(false);
        boutonMasquer.setTextColor(Palette.iconeNormal);
        boutonMasquer.setBackground(fond(Palette.boutonNormal, Palette.bordure, 8));
        boutonMasquer.setPadding(dp(10), dp(6), dp(10), dp(6));
        LinearLayout.LayoutParams paramsMasquer = new LinearLayout.LayoutParams(dp(44), dp(40));
        boutonMasquer.setLayoutParams(paramsMasquer);

        enteteInspecteur.addView(titreInspecteur);
        enteteInspecteur.addView(boutonMasquer);
        this.addView(enteteInspecteur);

        scrollInspecteur = new ScrollView(context);
        LinearLayout contenuInspecteur = new LinearLayout(context);
        contenuInspecteur.setOrientation(LinearLayout.VERTICAL);
        contenuInspecteur.setPadding(dp(10), dp(8), dp(10), dp(16));

        texteInfo = new TextView(context);
        texteInfo.setText("Sélectionnez un objet sur la scène pour afficher et modifier ses propriétés.");
        texteInfo.setPadding(dp(12), dp(14), dp(12), dp(14));
        texteInfo.setTextSize(13f);
        texteInfo.setTextColor(Palette.texteNormal);
        texteInfo.setBackground(fond(Palette.fondNormal, Palette.bordure, 10));
        contenuInspecteur.addView(texteInfo);

        blocProprietes = new LinearLayout(context);
        blocProprietes.setOrientation(LinearLayout.VERTICAL);
        blocProprietes.setVisibility(View.GONE);

        valeurType = new TextView(context);
        valeurType.setPadding(dp(12), dp(10), dp(12), dp(10));
        valeurType.setTextColor(Palette.texteSelectionne);
        valeurType.setTextSize(14f);
        valeurType.setTypeface(null, android.graphics.Typeface.BOLD);
        valeurType.setBackground(fond(Palette.fondListe, Palette.bordure, 10));
        blocProprietes.addView(valeurType);

        TextView labelNom = new TextView(context);
        labelNom.setText("Nom");
        styliserLabel(labelNom);
        blocProprietes.addView(labelNom);

        LinearLayout layoutNom = new LinearLayout(context);
        layoutNom.setOrientation(LinearLayout.HORIZONTAL);
        layoutNom.setGravity(Gravity.CENTER_VERTICAL);

        champNom = new EditText(context);
        champNom.setSingleLine(true);
        champNom.setImeOptions(EditorInfo.IME_ACTION_DONE);
        styliserChampFlexible(champNom);
        layoutNom.addView(champNom);

        btnValiderNom = new Button(context);
        btnValiderNom.setText("OK");
        styliserBouton(btnValiderNom);
        btnValiderNom.setLayoutParams(new LinearLayout.LayoutParams(dp(64), LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutNom.addView(btnValiderNom);

        blocProprietes.addView(layoutNom);

        TextView labelPos = new TextView(context);
        labelPos.setText("Position X / Y");
        styliserLabel(labelPos);
        blocProprietes.addView(labelPos);

        LinearLayout layoutPos = new LinearLayout(context);
        layoutPos.setOrientation(LinearLayout.HORIZONTAL);

        champX = new EditText(context);
        champX.setHint("X");
        champX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champX);

        champY = new EditText(context);
        champY.setHint("Y");
        champY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champY);

        layoutPos.addView(champX);
        layoutPos.addView(champY);
        blocProprietes.addView(layoutPos);

        View.OnClickListener toastListener = v -> Toast.makeText(context, "Réglage bientôt disponible", Toast.LENGTH_SHORT).show();

        TextView labelDim = new TextView(context);
        labelDim.setText("Largeur / Hauteur");
        styliserLabel(labelDim);
        blocProprietes.addView(labelDim);

        LinearLayout layoutDim = new LinearLayout(context);
        layoutDim.setOrientation(LinearLayout.HORIZONTAL);

        champLargeur = new EditText(context);
        champLargeur.setHint("Largeur");
        champLargeur.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        styliserChampFlexible(champLargeur);

        champHauteur = new EditText(context);
        champHauteur.setHint("Hauteur");
        champHauteur.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        styliserChampFlexible(champHauteur);

        layoutDim.addView(champLargeur);
        layoutDim.addView(champHauteur);
        blocProprietes.addView(layoutDim);

        TextView labelScale = new TextView(context);
        labelScale.setText("Echelle X / Y (Scale)");
        styliserLabel(labelScale);
        blocProprietes.addView(labelScale);

        LinearLayout layoutScale = new LinearLayout(context);
        layoutScale.setOrientation(LinearLayout.HORIZONTAL);

        champScaleX = new EditText(context);
        champScaleX.setHint("Scale X");
        champScaleX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champScaleX);

        champScaleY = new EditText(context);
        champScaleY.setHint("Scale Y");
        champScaleY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champScaleY);

        layoutScale.addView(champScaleX);
        layoutScale.addView(champScaleY);
        blocProprietes.addView(layoutScale);

        TextView labelRotation = new TextView(context);
        labelRotation.setText("Rotation");
        styliserLabel(labelRotation);
        blocProprietes.addView(labelRotation);

        champRotation = new EditText(context);
        champRotation.setHint("Rotation (°)");
        champRotation.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChamp(champRotation);
        blocProprietes.addView(champRotation);

        btnCouleur = new Button(context);
        btnCouleur.setText("Couleur : Sélecteur");
        styliserBouton(btnCouleur);
        blocProprietes.addView(btnCouleur);

        champAlpha = new EditText(context);
        champAlpha.setHint("Transparence (0-1)");
        champAlpha.setFocusable(false);
        champAlpha.setOnClickListener(toastListener);
        styliserChamp(champAlpha);
        blocProprietes.addView(champAlpha);

        cbVisible = new CheckBox(context);
        cbVisible.setText("Visible");
        styliserCase(cbVisible);
        blocProprietes.addView(cbVisible);

        cbVerrouille = new CheckBox(context);
        cbVerrouille.setText("Verrouillé (empêche l'édition)");
        styliserCase(cbVerrouille);
        blocProprietes.addView(cbVerrouille);

        TextView labelZOrder = new TextView(context);
        labelZOrder.setText("Calque (Z-Order)");
        styliserLabel(labelZOrder);
        blocProprietes.addView(labelZOrder);

        champZOrder = new EditText(context);
        champZOrder.setHint("Calque (Z-Order)");
        champZOrder.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChamp(champZOrder);
        blocProprietes.addView(champZOrder);

        TextView labelParent = new TextView(context);
        labelParent.setText("Objet Parent");
        styliserLabel(labelParent);
        blocProprietes.addView(labelParent);

        btnParent = new Button(context);
        btnParent.setText("Parent : Aucun");
        styliserBouton(btnParent);
        blocProprietes.addView(btnParent);
// bas 1



// haut 2
        btnParent.setOnClickListener(v -> {
            if (objetCourant == null) return;

            List<String> noms = new ArrayList<>();
            List<String> ids = new ArrayList<>();

            noms.add("Aucun");
            ids.add(null);

            for (ObjetBase o : sceneActive.objets) {
                if (o != objetCourant) {
                    noms.add(o.nom != null ? o.nom : "Objet sans nom");
                    ids.add(o.id);
                }
            }

            new AlertDialog.Builder(context)
                .setTitle("Sélectionner un parent")
                .setItems(noms.toArray(new String[0]), (dialog, which) -> {
                    String idChoisi = ids.get(which);
                    if (ObjetBase.verifierBoucleParent(objetCourant.id, idChoisi, sceneActive.objets)) {
                        objetCourant.parentId = idChoisi;
                        canvasEditeur.invalidate();
                        afficherObjet(objetCourant);
                    } else {
                        Toast.makeText(context, "Erreur : Boucle hiérarchique détectée", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        });

        blocTexte = new LinearLayout(context);
        blocTexte.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocTexte);

        TextView sepTexte = new TextView(context);
        sepTexte.setText("Propriétés Texte");
        styliserSousTitre(sepTexte);
        blocTexte.addView(sepTexte);

        champContenu = new EditText(context);
        champContenu.setHint("Contenu du texte");
        champContenu.setFocusable(false);
        styliserChamp(champContenu);
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
            styliserChamp(input);

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
        champTaille.setOnClickListener(v -> {
            if (objetCourant == null) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Taille de police");

            final EditText input = new EditText(context);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setText(String.valueOf(objetCourant.tailleFonte));
            styliserChamp(input);

            builder.setView(input);
            builder.setPositiveButton("Valider", (dialog, which) -> {
                try {
                    float nouvelleTaille = Float.parseFloat(input.getText().toString());
                    objetCourant.tailleFonte = nouvelleTaille;
                    miseAJourEnCours = true;
                    champTaille.setText(String.valueOf(nouvelleTaille));
                    miseAJourEnCours = false;
                    canvasEditeur.invalidate();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Valeur invalide", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Annuler", null);
            builder.show();
        });
        styliserChamp(champTaille);
        blocTexte.addView(champTaille);

        btnCouleurTexte = new Button(context);
        btnCouleurTexte.setText("Couleur du texte");
        styliserBouton(btnCouleurTexte);
        blocTexte.addView(btnCouleurTexte);

        btnPolice = new Button(context);
        btnPolice.setText("Police : Sélecteur");
        btnPolice.setOnClickListener(v -> {
            if (objetCourant == null) return;

            if (cheminProjet == null) {
                Toast.makeText(context, "Le chemin du projet n'est pas défini", Toast.LENGTH_SHORT).show();
                return;
            }

            java.io.File dossierPolices = new java.io.File(cheminProjet, "assets_ludexa/Fonts");
            List<String> polices = listerPolicesLocales(dossierPolices, "assets_ludexa/Fonts/");

            if (polices.isEmpty()) {
                Toast.makeText(context, "Aucune police trouvée dans les assets", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> options = new ArrayList<>();
            options.add("Police par défaut");
            options.addAll(polices);

            new AlertDialog.Builder(context)
                .setTitle("Sélectionner une police")
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        objetCourant.cheminPolice = null;
                    } else {
                        objetCourant.cheminPolice = options.get(which);
                    }
                    canvasEditeur.invalidate();
                    afficherObjet(objetCourant);
                })
                .show();
        });
        styliserBouton(btnPolice);
        blocTexte.addView(btnPolice);

        blocProprietes.addView(blocTexte);

        // --- BLOC IMAGE ---
        blocImage = new LinearLayout(context);
        blocImage.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocImage);

        TextView sepImage = new TextView(context);
        sepImage.setText("Propriétés Image");
        styliserSousTitre(sepImage);
        blocImage.addView(sepImage);

        btnChargerImage = new Button(context);
        btnChargerImage.setText("Charger une image (Assets)");
        styliserBouton(btnChargerImage);
        blocImage.addView(btnChargerImage);

        btnSupprimerImage = new Button(context);
        btnSupprimerImage.setText("Supprimer l'image");
        styliserBouton(btnSupprimerImage);
        blocImage.addView(btnSupprimerImage);

        cbFondColore = new CheckBox(context);
        cbFondColore.setText("Afficher le fond coloré");
        styliserCase(cbFondColore);
        blocImage.addView(cbFondColore);

        cbRamassable = new CheckBox(context);
        cbRamassable.setText("Ramassable (peut aller dans l'inventaire)");
        styliserCase(cbRamassable);
        blocProprietes.addView(cbRamassable);

        cbZoneDeClic = new CheckBox(context);
        cbZoneDeClic.setText("Zone de clic (hitbox invisible)");
        styliserCase(cbZoneDeClic);
        blocProprietes.addView(cbZoneDeClic);

        cbDeplacable = new CheckBox(context);
        cbDeplacable.setText("Déplaçable (glissable en mode Play)");
        styliserCase(cbDeplacable);
        blocProprietes.addView(cbDeplacable);

        blocProprietes.addView(blocImage);

        contenuInspecteur.addView(blocProprietes);

        boutonSupprimer = new Button(context);
        boutonSupprimer.setText("Supprimer l'objet");
        boutonSupprimer.setAllCaps(false);
        boutonSupprimer.setTextSize(15f);
        boutonSupprimer.setTextColor(Palette.texteNormal);
        boutonSupprimer.setBackground(fond(Color.parseColor("#8B3A3A"), Palette.bordure, 10));
        boutonSupprimer.setPadding(dp(14), dp(11), dp(14), dp(11));
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
        paramsBtn.setMargins(dp(3), dp(22), dp(3), dp(6));
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

        btnChargerImage.setOnClickListener(v -> {
            if (objetCourant == null) return;

            if (cheminProjet == null) {
                Toast.makeText(context, "Le chemin du projet n'est pas défini", Toast.LENGTH_SHORT).show();
                return;
            }

            java.io.File dossierImages = new java.io.File(cheminProjet, "assets_ludexa/Images");
            List<String> images = listerImagesLocales(dossierImages, "assets_ludexa/Images/");

            if (images.isEmpty()) {
                Toast.makeText(context, "Aucune image trouvée dans les assets", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(context)
                .setTitle("Sélectionner une image")
                .setItems(images.toArray(new String[0]), (dialog, which) -> {
                    objetCourant.cheminImage = images.get(which);
                    canvasEditeur.invalidate();
                    afficherObjet(objetCourant);
                })
                .show();
        });

        btnSupprimerImage.setOnClickListener(v -> {
            if (objetCourant == null) return;
            objetCourant.cheminImage = null;
            canvasEditeur.invalidate();
            afficherObjet(objetCourant);
        });

        cbFondColore.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (objetCourant != null && !miseAJourEnCours) {
                objetCourant.afficherFondColore = isChecked;
                canvasEditeur.invalidate();
            }
        });

        cbRamassable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (objetCourant != null && !miseAJourEnCours) {
                objetCourant.estRamassable = isChecked;
            }
        });

        cbZoneDeClic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (objetCourant != null && !miseAJourEnCours) {
                objetCourant.estZoneDeClic = isChecked;
            }
        });

        cbDeplacable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (objetCourant != null && !miseAJourEnCours) {
                objetCourant.estDeplacable = isChecked;
            }
        });

        cbVerrouille.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (objetCourant != null && !miseAJourEnCours) {
                objetCourant.estVerrouille = isChecked;
                canvasEditeur.invalidate();
            }
        });

        champX.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.x = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));
        champY.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.y = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));
        champLargeur.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.largeur = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));
        champHauteur.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.hauteur = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));

        champScaleX.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.scaleX = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));
        champScaleY.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.scaleY = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));

        champRotation.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.rotation = Float.parseFloat(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));
        champZOrder.addTextChangedListener(creerWatcherSimple(texte -> {
            if (objetCourant != null) {
                try { objetCourant.zOrder = Integer.parseInt(texte); canvasEditeur.invalidate(); } catch (NumberFormatException ignored) {}
            }
        }));

        cbVisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (objetCourant != null && !miseAJourEnCours) {
                objetCourant.visible = isChecked;
                canvasEditeur.invalidate();
            }
        });

        View.OnClickListener selecteurCouleurListener = v -> {
            if (objetCourant == null) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Sélectionner une couleur");
            String[] couleursNoms = {"Bleu (Défaut)", "Rouge", "Vert", "Noir", "Blanc", "Jaune", "Magenta", "Cyan"};
            int[] couleursValeurs = {Color.BLUE, Color.RED, Color.GREEN, Color.BLACK, Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.CYAN};

            builder.setItems(couleursNoms, (dialog, which) -> {
                objetCourant.couleur = couleursValeurs[which];
                canvasEditeur.invalidate();
            });
            builder.show();
        };

        btnCouleur.setOnClickListener(selecteurCouleurListener);
        btnCouleurTexte.setOnClickListener(selecteurCouleurListener);
    }

    private void cacherClavier(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void verifierEtConfirmerRenommage(Context context) {
        if (objetCourant == null) return;
        String nouveauNom = champNom.getText().toString().trim();
        String ancienNom = objetCourant.nom != null ? objetCourant.nom : "";

        if (!nouveauNom.equals(ancienNom) && !miseAJourEnCours) {

            if (sceneActive != null && sceneActive.objets != null) {
                for (ObjetBase obj : sceneActive.objets) {
                    if (!obj.id.equals(objetCourant.id) && obj.nom != null && obj.nom.trim().equalsIgnoreCase(nouveauNom)) {
                        new AlertDialog.Builder(context)
                                .setTitle("Impossible")
                                .setMessage("Un objet nommé '" + nouveauNom + "' existe déjà dans cette scène.")
                                .setPositiveButton("OK", null)
                                .show();

                        miseAJourEnCours = true;
                        champNom.setText(ancienNom);
                        miseAJourEnCours = false;
                        return; 
                    }
                }
            }

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

            champScaleX.setText(String.valueOf(objet.scaleX));
            champScaleY.setText(String.valueOf(objet.scaleY));

            champRotation.setText(String.valueOf((int) objet.rotation));
            champZOrder.setText(String.valueOf(objet.zOrder));
            cbVisible.setChecked(objet.visible);

            String nomParent = "Aucun";
            if (objet.parentId != null) {
                for (ObjetBase o : sceneActive.objets) {
                    if (o.id.equals(objet.parentId)) {
                        nomParent = o.nom != null ? o.nom : "Objet sans nom";
                        break;
                    }
                }
            }
            btnParent.setText("Parent : " + nomParent);

            cbRamassable.setChecked(objet.estRamassable);
            cbZoneDeClic.setChecked(objet.estZoneDeClic);
            cbDeplacable.setChecked(objet.estDeplacable);
            cbVerrouille.setChecked(objet.estVerrouille);

            if ("texte".equals(objet.type)) {
                blocTexte.setVisibility(View.VISIBLE);
                champContenu.setText(objet.contenuTexte);

                champTaille.setText(String.valueOf(objet.tailleFonte));
                if (objet.cheminPolice != null) {
                    java.io.File f = new java.io.File(objet.cheminPolice);
                    btnPolice.setText("Police : " + f.getName());
                } else {
                    btnPolice.setText("Police : Sélecteur");
                }

                blocImage.setVisibility(View.GONE);
            } else {
                blocTexte.setVisibility(View.GONE);
                blocImage.setVisibility(View.VISIBLE);

                if (objet.cheminImage != null) {
                    btnSupprimerImage.setVisibility(View.VISIBLE);
                    cbFondColore.setVisibility(View.VISIBLE);
                    cbFondColore.setChecked(objet.afficherFondColore);
                } else {
                    btnSupprimerImage.setVisibility(View.GONE);
                    cbFondColore.setVisibility(View.GONE);
                }
            }
        }

        miseAJourEnCours = false;
    }

    private List<String> listerImagesLocales(java.io.File dir, String cheminBase) {
        List<String> resultats = new ArrayList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            java.io.File[] fichiers = dir.listFiles();
            if (fichiers != null) {
                for (java.io.File f : fichiers) {
                    if (f.isDirectory()) {
                        resultats.addAll(listerImagesLocales(f, cheminBase + f.getName() + "/"));
                    } else {
                        String nom = f.getName().toLowerCase();
                        if (nom.endsWith(".png") || nom.endsWith(".jpg") || nom.endsWith(".jpeg") || nom.endsWith(".webp")) {
                            resultats.add(cheminBase + f.getName());
                        }
                    }
                }
            }
        }
        return resultats;
    }

    private List<String> listerPolicesLocales(java.io.File dir, String cheminBase) {
        List<String> resultats = new ArrayList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            java.io.File[] fichiers = dir.listFiles();
            if (fichiers != null) {
                for (java.io.File f : fichiers) {
                    if (f.isDirectory()) {
                        resultats.addAll(listerPolicesLocales(f, cheminBase + f.getName() + "/"));
                    } else {
                        String nom = f.getName().toLowerCase();
                        if (nom.endsWith(".ttf") || nom.endsWith(".otf")) {
                            resultats.add(cheminBase + f.getName());
                        }
                    }
                }
            }
        }
        return resultats;
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
// bas 1
