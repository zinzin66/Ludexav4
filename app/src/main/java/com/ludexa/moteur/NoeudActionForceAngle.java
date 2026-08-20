// haut 1
package com.ludexa.moteur;

public class NoeudActionForceAngle extends NoeudBase {
    private ObjetBase cibleObj;

    public NoeudActionForceAngle() {
        super(genererId(), Traducteur.get("noeud_force_angle"), Traducteur.get("cat_physique"));
        ajouterPort(new Port(Traducteur.get("port_entree"), Port.TYPE_EXECUTION_ENTREE));
        ajouterPort(new Port(Traducteur.get("port_sortie"), Port.TYPE_EXECUTION_SORTIE));

        ajouterParametre("Angle", "0", TYPE_NOMBRE);
        ajouterParametre("Force", "10", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        ObjetBase cible = getCibleObjet();
        if (cible != null) {
            try {
                float angleDeg = Float.parseFloat(getValeurParametre("Angle"));
                float force = Float.parseFloat(getValeurParametre("Force"));
                
                double angleRad = Math.toRadians(angleDeg);
                
                cible.intentionDeplacementX += (float) (Math.cos(angleRad) * force);
                cible.intentionDeplacementY += (float) (Math.sin(angleRad) * force);
            } catch (Exception e) {}
        }
        propagerExecution(Traducteur.get("port_sortie"));
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public void setCibleObjet(ObjetBase objet) { this.cibleObj = objet; }
    
    @Override
    public ObjetBase getCibleObjet() { 
        if ("__OBJET_IMPLIQUE__".equals(nomCibleObjet)) return MoteurLogique.dernierObjetImplique;
        return this.cibleObj; 
    }
}
// bas 1
