package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class CanvasEditeur extends View {
    private Paint paintGrille, paintCamera, paintObjet;
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private boolean isPanMode = false;
    private float niveauZoom = 1.0f;

    private Scene sceneActive;

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
    }

    public void setScene(Scene scene) {
        this.sceneActive = scene;
        invalidate();
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

        // Dessin des objets de la scène active
        if (sceneActive != null) {
            for (ObjetBase objet : sceneActive.objets) {
                canvas.drawRect(
                        objet.x + cameraX,
                        objet.y + cameraY,
                        objet.x + objet.largeur + cameraX,
                        objet.y + objet.hauteur + cameraY,
                        paintObjet
                );
            }
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isPanMode) {
                    cameraX += (x - lastTouchX) / niveauZoom;
                    cameraY += (y - lastTouchY) / niveauZoom;
                    lastTouchX = x;
                    lastTouchY = y;
                    invalidate();
                } else {
                    // TODO: Logique de sélection d'objet ici (prochaine étape)
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
