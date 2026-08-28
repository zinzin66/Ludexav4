// haut 1 : IMPORTS ET CONSTRUCTEUR
package com.ludexa.moteur;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PanneauRessources extends LinearLayout {

    private LinearLayout conteneurFichiers;
    private LinearLayout conteneurScenes;
    private LinearLayout conteneurArborescence;
    private LinearLayout conteneurArborescenceDossiers;
    private LinearLayout conteneurListeAssets;
    private LinearLayout conteneurVariables;
    private LinearLayout conteneurFonctions; 
    
    private File rootAssetsDir;
    private File rootFonctionsDir; 
    private CanvasEditeur canvasEditeur;
    private ObjetBase objetSelectionne;
    private Variable variableSelectionnee;
    private String fonctionSelectionnee; 
    
    private File currentFolderSelected;
    private File currentAssetSelected;
    private String cheminProjet;

    private ScrollView scrollPanneau;
    private LinearLayout.LayoutParams paramsOuvert;
    private LinearLayout.LayoutParams paramsFerme;
    private Button boutonMasquer;

    public PanneauRessources(Context context, CanvasEditeur canvasEditeur, String cheminProjet) {
        super(context);
        this.canvasEditeur = canvasEditeur;
        this.cheminProjet = cheminProjet;
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Palette.fondPanneaux);

        if (cheminProjet != null) {
            rootAssetsDir = new File(cheminProjet, "assets_ludexa");
            if (!rootAssetsDir.exists()) rootAssetsDir.mkdirs();
            new File(rootAssetsDir, "Images").mkdirs();
            new File(rootAssetsDir, "Sons").mkdirs();
            new File(rootAssetsDir, "Fonts").mkdirs();
            new File(rootAssetsDir, "Textes").mkdirs();
            
            rootFonctionsDir = new File(cheminProjet, "fonctions"); 
            if (!rootFonctionsDir.exists()) rootFonctionsDir.mkdirs(); 
        }

        paramsOuvert = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        LinearLayout entetePanneau = new LinearLayout(context);
        entetePanneau.setOrientation(LinearLayout.HORIZONTAL);
        entetePanneau.setPadding(dp(12), dp(10), dp(12), dp(10));
        entetePanneau.setBackgroundColor(Palette.enTeteDialogues);
        entetePanneau.setGravity(Gravity.CENTER_VERTICAL);

        boutonMasquer = new Button(context);
        boutonMasquer.setText("<"); 
        boutonMasquer.setAllCaps(false);
        boutonMasquer.setTextColor(Palette.iconeNormal);
        boutonMasquer.setBackground(fond(Palette.boutonNormal, Palette.bordure, 8));
        boutonMasquer.setPadding(dp(10), dp(6), dp(10), dp(6));
        boutonMasquer.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(40)));

        TextView titrePanneau = new TextView(context);
        titrePanneau.setText(Traducteur.get("panneau_ress_titre"));
        titrePanneau.setTextSize(17f);
        titrePanneau.setLetterSpacing(0.08f);
        titrePanneau.setTypeface(null, android.graphics.Typeface.BOLD);
        titrePanneau.setTextColor(Palette.texteSelectionne);
        titrePanneau.setPadding(dp(10), 0, 0, 0);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titrePanneau.setLayoutParams(paramsTitre);

        entetePanneau.addView(boutonMasquer);
        entetePanneau.addView(titrePanneau);
        addView(entetePanneau);

        scrollPanneau = new ScrollView(context);
        scrollPanneau.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout contenuScroll = new LinearLayout(context);
        contenuScroll.setOrientation(LinearLayout.VERTICAL);
        contenuScroll.setPadding(dp(8), dp(8), dp(8), dp(8));

        contenuScroll.addView(creerSectionScenes(context));
        contenuScroll.addView(creerSectionObjets(context));
        contenuScroll.addView(creerSectionArborescence(context));
        contenuScroll.addView(creerSectionAssets(context));
        contenuScroll.addView(creerSectionVariables(context));
        contenuScroll.addView(creerSectionFonctions(context)); 

        scrollPanneau.addView(contenuScroll);
        addView(scrollPanneau);

        boutonMasquer.setOnClickListener(v -> {
            if (scrollPanneau.getVisibility() == View.VISIBLE) {
                scrollPanneau.setVisibility(View.GONE);
                titrePanneau.setVisibility(View.GONE);
                boutonMasquer.setText(">");
                this.setLayoutParams(paramsFerme);
            } else {
                scrollPanneau.setVisibility(View.VISIBLE);
                titrePanneau.setVisibility(View.VISIBLE);
                boutonMasquer.setText("<");
                this.setLayoutParams(paramsOuvert);
            }
        });
    }
// bas 1
// haut 2 : UTILITAIRES GRAPHIQUES ET NOMMAGE
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

    private void styliserTitreSection(Button btn) {
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTextColor(Palette.texteSelectionne);
        btn.setTextSize(16f);
        btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btn.setTypeface(null, android.graphics.Typeface.BOLD);
        btn.setPadding(dp(8), dp(16), dp(8), dp(8));
        btn.setAllCaps(false);
    }

    private void styliserContenuSection(LinearLayout contenu) {
        contenu.setBackground(fond(Palette.fondNormal, Palette.bordure, 8));
        contenu.setPadding(dp(8), dp(8), dp(8), dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(12));
        contenu.setLayoutParams(lp);
    }

    private void styliserBoutonIcone(ImageButton btn) {
        btn.setBackground(fond(Palette.boutonNormal, Palette.bordure, 8));
        btn.setPadding(dp(12), dp(12), dp(12), dp(12));
        btn.setColorFilter(Palette.iconeNormal);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        btn.setLayoutParams(lp);
    }

    private void styliserDialogue(LinearLayout layout) {
        layout.setBackgroundColor(Palette.fondPanneaux);
        layout.setPadding(dp(16), dp(16), dp(16), dp(16));
    }

    private void styliserChampDialogue(EditText champ) {
        champ.setTextColor(Palette.texteNormal);
        champ.setHintTextColor(Palette.bordure);
        champ.setBackground(fond(Palette.fondNormal, Palette.bordure, 8));
        champ.setPadding(dp(12), dp(10), dp(12), dp(10));
        champ.setTextSize(14f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(12));
        champ.setLayoutParams(lp);
    }

    private String genererNomUnique(String prefixe, Scene scene) {
        if (scene == null || scene.objets == null) return prefixe + " 1";
        int compteur = 1;
        String nom;
        boolean existe;
        do {
            nom = prefixe + " " + compteur;
            existe = false;
            for (ObjetBase obj : scene.objets) {
                if (nom.equals(obj.nom)) {
                    existe = true;
                    break;
                }
            }
            compteur++;
        } while (existe);
        return nom;
    }
// bas 2

