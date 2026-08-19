// haut 1
package com.ludexa.moteur;

public class NoeudActionObjetLePlusProche extends NoeudBase {
    private ObjetBase cibleObj; // L'objet qui sert de point de départ (ex: le Joueur)
    private Variable variableSortie; // La variable où on va stocker le nom de la cible trouvée

    public NoeudActionObjetLePlusProche() {
        super(genererId(), Traducteur.get("noeud_objet_proche"), Traducteur.get("cat_logique_spatiale"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        // Permet de ne cibler que les objets ayant un tag précis (laisser vide pour tout cibler)
        ajouterParametre("Tag recherche (optionnel)", "", TYPE_TEXTE_LIBRE);
    }

    @Override
    public void executer() {
        ObjetBase refObj = getCibleObjet();
        Variable varSortie = getCibleVariable();
        String tagRecherche = getValeurParametre("Tag recherche (optionnel)");

        if (refObj != null && varSortie != null && contexteApplication != null) {
            try {
                // Récupération de la scène active pour scanner tous les objets
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene scene = (Scene) sceneField.get(contexteApplication);
                
                if (scene != null && scene.objets != null) {
                    ObjetBase plusProche = null;
                    float distMin = Float.MAX_VALUE;

                    for (ObjetBase obj : scene.objets) {
                        if (obj == refObj) continue; // On s'ignore soi-même
                        
                        // Si un tag est renseigné, on filtre
                        if (tagRecherche != null && !tagRecherche.trim().isEmpty()) {
                            if (obj.tag == null || !obj.tag.equals(tagRecherche.trim())) {
                                continue;
                            }
                        }

                        // Calcul de la distance géométrique
                        float dist = (float) Math.hypot(obj.x - refObj.x, obj.y - refObj.y);
                        if (dist < distMin) {
                            distMin = dist;
                            plusProche = obj;
                        }
                    }

                    // Enregistrement du résultat
                    if (plusProche != null) {
                        varSortie.valeur = plusProche.nom;
                    } else {
                        varSortie.valeur = ""; // Rien trouvé
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
    public ObjetBase getCibleObjet() { return this.cibleObj; }

    @Override
    public boolean requiertCibleVariable() { return true; }
    
    @Override
    public void setCibleVariable(Variable v) { this.variableSortie = v; }
    
    @Override
    public Variable getCibleVariable() { return this.variableSortie; }
}
// bas 1
