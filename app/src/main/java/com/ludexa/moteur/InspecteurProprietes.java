// haut 1
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
    
    private EditText champTag;
    private Button btnTagsExistants;

    private EditText champX;
    private EditText champY;
    private Button boutonSupprimer;

    private TextView valeurType;
    private EditText champLargeur, champHauteur, champRotation, champAlpha, champZOrder;
    private EditText champScaleX, champScaleY, champParallaxe;
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

    private LinearLayout blocBouton;
    private Button btnChargerImagePresse, btnSupprimerImagePresse;
    private Button btnChargerImageDesactive, btnSupprimerImageDesactive;
    private CheckBox cbDesactive;

    private LinearLayout blocJoystick;
    private Button btnCibleJoystick;

    // NOUVEAU : Bloc pour la Scène Liée (Prefab)
    private LinearLayout blocSceneInstance;
    private Button btnSelectSceneLiee;
    private Button btnEditerSceneLiee; 

    private LinearLayout blocPhysique;
    private CheckBox cbEstPhysique;
    private Button btnTogglePhysique;
    private LinearLayout conteneurPhysiqueDetails;
    private EditText champRebond;
    private EditText champGravite;

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
// bas 1

// haut 2
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
        titreInspecteur.setText(Traducteur.get("insp_titre"));
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
        texteInfo.setText(Traducteur.get("insp_info_selection"));
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
        labelNom.setText(Traducteur.get("insp_label_nom"));
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
        btnValiderNom.setText(Traducteur.get("bouton_ok"));
        styliserBouton(btnValiderNom);
        btnValiderNom.setLayoutParams(new LinearLayout.LayoutParams(dp(64), LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutNom.addView(btnValiderNom);

        blocProprietes.addView(layoutNom);

        TextView labelTag = new TextView(context);
        labelTag.setText(Traducteur.get("insp_label_tag")); 
        styliserLabel(labelTag);
        blocProprietes.addView(labelTag);

        LinearLayout layoutTag = new LinearLayout(context);
        layoutTag.setOrientation(LinearLayout.HORIZONTAL);
        layoutTag.setGravity(Gravity.CENTER_VERTICAL);

        champTag = new EditText(context);
        champTag.setSingleLine(true);
        champTag.setHint(Traducteur.get("insp_hint_tag"));
        champTag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        styliserChampFlexible(champTag);
        layoutTag.addView(champTag);

        btnTagsExistants = new Button(context);
        btnTagsExistants.setText(Traducteur.get("insp_btn_tags_liste"));
        styliserBouton(btnTagsExistants);
        btnTagsExistants.setLayoutParams(new LinearLayout.LayoutParams(dp(70), LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutTag.addView(btnTagsExistants);

        blocProprietes.addView(layoutTag);

        TextView labelPos = new TextView(context);
        labelPos.setText(Traducteur.get("insp_label_pos"));
        styliserLabel(labelPos);
        blocProprietes.addView(labelPos);

        LinearLayout layoutPos = new LinearLayout(context);
        layoutPos.setOrientation(LinearLayout.HORIZONTAL);

        champX = new EditText(context);
        champX.setHint(Traducteur.get("insp_hint_x"));
        champX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champX);

        champY = new EditText(context);
        champY.setHint(Traducteur.get("insp_hint_y"));
        champY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champY);

        layoutPos.addView(champX);
        layoutPos.addView(champY);
        blocProprietes.addView(layoutPos);

        TextView labelDim = new TextView(context);
        labelDim.setText(Traducteur.get("insp_label_dim"));
        styliserLabel(labelDim);
        blocProprietes.addView(labelDim);

        LinearLayout layoutDim = new LinearLayout(context);
        layoutDim.setOrientation(LinearLayout.HORIZONTAL);

        champLargeur = new EditText(context);
        champLargeur.setHint(Traducteur.get("insp_hint_largeur"));
        champLargeur.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        styliserChampFlexible(champLargeur);

        champHauteur = new EditText(context);
        champHauteur.setHint(Traducteur.get("insp_hint_hauteur"));
        champHauteur.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        styliserChampFlexible(champHauteur);

        layoutDim.addView(champLargeur);
        layoutDim.addView(champHauteur);
        blocProprietes.addView(layoutDim);

        TextView labelScale = new TextView(context);
        labelScale.setText(Traducteur.get("insp_label_scale"));
        styliserLabel(labelScale);
        blocProprietes.addView(labelScale);

        LinearLayout layoutScale = new LinearLayout(context);
        layoutScale.setOrientation(LinearLayout.HORIZONTAL);

        champScaleX = new EditText(context);
        champScaleX.setHint(Traducteur.get("insp_hint_scalex"));
        champScaleX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champScaleX);

        champScaleY = new EditText(context);
        champScaleY.setHint(Traducteur.get("insp_hint_scaley"));
        champScaleY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champScaleY);

        layoutScale.addView(champScaleX);
        layoutScale.addView(champScaleY);
        blocProprietes.addView(layoutScale);

        TextView labelRotation = new TextView(context);
        labelRotation.setText(Traducteur.get("insp_label_rotation"));
        styliserLabel(labelRotation);
        blocProprietes.addView(labelRotation);

        champRotation = new EditText(context);
        champRotation.setHint(Traducteur.get("insp_hint_rotation"));
        champRotation.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChamp(champRotation);
        blocProprietes.addView(champRotation);

        btnCouleur = new Button(context);
        btnCouleur.setText(Traducteur.get("insp_btn_couleur"));
        styliserBouton(btnCouleur);
        blocProprietes.addView(btnCouleur);

        TextView labelAlpha = new TextView(context);
        labelAlpha.setText(Traducteur.get("insp_label_alpha"));
        styliserLabel(labelAlpha);
        blocProprietes.addView(labelAlpha);

        champAlpha = new EditText(context);
        champAlpha.setHint(Traducteur.get("insp_hint_alpha"));
        champAlpha.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        styliserChamp(champAlpha);
        blocProprietes.addView(champAlpha);

        cbVisible = new CheckBox(context);
        cbVisible.setText(Traducteur.get("insp_cb_visible"));
        styliserCase(cbVisible);
        blocProprietes.addView(cbVisible);

        cbVerrouille = new CheckBox(context);
        cbVerrouille.setText(Traducteur.get("insp_cb_verrouille"));
        styliserCase(cbVerrouille);
        blocProprietes.addView(cbVerrouille);

        TextView labelZOrder = new TextView(context);
        labelZOrder.setText(Traducteur.get("insp_label_zorder"));
        styliserLabel(labelZOrder);
        blocProprietes.addView(labelZOrder);

        LinearLayout layoutZOrder = new LinearLayout(context);
        layoutZOrder.setOrientation(LinearLayout.HORIZONTAL);
        layoutZOrder.setGravity(Gravity.CENTER_VERTICAL);

        champZOrder = new EditText(context);
        champZOrder.setHint(Traducteur.get("insp_label_zorder"));
        champZOrder.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChampFlexible(champZOrder);
        layoutZOrder.addView(champZOrder);

        Button btnZOrderMoins = new Button(context);
        btnZOrderMoins.setText("-");
        styliserBouton(btnZOrderMoins);
        btnZOrderMoins.setLayoutParams(new LinearLayout.LayoutParams(dp(48), LinearLayout.LayoutParams.WRAP_CONTENT));

        Button btnZOrderPlus = new Button(context);
        btnZOrderPlus.setText("+");
        styliserBouton(btnZOrderPlus);
        btnZOrderPlus.setLayoutParams(new LinearLayout.LayoutParams(dp(48), LinearLayout.LayoutParams.WRAP_CONTENT));

        btnZOrderMoins.setOnClickListener(v -> {
            if (objetCourant != null) {
                objetCourant.zOrder--;
                miseAJourEnCours = true;
                champZOrder.setText(String.valueOf(objetCourant.zOrder));
                miseAJourEnCours = false;
                canvasEditeur.invalidate();
            }
        });

        btnZOrderPlus.setOnClickListener(v -> {
            if (objetCourant != null) {
                objetCourant.zOrder++;
                miseAJourEnCours = true;
                champZOrder.setText(String.valueOf(objetCourant.zOrder));
                miseAJourEnCours = false;
                canvasEditeur.invalidate();
            }
        });

        layoutZOrder.addView(btnZOrderMoins);
        layoutZOrder.addView(btnZOrderPlus);
        blocProprietes.addView(layoutZOrder);
        
        TextView labelParallaxe = new TextView(context);
        labelParallaxe.setText(Traducteur.get("insp_label_parallaxe"));
        styliserLabel(labelParallaxe);
        blocProprietes.addView(labelParallaxe);
        
        champParallaxe = new EditText(context);
        champParallaxe.setHint("1.0");
        champParallaxe.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChamp(champParallaxe);
        blocProprietes.addView(champParallaxe);

        TextView labelParent = new TextView(context);
        labelParent.setText(Traducteur.get("insp_label_parent"));
        styliserLabel(labelParent);
        blocProprietes.addView(labelParent);

        btnParent = new Button(context);
        btnParent.setText(Traducteur.get("insp_btn_parent_aucun"));
        styliserBouton(btnParent);
        blocProprietes.addView(btnParent);

        btnParent.setOnClickListener(v -> {
            if (objetCourant == null) return;
            List<String> noms = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            noms.add(Traducteur.get("valeur_aucune"));
            ids.add(null);
            for (ObjetBase o : sceneActive.objets) {
                if (o != objetCourant) {
                    noms.add(o.nom != null ? o.nom : Traducteur.get("insp_objet_sans_nom"));
                    ids.add(o.id);
                }
            }
            new AlertDialog.Builder(context)
                .setTitle(Traducteur.get("insp_titre_select_parent"))
                .setItems(noms.toArray(new String[0]), (dialog, which) -> {
                    String idChoisi = ids.get(which);
                    if (!ObjetBase.verifierBoucleParent(objetCourant.id, idChoisi, sceneActive.objets)) {
                        objetCourant.parentId = idChoisi;
                        canvasEditeur.invalidate();
                        afficherObjet(objetCourant);
                    } else {
                        Toast.makeText(context, Traducteur.get("insp_erreur_boucle_parent"), Toast.LENGTH_SHORT).show();
                    }
                }).show();
        });
