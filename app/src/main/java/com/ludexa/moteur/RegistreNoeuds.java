// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistreNoeuds {
    public static class InfoNoeud {
        public String libelle;
        public String categorie;
        public String classeType;

        public InfoNoeud(String libelle, String categorie, String classeType) {
            this.libelle = libelle;
            this.categorie = categorie;
            this.classeType = classeType;
        }
    }

    public static final List<InfoNoeud> REGISTRE = new ArrayList<>();

    static {
        REGISTRE.add(new InfoNoeud("Au Démarrage", "Événements", "NoeudEventStart"));
        REGISTRE.add(new InfoNoeud("Déplacer Objet", "Actions", "NoeudActionDeplacer"));
        REGISTRE.add(new InfoNoeud("Modifier Variable", "Actions", "NoeudActionModifierVariable"));
        REGISTRE.add(new InfoNoeud("Modifier Texte", "Actions", "NoeudActionModifierTexte"));
        // Ligne ajoutée pour le nouveau nœud de couleur
        REGISTRE.add(new InfoNoeud("Modifier Couleur", "Actions", "NoeudActionModifierCouleur"));
        // NOUVEAU : Enregistrement du noeud conditionnel
        REGISTRE.add(new InfoNoeud("Condition", "Logique", "NoeudConditionComparaison"));
    }

    public static Map<String, List<InfoNoeud>> getNoeudsParCategorie() {
        Map<String, List<InfoNoeud>> map = new LinkedHashMap<>();
        for (InfoNoeud info : REGISTRE) {
            if (!map.containsKey(info.categorie)) {
                map.put(info.categorie, new ArrayList<>());
            }
            map.get(info.categorie).add(info);
        }
        return map;
    }
}
// bas 1


