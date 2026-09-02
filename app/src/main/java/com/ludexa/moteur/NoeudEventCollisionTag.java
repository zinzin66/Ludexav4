// haut 1
package com.ludexa.moteur;

import java.util.HashSet;
import java.util.Set;

public class NoeudEventCollisionTag extends NoeudBase {
    
    private transient ObjetBase cible;
    private transient Set<String> objetsEnCollision = new HashSet<>();

    public NoeudEventCollisionTag() {
        super(genererId(), "Si objet touche Tag", "Événements");
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
        
        // CORRECTION : Utilisation du système natif de paramètres pour garantir la sauvegarde JSON
        this.ajouterParametre("Tag", "", "TYPE_CHOIX_TAG");
    }

    @Override
    public void executer() {
        if (!objetsEnCollision.isEmpty()) {
            String idTouche = objetsEnCollision.iterator().next(); 
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (idTouche.equals(o.id)) {
                        MoteurLogique.dernierObjetImplique = o;
                        break;
                    }
                }
            }
        }

        DiagLogger.log(NoeudBase.cheminProjetCourant, "COLLISION_TAG executer() appele, propagation Sortie...");
        propagerExecution("Sortie");
    }

    @Override
    public boolean utiliseClavierTexte() { 
        return false; 
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        if (objet != null) {
            this.nomCibleObjet = objet.nom;
        }
    }

    @Override
    public ObjetBase getCibleObjet() {
        if (cibleObjetResolue != null) return cibleObjetResolue;
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (nomCibleObjet != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
        }
        return this.cible;
    }

    public boolean isEnCollisionAvec(String idObjet) {
        return idObjet != null && objetsEnCollision.contains(idObjet);
    }

    public void marquerEnCollision(String idObjet) {
        if (idObjet != null) objetsEnCollision.add(idObjet);
    }

    public void marquerHorsCollision(String idObjet) {
        if (idObjet != null) objetsEnCollision.remove(idObjet);
    }

    public Set<String> getObjetsEnCollisionActuels() {
        return objetsEnCollision;
    }

    public String getTagCible() { 
        return getValeurParametre("Tag"); 
    }
}
// bas 1
