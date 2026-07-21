package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class CanvasBlueprint extends View {
    private Paint paintGrille;
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private float niveauZoom = 1.0f;

    public CanvasBlueprint(Context context) {
        super(context);
        init();
    }

    private void init() {
        paintGrille = new Paint();
        paintGrille.setColor(Color.DKGRAY); // Grille sombre
        paintGrille.setStrokeWidth(2);
        
        // Fond gris très foncé, typique des éditeurs de nœuds
        setBackgroundColor(Color.parseColor("#1E1E1E")); 
    }

    // --- Méthodes de Zoom ---
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

        int gridSize = 150; // Grille plus large pour les nœuds
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
                // Dans le blueprint, on permet toujours le déplacement en glissant le fond
                cameraX += (x - lastTouchX) / niveauZoom;
                cameraY += (y - lastTouchY) / niveauZoom;
                lastTouchX = x;
                lastTouchY = y;
                invalidate(); 
                return true;
        }
        return super.onTouchEvent(event);
    }
}
