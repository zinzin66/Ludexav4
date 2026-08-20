// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionFiltre extends NoeudBase {

    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String typeFiltre = "Noir et Blanc";

    public NoeudActionFiltre() {
        super(genererId(), "Filtre Couleur", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        if (obj != null) {
            obj.filtreCouleur = typeFiltre;
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Filtre");
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Filtre".equals(nom)) return typeFiltre;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Filtre".equals(nom)) typeFiltre = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        return TYPE_CHOIX_LISTE;
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        return Arrays.asList("Aucun", "Noir et Blanc", "Sepia", "Inversion");
    }

    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.cible = objet;
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (cible == null && nomCibleObjet != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    for (ObjetBase o : s.objets) {
                        if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                    }
                }
            } catch (Exception e) {}
        }
        return cible;
    }
}
// bas 1
