// haut 1
package com.ludexa.moteur;

import java.util.UUID;

public class Variable {
    public String id;
    public String nom;
    public String scope; // "LOCALE" ou "GLOBALE"
    public String type; // "CHIFFRE", "TEXTE", "BOOLEEN", "ENTIER", "LISTE_INVENTAIRE"
    public Object valeur;

    public Variable(String nom, String scope, String type) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.scope = scope;
        this.type = type;
        
        if ("CHIFFRE".equals(type)) {
            this.valeur = 0f;
        } else if ("TEXTE".equals(type)) {
            this.valeur = "";
        } else if ("BOOLEEN".equals(type)) {
            this.valeur = false;
        } else if ("ENTIER".equals(type)) {
            this.valeur = 0;
        } else if ("LISTE_INVENTAIRE".equals(type)) {
            this.valeur = new java.util.ArrayList<String>();
        }
    }

    // NOUVEAU : Méthode de clonage
    public Variable clonerProfond() {
        Variable copie = new Variable(this.nom, this.scope, this.type);
        copie.id = this.id; // IMPORTANT : Garder le même ID
        // Note : En Java, Float, String et Boolean sont immuables. 
        // Une copie de référence avec "=" est donc parfaitement sécurisée pour 'valeur'.
        if ("LISTE_INVENTAIRE".equals(this.type) && this.valeur instanceof java.util.List) {
            copie.valeur = new java.util.ArrayList<String>((java.util.List<String>) this.valeur);
        } else {
            copie.valeur = this.valeur;
        }
        return copie;
    }
}
// bas 1
