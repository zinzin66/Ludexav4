// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class CanvasBlueprint extends View {
    private Paint paintGrille;
    private Paint paintNoeudBG;
    private Paint paintTitreBG;
    private Paint paintTexteTitre;
    private Paint paintTextePort;
    private Paint paintPort;
    
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private float niveauZoom = 1.0f;
    
    private Blueprint blueprintActuel;

    public CanvasBlueprint(Context context) {
        super(context);
        init();
    }

    private void init() {
        paintGrille = new Paint();
        paintGrille.setColor(Color.DKGRAY); // Grille sombre
        paintGrille.setStrokeWidth(2);
        
        paintNoeudBG = new Paint();
        paintNoeudBG.setColor(Color.parseColor("#2A2A2A"));
        paintNoeudBG.setStyle(Paint.Style.FILL);
        
        paintTitreBG = new Paint();
        paintTitreBG.setColor(Color.parseColor("#444444"));
        paintTitreBG.setStyle(Paint.Style.FILL);
        
        paintTexteTitre = new Paint();
        paintTexteTitre.setColor(Color.WHITE);
        paintTexteTitre.setTextSize(24);
        paintTexteTitre.setFakeBoldText(true);
        paintTexteTitre.setAntiAlias(true);
        
        paintTextePort = new Paint();
        paintTextePort.setColor(Color.LTGRAY);
        paintTextePort.setTextSize(18);
        paintTextePort.setAntiAlias(true);
        
        paintPort = new Paint();
        paintPort.setStyle(Paint.Style.FILL);
        paintPort.setAntiAlias(true);
        
        // Fond gris très foncé
        setBackgroundColor(Color.parseColor("#1E1E1E")); 
    }

    public void setBlueprint(Blueprint blueprint) {
        this.blueprintActuel = blueprint;
        invalidate();
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
// bas 1
// haut 2
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(niveauZoom, niveauZoom, getWidth() / 2f, getHeight() / 2f);

        int gridSize = 150; 
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
        
        // Appliquer l'offset de caméra pour les objets
        canvas.translate(cameraX, cameraY);
        
        // Dessin des nœuds
        if (blueprintActuel != null) {
            for (NoeudBase noeud : blueprintActuel.noeuds) {
                dessinerNoeud(canvas, noeud);
            }
        }
        
        canvas.restore();
    }

    private void dessinerNoeud(Canvas canvas, NoeudBase noeud) {
        Float xObj = blueprintActuel.noeudsX.get(noeud.id);
        Float yObj = blueprintActuel.noeudsY.get(noeud.id);
        
        if (xObj == null || yObj == null) return;
        
        float x = xObj;
        float y = yObj;
        
        float largeur = 260;
        int maxPorts = Math.max(noeud.portsEntree.size(), noeud.portsSortie.size());
        float hauteur = 60 + (maxPorts * 40) + 20; 
        
        // 1. Dessiner le fond du nœud (arrondi)
        RectF rectFond = new RectF(x, y, x + largeur, y + hauteur);
        canvas.drawRoundRect(rectFond, 16, 16, paintNoeudBG);
        
        // 2. Dessiner l'en-tête du nœud (arrondi en haut, carré en bas)
        RectF rectTitre = new RectF(x, y, x + largeur, y + 45);
        canvas.drawRoundRect(rectTitre, 16, 16, paintTitreBG);
        canvas.drawRect(x, y + 25, x + largeur, y + 45, paintTitreBG);
        
        // 3. Titre
        canvas.drawText(noeud.nom, x + 15, y + 32, paintTexteTitre);
        
        // 4. Ports d'entrée
        float startY = y + 70;
        for (int i = 0; i < noeud.portsEntree.size(); i++) {
            Port p = noeud.portsEntree.get(i);
            definirCouleurPort(p);
            float portY = startY + (i * 40);
            canvas.drawCircle(x, portY, 8, paintPort);
            canvas.drawText(p.nom, x + 20, portY + 6, paintTextePort);
        }
        
        // 5. Ports de sortie
        for (int i = 0; i < noeud.portsSortie.size(); i++) {
            Port p = noeud.portsSortie.get(i);
            definirCouleurPort(p);
            float portY = startY + (i * 40);
            canvas.drawCircle(x + largeur, portY, 8, paintPort);
            float textWidth = paintTextePort.measureText(p.nom);
            canvas.drawText(p.nom, x + largeur - 20 - textWidth, portY + 6, paintTextePort);
        }
    }
    
    private void definirCouleurPort(Port p) {
        if (Port.TYPE_EXECUTION_ENTREE.equals(p.type) || Port.TYPE_EXECUTION_SORTIE.equals(p.type)) {
            paintPort.setColor(Color.WHITE);
        } else {
            paintPort.setColor(Color.parseColor("#44AAFF")); // Bleu pour les données
        }
    }
// bas 2
// haut 3
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
// bas 3
