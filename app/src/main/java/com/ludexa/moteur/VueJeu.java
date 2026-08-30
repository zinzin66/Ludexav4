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

        // NOUVEAU : on tient à jour les références statiques de NoeudBase dès la construction.
        // C'est ce qui permet à getCibleObjet()/neutraliserEtRetirer() de retrouver la bonne
        // scène sans réflexion, aussi bien en APK qu'en mode test-éditeur (basculerVersJeu()).
        NoeudBase.sceneActiveCourante = this.sceneActive;
        NoeudBase.sceneHudActiveCourante = this.sceneHudActive;

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
    this.moteur.setCheminProjet(this.cheminProjet);
}

if (blueprintHud != null) {
    this.moteurHud = new MoteurLogique(blueprintHud);
    this.moteurHud.setCheminProjet(this.cheminProjet);
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
    private void logDiag(String message) {
        DiagLogger.log(cheminProjet, message);
    }

    private void majCibleNoeud(NoeudBase noeud, String nomChamp, java.util.Map<String, String> mapRemplacement) {
        try {
            java.lang.reflect.Field champ = null;
            Class<?> c = noeud.getClass();
            while (c != null && champ == null) {
                try { champ = c.getDeclaredField(nomChamp); } 
                catch (Exception e) { c = c.getSuperclass(); }
            }
            if (champ != null) {
                champ.setAccessible(true);
                String ancienneVal = (String) champ.get(noeud);
                if (ancienneVal != null && !"__OBJET_IMPLIQUE__".equals(ancienneVal) && mapRemplacement.containsKey(ancienneVal)) {
                    champ.set(noeud, mapRemplacement.get(ancienneVal));
                }
            }
        } catch (Exception e) {}
    }

    public void setSceneHud(Scene scene) {
        this.sceneHudActive = scene;
        NoeudBase.sceneHudActiveCourante = this.sceneHudActive;
        if (scene != null) chargerAnimationsGlobales(scene.objets);
        if (scene == null) this.moteurHud = null;
    }

    public void ouvrirHudDynamique(Scene scene, Blueprint blueprintHud) {
        if (this.sceneHudActive != null && this.sceneHudActive == scene && this.moteurHud != null) {
            this.sceneHudActive = scene; 
            NoeudBase.sceneHudActiveCourante = this.sceneHudActive;
            return;
        }

        this.sceneHudActive = scene;
        NoeudBase.sceneHudActiveCourante = this.sceneHudActive;
        if (scene != null) chargerAnimationsGlobales(scene.objets);
        
        deballerPrefabs(this.sceneHudActive);

        if (blueprintHud != null) {
    this.moteurHud = new MoteurLogique(blueprintHud);
    this.moteurHud.setCheminProjet(this.cheminProjet);
    this.moteurHud.executerDemarrage();
        } else {
            this.moteurHud = null;
        }
    }

    public void chargerNouvelleScene(Scene nouvelleScene) {
        if (nouvelleScene == null) return;
        if (this.sceneActive != null) GestionnaireEtat.sauvegarderEtat(this.sceneActive);

        this.sceneActive = nouvelleScene;
        NoeudBase.sceneActiveCourante = this.sceneActive;
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
    this.moteur.setCheminProjet(this.cheminProjet);
    this.moteur.executerDemarrage();
        } else {
            this.moteur = null; 
        }
    }

    private java.util.Set<String> pilesInstanciationEnCours = new java.util.HashSet<>();

    public void instancierScene(Scene sceneAInstancier, float offsetX, float offsetY) {
        ObjetBase dummyPrefab = new ObjetBase("Prefab_Dynamique", offsetX, offsetY, 0, 0);
        dummyPrefab.type = "scene_instance";
        if (sceneAInstancier != null) dummyPrefab.sceneLieeId = sceneAInstancier.id;
        instancierScene(sceneAInstancier, dummyPrefab);
    }

    public void instancierScene(Scene sceneAInstancier, ObjetBase prefab) {
        if (sceneAInstancier == null || sceneActive == null || sceneAInstancier == sceneActive || prefab == null) return;
        if (pilesInstanciationEnCours.contains(sceneAInstancier.id)) {
            logDiag("ATTENTION: cycle de prefabs détecté et ignoré pour la scène " + sceneAInstancier.nom + " (prefab=" + prefab.nom + ")");
            return;
        }
        pilesInstanciationEnCours.add(sceneAInstancier.id);
        try {
            instancierSceneInterne(sceneAInstancier, prefab);
        } finally {
            pilesInstanciationEnCours.remove(sceneAInstancier.id);
        }
    }

    private void instancierSceneInterne(Scene sceneAInstancier, ObjetBase prefab) {
        float offsetX = prefab.x;
        float offsetY = prefab.y;
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
                    
                    String idOriginal = sceneAInstancier.id;
                    sceneAInstancier.id = idOriginal + "_" + java.util.UUID.randomUUID().toString().substring(0, 5);
                    blueprintInstance = Blueprint.fromJson(sb.toString(), sceneAInstancier);
                    sceneAInstancier.id = idOriginal;
                }
            } catch (Exception e) {
                logDiag("ERREUR: exception au chargement du Blueprint pour prefab=" + prefab.nom + " scene=" + sceneAInstancier.nom + " : " + e.toString());
            }
        }
        
        java.util.Map<String, String> mapIds = new java.util.HashMap<>();
        java.util.Map<String, String> mapVars = new java.util.HashMap<>();
        java.util.Map<String, String> mapNoms = new java.util.HashMap<>();

        if (sceneAInstancier.variablesLocales != null) {
            if (sceneActive.variablesLocales == null) sceneActive.variablesLocales = new ArrayList<>();
            for (Variable varOrigine : sceneAInstancier.variablesLocales) {
                Variable varClone = new Variable(varOrigine.nom, varOrigine.scope, varOrigine.type);
                varClone.valeur = varOrigine.valeur;
                
                if (prefab.surchargesVariables != null && prefab.surchargesVariables.containsKey(varOrigine.nom)) {
                    String valSurcharge = prefab.surchargesVariables.get(varOrigine.nom);
                    if ("CHIFFRE".equals(varOrigine.type)) {
                        try { varClone.valeur = Float.parseFloat(valSurcharge); } catch (Exception e) {}
                    } else if ("TEXTE".equals(varOrigine.type)) {
                        varClone.valeur = valSurcharge;
                    } else if ("BOOLEEN".equals(varOrigine.type)) {
                        String clean = valSurcharge.toLowerCase();
                        varClone.valeur = (clean.equals("oui") || clean.equals("vrai") || clean.equals("true"));
                    } else if ("ENTIER".equals(varOrigine.type)) {
                        try { varClone.valeur = Integer.parseInt(valSurcharge); } catch (Exception e) {}
                    }
                }

                String nouveauNomVar = prefab.id + "_" + varOrigine.nom;
                mapVars.put(varOrigine.nom, nouveauNomVar);
                varClone.nom = nouveauNomVar;
                sceneActive.variablesLocales.add(varClone);
            }
        }

        java.util.Map<String, ObjetBase> mapNomOrigineVersClone = new java.util.HashMap<>();

        if (sceneAInstancier.objets != null) {
            List<ObjetBase> objetsInjectes = new ArrayList<>();
            for (ObjetBase objOrigine : sceneAInstancier.objets) {
                ObjetBase clone = objOrigine.clonerProfond();
                
                String nouvelId = java.util.UUID.randomUUID().toString();
                mapIds.put(objOrigine.id, nouvelId); 
                clone.id = nouvelId;
                
                String nouveauNom = objOrigine.nom + "_" + nouvelId.substring(0, 5);
                mapNoms.put(objOrigine.nom, nouveauNom);
                clone.nom = nouveauNom;
                
                mapNomOrigineVersClone.put(objOrigine.nom, clone);
                
                clone.x += offsetX;
                clone.y += offsetY;
                clone.sceneLieeId = sceneAInstancier.id;
                
                objetsInjectes.add(clone);
            }
            
            for (ObjetBase clone : objetsInjectes) {
                if (clone.parentId == null) {
                    if (prefab.tag != null && !prefab.tag.trim().isEmpty()) {
                        clone.tag = prefab.tag;
                    }
                    if (prefab.estPhysique) {
                        clone.estPhysique = true;
                        clone.estStatique = prefab.estStatique;
                        clone.graviteScale = prefab.graviteScale;
                        clone.rebond = prefab.rebond;
                    }
                }
                
                if (clone.parentId != null && mapIds.containsKey(clone.parentId)) clone.parentId = mapIds.get(clone.parentId);
                if (clone.cibleJoystickId != null && mapIds.containsKey(clone.cibleJoystickId)) clone.cibleJoystickId = mapIds.get(clone.cibleJoystickId);
                if (clone.idCiblePoursuite != null && mapIds.containsKey(clone.idCiblePoursuite)) clone.idCiblePoursuite = mapIds.get(clone.idCiblePoursuite);
                sceneActive.ajouterObjet(clone);
            }
            chargerAnimationsGlobales(sceneActive.objets);
        }
        
        if (blueprintInstance != null && blueprintInstance.noeuds != null && this.moteur != null) {
            java.util.Map<String, String> mapNoeudsIds = new java.util.HashMap<>();

            for (NoeudBase noeud : blueprintInstance.noeuds) {
                String nouvelIdNoeud = java.util.UUID.randomUUID().toString();
                mapNoeudsIds.put(noeud.id, nouvelIdNoeud);
                noeud.id = nouvelIdNoeud;
                noeud.categorie = (noeud.categorie != null ? noeud.categorie + " " : "") + "_INSTANCE_" + sceneAInstancier.id;
            }

            // PASSE 1 : liaison complète de TOUS les nœuds du lot avant toute exécution.
            for (NoeudBase noeud : blueprintInstance.noeuds) {
                
                majCibleNoeud(noeud, "nomCible", mapNoms); 
                
                try {
                    java.lang.reflect.Field champVar = noeud.getClass().getField("nomVariable");
                    String ancienneVar = (String) champVar.get(noeud);
                    if (ancienneVar != null && mapVars.containsKey(ancienneVar)) champVar.set(noeud, mapVars.get(ancienneVar));
                } catch (Exception e) {}
                
                try {
                    java.lang.reflect.Field champVar2 = noeud.getClass().getField("cibleVariableId");
                    String ancienneVar2 = (String) champVar2.get(noeud);
                    if (ancienneVar2 != null && mapVars.containsKey(ancienneVar2)) champVar2.set(noeud, mapVars.get(ancienneVar2));
                } catch (Exception e) {}

                try {
                    java.lang.reflect.Field champSuivant = noeud.getClass().getField("noeudSuivantId");
                    String ancienSuivant = (String) champSuivant.get(noeud);
                    if (ancienSuivant != null && mapNoeudsIds.containsKey(ancienSuivant)) champSuivant.set(noeud, mapNoeudsIds.get(ancienSuivant));
                } catch (Exception e) {}

                try {
                    java.lang.reflect.Field champVrai = noeud.getClass().getField("noeudSuivantVraiId");
                    String ancienVrai = (String) champVrai.get(noeud);
                    if (ancienVrai != null && mapNoeudsIds.containsKey(ancienVrai)) champVrai.set(noeud, mapNoeudsIds.get(ancienVrai));
                } catch (Exception e) {}

                try {
                    java.lang.reflect.Field champFaux = noeud.getClass().getField("noeudSuivantFauxId");
                    String ancienFaux = (String) champFaux.get(noeud);
                    if (ancienFaux != null && mapNoeudsIds.containsKey(ancienFaux)) champFaux.set(noeud, mapNoeudsIds.get(ancienFaux));
                } catch (Exception e) {}
                
                if (noeud.requiertCibleObjet() && noeud.nomCibleObjet != null
                    && !"__OBJET_IMPLIQUE__".equals(noeud.nomCibleObjet)) {
                    ObjetBase cibleResolue = mapNomOrigineVersClone.get(noeud.nomCibleObjet);
                    if (cibleResolue != null) {
                        noeud.lierCibleObjetInstance(cibleResolue);
                    }
                }
                if (noeud.requiertCibleObjetB() && noeud.nomCibleObjetB != null
                    && !"__OBJET_IMPLIQUE__".equals(noeud.nomCibleObjetB)) {
                    ObjetBase cibleBResolue = mapNomOrigineVersClone.get(noeud.nomCibleObjetB);
                    if (cibleBResolue != null) {
                        noeud.lierCibleObjetBInstance(cibleBResolue);
                    }
                }
                
                this.moteur.ajouterNoeudAuBlueprintActif(noeud);

                // VÉRIFICATION AUTOMATIQUE DE CÂBLAGE : si un nœud a besoin d'une cible et
                // qu'elle est toujours introuvable après la tentative de liaison, on le
                // signale dans le journal permanent du projet. C'est ce contrôle qui aurait
                // détecté immédiatement le bug du champ nomCibleObjet dupliqué dans
                // NoeudActionJouerAnimation, au lieu de 2 jours d'investigation manuelle.
                if (noeud.requiertCibleObjet() && !"__OBJET_IMPLIQUE__".equals(noeud.nomCibleObjet)) {
                    if (noeud.getCibleObjet() == null) {
                        logDiag("ATTENTION: nœud " + noeud.getClass().getSimpleName() + " du prefab " + prefab.nom + " (scène " + sceneAInstancier.nom + ") sans cible objet valide après liaison (nomCibleObjet=" + noeud.nomCibleObjet + ")");
                    }
                }
                if (noeud.requiertCibleObjetB() && !"__OBJET_IMPLIQUE__".equals(noeud.nomCibleObjetB)) {
                    if (noeud.getCibleObjetB() == null) {
                        logDiag("ATTENTION: nœud " + noeud.getClass().getSimpleName() + " du prefab " + prefab.nom + " (scène " + sceneAInstancier.nom + ") sans cible objet B valide après liaison (nomCibleObjetB=" + noeud.nomCibleObjetB + ")");
                    }
                }
            }

            // PASSE 2 : exécution des événements de démarrage, une fois le lot entièrement lié.
            for (NoeudBase noeud : blueprintInstance.noeuds) {
                if (noeud instanceof NoeudEventStart) {
                    noeud.executer();
                }
            }
        }
        
        // SUPPRIMÉ ICI : Le déballage récursif de sceneActive qui provoquait la fausse boucle infinie (cycle).
    }
