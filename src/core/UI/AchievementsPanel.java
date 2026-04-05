package core.UI;

import DB.GameDAO;
import DB.Player; // Đảm bảo import class Player của ông
import core.Audio.SoundManager;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AchievementsPanel extends JPanel {

    private Image background = new ImageIcon(getClass().getResource("/Image/menu4.png")).getImage();

    // Thêm tham số Player vào đây
    public AchievementsPanel(GameFrame frame, Player player) {
        setLayout(new GridBagLayout());

        int playerId = player.getId(); // Lấy ID để truy vấn DB
        String playerName = player.getUsername(); // Lấy tên người chơi

        // 1. Tạo Box Kính mờ (Glass Box)
        JPanel glassBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 190));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 40, 40);
                g2.dispose();
            }
        };
        glassBox.setOpaque(false);
        glassBox.setLayout(new GridBagLayout());
        glassBox.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        // 2. Tiêu đề chính
        JLabel title = new JLabel("PLAYER ACHIEVEMENTS");
        title.setFont(new Font("Arial", Font.BOLD, 35));
        title.setForeground(new Color(220, 208, 48));
        gbc.gridy = 0;
        glassBox.add(title, gbc);

        // --- MỚI: HIỂN THỊ TÊN NGƯỜI CHƠI ---
        JLabel lblName = new JLabel("PLAYER: " + playerName.toUpperCase());
        lblName.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 22));
        lblName.setForeground(new Color(70, 70, 70)); // Màu xám tinh tế
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0); // Khoảng cách dưới tên
        glassBox.add(lblName, gbc);

        // 3. Lấy dữ liệu từ Database
        GameDAO dao = new GameDAO();
        Map<String, Object> data = dao.getFullAchievements(playerId);

        // --- PHẦN 1: HIỂN THỊ CHỈ SỐ TỔNG ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        statsPanel.setOpaque(false);
        statsPanel.add(createStatLabel("EXP: " + data.getOrDefault("total_exp", 0), new Color(46, 204, 113)));
        statsPanel.add(createStatLabel("COINS: " + data.getOrDefault("total_coins", 0), new Color(241, 196, 15)));
        statsPanel.add(createStatLabel("MASTERY: " + data.getOrDefault("total_mastery", 0), new Color(155, 89, 182)));

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        glassBox.add(statsPanel, gbc);

        // Phân cách
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(350, 2));
        gbc.gridy = 3;
        glassBox.add(sep, gbc);

        // --- PHẦN 2: KỶ LỤC THỜI GIAN ---
        JLabel subTitle = new JLabel("BEST TIMES");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        subTitle.setForeground(Color.DARK_GRAY);
        gbc.gridy = 4;
        glassBox.add(subTitle, gbc);

        gbc.gridy = 5; glassBox.add(createRecordLabel("EASY: " + formatTime((Double) data.getOrDefault("easy", 0.0))), gbc);
        gbc.gridy = 6; glassBox.add(createRecordLabel("MEDIUM: " + formatTime((Double) data.getOrDefault("medium", 0.0))), gbc);
        gbc.gridy = 7; glassBox.add(createRecordLabel("HARD: " + formatTime((Double) data.getOrDefault("hard", 0.0))), gbc);

        // 4. Nút BACK
        JButton backBtn = createSimpleButton("BACK");
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 5, 0);
        glassBox.add(backBtn, gbc);

        backBtn.addActionListener(e -> {
            SoundManager.play("src/Sound/tunetank.com_interface-cursor-click.wav");
            frame.showMenu();
        });

        add(glassBox);
    }

    // Các hàm trợ giúp giữ nguyên...
    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(color);
        return label;
    }

    private JLabel createRecordLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Monospaced", Font.BOLD, 24));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }

    private JButton createSimpleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 26));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(new Color(220, 208, 48)); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setForeground(Color.BLACK); }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private String formatTime(double seconds) {
        if (seconds <= 0) return "--:--s";
        int m = (int) (seconds / 60);
        int s = (int) (seconds % 60);
        return String.format("%02d:%02ds", m, s);
    }
}