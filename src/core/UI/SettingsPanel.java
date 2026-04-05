package core.UI;

import core.Audio.SoundManager;
import core.Config.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SettingsPanel extends JPanel {

    private Image background = new ImageIcon(getClass().getResource("/core/Image/menu4.png")).getImage();

    public SettingsPanel(GameFrame frame) {
        setLayout(new GridBagLayout());

        // 1. KHỞI TẠO CÁC COMPONENT TRƯỚC

        // ---- TITLE SETTING
        JLabel title = new JLabel("SETTING");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(Color.BLACK);

        // ----------- SOUND TOGGLE (Bật/Tắt âm thanh nói chung)
        JCheckBox soundToggle = new JCheckBox("Enable Sound");
        soundToggle.setForeground(Color.BLACK);
        soundToggle.setFont(new Font("Arial", Font.BOLD, 25));
        soundToggle.setOpaque(false);
        soundToggle.setFocusable(false);
        soundToggle.setSelected(Settings.soundEnabled);

        // --- SOUND EFFECTS (SFX) Slider
        JLabel lblSound = new JLabel("Sound Effects (SFX)");
        lblSound.setFont(new Font("Arial", Font.BOLD, 20));
        JSlider soundSlider = new JSlider(0, 100);
        soundSlider.setValue((int) (Settings.volume * 100));
        soundSlider.setOpaque(false);

        // --- MUSIC VOLUME (BGM) Slider
        JLabel lblMusic = new JLabel("Music Volume (BGM)");
        lblMusic.setFont(new Font("Arial", Font.BOLD, 20));
        JSlider musicSlider = new JSlider(0, 100);
        musicSlider.setValue((int) (Settings.musicVolume * 100));
        musicSlider.setOpaque(false);

        // --- BACK BUTTON
        JButton backBtn = createSettingsButton("BACK");

        // 2. KHỞI TẠO BOX (PANEL CHỨA) VÀ LAYOUT
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Hiệu ứng Glassmorphism (Trắng mờ)
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                // Viền trắng nhẹ
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 40, 40);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setLayout(new GridBagLayout());
        box.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints boxGbc = new GridBagConstraints();
        boxGbc.gridx = 0;
        boxGbc.fill = GridBagConstraints.HORIZONTAL;
        boxGbc.insets = new Insets(10, 0, 10, 0);

        // 3. THÊM CÁC THÀNH PHẦN VÀO BOX THEO THỨ TỰ gridy
        boxGbc.gridy = 0;
        boxGbc.anchor = GridBagConstraints.CENTER;
        box.add(title, boxGbc);

        boxGbc.gridy = 1;
        box.add(soundToggle, boxGbc);

        boxGbc.gridy = 2;
        boxGbc.insets = new Insets(10, 0, 0, 0);
        box.add(lblSound, boxGbc);

        boxGbc.gridy = 3;
        boxGbc.insets = new Insets(0, 0, 10, 0);
        box.add(soundSlider, boxGbc);

        boxGbc.gridy = 4;
        boxGbc.insets = new Insets(10, 0, 0, 0);
        box.add(lblMusic, boxGbc);

        boxGbc.gridy = 5;
        boxGbc.insets = new Insets(0, 0, 10, 0);
        box.add(musicSlider, boxGbc);

        boxGbc.gridy = 6;
        boxGbc.insets = new Insets(30, 0, 0, 0);
        box.add(backBtn, boxGbc);

        // 4. THÊM BOX VÀO SETTINGSPANEL CHÍNH
        add(box);

        // 5. XỬ LÝ SỰ KIỆN (LISTENERS)
        soundToggle.addActionListener(e -> {
            SoundManager.play("src/core/Sound/tunetank.com_interface-cursor-click.wav");
            Settings.soundEnabled = soundToggle.isSelected();
            Settings.save();
        });

        soundSlider.addChangeListener(e -> {
            Settings.volume = soundSlider.getValue() / 100f;
            Settings.save();
        });

        musicSlider.addChangeListener(e -> {
            Settings.musicVolume = musicSlider.getValue() / 100f;
            SoundManager.updateBGMVolume(); // Cập nhật nhạc nền ngay lập tức
            Settings.save();
        });

        backBtn.addActionListener(e -> {
            SoundManager.play("src/core/Sound/tunetank.com_interface-cursor-click.wav");
            frame.showMenu();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ background full màn hình
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        // Lớp overlay tối nhẹ để làm nổi bật Box setting
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private JButton createSettingsButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e) {

                SoundManager.play("src/core/Sound/menu_hover.wav");
                btn.setForeground(new Color(220, 208, 48)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.BLACK); }
        });
        return btn;
    }
}