// haut 3 : SECTION SCENES
    private View creerSectionScenes(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(Traducteur.get("panneau_ress_scenes") + " ▼");
        styliserTitreSection(btnTitre);

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        styliserContenuSection(contenu);

        conteneurScenes = new LinearLayout(context);
        conteneurScenes.setOrientation(LinearLayout.VERTICAL);
        conteneurScenes.setPadding(0, 0, 0, dp(8));
        contenu.addView(conteneurScenes);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnCreer = new ImageButton(context);
        btnCreer.setImageResource(R.drawable.add_24px);
        styliserBoutonIcone(btnCreer);
        btnCreer.setOnClickListener(v -> afficherPopupCreerScene(context));

        ImageButton btnRenommer = new ImageButton(context);
        btnRenommer.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonIcone(btnRenommer);
        btnRenommer.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            afficherPopupRenommerScene(context, editeur.sceneActive);
        });

        ImageButton btnSupprimer = new ImageButton(context);
        btnSupprimer.setImageResource(R.drawable.delete_24px);
        styliserBoutonIcone(btnSupprimer);
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
                btnTitre.setText(Traducteur.get("panneau_ress_scenes") + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(Traducteur.get("panneau_ress_scenes") + " ▼");
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
                    nomScene.setTextColor(Palette.texteSelectionne);
                    nomScene.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
                } else {
                    nomScene.setTextColor(Palette.texteNormal);
                }
                nomScene.setPadding(dp(10), dp(10), dp(10), dp(10));
                nomScene.setTextSize(15f);
                
                nomScene.setOnClickListener(v -> {
                    editeur.changerScene(s);
                    rafraichirArborescence();
                });

                conteneurScenes.addView(nomScene);
            }
        }
    }

    private void afficherPopupCreerScene(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_creer_scene_titre"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        EditText champTexte = new EditText(context);
        champTexte.setHint(Traducteur.get("hint_entrez_nom"));
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString().trim();
            if(!nom.isEmpty()) {
                InterfaceEditeur editeur = (InterfaceEditeur) context;
                if (editeur.listeScenes != null) {
                    for (Scene s : editeur.listeScenes) {
                        if (s.nom != null && s.nom.trim().equalsIgnoreCase(nom)) {
                            new AlertDialog.Builder(context).setTitle(Traducteur.get("insp_titre_impossible")).setMessage(Traducteur.get("erreur_scene_existe")).setPositiveButton(Traducteur.get("bouton_ok"), null).show();
                            return;
                        }
                    }
                }
                editeur.creerScene(nom);
                Toast.makeText(context, Traducteur.get("toast_scene_creee") + nom, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerScene(Context context, Scene scene) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_renommer_scene_titre"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        EditText champTexte = new EditText(context);
        champTexte.setText(scene.nom);
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if(nouveauNom.isEmpty()) return;
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            if (editeur.listeScenes != null) {
                for (Scene s : editeur.listeScenes) {
                    if (s != scene && s.nom != null && s.nom.trim().equalsIgnoreCase(nouveauNom)) {
                        new AlertDialog.Builder(context).setTitle(Traducteur.get("insp_titre_impossible")).setMessage(Traducteur.get("erreur_scene_existe")).setPositiveButton(Traducteur.get("bouton_ok"), null).show();
                        return;
                    }
                }
            }
            scene.nom = nouveauNom;
            rafraichirScenes();
            dialog.dismiss();
        });
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerScene(Context context, Scene scene) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_supprimer_scene_titre"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        TextView txtMessage = new TextView(context);
        txtMessage.setText(Traducteur.get("msg_supprimer_scene_1") + scene.nom + Traducteur.get("msg_supprimer_scene_2"));
        txtMessage.setTextColor(Palette.texteNormal);
        txtMessage.setTextSize(15f);
        txtMessage.setPadding(0, 0, 0, dp(14));
        layoutDialog.addView(txtMessage);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnOui);
        btnOui.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            if (editeur.listeScenes.size() <= 1) {
                Toast.makeText(context, Traducteur.get("erreur_supprimer_seule_scene"), Toast.LENGTH_SHORT).show();
            } else {
                editeur.listeScenes.remove(scene);
                if (editeur.sceneActive == scene) editeur.changerScene(editeur.listeScenes.get(0));
                else rafraichirScenes();
                rafraichirArborescence();
            }
            dialog.dismiss();
        });
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnNon);
        btnNon.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
