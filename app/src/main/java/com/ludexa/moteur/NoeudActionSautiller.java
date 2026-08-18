// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionSautiller extends NoeudBase {
    private String paramIntensite = "10.0";
    private String paramDuree = "500";
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
                cible.sautillementActif = true;
                cible.sautillementIntensite = intensite;
                cible.sautillementDureeMs = duree;
                cible.tempsDebutSautillement = System.currentTimeMillis();
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public List<String> getNomsParametres() {
        List<String> p = new ArrayList<>();
        p.add("Intensité");
        p.add("Durée (ms)");
        return p;
    }

    @Override
    public String getValeurParametre(String nom) {
        if (nom.equals("Intensité")) return paramIntensite;
        if (nom.equals("Durée (ms)")) return paramDuree;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if (nom.equals("Intensité")) paramIntensite = valeur;
        if (nom.equals("Durée (ms)")) paramDuree = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { cibleObj = objet; }

    @Override
    public ObjetBase getCibleObjet() { return cibleObj; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; }
}
// bas 1

