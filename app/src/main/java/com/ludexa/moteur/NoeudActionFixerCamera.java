// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionFixerCamera extends NoeudBase {

    private ObjetBase cible;
    private boolean suivreX = true;
    private boolean suivreY = true;
    private boolean parallaxeXSeulement = true;

    public NoeudActionFixerCamera() {
        super(genererId(), "Fixer Caméra", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleObj = getCibleObjet();
        if (cibleObj != null) {
            GestionnaireControles.cameraCibleId = cibleObj.id;
            GestionnaireControles.cameraSuitAxeX = suivreX;
            GestionnaireControles.cameraSuitAxeY = suivreY;
            GestionnaireControles.parallaxeUniquementX = parallaxeXSeulement;
        } else {
            GestionnaireControles.cameraCibleId = null; 
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { this.cible = objet; }

    @Override
    public ObjetBase getCibleObjet() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;
        return this.cible; 
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Suivre X", "Suivre Y", "Parallaxe X uniq."); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Suivre X".equals(nom)) return String.valueOf(suivreX);
        if ("Suivre Y".equals(nom)) return String.valueOf(suivreY);
        if ("Parallaxe X uniq.".equals(nom)) return String.valueOf(parallaxeXSeulement);
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Suivre X".equals(nom)) suivreX = Boolean.parseBoolean(valeur);
        if ("Suivre Y".equals(nom)) suivreY = Boolean.parseBoolean(valeur);
        if ("Parallaxe X uniq.".equals(nom)) parallaxeXSeulement = Boolean.parseBoolean(valeur);
    }

    @Override
    public boolean utiliseClavierTexte() { return false; }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return "TYPE_BOOLEEN";
    }
}
// bas 1
