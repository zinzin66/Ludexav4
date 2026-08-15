// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionJoystick extends NoeudBase {

    private String etat = "Masquer"; // "Afficher" ou "Masquer"

    public NoeudActionJoystick() {
        super(genererId(), "Afficher/Masquer Joystick", "Scène & HUD");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null) {
            boolean doitAfficher = "Afficher".equals(etat);
            try {
                // Recherche dans la scène principale
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    for (ObjetBase o : s.objets) {
                        if ("joystick".equals(o.type)) {
                            o.visible = doitAfficher;
                        }
                    }
                }
                
                // Recherche dans le HUD
                java.lang.reflect.Field sceneHudField = contexteApplication.getClass().getField("sceneHudActive");
                Scene sHud = (Scene) sceneHudField.get(contexteApplication);
                if (sHud != null && sHud.objets != null) {
                    for (ObjetBase o : sHud.objets) {
                        if ("joystick".equals(o.type)) {
                            o.visible = doitAfficher;
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore silencieusement si les champs ne sont pas trouvés
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("État");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("État".equals(nom)) return etat;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("État".equals(nom)) etat = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_CHOIX_LISTE;
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        return Arrays.asList("Afficher", "Masquer");
    }

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1

