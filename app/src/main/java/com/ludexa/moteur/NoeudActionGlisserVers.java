// haut 1
package com.ludexa.moteur;

public class NoeudActionGlisserVers extends NoeudBase {

    public NoeudActionGlisserVers() {
        super(genererId(), "Glisser Vers", "Animations");
        this.ajouterPort(new Port("Entrer", Port.TYPE_EXECUTION_ENTREE));
        this.ajouterPort(new Port("Suivant", Port.TYPE_EXECUTION_SORTIE));
        
        this.ajouterParametre("Cible X", "0", TYPE_NOMBRE);
        this.ajouterParametre("Cible Y", "0", TYPE_NOMBRE);
        this.ajouterParametre("Durée (secondes)", "1.0", TYPE_NOMBRE);
    }

    @Override
    public void executer() {
        // Le ciblage contextuel (tag ou nom) est géré nativement par NoeudBase
        ObjetBase obj = getCibleObjet();
        
        if (obj != null && contexteApplication != null) {
            try {
                float targetX = Float.parseFloat(getValeurParametre("Cible X"));
                float targetY = Float.parseFloat(getValeurParametre("Cible Y"));
                float temps = Float.parseFloat(getValeurParametre("Durée (secondes)"));
                
                android.os.Handler handler = new android.os.Handler(contexteApplication.getMainLooper());
                handler.post(() -> {
                    android.animation.PropertyValuesHolder pvhX = android.animation.PropertyValuesHolder.ofFloat("x", obj.x, targetX);
                    android.animation.PropertyValuesHolder pvhY = android.animation.PropertyValuesHolder.ofFloat("y", obj.y, targetY);
                    
                    android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                    anim.setDuration((long)(temps * 1000));
                    anim.addUpdateListener(a -> {
                        obj.x = (float) a.getAnimatedValue("x");
                        obj.y = (float) a.getAnimatedValue("y");
                    });
                    anim.start();
                });
            } catch (Exception e) {}
        }
        propagerExecution("Suivant"); // Poursuit la logique sans attendre la fin de l'animation
    }

    @Override
    public boolean requiertCibleObjet() { return true; }
    
    @Override
    public boolean utiliseClavierTexte() { return true; }
}
// bas 1
