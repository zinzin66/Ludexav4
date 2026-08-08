// haut 1 8 08
package com.ludexa.moteur;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.database.Cursor;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PanneauRessources extends ScrollView {

    private CanvasEditeur canvasEditeur;
    private LinearLayout conteneurScenes;
    private LinearLayout conteneurArborescence;
    private LinearLayout conteneurVariables;
    private Variable variableSelectionnee;
    private ObjetBase objetSelectionne;

    private File rootAssetsDir;
    private File currentFolderSelected;
    private File currentAssetSelected;
    
    private LinearLayout conteneurArborescenceDossiers;
    private LinearLayout conteneurListeAssets;
    
    private String cheminProjet;

    public PanneauRessources(Context context, CanvasEditeur canvas, String cheminProjet) {
        super(context);
        this.canvasEditeur = canvas;
        this.cheminProjet = cheminProjet;
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Palette.fondPanneaux);
        setLayoutParams(new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT));

        rootAssetsDir = new File(cheminProjet, "assets_ludexa");
        if (!rootAssetsDir.exists()) rootAssetsDir.mkdirs();
        
        File dirImages = new File(rootAssetsDir, "Images");
        File dirSons = new File(rootAssetsDir, "Sons");
        if (!dirImages.exists()) dirImages.mkdirs();
        if (!dirSons.exists()) dirSons.mkdirs();
        
        currentFolderSelected = dirImages;

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
                txtObjet.setPadding(10, 10, 10, 10);
                txtObjet.setTextSize(14f);
                
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
            txtVide.setText("Aucun objet dans la scène");
            txtVide.setTextColor(Palette.texteNormal);
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

        ImageButton btnAjouterCarre = new ImageButton(context);
        btnAjouterCarre.setImageResource(R.drawable.square_24px);
        btnAjouterCarre.setColorFilter(Palette.texteNormal);
        btnAjouterCarre.setBackgroundColor(Color.TRANSPARENT);
        btnAjouterCarre.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique("Carré", editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 150f, 150f, 80f, 80f);
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
            Toast.makeText(context, nomUnique + " ajouté à la scène", Toast.LENGTH_SHORT).show();
        });

        ImageButton btnAjouterTexte = new ImageButton(context);
        btnAjouterTexte.setImageResource(R.drawable.title_24px);
        btnAjouterTexte.setColorFilter(Palette.texteNormal);
        btnAjouterTexte.setBackgroundColor(Color.TRANSPARENT);
        btnAjouterTexte.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) getContext();
            String nomUnique = genererNomUnique("Texte", editeur.sceneActive);
            ObjetBase nouveau = new ObjetBase(nomUnique, 200f, 100f, 120f, 40f);
            editeur.sceneActive.ajouterObjet(nouveau);
            canvasEditeur.invalidate();
            rafraichirArborescence();
            Toast.makeText(context, nomUnique + " ajouté à la scène", Toast.LENGTH_SHORT).show();
        });

        ImageButton btnAjouterRond = new ImageButton(context);
        btnAjouterRond.setImageResource(R.drawable.circle_24px);
        btnAjouterRond.setColorFilter(Palette.texteNormal);
        btnAjouterRond.setBackgroundColor(Color.TRANSPARENT);
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

        ImageButton btnCreer = new ImageButton(context);
        btnCreer.setImageResource(R.drawable.add_24px);
        btnCreer.setColorFilter(Palette.texteNormal);
        btnCreer.setBackgroundColor(Color.TRANSPARENT);
        btnCreer.setOnClickListener(v -> afficherPopupCreerScene(context));

        ImageButton btnRenommer = new ImageButton(context);
        btnRenommer.setImageResource(R.drawable.edit_square_24px);
        btnRenommer.setColorFilter(Palette.texteNormal);
        btnRenommer.setBackgroundColor(Color.TRANSPARENT);
        btnRenommer.setOnClickListener(v -> {
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            afficherPopupRenommerScene(context, editeur.sceneActive);
        });

        ImageButton btnSupprimer = new ImageButton(context);
        btnSupprimer.setImageResource(R.drawable.delete_24px);
        btnSupprimer.setColorFilter(Palette.texteNormal);
        btnSupprimer.setBackgroundColor(Color.TRANSPARENT);
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
                    nomScene.setTextColor(Palette.texteSelectionne);
                } else {
                    nomScene.setTextColor(Palette.texteNormal);
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

    private boolean isRacineIndestructible(File dir) {
        if (dir == null) return false;
        String nom = dir.getName();
        return (dir.getParentFile() != null && dir.getParentFile().equals(rootAssetsDir)) &&
               (nom.equals("Images") || nom.equals("Sons"));
    }

    private View creerSectionAssets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Assets ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        conteneurArborescenceDossiers = new LinearLayout(context);
        conteneurArborescenceDossiers.setOrientation(LinearLayout.VERTICAL);

        LinearLayout boutonsDossiers = new LinearLayout(context);
        boutonsDossiers.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageButton btnAddFolder = new ImageButton(context);
        btnAddFolder.setImageResource(R.drawable.add_24px);
        btnAddFolder.setColorFilter(Palette.texteNormal);
        btnAddFolder.setBackgroundColor(Color.TRANSPARENT);
        
        ImageButton btnEditFolder = new ImageButton(context);
        btnEditFolder.setImageResource(R.drawable.edit_square_24px);
        btnEditFolder.setColorFilter(Palette.texteNormal);
        btnEditFolder.setBackgroundColor(Color.TRANSPARENT);
        
        ImageButton btnDelFolder = new ImageButton(context);
        btnDelFolder.setImageResource(R.drawable.delete_24px);
        btnDelFolder.setColorFilter(Palette.texteNormal);
        btnDelFolder.setBackgroundColor(Color.TRANSPARENT);

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
        conteneurListeAssets.setPadding(0, 20, 0, 0);

        LinearLayout boutonsAssets = new LinearLayout(context);
        boutonsAssets.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnImportAsset = new ImageButton(context);
        btnImportAsset.setImageResource(R.drawable.upload_file_24px);
        btnImportAsset.setColorFilter(Palette.texteNormal);
        btnImportAsset.setBackgroundColor(Color.TRANSPARENT);
        
        ImageButton btnEditAsset = new ImageButton(context);
        btnEditAsset.setImageResource(R.drawable.edit_square_24px);
        btnEditAsset.setColorFilter(Palette.texteNormal);
        btnEditAsset.setBackgroundColor(Color.TRANSPARENT);
        
        ImageButton btnDelAsset = new ImageButton(context);
        btnDelAsset.setImageResource(R.drawable.delete_24px);
        btnDelAsset.setColorFilter(Palette.texteNormal);
        btnDelAsset.setBackgroundColor(Color.TRANSPARENT);

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

        contenu.addView(conteneurArborescenceDossiers);
        contenu.addView(boutonsDossiers);
        contenu.addView(conteneurListeAssets);
        contenu.addView(boutonsAssets);

        rafraichirSectionAssetsTotale();

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText("Assets ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText("Assets ▼");
                rafraichirSectionAssetsTotale();
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }
// bas 2
// haut 3
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
            
            if (depth > 0) {
                TextView tvPrefix = new TextView(getContext());
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < depth; i++) prefix.append("   ");
                tvPrefix.setText(prefix.toString());
                layoutDossier.addView(tvPrefix);
            }
            
            ImageView iconeDossier = new ImageView(getContext());
            iconeDossier.setImageResource(R.drawable.folder_open_24px);
            iconeDossier.setColorFilter(Palette.texteNormal);
            iconeDossier.setPadding(0, 0, 15, 0);
            
            TextView tv = new TextView(getContext());
            tv.setText(dir.getName());
            tv.setTextColor(dir.equals(currentFolderSelected) ? Palette.texteSelectionne : Palette.texteNormal);
            tv.setPadding(0, 10, 0, 10);
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
        itemLayout.setPadding(0, 10, 0, 10);
        itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        boolean isImage = f.getAbsolutePath().contains("/Images/");
        
        if (isImage) {
            ImageView miniature = new ImageView(context);
            miniature.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            try {
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                miniature.setImageBitmap(bmp);
            } catch (Exception e) {}
            miniature.setScaleType(ImageView.ScaleType.CENTER_CROP);
            miniature.setPadding(0, 0, 15, 0);
            itemLayout.addView(miniature);
        }
        
        TextView nom = new TextView(context);
        nom.setText(f.getName());
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
// bas 3

