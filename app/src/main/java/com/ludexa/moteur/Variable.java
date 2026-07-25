// haut 1
package com.ludexa.moteur;

import java.util.UUID;

public class Variable {
    public String id;
    public String nom;
    public String scope; // "LOCALE" ou "GLOBALE"
    public String type; // "CHIFFRE", "TEXTE", "BOOLEEN"
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
        }
    }

    // NOUVEAU : Méthode de clonage
    public Variable clonerProfond() {
        Variable copie = new Variable(this.nom, this.scope, this.type);
        copie.id = this.id; // IMPORTANT : Garder le même ID
        // Note : En Java, Float, String et Boolean sont immuables. 
        // Une copie de référence avec "=" est donc parfaitement sécurisée pour 'valeur'.
        copie.valeur = this.valeur; 
        return copie;
    }
}
// bas 1
