package core.Config;

import java.io.*;

public class Settings {

    public static boolean soundEnabled = true;
    public static float volume = 0.5f;
    public static float musicVolume = 0.4f;

    private static final String FILE = "settings.dat";

    public static void save(){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))){
            out.writeBoolean(soundEnabled);
            out.writeFloat(volume);
            out.writeFloat(musicVolume);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void load(){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))){
            soundEnabled = in.readBoolean();
            volume = in.readFloat();
            musicVolume = in.readFloat();
        }catch(Exception e){
            // DEFAULT SETTING
            soundEnabled = true;
            volume = 0.5f;
            musicVolume = 0.4f;
        }
    }
}