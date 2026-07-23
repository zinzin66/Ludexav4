// haut 1
package com.ludexa.moteur;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

public class CanvasBlueprint extends View {
    private Paint paintGrille;
    private Paint paintNoeudBG;
    private Paint paintTitreBG;
    private Paint paintTexteTitre;
    private Paint paintTextePort;
    private Paint paintPort;
    private Paint paintLien; // Nouvel outil de dessin pour les liens
    
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private float niveauZoom = 1.0f;
    
    private Blueprint blueprintActuel;

    // Variables pour le déplacement d'un nœud
    private NoeudBase noeudEnDeplacement = null;
    private float decalageToucherX = 0;
    private float decalageToucherY = 0;

    // Variables pour le tracé d'un lien (Tâche 8.5)
    private Port portDepartDrag = null;
    private NoeudBase noeudDepartDrag = null;
    private float dragCurrentX = 0;
    private float dragCurrentY = 0;

    // Classe utilitaire pour la détection tactile des ports
    private class InfoPort {
        NoeudBase noeud;
        Port port;
        boolean isEntree;
        InfoPort(NoeudBase n, Port p, boolean e) { 
            this.noeud = n; this.port = p; this.isEntree = e; 
        }
    }

    public CanvasBlueprint(Context context) {
        super(context);
        init();
    }

    private void init() {
        paintGrille = new Paint();
        paintGrille.setColor(Color.DKGRAY);
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
        
        paintLien = new Paint();
        paintLien.setStyle(Paint.Style.STROKE);
        paintLien.setStrokeWidth(6);
        paintLien.setAntiAlias(true);
        
        setBackgroundColor(Color.parseColor("#1E1E1E")); 

        this.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_LOCATION:
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String typeNoeud = item.getText().toString();
                    
                    if (blueprintActuel != null) {
                        float screenX = event.getX();
                        float screenY = event.getY();
                        
                        float x = ((screenX - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                        float y = ((screenY - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                        
                        NoeudBase nouveauNoeud = null;
                        if ("NoeudEventStart".equals(typeNoeud)) {
                            nouveauNoeud = new NoeudEventStart();
                        } else if ("NoeudActionDeplacer".equals(typeNoeud)) {
                            try {
                                nouveauNoeud = (NoeudBase) Class.forName("com.ludexa.moteur.NoeudActionDeplacer").newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        
                        if (nouveauNoeud != null) {
                            blueprintActuel.ajouterNoeud(nouveauNoeud, x, y);
                            invalidate();
                        }
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                default:
                    return false;
            }
        });
    }
// bas 1
    // haut 2
    public void setBlueprint(Blueprint blueprint) {
        this.blueprintActuel = blueprint;
        invalidate();
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

        int gridSize = 150; 
        int w = getWidth();
        int h = getHeight();
        int limiteMax = (int) (Math.max(w, h) * 2 / niveauZoom);

        for (int i = -limiteMax + (int)(cameraX % gridSize); i < limiteMax; i += gridSize) {
            canvas.drawLine(i, -limiteMax, i, limiteMax, paintGrille);
        }
        for (int i = -limiteMax + (int)(cameraY % gridSize); i < limiteMax; i += gridSize) {
            canvas.drawLine(-limiteMax, i, limiteMax, i, paintGrille);
        }
        
        canvas.translate(cameraX, cameraY);
        
        if (blueprintActuel != null) {
            // 1. Dessiner les liens enregistrés (Tâche 8.5)
            Path path = new Path();
            for (Blueprint.Lien lien : blueprintActuel.liens) {
                Port pSortie = trouverPortParNom(lien.noeudDepart.portsSortie, lien.portSortieNom);
                Port pEntree = trouverPortParNom(lien.noeudArrivee.portsEntree, lien.portEntreeNom);
                
                if (pSortie != null && pEntree != null) {
                    float[] coordS = getCoordonneesPort(lien.noeudDepart, pSortie);
                    float[] coordE = getCoordonneesPort(lien.noeudArrivee, pEntree);
                    if (coordS != null && coordE != null) {
                        dessinerCourbe(canvas, path, coordS[0], coordS[1], coordE[0], coordE[1], pSortie);
                    }
                }
            }

            // 2. Dessiner le lien en cours de création / drag (Tâche 8.5)
            if (portDepartDrag != null && noeudDepartDrag != null) {
                float[] coordS = getCoordonneesPort(noeudDepartDrag, portDepartDrag);
                if (coordS != null) {
                    dessinerCourbe(canvas, path, coordS[0], coordS[1], dragCurrentX, dragCurrentY, portDepartDrag);
                }
            }

            // 3. Dessiner les nœuds par dessus les liens
            for (NoeudBase noeud : blueprintActuel.noeuds) {
                dessinerNoeud(canvas, noeud);
            }
        }
        
        canvas.restore();
    }

    private Port trouverPortParNom(java.util.ArrayList<Port> ports, String nom) {
        for (Port p : ports) {
            if (p.nom.equals(nom)) return p;
        }
        return null;
    }

    private void dessinerCourbe(Canvas canvas, Path path, float x1, float y1, float x2, float y2, Port portType) {
        path.reset();
        path.moveTo(x1, y1);
        // Courbe de Bézier pour un effet naturel de câble
        float dist = Math.abs(x2 - x1) / 2f;
        dist = Math.max(dist, 60f); // Courbure minimum
        path.cubicTo(x1 + dist, y1, x2 - dist, y2, x2, y2);
        
        if (Port.TYPE_EXECUTION_ENTREE.equals(portType.type) || Port.TYPE_EXECUTION_SORTIE.equals(portType.type)) {
            paintLien.setColor(Color.WHITE);
        } else {
            paintLien.setColor(Color.parseColor("#44AAFF"));
        }
        canvas.drawPath(path, paintLien);
    }

    private float[] getCoordonneesPort(NoeudBase noeud, Port port) {
        Float xObj = blueprintActuel.noeudsX.get(noeud.id);
        Float yObj = blueprintActuel.noeudsY.get(noeud.id);
        if (xObj == null || yObj == null) return null;

        float x = xObj;
        float y = yObj;
        float startY = y + 70;
        float largeur = 260;

        for (int i = 0; i < noeud.portsEntree.size(); i++) {
            if (noeud.portsEntree.get(i).nom.equals(port.nom)) {
                return new float[]{x, startY + (i * 40)};
            }
        }
        for (int i = 0; i < noeud.portsSortie.size(); i++) {
            if (noeud.portsSortie.get(i).nom.equals(port.nom)) {
                return new float[]{x + largeur, startY + (i * 40)};
            }
        }
        return null;
    }
// bas 2
    

