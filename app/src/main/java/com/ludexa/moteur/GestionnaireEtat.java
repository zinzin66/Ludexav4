// haut 1
package com.ludexa.moteur;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireEtat {
    
    // Dictionnaires pour stocker le JSON des objets et des variables pour chaque ID de scène
    private static Map<String, String> cacheObjets = new HashMap<>();
    private static Map<String, String> cacheVariables = new HashMap<>();
    private static Gson gson = new Gson();

    // Appelé au démarrage du mode Play pour repartir à zéro
    public static void viderCache() {
        cacheObjets.clear();
        cacheVariables.clear();
    }

    public static void sauvegarderEtat(Scene scene) {
        if (scene == null || scene.id == null) return;
        
        // On sauvegarde la liste des objets
        String jsonObjets = gson.toJson(scene.objets);
        cacheObjets.put(scene.id, jsonObjets);
        
        // On sauvegarde les variables locales
        String jsonVariables = gson.toJson(scene.variablesLocales);
        cacheVariables.put(scene.id, jsonVariables);
    }

    public static void restaurerEtat(Scene scene) {
        if (scene == null || scene.id == null) return;

        // Restauration des objets s'ils existent dans le cache
        if (cacheObjets.containsKey(scene.id)) {
            String jsonObjets = cacheObjets.get(scene.id);
            Type typeListObjets = new TypeToken<ArrayList<ObjetBase>>(){}.getType();
            List<ObjetBase> objetsRestaures = gson.fromJson(jsonObjets, typeListObjets);
            if (objetsRestaures != null) {
                scene.objets = objetsRestaures;
            }
        }

        // Restauration des variables locales si elles existent dans le cache
        if (cacheVariables.containsKey(scene.id)) {
            String jsonVariables = cacheVariables.get(scene.id);
            Type typeListVariables = new TypeToken<ArrayList<Variable>>(){}.getType();
            List<Variable> variablesRestaurees = gson.fromJson(jsonVariables, typeListVariables);
            if (variablesRestaurees != null) {
                scene.variablesLocales = variablesRestaurees;
            }
        }
    }
}
// bas 1
