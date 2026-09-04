// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionSpawner extends NoeudBase {
    private transient ObjetBase cible; // L'objet d'où sortira le clone (ex: Le Canon)
    private String nomCibleObjet;
    private String nomModele = ""; // Le nom de l'objet à cloner (ex: La Balle)

    public NoeudActionSpawner() {
        super(genererId(), "Générer un clone (Spawner)", "Apparence & Objets");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase spawnPoint = getCibleObjet();
        if (spawnPoint != null && contexteApplication != null) {
            try {
                java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                Scene s = (Scene) sceneField.get(contexteApplication);
                if (s != null && s.objets != null) {
                    
                    ObjetBase modele = null;
                    for (ObjetBase o : s.objets) {
                        if (nomModele.equals(o.nom)) {
                            modele = o;
                            break;
                        }
                    }
                    
                    if (modele != null) {
                        ObjetBase clone = modele.clonerProfond();
                        // Centre le clone exactement sur l'objet qui sert de Spawner
                        clone.x = spawnPoint.x + (spawnPoint.largeur / 2f) - (clone.largeur / 2f);
                        clone.y = spawnPoint.y + (spawnPoint.hauteur / 2f) - (clone.hauteur / 2f);
                        
                        // Le clone hérite de l'angle du Spawner (utile pour tirer dans la bonne direction)
                        clone.rotation = spawnPoint.rotation;
                        clone.visible = true;

                        try {
                            java.lang.reflect.Method methodZ = s.getClass().getMethod("prochainZOrder");
                            clone.zOrder = (int) methodZ.invoke(s);
                        } catch (Exception e) {
                            clone.zOrder = 999;
                        }

                        s.objets.add(clone);
                    }
                }
            } catch (Exception e) {}
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Nom Objet à cloner"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Nom Objet à cloner".equals(nom)) return nomModele;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Nom Objet à cloner".equals(nom)) nomModele = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) { 
        // CORRECTIF 2 : Forcer le clavier alphabétique pour le nom du modèle
        return TYPE_TEXTE_ALPHABETIQUE; 
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
