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
    private EditText champX;
    private EditText champY;
    private Button boutonSupprimer;

    private TextView valeurType;
    private EditText champLargeur, champHauteur, champRotation, champAlpha, champZOrder;
    // FIX 2: Ajout des champs d'Echelle pour refléter le redimensionnement dynamique
    private EditText champScaleX, champScaleY;
    private CheckBox cbVisible, cbVerrouille;
    private Button btnCouleur;
    private Button btnParent;
    
    private LinearLayout blocTexte;
    private EditText champContenu, champTaille;
    private Button btnCouleurTexte, btnPolice;

    // Composants pour l'image
    private LinearLayout blocImage;
    private Button btnChargerImage, btnSupprimerImage;
    private CheckBox cbFondColore;

    private Scene sceneActive;
    private CanvasEditeur canvasEditeur;
    private ObjetBase objetCourant;
    private boolean miseAJourEnCours = false;

    // NOUVEAU CHAMP
    private String cheminProjet;

    public InspecteurProprietes(Context context, Scene scene, CanvasEditeur canvas) {
        super(context);
        this.sceneActive = scene;
        this.canvasEditeur = canvas;
        initialiserInterface(context);
    }

    // NOUVEAU SETTER
    public void setCheminProjet(String cheminProjet) {
        this.cheminProjet = cheminProjet;
    }

    private void initialiserInterface(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(Palette.fondPanneaux);

        paramsOuvert = new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        LinearLayout enteteInspecteur = new LinearLayout(context);
        enteteInspecteur.setOrientation(LinearLayout.HORIZONTAL);
        enteteInspecteur.setPadding(10, 10, 10, 10);
        enteteInspecteur.setBackgroundColor(Palette.enTeteDialogues);

        titreInspecteur = new TextView(context);
        titreInspecteur.setText("INSPECTEUR");
        titreInspecteur.setTextSize(18f);
        titreInspecteur.setGravity(Gravity.CENTER_VERTICAL);
        titreInspecteur.setTextColor(Palette.texteNormal);
        LinearLayout.LayoutParams paramsTitre = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titreInspecteur.setLayoutParams(paramsTitre);

        boutonMasquer = new Button(context);
        boutonMasquer.setText(">");
        boutonMasquer.setBackgroundColor(Palette.boutonNormal);
        boutonMasquer.setTextColor(Palette.texteNormal);

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
        texteInfo.setTextColor(Palette.texteNormal);
        contenuInspecteur.addView(texteInfo);

        blocProprietes = new LinearLayout(context);
        blocProprietes.setOrientation(LinearLayout.VERTICAL);
        blocProprietes.setVisibility(View.GONE);

        valeurType = new TextView(context);
        valeurType.setPadding(0, 0, 0, 15);
        valeurType.setTextColor(Palette.texteNormal);
        valeurType.setTextSize(14f);
        blocProprietes.addView(valeurType);

        TextView labelNom = new TextView(context);
        labelNom.setText("Nom");
        labelNom.setTextColor(Palette.texteNormal);
        blocProprietes.addView(labelNom);

        LinearLayout layoutNom = new LinearLayout(context);
        layoutNom.setOrientation(LinearLayout.HORIZONTAL);
        
        champNom = new EditText(context);
        champNom.setSingleLine(true);
        champNom.setImeOptions(EditorInfo.IME_ACTION_DONE); 
        champNom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champNom.setTextColor(Palette.texteNormal);
        champNom.setBackgroundColor(Palette.canvasFond);
        layoutNom.addView(champNom);
        
        btnValiderNom = new Button(context);
        btnValiderNom.setText("OK");
        btnValiderNom.setBackgroundColor(Palette.boutonNormal);
        btnValiderNom.setTextColor(Palette.texteNormal);
        layoutNom.addView(btnValiderNom);
        
        blocProprietes.addView(layoutNom);
// bas 1
// haut 2
        TextView labelPos = new TextView(context);
        labelPos.setText("Position X / Y");
        labelPos.setTextColor(Palette.texteNormal);
        blocProprietes.addView(labelPos);

        LinearLayout layoutPos = new LinearLayout(context);
        layoutPos.setOrientation(LinearLayout.HORIZONTAL);
        
        champX = new EditText(context);
        champX.setHint("X");
        champX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champX.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champX.setTextColor(Palette.texteNormal);
        champX.setBackgroundColor(Palette.canvasFond);
        
        champY = new EditText(context);
        champY.setHint("Y");
        champY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champY.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champY.setTextColor(Palette.texteNormal);
        champY.setBackgroundColor(Palette.canvasFond);
        
        layoutPos.addView(champX);
        layoutPos.addView(champY);
        blocProprietes.addView(layoutPos);

        View.OnClickListener toastListener = v -> Toast.makeText(context, "Réglage bientôt disponible", Toast.LENGTH_SHORT).show();

        TextView labelDim = new TextView(context);
        labelDim.setText("Largeur / Hauteur");
        labelDim.setTextColor(Palette.texteNormal);
        blocProprietes.addView(labelDim);

        LinearLayout layoutDim = new LinearLayout(context);
        layoutDim.setOrientation(LinearLayout.HORIZONTAL);
        
        champLargeur = new EditText(context);
        champLargeur.setHint("Largeur");
        champLargeur.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        champLargeur.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champLargeur.setTextColor(Palette.texteNormal);
        champLargeur.setBackgroundColor(Palette.canvasFond);
        
        champHauteur = new EditText(context);
        champHauteur.setHint("Hauteur");
        champHauteur.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        champHauteur.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champHauteur.setTextColor(Palette.texteNormal);
        champHauteur.setBackgroundColor(Palette.canvasFond);
        
        layoutDim.addView(champLargeur);
        layoutDim.addView(champHauteur);
        blocProprietes.addView(layoutDim);
        
        // FIX 2: Ajout section Echelle
        TextView labelScale = new TextView(context);
        labelScale.setText("Echelle X / Y (Scale)");
        labelScale.setTextColor(Palette.texteNormal);
        blocProprietes.addView(labelScale);

        LinearLayout layoutScale = new LinearLayout(context);
        layoutScale.setOrientation(LinearLayout.HORIZONTAL);
        
        champScaleX = new EditText(context);
        champScaleX.setHint("Scale X");
        champScaleX.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champScaleX.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champScaleX.setTextColor(Palette.texteNormal);
        champScaleX.setBackgroundColor(Palette.canvasFond);
        
        champScaleY = new EditText(context);
        champScaleY.setHint("Scale Y");
        champScaleY.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champScaleY.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        champScaleY.setTextColor(Palette.texteNormal);
        champScaleY.setBackgroundColor(Palette.canvasFond);
        
        layoutScale.addView(champScaleX);
        layoutScale.addView(champScaleY);
        blocProprietes.addView(layoutScale);

        champRotation = new EditText(context);
        champRotation.setHint("Rotation (°)");
        champRotation.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champRotation.setTextColor(Palette.texteNormal);
        champRotation.setBackgroundColor(Palette.canvasFond);
        blocProprietes.addView(champRotation);

        btnCouleur = new Button(context);
        btnCouleur.setText("Couleur : Sélecteur");
        btnCouleur.setBackgroundColor(Palette.boutonNormal);
        btnCouleur.setTextColor(Palette.texteNormal);
        blocProprietes.addView(btnCouleur);

        champAlpha = new EditText(context);
        champAlpha.setHint("Transparence (0-1)");
        champAlpha.setFocusable(false);
        champAlpha.setOnClickListener(toastListener);
        champAlpha.setTextColor(Palette.texteNormal);
        champAlpha.setBackgroundColor(Palette.canvasFond);
        blocProprietes.addView(champAlpha);

        cbVisible = new CheckBox(context);
        cbVisible.setText("Visible");
        cbVisible.setTextColor(Palette.texteNormal);
        blocProprietes.addView(cbVisible);

        cbVerrouille = new CheckBox(context);
        cbVerrouille.setText("Verrouillé (empêche l'édition)");
        cbVerrouille.setOnClickListener(toastListener);
        cbVerrouille.setTextColor(Palette.texteNormal);
        blocProprietes.addView(cbVerrouille);

        TextView labelZOrder = new TextView(context);
        labelZOrder.setText("Calque (Z-Order)");
        labelZOrder.setTextColor(Palette.texteNormal);
        blocProprietes.addView(labelZOrder);

        champZOrder = new EditText(context);
        champZOrder.setHint("Calque (Z-Order)");
        champZOrder.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        champZOrder.setTextColor(Palette.texteNormal);
        champZOrder.setBackgroundColor(Palette.canvasFond);
        blocProprietes.addView(champZOrder);
// bas 2


// haut 3
        TextView labelParent = new TextView(context);
        labelParent.setText("Objet Parent");
        labelParent.setTextColor(Palette.texteNormal);
        blocProprietes.addView(labelParent);

        btnParent = new Button(context);
        btnParent.setText("Parent : Aucun");
        btnParent.setBackgroundColor(Palette.boutonNormal);
        btnParent.setTextColor(Palette.texteNormal);
        blocProprietes.addView(btnParent);

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
        blocTexte.setPadding(0, 15, 0, 0);

        TextView sepTexte = new TextView(context);
        sepTexte.setText("--- Propriétés Spécifiques Texte ---");
        sepTexte.setTextColor(Palette.texteNormal);
        sepTexte.setPadding(0, 10, 0, 10);
        blocTexte.addView(sepTexte);

        champContenu = new EditText(context);
        champContenu.setHint("Contenu du texte");
        champContenu.setFocusable(false);
        champContenu.setTextColor(Palette.texteNormal);
        champContenu.setBackgroundColor(Palette.canvasFond);
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
        champTaille.setTextColor(Palette.texteNormal);
        champTaille.setBackgroundColor(Palette.canvasFond);
        blocTexte.addView(champTaille);

        btnCouleurTexte = new Button(context);
        btnCouleurTexte.setText("Couleur du texte");
        btnCouleurTexte.setBackgroundColor(Palette.boutonNormal);
        btnCouleurTexte.setTextColor(Palette.texteNormal);
        blocTexte.addView(btnCouleurTexte);

        btnPolice = new Button(context);
        btnPolice.setText("Police : Sélecteur");
        btnPolice.setOnClickListener(toastListener);
        btnPolice.setBackgroundColor(Palette.boutonNormal);
        btnPolice.setTextColor(Palette.texteNormal);
        blocTexte.addView(btnPolice);
        
        blocProprietes.addView(blocTexte);

        // --- BLOC IMAGE ---
        blocImage = new LinearLayout(context);
        blocImage.setOrientation(LinearLayout.VERTICAL);
        blocImage.setPadding(0, 15, 0, 0);

        TextView sepImage = new TextView(context);
        sepImage.setText("--- Propriétés Image ---");
        sepImage.setTextColor(Palette.texteNormal);
        sepImage.setPadding(0, 10, 0, 10);
        blocImage.addView(sepImage);

        btnChargerImage = new Button(context);
        btnChargerImage.setText("Charger une image (Assets)");
        btnChargerImage.setBackgroundColor(Palette.boutonNormal);
        btnChargerImage.setTextColor(Palette.texteNormal);
        blocImage.addView(btnChargerImage);

        btnSupprimerImage = new Button(context);
        btnSupprimerImage.setText("Supprimer l'image");
        btnSupprimerImage.setBackgroundColor(Palette.boutonNormal);
        btnSupprimerImage.setTextColor(Palette.texteNormal);
        blocImage.addView(btnSupprimerImage);
// bas 3


// haut 4
        cbFondColore = new CheckBox(context);
        cbFondColore.setText("Afficher le fond coloré");
        cbFondColore.setTextColor(Palette.texteNormal);
        blocImage.addView(cbFondColore);

        blocProprietes.addView(blocImage);

        contenuInspecteur.addView(blocProprietes);

        boutonSupprimer = new Button(context);
        boutonSupprimer.setText("Supprimer l'objet");
        boutonSupprimer.setBackgroundColor(Color.parseColor("#8B3A3A"));
        boutonSupprimer.setTextColor(Palette.texteNormal);
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
            
            // CORRECTION: Utilisation de cheminProjet au lieu de context.getFilesDir()
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
// bas 4


// haut 5
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
        
        // FIX 2: Watchers Echelle
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
            
            // FIX 2: Mise à jour visuelle des valeurs d'échelle
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
            
            if ("texte".equals(objet.type)) {
                blocTexte.setVisibility(View.VISIBLE);
                champContenu.setText(objet.contenuTexte);
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
// bas 5




        
        
        


    
