// haut 1
package com.ludexa.moteur;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import java.util.HashMap;

public class GestionnaireAudio {
    private static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    private static HashMap<String, Integer> soundMap = new HashMap<>();

    public static void init() {
        if (soundPool == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                soundPool = new SoundPool.Builder()
                        .setMaxStreams(10) // Joue jusqu'à 10 bruitages simultanés
                        .setAudioAttributes(attributes)
                        .build();
            } else {
                soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            }
        }
    }

    public static void jouerSon(String cheminAbsolu) {
        init();
        if (cheminAbsolu == null || cheminAbsolu.isEmpty()) return;

        if (soundMap.containsKey(cheminAbsolu)) {
            int soundId = soundMap.get(cheminAbsolu);
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);
        } else {
            int soundId = soundPool.load(cheminAbsolu, 1);
            soundMap.put(cheminAbsolu, soundId);
            soundPool.setOnLoadCompleteListener((sp, id, status) -> {
                if (status == 0 && id == soundId) {
                    sp.play(id, 1f, 1f, 1, 0, 1f);
                }
            });
        }
    }

    public static void jouerMusique(String cheminAbsolu, boolean enBoucle) {
        arreterMusique(); // Arrête la musique précédente
        if (cheminAbsolu == null || cheminAbsolu.isEmpty()) return;
        
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(cheminAbsolu);
            mediaPlayer.setLooping(enBoucle);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void arreterMusique() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
// bas 1
