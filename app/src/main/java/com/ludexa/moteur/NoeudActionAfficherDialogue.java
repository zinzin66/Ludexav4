// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionAfficherDialogue extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String cleMessageDialogue = "";

    public NoeudActionAfficherDialogue() {
        super(genererId(), "Afficher Dialogue", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase obj = getCibleObjet();
        
        // Si on a bien ciblé un objet (ex: txt 1)
        if (obj != null && contexteApplication != null) {
            
            // 1. On cherche le chemin du projet pour lire le fichier texte
            String projPath = null;
            try {
                java.lang.reflect.Field field = contexteApplication.getClass().getField("cheminProjet");
                projPath = (String) field.get(contexteApplication);
            } catch(Exception e) {}

            // 2. On traduit la clé en vrai texte (par défaut, on affiche la clé si on ne trouve pas le fichier)
            String vraiMessage = cleMessageDialogue;
            
            if (projPath != null && cleMessageDialogue != null && !cleMessageDialogue.isEmpty()) {
                java.io.File fichierDialogues = new java.io.File(projPath, "assets_ludexa/Textes/dialogues.txt");
                if (fichierDialogues.exists()) {
                    try {
                        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fichierDialogues));
                        String ligne;
                        while ((ligne = br.readLine()) != null) {
                            ligne = ligne.trim();
                            // On ignore les commentaires et les lignes vides
                            if (ligne.isEmpty() || ligne.startsWith("//")) continue;
                            
                            int idxEgal = ligne.indexOf('=');
                            if (idxEgal > 0) {
                                String cle = ligne.substring(0, idxEgal).trim();
                                if (cle.equals(cleMessageDialogue)) {
                                    // On a trouvé la bonne phrase !
                                    vraiMessage = ligne.substring(idxEgal + 1).trim();
                                    break;
                                }
                            }
                        }
                        br.close();
                    } catch (Exception e) {}
                }
            }
            
            // 3. NOUVEAU COMPORTEMENT : On injecte la phrase directement dans l'objet cible !
            obj.contenuTexte = vraiMessage;
        }
        
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        // Le titre a disparu, on ne garde que la clé
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
    
    // Ouvre toujours le sélecteur avec la liste déroulante
    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        if ("Clé du Dialogue".equals(nomParametre)) return TYPE_CHOIX_DIALOGUE;
        return super.getTypeEditeurParametre(nomParametre);
    }

    // NOUVEAU : Exige qu'on sélectionne un objet
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
