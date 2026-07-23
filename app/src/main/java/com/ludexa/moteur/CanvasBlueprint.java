// haut 1
package com.ludexa.moteur;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        
        setBackgroundColor(Color.parseColor("#1E1E1E")); 

        // Gestion du Drag & Drop pour recevoir les nœuds depuis PanneauNoeuds
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
                        
                        // Conversion écran -> scène
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
