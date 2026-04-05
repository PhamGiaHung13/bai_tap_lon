package minigames;

import Controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MinigamePanel extends JPanel {
    protected GameController controller;
    protected boolean isGameOver = false;
    protected boolean isVictory = false;

    public MinigamePanel(GameController controller) {
        this.controller = controller;
        this.setFocusable(true);

        // Lắng nghe chuột để thoát game khi đã GameOver (khớp với MazePanel)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOver) {
                    handleExit(); // Đổi từ exitMinigame thành handleExit để khớp với Maze
                }
            }
        });
    }

    /**
     * Hàm trừu tượng để lớp con định nghĩa logic thoát (dừng nhạc, timer...)
     * Tên hàm: handleExit (khớp 100% với file Maze của ông)
     */
    protected abstract void handleExit();

    /**
     * Hàm vẽ bảng thông báo dùng chung
     * Tên hàm: drawCommonOverlay (khớp với dòng gọi hàm cuối paintComponent)
     */
    protected void drawCommonOverlay(Graphics2D g2) {
        if (isGameOver) {
            // 1. Vẽ lớp phủ tối (Overlay)
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            // 2. Xác định nội dung thông báo
            String msg = isVictory ? "MISSION ACCOMPLISHED" : "MISSON FAILED";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 50));
            FontMetrics fm = g2.getFontMetrics();
            int mx = (getWidth() - fm.stringWidth(msg)) / 2;
            int my = getHeight() / 2;

            // 3. Vẽ Shadow (Đổ bóng)
            g2.setColor(Color.BLACK);
            g2.drawString(msg, mx + 3, my + 3);

            // 4. Vẽ chữ chính (Thắng: Xanh, Thua: Đỏ)
            g2.setColor(isVictory ? new Color(0, 255, 150) : new Color(255, 50, 50));
            g2.drawString(msg, mx, my);

            // 5. Vẽ dòng chữ hướng dẫn click để thoát
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 22));
            g2.setColor(Color.WHITE);
            String subMsg = "Click anywhere to return to game";
            g2.drawString(subMsg, (getWidth() - g2.getFontMetrics().stringWidth(subMsg)) / 2, my + 70);
        }
    }
}