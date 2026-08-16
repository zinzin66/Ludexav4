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
        // ÉVÉNEMENTS
        REGISTRE.add(new InfoNoeud("Au Démarrage", "Événements", "NoeudEventStart"));
        REGISTRE.add(new InfoNoeud("Fin de Clic", "Événements", "NoeudEventFinClic"));
        REGISTRE.add(new InfoNoeud("Au Clic sur Objet", "Événements", "NoeudEventClicObjet"));
        REGISTRE.add(new InfoNoeud("Début de Glisser", "Événements", "NoeudEventDebutGlisser"));
        REGISTRE.add(new InfoNoeud("Fin de Glisser", "Événements", "NoeudEventFinGlisser"));
        REGISTRE.add(new InfoNoeud("Collision A/B", "Événements", "NoeudEventCollisionAB"));
        REGISTRE.add(new InfoNoeud("Entrée de zone", "Événements", "NoeudEventEntreeZone")); 
        REGISTRE.add(new InfoNoeud("Sortie de zone", "Événements", "NoeudEventSortieZone"));
        REGISTRE.add(new InfoNoeud("Au Survol", "Événements", "NoeudEventSurvolObjet"));
        REGISTRE.add(new InfoNoeud("Fin de Survol", "Événements", "NoeudEventFinSurvol"));
        REGISTRE.add(new InfoNoeud("Au Choc (Physique)", "Événements", "NoeudEventChoc"));
        REGISTRE.add(new InfoNoeud("Quand Variable Change", "Événements", "NoeudEventVariableChange"));
        REGISTRE.add(new InfoNoeud("Événement Local", "Événements", "NoeudEventPersonnalise"));
        REGISTRE.add(new InfoNoeud("Au Clic Action (Aventure)", "Événements", "NoeudEventBoutonAction"));

        // MOUVEMENTS & IA
        REGISTRE.add(new InfoNoeud("Avancer en continu", "Mouvements & IA", "NoeudActionAvancerContinu"));
        REGISTRE.add(new InfoNoeud("Poursuivre un objet", "Mouvements & IA", "NoeudActionPoursuivre"));
        REGISTRE.add(new InfoNoeud("Fuir un objet", "Mouvements & IA", "NoeudActionFuir"));
        REGISTRE.add(new InfoNoeud("Stopper les mouvements", "Mouvements & IA", "NoeudActionStopperMouvements"));

        // LOGIQUE SPATIALE
        REGISTRE.add(new InfoNoeud("Distance entre A et B", "Logique Spatiale", "NoeudConditionDistance"));
        REGISTRE.add(new InfoNoeud("Orienter vers (Look At)", "Logique Spatiale", "NoeudActionOrienterVers"));

        // LOGIQUE & CONDITIONS
        REGISTRE.add(new InfoNoeud("Condition", "Logique & Conditions", "NoeudConditionComparaison"));
        REGISTRE.add(new InfoNoeud("Si objet A touche zone B", "Logique & Conditions", "NoeudConditionSiObjetToucheZone"));
        REGISTRE.add(new InfoNoeud("Si Objet Visible", "Logique & Conditions", "NoeudConditionSiObjetVisible"));
        REGISTRE.add(new InfoNoeud("Si Objet a le Tag", "Logique & Conditions", "NoeudConditionTag"));
        REGISTRE.add(new InfoNoeud("Condition Double (ET/OU)", "Logique & Conditions", "NoeudConditionDouble"));
        REGISTRE.add(new InfoNoeud("Appeler Fonction", "Logique & Conditions", "NoeudAppelFonction"));
        REGISTRE.add(new InfoNoeud("Appeler Événement Local", "Logique & Conditions", "NoeudActionAppelerEvent"));
        REGISTRE.add(new InfoNoeud("Si Joystick Actif", "Logique & Conditions", "NoeudConditionSiJoystick"));
        
        // SCÈNE & HUD
        REGISTRE.add(new InfoNoeud("Changer de Scène", "Scène & HUD", "NoeudActionChangerScene"));
        REGISTRE.add(new InfoNoeud("Recharger Scène actuelle", "Scène & HUD", "NoeudActionRechargerScene"));
        REGISTRE.add(new InfoNoeud("Ouvrir HUD", "Scène & HUD", "NoeudActionOuvrirHUD"));
        REGISTRE.add(new InfoNoeud("Fermer HUD", "Scène & HUD", "NoeudActionFermerHUD"));
        REGISTRE.add(new InfoNoeud("Toast", "Scène & HUD", "NoeudActionToast"));
        REGISTRE.add(new InfoNoeud("Fixer Caméra", "Scène & HUD", "NoeudActionFixerCamera"));
        REGISTRE.add(new InfoNoeud("Tremblement de Caméra", "Scène & HUD", "NoeudActionTremblement"));
        REGISTRE.add(new InfoNoeud("Élasticité Caméra", "Scène & HUD", "NoeudActionParametresCamera"));
        REGISTRE.add(new InfoNoeud("Afficher/Masquer Joystick", "Scène & HUD", "NoeudActionJoystick"));
        REGISTRE.add(new InfoNoeud("Afficher/Masquer Action", "Scène & HUD", "NoeudActionBoutonAction"));
        REGISTRE.add(new InfoNoeud("Vibration", "Scène & HUD", "NoeudActionVibration"));

        // APPARENCE & OBJETS
        REGISTRE.add(new InfoNoeud("Déplacer Objet", "Apparence & Objets", "NoeudActionDeplacer"));
        REGISTRE.add(new InfoNoeud("Modifier Couleur", "Apparence & Objets", "NoeudActionModifierCouleur"));
        REGISTRE.add(new InfoNoeud("Visibilité", "Apparence & Objets", "NoeudActionVisibilite"));
        REGISTRE.add(new InfoNoeud("Modifier Déplaçable", "Apparence & Objets", "NoeudActionModifierDeplacable"));
        REGISTRE.add(new InfoNoeud("Modifier Verrouillage", "Apparence & Objets", "NoeudActionModifierVerrouillage"));
        REGISTRE.add(new InfoNoeud("Changer Z-Order", "Apparence & Objets", "NoeudActionChangerZOrder"));
        REGISTRE.add(new InfoNoeud("Changer Image", "Apparence & Objets", "NoeudActionChangerImage"));
        REGISTRE.add(new InfoNoeud("Rotation", "Apparence & Objets", "NoeudActionRotation"));
        REGISTRE.add(new InfoNoeud("Définir la taille (Scale)", "Apparence & Objets", "NoeudActionModifierTaille"));
        REGISTRE.add(new InfoNoeud("Détruire Objet", "Apparence & Objets", "NoeudActionDetruireObjet"));
        REGISTRE.add(new InfoNoeud("Générer un clone (Spawner)", "Apparence & Objets", "NoeudActionSpawner"));
        REGISTRE.add(new InfoNoeud("Définir le Tag", "Apparence & Objets", "NoeudActionDefinirTag"));
        REGISTRE.add(new InfoNoeud("Détruire par Tag", "Apparence & Objets", "NoeudActionDetruireParTag"));
        REGISTRE.add(new InfoNoeud("Filtre Couleur", "Apparence & Objets", "NoeudActionFiltre"));
        REGISTRE.add(new InfoNoeud("Clignotement (Blink)", "Apparence & Objets", "NoeudActionClignotement"));
        REGISTRE.add(new InfoNoeud("Surbrillance (Glow)", "Apparence & Objets", "NoeudActionSurbrillance"));
        
        // TEXTES & DIALOGUES
        REGISTRE.add(new InfoNoeud("Modifier Texte", "Textes & Dialogues", "NoeudActionModifierTexte"));
        REGISTRE.add(new InfoNoeud("Ajouter au Texte", "Textes & Dialogues", "NoeudActionConcatenerTexte"));
        REGISTRE.add(new InfoNoeud("Afficher Dialogue", "Textes & Dialogues", "NoeudActionAfficherDialogue"));

        // VARIABLES & INVENTAIRE
        REGISTRE.add(new InfoNoeud("Opération Mathématique", "Variables & Inventaire", "NoeudActionOperationMath"));
        REGISTRE.add(new InfoNoeud("Limiter Valeur (Clamp)", "Variables & Inventaire", "NoeudActionClampVariable"));
        REGISTRE.add(new InfoNoeud("Modifier Variable", "Variables & Inventaire", "NoeudActionModifierVariable"));
        REGISTRE.add(new InfoNoeud("Ajouter à Variable", "Variables & Inventaire", "NoeudActionAjouterVariable"));
        REGISTRE.add(new InfoNoeud("Nombre Aléatoire", "Variables & Inventaire", "NoeudActionNombreAleatoire"));
        REGISTRE.add(new InfoNoeud("Ajouter à l'inventaire", "Variables & Inventaire", "NoeudActionAjouterInventaire"));
        REGISTRE.add(new InfoNoeud("Retirer de l'inventaire", "Variables & Inventaire", "NoeudActionRetirerInventaire"));
        REGISTRE.add(new InfoNoeud("Si dans l'inventaire", "Variables & Inventaire", "NoeudConditionSiDansInventaire"));
        REGISTRE.add(new InfoNoeud("Combinaison d'Objets", "Variables & Inventaire", "NoeudActionCombinaison"));
        REGISTRE.add(new InfoNoeud("Point de Sauvegarde", "Variables & Inventaire", "NoeudActionCheckpoint"));

        // TEMPS
        REGISTRE.add(new InfoNoeud("Timer", "Temps", "NoeudActionTimer"));
        REGISTRE.add(new InfoNoeud("Chrono", "Temps", "NoeudActionChrono"));
        REGISTRE.add(new InfoNoeud("Attendre (ms)", "Temps", "NoeudActionAttendre"));

        // AUDIO
        REGISTRE.add(new InfoNoeud("Jouer un Son", "Audio", "NoeudActionJouerSon"));
        REGISTRE.add(new InfoNoeud("Gérer Musique", "Audio", "NoeudActionMusique"));

        // ANIMATIONS
        REGISTRE.add(new InfoNoeud("Fondu (Alpha)", "Animations", "NoeudActionFondu"));
        REGISTRE.add(new InfoNoeud("Glisser Vers", "Animations", "NoeudActionGlisserVers"));
        REGISTRE.add(new InfoNoeud("Jouer Animation", "Animations", "NoeudActionJouerAnimation"));
        
        // PHYSIQUE
        REGISTRE.add(new InfoNoeud("Activer Physique (Chute)", "Physique", "NoeudActionModifierPhysique"));
        REGISTRE.add(new InfoNoeud("Appliquer Impulsion (Saut)", "Physique", "NoeudActionImpulsion"));
        REGISTRE.add(new InfoNoeud("Changer Rebond", "Physique", "NoeudActionChangerRebond"));
        REGISTRE.add(new InfoNoeud("Si Objet en Chute", "Physique", "NoeudConditionEnMouvement"));
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
