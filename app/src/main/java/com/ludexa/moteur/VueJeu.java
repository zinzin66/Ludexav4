package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class VueJeu extends View {

    private ObjetBase objetAffiche;
    private Paint peintureCarre;
    private Paint peintureTexte;

    public VueJeu(Context context, ObjetBase objet) {
        super(context);
        this.objetAffiche = objet;

        peintureCarre = new Paint();
        peintureCarre.setColor(Color.BLUE);

        peintureTexte = new Paint();
        peintureTexte.setColor(Color.BLACK);
        peintureTexte.setTextSize(32f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        canvas.drawRect(
                objetAffiche.x,
                objetAffiche.y,
                objetAffiche.x + objetAffiche.largeur,
                objetAffiche.y + objetAffiche.hauteur,
                peintureCarre
        );

        canvas.drawText(
                objetAffiche.nom + " (" + (int) objetAffiche.x + ", " + (int) objetAffiche.y + ")",
                20f, 60f, peintureTexte
        );
    }
}
