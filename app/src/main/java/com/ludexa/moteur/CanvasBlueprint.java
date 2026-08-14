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
import android.widget.Toast;

public class CanvasBlueprint extends View {
    private Paint paintGrille;
    private Paint paintNoeudBG;
    private Paint paintTitreBG;
    private Paint paintTexteTitre;
    private Paint paintTextePort;
    private Paint paintPort;
    private Paint paintLien; 
    
    private Paint paintSelection;
    private Paint paintBoutonEdition;
    private Paint paintTexteBouton;
    private Paint paintResume;
    
    private float cameraX = 0, cameraY = 0;
    private float lastTouchX, lastTouchY;
    private float niveauZoom = 1.0f;
    
    public Scene sceneActive; 
    private long lastDownTime = 0;
    private float touchDownX = 0;
    private float touchDownY = 0;
    
    private Blueprint blueprintActuel;

    private NoeudBase noeudEnDeplacement = null;
    private float decalageToucherX = 0;
    private float decalageToucherY = 0;
    
    private NoeudBase noeudSelectionne = null;

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
        paintGrille.setColor(Palette.canvasGrille);
        paintGrille.setStrokeWidth(1.5f);
        paintGrille.setAntiAlias(true);
        
        paintNoeudBG = new Paint();
        paintNoeudBG.setColor(Palette.fondPanneaux);
        paintNoeudBG.setStyle(Paint.Style.FILL);
        
        paintTitreBG = new Paint();
        paintTitreBG.setColor(Palette.enTeteDialogues);
        paintTitreBG.setStyle(Paint.Style.FILL);
        
        paintTexteTitre = new Paint();
        paintTexteTitre.setColor(Palette.texteNormal);
        paintTexteTitre.setTextSize(26);
        paintTexteTitre.setFakeBoldText(true);
        paintTexteTitre.setAntiAlias(true);
        
        paintTextePort = new Paint();
        paintTextePort.setColor(Palette.texteNormal);
        paintTextePort.setTextSize(19);
        paintTextePort.setAntiAlias(true);
        
        paintPort = new Paint();
        paintPort.setStyle(Paint.Style.FILL);
        paintPort.setAntiAlias(true);
        
        paintLien = new Paint();
        paintLien.setStyle(Paint.Style.STROKE);
        paintLien.setStrokeWidth(5);
        paintLien.setStrokeCap(Paint.Cap.ROUND);
        paintLien.setStrokeJoin(Paint.Join.ROUND);
        paintLien.setAntiAlias(true);
        
        paintSelection = new Paint();
        paintSelection.setColor(Palette.texteSelectionne);
        paintSelection.setStyle(Paint.Style.STROKE);
        paintSelection.setStrokeWidth(4);
        paintSelection.setAntiAlias(true);

        paintBoutonEdition = new Paint();
        paintBoutonEdition.setColor(Palette.boutonNormal);
        paintBoutonEdition.setStyle(Paint.Style.FILL);
        
        paintTexteBouton = new Paint();
        paintTexteBouton.setColor(Palette.texteNormal);
        paintTexteBouton.setTextSize(18);
        paintTexteBouton.setFakeBoldText(true);
        paintTexteBouton.setTextAlign(Paint.Align.CENTER);
        paintTexteBouton.setAntiAlias(true);
        
        paintResume = new Paint();
        paintResume.setColor(Palette.texteSelectionne);
        paintResume.setTextSize(17);
        paintResume.setAntiAlias(true);
        
        setBackgroundColor(Palette.canvasFond); 
// bas 1
// haut 2
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
                        
