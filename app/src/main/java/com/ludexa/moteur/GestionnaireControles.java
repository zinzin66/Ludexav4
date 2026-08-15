// haut 1
package com.ludexa.moteur;

public class GestionnaireControles {
    // --- HUD Aventure (Joystick & Action) ---
    public static boolean modeAventureActif = true; 
    public static float joyDirX = 0f;
    public static float joyDirY = 0f;
    public static boolean isActionPressed = false;
    public static boolean isActionJustPressed = false;

    // --- Système de Caméra ---
    public static String cameraCibleId = null;
    public static float cameraX = 0f;
    public static float cameraY = 0f;
    
    // Bornes de la caméra repoussées à "l'infini" (valeurs extrêmes)
    public static float limiteMinX = -999999f;
    public static float limiteMaxX = 999999f; 
    public static float limiteMinY = -999999f;
    public static float limiteMaxY = 999999f;

    // Réinitialisation propre à chaque lancement du mode Play
    public static void reinitialiser() {
        joyDirX = 0f;
        joyDirY = 0f;
        isActionPressed = false;
        isActionJustPressed = false;
        cameraCibleId = null;
        cameraX = 0f;
        cameraY = 0f;
        limiteMinX = -999999f;
        limiteMaxX = 999999f;
        limiteMinY = -999999f;
        limiteMaxY = 999999f;
    }
}
// bas 1
