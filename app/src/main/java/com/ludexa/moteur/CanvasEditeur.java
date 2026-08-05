// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    
    private String cheminProjet; // Nouveau champ pour le chemin du projet

    private int currentMode = 0; 
    private float dragStartX, dragStartY;
    private float initX, initY, initW, initH, initRot, initScaleX, initScaleY;
    private Matrix initMatrix;
    
    private ScaleGestureDetector scaleGestureDetector;
    
    private java.util.Map<String, android.graphics.Bitmap> cacheImages = new java.util.HashMap<>();

    public CanvasEditeur(Context context) {
        super(context);
        init();
    }

    public void setCheminProjet(String cheminProjet) {
        this.cheminProjet = cheminProjet;
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

    public ObjetBase getObjetSelectionne() {
        return objetSelectionne;
    }

    // FIX 1: Ajout méthode pour synchroniser et notifier explicitement un changement de sélection depuis la liste
    public void setObjetSelectionne(ObjetBase obj) {
        this.objetSelectionne = obj;
        if (inspecteurLie != null) {
            inspecteurLie.afficherObjet(obj);
        }
        invalidate();
    }

    private void init() {
        paintGrille = new Paint();
        paintGrille.setColor(Palette.canvasGrille);
        paintGrille.setStrokeWidth(1);
        
        setBackgroundColor(Palette.canvasFond);

        paintCamera = new Paint();
        paintCamera.setColor(Color.RED);
        paintCamera.setStyle(Paint.Style.STROKE);
        paintCamera.setStrokeWidth(5);

        paintObjet = new Paint();
        paintObjet.setAntiAlias(true);

        paintTexte = new Paint();
        paintTexte.setAntiAlias(true);

        paintSelection = new Paint();
        paintSelection.setColor(Color.parseColor("#CC8844"));
        paintSelection.setStyle(Paint.Style.STROKE);
        
        paintPoignee = new Paint();
        paintPoignee.setColor(Color.parseColor("#E53935"));
        paintPoignee.setStyle(Paint.Style.FILL);
        paintPoignee.setAntiAlias(true);
        
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
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

    public void zoomPlus() { niveauZoom *= 1.25f; invalidate(); }
    public void zoomMoins() { niveauZoom /= 1.25f; invalidate(); }
    public void zoomReset() { niveauZoom = 1.0f; invalidate(); }

    public static class TransformAbsolue {
        public float x, y, rotation, scaleX, scaleY;
    }

    public ObjetBase getObjetById(String id) {
        if (sceneActive == null || id == null) return null;
        for (ObjetBase o : sceneActive.objets) {
            if (o.id.equals(id)) return o;
        }
        return null;
    }

    public Matrix getAbsoluteMatrix(ObjetBase obj) {
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

    public Matrix getParentMatrix(ObjetBase obj) {
        if (obj.parentId != null) {
            ObjetBase parent = getObjetById(obj.parentId);
            if (parent != null) {
                return getAbsoluteMatrix(parent);
            }
        }
        return new Matrix();
    }

    public float getAbsoluteRotation(ObjetBase obj) {
        float rot = 0;
        ObjetBase cur = obj;
        while (cur != null) {
            rot += cur.rotation;
            cur = getObjetById(cur.parentId);
        }
        return rot;
    }

    public TransformAbsolue getCalculTransformationAbsolue(ObjetBase obj) {
        TransformAbsolue t = new TransformAbsolue();
        t.rotation = getAbsoluteRotation(obj);
        
        float sx = 1f, sy = 1f;
        ObjetBase cur = obj;
        while(cur != null) {
            sx *= cur.scaleX; sy *= cur.scaleY;
            cur = getObjetById(cur.parentId);
        }
        t.scaleX = sx; t.scaleY = sy;
        
        float[] center = {obj.largeur / 2f, obj.hauteur / 2f};
        getAbsoluteMatrix(obj).mapPoints(center);
        t.x = center[0];
        t.y = center[1];
        return t;
    }

    public float[] worldToLocal(ObjetBase obj, float worldX, float worldY) {
        Matrix m = getAbsoluteMatrix(obj);
        Matrix inv = new Matrix();
        m.invert(inv);
        float[] pts = {worldX, worldY};
        inv.mapPoints(pts);
        return pts;
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

        canvas.drawRect(0 + cameraX, 0 + cameraY, ConfigurationJeu.LARGEUR_JEU + cameraX, ConfigurationJeu.HAUTEUR_JEU + cameraY, paintCamera);

        if (sceneActive != null) {
            List<ObjetBase> objetsTries = new ArrayList<>(sceneActive.objets);
            Collections.sort(objetsTries, new Comparator<ObjetBase>() {
                @Override
                public int compare(ObjetBase o1, ObjetBase o2) {
                    return Integer.compare(o1.zOrder, o2.zOrder);
                }
            });

            for (ObjetBase objet : objetsTries) {
                if (!objet.visible) continue;

                Matrix absMatrix = getAbsoluteMatrix(objet);
                
                canvas.save();
                canvas.translate(cameraX, cameraY);
                canvas.concat(absMatrix);

                if ("rond".equals(objet.type)) {
                    if (objet.afficherFondColore || objet.cheminImage == null) {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                        canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, paintObjet);
                    }
                    dessinerImage(canvas, objet);
                } else if ("texte".equals(objet.type)) {
                    paintTexte.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                    String txt = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                    paintTexte.setTextSize(objet.hauteur * 0.8f);
                    paintTexte.setTextScaleX(1.0f);
                    float tw = paintTexte.measureText(txt);
                    if (tw > 0) paintTexte.setTextScaleX(objet.largeur / tw);
                    canvas.drawText(txt, 0, objet.hauteur - (objet.hauteur * 0.1f), paintTexte);
                    paintTexte.setTextScaleX(1.0f);
                } else {
                    if (objet.afficherFondColore || objet.cheminImage == null) {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        canvas.drawRect(0, 0, objet.largeur, objet.hauteur, paintObjet);
                    }
                    dessinerImage(canvas, objet);
                }

                if (objet == objetSelectionne) {
                    float scaleFactor = Math.max(Math.abs(objet.scaleX), Math.abs(objet.scaleY));
                    if (scaleFactor < 0.01f) scaleFactor = 0.01f;

                    paintSelection.setStrokeWidth(2f / scaleFactor);
                    float l = -4f / scaleFactor;
                    float t = -4f / scaleFactor;
                    float r = objet.largeur + 4f / scaleFactor;
                    float b = objet.hauteur + 4f / scaleFactor;
                    
                    canvas.drawRect(l, t, r, b, paintSelection);
                    
                    float hs = 12f / scaleFactor;
                    canvas.drawRect(l - hs, t - hs, l + hs, t + hs, paintPoignee); 
                    canvas.drawRect(r - hs, t - hs, r + hs, t + hs, paintPoignee); 
                    canvas.drawRect(l - hs, b - hs, l + hs, b + hs, paintPoignee); 
                    canvas.drawRect(r - hs, b - hs, r + hs, b + hs, paintPoignee); 
                    
                    float cx = objet.largeur / 2f;
                    float rotY = t - (50f / scaleFactor);
                    canvas.drawLine(cx, t, cx, rotY, paintSelection);
                    canvas.drawCircle(cx, rotY, 15f / scaleFactor, paintPoignee);
                }
                canvas.restore();
            }
        }
        canvas.restore();
    }

    private void dessinerImage(Canvas canvas, ObjetBase objet) {
        if (objet.cheminImage != null && cheminProjet != null) {
            android.graphics.Bitmap bmp = cacheImages.get(objet.cheminImage);
            if (bmp == null) {
                try {
                    // Utilisation de cheminProjet au lieu de getFilesDir()
                    java.io.File imgFile = new java.io.File(cheminProjet, objet.cheminImage);
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
                    canvas.drawBitmap(bmp, null, new android.graphics.RectF(0, 0, objet.largeur, objet.hauteur), paintObjet);
                    canvas.restore();
                } else {
                    canvas.drawBitmap(bmp, null, new android.graphics.RectF(0, 0, objet.largeur, objet.hauteur), paintObjet);
                }
            }
        }
    }

    private float[] ecranVersScene(float xEcran, float yEcran) {
        float cx = getWidth() / 2f, cy = getHeight() / 2f;
        float xZoom = cx + (xEcran - cx) / niveauZoom;
        float yZoom = cy + (yEcran - cy) / niveauZoom;
        return new float[]{xZoom - cameraX, yZoom - cameraY};
    }

    private ObjetBase trouverObjetSousToucher(float xEcran, float yEcran) {
        if (sceneActive == null) return null;
        float[] scenePos = ecranVersScene(xEcran, yEcran);
        float sx = scenePos[0], sy = scenePos[1];

        List<ObjetBase> objetsTries = new ArrayList<>(sceneActive.objets);
        Collections.sort(objetsTries, new Comparator<ObjetBase>() {
            @Override public int compare(ObjetBase o1, ObjetBase o2) { return Integer.compare(o1.zOrder, o2.zOrder); }
        });

        for (int i = objetsTries.size() - 1; i >= 0; i--) {
            ObjetBase objet = objetsTries.get(i);
            float[] localPos = worldToLocal(objet, sx, sy);
            float lx = localPos[0], ly = localPos[1];
            if (lx >= 0 && lx <= objet.largeur && ly >= 0 && ly <= objet.hauteur) {
                return objet;
            }
        }
        return null;
    }

    private int getTouchTarget(float xEcran, float yEcran) {
        float[] scenePos = ecranVersScene(xEcran, yEcran);
        float sx = scenePos[0], sy = scenePos[1];
        
        if (objetSelectionne != null) {
            float[] pts = worldToLocal(objetSelectionne, sx, sy);
            float lx = pts[0], ly = pts[1];
            
            float scale = Math.max(Math.abs(objetSelectionne.scaleX), Math.abs(objetSelectionne.scaleY));
            if (scale < 0.01f) scale = 0.01f;
            float hit = (30f / niveauZoom) / scale;
            
            float midX = objetSelectionne.largeur / 2f;
            float rotY = -50f / Math.abs(objetSelectionne.scaleY);
            if (Math.hypot(lx - midX, ly - rotY) < hit) return 8; 
            
            if (Math.abs(lx) < hit && Math.abs(ly) < hit) return 4; 
            if (Math.abs(lx - objetSelectionne.largeur) < hit && Math.abs(ly) < hit) return 5; 
            if (Math.abs(lx) < hit && Math.abs(ly - objetSelectionne.hauteur) < hit) return 6; 
            if (Math.abs(lx - objetSelectionne.largeur) < hit && Math.abs(ly - objetSelectionne.hauteur) < hit) return 7; 
            
            if (lx >= 0 && lx <= objetSelectionne.largeur && ly >= 0 && ly <= objetSelectionne.hauteur) return 2; 
        }
        
        ObjetBase obj = trouverObjetSousToucher(xEcran, yEcran);
        if (obj != null) {
            objetSelectionne = obj;
            if (editeurLie != null) editeurLie.rafraichirArborescence(obj);
            return 2; 
        }
        objetSelectionne = null;
        if (editeurLie != null) editeurLie.rafraichirArborescence(null);
        return 0; 
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Traitement du Pinch-to-zoom AVANT le switch
        scaleGestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPanMode) {
                    currentMode = 1;
                } else {
                    currentMode = getTouchTarget(x, y);
                    
                    // Pan direct sur zone vide
                    if (currentMode == 0) {
                        currentMode = 1;
                    }
                    
                    if (objetSelectionne != null) {
                        initX = objetSelectionne.x; initY = objetSelectionne.y;
                        initW = objetSelectionne.largeur; initH = objetSelectionne.hauteur;
                        initRot = objetSelectionne.rotation;
                        initScaleX = objetSelectionne.scaleX; initScaleY = objetSelectionne.scaleY;
                        initMatrix = getAbsoluteMatrix(objetSelectionne);
                        dragStartX = objetSelectionne.x; dragStartY = objetSelectionne.y;
                    }
                    if (inspecteurLie != null) inspecteurLie.afficherObjet(objetSelectionne);
                    invalidate();
                }
                lastTouchX = x; lastTouchY = y;
                return true;

            case MotionEvent.ACTION_MOVE:
                // Ignore le traitement de pan/déplacement si un pincement est en cours
                if (scaleGestureDetector.isInProgress()) {
                    lastTouchX = x;
                    lastTouchY = y;
                    return true;
                }

                float[] scenePos = ecranVersScene(x, y);
                float sx = scenePos[0], sy = scenePos[1];
                
                if (currentMode == 1) { 
                    cameraX += (x - lastTouchX) / niveauZoom;
                    cameraY += (y - lastTouchY) / niveauZoom;
                } else if (currentMode == 2 && objetSelectionne != null) { 
                    Matrix invParent = new Matrix();
                    getParentMatrix(objetSelectionne).invert(invParent);
                    
                    float[] curTouchP = {sx, sy};
                    float[] lastTouchP = {ecranVersScene(lastTouchX, lastTouchY)[0], ecranVersScene(lastTouchX, lastTouchY)[1]};
                    
                    invParent.mapPoints(curTouchP); invParent.mapPoints(lastTouchP);
                    objetSelectionne.x += (curTouchP[0] - lastTouchP[0]);
                    objetSelectionne.y += (curTouchP[1] - lastTouchP[1]);
                } else if (currentMode >= 4 && currentMode <= 7 && objetSelectionne != null) { 
                    Matrix invInit = new Matrix();
                    initMatrix.invert(invInit);
                    float[] ptsInit = {sx, sy};
                    invInit.mapPoints(ptsInit);
                    float lx = ptsInit[0], ly = ptsInit[1];
                    
                    float newSx = initScaleX, newSy = initScaleY;
                    if (currentMode == 4) { newSx = initScaleX * ((initW - lx) / initW); newSy = initScaleY * ((initH - ly) / initH); }
                    else if (currentMode == 5) { newSx = initScaleX * (lx / initW); newSy = initScaleY * ((initH - ly) / initH); }
                    else if (currentMode == 6) { newSx = initScaleX * ((initW - lx) / initW); newSy = initScaleY * (ly / initH); }
                    else if (currentMode == 7) { newSx = initScaleX * (lx / initW); newSy = initScaleY * (ly / initH); }
                    
                    if (Math.abs(newSx) < 0.05f) newSx = 0.05f * Math.signum(newSx);
                    if (Math.abs(newSy) < 0.05f) newSy = 0.05f * Math.signum(newSy);
                    
                    objetSelectionne.scaleX = newSx; objetSelectionne.scaleY = newSy;
                    
                    float ancLocX = (currentMode == 4 || currentMode == 6) ? initW : 0;
                    float ancLocY = (currentMode == 4 || currentMode == 5) ? initH : 0;
                    
                    float[] initAnchorWorld = {ancLocX, ancLocY};
                    initMatrix.mapPoints(initAnchorWorld);
                    
                    Matrix newMat = getAbsoluteMatrix(objetSelectionne);
                    float[] newAnchorWorld = {ancLocX, ancLocY};
                    newMat.mapPoints(newAnchorWorld);
                    
                    Matrix parentMat = getParentMatrix(objetSelectionne);
                    Matrix invParent = new Matrix();
                    parentMat.invert(invParent);
                    
                    float[] initAncParent = {initAnchorWorld[0], initAnchorWorld[1]};
                    float[] newAncParent = {newAnchorWorld[0], newAnchorWorld[1]};
                    invParent.mapPoints(initAncParent); invParent.mapPoints(newAncParent);
                    
                    objetSelectionne.x += (initAncParent[0] - newAncParent[0]);
                    objetSelectionne.y += (initAncParent[1] - newAncParent[1]);
                    
                } else if (currentMode == 8 && objetSelectionne != null) { 
                    float[] centerWorld = {objetSelectionne.largeur / 2f, objetSelectionne.hauteur / 2f};
                    getAbsoluteMatrix(objetSelectionne).mapPoints(centerWorld);
                    
                    double angleWorld = Math.toDegrees(Math.atan2(sy - centerWorld[1], sx - centerWorld[0]));
                    float parentRot = getAbsoluteRotation(getObjetById(objetSelectionne.parentId));
                    objetSelectionne.rotation = (float) (angleWorld + 90) - parentRot;
                }
                
                lastTouchX = x; lastTouchY = y;
                if (currentMode != 0) {
                    if (inspecteurLie != null && objetSelectionne != null) inspecteurLie.afficherObjet(objetSelectionne);
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (currentMode == 2 && objetSelectionne != null) {
                    if (dragStartX != objetSelectionne.x || dragStartY != objetSelectionne.y) {
                        if (editeurLie != null) {
                            editeurLie.ajouterCommande(new CommandeDeplacement(objetSelectionne, dragStartX, dragStartY, objetSelectionne.x, objetSelectionne.y));
                        }
                    }
                }
                currentMode = 0; return true;
        }
        return super.onTouchEvent(event);
    }

    // Listener interne pour la gestion du zoom à deux doigts
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            niveauZoom *= detector.getScaleFactor();
            // Limitation raisonnable du zoom entre 0.2f et 5.0f
            niveauZoom = Math.max(0.2f, Math.min(niveauZoom, 5.0f));
            invalidate();
            return true;
        }
    }
}
// bas 2