// bas 3
// haut 4 : SECTION OBJETS (Ajout avec icônes explicites et intégration du bouton Prefab)
    private View creerSectionObjets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(Traducteur.get("panneau_ress_objets") + " ▼");
        styliserTitreSection(btnTitre);

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        styliserContenuSection(contenu);

        LinearLayout ligne1 = new LinearLayout(context);
        ligne1.setOrientation(LinearLayout.HORIZONTAL);
        
        LinearLayout ligne2 = new LinearLayout(context);
        ligne2.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout ligne3 = new LinearLayout(context);
        ligne3.setOrientation(LinearLayout.HORIZONTAL);
        
        LinearLayout ligne4 = new LinearLayout(context);
        ligne4.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnAjouterCarre = new ImageButton(context);
        btnAjouterCarre.setImageResource(R.drawable.square_24px);
        styliserBoutonIcone(btnAjouterCarre);
        btnAjouterCarre.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_carre"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 80f, 80f);
            nouveau.type = "carre"; 
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterTexte = new ImageButton(context);
        btnAjouterTexte.setImageResource(R.drawable.title_24px);
        styliserBoutonIcone(btnAjouterTexte);
        btnAjouterTexte.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_texte"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 200f, 100f, 120f, 40f);
            nouveau.type = "texte"; 
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterRond = new ImageButton(context);
        btnAjouterRond.setImageResource(R.drawable.circle_24px);
        styliserBoutonIcone(btnAjouterRond);
        btnAjouterRond.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_rond"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 100f, 200f, 90f, 90f);
            nouveau.type = "rond"; 
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterImage = new ImageButton(context);
        btnAjouterImage.setImageResource(R.drawable.add_photo_alternate_24px); 
        styliserBoutonIcone(btnAjouterImage);
        btnAjouterImage.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_image"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 100f, 100f);
            nouveau.type = "image"; 
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterZone = new ImageButton(context);
        btnAjouterZone.setImageResource(R.drawable.activity_zone_24px);
        styliserBoutonIcone(btnAjouterZone);
        btnAjouterZone.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_zone"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 100f, 100f);
            nouveau.type = "zone"; 
            nouveau.estZoneDeClic = true; 
            nouveau.couleur = Color.argb(120, 255, 152, 0); 
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterBouton = new ImageButton(context);
        btnAjouterBouton.setImageResource(R.drawable.buttons_alt_24px);
        styliserBoutonIcone(btnAjouterBouton);
        btnAjouterBouton.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_bouton"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 120f, 50f);
            nouveau.type = "bouton"; 
            nouveau.estZoneDeClic = true; 
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterDialogue = new ImageButton(context);
        btnAjouterDialogue.setImageResource(R.drawable.chat_24px);
        styliserBoutonIcone(btnAjouterDialogue);
        btnAjouterDialogue.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            
            String nomFond = genererNomUnique(Traducteur.get("obj_prefix_boitedialogue"), editeur.sceneActive);
            ObjetBase fond = new ObjetBase(nomFond, 50f, 300f, 700f, 150f);
            fond.type = "image";
            fond.couleur = Color.argb(220, 30, 30, 30);
            fond.afficherFondColore = true;
            fond.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(fond);
            
            String nomTexte = genererNomUnique(Traducteur.get("obj_prefix_textedialogue"), editeur.sceneActive);
            ObjetBase texte = new ObjetBase(nomTexte, 20f, 20f, 600f, 110f);
            texte.type = "texte";
            texte.contenuTexte = Traducteur.get("texte_dialogue_defaut");
            texte.couleur = Color.WHITE;
            texte.tailleFonte = 20f;
            texte.parentId = fond.id;
            texte.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(texte);
            
            String nomBtn = genererNomUnique(Traducteur.get("obj_prefix_btnfermer"), editeur.sceneActive);
            ObjetBase btn = new ObjetBase(nomBtn, 640f, 10f, 40f, 40f);
            btn.type = "bouton";
            btn.estZoneDeClic = true;
            btn.couleur = Color.parseColor("#E53935");
            btn.afficherFondColore = true;
            btn.parentId = fond.id;
            btn.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(btn);

            canvasEditeur.invalidate();
            rafraichirArborescence();
            Toast.makeText(context, Traducteur.get("toast_groupe_dialogue_cree"), Toast.LENGTH_SHORT).show();
        });

        ImageButton btnAjouterJoystick = new ImageButton(context);
        btnAjouterJoystick.setImageResource(R.drawable.trackpad_input_24px); 
        styliserBoutonIcone(btnAjouterJoystick);
        btnAjouterJoystick.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_joystick"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 50f, 400f, 160f, 160f);
            nouveau.type = "joystick"; 
            nouveau.afficherFondColore = false; 
            nouveau.couleur = Color.argb(100, 200, 200, 200);
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        ImageButton btnAjouterBtnAction = new ImageButton(context);
        btnAjouterBtnAction.setImageResource(R.drawable.center_focus_weak_24px); 
        styliserBoutonIcone(btnAjouterBtnAction);
        btnAjouterBtnAction.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_btnaction"), editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 600f, 450f, 100f, 100f);
            nouveau.type = "bouton_action"; 
            nouveau.afficherFondColore = false; 
            nouveau.couleur = Color.argb(150, 255, 100, 100);
            nouveau.zOrder = editeur.sceneActive.prochainZOrder();
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
        });

        // --- NOUVEAU BOUTON : AJOUTER UN PREFAB (SCENE_INSTANCE) ---
        ImageButton btnAjouterPrefab = new ImageButton(context);
        btnAjouterPrefab.setImageResource(R.drawable.display_add_24px);
        styliserBoutonIcone(btnAjouterPrefab);
        btnAjouterPrefab.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            
            List<Scene> autresScenes = new ArrayList<>();
            if (editeur.listeScenes != null) {
                for (Scene s : editeur.listeScenes) {
                    if (s != editeur.sceneActive) {
                        autresScenes.add(s);
                    }
                }
            }
            
            if (autresScenes.isEmpty()) {
                Toast.makeText(context, Traducteur.get("erreur_aucune_autre_scene"), Toast.LENGTH_SHORT).show();
                return;
            }
            
            Dialog dialog = new Dialog(context);
            dialog.setTitle(Traducteur.get("titre_select_scene_liee"));
            LinearLayout layoutDialog = new LinearLayout(context);
            layoutDialog.setOrientation(LinearLayout.VERTICAL);
            styliserDialogue(layoutDialog);
            
            for (Scene s : autresScenes) {
                Button btnScene = new Button(context);
                btnScene.setText(s.nom);
                btnScene.setTextColor(Palette.texteNormal);
                btnScene.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
                btnScene.setPadding(dp(12), dp(12), dp(12), dp(12));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, dp(8));
                btnScene.setLayoutParams(lp);
                
                btnScene.setOnClickListener(vScene -> {
                    String nomUnique = genererNomUnique(Traducteur.get("obj_prefix_prefab"), editeur.sceneActive);
                    ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 150f, 150f);
                    nouveau.type = "scene_instance";
                    nouveau.sceneLieeId = s.nom; 
                    nouveau.afficherFondColore = true;
                    nouveau.couleur = Color.argb(120, 100, 150, 255); 
                    nouveau.zOrder = editeur.sceneActive.prochainZOrder();
                    editeur.sceneActive.ajouterObjet(nouveau);
                    
                    canvasEditeur.invalidate();
                    rafraichirArborescence();
                    dialog.dismiss();
                });
                layoutDialog.addView(btnScene);
            }
            
            Button btnAnnuler = new Button(context);
            btnAnnuler.setText(Traducteur.get("bouton_annuler"));
            btnAnnuler.setTextColor(Palette.texteNormal);
            btnAnnuler.setBackground(fond(Palette.boutonNormal, Palette.bordure, 8));
            btnAnnuler.setPadding(dp(16), dp(12), dp(16), dp(12));
            btnAnnuler.setOnClickListener(vAnnuler -> dialog.dismiss());
            layoutDialog.addView(btnAnnuler);
            
            dialog.setContentView(layoutDialog);
            dialog.show();
        });

        ligne1.addView(btnAjouterCarre);
        ligne1.addView(btnAjouterTexte);
        ligne1.addView(btnAjouterRond);
        
        ligne2.addView(btnAjouterImage);
        ligne2.addView(btnAjouterZone);
        ligne2.addView(btnAjouterBouton);
        
        ligne3.addView(btnAjouterDialogue);
        View espace1 = new View(context);
        espace1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ligne3.addView(espace1);
        View espace2 = new View(context);
        espace2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ligne3.addView(espace2);

        ligne4.addView(btnAjouterJoystick);
        ligne4.addView(btnAjouterBtnAction);
        ligne4.addView(btnAjouterPrefab); // L'espace vide a été remplacé par le bouton Prefab

        contenu.addView(ligne1);
        contenu.addView(ligne2);
        contenu.addView(ligne3);
        contenu.addView(ligne4);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(Traducteur.get("panneau_ress_objets") + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(Traducteur.get("panneau_ress_objets") + " ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }
// bas 4
// haut 5 : SECTION ARBORESCENCE (Hierarchie objets)
    private View creerSectionArborescence(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(Traducteur.get("panneau_ress_arborescence") + " ▼");
        styliserTitreSection(btnTitre);

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        styliserContenuSection(contenu);

        conteneurArborescence = new LinearLayout(context);
        conteneurArborescence.setOrientation(LinearLayout.VERTICAL);
        contenu.addView(conteneurArborescence);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(Traducteur.get("panneau_ress_arborescence") + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(Traducteur.get("panneau_ress_arborescence") + " ▼");
                rafraichirArborescence();
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    public void setObjetSelectionne(ObjetBase objet) {
        this.objetSelectionne = objet;
        rafraichirArborescence();
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
                txtObjet.setTextColor(obj == objetSelectionne ? Palette.texteSelectionne : Palette.texteNormal);
                txtObjet.setPadding(dp(10), dp(9), dp(10), dp(9));
                txtObjet.setTextSize(14f);
                if (obj == objetSelectionne) {
                    txtObjet.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
                }
                
                txtObjet.setOnClickListener(v -> {
                    objetSelectionne = obj;
                    canvasEditeur.setObjetSelectionne(obj);
                    rafraichirArborescence();
                });
                
                conteneurArborescence.addView(txtObjet);
            }
        }
        
        if (conteneurArborescence.getChildCount() == 0) {
            TextView txtVide = new TextView(getContext());
            txtVide.setText(Traducteur.get("msg_aucun_objet_scene"));
            txtVide.setTextColor(Palette.bordure);
            txtVide.setTextSize(13f);
            txtVide.setPadding(dp(10), dp(10), dp(10), dp(10));
            conteneurArborescence.addView(txtVide);
        }
    }
// bas 5
    // haut 6
    private boolean isRacineIndestructible(File dir) {
        if (dir == null) return false;
        String nom = dir.getName();
        return (dir.getParentFile() != null && dir.getParentFile().equals(rootAssetsDir)) &&
               (nom.equals("Images") || nom.equals("Sons") || nom.equals("Fonts") || nom.equals("Textes"));
    }

    private View creerSectionAssets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(Traducteur.get("panneau_ress_assets") + " ▼");
        styliserTitreSection(btnTitre);

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        styliserContenuSection(contenu);

        conteneurArborescenceDossiers = new LinearLayout(context);
        conteneurArborescenceDossiers.setOrientation(LinearLayout.VERTICAL);

        LinearLayout boutonsDossiers = new LinearLayout(context);
        boutonsDossiers.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageButton btnAddFolder = new ImageButton(context);
        btnAddFolder.setImageResource(R.drawable.add_24px);
        styliserBoutonIcone(btnAddFolder);
        
        ImageButton btnEditFolder = new ImageButton(context);
        btnEditFolder.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonIcone(btnEditFolder);
        
        ImageButton btnDelFolder = new ImageButton(context);
        btnDelFolder.setImageResource(R.drawable.delete_24px);
        styliserBoutonIcone(btnDelFolder);

        btnAddFolder.setOnClickListener(v -> {
            if (currentFolderSelected != null) afficherPopupNouveauDossier(context);
        });
        btnEditFolder.setOnClickListener(v -> {
            if (currentFolderSelected != null && !isRacineIndestructible(currentFolderSelected)) {
                afficherPopupRenommerDossier(context, currentFolderSelected);
            }
        });
        btnDelFolder.setOnClickListener(v -> {
            if (currentFolderSelected != null && !isRacineIndestructible(currentFolderSelected)) {
                afficherPopupSupprimerDossier(context, currentFolderSelected);
            }
        });

        boutonsDossiers.addView(btnAddFolder);
        boutonsDossiers.addView(btnEditFolder);
        boutonsDossiers.addView(btnDelFolder);

        conteneurListeAssets = new LinearLayout(context);
        conteneurListeAssets.setOrientation(LinearLayout.VERTICAL);
        conteneurListeAssets.setPadding(0, dp(10), 0, 0);

        LinearLayout boutonsAssets = new LinearLayout(context);
        boutonsAssets.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnImportAsset = new ImageButton(context);
        btnImportAsset.setImageResource(R.drawable.upload_file_24px);
        styliserBoutonIcone(btnImportAsset);
        
        ImageButton btnEditAsset = new ImageButton(context);
        btnEditAsset.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonIcone(btnEditAsset);
        
        ImageButton btnDelAsset = new ImageButton(context);
        btnDelAsset.setImageResource(R.drawable.delete_24px);
        styliserBoutonIcone(btnDelAsset);

        btnImportAsset.setOnClickListener(v -> {
            if (currentFolderSelected == null) return;
            String chemin = currentFolderSelected.getAbsolutePath();
            String mime = chemin.contains("/Images") ? "image/*" : "*/*";
            ((InterfaceEditeur)context).lancerImportAsset(mime);
        });
        btnEditAsset.setOnClickListener(v -> {
            if (currentAssetSelected != null) afficherPopupRenommerAsset(context, currentAssetSelected);
        });
        btnDelAsset.setOnClickListener(v -> {
            if (currentAssetSelected != null) afficherPopupSupprimerAsset(context, currentAssetSelected);
        });

        boutonsAssets.addView(btnImportAsset);
        boutonsAssets.addView(btnEditAsset);
        boutonsAssets.addView(btnDelAsset);

        Button btnEditeurDial = new Button(context);
        btnEditeurDial.setText(Traducteur.get("btn_ouvrir_dialogues"));
        btnEditeurDial.setAllCaps(false);
        btnEditeurDial.setTextColor(Color.WHITE);
        btnEditeurDial.setBackground(fond(Color.parseColor("#4CAF50"), Palette.bordure, 8));
        LinearLayout.LayoutParams lpBtnDial = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBtnDial.setMargins(0, dp(8), 0, 0);
        btnEditeurDial.setLayoutParams(lpBtnDial);
        btnEditeurDial.setOnClickListener(v -> {
            afficherEditeurTexteGeant(context);
        });
        
        Button btnAnimations = new Button(context);
        btnAnimations.setText(Traducteur.get("btn_gerer_animations"));
        btnAnimations.setAllCaps(false);
        btnAnimations.setTextColor(Color.WHITE);
        btnAnimations.setBackground(fond(Color.parseColor("#673AB7"), Palette.bordure, 8)); 
        LinearLayout.LayoutParams lpBtnAnim = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBtnAnim.setMargins(0, dp(8), 0, 0);
        btnAnimations.setLayoutParams(lpBtnAnim);
        btnAnimations.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            EditeurAnimationsDialog dialog = new EditeurAnimationsDialog(context, editeur.cheminProjet);
            dialog.show();
        });

        contenu.addView(conteneurArborescenceDossiers);
        contenu.addView(boutonsDossiers);
        contenu.addView(conteneurListeAssets);
        contenu.addView(boutonsAssets);
        contenu.addView(btnEditeurDial);
        contenu.addView(btnAnimations);

        rafraichirSectionAssetsTotale();

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(Traducteur.get("panneau_ress_assets") + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(Traducteur.get("panneau_ress_assets") + " ▼");
                rafraichirSectionAssetsTotale();
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    public void rafraichirSectionAssetsTotale() {
        rafraichirArborescenceDossiers();
        rafraichirListeAssets();
    }

    private void rafraichirArborescenceDossiers() {
        if (conteneurArborescenceDossiers == null) return;
        conteneurArborescenceDossiers.removeAllViews();
        construireArbreDossiers(rootAssetsDir, -1);
    }

    private void construireArbreDossiers(File dir, int depth) {
        if (dir == null || !dir.exists()) return;

        if (depth >= 0) {
            LinearLayout layoutDossier = new LinearLayout(getContext());
            layoutDossier.setOrientation(LinearLayout.HORIZONTAL);
            layoutDossier.setGravity(android.view.Gravity.CENTER_VERTICAL);
            layoutDossier.setPadding(dp(6), dp(4), dp(6), dp(4));
            if (dir.equals(currentFolderSelected)) {
                layoutDossier.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
            }
            
            if (depth > 0) {
                TextView tvPrefix = new TextView(getContext());
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < depth; i++) prefix.append("   ");
                tvPrefix.setText(prefix.toString());
                layoutDossier.addView(tvPrefix);
            }
            
            ImageView iconeDossier = new ImageView(getContext());
            iconeDossier.setImageResource(R.drawable.folder_open_24px);
            iconeDossier.setColorFilter(dir.equals(currentFolderSelected) ? Palette.iconeSurvol : Palette.iconeNormal);
            iconeDossier.setPadding(0, 0, dp(8), 0);
            
            TextView tv = new TextView(getContext());
            
            // TACHE 3 : Traduction dynamique des dossiers
            String cleDossier = "dossier_" + dir.getName().toLowerCase();
            String nomAffiche = Traducteur.get(cleDossier);
            if (nomAffiche.startsWith("[")) nomAffiche = dir.getName(); // Fallback si non trouvé
            tv.setText(nomAffiche);
            
            tv.setTextColor(dir.equals(currentFolderSelected) ? Palette.texteSelectionne : Palette.texteNormal);
            tv.setPadding(0, dp(6), 0, dp(6));
            tv.setTextSize(14f);
            
            layoutDossier.addView(iconeDossier);
            layoutDossier.addView(tv);
            
            layoutDossier.setOnClickListener(v -> {
                currentFolderSelected = dir;
                currentAssetSelected = null;
                rafraichirSectionAssetsTotale();
            });
            conteneurArborescenceDossiers.addView(layoutDossier);
        }

        File[] enfants = dir.listFiles();
        if (enfants != null) {
            java.util.Arrays.sort(enfants, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            for (File f : enfants) {
                if (f.isDirectory()) construireArbreDossiers(f, depth + 1);
            }
        }
    }
// bas 6
    
// haut 7 : SECTION ASSETS LOGIQUE (Popups et import)
    private void rafraichirListeAssets() {
        if (conteneurListeAssets == null || currentFolderSelected == null) return;
        conteneurListeAssets.removeAllViews();
        
        File[] fichiers = currentFolderSelected.listFiles();
        if (fichiers != null) {
            java.util.Arrays.sort(fichiers, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            for (File f : fichiers) {
                if (!f.isDirectory()) ajouterVueAsset(f);
            }
        }
    }

    private void ajouterVueAsset(File f) {
        Context context = getContext();
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(dp(6), dp(6), dp(6), dp(6));
        itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        if (f.equals(currentAssetSelected)) {
            itemLayout.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
        }
        
        boolean isImage = f.getAbsolutePath().contains("/Images/");
        
        if (isImage) {
            ImageView miniature = new ImageView(context);
            miniature.setLayoutParams(new LinearLayout.LayoutParams(dp(36), dp(36)));
            try {
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                miniature.setImageBitmap(bmp);
            } catch (Exception e) {}
            miniature.setScaleType(ImageView.ScaleType.CENTER_CROP);
            miniature.setPadding(0, 0, dp(8), 0);
            itemLayout.addView(miniature);
        }
        
        TextView nom = new TextView(context);
        nom.setText(f.getName());
        nom.setTextSize(14f);
        nom.setTextColor(f.equals(currentAssetSelected) ? Palette.texteSelectionne : Palette.texteNormal);
        nom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        
        itemLayout.setOnClickListener(v -> {
            currentAssetSelected = f;
            rafraichirListeAssets();
        });
        
        itemLayout.addView(nom);
        conteneurListeAssets.addView(itemLayout);
    }

    public void traiterImportAsset(Uri uri) {
        Context context = getContext();
        String nomOriginal = getFileNameFromUri(context, uri);
        if (nomOriginal == null) nomOriginal = "asset_import";
        
        String nomBase = nomOriginal;
        String extension = "";
        int dotIdx = nomOriginal.lastIndexOf('.');
        if (dotIdx > 0) {
            nomBase = nomOriginal.substring(0, dotIdx);
            extension = nomOriginal.substring(dotIdx);
        }
        
        File fichierCible = genererNomFichierUnique(currentFolderSelected, nomBase, extension);
        
        try (InputStream in = context.getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(fichierCible)) {
            byte[] buffer = new byte[1024];
            int lu;
            while ((lu = in.read(buffer)) != -1) {
                out.write(buffer, 0, lu);
            }
            rafraichirSectionAssetsTotale();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(idx >= 0) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
    
    private File genererNomFichierUnique(File dossier, String base, String extension) {
        File f = new File(dossier, base + extension);
        if (!f.exists()) return f;
        
        int index = 1;
        while (true) {
            f = new File(dossier, base + "_" + index + extension);
            if (!f.exists()) return f;
            index++;
        }
    }
    
    private void supprimerRecursif(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if(files != null) {
                for (File child : files) supprimerRecursif(child);
            }
        }
        fileOrDirectory.delete();
    }

    private void afficherPopupNouveauDossier(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_nouveau_dossier"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        EditText champTexte = new EditText(context);
        champTexte.setHint(Traducteur.get("hint_nom_dossier"));
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString().trim();
            if (!nom.isEmpty()) {
                File nouveauDossier = new File(currentFolderSelected, nom);
                if (!nouveauDossier.exists()) nouveauDossier.mkdirs();
                rafraichirSectionAssetsTotale();
            }
            dialog.dismiss();
        });
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerDossier(Context context, File dir) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_renommer_dossier"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        EditText champTexte = new EditText(context);
        champTexte.setText(dir.getName());
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if (!nouveauNom.isEmpty()) {
                File newFile = new File(dir.getParentFile(), nouveauNom);
                if (!newFile.exists()) {
                    dir.renameTo(newFile);
                    currentFolderSelected = newFile;
                    rafraichirSectionAssetsTotale();
                }
            }
            dialog.dismiss();
        });
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerDossier(Context context, File dir) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_confirmer"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        TextView txtMessage = new TextView(context);
        txtMessage.setText(Traducteur.get("msg_supprimer_dossier"));
        txtMessage.setTextColor(Palette.texteNormal);
        txtMessage.setTextSize(15f);
        txtMessage.setPadding(0, 0, 0, dp(14));
        layoutDialog.addView(txtMessage);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnOui);
        btnOui.setOnClickListener(v -> {
            supprimerRecursif(dir);
            currentFolderSelected = new File(rootAssetsDir, "Images");
            currentAssetSelected = null;
            rafraichirSectionAssetsTotale();
            dialog.dismiss();
        });
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnNon);
        btnNon.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerAsset(Context context, File f) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_renommer"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        EditText champTexte = new EditText(context);
        champTexte.setText(f.getName());
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if (!nouveauNom.isEmpty()) {
                File newFile = new File(f.getParentFile(), nouveauNom);
                if (!newFile.exists()) {
                    f.renameTo(newFile);
                    currentAssetSelected = newFile;
                    rafraichirSectionAssetsTotale();
                }
            }
            dialog.dismiss();
        });
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerAsset(Context context, File f) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_confirmer"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        TextView txtMessage = new TextView(context);
        txtMessage.setText(Traducteur.get("msg_supprimer_asset"));
        txtMessage.setTextColor(Palette.texteNormal);
        txtMessage.setTextSize(15f);
        txtMessage.setPadding(0, 0, 0, dp(14));
        layoutDialog.addView(txtMessage);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnOui);
        btnOui.setOnClickListener(v -> {
            f.delete();
            currentAssetSelected = null;
            rafraichirSectionAssetsTotale();
            dialog.dismiss();
        });
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnNon);
        btnNon.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
