// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionChangerZOrder extends NoeudBase {

    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;[span_12](start_span)[span_12](end_span)
    private String nouvelleValeurZ = "1";

    public NoeudActionChangerZOrder() {
        super(genererId(), "Changer Z-Order", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null && nouvelleValeurZ != null) {
            try {
                // On convertit le texte saisi en nombre entier
                obj.zOrder = Integer.parseInt(nouvelleValeurZ.trim());
            } catch (NumberFormatException e) {
                // Si l'utilisateur a tapé du texte par erreur, on ignore pour ne pas crasher
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Z-Order"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Z-Order".equals(nom)) return nouvelleValeurZ;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Z-Order".equals(nom)) nouvelleValeurZ = valeur;
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
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        // Reconnexion dynamique après chargement de la sauvegarde
        if (cible == null && nomCibleObjet != null && contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                for (ObjetBase o : editeur.sceneActive.objets) {
                    if (nomCibleObjet.equals(o.nom)) {
                        cible = o;
                        break;
                    }
                }
            }
        }
        return cible;
    }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
