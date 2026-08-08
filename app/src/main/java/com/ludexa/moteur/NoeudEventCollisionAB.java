// NoeudEventCollisionAB.java
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudEventCollisionAB extends NoeudBase {
    private ObjetBase objetCible;
    private ObjetBase objetCibleB;
    
    // Mémorisation d'état pour one-shot
    private boolean etaitEnCollision = false;

    public NoeudEventCollisionAB() {
        super(genererId(), "Collision A/B", "Événements");
        this.ajouterPort(new Port("Collision", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        propagerExecution("Collision");
    }

    @Override
    public List<String> getNomsParametres() { return new ArrayList<>(); }
    @Override
    public String getValeurParametre(String nom) { return ""; }
    @Override
    public void setValeurParametre(String nom, String valeur) {}

    @Override
    public boolean requiertCibleObjet() { return true; }
    @Override
    public void setCibleObjet(ObjetBase objet) { this.objetCible = objet; }
    @Override
    public ObjetBase getCibleObjet() { return this.objetCible; }

    // -- Mécanisme DÉDIÉ Cible Objet B --
    @Override
    public boolean requiertCibleObjetB() { return true; }
    @Override
    public void setCibleObjetB(ObjetBase objet) { this.objetCibleB = objet; }
    @Override
    public ObjetBase getCibleObjetB() { return this.objetCibleB; }

    public boolean isEtaitEnCollision() { return etaitEnCollision; }
    public void setEtaitEnCollision(boolean etaitEnCollision) { this.etaitEnCollision = etaitEnCollision; }
}

