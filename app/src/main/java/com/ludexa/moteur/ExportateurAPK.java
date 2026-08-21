// haut 1
package com.ludexa.moteur;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.android.apksig.ApkSigner;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

public class ExportateurAPK {

    public interface InterfaceExport {
        void surProgression(String message);
        void surSucces(File apkFinal);
        void surErreur(String erreur);
    }

    public static void exporterJeu(Context contexte, File dossierProjet, InterfaceExport callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                mainHandler.post(() -> callback.surProgression("Préparation des fichiers..."));
                File cacheDir = contexte.getCacheDir();

                // ---------------------------------------------------------
                // 1. COMPRESSION DU PROJET EN COURS
                // ---------------------------------------------------------
                mainHandler.post(() -> callback.surProgression("Compression du projet..."));
                File fichierZipProjet = new File(cacheDir, "jeu_exporte.zip");
                if (fichierZipProjet.exists()) fichierZipProjet.delete();

                ZipFile zipProjet = new ZipFile(fichierZipProjet);
                File[] fichiers = dossierProjet.listFiles();
                if (fichiers != null) {
                    for (File f : fichiers) {
                        if (f.isDirectory()) {
                            zipProjet.addFolder(f);
                        } else {
                            zipProjet.addFile(f);
                        }
                    }
                }

                // ---------------------------------------------------------
                // 2. EXTRACTION DU RUNNER
                // ---------------------------------------------------------
                mainHandler.post(() -> callback.surProgression("Extraction du Runner Yop2D..."));
                File apkTemporaire = new File(cacheDir, "apk_temporaire.apk");
                if (apkTemporaire.exists()) apkTemporaire.delete();

                InputStream isRunner = contexte.getAssets().open("modele_runner.apk");
                OutputStream osRunner = new FileOutputStream(apkTemporaire);
                byte[] buffer = new byte[8192];
                int lu;
                while ((lu = isRunner.read(buffer)) > 0) {
                    osRunner.write(buffer, 0, lu);
                }
                isRunner.close();
                osRunner.close();

                // ---------------------------------------------------------
                // 3. INJECTION (DONNÉES + ICÔNE)
                // ---------------------------------------------------------
                mainHandler.post(() -> callback.surProgression("Injection des données et de l'icône..."));
                ZipFile zipApk = new ZipFile(apkTemporaire);
                ZipParameters paramsInjection = new ZipParameters();
                
                // A. Injection des données du jeu
                paramsInjection.setFileNameInZip("assets/jeu_exporte.zip");
                zipApk.addFile(fichierZipProjet, paramsInjection);

                // B. Injection de la vignette comme Icône de l'application
                File vignette = new File(dossierProjet, "vignette.png");
                if (vignette.exists()) {
                    // Suppression des icônes système par défaut pour forcer la nôtre
                    try { zipApk.removeFile("res/mipmap-anydpi-v26/ic_launcher.xml"); } catch (Exception e){}
                    try { zipApk.removeFile("res/mipmap-anydpi-v26/ic_launcher_round.xml"); } catch (Exception e){}

                    // Écrasement des PNG dans toutes les résolutions d'écran d'Android
                    String[] densites = {"mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};
                    for (String d : densites) {
                        paramsInjection.setFileNameInZip("res/mipmap-" + d + "-v4/ic_launcher.png");
                        zipApk.addFile(vignette, paramsInjection);
                        paramsInjection.setFileNameInZip("res/mipmap-" + d + "-v4/ic_launcher_round.png");
                        zipApk.addFile(vignette, paramsInjection);
                    }
                }

                // C. EMPLACEMENT POUR LA MODIFICATION DU NOM BIENTÔT
                String nomChoisi = EcranDemarrage.nomJeuAExporter;
                // ChirurgienAXML.modifierNom(apkTemporaire, nomChoisi);

                // ---------------------------------------------------------
                // 4. SÉCURITÉ ET SIGNATURE
                // ---------------------------------------------------------
                mainHandler.post(() -> callback.surProgression("Création du certificat de sécurité..."));
                String aliasCle = "Yop2DExportKey";
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);

                if (!keyStore.containsAlias(aliasCle)) {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                    kpg.initialize(new KeyGenParameterSpec.Builder(
                            aliasCle,
                            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                            .setCertificateSubject(new X500Principal("CN=Yop2D, O=Yop2D"))
                            .setCertificateSerialNumber(BigInteger.ONE)
                            .setCertificateNotBefore(new Date())
                            .setCertificateNotAfter(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 30))
                            .build());
                    kpg.generateKeyPair();
                }

                PrivateKey clePrivee = (PrivateKey) keyStore.getKey(aliasCle, null);
                X509Certificate certificat = (X509Certificate) keyStore.getCertificate(aliasCle);

                mainHandler.post(() -> callback.surProgression("Signature cryptographique de l'APK..."));
                File apkFinal = new File(cacheDir, "jeu_final_signe.apk");
                if (apkFinal.exists()) apkFinal.delete();

                ApkSigner.SignerConfig configSignature = new ApkSigner.SignerConfig.Builder(
                        aliasCle, clePrivee, Collections.singletonList(certificat)).build();

                ApkSigner signer = new ApkSigner.Builder(Collections.singletonList(configSignature))
                        .setInputApk(apkTemporaire)
                        .setOutputApk(apkFinal)
                        .build();
                signer.sign();

                // ---------------------------------------------------------
                // 5. NETTOYAGE
                // ---------------------------------------------------------
                fichierZipProjet.delete();
                apkTemporaire.delete();

                mainHandler.post(() -> callback.surSucces(apkFinal));

            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.surErreur("Erreur d'export : " + e.getMessage()));
            }
        }).start();
    }
}
// bas 1
