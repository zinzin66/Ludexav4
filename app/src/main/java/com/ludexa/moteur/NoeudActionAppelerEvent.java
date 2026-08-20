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
        boolean trouve = false;
        
        if (nomEvenement != null && !nomEvenement.isEmpty() && contexteApplication != null) {
            trouve = chercherEtExecuterEvent();
        }
        
        // Sécurité visuelle : si l'événement n'a pas été trouvé en mémoire, on te prévient
        if (!trouve && contexteApplication != null) {
            try {
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                handler.post(() -> android.widget.Toast.makeText(contexteApplication, "Échec : Événement '" + nomEvenement + "' introuvable", android.widget.Toast.LENGTH_LONG).show());
            } catch(Exception e) {}
        }

        // On continue la chaîne principale quoiqu'il arrive
        propagerExecution("Suivant");
    }

    private boolean chercherEtExecuterEvent() {
        try {
            if (contexteApplication.getClass().getSimpleName().equals("InterfaceBlueprint")) {
                java.lang.reflect.Field fBp = contexteApplication.getClass().getDeclaredField("blueprintActif");
                fBp.setAccessible(true);
                Object bp = fBp.get(contexteApplication);
                if (executerDans(bp)) return true;
            } else {
                // On cherche VueJeu (Mode Play)
                Object vueJeu = null;
                for (java.lang.reflect.Field f : contexteApplication.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    Object val = f.get(contexteApplication);
                    if (val != null && val.getClass().getSimpleName().equals("VueJeu")) {
                        vueJeu = val;
                        break;
                    }
                }

                if (vueJeu != null) {
                    for (java.lang.reflect.Field fMoteur : vueJeu.getClass().getDeclaredFields()) {
                        fMoteur.setAccessible(true);
                        Object moteurObj = fMoteur.get(vueJeu);
                        if (moteurObj != null) {
                            String nomClasse = moteurObj.getClass().getSimpleName();
                            if (nomClasse.equals("MoteurLogique") || nomClasse.equals("Blueprint")) {
                                if (executerDans(moteurObj)) return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return false;
    }

    private boolean executerDans(Object conteneur) {
        if (conteneur == null) return false;
        try {
            for (java.lang.reflect.Field f : conteneur.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object obj = f.get(conteneur);
                
                // Si le conteneur a directement une liste
                if (obj instanceof java.util.List) {
                    if (chercherDansListe((java.util.List<?>) obj)) return true;
                } 
                // S'il a un Blueprint qui cache une liste
                else if (obj != null && obj.getClass().getSimpleName().equals("Blueprint")) {
                    for (java.lang.reflect.Field fBp : obj.getClass().getDeclaredFields()) {
                        fBp.setAccessible(true);
                        Object objListe = fBp.get(obj);
                        if (objListe instanceof java.util.List) {
                            if (chercherDansListe((java.util.List<?>) objListe)) return true;
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return false;
    }

    private boolean chercherDansListe(java.util.List<?> liste) {
        if (liste == null || liste.isEmpty()) return false;
        for (Object obj : liste) {
            if (obj instanceof NoeudEventPersonnalise) {
                NoeudEventPersonnalise nep = (NoeudEventPersonnalise) obj;
                if (nomEvenement.equals(nep.getNomEvenement())) {
                    nep.executer();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    @Override
    public void setCibleObjet(ObjetBase objet) {}
    @Override
    public ObjetBase getCibleObjet() { return null; }
}
// bas 1
