import core.UI.GameFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //chay tren Thread rieng cua swing
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
        });
    }

}