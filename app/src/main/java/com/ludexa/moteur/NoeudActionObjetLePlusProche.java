// haut 1
package com.ludexa.moteur;

public class NoeudActionObjetLePlusProche extends NoeudBase {
    private ObjetBase cibleObj; 
    private Variable variableSortie; 

    public NoeudActionObjetLePlusProche() {
        super(genererId(), Traducteur.get("noeud_objet_proche"), Traducteur.get("cat_logique_spatiale"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Tag recherche (optionnel)", "", TYPE_TEXTE_LIBRE);
    }

    @Override
    public void executer() {
        ObjetBase refObj = getCibleObjet();
        Variable varSortie = getCibleVariable();
        String tagRecherche = getValeurParametre("Tag recherche (optionnel)");

        if (refObj != null && varSortie != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene scene = (Scene) sceneField.get(contexteApplication);
                
                if (scene != null && scene.objets != null) {
                    ObjetBase plusProche = null;
                    float distMin = Float.MAX_VALUE;

                    for (ObjetBase obj : scene.objets) {
                        if (obj == refObj) continue; 
                        
                        if (tagRecherche != null && !tagRecherche.trim().isEmpty()) {
                            if (obj.tag == null || !obj.tag.equals(tagRecherche.trim())) {
                                continue;
                            }
                        }

                        float dist = (float) Math.hypot(obj.x - refObj.x, obj.y - refObj.y);
                        if (dist < distMin) {
                            distMin = dist;
                            plusProche = obj;
                        }
                    }

                    if (plusProche != null) {
                        varSortie.valeur = plusProche.nom;
                    } else {
                        varSortie.valeur = ""; 
                    }
                }
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleObj = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;
        return this.cibleObj; 
    }

    @Override
    public boolean requiertCibleVariable() { return true; }
    
    @Override
    public void setCibleVariable(Variable v) { this.variableSortie = v; }
    
    @Override
    public Variable getCibleVariable() { return this.variableSortie; }
}
// bas 1
