// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionSautiller extends NoeudBase {
    private String paramIntensite = "10.0";
    private String paramDuree = "500";
    private String paramInfini = "false";
    private ObjetBase cibleObj;

    public NoeudActionSautiller() {
        super(genererId(), Traducteur.get("noeud_sautiller"), Traducteur.get("cat_animations"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            try {
                float intensite = Float.parseFloat(paramIntensite);
                long duree = Long.parseLong(paramDuree);
                boolean estInfini = Boolean.parseBoolean(paramInfini);
                
                if (intensite <= 0f) {
                    cible.sautillementActif = false; // Permet de l'arrêter manuellement
                } else {
                    cible.sautillementActif = true;
                    cible.sautillementIntensite = intensite;
                    cible.sautillementDureeMs = duree; 
                    
                    // LA LIGNE MANQUANTE EST ICI :
                    cible.sautillementInfiniMouvement = estInfini;
                    
                    cible.tempsDebutSautillement = System.currentTimeMillis();
                }
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public List<String> getNomsParametres() {
        List<String> p = new ArrayList<>();
        p.add("Intensite");
        p.add("Duree");
        p.add("Infini");
        return p;
    }

    @Override
    public String getValeurParametre(String nom) {
        if (nom.equals("Intensite")) return paramIntensite;
        if (nom.equals("Duree")) return paramDuree;
        if (nom.equals("Infini")) return paramInfini;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if (nom.equals("Intensite")) paramIntensite = valeur;
        if (nom.equals("Duree")) paramDuree = valeur;
        if (nom.equals("Infini")) paramInfini = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { cibleObj = objet; }

    @Override
    public ObjetBase getCibleObjet() { return cibleObj; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; }

    @Override
    public String getTypeEditeurParametre(String nomParametre) { 
        if (nomParametre.equals("Infini")) return "TYPE_BOOLEEN";
        return TYPE_NOMBRE; 
    }
}
// bas 1
