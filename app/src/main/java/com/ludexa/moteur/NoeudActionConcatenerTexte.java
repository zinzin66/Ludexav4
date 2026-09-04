// haut 1
package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionConcatenerTexte extends NoeudBase {

    private transient Variable cible;
    private String nomCibleVariable;
    private String texteAAjouter = "";

    public NoeudActionConcatenerTexte() {
        super(genererId(), "Ajouter au Texte", "Actions");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        Variable cibleActuelle = getCibleVariable();
        if (cibleActuelle != null) {
            // Récupère la valeur actuelle sous forme de chaîne (vide si null)
            String valeurActuelle = (cibleActuelle.valeur != null) ? cibleActuelle.valeur.toString() : "";
            String ajout = (texteAAjouter != null) ? texteAAjouter : "";
            
            // Concaténation simple
            cibleActuelle.valeur = valeurActuelle + ajout;
            
            // On force le type en TEXTE par sécurité pour la cohérence du moteur
            if (!"TEXTE".equals(cibleActuelle.type)) {
                cibleActuelle.type = "TEXTE";
            }
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Texte à ajouter"); 
    }

    @Override
    public String getValeurParametre(String nom) {
        if ("Texte à ajouter".equals(nom)) return texteAAjouter;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Texte à ajouter".equals(nom)) texteAAjouter = valeur;
    }

    @Override
    public String getTypeEditeurParametre(String nomParametre) {
        // CORRECTIF 2 : Forcer le clavier alphabétique pour la saisie de texte
        return TYPE_TEXTE_ALPHABETIQUE;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }
// bas 1
