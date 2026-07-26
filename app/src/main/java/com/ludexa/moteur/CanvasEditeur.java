// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CanvasEditeur extends View {
    private Paint paintGrille, paintCamera, paintObjet, paintSelection, paintTexte, paintPoignee;
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private boolean isPanMode = false;
    private float niveauZoom = 1.0f;

    private Scene sceneActive;
    private ObjetBase objetSelectionne;
    private InspecteurProprietes inspecteurLie;
    private InterfaceEditeur editeurLie;

    private int currentMode = 0; 
    private float dragStartX, dragStartY;
    private float initX, initY, initW, initH, initRot;

    public CanvasEditeur(Context context) {
        super(context);
        init();
    }

    public void setInspecteur(InspecteurProprietes inspecteur) {
        this.inspecteurLie = inspecteur;
    }

    public void setEditeur(InterfaceEditeur editeur) {
        this.editeurLie = editeur;
    }

    public void deselectionner() {
        this.objetSelectionne = null;
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
        paintObjet.setAntiAlias(true);

        paintTexte = new Paint();
        paintTexte.setTextSize(40f);
        paintTexte.setAntiAlias(true);

        paintSelection = new Paint();
        paintSelection.setColor(Color.parseColor("#CC8844"));
        paintSelection.setStyle(Paint.Style.STROKE);
        paintSelection.setStrokeWidth(6);
        
        paintPoignee = new Paint();
        paintPoignee.setColor(Color.parseColor("#E53935"));
        paintPoignee.setStyle(Paint.Style.FILL);
        paintPoignee.setAntiAlias(true);
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
// bas 1

// haut 2
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
            List<ObjetBase> objetsTries = new ArrayList<>(sceneActive.objets);
            Collections.sort(objetsTries, new Comparator<ObjetBase>() {
                @Override
                public int compare(ObjetBase o1, ObjetBase o2) {
                    return Integer.compare(o1.zOrder, o2.zOrder);
                }
            });

            for (ObjetBase objet : objetsTries) {
                float left = objet.x + cameraX;
                float top = objet.y + cameraY;
                float right = left + objet.largeur;
                float bottom = top + objet.hauteur;
                
                float cx = left + objet.largeur / 2f;
                float cy = top + objet.hauteur / 2f;

                canvas.save();
                if (objet.rotation != 0) {
                    canvas.rotate(objet.rotation, cx, cy);
                }

                if (objet.visible) {
                    if ("rond".equals(objet.type)) {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                        canvas.drawCircle(cx, cy, rayon, paintObjet);
                    } else if ("texte".equals(objet.type)) {
                        paintTexte.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        String texteAAfficher = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                        canvas.drawText(texteAAfficher, left, bottom, paintTexte);
                    } else {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        canvas.drawRect(left, top, right, bottom, paintObjet);
                    }
                }

                if (objet == objetSelectionne) {
                    canvas.drawRect(left - 4, top - 4, right + 4, bottom + 4, paintSelection);
                    
                    float hs = 12f;
                    canvas.drawRect(left - 4 - hs, top - 4 - hs, left - 4 + hs, top - 4 + hs, paintPoignee); 
                    canvas.drawRect(right + 4 - hs, top - 4 - hs, right + 4 + hs, top - 4 + hs, paintPoignee); 
                    canvas.drawRect(left - 4 - hs, bottom + 4 - hs, left - 4 + hs, bottom + 4 + hs, paintPoignee); 
                    canvas.drawRect(right + 4 - hs, bottom + 4 - hs, right + 4 + hs, bottom + 4 + hs, paintPoignee); 
                    
                    float rotY = top - 4 - 50f;
                    canvas.drawLine(cx, top - 4, cx, rotY, paintSelection);
                    canvas.drawCircle(cx, rotY, 15f, paintPoignee);
                }
                canvas.restore();
            }
        }
        canvas.restore();
    }

    private float[] ecranVersScene(float xEcran, float yEcran) {
        float centreX = getWidth() / 2f;
        float centreY = getHeight() / 2f;
        float xZoom = centreX + (xEcran - centreX) / niveauZoom;
        float yZoom = centreY + (yEcran - centreY) / niveauZoom;
        return new float[]{xZoom - cameraX, yZoom - cameraY};
    }

    private float[] sceneVersLocal(float sx, float sy, ObjetBase objet) {
        float cx = objet.x + objet.largeur / 2f;
        float cy = objet.y + objet.hauteur / 2f;
        double angleRad = Math.toRadians(-objet.rotation);
        float dx = sx - cx;
        float dy = sy - cy;
        float rx = (float) (dx * Math.cos(angleRad) - dy * Math.sin(angleRad));
        float ry = (float) (dx * Math.sin(angleRad) + dy * Math.cos(angleRad));
        return new float[]{cx + rx, cy + ry};
    }

    private float[] getPointInScene(float objX, float objY, float objW, float objH, float rot, float localX, float localY) {
        float cx = objX + objW / 2f;
        float cy = objY + objH / 2f;
        double angleRad = Math.toRadians(rot);
        float dx = localX - cx;
        float dy = localY - cy;
        float rx = (float) (dx * Math.cos(angleRad) - dy * Math.sin(angleRad));
        float ry = (float) (dx * Math.sin(angleRad) + dy * Math.cos(angleRad));
        return new float[]{cx + rx, cy + ry};
    }