// bas 2

// haut 3
    public void detruireInstances(Scene sceneCible) {
        if (sceneCible == null || sceneActive == null || sceneActive.objets == null) return;
        
        java.util.Iterator<ObjetBase> itObj = sceneActive.objets.iterator();
        while (itObj.hasNext()) {
            ObjetBase obj = itObj.next();
            if (obj.sceneLieeId != null && obj.sceneLieeId.equals(sceneCible.id) && !"scene_instance".equals(obj.type)) {
                itObj.remove();
            }
        }
        
        if (this.moteur != null) {
            String tagRecherche = "_INSTANCE_" + sceneCible.id;
            this.moteur.nettoyerNoeudsParTag(tagRecherche);
        }
    }

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
            } catch (Exception e) {}
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
        
        // CORRECTION : Boucle d'itération sécurisée pour traiter les prefabs un par un,
        // sans déclencher le bloqueur de boucle infinie (cycle detector) sur les frères.
        boolean aDeballe = true;
        while (aDeballe) {
            aDeballe = false;
            ObjetBase prefabATraiter = null;
            
            for (ObjetBase obj : scene.objets) {
                if ("scene_instance".equals(obj.type) && obj.sceneLieeId != null && obj.visible) {
                    prefabATraiter = obj;
                    break;
                }
            }
            
            if (prefabATraiter != null) {
                prefabATraiter.visible = false; 
                Scene sceneLiee = getSceneParId(prefabATraiter.sceneLieeId);
                if (sceneLiee != null) {
                    instancierScene(sceneLiee, prefabATraiter);
                }
                
                prefabATraiter.estPhysique = false;
                prefabATraiter.estZoneDeClic = false;
                prefabATraiter.tag = ""; 
                prefabATraiter.x = -99999;
                prefabATraiter.y = -99999;
                
                aDeballe = true; 
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
// bas 3
// haut 4
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
