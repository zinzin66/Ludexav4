// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudAppelFonction extends NoeudBase {

    private String nomFonction = "";

    public NoeudAppelFonction() {
        super(genererId(), "Appeler Fonction", "Logique & Conditions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public boolean aDesParametresEditables() {
        return true; 
    }
    
    @Override
    public List<String> getNomsParametres() {
        return Arrays.asList("Fonction à appeler");
    }
    
    @Override
    public String getValeurParametre(String nom) {
        if ("Fonction à appeler".equals(nom)) return nomFonction;
        return "";
    }
    
    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Fonction à appeler".equals(nom)) nomFonction = valeur;
    }
    
    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Fonction à appeler".equals(nom)) return NoeudBase.TYPE_CHOIX_FONCTION;
        return NoeudBase.TYPE_TEXTE_LIBRE;
    }

    @Override
    public void executer() {
        if (nomFonction != null && !nomFonction.isEmpty() && contexteApplication != null) {
            try {
                // Récupération dynamique du chemin et de la scène active pour être compatible avec l'Éditeur et le Jeu
                String cheminProj = null;
                Scene sceneActuelle = null;
                
                try {
                    java.lang.reflect.Field fChemin = contexteApplication.getClass().getField("cheminProjet");
                    cheminProj = (String) fChemin.get(contexteApplication);
                    
                    java.lang.reflect.Field fScene = contexteApplication.getClass().getField("sceneActive");
                    sceneActuelle = (Scene) fScene.get(contexteApplication);
                } catch (Exception e) {}

                if (cheminProj != null && sceneActuelle != null) {
                    java.io.File fichierFonction = new java.io.File(cheminProj + "/fonctions/" + nomFonction + ".json");
                    
                    if (fichierFonction.exists()) {
                        // Lecture du fichier JSON de la fonction
                        java.io.FileInputStream fis = new java.io.FileInputStream(fichierFonction);
                        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
                        StringBuilder sb = new StringBuilder();
                        String ligne;
                        while ((ligne = br.readLine()) != null) {
                            sb.append(ligne);
                        }
                        br.close();

                        // Désérialisation dans le contexte de la scène actuelle
                        Blueprint sousBlueprint = Blueprint.fromJson(sb.toString(), sceneActuelle);
                        
                        // Recherche du nœud déclencheur de la fonction (nœud "Début" ou événement de démarrage)
                        NoeudBase noeudDepart = null;
                        if (sousBlueprint != null && sousBlueprint.noeuds != null) {
                            for (NoeudBase n : sousBlueprint.noeuds) {
                                if (n.nom.equals("Début") || n.getClass().getSimpleName().equals("NoeudEventStart")) {
                                    noeudDepart = n;
                                    break;
                                }
                            }
                        }
                        
                        // Exécution de la fonction
                        if (noeudDepart != null) {
                            noeudDepart.executer();
                        } else {
                            android.util.Log.e("Yop2D", "Aucun nœud de départ trouvé dans la fonction : " + nomFonction);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // On continue le script de la scène principale sans bloquer
        propagerExecution("Suivant");
    }

    // --- Méthodes abstraites obligatoires ---
    @Override
    public boolean requiertCibleObjet() { return false; }

    @Override
    public void setCibleObjet(ObjetBase objet) {}

    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
