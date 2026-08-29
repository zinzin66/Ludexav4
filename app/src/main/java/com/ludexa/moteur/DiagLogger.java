package com.ludexa.moteur;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class DiagLogger {
    private static final String NOM_FICHIER = "diag_ludexa.txt";

    public static void log(String cheminProjet, String message) {
        if (cheminProjet == null) return;
        try {
            File logFile = new File(cheminProjet, NOM_FICHIER);
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(System.currentTimeMillis() + " - " + message + "\n");
            fw.close();
        } catch (Exception e) {}
    }

    public static String lire(String cheminProjet) {
        if (cheminProjet == null) return "";
        File logFile = new File(cheminProjet, NOM_FICHIER);
        if (!logFile.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(logFile));
            String ligne;
            while ((ligne = br.readLine()) != null) {
                sb.append(ligne).append("\n");
            }
            br.close();
        } catch (Exception e) {}
        return sb.toString();
    }

    public static void effacer(String cheminProjet) {
        if (cheminProjet == null) return;
        File logFile = new File(cheminProjet, NOM_FICHIER);
        if (logFile.exists()) logFile.delete();
    }
}
VueJeu.java — bloc 2 (remplace tout le bloc 2 actuel)
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
