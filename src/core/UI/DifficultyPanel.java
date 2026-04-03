package core.UI;

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
        glassBox.setOpaque(false);
        glassBox.setLayout(new GridBagLayout());
        glassBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        JButton easy = createButton("Easy");
        JButton medium = createButton("Medium");
        JButton hard = createButton("Hard");
        JButton back = createButton("Back");

        gbc.gridy = 0; glassBox.add(easy, gbc);
        gbc.gridy = 1; glassBox.add(medium, gbc);
        gbc.gridy = 2; glassBox.add(hard, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 0, 10, 0);
        glassBox.add(back, gbc);

        add(glassBox);





        // ------- ACTION LISTENER
        easy.addActionListener(e -> frame.startGame(1));
        medium.addActionListener(e -> frame.startGame(2));
        hard.addActionListener(e -> frame.startGame(3));
        back.addActionListener(e -> frame.showMenu());

    }





    /// --------- BUTTON
    private JButton createButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(Color.black);
        btn.setBackground(new Color(222, 227, 230));


        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // hover + click effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e){
                btn.setForeground(new Color(191, 178, 21)); // glow vàng
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setForeground(Color.black);
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
