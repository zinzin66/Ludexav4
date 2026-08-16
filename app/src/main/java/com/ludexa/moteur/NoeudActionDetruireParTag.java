// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class NoeudActionDetruireParTag extends NoeudBase {
    private String tagCible = "Ennemi";

    public NoeudActionDetruireParTag() {
        super(genererId(), "Détruire par Tag", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (contexteApplication != null && tagCible != null && !tagCible.isEmpty()) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    Iterator<ObjetBase> iterator = s.objets.iterator();
                    while (iterator.hasNext()) {
                        ObjetBase o = iterator.next();
                        if (tagCible.equals(o.tag)) {
                            iterator.remove();
                        }
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
    public String getTypeEditeurParametre(String nomParametre) { return TYPE_TEXTE_LIBRE; }

    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1

