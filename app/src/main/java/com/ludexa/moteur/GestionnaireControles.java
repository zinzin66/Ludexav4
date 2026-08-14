// haut 1
package com.ludexa.moteur;

public class GestionnaireControles {
    // --- HUD Aventure (Joystick & Action) ---
    public static boolean modeAventureActif = true; // À basculer selon le besoin
    public static float joyDirX = 0f;
    public static float joyDirY = 0f;
    public static boolean isActionPressed = false;
    public static boolean isActionJustPressed = false;

    // --- Système de Caméra ---
    public static String cameraCibleId = null;
    public static float cameraX = 0f;
    public static float cameraY = 0f;
    
    // Bornes de la caméra (Clamping) - Ajustable selon la taille de ton niveau
    public static float limiteMinX = 0f;
    public static float limiteMaxX = 3000f; 
    public static float limiteMinY = 0f;
    public static float limiteMaxY = 3000f;
}
// bas 1
