package com.ludexa.moteur;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private VueJeu vueJeu;
    private NoeudEventStart noeudStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scene sceneActive = new Scene("SceneDepart");
        ObjetBase carre = new ObjetBase("Carré", 50f, 50f, 120f, 120f);
        sceneActive.ajouterObjet(carre);

        noeudStart = new NoeudEventStart();
        NoeudActionDeplacer noeudDeplacer = new NoeudActionDeplacer(carre, 40f, 30f);

        noeudStart.connecterPort("Suivant", noeudDeplacer, "Entrer");

        sceneActive.ajouterNoeud(noeudStart);
        sceneActive.ajouterNoeud(noeudDeplacer);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        vueJeu = new VueJeu(this, carre);
        LinearLayout.LayoutParams paramsVue = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        layout.addView(vueJeu, paramsVue);

        Button boutonPlay = new Button(this);
        boutonPlay.setText("Play");
        boutonPlay.setOnClickListener(v -> {
            noeudStart.executer();
            vueJeu.invalidate();
        });
        layout.addView(boutonPlay);

        setContentView(layout);
    }
}