// bas 7

// haut 8 : SECTION DIALOGUES LOGIQUE
    private void afficherEditeurTexteGeant(Context context) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setBackgroundColor(Palette.fondPanneaux);
        layoutDialog.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView titre = new TextView(context);
        titre.setText(Traducteur.get("titre_editeur_script"));
        titre.setTextColor(Palette.texteSelectionne);
        titre.setTextSize(18f);
        titre.setTypeface(null, android.graphics.Typeface.BOLD);
        titre.setPadding(0, 0, 0, dp(10));
        layoutDialog.addView(titre);

        ScrollView scroll = new ScrollView(context);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scroll.setLayoutParams(scrollParams);
        scroll.setFillViewport(true);

        EditText champTexte = new EditText(context);
        champTexte.setBackground(fond(Palette.fondNormal, Palette.bordure, 8));
        champTexte.setTextColor(Palette.texteNormal);
        champTexte.setGravity(Gravity.TOP | Gravity.START);
        champTexte.setPadding(dp(12), dp(12), dp(12), dp(12));
        champTexte.setTextSize(14f);
        champTexte.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        
        File dirTextes = new File(rootAssetsDir, "Textes");
        if (!dirTextes.exists()) dirTextes.mkdirs();
        File fichierDialogues = new File(dirTextes, "dialogues.txt");
        
        String texteInitial = Traducteur.get("texte_aide_dialogues");

        if (fichierDialogues.exists()) {
            try {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierDialogues));
                StringBuilder sb = new StringBuilder();
                String ligne;
                while ((ligne = br.readLine()) != null) {
                    sb.append(ligne).append("\n");
                }
                br.close();
                if (sb.length() > 0) {
                    texteInitial = sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        champTexte.setText(texteInitial);
        scroll.addView(champTexte);
        layoutDialog.addView(scroll);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        zoneBoutons.setGravity(Gravity.END);
        zoneBoutons.setPadding(0, dp(10), 0, 0);

        Button btnAnnuler = new Button(context);
        btnAnnuler.setText(Traducteur.get("bouton_annuler"));
        btnAnnuler.setTextColor(Palette.texteNormal);
        btnAnnuler.setBackground(fond(Palette.boutonNormal, Palette.bordure, 8));
        btnAnnuler.setPadding(dp(16), dp(10), dp(16), dp(10));
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        Button btnSauvegarder = new Button(context);
        btnSauvegarder.setText(Traducteur.get("bouton_sauvegarder"));
        btnSauvegarder.setTextColor(Color.WHITE);
        btnSauvegarder.setBackground(fond(Color.parseColor("#4CAF50"), Palette.bordure, 8));
        btnSauvegarder.setPadding(dp(16), dp(10), dp(16), dp(10));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(dp(10), 0, 0, 0);
        btnSauvegarder.setLayoutParams(btnParams);
        
        btnSauvegarder.setOnClickListener(v -> {
            try {
                java.io.FileWriter fw = new java.io.FileWriter(fichierDialogues);
                fw.write(champTexte.getText().toString());
                fw.close();
                Toast.makeText(context, Traducteur.get("toast_script_sauvegarde"), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, Traducteur.get("erreur_sauvegarde"), Toast.LENGTH_LONG).show();
            }
        });

        zoneBoutons.addView(btnAnnuler);
        zoneBoutons.addView(btnSauvegarder);
        layoutDialog.addView(zoneBoutons);

        dialog.setContentView(layoutDialog);
        dialog.show();
    }
