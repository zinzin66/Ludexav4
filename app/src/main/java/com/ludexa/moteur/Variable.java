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
}
// bas 1
