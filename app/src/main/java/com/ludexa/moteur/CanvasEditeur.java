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
    private boolean isModeDeplacementObjet = false; // NOUVEAU
    private float niveauZoom = 1.0f;

    private Scene sceneActive;
    private ObjetBase objetSelectionne;
    private InspecteurProprietes inspecteurLie;
    private InterfaceEditeur editeurLie;
    
    private String cheminProjet; 

    private int currentMode = 0; 
    private float dragStartX, dragStartY;
    private float initX, initY, initW, initH, initRot, initScaleX, initScaleY;
    private Matrix initMatrix;
    
    private ScaleGestureDetector scaleGestureDetector;
    
    private java.util.Map<String, android.graphics.Bitmap> cacheImages = new java.util.HashMap<>();
    private java.util.Map<String, android.graphics.Typeface> cachePolices = new java.util.HashMap<>();

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

    public void setObjetSelectionne(ObjetBase obj) {
        this.objetSelectionne = obj;
        if (inspecteurLie != null) {
            inspecteurLie.afficherObjet(obj);
        }
        invalidate();
    }

    public void setModeDeplacementObjet(boolean mode) {
        this.isModeDeplacementObjet = mode;
        invalidate();
    }

    public boolean isModeDeplacementObjet() {
        return isModeDeplacementObjet;
    }

    private void init() {
        paintGrille = new Paint();
        paintGrille.setColor(Palette.canvasGrille);
        paintGrille.setStyle(Paint.Style.STROKE);
        paintGrille.setStrokeWidth(1);
        paintGrille.setAntiAlias(false);
        paintGrille.setAlpha(180);

        setBackgroundColor(Palette.canvasFond);

        paintCamera = new Paint();
        paintCamera.setColor(Palette.texteSelectionne);
        paintCamera.setStyle(Paint.Style.STROKE);
        paintCamera.setStrokeWidth(3);
        paintCamera.setAntiAlias(true);
        paintCamera.setPathEffect(new android.graphics.DashPathEffect(new float[]{18f, 12f}, 0f));

        paintObjet = new Paint();
        paintObjet.setAntiAlias(true);
        paintObjet.setFilterBitmap(true);
        paintObjet.setDither(true);

        paintTexte = new Paint();
        paintTexte.setAntiAlias(true);
        paintTexte.setSubpixelText(true);

        paintSelection = new Paint();
        paintSelection.setColor(Palette.texteSelectionne);
        paintSelection.setStyle(Paint.Style.STROKE);
        paintSelection.setAntiAlias(true);
        paintSelection.setStrokeCap(Paint.Cap.ROUND);

        paintPoignee = new Paint();
        paintPoignee.setColor(Palette.boutonSurvol);
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

    public boolean estVisibleEffectif(ObjetBase obj) {
        ObjetBase cur = obj;
        while (cur != null) {
            if (!cur.visible) return false;
            cur = getObjetById(cur.parentId);
        }
        return true;
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
    private float getHauteurReelle(ObjetBase objet) {
        if (!"texte".equals(objet.type)) {
            return objet.hauteur;
        }

        String txt = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
        
        Paint p = new Paint(paintTexte);
        if (objet.cheminPolice != null && cheminProjet != null) {
            android.graphics.Typeface tf = cachePolices.get(objet.cheminPolice);
            p.setTypeface(tf != null ? tf : android.graphics.Typeface.DEFAULT);
        } else {
            p.setTypeface(android.graphics.Typeface.DEFAULT);
        }
        p.setTextSize(objet.tailleFonte);
        p.setTextScaleX(1.0f);

        float hauteurLigne = objet.tailleFonte * 1.2f;
        float totalLines = 0;
        float largeurMax = objet.largeur > 0 ? objet.largeur : 1f;

        String[] paragraphes = txt.split("\n", -1);
        for (String paragraphe : paragraphes) {
            if (paragraphe.isEmpty()) {
                totalLines++;
                continue;
            }
            int start = 0;
            while (start < paragraphe.length()) {
                int count = p.breakText(paragraphe, start, paragraphe.length(), true, largeurMax, null);
                if (count <= 0) count = 1;

                int end = start + count;
                if (end < paragraphe.length()) {
                    int dernierEspace = paragraphe.lastIndexOf(' ', end - 1);
                    if (dernierEspace > start) {
                        end = dernierEspace + 1;
                    }
                }
                totalLines++;
                start = end;
            }
        }
        return totalLines * hauteurLigne;
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
                if (!estVisibleEffectif(objet)) continue; 

                int alphaVal = (int) (objet.alpha * 255);
                if (alphaVal < 0) alphaVal = 0;
                if (alphaVal > 255) alphaVal = 255;
                paintObjet.setAlpha(alphaVal);
                paintTexte.setAlpha(alphaVal);

                Matrix absMatrix = getAbsoluteMatrix(objet);
                
                canvas.save();
                canvas.translate(cameraX, cameraY);
                canvas.concat(absMatrix);
                
                String cheminAAfficher = objet.cheminImage;
                if ("bouton".equals(objet.type) && objet.estDesactive && objet.cheminImageDesactive != null) {
                    cheminAAfficher = objet.cheminImageDesactive;
                }

                if ("rond".equals(objet.type)) {
                    if (objet.afficherFondColore || cheminAAfficher == null) {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        paintObjet.setAlpha(alphaVal);
                        float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                        canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, paintObjet);
                    }
                    dessinerImage(canvas, objet, cheminAAfficher);
                } else if ("texte".equals(objet.type)) {
                    paintTexte.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                    paintTexte.setAlpha(alphaVal);
                    String txt = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                    
                    if (objet.cheminPolice != null && cheminProjet != null) {
                        android.graphics.Typeface tf = cachePolices.get(objet.cheminPolice);
                        if (tf == null) {
                            try {
                                java.io.File fontFile = new java.io.File(cheminProjet, objet.cheminPolice);
                                if (fontFile.exists()) {
                                    tf = android.graphics.Typeface.createFromFile(fontFile);
                                    cachePolices.put(objet.cheminPolice, tf);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        paintTexte.setTypeface(tf != null ? tf : android.graphics.Typeface.DEFAULT);
                    } else {
                        paintTexte.setTypeface(android.graphics.Typeface.DEFAULT);
                    }

                    paintTexte.setTextSize(objet.tailleFonte);
                    paintTexte.setTextScaleX(1.0f);
                    
                    float hauteurLigne = objet.tailleFonte * 1.2f;
                    float currentY = hauteurLigne;
                    float largeurMax = objet.largeur > 0 ? objet.largeur : 1f;
                    
                    String[] paragraphes = txt.split("\n", -1);
                    for (String paragraphe : paragraphes) {
                        if (paragraphe.isEmpty()) {
                            currentY += hauteurLigne;
                            continue;
                        }

                        int start = 0;
                        while (start < paragraphe.length()) {
                            int count = paintTexte.breakText(paragraphe, start, paragraphe.length(), true, largeurMax, null);
                            if (count <= 0) count = 1; 
                            
                            int end = start + count;
                            if (end < paragraphe.length()) {
                                int dernierEspace = paragraphe.lastIndexOf(' ', end - 1);
                                if (dernierEspace > start) {
                                    end = dernierEspace + 1;
                                }
                            }
                            
                            String ligne = paragraphe.substring(start, end);
                            canvas.drawText(ligne, 0, currentY, paintTexte);
                            currentY += hauteurLigne;
                            start = end;
                        }
                    }
                } else if ("image".equals(objet.type)) {
                    if (objet.afficherFondColore) {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        paintObjet.setAlpha(alphaVal);
                        canvas.drawRect(0, 0, objet.largeur, objet.hauteur, paintObjet);
                    }
                    dessinerImage(canvas, objet, cheminAAfficher);
                } else if ("scene_instance".equals(objet.type)) {
                    // NOUVEAU : Dessin spécifique pour un Prefab (Scène Imbriquée)
                    paintObjet.setColor(Color.argb(120, 50, 150, 255)); // Bleu clair semi-transparent
                    paintObjet.setAlpha((int) (0.5f * alphaVal)); 
                    canvas.drawRect(0, 0, objet.largeur, objet.hauteur, paintObjet);
                    
                    paintSelection.setStrokeWidth(2f);
                    canvas.drawRect(0, 0, objet.largeur, objet.hauteur, paintSelection);
                    
                    paintTexte.setColor(Color.WHITE);
                    paintTexte.setAlpha(alphaVal);
                    paintTexte.setTextSize(objet.largeur > 80 ? 14f : 10f);
                    paintTexte.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                    String label = "[ SCÈNE ]";
                    float textWidth = paintTexte.measureText(label);
                    canvas.drawText(label, (objet.largeur - textWidth) / 2f, objet.hauteur / 2f + 5f, paintTexte);
                } else {
                    if (objet.afficherFondColore || cheminAAfficher == null) {
                        paintObjet.setColor(objet.couleur != 0 ? objet.couleur : Color.BLUE);
                        paintObjet.setAlpha(alphaVal);
                        canvas.drawRect(0, 0, objet.largeur, objet.hauteur, paintObjet);
                    }
                    dessinerImage(canvas, objet, cheminAAfficher);
                }

                if (objet == objetSelectionne) {
                    float scaleFactorX = Math.abs(objet.scaleX);
                    if (scaleFactorX < 0.01f) scaleFactorX = 0.01f;
                    
                    float scaleFactorY = Math.abs(objet.scaleY);
                    if (scaleFactorY < 0.01f) scaleFactorY = 0.01f;

                    float maxScale = Math.max(scaleFactorX, scaleFactorY);
                    paintSelection.setStrokeWidth(2f / maxScale);
                    
                    float l = -4f / scaleFactorX;
                    float t = -4f / scaleFactorY;
                    float r = objet.largeur + 4f / scaleFactorX;
                    
                    float objHauteur = getHauteurReelle(objet);
                    float b = objHauteur + 4f / scaleFactorY;
                    
                    canvas.drawRect(l, t, r, b, paintSelection);
                    
                    if (!isModeDeplacementObjet) {
                        float hsX = 12f / scaleFactorX;
                        float hsY = 12f / scaleFactorY;
                        float rcX = 4f / scaleFactorX;
                        float rcY = 4f / scaleFactorY;
                        
                        canvas.drawRoundRect(new android.graphics.RectF(l - hsX, t - hsY, l + hsX, t + hsY), rcX, rcY, paintPoignee);
                        canvas.drawRoundRect(new android.graphics.RectF(r - hsX, t - hsY, r + hsX, t + hsY), rcX, rcY, paintPoignee);
                        canvas.drawRoundRect(new android.graphics.RectF(l - hsX, b - hsY, l + hsX, b + hsY), rcX, rcY, paintPoignee);
                        canvas.drawRoundRect(new android.graphics.RectF(r - hsX, b - hsY, r + hsX, b + hsY), rcX, rcY, paintPoignee);
                        
                        float cx = objet.largeur / 2f;
                        float rotY = t - (50f / scaleFactorY);
                        canvas.drawLine(cx, t, cx, rotY, paintSelection);
                        
                        float scaleFactorAvg = (scaleFactorX + scaleFactorY) / 2f;
                        canvas.drawCircle(cx, rotY, 15f / scaleFactorAvg, paintPoignee);
                    }
                }
                canvas.restore();
            }
        }
        canvas.restore();
    }

    private void dessinerImage(Canvas canvas, ObjetBase objet, String cheminAAfficher) {
        if (cheminAAfficher != null && cheminProjet != null) {
            android.graphics.Bitmap bmp = cacheImages.get(cheminAAfficher);
            if (bmp == null) {
                try {
                    java.io.File imgFile = new java.io.File(cheminProjet, cheminAAfficher);
                    if (imgFile.exists()) {
                        bmp = android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (bmp != null) {
                            cacheImages.put(cheminAAfficher, bmp);
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
            if (!estVisibleEffectif(objet)) continue; 

            float[] localPos = worldToLocal(objet, sx, sy);
            float lx = localPos[0], ly = localPos[1];
            
            float objHauteur = getHauteurReelle(objet);
            
            if (lx >= 0 && lx <= objet.largeur && ly >= 0 && ly <= objHauteur) {
                return objet;
            }
        }
        return null;
    }
// bas 2
    // haut 3
    private int getTouchTarget(float xEcran, float yEcran) {
        float[] scenePos = ecranVersScene(xEcran, yEcran);
        float sx = scenePos[0], sy = scenePos[1];
        
        if (objetSelectionne != null) {
            float[] pts = worldToLocal(objetSelectionne, sx, sy);
            float lx = pts[0], ly = pts[1];
            
            float scaleX = Math.abs(objetSelectionne.scaleX);
            if (scaleX < 0.01f) scaleX = 0.01f;
            float scaleY = Math.abs(objetSelectionne.scaleY);
            if (scaleY < 0.01f) scaleY = 0.01f;
            
            float hitX = (30f / niveauZoom) / scaleX;
            float hitY = (30f / niveauZoom) / scaleY;
            float hitAvg = (hitX + hitY) / 2f;
            
            float midX = objetSelectionne.largeur / 2f;
            float rotY = -50f / scaleY;
            float objHauteur = getHauteurReelle(objetSelectionne);

            if (!isModeDeplacementObjet) {
                if (Math.hypot(lx - midX, ly - rotY) < hitAvg) return 8; 
                
                if (Math.abs(lx) < hitX && Math.abs(ly) < hitY) return 4; 
                if (Math.abs(lx - objetSelectionne.largeur) < hitX && Math.abs(ly) < hitY) return 5; 
                if (Math.abs(lx) < hitX && Math.abs(ly - objHauteur) < hitY) return 6; 
                if (Math.abs(lx - objetSelectionne.largeur) < hitX && Math.abs(ly - objHauteur) < hitY) return 7; 
            }
            
            if (lx >= 0 && lx <= objetSelectionne.largeur && ly >= 0 && ly <= objHauteur) return 2; 
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
        scaleGestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPanMode) {
                    currentMode = 1;
                } else {
                    currentMode = getTouchTarget(x, y);
                    
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
                    if (!objetSelectionne.estVerrouille) {
                        Matrix invParent = new Matrix();
                        getParentMatrix(objetSelectionne).invert(invParent);
                        
                        float[] curTouchP = {sx, sy};
                        float[] lastTouchP = {ecranVersScene(lastTouchX, lastTouchY)[0], ecranVersScene(lastTouchX, lastTouchY)[1]};
                        
                        invParent.mapPoints(curTouchP); invParent.mapPoints(lastTouchP);
                        objetSelectionne.x += (curTouchP[0] - lastTouchP[0]);
                        objetSelectionne.y += (curTouchP[1] - lastTouchP[1]);
                    }
                } else if (currentMode >= 4 && currentMode <= 7 && objetSelectionne != null) { 
                    if (!objetSelectionne.estVerrouille) {
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
                    }
                } else if (currentMode == 8 && objetSelectionne != null) { 
                    if (!objetSelectionne.estVerrouille) {
                        float[] centerWorld = {objetSelectionne.largeur / 2f, objetSelectionne.hauteur / 2f};
                        getAbsoluteMatrix(objetSelectionne).mapPoints(centerWorld);
                        
                        double angleWorld = Math.toDegrees(Math.atan2(sy - centerWorld[1], sx - centerWorld[0]));
                        float parentRot = getAbsoluteRotation(getObjetById(objetSelectionne.parentId));
                        objetSelectionne.rotation = (float) (angleWorld + 90) - parentRot;
                    }
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            niveauZoom *= detector.getScaleFactor();
            niveauZoom = Math.max(0.2f, Math.min(niveauZoom, 5.0f));
            invalidate();
            return true;
        }
    }

    public void sauvegarderVignette(String cheminVignette) {
        try {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return; 

            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas tempCanvas = new android.graphics.Canvas(bitmap);

            ObjetBase selectionMemoire = this.objetSelectionne;
            this.objetSelectionne = null;
            
            this.draw(tempCanvas);
            
            this.objetSelectionne = selectionMemoire;

            float maxSize = 300f;
            float scale = Math.min(maxSize / w, maxSize / h);
            int newW = Math.max(1, Math.round(w * scale));
            int newH = Math.max(1, Math.round(h * scale));
            
            android.graphics.Bitmap vignette = android.graphics.Bitmap.createScaledBitmap(bitmap, newW, newH, true);

            java.io.File file = new java.io.File(cheminVignette);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            vignette.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();

            bitmap.recycle();
            vignette.recycle();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// bas 3