// bas 2

// haut 3
    private ObjetBase trouverObjetSousToucher(float xEcran, float yEcran) {
        if (sceneActive == null) return null;
        float[] scenePos = ecranVersScene(xEcran, yEcran);
        float sx = scenePos[0], sy = scenePos[1];

        List<ObjetBase> objetsTries = new ArrayList<>(sceneActive.objets);
        Collections.sort(objetsTries, new Comparator<ObjetBase>() {
            @Override
            public int compare(ObjetBase o1, ObjetBase o2) {
                return Integer.compare(o1.zOrder, o2.zOrder);
            }
        });

        for (int i = objetsTries.size() - 1; i >= 0; i--) {
            ObjetBase objet = objetsTries.get(i);
            float[] localPos = sceneVersLocal(sx, sy, objet);
            float lx = localPos[0], ly = localPos[1];
            
            if (lx >= objet.x && lx <= objet.x + objet.largeur
                    && ly >= objet.y && ly <= objet.y + objet.hauteur) {
                return objet;
            }
        }
        return null;
    }

    private int getTouchTarget(float xEcran, float yEcran) {
        float[] scenePos = ecranVersScene(xEcran, yEcran);
        float sx = scenePos[0], sy = scenePos[1];
        
        if (objetSelectionne != null) {
            float[] localPos = sceneVersLocal(sx, sy, objetSelectionne);
            float lx = localPos[0], ly = localPos[1];
            
            float left = objetSelectionne.x;
            float top = objetSelectionne.y;
            float right = left + objetSelectionne.largeur;
            float bottom = top + objetSelectionne.hauteur;
            
            float hit = 60f / niveauZoom;
            
            float midX = left + objetSelectionne.largeur / 2f;
            float rotY = top - 4 - 50f;
            if (Math.hypot(lx - midX, ly - rotY) < hit) return 8; 
            
            if (Math.abs(lx - left) < hit && Math.abs(ly - top) < hit) return 4; 
            if (Math.abs(lx - right) < hit && Math.abs(ly - top) < hit) return 5; 
            if (Math.abs(lx - left) < hit && Math.abs(ly - bottom) < hit) return 6; 
            if (Math.abs(lx - right) < hit && Math.abs(ly - bottom) < hit) return 7; 
            
            if (lx >= left && lx <= right && ly >= top && ly <= bottom) return 2; 
        }
        
        ObjetBase obj = trouverObjetSousToucher(xEcran, yEcran);
        if (obj != null) {
            objetSelectionne = obj;
            return 2; 
        }
        
        objetSelectionne = null;
        return 0; 
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPanMode) {
                    currentMode = 1;
                } else {
                    currentMode = getTouchTarget(x, y);
                    if (objetSelectionne != null) {
                        initX = objetSelectionne.x;
                        initY = objetSelectionne.y;
                        initW = objetSelectionne.largeur;
                        initH = objetSelectionne.hauteur;
                        initRot = objetSelectionne.rotation;
                        
                        dragStartX = objetSelectionne.x;
                        dragStartY = objetSelectionne.y;
                    }
                    if (inspecteurLie != null) {
                        inspecteurLie.afficherObjet(objetSelectionne);
                    }
                    invalidate();
                }
                lastTouchX = x;
                lastTouchY = y;
                return true;

            case MotionEvent.ACTION_MOVE:
                float[] scenePos = ecranVersScene(x, y);
                float sx = scenePos[0], sy = scenePos[1];
                
                if (currentMode == 1) { 
                    cameraX += (x - lastTouchX) / niveauZoom;
                    cameraY += (y - lastTouchY) / niveauZoom;
                } else if (currentMode == 2 && objetSelectionne != null) { 
                    objetSelectionne.x += (x - lastTouchX) / niveauZoom;
                    objetSelectionne.y += (y - lastTouchY) / niveauZoom;
                } else if (currentMode >= 4 && currentMode <= 7 && objetSelectionne != null) { 
                    
                    float[] localTouch = sceneVersLocal(sx, sy, objetSelectionne);
                    float lx = localTouch[0], ly = localTouch[1];
                    
                    float anchorLocX = 0, anchorLocY = 0;
                    float newX = initX, newY = initY, newW = initW, newH = initH;
                    
                    if (currentMode == 4) { 
                        anchorLocX = initX + initW; anchorLocY = initY + initH;
                        newX = lx; newY = ly;
                        newW = anchorLocX - lx; newH = anchorLocY - ly;
                    } else if (currentMode == 5) { 
                        anchorLocX = initX; anchorLocY = initY + initH;
                        newY = ly;
                        newW = lx - initX; newH = anchorLocY - ly;
                    } else if (currentMode == 6) { 
                        anchorLocX = initX + initW; anchorLocY = initY;
                        newX = lx;
                        newW = anchorLocX - lx; newH = ly - initY;
                    } else if (currentMode == 7) { 
                        anchorLocX = initX; anchorLocY = initY;
                        newW = lx - initX; newH = ly - initY;
                    }
                    
                    if (newW < 20) { newX += (newW - 20) * (currentMode==4||currentMode==6 ? 1 : 0); newW = 20; }
                    if (newH < 20) { newY += (newH - 20) * (currentMode==4||currentMode==5 ? 1 : 0); newH = 20; }
                    
                    float[] oldAnchor = getPointInScene(initX, initY, initW, initH, initRot, anchorLocX, anchorLocY);
                    float[] newAnchor = getPointInScene(newX, newY, newW, newH, initRot, anchorLocX, anchorLocY);
                    
                    objetSelectionne.x = newX + (oldAnchor[0] - newAnchor[0]);
                    objetSelectionne.y = newY + (oldAnchor[1] - newAnchor[1]);
                    objetSelectionne.largeur = newW;
                    objetSelectionne.hauteur = newH;
                    
                } else if (currentMode == 8 && objetSelectionne != null) { 
                    float cx = objetSelectionne.x + objetSelectionne.largeur / 2f;
                    float cy = objetSelectionne.y + objetSelectionne.hauteur / 2f;
                    double angle = Math.toDegrees(Math.atan2(sy - cy, sx - cx));
                    objetSelectionne.rotation = (float) (angle + 90);
                }
                
                lastTouchX = x;
                lastTouchY = y;
                
                if (currentMode != 0) {
                    if (inspecteurLie != null && objetSelectionne != null) {
                        inspecteurLie.afficherObjet(objetSelectionne);
                    }
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (currentMode == 2 && objetSelectionne != null) {
                    if (dragStartX != objetSelectionne.x || dragStartY != objetSelectionne.y) {
                        if (editeurLie != null) {
                            editeurLie.ajouterCommande(new CommandeDeplacement(
                                    objetSelectionne, dragStartX, dragStartY,
                                    objetSelectionne.x, objetSelectionne.y
                            ));
                        }
                    }
                }
                currentMode = 0;
                return true;
        }
        return super.onTouchEvent(event);
    }
}
// bas 3

