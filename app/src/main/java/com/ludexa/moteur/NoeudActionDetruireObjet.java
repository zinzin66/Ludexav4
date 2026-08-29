// haut 1
package com.ludexa.moteur;

import java.util.ArrayList;
import java.util.List;

public class NoeudActionDetruireObjet extends NoeudBase {

    private transient ObjetBase cible;

    public NoeudActionDetruireObjet() {
        super(genererId(), "Détruire Objet", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    // SIMPLIFIÉ : utilise NoeudBase.sceneActiveCourante / sceneHudActiveCourante
    // (posées directement par VueJeu) au lieu de fouiller contexteApplication par réflexion.
    // Fiable en APK compilé/obfusqué, contrairement à l'ancienne version.
    private void neutraliserEtRetirer(ObjetBase obj) {
        if (obj == null) return;
        
        obj.visible = false;
        obj.estPhysique = false;
        obj.estZoneDeClic = false;
        obj.estRamassable = false;
        obj.x = -99999;
        obj.y = -99999;
        
        Scene sceneParent = null;
        if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null 
            && NoeudBase.sceneActiveCourante.objets.contains(obj)) {
            sceneParent = NoeudBase.sceneActiveCourante;
        } else if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null 
            && NoeudBase.sceneHudActiveCourante.objets.contains(obj)) {
            sceneParent = NoeudBase.sceneHudActiveCourante;
        }
        
        if (sceneParent == null) return;
        
        List<ObjetBase> aDetruire = new ArrayList<>();
        for (ObjetBase o : sceneParent.objets) {
            if (obj.id.equals(o.parentId)) aDetruire.add(o);
        }
        for (ObjetBase enfant : aDetruire) {
            neutraliserEtRetirer(enfant);
        }

        try {
            sceneParent.objets.remove(obj);
        } catch (Exception e) {}
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null) {
            neutraliserEtRetirer(cibleActuelle);
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet;
        if (objet != null) {
            this.nomCibleObjet = objet.nom;
        } else if (!"__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            this.nomCibleObjet = null;
        }
    }

    // SIMPLIFIÉ : référence directe d'instance en priorité, puis __OBJET_IMPLIQUE__,
    // puis résolution par nom via les scènes courantes, puis fallback sur le champ local.
    // Toute la réflexion agressive sur contexteApplication a été supprimée.
    @Override
    public ObjetBase getCibleObjet() {
        if (cibleObjetResolue != null) return cibleObjetResolue;

        if ("__OBJET_IMPLIQUE__".equals(this.nomCibleObjet)) {
            return MoteurLogique.dernierObjetImplique;
        }

        if (this.nomCibleObjet != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (this.nomCibleObjet.equals(o.nom)) return o;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {
                    if (this.nomCibleObjet.equals(o.nom)) return o;
                }
            }
        }
        return this.cible;
    }
}
// bas 1
