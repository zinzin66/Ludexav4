// haut 1
package com.ludexa.moteur;

public class GestionnaireControles {
    public static boolean modeAventureActif = true;
    public static float joyDirX = 0f;
    public static float joyDirY = 0f;
    public static boolean isActionPressed = false;
    public static boolean isActionJustPressed = false;

    public static String cameraCibleId = null;
    public static float cameraX = 0f;
    public static float cameraY = 0f;
    
    public static float limiteMinX = 0f;
    public static float limiteMaxX = 3000f; 
    public static float limiteMinY = 0f;
    public static float limiteMaxY = 3000f;

    public static void reinitialiser() {
        joyDirX = 0f;
        joyDirY = 0f;
        isActionPressed = false;
        isActionJustPressed = false;
        cameraCibleId = null;
        cameraX = 0f;
        cameraY = 0f;
        limiteMinX = 0f;
        limiteMaxX = 3000f;
        limiteMinY = 0f;
        limiteMaxY = 3000f;
    }
}
// bas 1
