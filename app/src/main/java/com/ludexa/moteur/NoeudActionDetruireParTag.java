// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class NoeudActionDetruireParTag extends NoeudBase {
    private String tagCible = "Ennemi";

    public NoeudActionDetruireParTag() {
        super(genererId(), "Détruire par Tag", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
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
        if (contexteApplication != null && tagCible != null && !tagCible.isEmpty()) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    List<ObjetBase> objetsASupprimer = new ArrayList<>();
                    
                    for (ObjetBase o : s.objets) {
                        // Tolérance sur les majuscules et les espaces
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
    public List<String> getNomsParametres() { return Arrays.asList("Tag à détruire"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Tag à détruire".equals(nom)) return tagCible;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Tag à détruire".equals(nom)) tagCible = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) { 
        if ("Tag à détruire".equals(nomParametre)) return "TYPE_CHOIX_TAG";
        return TYPE_TEXTE_LIBRE; 
    }

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
    
    @Override
    public boolean utiliseClavierTexte() { return false; } 
}
// bas 1
