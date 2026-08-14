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

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Nom de l'événement".equals(nom)) return NoeudBase.TYPE_CHOIX_LISTE;
        return NoeudBase.TYPE_TEXTE_LIBRE;
    }

    @Override
    public List<String> getOptionsChoixListe(String nomParametre) {
        List<String> options = new java.util.ArrayList<>();
        if ("Nom de l'événement".equals(nomParametre) && contexteApplication != null) {
            try {
                if (contexteApplication.getClass().getSimpleName().equals("InterfaceBlueprint")) {
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
                List<NoeudBase> listeCible = null;

                // Recherche ultra-robuste des nœuds en mémoire
                if (contexteApplication.getClass().getSimpleName().equals("InterfaceBlueprint")) {
                    java.lang.reflect.Field fBp = contexteApplication.getClass().getDeclaredField("blueprintActif");
                    fBp.setAccessible(true);
                    Object bp = fBp.get(contexteApplication);
                    if (bp != null) {
                        java.lang.reflect.Field fNoeuds = bp.getClass().getDeclaredField("noeuds");
                        fNoeuds.setAccessible(true);
                        listeCible = (List<NoeudBase>) fNoeuds.get(bp);
                    }
                } else {
                    // Mode Play : On cherche VueJeu -> MoteurLogique -> List<NoeudBase>
                    Object vueJeu = null;
                    for (java.lang.reflect.Field f : contexteApplication.getClass().getDeclaredFields()) {
                        if (f.getType().getSimpleName().equals("VueJeu")) {
                            f.setAccessible(true);
                            vueJeu = f.get(contexteApplication);
                            if (vueJeu != null) break;
                        }
                    }

                    if (vueJeu != null) {
                        for (java.lang.reflect.Field fMoteur : vueJeu.getClass().getDeclaredFields()) {
                            if (fMoteur.getType().getSimpleName().equals("MoteurLogique")) {
                                fMoteur.setAccessible(true);
                                Object moteur = fMoteur.get(vueJeu);
                                if (moteur != null) {
                                    for (java.lang.reflect.Field fListe : moteur.getClass().getDeclaredFields()) {
                                        if (java.util.List.class.isAssignableFrom(fListe.getType())) {
                                            fListe.setAccessible(true);
                                            List<?> liste = (List<?>) fListe.get(moteur);
                                            if (liste != null && !liste.isEmpty()) {
                                                boolean contientNoeuds = false;
                                                for(Object obj : liste) {
                                                    if(obj != null) {
                                                        contientNoeuds = obj instanceof NoeudBase;
                                                        break;
                                                    }
                                                }
                                                if (contientNoeuds) {
                                                    @SuppressWarnings("unchecked")
                                                    List<NoeudBase> noeuds = (List<NoeudBase>) liste;
                                                    for (NoeudBase n : noeuds) {
                                                        if (n instanceof NoeudEventPersonnalise && nomEvenement.equals(((NoeudEventPersonnalise) n).getNomEvenement())) {
                                                            listeCible = noeuds;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Déclenchement de l'événement trouvé
                if (listeCible != null) {
                    for (NoeudBase noeud : listeCible) {
                        if (noeud instanceof NoeudEventPersonnalise) {
                            if (nomEvenement.equals(((NoeudEventPersonnalise) noeud).getNomEvenement())) {
                                noeud.executer();
                                break;
                            }
                        }
                    }
                } else {
                    android.util.Log.e("Yop2D", "Impossible de trouver la liste des noeuds pour l'événement " + nomEvenement);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // L'exécution du nœud "Appel" continue vers le nœud suivant
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
