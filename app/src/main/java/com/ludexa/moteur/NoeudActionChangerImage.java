// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionChangerImage extends NoeudBase {

    private transient ObjetBase cible;
    private String cheminImage = "";

    public NoeudActionChangerImage() {
        super(genererId(), "Changer Image", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            if (cheminImage == null || cheminImage.trim().isEmpty()) {
                obj.cheminImage = null; 
            } else {
                obj.cheminImage = cheminImage.trim();
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Image"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Image".equals(nom)) return cheminImage;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Image".equals(nom)) cheminImage = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Image".equals(nomParametre)) return TYPE_CHOIX_IMAGE;
        return super.getTypeEditeurParametre(nomParametre);
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

        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            Scene active = null;
            if (contexteApplication instanceof InterfaceEditeur) {
                active = ((InterfaceEditeur) contexteApplication).sceneActive;
            } else if (contexteApplication instanceof RunnerActivity) {
                active = ((RunnerActivity) contexteApplication).sceneActive;
            }
            
            if (active != null && active.objets != null) {
                for (ObjetBase o : active.objets) {
                    if (nomCibleObjet.equals(o.nom)) { 
                        cible = o; 
                        break; 
                    }
                }
            }
        }
        return cible;
    }
}
// bas 1
