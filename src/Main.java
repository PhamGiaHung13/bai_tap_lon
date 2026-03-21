import core.UI.GameFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Chạy UI trên Thread riêng của Swing để tránh lỗi xung đột
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}