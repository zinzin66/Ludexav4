// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionAfficherDialogue extends NoeudBase {

    private transient ObjetBase cible;
    // SUPPRIMÉ : private String nomCibleObjet;
    private String cleMessageDialogue = "";

    public NoeudActionAfficherDialogue() {
        super(genererId(), "Afficher Dialogue", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        
        if (obj != null && contexteApplication != null) {
            String projPath = null;
            try {
                java.lang.reflect.Field field = contexteApplication.getClass().getField("cheminProjet");
                projPath = (String) field.get(contexteApplication);
            } catch(Exception e) {}

            String vraiMessage = cleMessageDialogue;
            
            if (projPath != null && cleMessageDialogue != null && !cleMessageDialogue.isEmpty()) {
                java.io.File fichierDialogues = new java.io.File(projPath, "assets_ludexa/Textes/dialogues.txt");
                if (fichierDialogues.exists()) {
                    try {
                        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierDialogues));
                        String ligne;
                        while ((ligne = br.readLine()) != null) {
                            ligne = ligne.trim();
                            if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                            
                            int idxEgal = ligne.indexOf('=');
                            if (idxEgal > 0) {
                                String cle = ligne.substring(0, idxEgal).trim();
                                if (cle.equals(cleMessageDialogue)) {
                                    vraiMessage = ligne.substring(idxEgal + 1).trim();
                                    break;
                                }
                            }
                        }
                        br.close();
                    } catch (Exception e) {}
                }
            }
            
            obj.contenuTexte = vraiMessage;
        }
        
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Clé du Dialogue"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Clé du Dialogue".equals(nom)) return cleMessageDialogue;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Clé du Dialogue".equals(nom)) cleMessageDialogue = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Clé du Dialogue".equals(nomParametre)) return TYPE_CHOIX_DIALOGUE;
        return super.getTypeEditeurParametre(nomParametre);
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
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.objets != null) {
                        for (ObjetBase o : editeur.sceneActive.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.objets != null) {
                        for (ObjetBase o : s.objets) {
                            if (nomCibleObjet.equals(o.nom)) { cible = o; break; }
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return cible;
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
