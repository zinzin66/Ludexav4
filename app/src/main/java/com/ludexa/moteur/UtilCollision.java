// UtilCollision.java
package com.ludexa.moteur;

import android.graphics.Matrix;
import java.util.List;

public class UtilCollision {

    /**
     * Teste la collision entre deux objets orientés (OBB) via le théorème des axes séparateurs (SAT).
     */
    public static boolean rectanglesSeChevauchent(ObjetBase a, List<ObjetBase> contexteA, ObjetBase b, List<ObjetBase> contexteB, VueJeu vueJeu) {
        if (a == null || b == null) return false;

        float[] coinsA = obtenirCoinsMonde(a, contexteA, vueJeu);
        float[] coinsB = obtenirCoinsMonde(b, contexteB, vueJeu);

        float[][] axes = new float[4][2];
        calculerAxes(coinsA, axes, 0);
        calculerAxes(coinsB, axes, 2);

        for (int i = 0; i < 4; i++) {
            float axeX = axes[i][0];
            float axeY = axes[i][1];

            float[] projA = projeterPolygone(coinsA, axeX, axeY);
            float[] projB = projeterPolygone(coinsB, axeX, axeY);

            if (projA[1] < projB[0] || projB[1] < projA[0]) {
                return false; // Séparation trouvée : pas de collision
            }
        }
        return true;
    }

    private static float[] obtenirCoinsMonde(ObjetBase obj, List<ObjetBase> contexte, VueJeu vueJeu) {
        Matrix matrice = vueJeu.getAbsoluteMatrix(obj, contexte);
        float[] coinsLocaux = {
            0, 0,
            obj.largeur, 0,
            obj.largeur, obj.hauteur,
            0, obj.hauteur
        };
        matrice.mapPoints(coinsLocaux);
        return coinsLocaux;
    }

    private static void calculerAxes(float[] coins, float[][] axes, int offset) {
        axes[offset][0] = coins[2] - coins[0];
        axes[offset][1] = coins[3] - coins[1];
        axes[offset + 1][0] = coins[4] - coins[2];
        axes[offset + 1][1] = coins[5] - coins[3];

        for (int i = 0; i < 2; i++) {
            float longueur = (float) Math.hypot(axes[offset + i][0], axes[offset + i][1]);
            if (longueur != 0) {
                axes[offset + i][0] /= longueur;
                axes[offset + i][1] /= longueur;
            }
            float temp = axes[offset + i][0];
            axes[offset + i][0] = -axes[offset + i][1];
            axes[offset + i][1] = temp;
        }
    }

    private static float[] projeterPolygone(float[] coins, float axeX, float axeY) {
        float min = coins[0] * axeX + coins[1] * axeY;
        float max = min;
        for (int i = 1; i < 4; i++) {
            float proj = coins[i * 2] * axeX + coins[i * 2 + 1] * axeY;
            if (proj < min) min = proj;
            if (proj > max) max = proj;
        }
        return new float[]{min, max};
    }
}
