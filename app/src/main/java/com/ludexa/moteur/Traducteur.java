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
            Log.d(TAG, "Dictionnaire " + langueActuelle + " chargé.");
            
        } catch (Exception e) {
            Log.e(TAG, "Erreur fichier de langue : " + nomFichier, e);
        }
    }

    public static String get(String cle) {
        if (cle == null) return "";
        if (dictionnaire.containsKey(cle)) {
            return dictionnaire.get(cle);
        }
        
        // Securités (Fallbacks) au cas où le JSON n'est pas encore à jour
        if (cle.equals("dossier_images")) return "Images";
        if (cle.equals("dossier_sons")) return "Audio";
        if (cle.equals("dossier_fonts")) return "Polices";
        if (cle.equals("dossier_textes")) return "Textes";
        
        if (cle.equals("Suivre X")) return "Suivre l'axe X";
        if (cle.equals("Suivre Y")) return "Suivre l'axe Y";
        
        if (cle.equals("Intensite")) return "Intensité";
        if (cle.equals("Duree")) return "Durée (ms)";
        if (cle.equals("Infini")) return "En Boucle / Infini";
        
        if (cle.equals("noeud_sautiller")) return "Sautillement";
        if (cle.equals("noeud_si_mouvement")) return "Si Objet en Mouvement";
        
        // NOUVEAUX NOEUDS :
        if (cle.equals("noeud_chaque_image")) return "À chaque image";
        if (cle.equals("noeud_arreter")) return "Arrêter l'objet";
        if (cle.equals("noeud_miroir")) return "Effet Miroir / Inverser";
        
        if (cle.equals("port_entree")) return "Entrée";
        if (cle.equals("port_sortie")) return "Sortie";
        if (cle.equals("port_vrai")) return "Vrai";
        if (cle.equals("port_faux")) return "Faux";
        
        if (cle.equals("cat_animations")) return "Animations";

        return "[" + cle + "]";
    }

    public static String getLangueActuelle() {
        return langueActuelle;
    }
}
// bas 1
