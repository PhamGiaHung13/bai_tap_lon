package minigames.blockblast;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Block Blast");

            BlockBlastPanel panel = new BlockBlastPanel();
            frame.add(panel);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.requestFocusInWindow(); // đảm bảo nhận phím
        });
    }
}