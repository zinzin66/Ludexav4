package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudConditionSiJoystick extends NoeudBase {

    private ObjetBase cible;
    private float vitesse = 5f;
    
    // Le symbole 🕹️ devient la valeur par défaut
    private String directionRequise = "🕹️"; 

    public NoeudConditionSiJoystick() {
        super(genererId(), "Si Joystick", "Condition");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Vrai", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Faux", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        boolean joystickActif = (GestionnaireControles.joyDirX != 0 || GestionnaireControles.joyDirY != 0);
        boolean conditionRemplie = false;

        // Évaluation basée sur les symboles
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
            if (cible != null && joystickActif) {
                cible.x += GestionnaireControles.joyDirX * vitesse;
                cible.y += GestionnaireControles.joyDirY * vitesse;
            }
            propagerExecution("Vrai");
        } else {
            propagerExecution("Faux");
        }
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Vitesse", "Direction");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Vitesse".equals(nom)) return String.valueOf(vitesse);
        if ("Direction".equals(nom)) return directionRequise;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        try {
            if ("Vitesse".equals(nom)) vitesse = Float.parseFloat(valeur);
            if ("Direction".equals(nom)) directionRequise = valeur;
        } catch (NumberFormatException e) {}
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Direction".equals(nom)) return NoeudBase.TYPE_CHOIX_LISTE;
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public List<String> getOptionsChoixListe(String nom) {
        if ("Direction".equals(nom)) {
            // Liste déroulante visuelle sans besoin de traduction
            return Arrays.asList("🕹️", "⬆️", "⬇️", "⬅️", "➡️", "⏸️");
        }
        return super.getOptionsChoixListe(nom);
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { this.cible = objet; }

    @Override
    public ObjetBase getCibleObjet() { return this.cible; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
