package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class CanvasEditeur extends View {
    private Paint paintGrille, paintCamera, paintObjet, paintSelection, paintTexte;
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private boolean isPanMode = false;
    private float niveauZoom = 1.0f;

    private Scene sceneActive;
    private ObjetBase objetSelectionne;
    private InspecteurProprietes inspecteurLie;

    public void setInspecteur(InspecteurProprietes inspecteur) {
        this.inspecteurLie = inspecteur;
    }

    public void deselectionner() {
        this.objetSelectionne = null;
    }

    public CanvasEditeur(Context context) {
        super(context);
        init();
    }

    private void init() {
        paintGrille = new Paint();
        paintGrille.setColor(Color.LTGRAY);
        paintGrille.setStrokeWidth(1);

        paintCamera = new Paint();
        paintCamera.setColor(Color.RED);
        paintCamera.setStyle(Paint.Style.STROKE);
        paintCamera.setStrokeWidth(5);

        paintObjet = new Paint();
        paintObjet.setColor(Color.BLUE);
        paintObjet.setAntiAlias(true);

        // Nouveau pinceau spécifiquement pour le texte
        paintTexte = new Paint();
        paintTexte.setColor(Color.BLUE);
        paintTexte.setTextSize(40f);
        paintTexte.setAntiAlias(true);

        paintSelection = new Paint();
        paintSelection.setColor(Color.YELLOW);
        paintSelection.setStyle(Paint.Style.STROKE);
        paintSelection.setStrokeWidth(6);
    }

    public void setScene(Scene scene) {
        this.sceneActive = scene;
        invalidate();
    }

    public ObjetBase getObjetSelectionne() {
        return objetSelectionne;
    }

    public void setPanMode(boolean enabled) {
        this.isPanMode = enabled;
    }

    public boolean isPanMode() {
        return isPanMode;
    }

    public void zoomPlus() {
        niveauZoom *= 1.25f;
        invalidate();
    }

    public void zoomMoins() {
        niveauZoom /= 1.25f;
        invalidate();
    }

    public void zoomReset() {
        niveauZoom = 1.0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(niveauZoom, niveauZoom, getWidth() / 2f, getHeight() / 2f);

        int gridSize = 100;
        int w = getWidth();
        int h = getHeight();
        int limiteMax = (int) (Math.max(w, h) * 2 / niveauZoom);

        for (int i = -limiteMax + (int) (cameraX % gridSize); i < limiteMax; i += gridSize) {
            canvas.drawLine(i, -limiteMax, i, limiteMax, paintGrille);
        }
        for (int i = -limiteMax + (int) (cameraY % gridSize); i < limiteMax; i += gridSize) {
            canvas.drawLine(-limiteMax, i, limiteMax, i, paintGrille);
        }

        canvas.drawRect(200 + cameraX, 200 + cameraY, 600 + cameraX, 500 + cameraY, paintCamera);

        if (sceneActive != null) {
            for (ObjetBase objet : sceneActive.objets) {
                float left = objet.x + cameraX;
                float top = objet.y + cameraY;
                float right = left + objet.largeur;
                float bottom = top + objet.hauteur;

                if ("rond".equals(objet.type)) {
                    float cx = left + objet.largeur / 2f;
                    float cy = top + objet.hauteur / 2f;
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    canvas.drawCircle(cx, cy, rayon, paintObjet);
                } else if ("texte".equals(objet.type)) {
                    canvas.drawText(objet.nom, left, bottom, paintTexte);
                } else {
                    canvas.drawRect(left, top, right, bottom, paintObjet);
                }

                // Contour jaune si cet objet est sélectionné
                if (objet == objetSelectionne) {
                    canvas.drawRect(
                            left - 4,
                            top - 4,
                            right + 4,
                            bottom + 4,
                            paintSelection
                    );
                }
            }
        }

        canvas.restore();
    }

    // Convertit une coordonnée écran en coordonnée de scène (annule zoom + caméra)
    private float[] ecranVersScene(float xEcran, float yEcran) {
        float centreX = getWidth() / 2f;
        float centreY = getHeight() / 2f;
        float xZoom = centreX + (xEcran - centreX) / niveauZoom;
        float yZoom = centreY + (yEcran - centreY) / niveauZoom;
        return new float[]{xZoom - cameraX, yZoom - cameraY};
    }

    private ObjetBase trouverObjetSousToucher(float xEcran, float yEcran) {
        if (sceneActive == null) return null;

        float[] scenePos = ecranVersScene(xEcran, yEcran);
        float xScene = scenePos[0];
        float yScene = scenePos[1];

        // On parcourt à l'envers pour sélectionner l'objet dessiné en dernier (au-dessus)
        for (int i = sceneActive.objets.size() - 1; i >= 0; i--) {
            ObjetBase objet = sceneActive.objets.get(i);
            if (xScene >= objet.x && xScene <= objet.x + objet.largeur
                    && yScene >= objet.y && yScene <= objet.y + objet.hauteur) {
                return objet;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;

                if (!isPanMode) {
                    objetSelectionne = trouverObjetSousToucher(x, y);
                    if (inspecteurLie != null) {
                        inspecteurLie.afficherObjet(objetSelectionne);
                    }
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isPanMode) {
                    cameraX += (x - lastTouchX) / niveauZoom;
                    cameraY += (y - lastTouchY) / niveauZoom;
                    lastTouchX = x;
                    lastTouchY = y;
                    invalidate();
                } else if (objetSelectionne != null) {
                    // Tâche 5 : Déplacement de l'objet sélectionné
                    objetSelectionne.x += (x - lastTouchX) / niveauZoom;
                    objetSelectionne.y += (y - lastTouchY) / niveauZoom;
                    lastTouchX = x;
                    lastTouchY = y;
                    
                    // Met à jour l'inspecteur pour voir les valeurs X/Y changer en direct
                    if (inspecteurLie != null) {
                        inspecteurLie.afficherObjet(objetSelectionne);
                    }
                    invalidate();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
