// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionDetruireParTag extends NoeudBase {

    public NoeudActionDetruireParTag() {
        super(genererId(), "Détruire par Tag", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        // CORRECTION : On utilise le paramètre dynamique pour garantir la sauvegarde !
        this.ajouterParametre("Tag à détruire", "Ennemi", "TYPE_CHOIX_TAG");
    }

    private void neutraliserEtRetirer(ObjetBase obj, Scene sceneParent) {
        if (obj == null || sceneParent == null) return;
        
        List<ObjetBase> aDetruire = new ArrayList<>();
        for (ObjetBase o : sceneParent.objets) {
            if (obj.id.equals(o.parentId)) aDetruire.add(o);
        }
        for (ObjetBase enfant : aDetruire) {
            neutraliserEtRetirer(enfant, sceneParent);
        }

        obj.visible = false;
        obj.estPhysique = false;
        obj.estZoneDeClic = false;
        obj.estRamassable = false;
        obj.x = -99999;
        obj.y = -99999;
    }

    @Override
    public void executer() {
        String tagCible = getValeurParametre("Tag à détruire");
        
        if (contexteApplication != null && tagCible != null && !tagCible.isEmpty()) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    List<ObjetBase> objetsASupprimer = new ArrayList<>();
                    
                    for (ObjetBase o : s.objets) {
                        if (o.tag != null && tagCible.trim().equalsIgnoreCase(o.tag.trim())) {
                            objetsASupprimer.add(o);
                        }
                    }
                    
                    for (ObjetBase o : objetsASupprimer) {
                        neutraliserEtRetirer(o, s);
                        try {
                            s.objets.remove(o);
                        } catch (Exception ex) {}
                    }
                }
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
}
// bas 1
