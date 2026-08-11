// haut 1
package com.ludexa.moteur;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;

public class NoeudActionChrono extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;

    public NoeudActionChrono() {
        super(genererId(), "Chrono", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Chaque Seconde", Port.TYPE_EXECUTION_SORTIE));
        this.ajouterPort(new Port("Terminé", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        final Variable cibleActuelle = getCibleVariable();
        
        // 1. Vérification sécurisée du type (ENTIER uniquement)
        if (cibleActuelle == null || !"ENTIER".equals(cibleActuelle.type)) {
            return; // Si la variable n'est pas un ENTIER ou est nulle, on ne fait rien
        }

        // 2. Lecture sécurisée de la valeur
        int valeurCourante = 0;
        if (cibleActuelle.valeur instanceof Integer) {
            valeurCourante = (Integer) cibleActuelle.valeur;
        } else if (cibleActuelle.valeur != null) {
            try {
                valeurCourante = Integer.parseInt(cibleActuelle.valeur.toString().trim());
            } catch (NumberFormatException e) {
                valeurCourante = 0;
            }
        }

        // 3. Condition initiale : si déjà <= 0, on termine tout de suite
        if (valeurCourante <= 0) {
            propagerExecution("Terminé");
            return;
        }

        // 4. Lancement du compte à rebours
        final Handler handler = new Handler(Looper.getMainLooper());
        InterfaceEditeur.handlersActifs.add(handler);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Lecture de la valeur à chaque tick pour éviter les désynchronisations
                int valActuelle = 0;
                if (cibleActuelle.valeur instanceof Integer) {
                    valActuelle = (Integer) cibleActuelle.valeur;
                } else if (cibleActuelle.valeur != null) {
                    try {
                        valActuelle = Integer.parseInt(cibleActuelle.valeur.toString().trim());
                    } catch (NumberFormatException e) {
                        valActuelle = 0;
                    }
                }

                // Décrémentation
                valActuelle--;
                cibleActuelle.valeur = valActuelle;

                // Vérification de l'état du chronomètre
                if (valActuelle > 0) {
                    propagerExecution("Chaque Seconde");
                    handler.postDelayed(this, 1000); // Reprogrammation dans 1 seconde
                } else {
                    propagerExecution("Chaque Seconde"); // On propage une dernière fois pour afficher le "0"
                    propagerExecution("Terminé"); // Puis on termine sans reprogrammer
                }
            }
        }, 1000);
    }

    // Gestion des paramètres (Aucun paramètre texte requis)
    @Override
    public List<String> getNomsParametres() { return new ArrayList<String>(); }

    @Override
    public String getValeurParametre(String nom) { return ""; }

    @Override
    public void setValeurParametre(String nom, String valeur) { }

    @Override
    public boolean utiliseClavierTexte() { return false; }

    // Gestion de la cible Objet (Non requise)
    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }

    // Gestion de la cible Variable (Requise)
    @Override
    public boolean requiertCibleVariable() { return true; }
    
    @Override
    public void setCibleVariable(Variable v) { 
        this.cible = v; 
        this.nomCibleVariable = (v != null) ? v.nom : null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Variable getCibleVariable() { 
        if (cible == null && nomCibleVariable != null && contexteApplication != null) {
            // Reconnexion dynamique (copiée du standard robuste du moteur)
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                        for (Variable v : editeur.sceneActive.variablesLocales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                    if (cible == null && editeur.variablesGlobales != null) {
                        for (Variable v : editeur.variablesGlobales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                } else {
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Scene s = (Scene) sceneField.get(contexteApplication);
                    if (s != null && s.variablesLocales != null) {
                        for (Variable v : s.variablesLocales) if (v.nom.equals(nomCibleVariable)) cible = v;
                    }
                    if (cible == null) {
                        java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                        List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                        if (globales != null) {
                            for (Variable v : globales) if (v.nom.equals(nomCibleVariable)) cible = v;
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible; 
    }
}
// bas 1
