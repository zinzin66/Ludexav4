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

    public static void initialiser() {
        REGISTRE.clear();
        
        // ÉVÉNEMENTS
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_au_demarrage"), Traducteur.get("cat_evenements"), "NoeudEventStart"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fin_de_clic"), Traducteur.get("cat_evenements"), "NoeudEventFinClic"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_au_clic_sur_objet"), Traducteur.get("cat_evenements"), "NoeudEventClicObjet"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_debut_de_glisser"), Traducteur.get("cat_evenements"), "NoeudEventDebutGlisser"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fin_de_glisser"), Traducteur.get("cat_evenements"), "NoeudEventFinGlisser"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_collision_ab"), Traducteur.get("cat_evenements"), "NoeudEventCollisionAB"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_entree_de_zone"), Traducteur.get("cat_evenements"), "NoeudEventEntreeZone")); 
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_sortie_de_zone"), Traducteur.get("cat_evenements"), "NoeudEventSortieZone"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_au_survol"), Traducteur.get("cat_evenements"), "NoeudEventSurvolObjet"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fin_de_survol"), Traducteur.get("cat_evenements"), "NoeudEventFinSurvol"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_au_choc_physique"), Traducteur.get("cat_evenements"), "NoeudEventChoc"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_quand_variable_change"), Traducteur.get("cat_evenements"), "NoeudEventVariableChange"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_evenement_local"), Traducteur.get("cat_evenements"), "NoeudEventPersonnalise"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_au_clic_action_aventure"), Traducteur.get("cat_evenements"), "NoeudEventBoutonAction"));

        // MOUVEMENTS & IA
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_avancer_en_continu"), Traducteur.get("cat_mouvements_ia"), "NoeudActionAvancerContinu"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_poursuivre_un_objet"), Traducteur.get("cat_mouvements_ia"), "NoeudActionPoursuivre"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fuir_un_objet"), Traducteur.get("cat_mouvements_ia"), "NoeudActionFuir"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_stopper_mouvements"), Traducteur.get("cat_mouvements_ia"), "NoeudActionStopperMouvements"));

        // LOGIQUE SPATIALE
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_distance_entre_a_et_b"), Traducteur.get("cat_logique_spatiale"), "NoeudConditionDistance"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_orienter_vers"), Traducteur.get("cat_logique_spatiale"), "NoeudActionOrienterVers"));

        // LOGIQUE & CONDITIONS
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_condition"), Traducteur.get("cat_logique_conditions"), "NoeudConditionComparaison"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_si_objet_a_touche_zone_b"), Traducteur.get("cat_logique_conditions"), "NoeudConditionSiObjetToucheZone"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_si_objet_visible"), Traducteur.get("cat_logique_conditions"), "NoeudConditionSiObjetVisible"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_si_objet_a_le_tag"), Traducteur.get("cat_logique_conditions"), "NoeudConditionTag"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_condition_double"), Traducteur.get("cat_logique_conditions"), "NoeudConditionDouble"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_appeler_fonction"), Traducteur.get("cat_logique_conditions"), "NoeudAppelFonction"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_appeler_evenement_local"), Traducteur.get("cat_logique_conditions"), "NoeudActionAppelerEvent"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_si_joystick_actif"), Traducteur.get("cat_logique_conditions"), "NoeudConditionSiJoystick"));
// bas 1
// haut 2
        // SCÈNE & HUD
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_changer_de_scene"), Traducteur.get("cat_scene_hud"), "NoeudActionChangerScene"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_recharger_scene"), Traducteur.get("cat_scene_hud"), "NoeudActionRechargerScene"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_ouvrir_hud"), Traducteur.get("cat_scene_hud"), "NoeudActionOuvrirHUD"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fermer_hud"), Traducteur.get("cat_scene_hud"), "NoeudActionFermerHUD"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_toast"), Traducteur.get("cat_scene_hud"), "NoeudActionToast"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fixer_camera"), Traducteur.get("cat_scene_hud"), "NoeudActionFixerCamera"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_tremblement_camera"), Traducteur.get("cat_scene_hud"), "NoeudActionTremblement"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_elasticite_camera"), Traducteur.get("cat_scene_hud"), "NoeudActionParametresCamera"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_afficher_masquer_joystick"), Traducteur.get("cat_scene_hud"), "NoeudActionJoystick"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_afficher_masquer_action"), Traducteur.get("cat_scene_hud"), "NoeudActionBoutonAction"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_vibration"), Traducteur.get("cat_scene_hud"), "NoeudActionVibration"));

        // APPARENCE & OBJETS
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_deplacer_objet"), Traducteur.get("cat_apparence_objets"), "NoeudActionDeplacer"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_pousser_objet"), Traducteur.get("cat_apparence_objets"), "NoeudActionPousser"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_modifier_couleur"), Traducteur.get("cat_apparence_objets"), "NoeudActionModifierCouleur"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_visibilite"), Traducteur.get("cat_apparence_objets"), "NoeudActionVisibilite"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_modifier_deplacable"), Traducteur.get("cat_apparence_objets"), "NoeudActionModifierDeplacable"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_modifier_verrouillage"), Traducteur.get("cat_apparence_objets"), "NoeudActionModifierVerrouillage"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_changer_zorder"), Traducteur.get("cat_apparence_objets"), "NoeudActionChangerZOrder"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_changer_image"), Traducteur.get("cat_apparence_objets"), "NoeudActionChangerImage"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_rotation"), Traducteur.get("cat_apparence_objets"), "NoeudActionRotation"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_definir_taille"), Traducteur.get("cat_apparence_objets"), "NoeudActionModifierTaille"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_detruire_objet"), Traducteur.get("cat_apparence_objets"), "NoeudActionDetruireObjet"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_generer_clone"), Traducteur.get("cat_apparence_objets"), "NoeudActionSpawner"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_definir_tag"), Traducteur.get("cat_apparence_objets"), "NoeudActionDefinirTag"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_detruire_par_tag"), Traducteur.get("cat_apparence_objets"), "NoeudActionDetruireParTag"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_filtre_couleur"), Traducteur.get("cat_apparence_objets"), "NoeudActionFiltre"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_clignotement"), Traducteur.get("cat_apparence_objets"), "NoeudActionClignotement"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_surbrillance"), Traducteur.get("cat_apparence_objets"), "NoeudActionSurbrillance"));
        
        // TEXTES & DIALOGUES
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_modifier_texte"), Traducteur.get("cat_textes_dialogues"), "NoeudActionModifierTexte"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_ajouter_au_texte"), Traducteur.get("cat_textes_dialogues"), "NoeudActionConcatenerTexte"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_afficher_dialogue"), Traducteur.get("cat_textes_dialogues"), "NoeudActionAfficherDialogue"));

        // VARIABLES & INVENTAIRE
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_operation_mathematique"), Traducteur.get("cat_variables_inventaire"), "NoeudActionOperationMath"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_limiter_valeur"), Traducteur.get("cat_variables_inventaire"), "NoeudActionClampVariable"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_modifier_variable"), Traducteur.get("cat_variables_inventaire"), "NoeudActionModifierVariable"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_ajouter_a_variable"), Traducteur.get("cat_variables_inventaire"), "NoeudActionAjouterVariable"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_nombre_aleatoire"), Traducteur.get("cat_variables_inventaire"), "NoeudActionNombreAleatoire"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_ajouter_inventaire"), Traducteur.get("cat_variables_inventaire"), "NoeudActionAjouterInventaire"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_retirer_inventaire"), Traducteur.get("cat_variables_inventaire"), "NoeudActionRetirerInventaire"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_si_dans_inventaire"), Traducteur.get("cat_variables_inventaire"), "NoeudConditionSiDansInventaire"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_combinaison_objets"), Traducteur.get("cat_variables_inventaire"), "NoeudActionCombinaison"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_point_de_sauvegarde"), Traducteur.get("cat_variables_inventaire"), "NoeudActionCheckpoint"));

        // TEMPS
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_timer"), Traducteur.get("cat_temps"), "NoeudActionTimer"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_chrono"), Traducteur.get("cat_temps"), "NoeudActionChrono"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_attendre"), Traducteur.get("cat_temps"), "NoeudActionAttendre"));

        // AUDIO
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_jouer_son"), Traducteur.get("cat_audio"), "NoeudActionJouerSon"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_gerer_musique"), Traducteur.get("cat_audio"), "NoeudActionMusique"));

        // ANIMATIONS
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_fondu_alpha"), Traducteur.get("cat_animations"), "NoeudActionFondu"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_glisser_vers"), Traducteur.get("cat_animations"), "NoeudActionGlisserVers"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_jouer_animation"), Traducteur.get("cat_animations"), "NoeudActionJouerAnimation"));
        
        // PHYSIQUE
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_activer_physique"), Traducteur.get("cat_physique"), "NoeudActionModifierPhysique"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_appliquer_impulsion"), Traducteur.get("cat_physique"), "NoeudActionImpulsion"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_changer_rebond"), Traducteur.get("cat_physique"), "NoeudActionChangerRebond"));
        REGISTRE.add(new InfoNoeud(Traducteur.get("noeud_si_objet_en_chute"), Traducteur.get("cat_physique"), "NoeudConditionEnMouvement"));
    }

    public static Map<String, List<InfoNoeud>> getNoeudsParCategorie() {
        if (REGISTRE.isEmpty()) {
            initialiser(); 
        }
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
// bas 2
