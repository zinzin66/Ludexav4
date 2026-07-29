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
        
        // NOUVEAU : Enregistrement du noeud Timer dans la catégorie "Actions"
        REGISTRE.add(new InfoNoeud("Timer", "Actions", "NoeudActionTimer"));
        REGISTRE.add(new InfoNoeud("Fin de Clic", "Événements", "NoeudEventFinClic"));
        REGISTRE.add(new InfoNoeud("Toast", "Actions", "NoeudActionToast"));
        REGISTRE.add(new InfoNoeud("Ajouter à Variable", "Actions", "NoeudActionAjouterVariable"));
        REGISTRE.add(new InfoNoeud("Visibilité", "Actions", "NoeudActionVisibilite"));
        REGISTRE.add(new InfoNoeud("Nombre Aléatoire", "Actions", "NoeudActionNombreAleatoire"));
        
    }
    }

    // Décommenter et déplacer la ligne dans le bloc static{} ci-dessus une fois le fichier NoeudXxx.java créé et testé.
    /* 
     * TODO — Catalogue de nœuds à créer
     * 
     * --- Catégorie Texte ---
     * // REGISTRE.add(new InfoNoeud("Random And Set Text", "Texte", "NoeudActionRandomAndSetText"));
     * // REGISTRE.add(new InfoNoeud("Toast", "Texte", "NoeudActionToast"));
     * // REGISTRE.add(new InfoNoeud("Get Text", "Texte", "NoeudActionGetText"));
     * // REGISTRE.add(new InfoNoeud("Set Font", "Texte", "NoeudActionSetFont"));
     * // REGISTRE.add(new InfoNoeud("Set Text Color", "Texte", "NoeudActionSetTextColor"));
     * // REGISTRE.add(new InfoNoeud("Set Text Size", "Texte", "NoeudActionSetTextSize"));
     * // REGISTRE.add(new InfoNoeud("Clear Text", "Texte", "NoeudActionClearText"));
     * // REGISTRE.add(new InfoNoeud("Append String", "Texte", "NoeudActionAppendString"));
     * 
     * --- Catégorie UI ---
     * // REGISTRE.add(new InfoNoeud("Set Visibility", "UI", "NoeudActionSetVisibility"));
     * // REGISTRE.add(new InfoNoeud("Set Alpha", "UI", "NoeudActionSetAlpha"));
     * // REGISTRE.add(new InfoNoeud("Set Progress", "UI", "NoeudActionSetProgress"));
     * // REGISTRE.add(new InfoNoeud("Set Max", "UI", "NoeudActionSetMax"));
     * // REGISTRE.add(new InfoNoeud("HUD Scene", "UI", "NoeudActionHUDScene"));
     * 
     * --- Catégorie Scène ---
     * // REGISTRE.add(new InfoNoeud("Open Scene", "Scène", "NoeudActionOpenScene"));
     * // REGISTRE.add(new InfoNoeud("Open URL", "Scène", "NoeudActionOpenUrl"));
     * 
     * --- Catégorie Fichiers ---
     * // REGISTRE.add(new InfoNoeud("Save Value", "Fichiers", "NoeudActionSaveValue"));
     * // REGISTRE.add(new InfoNoeud("Write To File", "Fichiers", "NoeudActionWriteToFile"));
     * // REGISTRE.add(new InfoNoeud("Write Internal", "Fichiers", "NoeudActionWriteInternal"));
     * // REGISTRE.add(new InfoNoeud("Read Integer From File", "Fichiers", "NoeudActionReadIntegerFromFile"));
     * // REGISTRE.add(new InfoNoeud("Write Integer To File", "Fichiers", "NoeudActionWriteIntegerToFile"));
     * 
     * --- Catégorie Variables/Math ---
     * // REGISTRE.add(new InfoNoeud("Add To Variable", "Variables/Math", "NoeudActionAddToVariable"));
     * // REGISTRE.add(new InfoNoeud("Subtract From Variable", "Variables/Math", "NoeudActionSubtractFromVariable"));
     * // REGISTRE.add(new InfoNoeud("Random", "Variables/Math", "NoeudActionRandom"));
     * 
     * --- Catégorie Objet ---
     * // REGISTRE.add(new InfoNoeud("Set X", "Objet", "NoeudActionSetX"));
     * // REGISTRE.add(new InfoNoeud("Set Y", "Objet", "NoeudActionSetY"));
     * // REGISTRE.add(new InfoNoeud("Set Scale XY", "Objet", "NoeudActionSetScaleXY"));
     * // REGISTRE.add(new InfoNoeud("Set Scale X", "Objet", "NoeudActionSetScaleX"));
     * // REGISTRE.add(new InfoNoeud("Set Scale Y", "Objet", "NoeudActionSetScaleY"));
     * // REGISTRE.add(new InfoNoeud("Set Angle", "Objet", "NoeudActionSetAngle"));
     * // REGISTRE.add(new InfoNoeud("Set Transform", "Objet", "NoeudActionSetTransform"));
     * // REGISTRE.add(new InfoNoeud("Set Image", "Objet", "NoeudActionSetImage"));
     * // REGISTRE.add(new InfoNoeud("Set Animation", "Objet", "NoeudActionSetAnimation"));
     * // REGISTRE.add(new InfoNoeud("Remove Animation", "Objet", "NoeudActionRemoveAnimation"));
     * // REGISTRE.add(new InfoNoeud("Copy Body", "Objet", "NoeudActionCopyBody"));
     * // REGISTRE.add(new InfoNoeud("Destroy", "Objet", "NoeudActionDestroy"));
     * // REGISTRE.add(new InfoNoeud("Destroy If Group", "Objet", "NoeudActionDestroyIfGroup"));
     * 
     * --- Catégorie Caméra ---
     * // REGISTRE.add(new InfoNoeud("Set Zoom", "Caméra", "NoeudActionSetZoom"));
     * // REGISTRE.add(new InfoNoeud("Camera Follow", "Caméra", "NoeudActionCameraFollow"));
     * // REGISTRE.add(new InfoNoeud("Set Camera XY", "Caméra", "NoeudActionSetCameraXY"));
     * // REGISTRE.add(new InfoNoeud("Set Camera Center", "Caméra", "NoeudActionSetCameraCenter"));
     * // REGISTRE.add(new InfoNoeud("Set Camera X", "Caméra", "NoeudActionSetCameraX"));
     * // REGISTRE.add(new InfoNoeud("Set Camera Y", "Caméra", "NoeudActionSetCameraY"));
     * // REGISTRE.add(new InfoNoeud("Set Camera Offset", "Caméra", "NoeudActionSetCameraOffset"));
     * 
     * --- Catégorie Physique ---
     * // REGISTRE.add(new InfoNoeud("Apply Force", "Physique", "NoeudActionApplyForce"));
     * // REGISTRE.add(new InfoNoeud("Apply Force To Center", "Physique", "NoeudActionApplyForceToCenter"));
     * // REGISTRE.add(new InfoNoeud("Apply Linear Impulse", "Physique", "NoeudActionApplyLinearImpulse"));
     * // REGISTRE.add(new InfoNoeud("Set Linear Velocity", "Physique", "NoeudActionSetLinearVelocity"));
     * // REGISTRE.add(new InfoNoeud("Set Linear Damping", "Physique", "NoeudActionSetLinearDamping"));
     * // REGISTRE.add(new InfoNoeud("Apply Torque", "Physique", "NoeudActionApplyTorque"));
     * // REGISTRE.add(new InfoNoeud("Apply Angular Impulse", "Physique", "NoeudActionApplyAngularImpulse"));
     * // REGISTRE.add(new InfoNoeud("Set Angular Velocity", "Physique", "NoeudActionSetAngularVelocity"));
     * // REGISTRE.add(new InfoNoeud("Set Gravity", "Physique", "NoeudActionSetGravity"));
     * // REGISTRE.add(new InfoNoeud("Set Gravity Scale", "Physique", "NoeudActionSetGravityScale"));
     * // REGISTRE.add(new InfoNoeud("Set Awake", "Physique", "NoeudActionSetAwake"));
     * // REGISTRE.add(new InfoNoeud("Set Sleeping Allowed", "Physique", "NoeudActionSetSleepingAllowed"));
     * // REGISTRE.add(new InfoNoeud("Set Active", "Physique", "NoeudActionSetActive"));
     * // REGISTRE.add(new InfoNoeud("Set Bullet", "Physique", "NoeudActionSetBullet"));
     * // REGISTRE.add(new InfoNoeud("Set Fixed Rotation", "Physique", "NoeudActionSetFixedRotation"));
     * 
     * --- Catégorie Effets ---
     * // REGISTRE.add(new InfoNoeud("Sepia", "Effets", "NoeudActionSepia"));
     * // REGISTRE.add(new InfoNoeud("Black White", "Effets", "NoeudActionBlackWhite"));
     * // REGISTRE.add(new InfoNoeud("Red Tint", "Effets", "NoeudActionRedTint"));
     * // REGISTRE.add(new InfoNoeud("Blue Tint", "Effets", "NoeudActionBlueTint"));
     * // REGISTRE.add(new InfoNoeud("Random Image For Body", "Effets", "NoeudActionRandomImageForBody"));
     * 
     * --- Catégorie Sons ---
     * // REGISTRE.add(new InfoNoeud("Play Sound", "Sons", "NoeudActionPlaySound"));
     * // REGISTRE.add(new InfoNoeud("Loop Sound", "Sons", "NoeudActionLoopSound"));
     * // REGISTRE.add(new InfoNoeud("Pause Sound", "Sons", "NoeudActionPauseSound"));
     * // REGISTRE.add(new InfoNoeud("Release Sound", "Sons", "NoeudActionReleaseSound"));
     * 
     * --- Catégorie Débogage ---
     * // REGISTRE.add(new InfoNoeud("Debug", "Débogage", "NoeudActionDebug"));
     * // REGISTRE.add(new InfoNoeud("Comment", "Débogage", "NoeudActionComment"));
     * // REGISTRE.add(new InfoNoeud("Pause", "Débogage", "NoeudActionPause"));
     * // REGISTRE.add(new InfoNoeud("Resume", "Débogage", "NoeudActionResume"));
     * 
     * --- Catégorie Logique/Contrôle ---
     * // REGISTRE.add(new InfoNoeud("While", "Logique/Contrôle", "NoeudConditionWhile"));
     * // REGISTRE.add(new InfoNoeud("Try", "Logique/Contrôle", "NoeudConditionTry"));
     * // REGISTRE.add(new InfoNoeud("Do After", "Logique/Contrôle", "NoeudActionDoAfter"));
     * // REGISTRE.add(new InfoNoeud("Repeat Every", "Logique/Contrôle", "NoeudActionRepeatEvery"));
     * // REGISTRE.add(new InfoNoeud("Cancel Timer", "Logique/Contrôle", "NoeudActionCancelTimer"));
     * // REGISTRE.add(new InfoNoeud("Call Function", "Logique/Contrôle", "NoeudActionCallFunction"));
     * // REGISTRE.add(new InfoNoeud("Void", "Logique/Contrôle", "NoeudActionVoid"));
     * // REGISTRE.add(new InfoNoeud("Finish", "Logique/Contrôle", "NoeudActionFinish"));
     * 
     * --- Catégorie Événements ---
     * // REGISTRE.add(new InfoNoeud("On Create", "Événements", "NoeudEventOnCreate"));
     * // REGISTRE.add(new InfoNoeud("On Pause", "Événements", "NoeudEventOnPause"));
     * // REGISTRE.add(new InfoNoeud("On Resume", "Événements", "NoeudEventOnResume"));
     * // REGISTRE.add(new InfoNoeud("On Destroy", "Événements", "NoeudEventOnDestroy"));
     * // REGISTRE.add(new InfoNoeud("On Step", "Événements", "NoeudEventOnStep"));
     * // REGISTRE.add(new InfoNoeud("On Body Update", "Événements", "NoeudEventOnBodyUpdate"));
     * // REGISTRE.add(new InfoNoeud("On Body Created", "Événements", "NoeudEventOnBodyCreated"));
     * // REGISTRE.add(new InfoNoeud("On Body Destroyed", "Événements", "NoeudEventOnBodyDestroyed"));
     * // REGISTRE.add(new InfoNoeud("On Collision Enter", "Événements", "NoeudEventOnCollisionEnter"));
     * // REGISTRE.add(new InfoNoeud("On Collision Stay", "Événements", "NoeudEventOnCollisionStay"));
     * // REGISTRE.add(new InfoNoeud("On Collision Exit", "Événements", "NoeudEventOnCollisionExit"));
     * // REGISTRE.add(new InfoNoeud("On Click", "Événements", "NoeudEventOnClick"));
     * // REGISTRE.add(new InfoNoeud("On Touch Start", "Événements", "NoeudEventOnTouchStart"));
     * // REGISTRE.add(new InfoNoeud("On Touch Move", "Événements", "NoeudEventOnTouchMove"));
     * // REGISTRE.add(new InfoNoeud("On Key Pressed", "Événements", "NoeudEventOnKeyPressed"));
     * // REGISTRE.add(new InfoNoeud("On Key Released", "Événements", "NoeudEventOnKeyReleased"));
     * // REGISTRE.add(new InfoNoeud("On Became Visible", "Événements", "NoeudEventOnBecameVisible"));
     * // REGISTRE.add(new InfoNoeud("On Became Invisible", "Événements", "NoeudEventOnBecameInvisible"));
     * // REGISTRE.add(new InfoNoeud("On Timer Complete", "Événements", "NoeudEventOnTimerComplete"));
     */

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
