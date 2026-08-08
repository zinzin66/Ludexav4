// haut 1
package com.ludexa.moteur;

import android.graphics.Color;
import android.widget.ImageView;

public class Palette {
    // Vos couleurs d'origine restaurées
    public static int fondPanneaux = Color.parseColor("#021726");
    public static int enTeteDialogues = Color.parseColor("#034574");
    public static int bordure = Color.parseColor("#10598A");
    public static int texteNormal = Color.parseColor("#FFFFFF");
    public static int texteSelectionne = Color.parseColor("#95DFF5");
    public static int boutonNormal = Color.parseColor("#045A90");
    public static int boutonSurvol = Color.parseColor("#0975B5");
    public static int canvasFond = Color.parseColor("#022E4D");
    public static int canvasGrille = Color.parseColor("#152B43");

    // La nouvelle couleur ajoutée sans rien casser
    public static int fondListe = Color.parseColor("#1E1E1E");
    public static int fondNormal = Color.parseColor("#121212");

    // Couleur dédiée aux icônes monochromes
    public static int iconeNormal = Color.parseColor("#FFFFFF");
    public static int iconeSurvol = Color.parseColor("#95DFF5");

    public static void appliquerCouleurIcone(ImageView icone, int couleur) {
        icone.setColorFilter(couleur);
    }
}
// bas 1
