package core.Config;

import java.io.*;

public class Settings {

    public static boolean soundEnabled = true;
    public static float volume = 0.5f; // 0.0 -> 1.0

    private static final String FILE = "settings.dat";

    public static void save(){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))){
            out.writeBoolean(soundEnabled);
            out.writeFloat(volume);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void load(){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))){
            soundEnabled = in.readBoolean();
            volume = in.readFloat();
        }catch(Exception e){
            // ------ DEFAULT SETTING NEU CHUA CO FILE
            soundEnabled = true;
            volume = 0.5f;
        }
    }
}

