// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    
    private java.util.Map<String, android.graphics.Bitmap> cacheImages = new java.util.HashMap<>();

    public VueJeu(Context context, Scene scene, Blueprint blueprintActif) {
        super(context);
        this.sceneActive = scene;

        peintureObjet = new Paint();
        peintureObjet.setColor(Color.BLUE);
        peintureObjet.setAntiAlias(true);

        peintureTexte = new Paint();
        peintureTexte.setColor(Color.BLUE);
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

    private ObjetBase getObjetById(String id) {
        if (sceneActive == null || id == null) return null;
        for (ObjetBase o : sceneActive.objets) {
            if (o.id.equals(id)) return o;
        }
        return null;
    }

    private Matrix getAbsoluteMatrix(ObjetBase obj) {
        Matrix m = new Matrix();
        List<ObjetBase> chaine = new ArrayList<>();
        ObjetBase cur = obj;
        while (cur != null) {
            chaine.add(cur);
            cur = getObjetById(cur.parentId);
        }
        
        for (int i = chaine.size() - 1; i >= 0; i--) {
            ObjetBase o = chaine.get(i);
            Matrix local = new Matrix();
            local.postTranslate(-o.largeur / 2f, -o.hauteur / 2f);
            local.postScale(o.scaleX, o.scaleY);
            local.postRotate(o.rotation);
            local.postTranslate(o.x + o.largeur / 2f, o.y + o.hauteur / 2f);
            m.preConcat(local); 
        }
        return m;
    }

    private void dessinerImage(Canvas canvas, ObjetBase objet) {
        if (objet.cheminImage != null) {
            android.graphics.Bitmap bmp = cacheImages.get(objet.cheminImage);
            if (bmp == null) {
                try {
                    // Lecture avec le même correctif que le CanvasEditeur
                    java.io.File imgFile = new java.io.File(getContext().getFilesDir(), objet.cheminImage);
                    if (imgFile.exists()) {
                        bmp = android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (bmp != null) {
                            cacheImages.put(objet.cheminImage, bmp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bmp != null) {
                if ("rond".equals(objet.type)) {
                    canvas.save();
                    android.graphics.Path path = new android.graphics.Path();
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    path.addCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, android.graphics.Path.Direction.CW);
                    canvas.clipPath(path);
                    canvas.drawBitmap(bmp, null, new android.graphics.RectF(0, 0, objet.largeur, objet.hauteur), peintureObjet);
                    canvas.restore();
                } else {
                    canvas.drawBitmap(bmp, null, new android.graphics.RectF(0, 0, objet.largeur, objet.hauteur), peintureObjet);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        if (sceneActive != null && sceneActive.objets != null) {
            List<ObjetBase> objetsTries = new ArrayList<>(sceneActive.objets);
            Collections.sort(objetsTries, new Comparator<ObjetBase>() {
                @Override
                public int compare(ObjetBase o1, ObjetBase o2) {
                    return Integer.compare(o1.zOrder, o2.zOrder);
                }
            });

            for (ObjetBase objet : objetsTries) {
                if (!objet.visible) {
                    continue; 
                }

                peintureObjet.setColor(objet.couleur);
                peintureTexte.setColor(objet.couleur);
                
                if ("texte".equals(objet.type)) {
                    peintureTexte.setTextSize(objet.hauteur > 0 ? objet.hauteur : 40f); 
                }

                Matrix absMatrix = getAbsoluteMatrix(objet);

                canvas.save();
                canvas.concat(absMatrix);

                if ("rond".equals(objet.type)) {
                    if (objet.afficherFondColore || objet.cheminImage == null) {
                        float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                        canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, peintureObjet);
                    }
                    dessinerImage(canvas, objet);
                } else if ("texte".equals(objet.type)) {
                    String texteAAfficher = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                    peintureTexte.setTextScaleX(1.0f);
                    float tw = peintureTexte.measureText(texteAAfficher);
                    if (tw > 0) peintureTexte.setTextScaleX(objet.largeur / tw);
                    canvas.drawText(texteAAfficher, 0, objet.hauteur - (objet.hauteur * 0.1f), peintureTexte);
                    peintureTexte.setTextScaleX(1.0f);
                } else {
                    if (objet.afficherFondColore || objet.cheminImage == null) {
                        canvas.drawRect(0, 0, objet.largeur, objet.hauteur, peintureObjet);
                    }
                    dessinerImage(canvas, objet);
                }

                canvas.restore();

                float[] posAbsolue = {0, 0};
                absMatrix.mapPoints(posAbsolue);
                canvas.drawText(
                        objet.nom + " (" + (int) objet.x + ", " + (int) objet.y + ")",
                        posAbsolue[0], posAbsolue[1] - 10f, peintureDebug
                );
            }
        }
    }
}
// bas 1
