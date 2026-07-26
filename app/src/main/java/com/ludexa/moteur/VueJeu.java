// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        // La taille n'est plus figée ici, elle sera définie dynamiquement dans onDraw
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
            // 1. Tri par zOrder (sur une copie de la liste)
            List<ObjetBase> objetsTries = new ArrayList<>(sceneActive.objets);
            Collections.sort(objetsTries, new Comparator<ObjetBase>() {
                @Override
                public int compare(ObjetBase o1, ObjetBase o2) {
                    return Integer.compare(o1.zOrder, o2.zOrder);
                }
            });

            for (ObjetBase objet : objetsTries) {
                // 2. Filtre visible
                if (!objet.visible) {
                    continue; // Ignorer cet objet
                }

                // 3. Application des propriétés dynamiques (Couleur et Taille)
                peintureObjet.setColor(objet.couleur);
                peintureTexte.setColor(objet.couleur);
                
                if ("texte".equals(objet.type)) {
                    // On attribue la hauteur de l'objet comme taille de la police
                    peintureTexte.setTextSize(objet.hauteur > 0 ? objet.hauteur : 40f); 
                }

                float left = objet.x;
                float top = objet.y;
                float right = left + objet.largeur;
                float bottom = top + objet.hauteur;

                float cx = left + objet.largeur / 2f;
                float cy = top + objet.hauteur / 2f;

                // 4. Rotation
                canvas.save();
                canvas.translate(cx, cy);
                canvas.rotate(objet.rotation);

                // Coordonnées relatives au centre pour le dessin
                float relLeft = -objet.largeur / 2f;
                float relTop = -objet.hauteur / 2f;
                float relRight = objet.largeur / 2f;
                float relBottom = objet.hauteur / 2f;

                // Dessin selon le type (avec coordonnées relatives)
                if ("rond".equals(objet.type)) {
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    canvas.drawCircle(0, 0, rayon, peintureObjet);
                } else if ("texte".equals(objet.type)) {
                    // Rétrocompatibilité : affichage du nouveau champ, sinon fallback sur nom
                    String texteAAfficher = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                    canvas.drawText(texteAAfficher, relLeft, relBottom, peintureTexte);
                } else {
                    canvas.drawRect(relLeft, relTop, relRight, relBottom, peintureObjet);
                }

                // Fin de la zone sous rotation
                canvas.restore();

                // 5. Texte de debug affiché juste au-dessus de l'objet, HORS du bloc de rotation
                canvas.drawText(
                        objet.nom + " (" + (int) objet.x + ", " + (int) objet.y + ")",
                        left, top - 10f, peintureDebug
                );
            }
        }
    }
}
// bas 1
