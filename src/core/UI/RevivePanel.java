package core.UI;

import Controller.GameController;
import core.Logic.Tile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RevivePanel extends JPanel {
    private Image bgImage;
    private GameController controller;
    private int difficulty;
    private Tile bombTile;

    public RevivePanel(Tile bombTile, int difficulty, GameController controller) {
        this.bombTile = bombTile;
        this.difficulty = difficulty;
        this.controller = controller;

        try {
            bgImage = new ImageIcon(getClass().getResource("/core/Image/revive.jpg")).getImage();
        } catch (Exception e) {
            System.err.println("Lỗi nạp ảnh: " + e.getMessage());
        }

        // Lắng nghe cú click chuột bất kỳ để gọi sang Minigame
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Khi người chơi click, bảo Controller: "Ok, vào game phụ đi!"
                controller.switchToRandomMinigame(bombTile, difficulty);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.setFont(new Font("Arial", Font.BOLD, 35));
        String text = "CLICK ANYWHERE TO START YOUR TRIAL";
        int x = (getWidth() - g2.getFontMetrics().stringWidth(text)) / 2;

        // Vẽ bóng đổ cho chữ
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(text, x + 3, getHeight() - 97);
        // Chữ chính
        g2.setColor(Color.BLACK);
        g2.drawString(text, x, getHeight() - 100);
    }
}