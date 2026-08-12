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
        REGISTRE.add(new InfoNoeud("Modifier Couleur", "Actions", "NoeudActionModifierCouleur"));
        REGISTRE.add(new InfoNoeud("Condition", "Logique", "NoeudConditionComparaison"));
        REGISTRE.add(new InfoNoeud("Timer", "Actions", "NoeudActionTimer"));
        REGISTRE.add(new InfoNoeud("Fin de Clic", "Événements", "NoeudEventFinClic"));
        REGISTRE.add(new InfoNoeud("Toast", "Actions", "NoeudActionToast"));
        REGISTRE.add(new InfoNoeud("Ajouter à Variable", "Actions", "NoeudActionAjouterVariable"));
        REGISTRE.add(new InfoNoeud("Visibilité", "Actions", "NoeudActionVisibilite"));
        REGISTRE.add(new InfoNoeud("Modifier Déplaçable", "Actions", "NoeudActionModifierDeplacable"));
        REGISTRE.add(new InfoNoeud("Nombre Aléatoire", "Actions", "NoeudActionNombreAleatoire"));
        
        REGISTRE.add(new InfoNoeud("Ouvrir HUD", "UI", "NoeudActionOuvrirHUD"));
        REGISTRE.add(new InfoNoeud("Fermer HUD", "UI", "NoeudActionFermerHUD"));
        
        REGISTRE.add(new InfoNoeud("Au Clic sur Objet", "Événements", "NoeudEventClicObjet"));
        REGISTRE.add(new InfoNoeud("Début de Glisser", "Événements", "NoeudEventDebutGlisser"));
        REGISTRE.add(new InfoNoeud("Fin de Glisser", "Événements", "NoeudEventFinGlisser"));
        
        REGISTRE.add(new InfoNoeud("Ajouter à l'inventaire", "Actions", "NoeudActionAjouterInventaire"));
        REGISTRE.add(new InfoNoeud("Retirer de l'inventaire", "Actions", "NoeudActionRetirerInventaire"));
        REGISTRE.add(new InfoNoeud("Si dans l'inventaire", "Logique", "NoeudConditionSiDansInventaire"));
        
        REGISTRE.add(new InfoNoeud("Collision A/B", "Événements", "NoeudEventCollisionAB"));
        REGISTRE.add(new InfoNoeud("Si objet A touche zone B", "Logique", "NoeudConditionSiObjetToucheZone"));
        
        REGISTRE.add(new InfoNoeud("Chrono", "Actions", "NoeudActionChrono"));
        
        REGISTRE.add(new InfoNoeud("Ajouter au Texte", "Actions", "NoeudActionConcatenerTexte"));
        REGISTRE.add(new InfoNoeud("Changer Z-Order", "Actions", "NoeudActionChangerZOrder"));
        REGISTRE.add(new InfoNoeud("Changer de Scène", "Actions", "NoeudActionChangerScene"));
        REGISTRE.add(new InfoNoeud("Changer Image", "Actions", "NoeudActionChangerImage"));
        REGISTRE.add(new InfoNoeud("Si Objet Visible", "Logique", "NoeudConditionSiObjetVisible"));
        REGISTRE.add(new InfoNoeud("Modifier Verrouillage", "Actions", "NoeudActionModifierVerrouillage"));
        REGISTRE.add(new InfoNoeud("Quand Variable Change", "Événements", "NoeudEventVariableChange"));
        
        REGISTRE.add(new InfoNoeud("Afficher Dialogue", "Actions", "NoeudActionAfficherDialogue"));
        REGISTRE.add(new InfoNoeud("Au Survol", "Événements", "NoeudEventSurvolObjet"));

        REGISTRE.add(new InfoNoeud("Jouer un Son", "Audio", "NoeudActionJouerSon"));
        REGISTRE.add(new InfoNoeud("Gérer Musique", "Audio", "NoeudActionMusique"));

        // NOUVEAUX NOEUDS INTÉGRÉS
        REGISTRE.add(new InfoNoeud("Fondu (Alpha)", "Animations", "NoeudActionFondu"));
        REGISTRE.add(new InfoNoeud("Glisser Vers", "Animations", "NoeudActionGlisserVers"));
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
