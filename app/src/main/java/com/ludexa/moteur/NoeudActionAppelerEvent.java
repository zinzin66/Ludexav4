// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionAppelerEvent extends NoeudBase {

    private String nomEvenement = "";

    public NoeudActionAppelerEvent() {
        super(genererId(), "Appeler Événement Local", "Logique & Conditions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public boolean aDesParametresEditables() { return true; }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Nom de l'événement"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Nom de l'événement".equals(nom)) return nomEvenement;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Nom de l'événement".equals(nom)) nomEvenement = valeur;
    }

    // --- On indique à l'éditeur d'utiliser une liste déroulante ---
    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Nom de l'événement".equals(nom)) return NoeudBase.TYPE_CHOIX_LISTE;
        return NoeudBase.TYPE_TEXTE_LIBRE;
    }

    // --- On génère la liste en scannant les nœuds de la scène ---
    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        List<String> options = new java.util.ArrayList<>();
        if ("Nom de l'événement".equals(nomParametre) && contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceBlueprint) {
                    java.lang.reflect.Field fBlueprint = contexteApplication.getClass().getDeclaredField("blueprintActif");
                    fBlueprint.setAccessible(true);
                    Blueprint bp = (Blueprint) fBlueprint.get(contexteApplication);
                    if (bp != null && bp.noeuds != null) {
                        for (NoeudBase n : bp.noeuds) {
                            if (n instanceof NoeudEventPersonnalise) {
                                options.add(((NoeudEventPersonnalise) n).getNomEvenement());
                            }
                        }
                    }
                }
            } catch (Exception e) {}
        }
        if (options.isEmpty()) {
            options.add("Aucun événement local trouvé");
        }
        return options;
    }

    @Override
    public void executer() {
        if (nomEvenement != null && !nomEvenement.isEmpty() && contexteApplication != null) {
            try {
                List<NoeudBase> listeNoeuds = null;
                
                if (contexteApplication instanceof InterfaceBlueprint) {
                    java.lang.reflect.Field fBlueprint = contexteApplication.getClass().getDeclaredField("blueprintActif");
                    fBlueprint.setAccessible(true);
                    Blueprint bp = (Blueprint) fBlueprint.get(contexteApplication);
                    if (bp != null) listeNoeuds = bp.noeuds;
                } else {
                    java.lang.reflect.Field fMoteur = contexteApplication.getClass().getField("moteurLogique");
                    Object moteur = fMoteur.get(contexteApplication);
                    if (moteur != null) {
                        java.lang.reflect.Field fNoeuds = moteur.getClass().getField("noeuds");
                        @SuppressWarnings("unchecked")
                        List<NoeudBase> noeudsRuntime = (List<NoeudBase>) fNoeuds.get(moteur);
                        listeNoeuds = noeudsRuntime;
                    }
                }

                if (listeNoeuds != null) {
                    for (NoeudBase noeud : listeNoeuds) {
                        if (noeud instanceof NoeudEventPersonnalise) {
                            if (nomEvenement.equals(((NoeudEventPersonnalise) noeud).getNomEvenement())) {
                                noeud.executer(); 
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
      
