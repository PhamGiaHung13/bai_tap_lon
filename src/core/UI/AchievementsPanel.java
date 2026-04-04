package core.UI;

import core.Audio.SoundManager;

import javax.swing.*;
import java.awt.*;

public class AchievementsPanel extends JPanel {

    private Image background = new ImageIcon(getClass().getResource("/Image/menu4.png")).getImage();

    public AchievementsPanel(GameFrame frame) {
        setLayout(new GridBagLayout());

        // 1. Tạo Box Kính mờ (Glass Box)
        JPanel glassBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền trắng mờ (Glassmorphism)
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Viền sáng
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 40, 40);
                g2.dispose();
            }
        };
        glassBox.setOpaque(false);
        glassBox.setLayout(new GridBagLayout());
        glassBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        // 2. Tiêu đề bảng thành tích
        JLabel title = new JLabel("BEST TIMES");
        title.setFont(new Font("Arial", Font.BOLD, 45));
        title.setForeground(new Color(220, 208, 48)); // Màu vàng đồng bộ Title game
        gbc.gridy = 0;
        glassBox.add(title, gbc);

        // 3. Hiển thị kỷ lục (Ví dụ giả lập dữ liệu)
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 1; glassBox.add(createRecordLabel("EASY: 00:45s"), gbc);
        gbc.gridy = 2; glassBox.add(createRecordLabel("MEDIUM: 02:10s"), gbc);
        gbc.gridy = 3; glassBox.add(createRecordLabel("HARD: 05:30s"), gbc);

        // 4. Nút BACK
        JButton backBtn = createSimpleButton("BACK");
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 10, 0);
        glassBox.add(backBtn, gbc);

        backBtn.addActionListener(e -> {
            SoundManager.play("src/Sound/tunetank.com_interface-cursor-click.wav");
            frame.showMenu();
        });

        add(glassBox);
    }

    // Hàm tạo Label kỷ lục cho đẹp
    private JLabel createRecordLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(new Color(60, 60, 60)); // Xám đậm chuyên nghiệp
        return label;
    }

    // Hàm tạo nút Back đồng bộ
    private JButton createSimpleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(220, 208, 48));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.BLACK);
            }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 20)); // Overlay nhẹ cho sâu
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}