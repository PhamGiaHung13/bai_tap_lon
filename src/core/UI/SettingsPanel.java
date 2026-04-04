package core.UI;

import core.Config.Settings;

import javax.sound.midi.VoiceStatus;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SettingsPanel extends JPanel {

    private Image background = new ImageIcon(getClass().getResource("/Image/menu4.png")).getImage();

    public SettingsPanel(GameFrame frame){
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;




        // ---- TITLE SETTING
        JLabel title = new JLabel("SETTING");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(Color.BLACK);



        // ----------- SOUND TOGGLE
        JCheckBox soundToggle = new JCheckBox("Sound");
        soundToggle.setForeground(Color.BLACK);
        soundToggle.setFont(new Font("Arial", Font.BOLD, 30));
        soundToggle.setOpaque(false);




        // ------- SLIDER
        JSlider volumeSlider = new JSlider(0,100);
        volumeSlider.setPreferredSize(new Dimension(300, 50));
        volumeSlider.setValue((int)(Settings.volume * 100));
        volumeSlider.setOpaque(false);
        volumeSlider.setBackground(new Color(255, 255, 255, 0));
        volumeSlider.setForeground(new Color(220, 208, 48));






        /// ------- BOX CHUA SETTING
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glassmorphism
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Vien trang
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 40, 40);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setLayout(new GridBagLayout());
        box.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints boxGbc = new GridBagConstraints();
        boxGbc.insets = new Insets(15, 0, 15, 0);
        boxGbc.gridx = 0;

        boxGbc.gridy = 0;
        box.add(title, boxGbc);

        boxGbc.gridy = 1;
        box.add(soundToggle, boxGbc);

        boxGbc.gridy = 2;
        box.add(volumeSlider, boxGbc);

        JButton backBtn = createSettingsButton("BACK");
        boxGbc.gridy = 3;
        boxGbc.insets = new Insets(30, 0, 0, 0);

        box.add(backBtn, boxGbc);

       add(box);







        // load state
        soundToggle.setSelected(Settings.soundEnabled);

        // event
        soundToggle.addActionListener(e -> {
            Settings.soundEnabled = soundToggle.isSelected();
            Settings.save();
        });

        volumeSlider.addChangeListener(e -> {
            Settings.volume = volumeSlider.getValue() / 100f;
            Settings.save();
        });

        backBtn.addActionListener(e -> frame.showMenu());

    }





    ///  --------- BACKGROUND
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        // ===== BACKGROUND =====
        g.drawImage(background, 0, 0, w, h, this);

        // ===== OVERLAY =====
        g.setColor(new Color(0, 0, 0, 20));
        g.fillRect(0, 0, w, h);
    }




    /// ---------BUTTON
    private JButton createSettingsButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(new Color(220, 208, 48)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.BLACK); }
        });
        return btn;
    }
}
