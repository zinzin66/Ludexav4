// haut 1
package com.ludexa.moteur;

public class NoeudActionSautiller extends NoeudBase {

    public NoeudActionSautiller() {
        super(genererId(), Traducteur.get("noeud_sautiller"), Traducteur.get("cat_animations"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));
        
        ajouterParametre("Intensite", "10.0", TYPE_NOMBRE);
        ajouterParametre("Duree", "500", TYPE_NOMBRE);
        ajouterParametre("Infini", "false", "TYPE_BOOLEEN");
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            try {
                float intensite = Float.parseFloat(getValeurParametre("Intensite"));
                long duree = Long.parseLong(getValeurParametre("Duree"));
                boolean estInfini = Boolean.parseBoolean(getValeurParametre("Infini"));
                
                if (intensite <= 0f) {
                    cible.sautillementActif = false; 
                } else {
                    cible.sautillementActif = true;
                    cible.sautillementIntensite = intensite;
                    cible.sautillementDureeMs = duree; 
                    cible.sautillementInfiniMouvement = estInfini;
                    cible.tempsDebutSautillement = System.currentTimeMillis();
                }
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
