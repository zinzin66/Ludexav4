// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionModifierVerrouillage extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String valeurVerrouillage = "true"; // true par défaut pour verrouiller

    public NoeudActionModifierVerrouillage() {
        super(genererId(), "Modifier Verrouillage", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            String valLower = valeurVerrouillage.toLowerCase().trim();
            // Accepte oui/vrai/true pour verrouiller, sinon déverrouille
            obj.estVerrouille = valLower.equals("oui") || valLower.equals("vrai") || valLower.equals("true");
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Verrouiller (true/false)"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Verrouiller (true/false)".equals(nom)) return valeurVerrouillage;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Verrouiller (true/false)".equals(nom)) valeurVerrouillage = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjet() {
        // Reconnexion dynamique après chargement de la sauvegarde
        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                        for (ObjetBase o : editeur.sceneActive.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return cible;
    }

    @Override
    public boolean utiliseClavierTexte() { 
        // Active l'interface de saisie (avec les boutons Vrai/Faux de votre éditeur)
        return true; 
    }
}
// bas 1