// bas 8
   // haut 9 : SECTION VARIABLES UI
    private View creerSectionVariables(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(Traducteur.get("panneau_ress_variables") + " ▼");
        styliserTitreSection(btnTitre);

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        styliserContenuSection(contenu);

        conteneurVariables = new LinearLayout(context);
        conteneurVariables.setOrientation(LinearLayout.VERTICAL);
        conteneurVariables.setPadding(0, 0, 0, dp(8));
        contenu.addView(conteneurVariables);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnCreer = new ImageButton(context);
        btnCreer.setImageResource(R.drawable.add_24px);
        styliserBoutonIcone(btnCreer);
        btnCreer.setOnClickListener(v -> afficherPopupCreerVariable(context));

        ImageButton btnRenommer = new ImageButton(context);
        btnRenommer.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonIcone(btnRenommer);
        btnRenommer.setOnClickListener(v -> {
            if (variableSelectionnee != null) afficherPopupRenommerVariable(context, variableSelectionnee);
        });

        ImageButton btnSupprimer = new ImageButton(context);
        btnSupprimer.setImageResource(R.drawable.delete_24px);
        styliserBoutonIcone(btnSupprimer);
        btnSupprimer.setOnClickListener(v -> {
            if (variableSelectionnee != null) afficherPopupSupprimerVariable(context, variableSelectionnee);
        });

        zoneBoutons.addView(btnCreer);
        zoneBoutons.addView(btnRenommer);
        zoneBoutons.addView(btnSupprimer);

        contenu.addView(zoneBoutons);
        rafraichirVariables();

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(Traducteur.get("panneau_ress_variables") + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(Traducteur.get("panneau_ress_variables") + " ▼");
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
            for (Variable var : editeur.variablesGlobales) ajouterVueVariable(var);
        }
        if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
            for (Variable var : editeur.sceneActive.variablesLocales) ajouterVueVariable(var);
        }
    }

    private void ajouterVueVariable(Variable var) {
        Context context = getContext();
        LinearLayout conteneurLigne = new LinearLayout(context);
        conteneurLigne.setOrientation(LinearLayout.VERTICAL);
        conteneurLigne.setPadding(0, dp(3), 0, dp(3));
        
        TextView nomVariable = new TextView(context);
        
        String labelType;
        switch (var.type) {
            case "BOOLEEN": labelType = Traducteur.get("var_type_booleen"); break;
            case "CHIFFRE": labelType = Traducteur.get("var_type_chiffre"); break;
            case "ENTIER": labelType = Traducteur.get("var_type_entier"); break;
            case "LISTE_INVENTAIRE": labelType = Traducteur.get("var_type_liste"); break;
            default: labelType = Traducteur.get("var_type_texte"); break;
        }
        
        String labelScope = var.scope.equals("GLOBALE") ? Traducteur.get("var_scope_globale") : Traducteur.get("var_scope_locale");
        
        String texteValeur;
        if (var.type.equals("LISTE_INVENTAIRE") && var.valeur instanceof java.util.List) {
            texteValeur = ((java.util.List<?>) var.valeur).size() + Traducteur.get("var_objets");
        } else {
            texteValeur = String.valueOf(var.valeur);
        }
        
        nomVariable.setText(var.nom + " [" + labelScope + ", " + labelType + "] = " + texteValeur);
        
        if (var == variableSelectionnee) {
            nomVariable.setTextColor(Palette.texteSelectionne);
            nomVariable.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
        } else {
            nomVariable.setTextColor(var.scope.equals("GLOBALE") ? Color.parseColor("#ADD8E6") : Color.parseColor("#90EE90"));
        }
        
        nomVariable.setPadding(dp(10), dp(8), dp(10), dp(8));
        nomVariable.setTextSize(14f);

        nomVariable.setOnClickListener(v -> {
            variableSelectionnee = var;
            rafraichirVariables();
        });

        conteneurLigne.addView(nomVariable);
        conteneurVariables.addView(conteneurLigne);
    }
