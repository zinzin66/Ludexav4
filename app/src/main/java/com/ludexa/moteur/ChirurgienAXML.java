// haut 1
package com.ludexa.moteur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

public class ChirurgienAXML {

    // Les cibles EXACTES de 50 caractères que l'on va définir dans GitHub
    private static final String CIBLE_NOM = "YOP2D_GAME_NAME_PLACEHOLDER_X_X_X_X_X_X_X_X_X_X_X_";
    private static final String CIBLE_PKG = "com.yop2d.export.placeholder.xxxxxxxxxxxxxxxxxxxxx";

    public static void opererAPK(File apkTemporaire, String nouveauNom, File cacheDir) throws Exception {
        ZipFile zip = new ZipFile(apkTemporaire);
        
        // 1. On extrait le Manifest binaire
        zip.extractFile("AndroidManifest.xml", cacheDir.getAbsolutePath());
        File manifestExtrait = new File(cacheDir, "AndroidManifest.xml");
        if (!manifestExtrait.exists()) return;

        // 2. On lit tous les octets du fichier
        FileInputStream fis = new FileInputStream(manifestExtrait);
        byte[] data = new byte[(int) manifestExtrait.length()];
        fis.read(data);
        fis.close();

        // 3. Sécurité anti-débordement (on limite la taille en octets pour ne pas casser le fichier)
        byte[] nvBytes = nouveauNom.getBytes(StandardCharsets.UTF_8);
        if (nvBytes.length > 50) {
            nouveauNom = nouveauNom.substring(0, 20); // Tronque si trop de caractères spéciaux
        }

        // Création d'un nom de package unique (pour pouvoir installer plusieurs jeux !)
        String idUnique = UUID.randomUUID().toString().substring(0, 6);
        String nouveauPkg = "com.jeu.yop2d_" + idUnique;

        // 4. Opération de remplacement (on teste UTF-8 puis UTF-16 au cas où)
        boolean nomChange = remplacer(data, CIBLE_NOM, nouveauNom);
        boolean pkgChange = remplacer(data, CIBLE_PKG, nouveauPkg);

        // 5. Si l'opération a réussi, on recoud le patient (réinjection)
        if (nomChange || pkgChange) {
            FileOutputStream fos = new FileOutputStream(manifestExtrait);
            fos.write(data);
            fos.close();

            // On supprime l'ancien Manifest et on met le nouveau
            try { zip.removeFile("AndroidManifest.xml"); } catch (Exception e) {}
            
            ZipParameters params = new ZipParameters();
            params.setFileNameInZip("AndroidManifest.xml");
            zip.addFile(manifestExtrait, params);
        }

        manifestExtrait.delete();
    }

    private static boolean remplacer(byte[] data, String cible, String valeur) {
        if (remplacerUTF8(data, cible, valeur)) return true;
        return remplacerUTF16(data, cible, valeur);
    }

    private static boolean remplacerUTF8(byte[] data, String cible, String valeur) {
        byte[] bCible = cible.getBytes(StandardCharsets.UTF_8);
        byte[] bValeur = valeur.getBytes(StandardCharsets.UTF_8);

        for (int i = 2; i < data.length - bCible.length; i++) {
            boolean match = true;
            for (int j = 0; j < bCible.length; j++) {
                if (data[i + j] != bCible[j]) { match = false; break; }
            }
            if (match) {
                // HACK AXML : On met à jour les préfixes de longueur juste avant la chaîne
                data[i - 2] = (byte) valeur.length();
                data[i - 1] = (byte) bValeur.length;

                // On écrit le nouveau nom
                for (int k = 0; k < bValeur.length; k++) data[i + k] = bValeur[k];
                // Terminateur de chaîne
                data[i + bValeur.length] = 0x00;
                // On remplit le reste des 50 caractères avec du vide
                for (int k = bValeur.length + 1; k < bCible.length; k++) data[i + k] = 0x00;
                return true;
            }
        }
        return false;
    }

    private static boolean remplacerUTF16(byte[] data, String cible, String valeur) {
        byte[] bCible = cible.getBytes(StandardCharsets.UTF_16LE);
        byte[] bValeur = valeur.getBytes(StandardCharsets.UTF_16LE);

        for (int i = 2; i < data.length - bCible.length; i += 2) {
            boolean match = true;
            for (int j = 0; j < bCible.length; j++) {
                if (data[i + j] != bCible[j]) { match = false; break; }
            }
            if (match) {
                data[i - 2] = (byte) (valeur.length() & 0xFF);
                data[i - 1] = (byte) ((valeur.length() >> 8) & 0xFF);

                for (int k = 0; k < bValeur.length; k++) data[i + k] = bValeur[k];
                data[i + bValeur.length] = 0x00;
                data[i + bValeur.length + 1] = 0x00;
                for (int k = bValeur.length + 2; k < bCible.length; k++) data[i + k] = 0x00;
                return true;
            }
        }
        return false;
    }
}
// bas 1