// bas 2


// haut 3
        blocTexte = new LinearLayout(context);
        blocTexte.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocTexte);

        TextView sepTexte = new TextView(context);
        sepTexte.setText(Traducteur.get("insp_sep_texte"));
        styliserSousTitre(sepTexte);
        blocTexte.addView(sepTexte);

        champContenu = new EditText(context);
        champContenu.setHint(Traducteur.get("insp_hint_contenu_texte"));
        champContenu.setFocusable(false);
        styliserChamp(champContenu);
        champContenu.setOnClickListener(v -> {
            if (objetCourant == null) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(Traducteur.get("insp_titre_modif_texte"));
            final EditText input = new EditText(context);
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setSingleLine(false);
            input.setLines(5);
            input.setGravity(Gravity.TOP | Gravity.START);
            input.setText(objetCourant.contenuTexte);
            styliserChamp(input);
            builder.setView(input);
            builder.setPositiveButton(Traducteur.get("bouton_valider"), (dialog, which) -> {
                String nouveauTexte = input.getText().toString();
                objetCourant.contenuTexte = nouveauTexte;
                miseAJourEnCours = true;
                champContenu.setText(nouveauTexte);
                miseAJourEnCours = false;
                canvasEditeur.invalidate();
            });
            builder.setNegativeButton(Traducteur.get("bouton_annuler"), null);
            builder.show();
        });
        blocTexte.addView(champContenu);

        champTaille = new EditText(context);
        champTaille.setHint(Traducteur.get("insp_hint_taille_police"));
        champTaille.setFocusable(false);
        champTaille.setOnClickListener(v -> {
            if (objetCourant == null) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(Traducteur.get("insp_titre_taille_police"));
            final EditText input = new EditText(context);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setText(String.valueOf(objetCourant.tailleFonte));
            styliserChamp(input);
            builder.setView(input);
            builder.setPositiveButton(Traducteur.get("bouton_valider"), (dialog, which) -> {
                try {
                    float nouvelleTaille = Float.parseFloat(input.getText().toString());
                    objetCourant.tailleFonte = nouvelleTaille;
                    miseAJourEnCours = true;
                    champTaille.setText(String.valueOf(nouvelleTaille));
                    miseAJourEnCours = false;
                    canvasEditeur.invalidate();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, Traducteur.get("insp_erreur_valeur_invalide"), Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton(Traducteur.get("bouton_annuler"), null);
            builder.show();
        });
        styliserChamp(champTaille);
        blocTexte.addView(champTaille);

        btnCouleurTexte = new Button(context);
        btnCouleurTexte.setText(Traducteur.get("insp_btn_couleur_texte"));
        styliserBouton(btnCouleurTexte);
        blocTexte.addView(btnCouleurTexte);

        btnPolice = new Button(context);
        btnPolice.setText(Traducteur.get("insp_btn_police_selecteur"));
        btnPolice.setOnClickListener(v -> {
            if (objetCourant == null) return;
            if (cheminProjet == null) { Toast.makeText(context, Traducteur.get("erreur_chemin_projet"), Toast.LENGTH_SHORT).show(); return; }
            java.io.File dossierPolices = new java.io.File(cheminProjet, "assets_ludexa/Fonts");
            List<String> polices = listerPolicesLocales(dossierPolices, "assets_ludexa/Fonts/");
            if (polices.isEmpty()) { Toast.makeText(context, Traducteur.get("insp_aucune_police"), Toast.LENGTH_SHORT).show(); return; }
            List<String> options = new ArrayList<>();
            options.add(Traducteur.get("insp_police_defaut"));
            options.addAll(polices);
            new AlertDialog.Builder(context)
                .setTitle(Traducteur.get("insp_titre_select_police"))
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) objetCourant.cheminPolice = null;
                    else objetCourant.cheminPolice = options.get(which);
                    canvasEditeur.invalidate();
                    afficherObjet(objetCourant);
                }).show();
        });
        styliserBouton(btnPolice);
        blocTexte.addView(btnPolice);

        blocProprietes.addView(blocTexte);

        blocImage = new LinearLayout(context);
        blocImage.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocImage);

        TextView sepImage = new TextView(context);
        sepImage.setText(Traducteur.get("insp_sep_image"));
        styliserSousTitre(sepImage);
        blocImage.addView(sepImage);

        btnChargerImage = new Button(context);
        btnChargerImage.setText(Traducteur.get("insp_btn_charger_image"));
        styliserBouton(btnChargerImage);
        blocImage.addView(btnChargerImage);

        btnSupprimerImage = new Button(context);
        btnSupprimerImage.setText(Traducteur.get("insp_btn_supprimer_image"));
        styliserBouton(btnSupprimerImage);
        blocImage.addView(btnSupprimerImage);

        cbFondColore = new CheckBox(context);
        cbFondColore.setText(Traducteur.get("insp_cb_fond_colore"));
        styliserCase(cbFondColore);
        blocImage.addView(cbFondColore);

        blocProprietes.addView(blocImage);
        
        blocBouton = new LinearLayout(context);
        blocBouton.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocBouton);

        TextView sepBouton = new TextView(context);
        sepBouton.setText(Traducteur.get("insp_sep_bouton"));
        styliserSousTitre(sepBouton);
        blocBouton.addView(sepBouton);

        btnChargerImagePresse = new Button(context);
        btnChargerImagePresse.setText(Traducteur.get("insp_btn_charger_image_presse"));
        styliserBouton(btnChargerImagePresse);
        blocBouton.addView(btnChargerImagePresse);

        btnSupprimerImagePresse = new Button(context);
        btnSupprimerImagePresse.setText(Traducteur.get("insp_btn_suppr_image_presse"));
        styliserBouton(btnSupprimerImagePresse);
        blocBouton.addView(btnSupprimerImagePresse);

        btnChargerImageDesactive = new Button(context);
        btnChargerImageDesactive.setText(Traducteur.get("insp_btn_charger_image_desac"));
        styliserBouton(btnChargerImageDesactive);
        blocBouton.addView(btnChargerImageDesactive);

        btnSupprimerImageDesactive = new Button(context);
        btnSupprimerImageDesactive.setText(Traducteur.get("insp_btn_suppr_image_desac"));
        styliserBouton(btnSupprimerImageDesactive);
        blocBouton.addView(btnSupprimerImageDesactive);

        cbDesactive = new CheckBox(context);
        cbDesactive.setText(Traducteur.get("insp_cb_desactive"));
        styliserCase(cbDesactive);
        blocBouton.addView(cbDesactive);

        blocProprietes.addView(blocBouton);

        blocJoystick = new LinearLayout(context);
        blocJoystick.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocJoystick);

        TextView sepJoystick = new TextView(context);
        sepJoystick.setText(Traducteur.get("insp_sep_joystick"));
        styliserSousTitre(sepJoystick);
        blocJoystick.addView(sepJoystick);

        TextView labelCibleJoystick = new TextView(context);
        labelCibleJoystick.setText(Traducteur.get("insp_label_cible_joystick"));
        styliserLabel(labelCibleJoystick);
        blocJoystick.addView(labelCibleJoystick);

        btnCibleJoystick = new Button(context);
        btnCibleJoystick.setText(Traducteur.get("insp_btn_cible_joystick_aucune"));
        styliserBouton(btnCibleJoystick);
        blocJoystick.addView(btnCibleJoystick);

        btnCibleJoystick.setOnClickListener(v -> {
            if (objetCourant == null) return;
            List<String> noms = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            noms.add(Traducteur.get("valeur_aucune"));
            ids.add(null);
            
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            for (Scene s : editeur.listeScenes) {
                for (ObjetBase o : s.objets) {
                    if (o != objetCourant && !"joystick".equals(o.type)) {
                        String nomObj = o.nom != null ? o.nom : Traducteur.get("insp_objet_sans_nom");
                        noms.add(nomObj + " [" + s.nom + "]");
                        ids.add(o.id);
                    }
                }
            }
            
            new AlertDialog.Builder(context)
                .setTitle(Traducteur.get("insp_titre_select_cible"))
                .setItems(noms.toArray(new String[0]), (dialog, which) -> {
                    objetCourant.cibleJoystickId = ids.get(which);
                    canvasEditeur.invalidate();
                    afficherObjet(objetCourant);
                }).show();
        });

        blocProprietes.addView(blocJoystick);

        // --- NOUVEAU : UI POUR SCENE INSTANCE ---
        blocSceneInstance = new LinearLayout(context);
        blocSceneInstance.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocSceneInstance);

        TextView sepSceneLiee = new TextView(context);
        sepSceneLiee.setText(Traducteur.get("insp_sep_scene_liee"));
        styliserSousTitre(sepSceneLiee);
        blocSceneInstance.addView(sepSceneLiee);

        btnSelectSceneLiee = new Button(context);
        btnSelectSceneLiee.setText(Traducteur.get("insp_btn_select_scene_liee"));
        styliserBouton(btnSelectSceneLiee);
        blocSceneInstance.addView(btnSelectSceneLiee);
        
        btnEditerSceneLiee = new Button(context);
        btnEditerSceneLiee.setText("Éditer la scène source ➔");
        styliserBouton(btnEditerSceneLiee);
        btnEditerSceneLiee.setTextColor(Color.parseColor("#4CAF50")); // Couleur verte pour indiquer une action forte
        blocSceneInstance.addView(btnEditerSceneLiee);
        
        blocProprietes.addView(blocSceneInstance);
        // ----------------------------------------
        
        blocPhysique = new LinearLayout(context);
        blocPhysique.setOrientation(LinearLayout.VERTICAL);
        styliserSection(blocPhysique);

        TextView sepPhysique = new TextView(context);
        sepPhysique.setText(Traducteur.get("insp_sep_physique"));
        styliserSousTitre(sepPhysique);
        blocPhysique.addView(sepPhysique);

        cbEstPhysique = new CheckBox(context);
        cbEstPhysique.setText(Traducteur.get("insp_cb_physique"));
        styliserCase(cbEstPhysique);
        blocPhysique.addView(cbEstPhysique);

        btnTogglePhysique = new Button(context);
        btnTogglePhysique.setText(Traducteur.get("insp_btn_physique_statique"));
        styliserBouton(btnTogglePhysique);
        blocPhysique.addView(btnTogglePhysique);

        conteneurPhysiqueDetails = new LinearLayout(context);
        conteneurPhysiqueDetails.setOrientation(LinearLayout.VERTICAL);
        conteneurPhysiqueDetails.setVisibility(View.GONE);

        TextView labelRebond = new TextView(context);
        labelRebond.setText(Traducteur.get("insp_label_rebond"));
        styliserLabel(labelRebond);
        conteneurPhysiqueDetails.addView(labelRebond);

        champRebond = new EditText(context);
        champRebond.setHint("0.4");
        champRebond.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        styliserChamp(champRebond);
        conteneurPhysiqueDetails.addView(champRebond);

        TextView labelGravite = new TextView(context);
        labelGravite.setText(Traducteur.get("insp_label_gravite"));
        styliserLabel(labelGravite);
        conteneurPhysiqueDetails.addView(labelGravite);

        champGravite = new EditText(context);
        champGravite.setHint("1.0");
        champGravite.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        styliserChamp(champGravite);
        conteneurPhysiqueDetails.addView(champGravite);

        blocPhysique.addView(conteneurPhysiqueDetails);
        blocProprietes.addView(blocPhysique);

        cbRamassable = new CheckBox(context);
        cbRamassable.setText(Traducteur.get("insp_cb_ramassable"));
        styliserCase(cbRamassable);
        blocProprietes.addView(cbRamassable);

        cbZoneDeClic = new CheckBox(context);
        cbZoneDeClic.setText(Traducteur.get("insp_cb_zone_clic"));
        styliserCase(cbZoneDeClic);
        blocProprietes.addView(cbZoneDeClic);

        cbDeplacable = new CheckBox(context);
        cbDeplacable.setText(Traducteur.get("insp_cb_deplacable"));
        styliserCase(cbDeplacable);
        blocProprietes.addView(cbDeplacable);

        contenuInspecteur.addView(blocProprietes);

        boutonSupprimer = new Button(context);
        boutonSupprimer.setText(Traducteur.get("insp_btn_supprimer_objet"));
        boutonSupprimer.setAllCaps(false);
        boutonSupprimer.setTextSize(15f);
        boutonSupprimer.setTextColor(Palette.texteNormal);
        boutonSupprimer.setBackground(fond(Color.parseColor("#8B3A3A"), Palette.bordure, 10));
        boutonSupprimer.setPadding(dp(14), dp(11), dp(14), dp(11));
        boutonSupprimer.setOnClickListener(v -> {
            if (objetCourant == null) { Toast.makeText(context, Traducteur.get("insp_erreur_aucun_objet"), Toast.LENGTH_SHORT).show(); return; }
            new AlertDialog.Builder(context)
                    .setTitle(Traducteur.get("insp_titre_confirm_suppr"))
                    .setMessage(Traducteur.get("insp_msg_confirm_suppr"))
                    .setPositiveButton(Traducteur.get("bouton_supprimer"), (dialog, which) -> {
                        sceneActive.objets.remove(objetCourant);
                        canvasEditeur.deselectionner();
                        afficherObjet(null);
                        canvasEditeur.invalidate();
                        Toast.makeText(context, Traducteur.get("insp_toast_objet_supprime"), Toast.LENGTH_SHORT).show();
                    }).setNegativeButton(Traducteur.get("bouton_annuler"), null).show();
        });

        LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsBtn.setMargins(dp(3), dp(22), dp(3), dp(6));
        boutonSupprimer.setLayoutParams(paramsBtn);

        contenuInspecteur.addView(boutonSupprimer);
        scrollInspecteur.addView(contenuInspecteur);
        this.addView(scrollInspecteur);
