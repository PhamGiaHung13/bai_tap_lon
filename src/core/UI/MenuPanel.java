package core.UI;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    private Image background;

    public MenuPanel(GameFrame frame) {

        // load ảnh
        background = new ImageIcon("src/Image/wtf8.png").getImage();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = new JLabel("SUPER MINESWEEPER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.PINK);

        gbc.gridy = 0;
        add(titleLabel, gbc);

        JButton playBtn = createStyledButton("PLAY", new Color(221, 15, 15));
        gbc.gridy = 1;
        add(playBtn, gbc);

        JButton exitBtn = createStyledButton("EXIT", new Color(0, 76, 232));
        gbc.gridy = 3;
        add(exitBtn, gbc);

        JButton settingBtn = createStyledButton("Setting", new Color(232, 157, 14));
        gbc.gridy = 2;
        add(settingBtn, gbc);

        playBtn.addActionListener(e -> frame.startGame());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // vẽ ảnh full panel
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setFocusPainted(false);// bo vien khi click
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        return btn;
    }
}