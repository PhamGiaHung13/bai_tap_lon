package core.UI;

import core.Audio.SoundManager;

import javax.swing.*;
import java.awt.*;


public class DifficultyPanel extends JPanel {

    private Image background;

    public DifficultyPanel(GameFrame frame){
        background = new ImageIcon(getClass().getResource("/Image/1.png")).getImage();
        setLayout(new GridBagLayout());


        JPanel glassBox = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 180)); // Trắng mờ
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };

        glassBox.setPreferredSize(new Dimension(400, 500));
        glassBox.setOpaque(false);
        glassBox.setLayout(new GridLayout(4,1,20,0));
        glassBox.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));



        JButton easy = createButton("Easy");
        JButton medium = createButton("Medium");
        JButton hard = createButton("Hard");
        JButton back = createButton("Back");
        back.setBorder(BorderFactory.createEmptyBorder(30, 0,0,0));

        glassBox.add(easy);
        glassBox.add(medium);
        glassBox.add(hard);

        glassBox.add(back);

        add(glassBox);





        // ------- ACTION LISTENER
        easy.addActionListener(e -> {
            SoundManager.play("src/Sound/start.wav");
            frame.startGame(1);

        });
        medium.addActionListener(e ->{
            SoundManager.play("src/Sound/start.wav");
            frame.startGame(2);
        });

        hard.addActionListener(e ->{
            SoundManager.play("src/Sound/start.wav");
            frame.startGame(3);
        });
        back.addActionListener(e -> {
            SoundManager.play("src/Sound/tunetank.com_interface-cursor-click.wav");
            frame.showMenu();
        });

    }





    /// --------- BUTTON
    private JButton createButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(new Color(40, 40, 40));
        btn.setBackground(new Color(222, 227, 230));


        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // hover + click effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e){
                SoundManager.play("src/Sound/menu_hover.wav");
                btn.setForeground(new Color(191, 178, 21)); // glow vàng
                btn.setFont(new Font("Segoe UI", Font.BOLD, 32));
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setForeground(new Color(40, 40, 40));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 28));

            }

        });

        return btn;
    }




    ///  ---------- PAINT BACKGROUND
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        g.drawImage(background, 0, 0, w, h, this);
        g.setColor(new Color(0, 0, 0, 20));
        g.fillRect(0, 0, w, h);
    }
}
