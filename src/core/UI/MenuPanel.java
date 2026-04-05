package core.UI;

import DB.GameDAO;
import DB.Player;
import core.Audio.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel {

    private Image background;


///  ---------- CONSTRUCTOR
    public MenuPanel(GameFrame frame) {



        setPreferredSize(new Dimension(1200, 700));

        // ----- BACKGROUND
        background = new ImageIcon(getClass().getResource("/Image/menu4.png")).getImage();

        // ----- MUSIC
        SoundManager.playBGM("src/core/Sound/music.wav");



        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các nút

// Thêm khoảng trống phía trên để nhường chỗ cho TITLE (HKL MINESWEEPER)
        gbc.gridy = 0;
        add(Box.createVerticalStrut(95), gbc);

      //--------- BUTTON
        JButton playBtn = createGameButton("PLAY");
        gbc.gridy = 1;
        add(playBtn, gbc);

        JButton settingBtn = createGameButton("Setting");
        gbc.gridy = 2;
        add(settingBtn, gbc);

        JButton achievementsBtn = createGameButton("Achievements");
        gbc.gridy = 3;
        add(achievementsBtn, gbc);

        JButton exitBtn = createGameButton("EXIT");
        gbc.gridy = 4;
        add(exitBtn, gbc);





        /// -----  PLAY -> DIFFICULTY PANEL
        playBtn.addActionListener(e -> {
            SoundManager.play("src/core/Sound/tunetank.com_interface-cursor-click.wav");
            frame.showDifficulty();
        });
        settingBtn.addActionListener(e -> {
            SoundManager.play("src/core/Sound/tunetank.com_interface-cursor-click.wav");
            frame.showSetting();
        });
        achievementsBtn.addActionListener(e -> {
            SoundManager.play("src/core/Sound/tunetank.com_interface-cursor-click.wav");

            frame.showAchievements();
        });
        exitBtn.addActionListener(e -> {
            SoundManager.play("src/core/Sound/tunetank.com_interface-cursor-click.wav");
            System.exit(0);
        });



        /// -----
    }






/// -------------- PAINT COMPONENT
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int w = getWidth();
    int h = getHeight();

    // ===== BACKGROUND =====
    g.drawImage(background, 0, 0, w, h, this);

    // ===== OVERLAY =====
    g.setColor(new Color(0, 0, 0, 0));
    g.fillRect(0, 0, w, h);





  //   ===== TITLE =====
    Graphics2D g2 = (Graphics2D) g;

    g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );

    Font font = new Font("Arial", Font.BOLD, 50);
    g2.setFont(font);

    String text = "HKL MINESWEEPER";

    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(text);

    int x = (w - textWidth) / 2;
    int y = 215;

    // shadow
    g2.setColor(new Color(0, 0, 0, 150));
    g2.drawString(text, x + 3, y + 3);

    // main text
    g2.setColor(new Color(220, 208, 48));
    g2.drawString(text, x, y);
}




/// ---------- DESIGN BUTTON _______
    private JButton createGameButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(new Color(60, 60, 60));

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // hover + click effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {


            public void mouseEntered(java.awt.event.MouseEvent e){
                SoundManager.play("src/core/Sound/menu_hover.wav");
                btn.setForeground(new Color(220, 208, 48));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 32));
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setForeground(new Color(60, 60, 60));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
            }

        });

        return btn;
    }
}