// bas 9
   // haut 10 : SECTION VARIABLES POPUPS
    private void afficherPopupCreerVariable(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_creer_var_titre"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        
        EditText champTexte = new EditText(context);
        champTexte.setHint(Traducteur.get("hint_nom_var"));
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        
        TextView txtScope = new TextView(context);
        txtScope.setText(Traducteur.get("label_portee"));
        txtScope.setTextColor(Palette.texteSelectionne);
        txtScope.setPadding(0, dp(12), 0, dp(4));
        layoutDialog.addView(txtScope);
        
        Spinner spinnerScope = new Spinner(context);
        String[] scopeArray = {Traducteur.get("var_scope_locale"), Traducteur.get("var_scope_globale")};
        ArrayAdapter<String> adapterScope = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, scopeArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Palette.texteNormal);
                return tv;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(Palette.texteNormal);
                tv.setBackgroundColor(Palette.fondNormal);
                tv.setPadding(dp(16), dp(16), dp(16), dp(16));
                return tv;
            }
        };
        spinnerScope.setAdapter(adapterScope);
        spinnerScope.setBackground(fond(Palette.fondNormal, Palette.bordure, 8));
        layoutDialog.addView(spinnerScope);
        
        TextView txtType = new TextView(context);
        txtType.setText(Traducteur.get("label_type"));
        txtType.setTextColor(Palette.texteSelectionne);
        txtType.setPadding(0, dp(12), 0, dp(4));
        layoutDialog.addView(txtType);
        
        Spinner spinnerType = new Spinner(context);
        String[] typeArray = {Traducteur.get("var_type_chiffre"), Traducteur.get("var_type_entier"), Traducteur.get("var_type_texte"), Traducteur.get("var_type_booleen"), Traducteur.get("var_type_liste")};
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, typeArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Palette.texteNormal);
                return tv;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(Palette.texteNormal);
                tv.setBackgroundColor(Palette.fondNormal);
                tv.setPadding(dp(16), dp(16), dp(16), dp(16));
                return tv;
            }
        };
        spinnerType.setAdapter(adapterType);
        spinnerType.setBackground(fond(Palette.fondNormal, Palette.bordure, 8));
        layoutDialog.addView(spinnerType);
        
        EditText champValeurInit = new EditText(context);
        champValeurInit.setHint(Traducteur.get("hint_valeur_init"));
        styliserChampDialogue(champValeurInit);
        layoutDialog.addView(champValeurInit);
        
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerType.getSelectedItem().toString().equals(Traducteur.get("var_type_liste"))) champValeurInit.setVisibility(View.GONE);
                else champValeurInit.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString().trim();
            if(nom.isEmpty()) return;
            
            String scopeSelect = spinnerScope.getSelectedItem().toString().equals(Traducteur.get("var_scope_globale")) ? "GLOBALE" : "LOCALE";
            String typeSelectText = spinnerType.getSelectedItem().toString();
            String typeSelect = "CHIFFRE";
            
            if (typeSelectText.equals(Traducteur.get("var_type_texte"))) typeSelect = "TEXTE";
            if (typeSelectText.equals(Traducteur.get("var_type_booleen"))) typeSelect = "BOOLEEN";
            if (typeSelectText.equals(Traducteur.get("var_type_entier"))) typeSelect = "ENTIER";
            if (typeSelectText.equals(Traducteur.get("var_type_liste"))) typeSelect = "LISTE_INVENTAIRE";
            
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            
            if (scopeSelect.equals("GLOBALE")) {
                for (Variable vExistant : editeur.variablesGlobales) {
                    if (vExistant.nom.equals(nom)) return;
                }
            } else {
                for (Variable vExistant : editeur.sceneActive.variablesLocales) {
                    if (vExistant.nom.equals(nom)) return;
                }
            }
            
            Variable nouvelleVar = new Variable(nom, scopeSelect, typeSelect);
            String valInitTexte = champValeurInit.getText().toString().trim();
            
            if (!valInitTexte.isEmpty()) {
                if (typeSelect.equals("CHIFFRE")) {
                    try { nouvelleVar.valeur = Float.parseFloat(valInitTexte); } catch (Exception e) { nouvelleVar.valeur = 0f; }
                } else if (typeSelect.equals("TEXTE")) {
                    nouvelleVar.valeur = valInitTexte;
                } else if (typeSelect.equals("BOOLEEN")) {
                    String cleanVal = valInitTexte.toLowerCase();
                    nouvelleVar.valeur = (cleanVal.equals("oui") || cleanVal.equals("vrai") || cleanVal.equals("true"));
                } else if (typeSelect.equals("ENTIER")) {
                    try { nouvelleVar.valeur = Integer.parseInt(valInitTexte); } catch (Exception e) { nouvelleVar.valeur = 0; }
                }
            }
            
            if (scopeSelect.equals("GLOBALE")) editeur.variablesGlobales.add(nouvelleVar);
            else editeur.sceneActive.variablesLocales.add(nouvelleVar);
            
            rafraichirVariables();
            dialog.dismiss();
        });
        
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerVariable(Context context, Variable var) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_renommer_var"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        EditText champTexte = new EditText(context);
        champTexte.setText(var.nom);
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if(nouveauNom.isEmpty()) return;
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            if (var.scope.equals("GLOBALE")) {
                for (Variable vExistant : editeur.variablesGlobales) {
                    if (vExistant != var && vExistant.nom.equals(nouveauNom)) return;
                }
            } else {
                for (Variable vExistant : editeur.sceneActive.variablesLocales) {
                    if (vExistant != var && vExistant.nom.equals(nouveauNom)) return;
                }
            }
            var.nom = nouveauNom;
            rafraichirVariables();
            dialog.dismiss();
        });
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerVariable(Context context, Variable var) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_supprimer_var"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        TextView txtMessage = new TextView(context);
        txtMessage.setText(Traducteur.get("msg_suppr_var_1") + var.nom + Traducteur.get("msg_suppr_var_2"));
        txtMessage.setTextColor(Palette.texteNormal);
        txtMessage.setTextSize(15f);
        txtMessage.setPadding(0, 0, 0, dp(14));
        layoutDialog.addView(txtMessage);
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnOui);
        btnOui.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            if (var.scope.equals("GLOBALE")) editeur.variablesGlobales.remove(var);
            else editeur.sceneActive.variablesLocales.remove(var);
            if (var == variableSelectionnee) variableSelectionnee = null;
            rafraichirVariables();
            dialog.dismiss();
        });
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnNon);
        btnNon.setOnClickListener(v -> dialog.dismiss());
        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