// bas 3


// haut 5
    private void verifierEtConfirmerRenommage(Context context) {
        if (objetCourant == null) return;
        String nouveauNom = champNom.getText().toString().trim();
        String ancienNom = objetCourant.nom != null ? objetCourant.nom : "";

        if (!nouveauNom.equals(ancienNom) && !miseAJourEnCours) {
            if (sceneActive != null && sceneActive.objets != null) {
                for (ObjetBase obj : sceneActive.objets) {
                    if (!obj.id.equals(objetCourant.id) && obj.nom != null && obj.nom.trim().equalsIgnoreCase(nouveauNom)) {
                        new AlertDialog.Builder(context).setTitle(Traducteur.get("insp_titre_impossible"))
                                .setMessage(Traducteur.get("insp_msg_nom_existe_1") + nouveauNom + Traducteur.get("insp_msg_nom_existe_2"))
                                .setPositiveButton("OK", null).show();
                        miseAJourEnCours = true; champNom.setText(ancienNom); miseAJourEnCours = false;
                        return; 
                    }
                }
            }
            new AlertDialog.Builder(context).setTitle(Traducteur.get("insp_titre_confirmation"))
                    .setMessage(Traducteur.get("insp_msg_renommer_1") + ancienNom + Traducteur.get("insp_msg_renommer_2") + nouveauNom + Traducteur.get("insp_msg_renommer_3"))
                    .setPositiveButton(Traducteur.get("bouton_oui"), (dialog, which) -> { objetCourant.nom = nouveauNom; canvasEditeur.invalidate(); })
                    .setNegativeButton(Traducteur.get("bouton_non"), (dialog, which) -> { miseAJourEnCours = true; champNom.setText(ancienNom); miseAJourEnCours = false; })
                    .setOnCancelListener(dialog -> { miseAJourEnCours = true; champNom.setText(ancienNom); miseAJourEnCours = false; })
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
            
            champTag.setText(objet.tag != null ? objet.tag : "");

            champX.setText(String.valueOf((int) objet.x));
            champY.setText(String.valueOf((int) objet.y));

            String nomType = objet.type != null ? objet.type.substring(0, 1).toUpperCase() + objet.type.substring(1) : Traducteur.get("insp_type_inconnu");
            valeurType.setText(Traducteur.get("insp_type") + nomType);

            champLargeur.setText(String.valueOf((int) objet.largeur));
            champHauteur.setText(String.valueOf((int) objet.hauteur));

            champScaleX.setText(String.valueOf(objet.scaleX));
            champScaleY.setText(String.valueOf(objet.scaleY));

            champAlpha.setText(String.valueOf(objet.alpha));
            champRotation.setText(String.valueOf((int) objet.rotation));
            champZOrder.setText(String.valueOf(objet.zOrder));
            champParallaxe.setText(String.valueOf(objet.facteurParallaxe));
            cbVisible.setChecked(objet.visible);

            String nomParent = Traducteur.get("valeur_aucune");
            if (objet.parentId != null) {
                for (ObjetBase o : sceneActive.objets) {
                    if (o.id.equals(objet.parentId)) { nomParent = o.nom != null ? o.nom : Traducteur.get("insp_objet_sans_nom"); break; }
                }
            }
            btnParent.setText(Traducteur.get("insp_btn_parent") + nomParent);

            cbRamassable.setChecked(objet.estRamassable);
            cbZoneDeClic.setChecked(objet.estZoneDeClic);
            cbDeplacable.setChecked(objet.estDeplacable);
            cbVerrouille.setChecked(objet.estVerrouille);

            cbEstPhysique.setChecked(objet.estPhysique);
            
            if (!objet.estPhysique) {
                btnTogglePhysique.setVisibility(View.GONE);
                conteneurPhysiqueDetails.setVisibility(View.GONE);
            } else {
                btnTogglePhysique.setVisibility(View.VISIBLE);
                if (objet.estStatique) {
                    btnTogglePhysique.setText(Traducteur.get("insp_btn_physique_statique"));
                    conteneurPhysiqueDetails.setVisibility(View.GONE);
                } else {
                    btnTogglePhysique.setText(Traducteur.get("insp_btn_physique_dynamique"));
                    conteneurPhysiqueDetails.setVisibility(View.VISIBLE);
                }
            }
            champRebond.setText(String.valueOf(objet.rebond));
            champGravite.setText(String.valueOf(objet.graviteScale));

            if ("texte".equals(objet.type)) {
                blocTexte.setVisibility(View.VISIBLE);
                blocImage.setVisibility(View.GONE);
                blocBouton.setVisibility(View.GONE);
                blocJoystick.setVisibility(View.GONE);
                blocSceneInstance.setVisibility(View.GONE); 
                
                champContenu.setText(objet.contenuTexte);
                champTaille.setText(String.valueOf(objet.tailleFonte));
                if (objet.cheminPolice != null) {
                    java.io.File f = new java.io.File(objet.cheminPolice);
                    btnPolice.setText(Traducteur.get("insp_btn_police") + f.getName());
                } else {
                    btnPolice.setText(Traducteur.get("insp_btn_police_selecteur"));
                }
            } else if ("scene_instance".equals(objet.type)) { 
                blocTexte.setVisibility(View.GONE);
                blocImage.setVisibility(View.GONE);
                blocBouton.setVisibility(View.GONE);
                blocJoystick.setVisibility(View.GONE);
                blocSceneInstance.setVisibility(View.VISIBLE);
                
                String nomScene = Traducteur.get("valeur_aucune");
                boolean aUneScene = false;
                
                if (objet.sceneLieeId != null) {
                    InterfaceEditeur editeur = (InterfaceEditeur) getContext();
                    for (Scene s : editeur.listeScenes) {
                        if (s.id.equals(objet.sceneLieeId)) {
                            nomScene = s.nom != null ? s.nom : Traducteur.get("insp_objet_sans_nom");
                            aUneScene = true;
                            break;
                        }
                    }
                }
                
                btnSelectSceneLiee.setText(Traducteur.get("insp_btn_scene_liee_val") + nomScene);
                
                // Le bouton d'édition n'apparaît que si le Prefab a une scène valide
                btnEditerSceneLiee.setVisibility(aUneScene ? View.VISIBLE : View.GONE);
                
            } else {
                blocTexte.setVisibility(View.GONE);
                blocSceneInstance.setVisibility(View.GONE); 
                blocImage.setVisibility(View.VISIBLE);

                if (objet.cheminImage != null) {
                    btnSupprimerImage.setVisibility(View.VISIBLE);
                    cbFondColore.setVisibility(View.VISIBLE);
                    cbFondColore.setChecked(objet.afficherFondColore);
                } else {
                    btnSupprimerImage.setVisibility(View.GONE);
                    cbFondColore.setVisibility(View.GONE);
                }
                
                if ("bouton".equals(objet.type)) {
                    blocBouton.setVisibility(View.VISIBLE);
                    btnSupprimerImagePresse.setVisibility(objet.cheminImagePresse != null ? View.VISIBLE : View.GONE);
                    btnSupprimerImageDesactive.setVisibility(objet.cheminImageDesactive != null ? View.VISIBLE : View.GONE);
                    cbDesactive.setChecked(objet.estDesactive);
                } else {
                    blocBouton.setVisibility(View.GONE);
                }

                if ("joystick".equals(objet.type)) {
                    blocJoystick.setVisibility(View.VISIBLE);
                    String nomCible = Traducteur.get("valeur_aucune");
                    if (objet.cibleJoystickId != null) {
                        InterfaceEditeur editeur = (InterfaceEditeur) getContext();
                        boolean trouve = false;
                        for (Scene s : editeur.listeScenes) {
                            for (ObjetBase o : s.objets) {
                                if (o.id.equals(objet.cibleJoystickId)) {
                                    nomCible = o.nom != null ? o.nom : Traducteur.get("insp_objet_sans_nom");
                                    trouve = true;
                                    break;
                                }
                            }
                            if (trouve) break;
                        }
                    }
                    btnCibleJoystick.setText(Traducteur.get("insp_btn_cible_joystick") + nomCible);
                } else {
                    blocJoystick.setVisibility(View.GONE);
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!miseAJourEnCours) action.accept(s.toString());
            }
        };
    }
}
// bas 5



        



        




