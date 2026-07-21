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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int gridSize = 100;
        int w = getWidth();
        int h = getHeight();

        // Dessin de la grille avec offset de caméra
        for (int i = (int)(cameraX % gridSize); i < w; i += gridSize) {
            canvas.drawLine(i, 0, i, h, paintGrille);
        }
        for (int i = (int)(cameraY % gridSize); i < h; i += gridSize) {
            canvas.drawLine(0, i, w, i, paintGrille);
        }

        // Dessin du rectangle caméra (fixe au centre pour l'instant)
        canvas.drawRect(200 + cameraX, 200 + cameraY, 600 + cameraX, 500 + cameraY, paintCamera);
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
                    cameraX += (x - lastTouchX);
                    cameraY += (y - lastTouchY);
                    lastTouchX = x;
                    lastTouchY = y;
                    invalidate(); // Redessine
                } else {
                    // TODO: Logique de sélection d'objet ici
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