                        try {
                            Class<?> clazz = Class.forName("com.ludexa.moteur." + typeNoeud);
                            nouveauNoeud = (NoeudBase) clazz.newInstance();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Erreur création noeud : " + typeNoeud, Toast.LENGTH_LONG).show();
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

    public void setBlueprint(Blueprint blueprint) {
        this.blueprintActuel = blueprint;
        noeudSelectionne = null;
        invalidate();
    }

    public void zoomPlus() { niveauZoom *= 1.25f; invalidate(); }
    public void zoomMoins() { niveauZoom /= 1.25f; invalidate(); }
    public void zoomReset() { niveauZoom = 1.0f; invalidate(); }
    
    public void supprimerNoeudSelectionne() {
        if (noeudSelectionne != null && blueprintActuel != null) {
            blueprintActuel.liens.removeIf(l -> 
                l.noeudDepart == noeudSelectionne || l.noeudArrivee == noeudSelectionne
            );
            
            for (NoeudBase noeud : blueprintActuel.noeuds) {
                for (Port p : noeud.portsSortie) {
                    if (p.noeudDestination == noeudSelectionne) {
                        p.noeudDestination = null;
                        p.portDestination = null;
                    }
                }
            }
            
            blueprintActuel.noeuds.remove(noeudSelectionne);
            blueprintActuel.noeudsX.remove(noeudSelectionne.id);
            blueprintActuel.noeudsY.remove(noeudSelectionne.id);
            
            noeudSelectionne = null;
            invalidate();
        }
    }

    public void dupliquerNoeudSelectionne() {
        if (noeudSelectionne != null && blueprintActuel != null) {
            try {
                Class<?> clazz = noeudSelectionne.getClass();
                NoeudBase nouveauNoeud = (NoeudBase) clazz.newInstance();

                nouveauNoeud.nom = noeudSelectionne.nom;

                if (noeudSelectionne.requiertCibleObjet()) nouveauNoeud.setCibleObjet(noeudSelectionne.getCibleObjet());
                if (noeudSelectionne.requiertCibleObjetB()) nouveauNoeud.setCibleObjetB(noeudSelectionne.getCibleObjetB());
                if (noeudSelectionne.requiertCibleVariable()) nouveauNoeud.setCibleVariable(noeudSelectionne.getCibleVariable());
                if (noeudSelectionne.requiertCibleScene()) nouveauNoeud.setCibleScene(noeudSelectionne.getCibleScene());

                if (noeudSelectionne.getNomsParametres() != null) {
                    for (String param : noeudSelectionne.getNomsParametres()) {
                        nouveauNoeud.setValeurParametre(param, noeudSelectionne.getValeurParametre(param));
                    }
                }

                Float xObj = blueprintActuel.noeudsX.get(noeudSelectionne.id);
                Float yObj = blueprintActuel.noeudsY.get(noeudSelectionne.id);
                float newX = (xObj != null ? xObj : 0) + 50f;
                float newY = (yObj != null ? yObj : 0) + 50f;

                blueprintActuel.ajouterNoeud(nouveauNoeud, newX, newY);
                
                noeudSelectionne = nouveauNoeud;
                invalidate();
                
                Toast.makeText(getContext(), "Nœud copié", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Erreur lors de la duplication", Toast.LENGTH_LONG).show();
            }
        }
    }
// bas 2
// haut 3
    public void basculerRepliNoeudSelectionne() {
        if (noeudSelectionne != null) {
            noeudSelectionne.estReplie = !noeudSelectionne.estReplie;
            invalidate();
            Toast.makeText(getContext(), noeudSelectionne.estReplie ? "Nœuds masqués" : "Nœuds affichés", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Sélectionnez un nœud d'abord", Toast.LENGTH_SHORT).show();
        }
    }

    private java.util.Set<String> getNoeudsMasques() {
        java.util.Set<String> masques = new java.util.HashSet<>();
        if (blueprintActuel == null) return masques;

        java.util.List<NoeudBase> aTraiter = new java.util.ArrayList<>();
        for (NoeudBase n : blueprintActuel.noeuds) {
            if (n.estReplie) {
                for (Blueprint.Lien lien : blueprintActuel.liens) {
                    if (lien.noeudDepart == n) {
                        aTraiter.add(lien.noeudArrivee);
                    }
                }
            }
        }

        int index = 0;
        while (index < aTraiter.size()) {
            NoeudBase courant = aTraiter.get(index);
            if (!masques.contains(courant.id)) {
                masques.add(courant.id);
                for (Blueprint.Lien lien : blueprintActuel.liens) {
                    if (lien.noeudDepart == courant) {
                        aTraiter.add(lien.noeudArrivee);
                    }
                }
            }
            index++;
        }
        return masques;
    }

    public NoeudBase getNoeudSelectionne() {
        return noeudSelectionne;
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
        
        java.util.Set<String> noeudsMasques = getNoeudsMasques();

        if (blueprintActuel != null) {
            Path path = new Path();
            for (Blueprint.Lien lien : blueprintActuel.liens) {
                if (noeudsMasques.contains(lien.noeudArrivee.id)) continue;

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
                if (noeudsMasques.contains(noeud.id)) continue;
                dessinerNoeud(canvas, noeud);
            }
        }
        
        canvas.restore();
    }
// bas 3
// haut 4
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
            paintLien.setColor(Palette.texteNormal);
        } else {
            paintLien.setColor(Palette.texteSelectionne);
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
        float largeur = getLargeurNoeud(noeud);

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

    private float calculerHauteurResume(NoeudBase noeud) {
        int count = 0;
        if (noeud.requiertCibleObjet()) count++;
        if (noeud.requiertCibleObjetB()) count++;
        if (noeud.requiertCibleVariable()) count++;
        if (noeud.requiertCibleScene()) count++;
        if (noeud.getNomsParametres() != null) count += noeud.getNomsParametres().size();
        return count * 28f;
    }

    private float calculerHauteurBase(NoeudBase noeud) {
        int maxPorts = Math.max(noeud.portsEntree.size(), noeud.portsSortie.size());
        return 60 + (maxPorts * 40) + 20 + calculerHauteurResume(noeud);
    }

    private float calculerHauteurTotale(NoeudBase noeud) {
        return calculerHauteurBase(noeud) + (noeud.aDesParametresEditables() ? 50 : 0);
    }
    
    private float getLargeurNoeud(NoeudBase noeud) {
        float max = 220f;
        
        String texteTitre = noeud.nom + (noeud.estReplie ? " [...]" : "");
        float wTitre = paintTexteTitre.measureText(texteTitre) + 40f;
        if (wTitre > max) max = wTitre;
        
        int maxPorts = Math.max(noeud.portsEntree.size(), noeud.portsSortie.size());
        for (int i = 0; i < maxPorts; i++) {
            float rowWidth = 60f; 
            if (i < noeud.portsEntree.size()) {
                rowWidth += paintTextePort.measureText(noeud.portsEntree.get(i).nom);
            }
            if (i < noeud.portsSortie.size()) {
                rowWidth += paintTextePort.measureText(noeud.portsSortie.get(i).nom);
            }
            if (rowWidth > max) max = rowWidth;
        }

        if (noeud.requiertCibleObjet()) {
            String nom = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "Aucune";
            float w = paintResume.measureText("Objet : " + nom) + 30f;
            if (w > max) max = w;
        }
        if (noeud.requiertCibleObjetB()) {
            String nom = (noeud.getCibleObjetB() != null && noeud.getCibleObjetB().nom != null) ? noeud.getCibleObjetB().nom : "Aucune";
            float w = paintResume.measureText("Objet B : " + nom) + 30f;
            if (w > max) max = w;
        }
        if (noeud.requiertCibleVariable()) {
            String nom = (noeud.getCibleVariable() != null && noeud.getCibleVariable().nom != null) ? noeud.getCibleVariable().nom : "Aucune";
            float w = paintResume.measureText("Variable : " + nom) + 30f;
            if (w > max) max = w;
        }
        if (noeud.requiertCibleScene()) {
            String nom = (noeud.getCibleScene() != null && noeud.getCibleScene().nom != null) ? noeud.getCibleScene().nom : "Aucune";
            float w = paintResume.measureText("Scène : " + nom) + 30f;
            if (w > max) max = w;
        }
        if (noeud.getNomsParametres() != null) {
            for (String param : noeud.getNomsParametres()) {
                String val = noeud.getValeurParametre(param);
                if (val == null) val = "";
                String ligne = param + " : " + val;
                float w = paintResume.measureText(ligne) + 30f;
                if (w > max) max = w;
            }
        }
        
        return max;
    }
// bas 4
// haut 5
    private void dessinerNoeud(Canvas canvas, NoeudBase noeud) {
        Float xObj = blueprintActuel.noeudsX.get(noeud.id);
        Float yObj = blueprintActuel.noeudsY.get(noeud.id);
        if (xObj == null || yObj == null) return;
        
        float x = xObj;
        float y = yObj;
        float largeur = getLargeurNoeud(noeud);
        int maxPorts = Math.max(noeud.portsEntree.size(), noeud.portsSortie.size());
        
        boolean estEditable = noeud.aDesParametresEditables();
        float hauteurBase = calculerHauteurBase(noeud);
        float hauteurTotale = calculerHauteurTotale(noeud);
        
        if (noeud == noeudSelectionne) {
            RectF rectSelection = new RectF(x - 4, y - 4, x + largeur + 4, y + hauteurTotale + 4);
            canvas.drawRoundRect(rectSelection, 22, 22, paintSelection);
        }
        
        RectF rectFond = new RectF(x, y, x + largeur, y + hauteurTotale);
        canvas.drawRoundRect(rectFond, 20, 20, paintNoeudBG);
        
        RectF rectTitre = new RectF(x, y, x + largeur, y + 45);
        paintTitreBG.setColor(noeud.estReplie ? Palette.fondListe : Palette.enTeteDialogues);
        canvas.drawRoundRect(rectTitre, 20, 20, paintTitreBG);
        canvas.drawRect(x, y + 25, x + largeur, y + 45, paintTitreBG);
        paintTitreBG.setColor(Palette.enTeteDialogues); 
        
        String texteTitre = noeud.nom + (noeud.estReplie ? " [...]" : "");
        canvas.drawText(texteTitre, x + 15, y + 32, paintTexteTitre);
        
        float startY = y + 70;
        for (int i = 0; i < noeud.portsEntree.size(); i++) {
            Port p = noeud.portsEntree.get(i);
            definirCouleurPort(p);
            float portY = startY + (i * 40);
            canvas.drawCircle(x, portY, 9, paintPort);
            canvas.drawText(p.nom, x + 20, portY + 6, paintTextePort);
        }
        
        for (int i = 0; i < noeud.portsSortie.size(); i++) {
            Port p = noeud.portsSortie.get(i);
            definirCouleurPort(p);
            float portY = startY + (i * 40);
            canvas.drawCircle(x + largeur, portY, 9, paintPort);
            float textWidth = paintTextePort.measureText(p.nom);
            canvas.drawText(p.nom, x + largeur - 20 - textWidth, portY + 6, paintTextePort);
        }
        
        float currentY = y + 60 + (maxPorts * 40) + 15;
        
        if (noeud.requiertCibleObjet()) {
            String nom = (noeud.getCibleObjet() != null && noeud.getCibleObjet().nom != null) ? noeud.getCibleObjet().nom : "Aucune";
            canvas.drawText("Objet : " + nom, x + 15, currentY, paintResume);
            currentY += 28;
        }
        if (noeud.requiertCibleObjetB()) {
            String nom = (noeud.getCibleObjetB() != null && noeud.getCibleObjetB().nom != null) ? noeud.getCibleObjetB().nom : "Aucune";
            canvas.drawText("Objet B : " + nom, x + 15, currentY, paintResume);
            currentY += 28;
        }
        if (noeud.requiertCibleVariable()) {
            String nom = (noeud.getCibleVariable() != null && noeud.getCibleVariable().nom != null) ? noeud.getCibleVariable().nom : "Aucune";
            canvas.drawText("Variable : " + nom, x + 15, currentY, paintResume);
            currentY += 28;
        }
        if (noeud.requiertCibleScene()) {
            String nom = (noeud.getCibleScene() != null && noeud.getCibleScene().nom != null) ? noeud.getCibleScene().nom : "Aucune";
            canvas.drawText("Scène : " + nom, x + 15, currentY, paintResume);
            currentY += 28;
        }
        if (noeud.getNomsParametres() != null) {
            for (String param : noeud.getNomsParametres()) {
                String val = noeud.getValeurParametre(param);
                if (val == null) val = "";
                String ligne = param + " : " + val;
                canvas.drawText(ligne, x + 15, currentY, paintResume);
                currentY += 28;
            }
        }

        if (estEditable) {
            float btnY = y + hauteurBase;
            RectF rectBouton = new RectF(x + 10, btnY, x + largeur - 10, btnY + 40);
            canvas.drawRoundRect(rectBouton, 12, 12, paintBoutonEdition);
            canvas.drawText("📝 Configurer", x + largeur / 2f, btnY + 26, paintTexteBouton);
        }
    }
    
    private void definirCouleurPort(Port p) {
        if (Port.TYPE_EXECUTION_ENTREE.equals(p.type) || Port.TYPE_EXECUTION_SORTIE.equals(p.type)) {
            paintPort.setColor(Palette.texteNormal);
        } else {
            paintPort.setColor(Palette.texteSelectionne); 
        }
    }
// bas 5



    // haut 6
    private InfoPort trouverPortSousToucher(float sceneX, float sceneY) {
        if (blueprintActuel == null) return null;
        float margeY = 40f; 
        java.util.Set<String> masques = getNoeudsMasques();

        for (int i = blueprintActuel.noeuds.size() - 1; i >= 0; i--) {
            NoeudBase noeud = blueprintActuel.noeuds.get(i);
            if (masques.contains(noeud.id)) continue;
            
            Float nx = blueprintActuel.noeudsX.get(noeud.id);
            Float ny = blueprintActuel.noeudsY.get(noeud.id);
            if (nx == null || ny == null) continue;

            float startY = ny + 70;
            float largeur = getLargeurNoeud(noeud);

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
    
    private NoeudBase trouverZoneEditionSousToucher(float sceneX, float sceneY) {
        if (blueprintActuel == null) return null;
        java.util.Set<String> masques = getNoeudsMasques();
        for (int i = blueprintActuel.noeuds.size() - 1; i >= 0; i--) {
            NoeudBase noeud = blueprintActuel.noeuds.get(i);
            if (masques.contains(noeud.id) || !noeud.aDesParametresEditables()) continue;
            
            Float nx = blueprintActuel.noeudsX.get(noeud.id);
            Float ny = blueprintActuel.noeudsY.get(noeud.id);
            
            if (nx != null && ny != null) {
                float largeur = getLargeurNoeud(noeud);
                float hauteurBase = calculerHauteurBase(noeud);
                float btnY = ny + hauteurBase;
                
                if (sceneX >= nx + 10 && sceneX <= nx + largeur - 10 && sceneY >= btnY && sceneY <= btnY + 40) {
                    return noeud;
                }
            }
        }
        return null;
    }

    private NoeudBase trouverNoeudSousToucher(float sceneX, float sceneY) {
        if (blueprintActuel == null) return null;
        java.util.Set<String> masques = getNoeudsMasques();
        for (int i = blueprintActuel.noeuds.size() - 1; i >= 0; i--) {
            NoeudBase noeud = blueprintActuel.noeuds.get(i);
            if (masques.contains(noeud.id)) continue;
            
            Float nx = blueprintActuel.noeudsX.get(noeud.id);
            Float ny = blueprintActuel.noeudsY.get(noeud.id);
            
            if (nx != null && ny != null) {
                float largeur = getLargeurNoeud(noeud);
                float hauteur = calculerHauteurTotale(noeud);
                
                if (sceneX >= nx && sceneX <= nx + largeur && sceneY >= ny && sceneY <= ny + hauteur) {
                    return noeud;
                }
            }
        }
        return null;
    }

    private Blueprint.Lien trouverLienSousToucher(float sceneX, float sceneY) {
        if (blueprintActuel == null) return null;
        float seuilDistance = 40f; 
        java.util.Set<String> masques = getNoeudsMasques();

        for (Blueprint.Lien lien : blueprintActuel.liens) {
            if (masques.contains(lien.noeudArrivee.id)) continue;
            
            Port pSortie = trouverPortParNom(lien.noeudDepart.portsSortie, lien.portSortieNom);
            Port pEntree = trouverPortParNom(lien.noeudArrivee.portsEntree, lien.portEntreeNom);
            
            if (pSortie != null && pEntree != null) {
                float[] coordS = getCoordonneesPort(lien.noeudDepart, pSortie);
                float[] coordE = getCoordonneesPort(lien.noeudArrivee, pEntree);
                
                if (coordS != null && coordE != null) {
                    float x1 = coordS[0];
                    float y1 = coordS[1];
                    float x2 = coordE[0];
                    float y2 = coordE[1];
                    
                    float dist = Math.abs(x2 - x1) / 2f;
                    dist = Math.max(dist, 60f);
                    float cx1 = x1 + dist;
                    float cy1 = y1;
                    float cx2 = x2 - dist;
                    float cy2 = y2;
                    
                    for (float t = 0; t <= 1.0f; t += 0.1f) {
                        float u = 1.0f - t;
                        float tt = t * t;
                        float uu = u * u;
                        float uuu = uu * u;
                        float ttt = tt * t;
                        
                        float px = uuu * x1 + 3 * uu * t * cx1 + 3 * u * tt * cx2 + ttt * x2;
                        float py = uuu * y1 + 3 * uu * t * cy1 + 3 * u * tt * cy2 + ttt * y2;
                        
                        float dx = px - sceneX;
                        float dy = py - sceneY;
                        if (Math.sqrt(dx * dx + dy * dy) <= seuilDistance) {
                            return lien;
                        }
                    }
                }
            }
        }
        return null;
    }
// bas 6

// haut 7
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                lastDownTime = System.currentTimeMillis();
                touchDownX = x;
                touchDownY = y;
                
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
                        }
                        return true; 
                    }

                    if (trouverZoneEditionSousToucher(sceneX, sceneY) == null) {
                        noeudEnDeplacement = trouverNoeudSousToucher(sceneX, sceneY);
                        if (noeudEnDeplacement != null) {
                            decalageToucherX = sceneX - blueprintActuel.noeudsX.get(noeudEnDeplacement.id);
                            decalageToucherY = sceneY - blueprintActuel.noeudsY.get(noeudEnDeplacement.id);
                        }
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
                long upTime = System.currentTimeMillis();
                float dx = x - touchDownX;
                float dy = y - touchDownY;
                
                boolean estUnClic = (upTime - lastDownTime < 600) && (Math.abs(dx) < 100) && (Math.abs(dy) < 100);
                
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
                } else if (estUnClic && blueprintActuel != null) {
                    float sceneX = ((x - getWidth() / 2f) / niveauZoom) + getWidth() / 2f - cameraX;
                    float sceneY = ((y - getHeight() / 2f) / niveauZoom) + getHeight() / 2f - cameraY;
                    
                    NoeudBase noeudEditTouche = trouverZoneEditionSousToucher(sceneX, sceneY);
                    if (noeudEditTouche != null) {
                        noeudSelectionne = noeudEditTouche; 
                        if (sceneActive != null) {
                            new EditeurNoeudDialog(getContext(), noeudEditTouche, sceneActive, () -> invalidate()).show();
                        } else {
                            System.err.println("ERREUR : sceneActive est null dans CanvasBlueprint !");
                        }
                        invalidate();
                        return true;
                    }
                    
                    Blueprint.Lien lienTouche = trouverLienSousToucher(sceneX, sceneY);
                    if (lienTouche != null) {
                        blueprintActuel.liens.remove(lienTouche);
                        Port pSortie = trouverPortParNom(lienTouche.noeudDepart.portsSortie, lienTouche.portSortieNom);
                        if (pSortie != null && pSortie.noeudDestination == lienTouche.noeudArrivee) {
                            pSortie.noeudDestination = null;
                            pSortie.portDestination = null;
                        }
                        invalidate();
                        return true;
                    }
                    
                    NoeudBase noeudTouche = trouverNoeudSousToucher(sceneX, sceneY);
                    noeudSelectionne = noeudTouche;
                    invalidate();
                }
                
                noeudEnDeplacement = null;
                return true;
        }
        return super.onTouchEvent(event);
    }
}
// bas 7



    



    


    


    


    
