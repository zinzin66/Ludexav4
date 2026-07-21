package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class CanvasEditeur extends View {
    private Paint paintGrille, paintCamera;
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private boolean isPanMode = false;
    
    // Nouvelle variable pour gérer le zoom
    private float niveauZoom = 1.0f;

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
    }

    public void setPanMode(boolean enabled) {
        this.isPanMode = enabled;
    }

    public boolean isPanMode() {
        return isPanMode;
    }

    // --- Méthodes de Zoom ---
    public void zoomPlus() {
        niveauZoom *= 1.25f; // Augmente de 25%
        invalidate(); // Force le redessin
    }

    public void zoomMoins() {
        niveauZoom /= 1.25f; // Diminue de 25%
        invalidate();
    }

    public void zoomReset() {
        niveauZoom = 1.0f; // Retour à la normale
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Sauvegarde de l'état du canvas avant transformation
        canvas.save();
        
        // Appliquer le zoom par rapport au centre de l'écran
        canvas.scale(niveauZoom, niveauZoom, getWidth() / 2f, getHeight() / 2f);

        int gridSize = 100;
        
        // On calcule une zone de dessin très large pour s'assurer que la grille 
        // couvre tout l'écran même en cas de fort dézoom et déplacement
        int w = getWidth();
        int h = getHeight();
        int limiteMax = (int) (Math.max(w, h) * 2 / niveauZoom);

        // Dessin de la grille avec offset de caméra
        for (int i = -limiteMax + (int)(cameraX % gridSize); i < limiteMax; i += gridSize) {
            canvas.drawLine(i, -limiteMax, i, limiteMax, paintGrille);
        }
        for (int i = -limiteMax + (int)(cameraY % gridSize); i < limiteMax; i += gridSize) {
            canvas.drawLine(-limiteMax, i, limiteMax, i, paintGrille);
        }

        // Dessin du rectangle caméra (fixe au centre pour l'instant)
        canvas.drawRect(200 + cameraX, 200 + cameraY, 600 + cameraX, 500 + cameraY, paintCamera);
        
        // Restauration de l'état initial
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
                    // On divise le déplacement par le zoom pour que le mouvement sous le doigt reste naturel (1:1)
                    cameraX += (x - lastTouchX) / niveauZoom;
                    cameraY += (y - lastTouchY) / niveauZoom;
                    lastTouchX = x;
                    lastTouchY = y;
                    invalidate(); 
                } else {
                    // TODO: Logique de sélection d'objet ici
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
