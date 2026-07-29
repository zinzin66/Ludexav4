package com.ludexa.moteur;

import android.widget.Toast;
import java.util.Arrays;
import java.util.List;

public class NoeudActionToast extends NoeudBase {

    private String message = "";

    public NoeudActionToast() {
        super(genererId(), "Toast", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        if (NoeudBase.contexteApplication != null) {
            Toast.makeText(NoeudBase.contexteApplication, message, Toast.LENGTH_SHORT).show();
        }
        propagerExecution("Suivant");
    }

    @Override
    public List<String> getNomsParametres() { return Arrays.asList("Message"); }

    @Override
    public String getValeurParametre(String nom) {
        if ("Message".equals(nom)) return message;
        return "";
    }

    @Override
    public void setValeurParametre(String nom, String valeur) {
        if ("Message".equals(nom)) message = valeur;
    }

    @Override
    public boolean requiertCibleObjet() { return false; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { }
    
    @Override
    public ObjetBase getCibleObjet() { return null; }

    @Override
    public boolean requiertCibleVariable() { return false; }
    
    @Override
    public void setCibleVariable(Variable v) { }
    
    @Override
    public Variable getCibleVariable() { return null; }

    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}
