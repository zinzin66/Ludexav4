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
    private Paint paintLien; 
    
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private float niveauZoom = 1.0f;
    
    private Blueprint blueprintActuel;

    private NoeudBase noeudEnDeplacement = null;
    private float decalageToucherX = 0;
    private float decalageToucherY = 0;

    private Port portDepartDrag = null;
    private NoeudBase noeudDepartDrag = null;
    private float dragCurrentX = 0;
    private float dragCurrentY = 0;

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
                        
                        // Conversion robuste des coordonnées de l'écran vers le monde virtuel
                        float x = ((screenX - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                        float y = ((screenY - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                        
                        NoeudBase nouveauNoeud = null;
                        
                        // Instanciation explicite sécurisée (remplace la réflexion défaillante)
                        if ("NoeudEventStart".equals(typeNoeud)) {
                            nouveauNoeud = new NoeudEventStart();
                        } else if ("NoeudActionDeplacer".equals(typeNoeud)) {
                            nouveauNoeud = new NoeudActionDeplacer();
                        }
                        
                        if (nouveauNoeud != null) {
                            blueprintActuel.ajouterNoeud(nouveauNoeud, x, y);
                            invalidate(); // Force le redessin immédiat
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

            if (portDepartDrag != null && noeudDepartDrag != null) {
                float[] coordS = getCoordonneesPort(noeudDepartDrag, portDepartDrag);
                if (coordS != null) {
                    dessinerCourbe(canvas, path, coordS[0], coordS[1], dragCurrentX, dragCurrentY, portDepartDrag);
                }
            }

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
        float dist = Math.abs(x2 - x1) / 2f;
        dist = Math.max(dist, 60f); 
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
// haut 3
    private void dessinerNoeud(Canvas canvas, NoeudBase noeud) {
        Float xObj = blueprintActuel.noeudsX.get(noeud.id);
        Float yObj = blueprintActuel.noeudsY.get(noeud.id);
        if (xObj == null || yObj == null) return;
        
        float x = xObj;
        float y = yObj;
        float largeur = 260;
        int maxPorts = Math.max(noeud.portsEntree.size(), noeud.portsSortie.size());
        float hauteur = 60 + (maxPorts * 40) + 20; 
        
        RectF rectFond = new RectF(x, y, x + largeur, y + hauteur);
        canvas.drawRoundRect(rectFond, 16, 16, paintNoeudBG);
        
        RectF rectTitre = new RectF(x, y, x + largeur, y + 45);
        canvas.drawRoundRect(rectTitre, 16, 16, paintTitreBG);
        canvas.drawRect(x, y + 25, x + largeur, y + 45, paintTitreBG);
        
        canvas.drawText(noeud.nom, x + 15, y + 32, paintTexteTitre);
        
        float startY = y + 70;
        for (int i = 0; i < noeud.portsEntree.size(); i++) {
            Port p = noeud.portsEntree.get(i);
            definirCouleurPort(p);
            float portY = startY + (i * 40);
            canvas.drawCircle(x, portY, 8, paintPort);
            canvas.drawText(p.nom, x + 20, portY + 6, paintTextePort);
        }
        
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
            paintPort.setColor(Color.parseColor("#44AAFF")); 
        }
    }

    private InfoPort trouverPortSousToucher(float sceneX, float sceneY) {
        if (blueprintActuel == null) return null;
        float margeY = 40f; 

        for (int i = blueprintActuel.noeuds.size() - 1; i >= 0; i--) {
            NoeudBase noeud = blueprintActuel.noeuds.get(i);
            Float nx = blueprintActuel.noeudsX.get(noeud.id);
            Float ny = blueprintActuel.noeudsY.get(noeud.id);
            if (nx == null || ny == null) continue;

            float startY = ny + 70;
            float largeur = 260;

            for (int j = 0; j < noeud.portsEntree.size(); j++) {
                float py = startY + (j * 40);
                if (sceneX >= nx - 40 && sceneX <= nx + 140 && Math.abs(sceneY - py) <= margeY) {
                    return new InfoPort(noeud, noeud.portsEntree.get(j), true);
                }
            }
            for (int j = 0; j < noeud.portsSortie.size(); j++) {
                float py = startY + (j * 40);
                if (sceneX >= nx + largeur - 140 && sceneX <= nx + largeur + 40 && Math.abs(sceneY - py) <= margeY) {
                    return new InfoPort(noeud, noeud.portsSortie.get(j), false);
                }
            }
        }
        return null;
    }

    private NoeudBase trouverNoeudSousToucher(float sceneX, float sceneY) {
        if (blueprintActuel == null) return null;
        for (int i = blueprintActuel.noeuds.size() - 1; i >= 0; i--) {
            NoeudBase noeud = blueprintActuel.noeuds.get(i);
            Float nx = blueprintActuel.noeudsX.get(noeud.id);
            Float ny = blueprintActuel.noeudsY.get(noeud.id);
            
            if (nx != null && ny != null) {
                float largeur = 260;
                int maxPorts = Math.max(noeud.portsEntree.size(), noeud.portsSortie.size());
                float hauteur = 60 + (maxPorts * 40) + 20;
                
                if (sceneX >= nx && sceneX <= nx + largeur && sceneY >= ny && sceneY <= ny + hauteur) {
                    return noeud;
                }
            }
        }
        return null;
    }
// bas 3

// haut 4
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                
                if (blueprintActuel != null) {
                    float sceneX = ((x - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                    float sceneY = ((y - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                    
                    InfoPort portTouche = trouverPortSousToucher(sceneX, sceneY);
                    if (portTouche != null) {
                        if (!portTouche.isEntree) { 
                            portDepartDrag = portTouche.port;
                            noeudDepartDrag = portTouche.noeud;
                            dragCurrentX = sceneX;
                            dragCurrentY = sceneY;
                            invalidate(); 
                            return true;
                        }
                    }

                    noeudEnDeplacement = trouverNoeudSousToucher(sceneX, sceneY);
                    if (noeudEnDeplacement != null) {
                        decalageToucherX = sceneX - blueprintActuel.noeudsX.get(noeudEnDeplacement.id);
                        decalageToucherY = sceneY - blueprintActuel.noeudsY.get(noeudEnDeplacement.id);
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (portDepartDrag != null) {
                    dragCurrentX = ((x - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                    dragCurrentY = ((y - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                    invalidate();
                    return true;
                } else if (noeudEnDeplacement != null && blueprintActuel != null) {
                    float sceneX = ((x - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                    float sceneY = ((y - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                    blueprintActuel.noeudsX.put(noeudEnDeplacement.id, sceneX - decalageToucherX);
                    blueprintActuel.noeudsY.put(noeudEnDeplacement.id, sceneY - decalageToucherY);
                    invalidate();
                } else {
                    cameraX += (x - lastTouchX) / niveauZoom;
                    cameraY += (y - lastTouchY) / niveauZoom;
                    lastTouchX = x;
                    lastTouchY = y;
                    invalidate(); 
                }
                return true;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (portDepartDrag != null) {
                    float sceneX = ((x - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                    float sceneY = ((y - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                    InfoPort portArrivee = trouverPortSousToucher(sceneX, sceneY);
                    
                    if (portArrivee != null && portArrivee.isEntree && portArrivee.noeud != noeudDepartDrag) {
                        boolean isCompatEx = Port.TYPE_EXECUTION_SORTIE.equals(portDepartDrag.type) && Port.TYPE_EXECUTION_ENTREE.equals(portArrivee.port.type);
                        boolean isCompatDonnee = Port.TYPE_DONNEE_SORTIE.equals(portDepartDrag.type) && Port.TYPE_DONNEE_ENTREE.equals(portArrivee.port.type);
                        
                        if (isCompatEx || isCompatDonnee) {
                            for (int i = 0; i < blueprintActuel.liens.size(); i++) {
                                Blueprint.Lien l = blueprintActuel.liens.get(i);
                                if (l.noeudDepart == noeudDepartDrag && l.portSortieNom.equals(portDepartDrag.nom)) {
                                    blueprintActuel.liens.remove(i);
                                    break;
                                }
                            }
                            blueprintActuel.liens.add(new Blueprint.Lien(
                                noeudDepartDrag, portDepartDrag.nom,
                                portArrivee.noeud, portArrivee.port.nom
                            ));
                            noeudDepartDrag.connecterPort(portDepartDrag.nom, portArrivee.noeud, portArrivee.port.nom);
                        }
                    }
                    portDepartDrag = null;
                    noeudDepartDrag = null;
                    invalidate();
                    return true;
                }
                noeudEnDeplacement = null;
                return true;
        }
        return super.onTouchEvent(event);
    }
}
// bas 4
