// haut 1
package com.ludexa.moteur;

import android.widget.Toast;

public class NoeudActionToast extends NoeudBase {

    public NoeudActionToast() {
        super(genererId(), "Toast", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Message", "", TYPE_TEXTE_LIBRE);
    }

    @Override
    public void executer() {
        if (NoeudBase.contexteApplication != null) {
            Toast.makeText(NoeudBase.contexteApplication, getValeurParametre("Message"), Toast.LENGTH_SHORT).show();
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
