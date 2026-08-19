// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionDetruireObjet extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;

    public NoeudActionDetruireObjet() {
        super(genererId(), "Détruire Objet", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    // Nouvelle fonction récursive pour détruire l'objet ET ses enfants
    private void neutraliserEtRetirer(ObjetBase obj, Scene scene) {
        if (obj == null || scene == null || scene.objets == null) return;
        
        // 1. Trouver et détruire tous les enfants d'abord
        List<ObjetBase> aDetruire = new ArrayList<>();
        for (ObjetBase o : scene.objets) {
            if (obj.id.equals(o.parentId)) {
                aDetruire.add(o);
            }
        }
        for (ObjetBase enfant : aDetruire) {
            neutraliserEtRetirer(enfant, scene);
        }

        // 2. Neutralisation totale pour tuer les collisions et interactions
        obj.visible = false;
        obj.estPhysique = false;
        obj.estZoneDeClic = false;
        obj.estRamassable = false;
        obj.x = -99999;
        obj.y = -99999;
        
        // 3. Retrait sécurisé de la mémoire
        try {
            scene.objets.remove(obj);
        } catch (Exception e) {
            // Ignoré silencieusement : même si la suppression échoue pendant 
            // la boucle de rendu, l'objet est désactivé et inoffensif.
        }
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            
            // On vérifie si l'objet est dans la scène principale
            if (editeur.sceneActive != null && editeur.sceneActive.objets != null && editeur.sceneActive.objets.contains(cibleActuelle)) {
                neutraliserEtRetirer(cibleActuelle, editeur.sceneActive);
            } 
            // Ou s'il appartient au HUD
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
    
    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }
    
    @Override
    public ObjetBase getCibleObjet() {
        if (cible == null && nomCibleObjet != null && contexteApplication instanceof InterfaceEditeur) {
            InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
            
            // Recherche dans la scène active
            if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                for (ObjetBase o : editeur.sceneActive.objets) {
                    if (nomCibleObjet.equals(o.nom)) {
                        cible = o;
                        return cible;
                    }
                }
            }
            // Recherche dans la scène HUD
            if (editeur.sceneHudActive != null && editeur.sceneHudActive.objets != null) {
                for (ObjetBase o : editeur.sceneHudActive.objets) {
                    if (nomCibleObjet.equals(o.nom)) {
                        cible = o;
                        return cible;
                    }
                }
            }
        }
        return this.cible;
    }
}
// bas 1
