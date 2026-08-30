package com.ludexa.moteur;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class DiagLogger {
    private static final String NOM_FICHIER = "diag_ludexa.txt";

    public static void log(String cheminProjet, String message) {
        if (cheminProjet == null) return;
        try {
            File logFile = new File(cheminProjet, NOM_FICHIER);
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(System.currentTimeMillis() + " - " + message + "\n");
            fw.close();
        } catch (Exception e) {}
    }

    public static String lire(String cheminProjet) {
        if (cheminProjet == null) return "";
        File logFile = new File(cheminProjet, NOM_FICHIER);
        if (!logFile.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(logFile));
            String ligne;
            while ((ligne = br.readLine()) != null) {
                sb.append(ligne).append("\n");
            }
            br.close();
        } catch (Exception e) {}
        return sb.toString();
    }

    public static void effacer(String cheminProjet) {
        if (cheminProjet == null) return;
        File logFile = new File(cheminProjet, NOM_FICHIER);
        if (logFile.exists()) logFile.delete();
    }
}
