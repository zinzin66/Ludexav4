// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionDetruireObjet extends NoeudBase {

    public NoeudActionDetruireObjet() {
        super(genererId(), "Détruire Objet", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    // Fonction récursive pour détruire l'objet ET ses enfants
    private void neutraliserEtRetirer(ObjetBase obj, Scene scene) {
        if (obj == null || scene == null || scene.objets == null) return;
        
        List<ObjetBase> aDetruire = new ArrayList<>();
        for (ObjetBase o : scene.objets) {
            if (obj.id.equals(o.parentId)) {
                aDetruire.add(o);
            }
        }
        for (ObjetBase enfant : aDetruire) {
            neutraliserEtRetirer(enfant, scene);
        }

        // Neutralisation totale pour tuer les collisions et interactions
        obj.visible = false;
        obj.estPhysique = false;
        obj.estZoneDeClic = false;
        obj.estRamassable = false;
        obj.x = -99999;
        obj.y = -99999;
        
        // Retrait sécurisé de la mémoire
        try {
            scene.objets.remove(obj);
        } catch (Exception e) {}
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet(); // Géré nativement par NoeudBase !
        if (cibleActuelle != null && contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            
            if (editeur.sceneActive != null && editeur.sceneActive.objets != null && editeur.sceneActive.objets.contains(cibleActuelle)) {
                neutraliserEtRetirer(cibleActuelle, editeur.sceneActive);
            } 
            else if (editeur.sceneHudActive != null && editeur.sceneHudActive.objets != null && editeur.sceneHudActive.objets.contains(cibleActuelle)) {
                neutraliserEtRetirer(cibleActuelle, editeur.sceneHudActive);
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) { }

    @Override
    public boolean requiertCibleObjet() { return true; }
}
// bas 1
