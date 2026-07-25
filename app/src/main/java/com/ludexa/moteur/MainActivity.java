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
        
        // Initialisation du contexte pour permettre l'affichage des erreurs (Toasts) 
        // depuis les classes de logique abstraites comme NoeudBase.
        com.ludexa.moteur.NoeudBase.contexteApplication = this;

        // Création d'une scène vide sans faux objet de test pour éviter tout code fantôme
        Scene sceneActive = new Scene("SceneTest");
        
        // Blueprint vide pour la compilation
        Blueprint blueprint = new Blueprint();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // NOUVEAU : On passe la Scene vide au constructeur de VueJeu pour corriger l'erreur de build
        vueJeu = new VueJeu(this, sceneActive, blueprint);
        LinearLayout.LayoutParams paramsVue = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        layout.addView(vueJeu, paramsVue);

        Button boutonPlay = new Button(this);
        boutonPlay.setText("Redessiner (Test)");
        boutonPlay.setOnClickListener(v -> {
            // L'exécution du Blueprint n'est plus codée en dur ici.
            // Ce bouton sert uniquement à forcer le rafraîchissement visuel si besoin.
            vueJeu.invalidate();
        });
        layout.addView(boutonPlay);

        setContentView(layout);
    }
}
// bas 1
