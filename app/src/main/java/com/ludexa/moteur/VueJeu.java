package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class VueJeu extends View {

    private Scene sceneActive;
    private Paint peintureObjet;
    private Paint peintureTexte;
    private Paint peintureDebug;
    private MoteurLogique moteur;

    public VueJeu(Context context, Scene scene, Blueprint blueprintActif) {
        super(context);
        this.sceneActive = scene;

        peintureObjet = new Paint();
        peintureObjet.setColor(Color.BLUE);
        peintureObjet.setAntiAlias(true);

        peintureTexte = new Paint();
        peintureTexte.setColor(Color.BLUE);
        peintureTexte.setTextSize(40f);
        peintureTexte.setAntiAlias(true);

        peintureDebug = new Paint();
        peintureDebug.setColor(Color.BLACK);
        peintureDebug.setTextSize(24f);
        peintureDebug.setAntiAlias(true);

        if (blueprintActif != null) {
            this.moteur = new MoteurLogique(blueprintActif);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.moteur != null) {
            this.moteur.executerDemarrage();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        if (sceneActive != null && sceneActive.objets != null) {
            for (ObjetBase objet : sceneActive.objets) {
                float left = objet.x;
                float top = objet.y;
                float right = left + objet.largeur;
                float bottom = top + objet.hauteur;

                // Dessin selon le type
                if ("rond".equals(objet.type)) {
                    float cx = left + objet.largeur / 2f;
                    float cy = top + objet.hauteur / 2f;
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    canvas.drawCircle(cx, cy, rayon, peintureObjet);
                } else if ("texte".equals(objet.type)) {
                    canvas.drawText(objet.nom, left, bottom, peintureTexte);
                } else {
                    canvas.drawRect(left, top, right, bottom, peintureObjet);
                }

                // Texte de debug affiché juste au-dessus de chaque objet
                canvas.drawText(
                        objet.nom + " (" + (int) objet.x + ", " + (int) objet.y + ")",
                        left, top - 10f, peintureDebug
                );
            }
        }
    }
}
