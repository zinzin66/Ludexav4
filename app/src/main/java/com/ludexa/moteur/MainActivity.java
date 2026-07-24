// haut 1
package com.ludexa.moteur;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private VueJeu vueJeu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // NOUVEAU : Initialisation du contexte pour permettre l'affichage des erreurs (Toasts) 
        // depuis les classes de logique abstraites comme NoeudBase.
        com.ludexa.moteur.NoeudBase.contexteApplication = this;

        Scene sceneActive = new Scene("SceneDepart");
        ObjetBase carre = new ObjetBase("Carré", 50f, 50f, 120f, 120f);
        sceneActive.ajouterObjet(carre);

        NoeudEventStart noeudStart = new NoeudEventStart();
        NoeudActionDeplacer noeudDeplacer = new NoeudActionDeplacer(carre, 40f, 30f);

        noeudStart.connecterPort("Suivant", noeudDeplacer, "Entrer");

        sceneActive.ajouterNoeud(noeudStart);
        sceneActive.ajouterNoeud(noeudDeplacer);

        // NOUVEAU : Création du Blueprint réel et ajout des nœuds
        Blueprint blueprint = new Blueprint();
        blueprint.ajouterNoeud(noeudStart, 0, 0);
        blueprint.ajouterNoeud(noeudDeplacer, 200, 0);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // NOUVEAU : On passe le Blueprint au constructeur de VueJeu
        vueJeu = new VueJeu(this, carre, blueprint);
        LinearLayout.LayoutParams paramsVue = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        layout.addView(vueJeu, paramsVue);

        Button boutonPlay = new Button(this);
        boutonPlay.setText("Redessiner (Test)");
        boutonPlay.setOnClickListener(v -> {
            // L'exécution du Blueprint n'est plus codée en dur ici, 
            // elle est gérée par MoteurLogique à l'affichage de la vue.
            // Ce bouton sert uniquement à forcer le rafraîchissement visuel si besoin.
            vueJeu.invalidate();
        });
        layout.addView(boutonPlay);

        setContentView(layout);
    }
}
// bas 1
