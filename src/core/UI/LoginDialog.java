package core.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginDialog extends JDialog {
    private JTextField txtName;
    private String inputName = null;

    public LoginDialog(Frame parent) {
        super(parent, true);
        setUndecorated(true);
        setSize(420, 240);
        setLocationRelativeTo(parent);

        // Cho phép cửa sổ có hình dạng bo tròn
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));

        // Panel chính
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Nền màu xám cực tối (Charcoal)
                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                // Viền vàng Neon
                g2.setColor(new Color(220, 208, 48));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 40, 40);
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Tiêu đề
        JLabel lblTitle = new JLabel("PLAYER LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 22));
        lblTitle.setForeground(new Color(220, 208, 48));

        // Ô nhập liệu
        txtName = new JTextField();
        txtName.setFont(new Font("Consolas", Font.BOLD, 20));
        txtName.setForeground(Color.WHITE);
        txtName.setCaretColor(new Color(220, 208, 48));
        txtName.setOpaque(false);
        txtName.setHorizontalAlignment(JTextField.CENTER);
        txtName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY),
                new EmptyBorder(10, 0, 5, 0)
        ));

        // Nút Start
        JButton btnStart = new JButton("START GAME");
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStart.setBackground(new Color(220, 208, 48));
        btnStart.setForeground(Color.BLACK);
        btnStart.setFocusPainted(false);
        btnStart.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnStart.addActionListener(e -> {
            inputName = txtName.getText();
            dispose();
        });

        // Nhấn Enter để gửi
        txtName.addActionListener(e -> btnStart.doClick());

        JPanel center = new JPanel(new GridLayout(2, 1, 5, 5));
        center.setOpaque(false);
        JLabel lblHint = new JLabel("Enter your name bro:", SwingConstants.CENTER);
        lblHint.setForeground(Color.LIGHT_GRAY);
        center.add(lblHint);
        center.add(txtName);

        content.add(lblTitle, BorderLayout.NORTH);
        content.add(center, BorderLayout.CENTER);
        content.add(btnStart, BorderLayout.SOUTH);

        setContentPane(content);
    }

    public String getInputName() { return inputName; }
}