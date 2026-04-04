package core.Audio;

import core.Config.Settings;
import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    private static Clip backgroundClip; // Giữ lại để có thể tắt nhạc khi vào game

    public static void play(String path){
        if(!Settings.soundEnabled) return;
        try{
            File file = new File(path);
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioFormat baseFormat = in.getFormat();

            // Tạo định dạng mới "nhẹ" hơn (16-bit, 44100Hz) mà Java chắc chắn đọc được
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100, 16, baseFormat.getChannels(),
                    baseFormat.getChannels() * 2, 44100, false
            );

            // Ép luồng âm thanh cũ sang định dạng mới
            AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
            Clip clip = AudioSystem.getClip();
            clip.open(din);

            clip.addLineListener(event -> {
                if(event.getType() == LineEvent.Type.STOP){
                    clip.close();
                }
            });

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


    ///  ----------- PLAY BGM
    public static void playBGM(String path) {
        if (!Settings.soundEnabled) return;
        try {
            // Dừng nhạc cũ nếu đang phát
            if (backgroundClip != null && backgroundClip.isRunning()) {
                backgroundClip.stop();
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audio);

            // Loop vô hạn cho nhạc Menu
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);

            // Chỉnh volume từ Settings
            FloatControl gain = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float)(Math.log(Settings.volume) / Math.log(10.0) * 20.0);
            gain.setValue(dB);

            backgroundClip.start();
        } catch (Exception e) { e.printStackTrace(); }
    }



    /// ------- STOP BGM
    public static void stopBGM() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }



    public static void updateBGMVolume() {
        // Kiểm tra xem backgroundClip có đang tồn tại không
        if (backgroundClip != null && backgroundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            try {
                FloatControl gain = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
                // Tính toán dB từ musicVolume trong Settings
                float dB = (float) (Math.log(Settings.musicVolume) / Math.log(10.0) * 20.0);

                // Đảm bảo giá trị dB nằm trong ngưỡng phần cứng cho phép
                dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));

                gain.setValue(dB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

