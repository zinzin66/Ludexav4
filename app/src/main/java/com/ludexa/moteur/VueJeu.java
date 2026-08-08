// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VueJeu extends View {
    private Scene sceneActive;
    private Scene sceneHudActive;
    private Paint peintureObjet;
    private Paint peintureTexte;
    private Paint peintureDebug;
    private Paint peintureFondBlanc;
    private MoteurLogique moteur;
    private MoteurLogique moteurHud;
    private String cheminProjet; 
    
    private float echelle = 1f;
    private float decalageX = 0f;
    private float decalageY = 0f;

    private ObjetBase objetEnGlissement = null;
    private float lastXJeu = 0f;
    private float lastYJeu = 0f;
    
    private java.util.Map<String, android.graphics.Bitmap> cacheImages = new java.util.HashMap<>();

    private final Runnable boucleDeRendu = new Runnable() {
        @Override
        public void run() {
            invalidate();
            postOnAnimation(this);
        }
    };

    public VueJeu(Context context, Scene scene, Blueprint blueprintActif, String cheminProjet, Scene sceneHud, Blueprint blueprintHud) {
        super(context);
        this.sceneActive = scene;
        this.sceneHudActive = sceneHud;
        this.cheminProjet = cheminProjet;

        peintureObjet = new Paint();
        peintureObjet.setColor(Color.BLUE);
        peintureObjet.setAntiAlias(true);

        peintureTexte = new Paint();
        peintureTexte.setColor(Color.BLUE);
        peintureTexte.setAntiAlias(true);

        peintureDebug = new Paint();
        peintureDebug.setColor(Color.BLACK);
        peintureDebug.setTextSize(24f);
        peintureDebug.setAntiAlias(true);
        
        peintureFondBlanc = new Paint();
        peintureFondBlanc.setColor(Color.WHITE);

        if (blueprintActif != null) {
            this.moteur = new MoteurLogique(blueprintActif);
        }
        
        if (blueprintHud != null) {
            this.moteurHud = new MoteurLogique(blueprintHud);
        }
    }

    public void setSceneHud(Scene scene) {
        this.sceneHudActive = scene;
    }

    public void ouvrirHudDynamique(Scene scene, Blueprint blueprintHud) {
        this.sceneHudActive = scene;
        if (blueprintHud != null) {
            this.moteurHud = new MoteurLogique(blueprintHud);
            this.moteurHud.executerDemarrage();
        } else {
            this.moteurHud = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.moteur != null) this.moteur.executerDemarrage();
        if (this.moteurHud != null) this.moteurHud.executerDemarrage();
        postOnAnimation(boucleDeRendu);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(boucleDeRendu);
    }

    private ObjetBase trouverObjetSousPoint(float xJeu, float yJeu) {
        List<ObjetBase> listeARechercher = null;
        if (sceneHudActive != null && sceneHudActive.objets != null) {
            listeARechercher = sceneHudActive.objets;
        } else if (sceneActive != null && sceneActive.objets != null) {
            listeARechercher = sceneActive.objets;
        }
        if (listeARechercher == null) return null;

        List<ObjetBase> objetsTries = new ArrayList<>(listeARechercher);
        Collections.sort(objetsTries, new Comparator<ObjetBase>() {
            @Override
            public int compare(ObjetBase o1, ObjetBase o2) {
                return Integer.compare(o2.zOrder, o1.zOrder);
            }
        });

        for (ObjetBase obj : objetsTries) {
            if (!obj.visible || !obj.estDeplacable) continue;
            Matrix absMatrix = getAbsoluteMatrix(obj, listeARechercher);
            Matrix inverseMatrix = new Matrix();
            if (absMatrix.invert(inverseMatrix)) {
                float[] ptLocal = new float[]{xJeu, yJeu};
                inverseMatrix.mapPoints(ptLocal);
                if (ptLocal[0] >= 0 && ptLocal[0] <= obj.largeur && ptLocal[1] >= 0 && ptLocal[1] <= obj.hauteur) {
                    return obj;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xJeuActuel = (event.getX() - decalageX) / echelle;
        float yJeuActuel = (event.getY() - decalageY) / echelle;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            objetEnGlissement = trouverObjetSousPoint(xJeuActuel, yJeuActuel);
            lastXJeu = xJeuActuel;
            lastYJeu = yJeuActuel;
            
            if (objetEnGlissement != null) {
                if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(objetEnGlissement) && this.moteurHud != null) {
                    this.moteurHud.executerEvenementSurObjet(NoeudEventDebutGlisser.class, objetEnGlissement);
                } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(objetEnGlissement) && this.moteur != null) {
                    this.moteur.executerEvenementSurObjet(NoeudEventDebutGlisser.class, objetEnGlissement);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (objetEnGlissement != null) {
                objetEnGlissement.x += xJeuActuel - lastXJeu;
                objetEnGlissement.y += yJeuActuel - lastYJeu;
                lastXJeu = xJeuActuel;
                lastYJeu = yJeuActuel;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            float xJeu = (event.getX() - decalageX) / echelle;
            float yJeu = (event.getY() - decalageY) / echelle;
            boolean clickIntercepte = false;

            if (sceneHudActive != null && sceneHudActive.objets != null) {
                List<ObjetBase> objetsHudTries = new ArrayList<>(sceneHudActive.objets);
                Collections.sort(objetsHudTries, (o1, o2) -> Integer.compare(o2.zOrder, o1.zOrder));

                for (ObjetBase obj : objetsHudTries) {
                    if (!obj.visible) continue;
                    Matrix absMatrix = getAbsoluteMatrix(obj, sceneHudActive.objets);
                    Matrix inverseMatrix = new Matrix();
                    if (absMatrix.invert(inverseMatrix)) {
                        float[] ptLocal = new float[]{xJeu, yJeu};
                        inverseMatrix.mapPoints(ptLocal);
                        if (ptLocal[0] >= 0 && ptLocal[0] <= obj.largeur && ptLocal[1] >= 0 && ptLocal[1] <= obj.hauteur) {
                            if (this.moteurHud != null) this.moteurHud.executerEvenementSurObjet(NoeudEventClicObjet.class, obj);
                            break;
                        }
                    }
                }
                clickIntercepte = true;
            }

            if (!clickIntercepte && sceneActive != null && sceneActive.objets != null) {
                List<ObjetBase> objetsJeuTries = new ArrayList<>(sceneActive.objets);
                Collections.sort(objetsJeuTries, (o1, o2) -> Integer.compare(o2.zOrder, o1.zOrder));

                for (ObjetBase obj : objetsJeuTries) {
                    if (!obj.visible) continue;
                    Matrix absMatrix = getAbsoluteMatrix(obj, sceneActive.objets);
                    Matrix inverseMatrix = new Matrix();
                    if (absMatrix.invert(inverseMatrix)) {
                        float[] ptLocal = new float[]{xJeu, yJeu};
                        inverseMatrix.mapPoints(ptLocal);
                        if (ptLocal[0] >= 0 && ptLocal[0] <= obj.largeur && ptLocal[1] >= 0 && ptLocal[1] <= obj.hauteur) {
                            if (this.moteur != null) this.moteur.executerEvenementSurObjet(NoeudEventClicObjet.class, obj);
                            break;
                        }
                    }
                }
            }

            if (this.moteur != null) this.moteur.executerEvenement(NoeudEventFinClic.class);

            if (objetEnGlissement != null) {
                if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(objetEnGlissement) && this.moteurHud != null) {
                    this.moteurHud.executerEvenementSurObjet(NoeudEventFinGlisser.class, objetEnGlissement);
                } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(objetEnGlissement) && this.moteur != null) {
                    this.moteur.executerEvenementSurObjet(NoeudEventFinGlisser.class, objetEnGlissement);
                }
            }
            objetEnGlissement = null;
        }
        return true;
    }

    private ObjetBase getObjetById(String id, List<ObjetBase> contexteObjets) {
        if (contexteObjets == null || id == null) return null;
        for (ObjetBase o : contexteObjets) {
            if (o.id.equals(id)) return o;
        }
        return null;
    }

    // MODIFICATION : Rendue publique pour être accessible par UtilCollision
    public Matrix getAbsoluteMatrix(ObjetBase obj, List<ObjetBase> contexteObjets) {
        Matrix m = new Matrix();
        List<ObjetBase> chaine = new ArrayList<>();
        ObjetBase cur = obj;
        while (cur != null) {
            chaine.add(cur);
            cur = getObjetById(cur.parentId, contexteObjets);
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

    private void dessinerImage(Canvas canvas, ObjetBase objet) {
        if (objet.cheminImage != null && cheminProjet != null) {
            android.graphics.Bitmap bmp = cacheImages.get(objet.cheminImage);
            if (bmp == null) {
                try {
                    java.io.File imgFile = new java.io.File(cheminProjet, objet.cheminImage);
                    if (imgFile.exists()) {
                        bmp = android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (bmp != null) cacheImages.put(objet.cheminImage, bmp);
                    }
                } catch (Exception e) {}
            }
            if (bmp != null) {
                if ("rond".equals(objet.type)) {
                    canvas.save();
                    android.graphics.Path path = new android.graphics.Path();
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    path.addCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, android.graphics.Path.Direction.CW);
                    canvas.clipPath(path);
                    canvas.drawBitmap(bmp, null, new android.graphics.RectF(0, 0, objet.largeur, objet.hauteur), peintureObjet);
                    canvas.restore();
                } else {
                    canvas.drawBitmap(bmp, null, new android.graphics.RectF(0, 0, objet.largeur, objet.hauteur), peintureObjet);
                }
            }
        }
    }

    private void dessinerListeObjets(Canvas canvas, List<ObjetBase> objets, boolean avecDebugPosition) {
        List<ObjetBase> objetsTries = new ArrayList<>(objets);
        Collections.sort(objetsTries, (o1, o2) -> Integer.compare(o1.zOrder, o2.zOrder));

        for (ObjetBase objet : objetsTries) {
            if (!objet.visible) continue; 

            peintureObjet.setColor(objet.couleur);
            peintureTexte.setColor(objet.couleur);
            if ("texte".equals(objet.type)) peintureTexte.setTextSize(objet.hauteur > 0 ? objet.hauteur : 40f); 

            Matrix absMatrix = getAbsoluteMatrix(objet, objets);

            canvas.save();
            canvas.concat(absMatrix);

            if ("rond".equals(objet.type)) {
                if (objet.afficherFondColore || objet.cheminImage == null) {
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, peintureObjet);
                }
                dessinerImage(canvas, objet);
            } else if ("texte".equals(objet.type)) {
                String texteAAfficher = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                peintureTexte.setTextScaleX(1.0f);
                float tw = peintureTexte.measureText(texteAAfficher);
                if (tw > 0) peintureTexte.setTextScaleX(objet.largeur / tw);
                canvas.drawText(texteAAfficher, 0, objet.hauteur - (objet.hauteur * 0.1f), peintureTexte);
                peintureTexte.setTextScaleX(1.0f);
            } else {
                if (objet.afficherFondColore || objet.cheminImage == null) canvas.drawRect(0, 0, objet.largeur, objet.hauteur, peintureObjet);
                dessinerImage(canvas, objet);
            }
            canvas.restore();

            if (avecDebugPosition) {
                float[] posAbsolue = {0, 0};
                absMatrix.mapPoints(posAbsolue);
                canvas.drawText(objet.nom + " (" + (int) objet.x + ", " + (int) objet.y + ")", posAbsolue[0], posAbsolue[1] - 10f, peintureDebug);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // NOUVEAU : Vérification continue des collisions à chaque frame (~60fps)
        if (this.moteur != null && sceneActive != null && sceneActive.objets != null) {
            this.moteur.verifierCollisions(this, sceneActive.objets);
        }
        if (this.moteurHud != null && sceneHudActive != null && sceneHudActive.objets != null) {
            this.moteurHud.verifierCollisions(this, sceneHudActive.objets);
        }
        
        echelle = Math.min((float) getWidth() / ConfigurationJeu.LARGEUR_JEU, (float) getHeight() / ConfigurationJeu.HAUTEUR_JEU);
        decalageX = (getWidth() - ConfigurationJeu.LARGEUR_JEU * echelle) / 2f;
        decalageY = (getHeight() - ConfigurationJeu.HAUTEUR_JEU * echelle) / 2f;
        
        canvas.drawColor(Color.BLACK);
        canvas.translate(decalageX, decalageY);
        canvas.scale(echelle, echelle);
        canvas.drawRect(0, 0, ConfigurationJeu.LARGEUR_JEU, ConfigurationJeu.HAUTEUR_JEU, peintureFondBlanc);

        if (sceneActive != null && sceneActive.objets != null) dessinerListeObjets(canvas, sceneActive.objets, true);
        if (sceneHudActive != null && sceneHudActive.objets != null) dessinerListeObjets(canvas, sceneHudActive.objets, false);
    }
}
// bas 1
            