// haut 4
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

        ImageButton btnCreer = new ImageButton(context);
        btnCreer.setImageResource(R.drawable.add_24px);
        btnCreer.setColorFilter(Palette.texteNormal);
        btnCreer.setBackgroundColor(Color.TRANSPARENT);
        btnCreer.setOnClickListener(v -> afficherPopupCreerVariable(context));

        ImageButton btnRenommer = new ImageButton(context);
        btnRenommer.setImageResource(R.drawable.edit_square_24px);
        btnRenommer.setColorFilter(Palette.texteNormal);
        btnRenommer.setBackgroundColor(Color.TRANSPARENT);
        btnRenommer.setOnClickListener(v -> {
            if (variableSelectionnee != null) afficherPopupRenommerVariable(context, variableSelectionnee);
        });

        ImageButton btnSupprimer = new ImageButton(context);
        btnSupprimer.setImageResource(R.drawable.delete_24px);
        btnSupprimer.setColorFilter(Palette.texteNormal);
        btnSupprimer.setBackgroundColor(Color.TRANSPARENT);
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
        conteneurLigne.setPadding(0, 5, 0, 15);
        
        TextView nomVariable = new TextView(context);
        
        String labelType;
        switch (var.type) {
            case "BOOLEEN": labelType = "Oui/Non"; break;
            case "CHIFFRE": labelType = "Chiffre"; break;
            case "ENTIER": labelType = "Entier"; break;
            case "LISTE_INVENTAIRE": labelType = "Liste d'Inventaire"; break;
            default: labelType = "Texte"; break;
        }
        
        String labelScope = var.scope.equals("GLOBALE") ? "Globale" : "Locale";
        
        String texteValeur;
        if (var.type.equals("LISTE_INVENTAIRE") && var.valeur instanceof java.util.List) {
            texteValeur = ((java.util.List<?>) var.valeur).size() + " objet(s)";
        } else {
            texteValeur = String.valueOf(var.valeur);
        }
        
        nomVariable.setText(var.nom + " [" + labelScope + ", " + labelType + "] = " + texteValeur);
        
        if (var == variableSelectionnee) {
            nomVariable.setTextColor(Palette.texteSelectionne); 
        } else {
            nomVariable.setTextColor(var.scope.equals("GLOBALE") ? Color.parseColor("#ADD8E6") : Color.parseColor("#90EE90"));
        }
        
        nomVariable.setPadding(10, 5, 10, 5);
        nomVariable.setTextSize(16f);

        nomVariable.setOnClickListener(v -> {
            variableSelectionnee = var;
            rafraichirVariables();
        });

        conteneurLigne.addView(nomVariable);
        conteneurVariables.addView(conteneurLigne);
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

        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
        btnValider.setOnClickListener(v -> {
            String nom = champTexte.getText().toString().trim();
            if(!nom.isEmpty()) {
                
                // ANTI-DOUBLON POUR LA CRÉATION DE SCÈNE
                InterfaceEditeur editeur = (InterfaceEditeur) context;
                if (editeur.listeScenes != null) {
                    for (Scene s : editeur.listeScenes) {
                        if (s.nom != null && s.nom.trim().equalsIgnoreCase(nom)) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Impossible")
                                    .setMessage("Une scène avec ce nom existe déjà dans le projet.")
                                    .setPositiveButton("OK", null)
                                    .show();
                            return; // Bloque la création
                        }
                    }
                }

                editeur.creerScene(nom);
                Toast.makeText(context, "Scène créée : " + nom, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
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

        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
        btnValider.setOnClickListener(v -> {
            String nouveauNom = champTexte.getText().toString().trim();
            if(nouveauNom.isEmpty()) {
                Toast.makeText(context, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            // ANTI-DOUBLON STRICT POUR LE RENOMMAGE DE SCÈNE
            InterfaceEditeur editeur = (InterfaceEditeur) context;
            if (editeur.listeScenes != null) {
                for (Scene s : editeur.listeScenes) {
                    if (s != scene && s.nom != null && s.nom.trim().equalsIgnoreCase(nouveauNom)) {
                        new AlertDialog.Builder(context)
                                .setTitle("Impossible")
                                .setMessage("Une scène avec ce nom existe déjà dans le projet.")
                                .setPositiveButton("OK", null)
                                .show();
                        return; // Bloque le renommage, laisse le dialogue ouvert
                    }
                }
            }

            scene.nom = nouveauNom;
            rafraichirScenes();
            Toast.makeText(context, "Scène renommée", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
// bas 4
// haut 5
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

        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        btnOui.setColorFilter(Palette.texteNormal);
        btnOui.setBackgroundColor(Color.TRANSPARENT);
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

        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        btnNon.setColorFilter(Palette.texteNormal);
        btnNon.setBackgroundColor(Color.TRANSPARENT);
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

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
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new String[]{"Chiffre", "Entier", "Texte", "Oui/Non", "Liste d'Inventaire"});
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        layoutDialog.addView(spinnerType);

        EditText champValeurInit = new EditText(context);
        champValeurInit.setHint("Valeur initiale (optionnel)");
        layoutDialog.addView(champValeurInit);
        
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerType.getSelectedItem().toString().equals("Liste d'Inventaire")) {
                    champValeurInit.setVisibility(View.GONE);
                } else {
                    champValeurInit.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
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
            if (typeSelectText.equals("Entier")) typeSelect = "ENTIER";
            if (typeSelectText.equals("Liste d'Inventaire")) typeSelect = "LISTE_INVENTAIRE";

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
                } else if (typeSelect.equals("ENTIER")) {
                    try {
                        nouvelleVar.valeur = Integer.parseInt(valInitTexte);
                    } catch (NumberFormatException e) {
                        nouvelleVar.valeur = 0;
                    }
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

        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
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

        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
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

        ImageButton btnAnnuler = new ImageButton(context);
        btnAnnuler.setImageResource(R.drawable.undo_24px);
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
// bas 5

// haut 6
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

        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        btnOui.setColorFilter(Palette.texteNormal);
        btnOui.setBackgroundColor(Color.TRANSPARENT);
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

        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        btnNon.setColorFilter(Palette.texteNormal);
        btnNon.setBackgroundColor(Color.TRANSPARENT);
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);

        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupNouveauDossier(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Nouveau dossier");
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setHint("Nom du dossier");
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
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
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerDossier(Context context, File dir) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Renommer dossier");
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setText(dir.getName());
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
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
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerDossier(Context context, File dir) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Confirmer");
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        TextView txtMessage = new TextView(context);
        txtMessage.setText("Supprimer le dossier (et tout son contenu) ?");
        layoutDialog.addView(txtMessage);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        btnOui.setColorFilter(Palette.texteNormal);
        btnOui.setBackgroundColor(Color.TRANSPARENT);
        btnOui.setOnClickListener(v -> {
            supprimerRecursif(dir);
            currentFolderSelected = new File(rootAssetsDir, "Images");
            currentAssetSelected = null;
            rafraichirSectionAssetsTotale();
            dialog.dismiss();
        });
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        btnNon.setColorFilter(Palette.texteNormal);
        btnNon.setBackgroundColor(Color.TRANSPARENT);
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupRenommerAsset(Context context, File f) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Renommer");
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        EditText champTexte = new EditText(context);
        champTexte.setText(f.getName());
        layoutDialog.addView(champTexte);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnValider = new ImageButton(context);
        btnValider.setImageResource(R.drawable.save_24px);
        btnValider.setColorFilter(Palette.texteNormal);
        btnValider.setBackgroundColor(Color.TRANSPARENT);
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
        btnAnnuler.setColorFilter(Palette.texteNormal);
        btnAnnuler.setBackgroundColor(Color.TRANSPARENT);
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnValider);
        zoneBoutons.addView(btnAnnuler);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }

    private void afficherPopupSupprimerAsset(Context context, File f) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Confirmer");
        LinearLayout layoutDialog = new LinearLayout(context);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(40, 40, 40, 40);

        TextView txtMessage = new TextView(context);
        txtMessage.setText("Supprimer cet asset ?");
        layoutDialog.addView(txtMessage);

        LinearLayout zoneBoutons = new LinearLayout(context);
        zoneBoutons.setOrientation(LinearLayout.HORIZONTAL);
        ImageButton btnOui = new ImageButton(context);
        btnOui.setImageResource(R.drawable.save_24px);
        btnOui.setColorFilter(Palette.texteNormal);
        btnOui.setBackgroundColor(Color.TRANSPARENT);
        btnOui.setOnClickListener(v -> {
            f.delete();
            currentAssetSelected = null;
            rafraichirSectionAssetsTotale();
            dialog.dismiss();
        });
        ImageButton btnNon = new ImageButton(context);
        btnNon.setImageResource(R.drawable.undo_24px);
        btnNon.setColorFilter(Palette.texteNormal);
        btnNon.setBackgroundColor(Color.TRANSPARENT);
        btnNon.setOnClickListener(v -> dialog.dismiss());

        zoneBoutons.addView(btnOui);
        zoneBoutons.addView(btnNon);
        layoutDialog.addView(zoneBoutons);
        dialog.setContentView(layoutDialog);
        dialog.show();
    }
}
// bas 6







    



    





    


    


    




