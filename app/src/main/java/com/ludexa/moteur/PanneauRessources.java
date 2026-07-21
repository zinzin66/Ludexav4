package com.ludexa.moteur;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;

public class PanneauRessources extends LinearLayout {

    private boolean estOuvert;
    private LinearLayout.LayoutParams paramsOuvert;
    private LinearLayout.LayoutParams paramsFerme;

    private LinearLayout sectionScenes;
    private ListView listeScenes;
    private ArrayAdapter<String> adapterScenes;
    private java.util.ArrayList<String> nomsScenes;

    public PanneauRessources(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);

        estOuvert = true;
        paramsOuvert = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsFerme = new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(paramsOuvert);

        construireBoutonBascule();
        construireSectionScenes();
    }

    private void construireBoutonBascule() {
        Button boutonBascule = new Button(getContext());
        boutonBascule.setText("<<");
        boutonBascule.setOnClickListener(v -> {
            estOuvert = !estOuvert;
            this.setLayoutParams(estOuvert ? paramsOuvert : paramsFerme);
            sectionScenes.setVisibility(estOuvert ? VISIBLE : GONE);
            boutonBascule.setText(estOuvert ? "<<" : ">>");
        });
        this.addView(boutonBascule);
    }

    private void construireSectionScenes() {
        sectionScenes = new LinearLayout(getContext());
        sectionScenes.setOrientation(LinearLayout.VERTICAL);

        TextView titre = new TextView(getContext());
        titre.setText("Scènes");
        titre.setTextSize(18f);
        titre.setPadding(10, 10, 10, 10);
        sectionScenes.addView(titre);

        // Boutons Ajouter / Renommer / Supprimer
        LinearLayout ligneBoutons = new LinearLayout(getContext());
        ligneBoutons.setOrientation(LinearLayout.HORIZONTAL);

        Button boutonAjouter = new Button(getContext());
        boutonAjouter.setText("+");
        boutonAjouter.setOnClickListener(v -> demanderNomScene());
        ligneBoutons.addView(boutonAjouter);

        Button boutonRenommer = new Button(getContext());
        boutonRenommer.setText("Renommer");
        boutonRenommer.setOnClickListener(v -> {
            int position = listeScenes.getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                renommerScene(position);
            } else {
                Toast.makeText(getContext(), "Sélectionne une scène d'abord", Toast.LENGTH_SHORT).show();
            }
        });
        ligneBoutons.addView(boutonRenommer);

        Button boutonSupprimer = new Button(getContext());
        boutonSupprimer.setText("Supprimer");
        boutonSupprimer.setOnClickListener(v -> {
            int position = listeScenes.getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                confirmerSuppressionScene(position);
            } else {
                Toast.makeText(getContext(), "Sélectionne une scène d'abord", Toast.LENGTH_SHORT).show();
            }
        });
        ligneBoutons.addView(boutonSupprimer);

        sectionScenes.addView(ligneBoutons);

        // Liste des scènes
        nomsScenes = new java.util.ArrayList<>();
        adapterScenes = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, nomsScenes);
        listeScenes = new ListView(getContext());
        listeScenes.setAdapter(adapterScenes);
        listeScenes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        LinearLayout.LayoutParams paramsListe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        listeScenes.setLayoutParams(paramsListe);
        sectionScenes.addView(listeScenes);

        this.addView(sectionScenes);
    }

    private void demanderNomScene() {
        EditText champNom = new EditText(getContext());
        champNom.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(getContext())
                .setTitle("Nom de la scène")
                .setView(champNom)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String nom = champNom.getText().toString().trim();
                    if (!nom.isEmpty()) {
                        nomsScenes.add(nom);
                        adapterScenes.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Scène ajoutée : " + nom, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void renommerScene(int position) {
        EditText champNom = new EditText(getContext());
        champNom.setText(nomsScenes.get(position));

        new AlertDialog.Builder(getContext())
                .setTitle("Renommer la scène")
                .setView(champNom)
                .setPositiveButton("Valider", (dialog, which) -> {
                    String nouveauNom = champNom.getText().toString().trim();
                    if (!nouveauNom.isEmpty()) {
                        nomsScenes.set(position, nouveauNom);
                        adapterScenes.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Scène renommée : " + nouveauNom, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void confirmerSuppressionScene(int position) {
        String nom = nomsScenes.get(position);
        new AlertDialog.Builder(getContext())
                .setTitle("Supprimer la scène")
                .setMessage("Supprimer \"" + nom + "\" ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    nomsScenes.remove(position);
                    adapterScenes.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Scène supprimée : " + nom, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
                          }
