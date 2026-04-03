package core.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel {

    private Image background;
//    JLabel



    ///  ---------- CONSTRUCTOR
    public MenuPanel(GameFrame frame) {

        setPreferredSize(new Dimension(1200, 700));

        // load ảnh
        background = new ImageIcon(getClass().getResource("/Image/menu4.png")).getImage();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();


//        //--------- BUTTON
        JButton playBtn = createGameButton("PLAY");
        gbc.gridy = 1;
        add(playBtn, gbc);
//
        JButton settingBtn = createGameButton("Setting");
        gbc.gridy = 2;
        add(settingBtn, gbc);
//
        JButton achievementsBtn = createGameButton("Achievements");
        gbc.gridy = 3;
        add(achievementsBtn, gbc);
//
        JButton exitBtn = createGameButton("EXIT");
        gbc.gridy = 4;
        add(exitBtn, gbc);





        /// -----  DIFFICULTY
        playBtn.addActionListener(e ->{
            String[] options = {"Easy","Medium","Hard"};

            int choice = JOptionPane.showOptionDialog(this, "Choose difficulty", "Difficulty",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            if(choice == 0) frame.startGame(1);
            else if(choice == 1) frame.startGame(2);
            else frame.startGame(3);
        });
        exitBtn.addActionListener(e -> System.exit(0));



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
        g.setColor(new Color(0, 0, 0, 50));
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
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
    }




    /// ---------- DESIGN BUTTON _______
    private JButton createGameButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(Color.WHITE);

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // hover + click effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e){
                btn.setForeground(new Color(255, 220, 120)); // glow vàng
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setForeground(Color.WHITE);
            }

            public void mousePressed(MouseEvent e){
                btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 2, 10));
            }

            public void mouseReleased(MouseEvent e){
                btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
        });

        return btn;
    }
}