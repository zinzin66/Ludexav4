// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Traducteur {
    private static final String TAG = "Traducteur";
    private static final Map<String, String> dictionnaire = new HashMap<>();
    private static String langueActuelle = "fr";

    public static void initialiser(Context context, String langue) {
        langueActuelle = langue;
        dictionnaire.clear();

        String nomFichier = "lang_" + langueActuelle + ".json";

        try {
            InputStream is = context.getAssets().open(nomFichier);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                dictionnaire.put(key, jsonObject.getString(key));
            }
            Log.d(TAG, "Dictionnaire " + langueActuelle + " chargé avec succès. (" + dictionnaire.size() + " entrées)");
            
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement du fichier de langue : " + nomFichier, e);
        }
    }

    public static String get(String cle) {
        if (dictionnaire.containsKey(cle)) {
            return dictionnaire.get(cle);
        }
        // MODIFICATION : On renvoie la clé elle-même au lieu de "[cle]"
        // Cela permet de ne pas casser l'affichage si on demande la traduction d'un nom de dossier ou de nœud non traduit !
        return cle; 
    }

    public static String getLangueActuelle() {
        return langueActuelle;
    }
}
// bas 1
