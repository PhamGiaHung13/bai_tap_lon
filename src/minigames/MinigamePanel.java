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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOver) {
                    forceExit(); // Đổi thành forceExit để xử lý tập trung
                }
            }
        });
    }

    // Hàm này ép buộc đóng cửa sổ và quay về Menu
    protected void forceExit() {
        handleExit(); // Gọi hàm dọn dẹp (dừng nhạc, timer) của lớp con

        if (controller != null) {
            if (isVictory) controller.onMinigameWin();
            else controller.onMinigameLose();
        } else {
            // Nếu không có controller, tự tìm Frame và đóng để quay về main
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) win.dispose();
        }
    }
    protected abstract void handleExit();

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