// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionSiJoystick extends NoeudBase {

    private String directionRequise = "🕹️"; 

    public NoeudConditionSiJoystick() {
        super(genererId(), Traducteur.get("noeud_si_joystick_actif"), "Condition");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        boolean joystickActif = (GestionnaireControles.joyDirX != 0 || GestionnaireControles.joyDirY != 0);
        boolean conditionRemplie = false;

        if ("⏸️".equals(directionRequise)) {
            conditionRemplie = !joystickActif;
        } else if (joystickActif) {
            if ("🕹️".equals(directionRequise)) {
                conditionRemplie = true;
            } else {
                if (Math.abs(GestionnaireControles.joyDirX) > Math.abs(GestionnaireControles.joyDirY)) {
                    if ("➡️".equals(directionRequise) && GestionnaireControles.joyDirX > 0) conditionRemplie = true;
                    if ("⬅️".equals(directionRequise) && GestionnaireControles.joyDirX < 0) conditionRemplie = true;
                } else {
                    if ("⬇️".equals(directionRequise) && GestionnaireControles.joyDirY > 0) conditionRemplie = true;
                    if ("⬆️".equals(directionRequise) && GestionnaireControles.joyDirY < 0) conditionRemplie = true;
                }
            }
        }

        if (conditionRemplie) {
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Direction");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Direction".equals(nom)) return directionRequise;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Direction".equals(nom)) directionRequise = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Direction".equals(nom)) return NoeudBase.TYPE_CHOIX_LISTE;
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public List<String> getOptionsChoixListe(String nom) {
        if ("Direction".equals(nom)) {
            return Arrays.asList("🕹️", "⬆️", "⬇️", "⬅️", "➡️", "⏸️");
        }
        return super.getOptionsChoixListe(nom);
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; }
}
// bas 1
