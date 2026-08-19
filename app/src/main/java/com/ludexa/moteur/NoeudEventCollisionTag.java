// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventCollisionTag extends NoeudBase {
    
    private String tagCible = "";
    private transient boolean etaitEnCollision = false;

    public NoeudEventCollisionTag() {
        super(genererId(), "Si objet touche Tag", "Événements");
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Sortie");
    }

    @Override
    public List<String> getNomsParametres() {
        List<String> params = new ArrayList<>();
        params.add("Tag");
        return params;
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Tag".equals(nom)) return tagCible;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Tag".equals(nom)) this.tagCible = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Tag".equals(nom)) return "TYPE_CHOIX_TAG";
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    public boolean isEtaitEnCollision() { return etaitEnCollision; }
    public void setEtaitEnCollision(boolean etat) { this.etaitEnCollision = etat; }
    public String getTagCible() { return tagCible; }
}
// bas 1
