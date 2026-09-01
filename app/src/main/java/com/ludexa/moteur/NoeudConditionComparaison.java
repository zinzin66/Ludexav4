// haut 1
    @Override
    public boolean requiertCibleObjet() { return true; }

    @Override
    public void setCibleObjet(ObjetBase objet) {
        this.nomCibleObjet = (objet != null) ? objet.nom : null;
    }

    @Override
    public ObjetBase getCibleObjet() {
        if (cibleObjetResolue != null) return cibleObjetResolue;
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;

        if (nomCibleObjet != null) {
            if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
            if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.objets != null) {
                for (ObjetBase o : NoeudBase.sceneHudActiveCourante.objets) {
                    if (nomCibleObjet.equals(o.nom)) return o;
                }
            }
        }
        return null;
    }

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
        if (nomCibleVariable == null) return null;

        // 1. Priorité à la variable locale de l'objet ciblé
        ObjetBase objTarget = getCibleObjet();
        if (objTarget != null && objTarget.variablesLocales != null) {
            for (Variable v : objTarget.variablesLocales) {
                if (nomCibleVariable.equals(v.nom)) return v; // Résolution dynamique (pas de cache pour éviter les conflits entre clones)
            }
        }

        // 2. Fallback Scène/Globale
        if (cible != null && nomCibleVariable.equals(cible.nom)) return cible;

        if (contexteApplication != null) {
            try {
                if (contexteApplication instanceof InterfaceEditeur) {
                    InterfaceEditeur editeur = (InterfaceEditeur) contexteApplication;
                    if (editeur.sceneActive != null && editeur.sceneActive.variablesLocales != null) {
                        for (Variable v : editeur.sceneActive.variablesLocales) {
                            if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                        }
                    }
                    if (editeur.variablesGlobales != null) {
                        for (Variable v : editeur.variablesGlobales) {
                            if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                        }
                    }
                } else {
                    if (NoeudBase.sceneActiveCourante != null && NoeudBase.sceneActiveCourante.variablesLocales != null) {
                        for (Variable v : NoeudBase.sceneActiveCourante.variablesLocales) {
                            if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                        }
                    }
                    if (NoeudBase.sceneHudActiveCourante != null && NoeudBase.sceneHudActiveCourante.variablesLocales != null) {
                        for (Variable v : NoeudBase.sceneHudActiveCourante.variablesLocales) {
                            if (nomCibleVariable.equals(v.nom)) { cible = v; return cible; }
                        }
                    }

                    java.lang.reflect.Field sceneField = contexteApplication.getClass().getField("sceneActive");
                    Object sceneObj = sceneField.get(contexteApplication);
                    if (sceneObj != null) {
                        java.lang.reflect.Field varsLocalesField = sceneObj.getClass().getField("variablesLocales");
                        List<Variable> varsLocales = (List<Variable>) varsLocalesField.get(sceneObj);
                        if (varsLocales != null) {
                            for (Variable v : varsLocales) {
                                if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                            }
                        }
                    }
                    java.lang.reflect.Field varsField = contexteApplication.getClass().getField("variablesGlobales");
                    List<Variable> globales = (List<Variable>) varsField.get(contexteApplication);
                    if (globales != null) {
                        for (Variable v : globales) {
                            if (v.nom.equals(nomCibleVariable)) { cible = v; return cible; }
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return this.cible; 
    }

    @Override
    public boolean utiliseClavierTexte() {
        return true;
    }
}
// bas 1
