// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoeudActionObjetHasard extends NoeudBase {
    private Variable variableSortie;

    public NoeudActionObjetHasard() {
        super(genererId(), Traducteur.get("noeud_objet_hasard"), Traducteur.get("cat_logique_spatiale"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Tag recherche (optionnel)", "", TYPE_TEXTE_LIBRE);
    }

    @Override
    public void executer() {
        Variable varSortie = getCibleVariable();
        String tagRecherche = getValeurParametre("Tag recherche (optionnel)");

        if (varSortie != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene scene = (Scene) sceneField.get(contexteApplication);
                
                if (scene != null && scene.objets != null) {
                    List<ObjetBase> candidats = new ArrayList<>();
                    
                    for (ObjetBase obj : scene.objets) {
                        if (tagRecherche != null && !tagRecherche.trim().isEmpty()) {
                            if (obj.tag == null || !obj.tag.equals(tagRecherche.trim())) {
                                continue;
                            }
                        }
                        candidats.add(obj);
                    }

                    if (!candidats.isEmpty()) {
                        Random rand = new Random();
                        ObjetBase choisi = candidats.get(rand.nextInt(candidats.size()));
                        varSortie.valeur = choisi.nom;
                    } else {
                        varSortie.valeur = "";
                    }
                }
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleVariable() { return true; }
    
    @Override
    public void setCibleVariable(Variable v) { this.variableSortie = v; }
    
    @Override
    public Variable getCibleVariable() { return this.variableSortie; }
}
// bas 1
