package com.ludexa.moteur;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PanneauRessources extends ScrollView {

    public PanneauRessources(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Définition de l'apparence du panneau gauche
        setBackgroundColor(Color.parseColor("#333333"));
        setLayoutParams(new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layoutPrincipal = new LinearLayout(context);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);

        // Ajout des sections de l'accordéon avec leurs logiques spécifiques
        layoutPrincipal.addView(creerSectionScenes(context));
        layoutPrincipal.addView(creerSectionObjets(context)); // Nouvelle vraie section Objets
        layoutPrincipal.addView(creerSectionGenerique(context, "Arborescence", "Gestion de l'ordre Z"));
        layoutPrincipal.addView(creerSectionAssets(context));
        layoutPrincipal.addView(creerSectionVariables(context));

        addView(layoutPrincipal);
    }

    // Méthode pour construire les sections génériques restantes (ex: Arborescence)
    private View creerSectionGenerique(Context context, String titre, String contenuApercu) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText(titre + " ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        TextView txt = new TextView(context);
        txt.setText("[ " + contenuApercu + " ]");
        txt.setTextColor(Color.LTGRAY);
        contenu.addView(txt);

        btnTitre.setOnClickListener(v -> {
            if (contenu.getVisibility() == View.VISIBLE) {
                contenu.setVisibility(View.GONE);
                btnTitre.setText(titre + " ▶");
            } else {
                contenu.setVisibility(View.VISIBLE);
                btnTitre.setText(titre + " ▼");
            }
        });

        section.addView(btnTitre);
        section.addView(contenu);
        return section;
    }

    // --- NOUVELLE SECTION : OBJETS À PLACER ---
    private View creerSectionObjets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Objets à placer ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        // Boutons spécifiques pour ajouter des éléments visuels
        Button btnAjouterTexte = new Button(context);
        btnAjouterTexte.setText("+ Ajouter un Texte");
        btnAjouterTexte.setOnClickListener(v -> Toast.makeText(context, "Texte ajouté au Canvas", Toast.LENGTH_SHORT).show());

        Button btnAjouterCarre = new Button(context);
        btnAjouterCarre.setText("+ Ajouter un Carré");
        btnAjouterCarre.setOnClickListener(v -> Toast.makeText(context, "Carré ajouté au Canvas", Toast.LENGTH_SHORT).show());

        Button btnAjouterRond = new Button(context);
        btnAjouterRond.setText("+ Ajouter un Rond");
        btnAjouterRond.setOnClickListener(v -> Toast.makeText(context, "Rond ajouté au Canvas", Toast.LENGTH_SHORT).show());

        contenu.addView(btnAjouterTexte);
        contenu.addView(btnAjouterCarre);
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

    // Méthode spécifique pour la section Scènes
    private View creerSectionScenes(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Scènes ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        Button btnCreer = new Button(context);
        btnCreer.setText("Créer une scène");
        btnCreer.setOnClickListener(v -> afficherPopupCreer(context, "une nouvelle scène"));
        contenu.addView(btnCreer);

        LinearLayout itemScene = new LinearLayout(context);
        itemScene.setOrientation(LinearLayout.HORIZONTAL);
        itemScene.setPadding(0, 10, 0, 10);
        
        TextView nomScene = new TextView(context);
        nomScene.setText("Niveau_1");
        nomScene.setTextColor(Color.WHITE);
        nomScene.setPadding(10, 10, 20, 10);
        
        Button btnRenommer = new Button(context);
        btnRenommer.setText("Renommer");
        btnRenommer.setOnClickListener(v -> afficherPopupRenommer(context, "Niveau_1"));

        Button btnSupprimer = new Button(context);
        btnSupprimer.setText("Supprimer");
        btnSupprimer.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer la scène 'Niveau_1' ?"));

        itemScene.addView(nomScene);
        itemScene.addView(btnRenommer);
        itemScene.addView(btnSupprimer);
        
        contenu.addView(itemScene);

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

    // Méthode spécifique pour la section Assets
    private View creerSectionAssets(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Assets ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        LinearLayout itemAsset = new LinearLayout(context);
        itemAsset.setOrientation(LinearLayout.HORIZONTAL);
        itemAsset.setPadding(0, 10, 0, 10);
        
        TextView nomAsset = new TextView(context);
        nomAsset.setText("Sprite_Joueur");
        nomAsset.setTextColor(Color.WHITE);
        nomAsset.setPadding(10, 10, 20, 10);

        Button btnRenommer = new Button(context);
        btnRenommer.setText("Renommer");
        btnRenommer.setOnClickListener(v -> afficherPopupRenommer(context, "Sprite_Joueur"));

        Button btnSupprimer = new Button(context);
        btnSupprimer.setText("Supprimer");
        btnSupprimer.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer l'asset 'Sprite_Joueur' ?"));

        itemAsset.addView(nomAsset);
        itemAsset.addView(btnRenommer);
        itemAsset.addView(btnSupprimer);
        
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

    // Méthode spécifique pour la section Variables
    private View creerSectionVariables(Context context) {
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        Button btnTitre = new Button(context);
        btnTitre.setText("Variables ▼");

        LinearLayout contenu = new LinearLayout(context);
        contenu.setOrientation(LinearLayout.VERTICAL);
        contenu.setPadding(20, 10, 10, 20);

        Button btnCreer = new Button(context);
        btnCreer.setText("Créer une variable");
        btnCreer.setOnClickListener(v -> afficherPopupCreer(context, "une variable"));
        contenu.addView(btnCreer);

        LinearLayout itemVariable = new LinearLayout(context);
        itemVariable.setOrientation(LinearLayout.HORIZONTAL);
        itemVariable.setPadding(0, 10, 0, 10);
        
        TextView nomVariable = new TextView(context);
        nomVariable.setText("scoreJoueur");
        nomVariable.setTextColor(Color.WHITE);
        nomVariable.setPadding(10, 10, 20, 10);
        
        Button btnSupprimerVar = new Button(context);
        btnSupprimerVar.setText("Supprimer");
        btnSupprimerVar.setOnClickListener(v -> afficherPopupConfirmation(context, "Supprimer la variable 'scoreJoueur' ?"));
        
        itemVariable.addView(nomVariable);
        itemVariable.addView(btnSupprimerVar);
        contenu.addView(itemVariable);

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

    // Fenêtre modale générique pour la création (Variables, Scènes...)
    private void afficherPopupCreer(Context context, String type) {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Créer " + type);

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
            Toast.makeText(context, "Création : " + nom, Toast.LENGTH_SHORT).show();
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

    // Fenêtre modale pour renommer
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

    // Fenêtre modale pour confirmer la suppression
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
