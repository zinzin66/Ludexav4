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
    
    public static float tremblementIntensite = 0f;
    public static long tremblementFin = 0;
    public static float vitesseSuiviCamera = 0.1f;

    private Scene sceneActive;
    private Scene sceneHudActive;
    private Paint peintureObjet;
    private Paint peintureTexte;
    private Paint peintureDebug;
    private Paint peintureFondBlanc;
    private MoteurLogique moteur;
    private MoteurLogique moteurHud;
    private MoteurPhysique moteurPhysique;
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
        
        GestionnaireControles.reinitialiser();

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
        peintureFondBlanc.setColor(Color.BLACK);

        this.moteurPhysique = new MoteurPhysique(); 

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
        } catch (Exception e) {}
        
        for (ObjetBase obj : objets) {
            for (Map.Entry<String, List<String>> entry : animsGlobales.entrySet()) {
                obj.animations.put(entry.getKey(), entry.getValue());
            }
        }
    }
// bas 1
// haut 2
    public void setSceneHud(Scene scene) {
        this.sceneHudActive = scene;
        if (scene != null) chargerAnimationsGlobales(scene.objets);
        if (scene == null) this.moteurHud = null;
    }

    public void ouvrirHudDynamique(Scene scene, Blueprint blueprintHud) {
        if (this.sceneHudActive != null && this.sceneHudActive == scene && this.moteurHud != null) {
            this.sceneHudActive = scene; 
            return;
        }

        this.sceneHudActive = scene;
        if (scene != null) chargerAnimationsGlobales(scene.objets);
        
        deballerPrefabs(this.sceneHudActive);

        if (blueprintHud != null) {
            this.moteurHud = new MoteurLogique(blueprintHud);
            this.moteurHud.executerDemarrage();
        } else {
            this.moteurHud = null;
        }
    }

    public void chargerNouvelleScene(Scene nouvelleScene) {
        if (nouvelleScene == null) return;
        if (this.sceneActive != null) GestionnaireEtat.sauvegarderEtat(this.sceneActive);

        this.sceneActive = nouvelleScene;
        GestionnaireEtat.restaurerEtat(this.sceneActive);
        chargerAnimationsGlobales(nouvelleScene.objets);

        deballerPrefabs(this.sceneActive);

        Blueprint nouveauBlueprint = null;
        if (cheminProjet != null) {
            try {
                java.io.File dossierLogique = new java.io.File(cheminProjet, "logique");
                java.io.File fileBlueprint = new java.io.File(dossierLogique, nouvelleScene.id + ".json");
                if (fileBlueprint.exists()) {
                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileBlueprint));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();
                    nouveauBlueprint = Blueprint.fromJson(sb.toString(), nouvelleScene);
                }
            } catch (Exception e) {}
        }

        if (nouveauBlueprint != null) {
            this.moteur = new MoteurLogique(nouveauBlueprint);
            this.moteur.executerDemarrage();
        } else {
            this.moteur = null; 
        }
    }

    // --- NOUVEAU : Mécanique de Prefabs / Scènes Imbriquées ---
    private java.util.Set<String> pilesInstanciationEnCours = new java.util.HashSet<>();

    public void instancierScene(Scene sceneAInstancier, float offsetX, float offsetY) {
        if (sceneAInstancier == null || sceneActive == null || sceneAInstancier == sceneActive) return;
        if (pilesInstanciationEnCours.contains(sceneAInstancier.id)) {
            android.util.Log.w("VueJeu", "Cycle de prefabs détecté et ignoré pour la scène " + sceneAInstancier.id);
            return;
        }
        pilesInstanciationEnCours.add(sceneAInstancier.id);
        try {
            instancierSceneInterne(sceneAInstancier, offsetX, offsetY);
        } finally {
            pilesInstanciationEnCours.remove(sceneAInstancier.id);
        }
    }

    private void instancierSceneInterne(Scene sceneAInstancier, float offsetX, float offsetY) {
        Blueprint blueprintInstance = null;
        if (cheminProjet != null) {
            try {
                java.io.File dossierLogique = new java.io.File(cheminProjet, "logique");
                java.io.File fileBlueprint = new java.io.File(dossierLogique, sceneAInstancier.id + ".json");
                if (fileBlueprint.exists()) {
                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileBlueprint));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();
                    blueprintInstance = Blueprint.fromJson(sb.toString(), sceneAInstancier);
                }
            } catch (Exception e) {}
        }
        
        java.util.Map<String, String> mapIds = new java.util.HashMap<>();
        
        if (sceneAInstancier.objets != null) {
            List<ObjetBase> objetsInjectes = new ArrayList<>();
            for (ObjetBase objOrigine : sceneAInstancier.objets) {
                ObjetBase clone = objOrigine.clonerProfond();
                
                // CORRECTION : Régénération de l'ID pour garantir l'unicité et éviter les crashs de parenté
                String nouvelId = java.util.UUID.randomUUID().toString();
                mapIds.put(clone.id, nouvelId);
                clone.id = nouvelId;
                
                clone.x += offsetX;
                clone.y += offsetY;
                clone.tag = (clone.tag != null ? clone.tag + " " : "") + "_INSTANCE_" + sceneAInstancier.id;
                objetsInjectes.add(clone);
            }
            
            // CORRECTION : Réparation des liens internes pour les enfants / joysticks
            for (ObjetBase clone : objetsInjectes) {
                if (clone.parentId != null && mapIds.containsKey(clone.parentId)) {
                    clone.parentId = mapIds.get(clone.parentId);
                }
                if (clone.cibleJoystickId != null && mapIds.containsKey(clone.cibleJoystickId)) {
                    clone.cibleJoystickId = mapIds.get(clone.cibleJoystickId);
                }
                if (clone.idCiblePoursuite != null && mapIds.containsKey(clone.idCiblePoursuite)) {
                    clone.idCiblePoursuite = mapIds.get(clone.idCiblePoursuite);
                }
                sceneActive.ajouterObjet(clone);
            }
            chargerAnimationsGlobales(sceneActive.objets);
        }
        
        if (blueprintInstance != null && blueprintInstance.noeuds != null && this.moteur != null) {
            for (NoeudBase noeud : blueprintInstance.noeuds) {
                noeud.categorie = (noeud.categorie != null ? noeud.categorie + " " : "") + "_INSTANCE_" + sceneAInstancier.id;
                
                // Tentative générique de mise à jour des cibles dans le Blueprint
                try {
                    java.lang.reflect.Field champCible = noeud.getClass().getField("cibleObjetId");
                    String ancienneCible = (String) champCible.get(noeud);
                    if (ancienneCible != null && mapIds.containsKey(ancienneCible)) {
                        champCible.set(noeud, mapIds.get(ancienneCible));
                    }
                } catch (Exception e) {}
                
                this.moteur.ajouterNoeudAuBlueprintActif(noeud);
                if (noeud instanceof NoeudEventStart) {
                    noeud.executer();
                }
            }
        }

        deballerPrefabs(this.sceneActive);
    }

    public void detruireInstances(Scene sceneCible) {
        if (sceneCible == null || sceneActive == null || sceneActive.objets == null) return;
        
        String tagRecherche = "_INSTANCE_" + sceneCible.id;
        
        java.util.Iterator<ObjetBase> itObj = sceneActive.objets.iterator();
        while (itObj.hasNext()) {
            ObjetBase obj = itObj.next();
            if (obj.tag != null && obj.tag.contains(tagRecherche)) {
                itObj.remove();
            }
        }
        
        if (this.moteur != null) {
            this.moteur.nettoyerNoeudsParTag(tagRecherche);
        }
    }

    // --- CORRECTION : Accès hybride (Éditeur en direct / Disque compilé) ---
    private List<Scene> cacheListeScenesDisque = null;

    private List<Scene> chargerToutesLesScenesDepuisDisque() {
        if (cacheListeScenesDisque != null) return cacheListeScenesDisque;
        if (cheminProjet == null) return null;
        try {
            java.io.File fileProjet = new java.io.File(cheminProjet, "projet_sauvegarde.json");
            if (!fileProjet.exists()) return null;
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileProjet));
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Scene>>(){}.getType();
            List<Scene> scenes = new com.google.gson.Gson().fromJson(br, listType);
            br.close();
            cacheListeScenesDisque = scenes;
            return scenes;
        } catch (Exception e) {
            android.util.Log.e("VueJeu", "Erreur chargerToutesLesScenesDepuisDisque : " + e.getMessage());
            return null;
        }
    }

    // NOUVELLE MÉTHODE : Permet de lire les scènes non sauvegardées en mémoire vive si le jeu tourne dans l'éditeur
    private List<Scene> obtenirToutesLesScenes() {
        Context ctx = getContext();
        if (ctx != null) {
            try {
                if ("InterfaceEditeur".equals(ctx.getClass().getSimpleName())) {
                    java.lang.reflect.Field field = ctx.getClass().getField("listeScenes");
                    @SuppressWarnings("unchecked")
                    List<Scene> scenesEditeur = (List<Scene>) field.get(ctx);
                    if (scenesEditeur != null) return scenesEditeur;
                }
            } catch (Exception e) {
                android.util.Log.e("VueJeu", "Accès refusé à listeScenes de InterfaceEditeur", e);
            }
        }
        return chargerToutesLesScenesDepuisDisque();
    }

    private Scene getSceneParId(String id) {
        if (id == null) return null;
        List<Scene> scenes = obtenirToutesLesScenes();
        if (scenes != null) {
            for (Scene s : scenes) {
                if (id.equals(s.id)) return s;
            }
        }
        return null;
    }

    private void deballerPrefabs(Scene scene) {
        if (scene == null || scene.objets == null) return;
        List<ObjetBase> prefabs = new ArrayList<>();
        for (ObjetBase obj : scene.objets) {
            if ("scene_instance".equals(obj.type) && obj.sceneLieeId != null && obj.visible) {
                prefabs.add(obj);
            }
        }
        for (ObjetBase prefab : prefabs) {
            prefab.visible = false; 
            Scene sceneLiee = getSceneParId(prefab.sceneLieeId);
            if (sceneLiee != null) {
                instancierScene(sceneLiee, prefab.x, prefab.y);
            } else {
                android.util.Log.e("VueJeu", "Déballage échoué : Scène liée introuvable en mémoire/disque (" + prefab.sceneLieeId + ")");
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        deballerPrefabs(this.sceneActive); 
        deballerPrefabs(this.sceneHudActive);

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

    private ObjetBase trouverObjetParType(String type) {
        if (sceneHudActive != null && sceneHudActive.objets != null) {
            for (ObjetBase o : sceneHudActive.objets) {
                if (type.equals(o.type) && estVisibleEffectif(o, sceneHudActive.objets)) return o;
            }
        }
        if (sceneActive != null && sceneActive.objets != null) {
            for (ObjetBase o : sceneActive.objets) {
                if (type.equals(o.type) && estVisibleEffectif(o, sceneActive.objets)) return o;
            }
        }
        return null;
    }
// bas 2
// haut 3
    public Matrix getAbsoluteMatrix(ObjetBase obj, List<ObjetBase> contexteObjets) {
        boolean isHud = (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(obj));
        float camX = isHud ? 0f : GestionnaireControles.cameraX;
        float camY = isHud ? 0f : GestionnaireControles.cameraY;
        return getAbsoluteMatrix(obj, contexteObjets, camX, camY);
    }

    public Matrix getAbsoluteMatrix(ObjetBase obj, List<ObjetBase> contexteObjets, float camX, float camY) {
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
            
            if (o.facteurParallaxe != 1.0f) {
                float shiftX = camX * (1.0f - o.facteurParallaxe);
                float shiftY = GestionnaireControles.parallaxeUniquementX ? 0f : camY * (1.0f - o.facteurParallaxe);
                local.postTranslate(shiftX, shiftY);
            }
            
            m.preConcat(local); 
        }
        return m;
    }
    
    private boolean pointDansObjet(float xVue, float yVue, float xMonde, float yMonde, ObjetBase obj) {
        boolean isHud = (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(obj));
        List<ObjetBase> contexte = isHud ? sceneHudActive.objets : sceneActive.objets;
        
        float camX = isHud ? 0f : GestionnaireControles.cameraX;
        float camY = isHud ? 0f : GestionnaireControles.cameraY;
        
        Matrix absMatrix = getAbsoluteMatrix(obj, contexte, camX, camY);
        Matrix inverseMatrix = new Matrix();
        if (absMatrix.invert(inverseMatrix)) {
            float[] ptLocal = new float[]{isHud ? xVue : xMonde, isHud ? yVue : yMonde};
            inverseMatrix.mapPoints(ptLocal);
            return (ptLocal[0] >= 0 && ptLocal[0] <= obj.largeur && ptLocal[1] >= 0 && ptLocal[1] <= obj.hauteur);
        }
        return false;
    }

    private ObjetBase trouverObjetSousPoint(float xVue, float yVue, boolean exigeDeplacable) {
        if (sceneHudActive != null && sceneHudActive.objets != null) {
            List<ObjetBase> objetsHudTries = new ArrayList<>(sceneHudActive.objets);
            Collections.sort(objetsHudTries, (o1, o2) -> Integer.compare(o2.zOrder, o1.zOrder));
            for (ObjetBase obj : objetsHudTries) {
                if (!estVisibleEffectif(obj, sceneHudActive.objets)) continue;
                if (exigeDeplacable && !obj.estDeplacable) continue;
                if (pointDansObjet(xVue, yVue, 0f, 0f, obj)) return obj;
            }
        }

        float xMonde = xVue + GestionnaireControles.cameraX;
        float yMonde = yVue + GestionnaireControles.cameraY;
        
        if (sceneActive != null && sceneActive.objets != null) {
            List<ObjetBase> objetsJeuTries = new ArrayList<>(sceneActive.objets);
            Collections.sort(objetsJeuTries, (o1, o2) -> Integer.compare(o2.zOrder, o1.zOrder));
            for (ObjetBase obj : objetsJeuTries) {
                if (!estVisibleEffectif(obj, sceneActive.objets)) continue;
                if (exigeDeplacable && !obj.estDeplacable) continue;
                if (pointDansObjet(0f, 0f, xMonde, yMonde, obj)) return obj;
            }
        }
        return null;
    }

    private boolean verifierCollisionStatique(float testX, float testY, float largeur, float hauteur, float scaleX, float scaleY, ObjetBase objetCible, List<ObjetBase> objets) {
        if (objets == null) return false;

        float aCentreX = testX + (largeur / 2f);
        float aCentreY = testY + (hauteur / 2f);
        float aDemiLargeur = (largeur * Math.abs(scaleX)) / 2f;
        float aDemiHauteur = (hauteur * Math.abs(scaleY)) / 2f;

        float aGauche = aCentreX - aDemiLargeur;
        float aDroite = aCentreX + aDemiLargeur;
        float aHaut = aCentreY - aDemiHauteur;
        float aBas = aCentreY + aDemiHauteur;

        for (ObjetBase mur : objets) {
            if (mur == objetCible || !estVisibleEffectif(mur, objets)) continue;
            if (mur.estPhysique && mur.estStatique) {
                
                float bCentreX = mur.x + (mur.largeur / 2f);
                float bCentreY = mur.y + (mur.hauteur / 2f);
                float bDemiLargeur = (mur.largeur * Math.abs(mur.scaleX)) / 2f;
                float bDemiHauteur = (mur.hauteur * Math.abs(mur.scaleY)) / 2f;

                float bGauche = bCentreX - bDemiLargeur;
                float bDroite = bCentreX + bDemiLargeur;
                float bHaut = bCentreY - bDemiHauteur;
                float bBas = bCentreY + bDemiHauteur;

                if (aGauche < bDroite && aDroite > bGauche && aHaut < bBas && aBas > bHaut) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deplacerAvecCollision(ObjetBase objet, float deltaX, float deltaY, List<ObjetBase> contexteObjets) {
        if (deltaX == 0 && deltaY == 0) return;

        if (objet.estPhysique && !objet.estStatique) {
            if (deltaX != 0) {
                float futurX = objet.x + deltaX;
                if (!verifierCollisionStatique(futurX, objet.y, objet.largeur, objet.hauteur, objet.scaleX, objet.scaleY, objet, contexteObjets)) {
                    objet.x = futurX;
                }
            }
            if (deltaY != 0) {
                float futurY = objet.y + deltaY;
                if (!verifierCollisionStatique(objet.x, futurY, objet.largeur, objet.hauteur, objet.scaleX, objet.scaleY, objet, contexteObjets)) {
                    objet.y = futurY;
                }
            }
        } else {
            objet.x += deltaX;
            objet.y += deltaY;
        }
    }
// bas 3

// haut 4
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
            float xVue = (event.getX() - decalageX) / echelle;
            float yVue = (event.getY() - decalageY) / echelle;
            ObjetBase objSurvole = trouverObjetSousPoint(xVue, yVue, false);
            
            if (objSurvole != dernierObjetSurvole) {
                if (dernierObjetSurvole != null) {
                    MoteurLogique.dernierObjetImplique = dernierObjetSurvole;
                    
                    if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(dernierObjetSurvole) && this.moteurHud != null) {
                        this.moteurHud.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                    } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(dernierObjetSurvole) && this.moteur != null) {
                        this.moteur.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                    }
                }
                if (objSurvole != null) {
                    MoteurLogique.dernierObjetImplique = objSurvole;
                    
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
        boolean touchJoystick = false;
        boolean touchAction = false;
        
        float newJoyDirX = 0f;
        float newJoyDirY = 0f;
        GestionnaireControles.isActionJustPressed = false;

        ObjetBase joystickObj = trouverObjetParType("joystick");
        ObjetBase actionBtnObj = trouverObjetParType("bouton_action");

        if (GestionnaireControles.modeAventureActif) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                
                if ((event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) && event.getActionIndex() == i) {
                    continue; 
                }

                float ptrX = (event.getX(i) - decalageX) / echelle;
                float ptrY = (event.getY(i) - decalageY) / echelle;
                float ptrXMonde = ptrX + GestionnaireControles.cameraX;
                float ptrYMonde = ptrY + GestionnaireControles.cameraY;
                
                if (actionBtnObj != null && pointDansObjet(ptrX, ptrY, ptrXMonde, ptrYMonde, actionBtnObj)) {
                    touchAction = true;
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                        if (!GestionnaireControles.isActionPressed) GestionnaireControles.isActionJustPressed = true;
                    }
                }
                
                if (joystickObj != null) {
                    boolean isHud = (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(joystickObj));
                    float checkX = isHud ? ptrX : ptrXMonde;
                    float checkY = isHud ? ptrY : ptrYMonde;
                    
                    float joyCentreX = joystickObj.x + joystickObj.largeur / 2f;
                    float joyCentreY = joystickObj.y + joystickObj.hauteur / 2f;
                    float rayon = Math.min(joystickObj.largeur, joystickObj.hauteur) / 2f;
                    
                    float distCenter = (float) Math.hypot(checkX - joyCentreX, checkY - joyCentreY);
                    if (distCenter <= rayon * 1.5f) {
                        touchJoystick = true;
                        float dx = checkX - joyCentreX;
                        float dy = checkY - joyCentreY;
                        float dist = (float) Math.hypot(dx, dy);
                        if (dist > 0) {
                            newJoyDirX = dx / Math.max(dist, rayon); 
                            newJoyDirY = dy / Math.max(dist, rayon);
                            if (dist > rayon) {
                                newJoyDirX = dx / dist;
                                newJoyDirY = dy / dist;
                            }
                        }
                    }
                }
            }
        }
        
        GestionnaireControles.joyDirX = newJoyDirX;
        GestionnaireControles.joyDirY = newJoyDirY;
        
        GestionnaireControles.isActionPressed = touchAction;
        if (GestionnaireControles.isActionJustPressed && this.moteur != null) {
            this.moteur.executerEvenement(NoeudEventBoutonAction.class);
        }
        
        if (touchJoystick || touchAction) {
            objetEnGlissement = null;
            return true; 
        }

        float xVue = (event.getX() - decalageX) / echelle;
        float yVue = (event.getY() - decalageY) / echelle;
        float xMonde = xVue + GestionnaireControles.cameraX;
        float yMonde = yVue + GestionnaireControles.cameraY;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            objetEnGlissement = trouverObjetSousPoint(xVue, yVue, false);
            lastXJeu = xMonde;
            lastYJeu = yMonde;
            
            if (objetEnGlissement != null) {
                MoteurLogique.dernierObjetImplique = objetEnGlissement;
                
                if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(objetEnGlissement) && this.moteurHud != null) {
                    this.moteurHud.executerEvenementSurObjet(NoeudEventDebutGlisser.class, objetEnGlissement);
                    lastXJeu = xVue; lastYJeu = yVue; 
                } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(objetEnGlissement) && this.moteur != null) {
                    this.moteur.executerEvenementSurObjet(NoeudEventDebutGlisser.class, objetEnGlissement);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (objetEnGlissement != null && objetEnGlissement.estDeplacable) {
                boolean isHudDrag = sceneHudActive != null && sceneHudActive.objets.contains(objetEnGlissement);
                float newX = isHudDrag ? xVue : xMonde;
                float newY = isHudDrag ? yVue : yMonde;
                
                float deltaX = newX - lastXJeu;
                float deltaY = newY - lastYJeu;
                
                deplacerAvecCollision(objetEnGlissement, deltaX, deltaY, isHudDrag ? sceneHudActive.objets : sceneActive.objets);
                
                lastXJeu = newX;
                lastYJeu = newY;
            } else {
                ObjetBase objSurvole = trouverObjetSousPoint(xVue, yVue, false);
                if (objSurvole != dernierObjetSurvole) {
                    if (dernierObjetSurvole != null) {
                        MoteurLogique.dernierObjetImplique = dernierObjetSurvole;
                        
                        if (sceneHudActive != null && sceneHudActive.objets != null && sceneHudActive.objets.contains(dernierObjetSurvole) && this.moteurHud != null) {
                            this.moteurHud.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                        } else if (sceneActive != null && sceneActive.objets != null && sceneActive.objets.contains(dernierObjetSurvole) && this.moteur != null) {
                            this.moteur.executerEvenementSurObjet(NoeudEventFinSurvol.class, dernierObjetSurvole);
                        }
                    }
                    if (objSurvole != null) {
                        MoteurLogique.dernierObjetImplique = objSurvole;
                        
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
            ObjetBase objClick = trouverObjetSousPoint(xVue, yVue, false);
            if (objClick != null && !objClick.estDesactive) {
                MoteurLogique.dernierObjetImplique = objClick;
                
                if (sceneHudActive != null && sceneHudActive.objets.contains(objClick) && this.moteurHud != null) {
                    this.moteurHud.executerEvenementSurObjet(NoeudEventClicObjet.class, objClick);
                } else if (sceneActive != null && sceneActive.objets.contains(objClick) && this.moteur != null) {
                    this.moteur.executerEvenementSurObjet(NoeudEventClicObjet.class, objClick);
                }
            }
            if (this.moteur != null) this.moteur.executerEvenement(NoeudEventFinClic.class);

            if (objetEnGlissement != null) {
                MoteurLogique.dernierObjetImplique = objetEnGlissement;
                
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
// bas 4

// haut 5
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
                if ("rond".equals(objet.type) || "joystick".equals(objet.type) || "bouton_action".equals(objet.type)) {
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

    private void dessinerListeObjets(Canvas canvas, List<ObjetBase> objets, boolean avecDebugPosition, float camX, float camY) {
        List<ObjetBase> objetsTries = new ArrayList<>(objets);
        Collections.sort(objetsTries, (o1, o2) -> Integer.compare(o1.zOrder, o2.zOrder));

        for (ObjetBase objet : objetsTries) {
            if (!estVisibleEffectif(objet, objets)) continue; 
            if ("zone".equals(objet.type)) continue;

            if (objet.clignotementActif) {
                long now = System.currentTimeMillis();
                if (objet.clignotementDureeTotalMs > 0 && now - objet.tempsDebutClignotement > objet.clignotementDureeTotalMs) {
                    objet.clignotementActif = false;
                    objet.etatVisibleClignotement = true;
                } else {
                    long ecoule = now - objet.tempsDebutClignotement;
                    objet.etatVisibleClignotement = (ecoule / Math.max(1, objet.clignotementVitesseMs)) % 2 == 0;
                }
            } else {
                objet.etatVisibleClignotement = true;
            }

            if (!objet.etatVisibleClignotement) continue;
            
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
                            if (objet.boucleAnimation) objet.frameCourante = 0;
                            else {
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

            if (!"Aucun".equals(objet.filtreCouleur)) {
                android.graphics.ColorMatrix cm = new android.graphics.ColorMatrix();
                if ("Noir et Blanc".equals(objet.filtreCouleur)) {
                    cm.setSaturation(0);
                } else if ("Sepia".equals(objet.filtreCouleur)) {
                    cm.setSaturation(0);
                    android.graphics.ColorMatrix sepiaMatrix = new android.graphics.ColorMatrix();
                    sepiaMatrix.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                    cm.postConcat(sepiaMatrix);
                } else if ("Inversion".equals(objet.filtreCouleur)) {
                    cm.set(new float[] {
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                    });
                }
                peintureObjet.setColorFilter(new android.graphics.ColorMatrixColorFilter(cm));
            } else {
                peintureObjet.setColorFilter(null);
            }

            Matrix absMatrix = getAbsoluteMatrix(objet, objets, camX, camY);
            
            boolean estEnMouvement = (Math.abs(objet.x - objet.ancienneX) > 0.1f) || (Math.abs(objet.y - objet.ancienneY) > 0.1f);
            
            if (objet.sautillementActif) {
                boolean doitSautiller = false;
                
                if (objet.sautillementInfiniMouvement) {
                    doitSautiller = estEnMouvement;
                } else {
                    long now = System.currentTimeMillis();
                    if (now - objet.tempsDebutSautillement < objet.sautillementDureeMs) {
                        doitSautiller = true;
                    } else {
                        objet.sautillementActif = false;
                    }
                }
                
                if (doitSautiller) {
                    float shakeX = (float) ((Math.random() - 0.5) * 2.0 * objet.sautillementIntensite);
                    float shakeY = (float) ((Math.random() - 0.5) * 2.0 * objet.sautillementIntensite);
                    absMatrix.postTranslate(shakeX, shakeY);
                }
            }
            
            objet.ancienneX = objet.x;
            objet.ancienneY = objet.y;
            
            canvas.save();
            canvas.concat(absMatrix);

            if (objet.surbrillanceActive) {
                Paint pSurbrillance = new Paint();
                pSurbrillance.setStyle(Paint.Style.STROKE);
                pSurbrillance.setStrokeWidth(6f);
                pSurbrillance.setAntiAlias(true);
                
                int colorGlow = Color.YELLOW;
                switch(objet.couleurSurbrillance) {
                    case "Bleu": colorGlow = Color.BLUE; break;
                    case "Rouge": colorGlow = Color.RED; break;
                    case "Vert": colorGlow = Color.GREEN; break;
                    case "Blanc": colorGlow = Color.WHITE; break;
                    case "Magenta": colorGlow = Color.MAGENTA; break;
                    case "Cyan": colorGlow = Color.CYAN; break;
                    case "Noir": colorGlow = Color.BLACK; break;
                }
                pSurbrillance.setColor(colorGlow);
                
                if ("rond".equals(objet.type) || "joystick".equals(objet.type) || "bouton_action".equals(objet.type)) {
                    float rayon = Math.min(objet.largeur, objet.hauteur) / 2f;
                    canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayon + 3f, pSurbrillance);
                } else {
                    canvas.drawRect(-3f, -3f, objet.largeur + 3f, objet.hauteur + 3f, pSurbrillance);
                }
            }

            String cheminAAfficher = objet.cheminImage;
            
            if ("bouton".equals(objet.type)) {
                if (objet.estDesactive && objet.cheminImageDesactive != null) cheminAAfficher = objet.cheminImageDesactive;
                else if (objet == objetEnGlissement && objet.cheminImagePresse != null) cheminAAfficher = objet.cheminImagePresse;
            }

            if ("joystick".equals(objet.type)) {
                float rayonBase = Math.min(objet.largeur, objet.hauteur) / 2f;
                canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayonBase, peintureObjet);
                dessinerImage(canvas, objet, cheminAAfficher);
                
                Paint pStick = new Paint();
                pStick.setColor(Color.WHITE);
                pStick.setAlpha(200);
                float stickX = (objet.largeur / 2f) + (GestionnaireControles.joyDirX * rayonBase * 0.5f);
                float stickY = (objet.hauteur / 2f) + (GestionnaireControles.joyDirY * rayonBase * 0.5f);
                canvas.drawCircle(stickX, stickY, rayonBase * 0.4f, pStick);
                
            } else if ("bouton_action".equals(objet.type)) {
                float rayonBase = Math.min(objet.largeur, objet.hauteur) / 2f;
                if (GestionnaireControles.isActionPressed) {
                    peintureObjet.setAlpha(Math.min(255, alphaInt + 50));
                }
                canvas.drawCircle(objet.largeur / 2f, objet.hauteur / 2f, rayonBase, peintureObjet);
                dessinerImage(canvas, objet, cheminAAfficher);
                
                peintureTexte.setColor(Color.WHITE);
                peintureTexte.setTextSize(objet.largeur * 0.25f);
                peintureTexte.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("ACTION", objet.largeur / 2f, (objet.hauteur / 2f) + (peintureTexte.getTextSize() / 3f), peintureTexte);
                peintureTexte.setTextAlign(Paint.Align.LEFT);
                
            } else if ("rond".equals(objet.type)) {
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
                } else { peintureTexte.setTypeface(android.graphics.Typeface.DEFAULT); }

                peintureTexte.setTextSize(objet.tailleFonte);
                peintureTexte.setTextScaleX(1.0f);
                float hauteurLigne = objet.tailleFonte * 1.2f;
                float currentY = hauteurLigne; 
                float largeurMax = objet.largeur > 0 ? objet.largeur : 1f;
                String[] paragraphes = texteAAfficher.split("\n", -1);
                for (String paragraphe : paragraphes) {
                    if (paragraphe.isEmpty()) { currentY += hauteurLigne; continue; }
                    int start = 0;
                    while (start < paragraphe.length()) {
                        int count = peintureTexte.breakText(paragraphe, start, paragraphe.length(), true, largeurMax, null);
                        if (count <= 0) count = 1;
                        int end = start + count;
                        if (end < paragraphe.length()) {
                            int dernierEspace = paragraphe.lastIndexOf(' ', end - 1);
                            if (dernierEspace > start) end = dernierEspace + 1;
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
        
        if (GestionnaireControles.modeAventureActif && (GestionnaireControles.joyDirX != 0 || GestionnaireControles.joyDirY != 0)) {
            ObjetBase joystickObj = trouverObjetParType("joystick");
            if (joystickObj != null && joystickObj.cibleJoystickId != null && sceneActive != null && sceneActive.objets != null) {
                ObjetBase joueurCible = getObjetById(joystickObj.cibleJoystickId, sceneActive.objets);
                if (joueurCible != null) {
                    float vitesseDefaut = 5f; 
                    float moveX = GestionnaireControles.joyDirX * vitesseDefaut;
                    float moveY = GestionnaireControles.joyDirY * vitesseDefaut;
                    deplacerAvecCollision(joueurCible, moveX, moveY, sceneActive.objets);
                }
            }
        }

        if (sceneActive != null && sceneActive.objets != null) {
            for (ObjetBase obj : sceneActive.objets) {
                
                if (obj.intentionDeplacementX != 0f || obj.intentionDeplacementY != 0f) {
                    deplacerAvecCollision(obj, obj.intentionDeplacementX, obj.intentionDeplacementY, sceneActive.objets);
                    obj.intentionDeplacementX = 0f;
                    obj.intentionDeplacementY = 0f;
                }
                
                if (obj.vitesseAvanceContinue != 0f) {
                    double rad = Math.toRadians(obj.rotation);
                    float dX = (float)(Math.cos(rad) * obj.vitesseAvanceContinue);
                    float dY = (float)(Math.sin(rad) * obj.vitesseAvanceContinue);
                    deplacerAvecCollision(obj, dX, dY, sceneActive.objets);
                }
                
                if (obj.idCiblePoursuite != null && obj.vitessePoursuite != 0f) {
                    ObjetBase cible = getObjetById(obj.idCiblePoursuite, sceneActive.objets);
                    if (cible != null) {
                        float centreAX = obj.x + (obj.largeur / 2f);
                        float centreAY = obj.y + (obj.hauteur / 2f);
                        float centreBX = cible.x + (cible.largeur / 2f);
                        float centreBY = cible.y + (cible.hauteur / 2f);
                        
                        float dx = centreBX - centreAX;
                        float dy = centreBY - centreAY;
                        double dist = Math.hypot(dx, dy);
                        
                        if (dist > 0) {
                            float moveX = (float) ((dx / dist) * obj.vitessePoursuite);
                            float moveY = (float) ((dy / dist) * obj.vitessePoursuite);
                            
                            if (obj.fuiteActive) {
                                deplacerAvecCollision(obj, -moveX, -moveY, sceneActive.objets);
                                obj.rotation = (float) Math.toDegrees(Math.atan2(-dy, -dx));
                            } else {
                                deplacerAvecCollision(obj, moveX, moveY, sceneActive.objets);
                                obj.rotation = (float) Math.toDegrees(Math.atan2(dy, dx));
                            }
                        }
                    }
                }
            }
        }

        if (this.moteurPhysique != null && sceneActive != null && sceneActive.objets != null) {
            List<ObjetBase> chocs = this.moteurPhysique.mettreAJour(sceneActive.objets);
            if (this.moteur != null && !chocs.isEmpty()) {
                for (ObjetBase objChoque : chocs) this.moteur.executerEvenementSurObjet(NoeudEventChoc.class, objChoque);
            }
        }

        if (this.moteur != null && sceneActive != null && sceneActive.objets != null) {
            this.moteur.executerEvenement(NoeudEventChaqueImage.class); 
            this.moteur.verifierCollisions(this, sceneActive.objets);
            this.moteur.verifierVariablesChangees(); 
        }
        if (this.moteurHud != null && sceneHudActive != null && sceneHudActive.objets != null) {
            this.moteurHud.executerEvenement(NoeudEventChaqueImage.class); 
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

        if (GestionnaireControles.cameraCibleId != null && sceneActive != null) {
            ObjetBase cible = getObjetById(GestionnaireControles.cameraCibleId, sceneActive.objets);
            if (cible != null) {
                float cibleCamX = cible.x + (cible.largeur / 2f) - (ConfigurationJeu.LARGEUR_JEU / 2f);
                float cibleCamY = cible.y + (cible.hauteur / 2f) - (ConfigurationJeu.HAUTEUR_JEU / 2f);
                
                if (GestionnaireControles.cameraSuitAxeX) {
                    GestionnaireControles.cameraX += (cibleCamX - GestionnaireControles.cameraX) * vitesseSuiviCamera; 
                }
                if (GestionnaireControles.cameraSuitAxeY) {
                    GestionnaireControles.cameraY += (cibleCamY - GestionnaireControles.cameraY) * vitesseSuiviCamera; 
                }
                
                GestionnaireControles.cameraX = Math.max(GestionnaireControles.limiteMinX, Math.min(GestionnaireControles.cameraX, GestionnaireControles.limiteMaxX - ConfigurationJeu.LARGEUR_JEU));
                GestionnaireControles.cameraY = Math.max(GestionnaireControles.limiteMinY, Math.min(GestionnaireControles.cameraY, GestionnaireControles.limiteMaxY - ConfigurationJeu.HAUTEUR_JEU));
            }
        }

        float shakeX = 0f;
        float shakeY = 0f;
        if (System.currentTimeMillis() < tremblementFin) {
            shakeX = (float) ((Math.random() - 0.5) * 2.0 * tremblementIntensite);
            shakeY = (float) ((Math.random() - 0.5) * 2.0 * tremblementIntensite);
        }

      canvas.save();
        canvas.translate(-GestionnaireControles.cameraX + shakeX, -GestionnaireControles.cameraY + shakeY);
        if (sceneActive != null && sceneActive.objets != null) dessinerListeObjets(canvas, sceneActive.objets, false, GestionnaireControles.cameraX, GestionnaireControles.cameraY);
        canvas.restore(); 

        if (sceneHudActive != null && sceneHudActive.objets != null) dessinerListeObjets(canvas, sceneHudActive.objets, false, 0f, 0f);
    }
}
// bas 5
