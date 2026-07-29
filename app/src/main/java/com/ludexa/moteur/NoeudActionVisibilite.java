package com.ludexa.moteur;

import java.util.Arrays;
import java.util.List;

public class NoeudActionVisibilite extends NoeudBase {

    private transient ObjetBase cible;
    private String nomCibleObjet;
    private String valeurVisible = "true"; // Valeur par défaut

    public NoeudActionVisibilite() {
        super(genererId(), "Modifier Visibilité", "Action");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
    }

    @Override
    public void executer() {
        ObjetBase cibleActuelle = getCibleObjet();
        if (cibleActuelle != null && valeurVisible != null) {
            String val = valeurVisible.trim().toLowerCase();
            // Accepte true, vrai ou oui comme valeurs positives
            boolean estVisible = val.equals("true") || val.equals("vrai") || val.equals("oui");
            cibleActuelle.visible = estVisible;
        }
        propagerExecution("Suivant");
    }

    @Override
    public boolean utiliseClavierTexte() { return false; } 

    @Override
    public String getTypeEditeurParametre(String nom) {
        if ("Visible".equals(nom)) {
            // Utilisation d'un type booléen si géré par EditeurNoeudDialog
            // Remplacez TYPE_BOOLEEN par la constante exacte si elle diffère (ex: NoeudBase.TYPE_BOOLEEN)
            return "TYPE_BOOLEEN"; 
        }
        return super.getTypeEditeurParametre(nom);
    }

    @Override
    public List<String> getNomsParametres() { 
        return Arrays.asList("Visible"); 
    }

    @Override
    public String getValeurParametre(String nom) { 
        return valeurVisible; 
    }

    @Override
    public void setValeurParametre(String nom, String valeur) { 
        valeurVisible = valeur; 
    }

    @Override
    public boolean requiertCibleObjet() { return true; }[span_1](start_span)[span_1](end_span)
    
    @Override
    public void setCibleObjet(ObjetBase objet) { 
        this.cible = objet;[span_2](start_span)[span_2](end_span)
        this.nomCibleObjet = (objet != null) ? objet.nom : null;[span_3](start_span)[span_3](end_span)
    }
    
    @Override
    public ObjetBase getCibleObjet() {
        if (cible == null && nomCibleObjet != null && contexteApplication != null) {[span_4](start_span)[span_4](end_span)
            // Reconnexion dynamique[span_5](start_span)[span_5](end_span)
            try {[span_6](start_span)[span_6](end_span)
                if (contexteApplication instanceof InterfaceEditeur) {[span_7](start_span)[span_7](end_span)
                    Scene s = ((InterfaceEditeur) contexteApplication).sceneActive;[span_8](start_span)[span_8](end_span)
                    if (s != null && s.objets != null) {[span_9](start_span)[span_9](end_span)
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cible = o;[span_10](start_span)[span_10](end_span)
                    }[span_11](start_span)[span_11](end_span)
                } else {[span_12](start_span)[span_12](end_span)
                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");[span_13](start_span)[span_13](end_span)
                    Scene s = (Scene) sceneField.get(contexteApplication);[span_14](start_span)[span_14](end_span)
                    if (s != null && s.objets != null) {[span_15](start_span)[span_15](end_span)
                        for (ObjetBase o : s.objets) if (o.nom.equals(nomCibleObjet)) cible = o;[span_16](start_span)[span_16](end_span)
                    }[span_17](start_span)[span_17](end_span)
                }[span_18](start_span)[span_18](end_span)
            } catch (Exception e) {}[span_19](start_span)[span_19](end_span)
        }[span_20](start_span)[span_20](end_span)
        return this.cible;[span_21](start_span)[span_21](end_span)
    }
                       }