// bas 10
// haut 11 : SECTION FONCTIONS UI
    private View creerSectionFonctions(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(Traducteur.get("panneau_ress_fonctions") + " ▼");
        styliserTitreSection(btnTitre);

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        styliserContenuSection(contenu);

        conteneurFonctions = new LinearLayout(context);
        conteneurFonctions.setOrientation(LinearLayout.VERTICAL);
        conteneurFonctions.setPadding(0, 0, 0, dp(8));
        contenu.addView(conteneurFonctions);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnCreer = new ImageButton(context);
        btnCreer.setImageResource(R.drawable.add_24px);
        styliserBoutonIcone(btnCreer);
        btnCreer.setOnClickListener(v -> afficherPopupCreerFonction(context));

        ImageButton btnEditer = new ImageButton(context);
        btnEditer.setImageResource(R.drawable.account_tree_24px); 
        styliserBoutonIcone(btnEditer);
        btnEditer.setOnClickListener(v -> {
            if (fonctionSelectionnee != null) {
                android.content.Intent intent = new android.content.Intent(context, InterfaceBlueprint.class);
                intent.putExtra("cheminProjet", cheminProjet);
                intent.putExtra("modeFonction", true);
                intent.putExtra("nomFonction", fonctionSelectionnee);
                InterfaceBlueprint.sceneACharger = null; 
                context.startActivity(intent);
            }
        });

        ImageButton btnRenommer = new ImageButton(context);
        btnRenommer.setImageResource(R.drawable.edit_square_24px);
        styliserBoutonIcone(btnRenommer);
        btnRenommer.setOnClickListener(v -> {
            if (fonctionSelectionnee != null) afficherPopupRenommerFonction(context, fonctionSelectionnee);
        });

        ImageButton btnSupprimer = new ImageButton(context);
        btnSupprimer.setImageResource(R.drawable.delete_24px);
        styliserBoutonIcone(btnSupprimer);
        btnSupprimer.setOnClickListener(v -> {
            if (fonctionSelectionnee != null) afficherPopupSupprimerFonction(context, fonctionSelectionnee);
        });

        zoneBoutons.addView(btnCreer);
        zoneBoutons.addView(btnEditer);
        zoneBoutons.addView(btnRenommer);
        zoneBoutons.addView(btnSupprimer);

        contenu.addView(zoneBoutons);
        rafraichirFonctions();

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(Traducteur.get("panneau_ress_fonctions") + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(Traducteur.get("panneau_ress_fonctions") + " ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    public void rafraichirFonctions() {
        if (conteneurFonctions == null || rootFonctionsDir == null) return;
        conteneurFonctions.removeAllViews();

        File[] fichiers = rootFonctionsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (fichiers != null) {
            java.util.Arrays.sort(fichiers, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            for (File f : fichiers) {
                String nomFonc = f.getName().replace(".json", "");
                
                TextView nomView = new TextView(getContext());
                nomView.setText("ƒ " + nomFonc);
                if (nomFonc.equals(fonctionSelectionnee)) {
                    nomView.setTextColor(Palette.texteSelectionne);
                    nomView.setBackground(fond(Palette.fondListe, Palette.bordure, 8));
                } else {
                    nomView.setTextColor(Color.parseColor("#E040FB")); 
                }
                nomView.setPadding(dp(10), dp(8), dp(10), dp(8));
                nomView.setTextSize(14f);

                nomView.setOnClickListener(v -> {
                    fonctionSelectionnee = nomFonc;
                    rafraichirFonctions();
                });

                conteneurFonctions.addView(nomView);
            }
        }
    }
// bas 11

// haut 12 : SECTION FONCTIONS POPUPS
    private void afficherPopupCreerFonction(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_creer_fonction"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        
        EditText champTexte = new EditText(context);
        champTexte.setHint(Traducteur.get("hint_nom_fonction"));
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString().trim();
            if(!nom.isEmpty()) {
                File nvFichier = new File(rootFonctionsDir, nom + ".json");
                if (nvFichier.exists()) {
                    Toast.makeText(context, Traducteur.get("erreur_fonction_existe"), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    nvFichier.createNewFile();
                    FileOutputStream fos = new FileOutputStream(nvFichier);
                    fos.write("{\"noeuds\":[],\"liens\":[]}".getBytes());
                    fos.close();
                    
                    fonctionSelectionnee = nom;
                    rafraichirFonctions();
                    dialog.dismiss();
                } catch(Exception e) {
                    Toast.makeText(context, Traducteur.get("erreur_creation"), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerFonction(Context context, String oldNom) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_renommer_fonction"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        
        EditText champTexte = new EditText(context);
        champTexte.setText(oldNom);
        styliserChampDialogue(champTexte);
        layoutDialog.addView(champTexte);
        
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnValider);
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if(!nouveauNom.isEmpty() && !nouveauNom.equals(oldNom)) {
                File oldFichier = new File(rootFonctionsDir, oldNom + ".json");
                File newFichier = new File(rootFonctionsDir, nouveauNom + ".json");
                if (newFichier.exists()) {
                    Toast.makeText(context, Traducteur.get("erreur_nom_pris"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (oldFichier.renameTo(newFichier)) {
                    fonctionSelectionnee = nouveauNom;
                    rafraichirFonctions();
                    dialog.dismiss();
                }
            }
        });
        
        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnAnnuler);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        
        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerFonction(Context context, String nomFonc) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle(Traducteur.get("popup_supprimer_fonction"));
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        styliserDialogue(layoutDialog);
        
        TextView txtMessage = new TextView(context);
        txtMessage.setText(Traducteur.get("msg_suppr_fonction_1") + nomFonc + Traducteur.get("msg_suppr_fonction_2"));
        txtMessage.setTextColor(Palette.texteNormal);
        txtMessage.setTextSize(15f);
        txtMessage.setPadding(0, 0, 0, dp(14));
        layoutDialog.addView(txtMessage);
        
        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        styliserBoutonIcone(btnOui);
        btnOui.setOnClickListener(v -> {
            File f = new File(rootFonctionsDir, nomFonc + ".json");
            if (f.exists()) f.delete();
            fonctionSelectionnee = null;
            rafraichirFonctions();
            dialog.dismiss();
        });
        
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        styliserBoutonIcone(btnNon);
        btnNon.setOnClickListener(v -> dialog.dismiss());
        
        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
// bas 12





    

    


    

    



    


    




