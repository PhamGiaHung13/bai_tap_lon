package core.Audio;

import core.Config.Settings;
import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    private static boolean enabled = true;

    public static void setEnabled(boolean e){
        enabled = e;
    }

    public static void play(String path){
        if(!enabled) return;

        try{
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();// -------- MAY PHAT AM THANH CLIP
            clip.open(audio);

            // ------ BO DIEU KHIEN CUA CLIP (MASTER_GAIN)
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // ---- CONG THUC CHUYEN VOLUME TU LINEAR -> dB
            float dB = (float)(Math.log(Settings.volume) / Math.log(10.0) * 20.0);
            gain.setValue(dB);

            clip.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

