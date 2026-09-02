// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoeudEventCollisionTag extends NoeudBase {
    
    private transient ObjetBase cible;
    private String tagCible = "";
    private transient Set<String> objetsEnCollision = new HashSet<>();

    public NoeudEventCollisionTag() {
        super(genererId(), "Si objet touche Tag", "Événements");
        this.ajouterPort(new Port("Sortie", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        // On définit officiellement l'objet impliqué pour les nœuds suivants
        // (ex: pour que __OBJET_IMPLIQUE__ fonctionne dans un nœud en aval)
        if (!objetsEnCollision.isEmpty()) {
            String idTouche = objetsEnCollision.iterator().next(); // On prend le premier objet touché
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

    public String getTagCible() { return tagCible; }
}
// bas 1
