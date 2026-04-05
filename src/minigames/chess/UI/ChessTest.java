package minigames.chess.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChessTest {

    private static int timeLeft = 600;

    // PANEL VẼ BACKGROUND FULL CỬA SỔ
    static class BackgroundPanel extends JPanel {
        private Image bg;

        public BackgroundPanel() {
            try {
                bg = ImageIO.read(
                        getClass().getResource("/minigames/chess/bg.png")
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Chess Puzzle Pro");

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize((int)(screen.width),
                    (int)(screen.height));

            // 🔥 SET BACKGROUND PANEL LÀM CONTENTPANE
            BackgroundPanel mainPanel = new BackgroundPanel();
            frame.setContentPane(mainPanel);

            // --- TRUNG TÂM: BÀN CỜ ---
            JPanel boardContainer = new JPanel(new GridBagLayout());
            boardContainer.setOpaque(false);

            ChessPanel chessPanel = new ChessPanel(1, null);

            // vẫn giữ kích thước 640x640 như m muốn
            chessPanel.setPreferredSize(new Dimension(640, 640));

            boardContainer.add(chessPanel);
            mainPanel.add(boardContainer, BorderLayout.CENTER);

            // --- SIDEBAR ---
            JPanel sidebar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();

                    // màu xám + alpha (độ trong suốt)
                    g2.setColor(new Color(38, 36, 33, 200));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.dispose();
                }
            };

            sidebar.setOpaque(false); // BẮT BUỘC
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setPreferredSize(new Dimension(320, 0));
            sidebar.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            JLabel timerTitle = new JLabel("TIME LEFT", SwingConstants.CENTER);
            timerTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
            timerTitle.setForeground(Color.GRAY);
            timerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel timerLabel = new JLabel("10:00", SwingConstants.CENTER);
            timerLabel.setFont(new Font("Monospaced", Font.BOLD, 60));
            timerLabel.setForeground(new Color(200, 50, 50));
            timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton quitBtn = new JButton("QUIT & LOSE");
            quitBtn.setMaximumSize(new Dimension(250, 60));
            quitBtn.setBackground(new Color(120, 0, 0));
            quitBtn.setForeground(Color.WHITE);
            quitBtn.setFont(new Font("Arial", Font.BOLD, 18));
            quitBtn.setFocusPainted(false);
            quitBtn.setBorder(BorderFactory.createLineBorder(
                    new Color(150, 50, 50), 2));
            quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            quitBtn.addActionListener(e -> System.exit(0));

            sidebar.add(timerTitle);
            sidebar.add(timerLabel);
            sidebar.add(Box.createVerticalStrut(50));
            sidebar.add(new JSeparator());
            sidebar.add(Box.createVerticalGlue());
            sidebar.add(quitBtn);

            mainPanel.add(sidebar, BorderLayout.EAST);

            // TIMER
            Timer countdown = new Timer(1000, e -> {
                timeLeft--;
                if (timeLeft <= 0) System.exit(0);
                timerLabel.setText(String.format("%02d:%02d",
                        timeLeft / 60, timeLeft % 60));
            });
            countdown.start();

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}