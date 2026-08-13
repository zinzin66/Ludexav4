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
import java.util.Map;

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
    private ObjetBase dernierObjetSurvole = null;
    private float lastXJeu = 0f;
    private float lastYJeu = 0f;
    
    private java.util.Map<String, android.graphics.Bitmap> cacheImages = new java.util.HashMap<>();
    private java.util.Map<String, android.graphics.Typeface> cachePolices = new java.util.HashMap<>();

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

        GestionnaireEtat.viderCache();

        if (scene != null) chargerAnimationsGlobales(scene.objets);
        if (sceneHud != null) chargerAnimationsGlobales(sceneHud.objets);

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

    private void chargerAnimationsGlobales(List<ObjetBase> objets) {
        if (objets == null || cheminProjet == null) return;
        java.io.File fichierAnim = new java.io.File(cheminProjet, "assets_ludexa/Textes/animations.txt");
        if (!fichierAnim.exists()) return;
        
        Map<String, List<String>> animsGlobales = new java.util.HashMap<>();
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierAnim));
            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                int idxEgal = ligne.indexOf('=');
                if (idxEgal > 0) {
                    String cle = ligne.substring(0, idxEgal).trim();
                    String valeurs = ligne.substring(idxEgal + 1).trim();
                    List<String> images = new ArrayList<>();
                    if (!valeurs.isEmpty()) {
                        String[] parts = valeurs.split(",");
                        for (String p : parts) images.add(p.trim());
                    }
                    animsGlobales.put(cle, images);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (ObjetBase obj : objets) {
            for (Map.Entry<String, List<String>> entry : animsGlobales.entrySet()) {
                obj.animations.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setSceneHud(Scene scene) {
        this.sceneHudActive = scene;
        if (scene != null) chargerAnimationsGlobales(scene.objets);
        if (scene == null) {
            this.moteurHud = null;
        }
    }

    public void ouvrirHudDynamique(Scene scene, Blueprint blueprintHud) {
        if (this.sceneHudActive != null && this.sceneHudActive == scene && this.moteurHud != null) {
            this.sceneHudActive = scene; 
            return;
        }

        this.sceneHudActive = scene;
        if (scene != null) chargerAnimationsGlobales(scene.objets);
        
        if (blueprintHud != null) {
            this.moteurHud = new MoteurLogique(blueprintHud);
            this.moteurHud.executerDemarrage();
        } else {
            this.moteurHud = null;
        }
    }

    public void chargerNouvelleScene(Scene nouvelleScene) {
        if (nouvelleScene == null) return;

        if (this.sceneActive != null) {
            GestionnaireEtat.sauvegarderEtat(this.sceneActive);
        }

        this.sceneActive = nouvelleScene;
        GestionnaireEtat.restaurerEtat(this.sceneActive);
        chargerAnimationsGlobales(nouvelleScene.objets);

        Blueprint nouveauBlueprint = null;
        if (cheminProjet != null) {
            try {
                java.io.File dossierLogique = new java.io.File(cheminProjet, "logique");
                java.io.File fileBlueprint = new java.io.File(dossierLogique, nouvelleScene.id + ".json");
                
                if (fileBlueprint.exists()) {
                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileBlueprint));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    nouveauBlueprint = Blueprint.fromJson(sb.toString(), nouvelleScene);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (nouveauBlueprint != null) {
            this.moteur = new MoteurLogique(nouveauBlueprint);
            this.moteur.executerDemarrage();
        } else {
            this.moteur = null; 
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
        GestionnaireAudio.arreterMusique();
    }
// bas 1

 // haut 2
    private ObjetBase getObjetById(String id, List<ObjetBase> contexteObjets) {
        if (contexteObjets == null || id == null) return null;
        for (ObjetBase o : contexteObjets) {
            if (o.id.equals(id)) return o;
        }
        return null;
    }

    public boolean estVisibleEffectif(ObjetBase obj, List<ObjetBase> contexteObjets) {
        ObjetBase cur = obj;
        while (cur != null) {
            if (!cur.visible) return false;
            cur = getObjetById(cur.parentId, contexteObjets);
        }
        return true;
    }

    private ObjetBase trouverObjetSousPoint(float xJeu, float yJeu, boolean exigeDeplacable) {
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
            if (!estVisibleEffectif(obj, listeARechercher)) continue;
            if (exigeDeplacable && !obj.estDeplacable) continue;
            
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
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
            float xJeuActuel = (event.getX() - decalageX) / echelle;
            float yJeuActuel = (event.getY() - decalageY) / echelle;

            ObjetBase objSurvole = trouverObjetSousPoint(xJeuActuel, yJeuActuel, false);
            
            if (objSurvole != dernierObjetSurvole) {
                if (dernierObjetSurvole != null) {
                    if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(dernierObjetSurvole) && this.moteurHud != null) {
                        this.moteurHud.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                    } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(dernierObjetSurvole) && this.moteur != null) {
                        this.moteur.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                    }
                }
                
                if (objSurvole != null) {
                    if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(objSurvole) && this.moteurHud != null) {
                        this.moteurHud.executerEvenementSurObjet(NoeudEventSurvolObjet.class, objSurvole);
                    } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(objSurvole) && this.moteur != null) {
                        this.moteur.executerEvenementSurObjet(NoeudEventSurvolObjet.class, objSurvole);
                    }
                }
                dernierObjetSurvole = objSurvole;
            }
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xJeuActuel = (event.getX() - decalageX) / echelle;
        float yJeuActuel = (event.getY() - decalageY) / echelle;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            objetEnGlissement = trouverObjetSousPoint(xJeuActuel, yJeuActuel, false);
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
            if (objetEnGlissement != null && objetEnGlissement.estDeplacable) {
                objetEnGlissement.x += xJeuActuel - lastXJeu;
                objetEnGlissement.y += yJeuActuel - lastYJeu;
                lastXJeu = xJeuActuel;
                lastYJeu = yJeuActuel;
            } else {
                ObjetBase objSurvole = trouverObjetSousPoint(xJeuActuel, yJeuActuel, false);
                if (objSurvole != dernierObjetSurvole) {
                    if (dernierObjetSurvole != null) {
                        if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(dernierObjetSurvole) && this.moteurHud != null) {
                            this.moteurHud.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                        } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(dernierObjetSurvole) && this.moteur != null) {
                            this.moteur.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                        }
                    }
                    if (objSurvole != null) {
                        if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(objSurvole) && this.moteurHud != null) {
                            this.moteurHud.executerEvenementSurObjet(NoeudEventSurvolObjet.class, objSurvole);
                        } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(objSurvole) && this.moteur != null) {
                            this.moteur.executerEvenementSurObjet(NoeudEventSurvolObjet.class, objSurvole);
                        }
                    }
                    dernierObjetSurvole = objSurvole;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            float xJeu = (event.getX() - decalageX) / echelle;
            float yJeu = (event.getY() - decalageY) / echelle;
            boolean clickIntercepte = false;

            if (sceneHudActive != null && sceneHudActive.objets != null) {
                List<ObjetBase> objetsHudTries = new ArrayList<>(sceneHudActive.objets);
                Collections.sort(objetsHudTries, (o1, o2) -> Integer.compare(o2.zOrder, o1.zOrder));

                for (ObjetBase obj : objetsHudTries) {
                    if (!estVisibleEffectif(obj, sceneHudActive.objets)) continue; 
                    Matrix absMatrix = getAbsoluteMatrix(obj, sceneHudActive.objets);
                    Matrix inverseMatrix = new Matrix();
                    if (absMatrix.invert(inverseMatrix)) {
                        float[] ptLocal = new float[]{xJeu, yJeu};
                        inverseMatrix.mapPoints(ptLocal);
                        if (ptLocal[0] >= 0 && ptLocal[0] <= obj.largeur && ptLocal[1] >= 0 && ptLocal[1] <= obj.hauteur) {
                            if (this.moteurHud != null && !obj.estDesactive) this.moteurHud.executerEvenementSurObjet(NoeudEventClicObjet.class, obj);
                            clickIntercepte = true;
                            break;
                        }
                    }
                }
            }

            if (!clickIntercepte && sceneActive != null && sceneActive.objets != null) {
                List<ObjetBase> objetsJeuTries = new ArrayList<>(sceneActive.objets);
                Collections.sort(objetsJeuTries, (o1, o2) -> Integer.compare(o2.zOrder, o1.zOrder));

                for (ObjetBase obj : objetsJeuTries) {
                    if (!estVisibleEffectif(obj, sceneActive.objets)) continue; 
                    Matrix absMatrix = getAbsoluteMatrix(obj, sceneActive.objets);
                    Matrix inverseMatrix = new Matrix();
                    if (absMatrix.invert(inverseMatrix)) {
                        float[] ptLocal = new float[]{xJeu, yJeu};
                        inverseMatrix.mapPoints(ptLocal);
                        if (ptLocal[0] >= 0 && ptLocal[0] <= obj.largeur && ptLocal[1] >= 0 && ptLocal[1] <= obj.hauteur) {
                            if (this.moteur != null && !obj.estDesactive) this.moteur.executerEvenementSurObjet(NoeudEventClicObjet.class, obj);
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
// bas 2


// haut 3
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

    private void dessinerImage(Canvas canvas, ObjetBase objet, String cheminAAfficher) {
        if (cheminAAfficher != null && cheminProjet != null) {
            android.graphics.Bitmap bmp = cacheImages.get(cheminAAfficher);
            if (bmp == null) {
                try {
                    java.io.File imgFile = new java.io.File(cheminProjet, cheminAAfficher);
                    if (imgFile.exists()) {
                        bmp = android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (bmp != null) cacheImages.put(cheminAAfficher, bmp);
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
            if (!estVisibleEffectif(objet, objets)) continue; // CASCADE VISIBILITÉ
            if ("zone".equals(objet.type)) continue;
            
            if (objet.animationEnCours && objet.animationActive != null && objet.animations.containsKey(objet.animationActive)) {
                List<String> frames = objet.animations.get(objet.animationActive);
                if (frames != null && !frames.isEmpty()) {
                    long tempsActuel = System.currentTimeMillis();
                    if (objet.dernierTempsFrame == 0) objet.dernierTempsFrame = tempsActuel;
                    
                    long ecoulement = tempsActuel - objet.dernierTempsFrame;
                    long delaiFrame = 1000 / Math.max(1, objet.vitesseFps);
                    
                    if (ecoulement >= delaiFrame) {
                        objet.frameCourante++;
                        objet.dernierTempsFrame = tempsActuel;
                        
                        if (objet.frameCourante >= frames.size()) {
                            if (objet.boucleAnimation) {
                                objet.frameCourante = 0;
                            } else {
                                objet.frameCourante = frames.size() - 1;
                                objet.animationEnCours = false;
                            }
                        }
                    }
                    objet.cheminImage = frames.get(objet.frameCourante);
                }
            }

            int alphaInt = Math.max(0, Math.min(255, (int)(objet.alpha * 255)));
            peintureObjet.setColor(objet.couleur);
            peintureObjet.setAlpha(alphaInt);
            peintureTexte.setColor(objet.couleur);
            peintureTexte.setAlpha(alphaInt);

            Matrix absMatrix = getAbsoluteMatrix(objet, objets);

            canvas.save();
            canvas.concat(absMatrix);

            String cheminAAfficher = objet.cheminImage;
            if ("bouton".equals(objet.type)) {
                if (objet.estDesactive && objet.cheminImageDesactive != null) {
                    cheminAAfficher = objet.cheminImageDesactive;
                } else if (objet == objetEnGlissement && objet.cheminImagePresse != null) {
                    cheminAAfficher = objet.cheminImagePresse;
                }
            }

            if ("rond".equals(objet.type)) {
                if (objet.afficherFondColore || cheminAAfficher == null) {
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon, peintureObjet);
                }
                dessinerImage(canvas, objet, cheminAAfficher);
            } else if ("texte".equals(objet.type)) {
                String texteAAfficher = (objet.contenuTexte != null && !objet.contenuTexte.isEmpty()) ? objet.contenuTexte : objet.nom;
                
                if (objet.cheminPolice != null && cheminProjet != null) {
                    android.graphics.Typeface tf = cachePolices.get(objet.cheminPolice);
                    if (tf == null) {
                        try {
                            java.io.File fontFile = new java.io.File(cheminProjet, objet.cheminPolice);
                            if (fontFile.exists()) {
                                tf = android.graphics.Typeface.createFromFile(fontFile);
                                cachePolices.put(objet.cheminPolice, tf);
                            }
                        } catch (Exception e) {}
                    }
                    peintureTexte.setTypeface(tf != null ? tf : android.graphics.Typeface.DEFAULT);
                } else {
                    peintureTexte.setTypeface(android.graphics.Typeface.DEFAULT);
                }

                peintureTexte.setTextSize(objet.tailleFonte);
                peintureTexte.setTextScaleX(1.0f);
                
                float hauteurLigne = objet.tailleFonte * 1.2f;
                float currentY = hauteurLigne; 
                float largeurMax = objet.largeur > 0 ? objet.largeur : 1f;
                
                String[] paragraphes = texteAAfficher.split("\n", -1);
                for (String paragraphe : paragraphes) {
                    if (paragraphe.isEmpty()) {
                        currentY += hauteurLigne;
                        continue;
                    }

                    int start = 0;
                    while (start < paragraphe.length()) {
                        int count = peintureTexte.breakText(paragraphe, start, paragraphe.length(), true, largeurMax, null);
                        if (count <= 0) count = 1;
                        
                        int end = start + count;
                        if (end < paragraphe.length()) {
                            int dernierEspace = paragraphe.lastIndexOf(' ', end - 1);
                            if (dernierEspace > start) {
                                end = dernierEspace + 1;
                            }
                        }
                        
                        String ligne = paragraphe.substring(start, end);
                        canvas.drawText(ligne, 0, currentY, peintureTexte);
                        currentY += hauteurLigne;
                        start = end;
                    }
                }
            } else {
                if (objet.afficherFondColore || cheminAAfficher == null) canvas.drawRect(0, 0, objet.largeur, objet.hauteur, peintureObjet);
                dessinerImage(canvas, objet, cheminAAfficher);
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
        
        if (this.moteur != null && sceneActive != null && sceneActive.objets != null) {
            this.moteur.verifierCollisions(this, sceneActive.objets);
            this.moteur.verifierVariablesChangees(); 
        }
        if (this.moteurHud != null && sceneHudActive != null && sceneHudActive.objets != null) {
            this.moteurHud.verifierCollisions(this, sceneHudActive.objets);
            this.moteurHud.verifierVariablesChangees(); 
        }
        
        echelle = Math.min((float) getWidth() / ConfigurationJeu.LARGEUR_JEU, (float) getHeight() / ConfigurationJeu.HAUTEUR_JEU);
        decalageX = (getWidth() - ConfigurationJeu.LARGEUR_JEU * echelle) / 2f;
        decalageY = (getHeight() - ConfigurationJeu.HAUTEUR_JEU * echelle) / 2f;
        
        canvas.drawColor(Color.BLACK);
        canvas.translate(decalageX, decalageY);
        canvas.scale(echelle, echelle);
        canvas.drawRect(0, 0, ConfigurationJeu.LARGEUR_JEU, ConfigurationJeu.HAUTEUR_JEU, peintureFondBlanc);

        if (sceneActive != null && sceneActive.objets != null) dessinerListeObjets(canvas, sceneActive.objets, false);
        if (sceneHudActive != null && sceneHudActive.objets != null) dessinerListeObjets(canvas, sceneHudActive.objets, false);
    }
}
// bas 3




    



