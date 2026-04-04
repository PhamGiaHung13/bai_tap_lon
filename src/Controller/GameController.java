package Controller;

import core.UI.GameFrame;
import core.UI.GamePanel;
import core.Logic.Tile;
import minigames.sudoku.UI.SudokuPanel;
import minigames.maze.UI.MazePanel;
import minigames.blockblast.UI.BlockBlastPanel;
import minigames.chess.UI.ChessPanel;
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



public class GameController {
    private GameFrame frame;
    private GamePanel minesPanel;
    private Tile currentBombTile;
    private Random rand = new Random();

    // Lấy ra mainPanel và CardLayout từ Frame để điều khiển
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public GameController(GameFrame frame, GamePanel minesPanel) {
        this.frame = frame;
        this.minesPanel = minesPanel;
        this.minesPanel.setController(this);

        // TRÍCH XUẤT mainPanel TỪ FRAME (Vì ông đặt nó làm ContentPane trong GameFrame)
        this.mainPanel = (JPanel) frame.getContentPane();
        this.cardLayout = (CardLayout) mainPanel.getLayout();
    }

    public void startSecondChance(Tile bombTile, int difficulty) {
        this.currentBombTile = bombTile;
        JPanel gameUI = null;

        // Random từ 0 đến 3
        int luckyNumber = rand.nextInt(4);

        try {
            switch (luckyNumber) {
                case 0:
                    gameUI = new SudokuPanel(difficulty, this);
                    break;
                case 1:
                    gameUI = new MazePanel(difficulty, this);
                    break;
                case 2:
                    // CHÚ Ý: Đảm bảo đúng tên Class và tham số
                    gameUI = new BlockBlastPanel(difficulty, this);
                    break;
                case 3:
                    gameUI = new ChessPanel(difficulty, this);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Minigame: " + e.getMessage());
            // Nếu lỗi game mới, ép quay về Sudoku cho an toàn
            gameUI = new SudokuPanel(difficulty, this);
        }

        if (gameUI != null) {
            mainPanel.add(gameUI, "MINIGAME");
            cardLayout.show(mainPanel, "MINIGAME");

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setResizable(true);
            mainPanel.revalidate();
            mainPanel.repaint();

            // CỰC KỲ QUAN TRỌNG: BlockBlast và Maze cần cái này để nhận phím
            gameUI.requestFocusInWindow();
        }
    }

    public void onMinigameWin() {
        if (currentBombTile != null) {
            currentBombTile.setRevealed(true);
        }

        backToMines();

        showResultDialog(true);
    }


    public void onMinigameLose() {
        minesPanel.revealAllMines();
        backToMines();

        showResultDialog(false);

    }

    private void returnToMainMenu() {
        // QUAY VỀ CARD "MENU" CÓ SẴN TRONG FRAME
        cardLayout.show(mainPanel, "MENU");

        frame.setExtendedState(JFrame.NORMAL);
        frame.pack(); // Tự về 1200x700 như ông set ở MenuPanel
        frame.setLocationRelativeTo(null);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void backToMines() {
        // QUAY VỀ CARD "GAME" CÓ SẴN TRONG FRAME
        cardLayout.show(mainPanel, "GAME");

        // Khôi phục kích thước bàn cờ mìn
        frame.setExtendedState(JFrame.NORMAL);
        int tileSize = 40;
        int width = minesPanel.getBoard().columns * tileSize + 120;
        int height = minesPanel.getBoard().rows * tileSize + 220;

        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        mainPanel.revalidate();
        mainPanel.repaint();
        minesPanel.updateUIBoard();
        minesPanel.requestFocusInWindow();
    }

    // ----- LAM TI UI
    private void showResultDialog(boolean isWin) {
        JDialog dialog = new JDialog(frame, isWin ? "VICTORY" : "GAME OVER", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(frame);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Màu nền: Vàng nhạt nếu thắng, Đỏ nhạt nếu thua
                g2.setColor(isWin ? new Color(255, 255, 255, 240) : new Color(255, 230, 230, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);

            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        // Tiêu đề
        JLabel lblTitle = new JLabel(isWin ? "REMOVED MINE SUCCESSFUL" : "BOOM! YOU'RE DIED", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(isWin ? new Color(180, 160, 0) : new Color(180, 0, 0));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(40, 10, 10, 10));

        // Nội dung thông báo
        JLabel lblMsg = new JLabel(isWin ? "YOU GOT REVIVED" : "NO MORE CHANCE.", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblMsg.setForeground(new Color(80, 80, 80));

        // Panel chứa nút bấm (Dùng lại style nút của ông)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        btnPanel.setOpaque(false);

        if (isWin) {
            JButton resumeBtn = createStyledButton("KEEP PLAYING");
            resumeBtn.addActionListener(e -> dialog.dispose());
            btnPanel.add(resumeBtn);
        } else {
            JButton retryBtn = createStyledButton("RESTART");
            JButton menuBtn = createStyledButton("MAIN MENU");

            retryBtn.addActionListener(e -> {
                dialog.dispose();
                minesPanel.restartGame();
            });

            menuBtn.addActionListener(e -> {
                dialog.dispose();
                returnToMainMenu();
            });

            btnPanel.add(retryBtn);
            btnPanel.add(menuBtn);
        }

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(lblMsg, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    // Hàm hỗ trợ tạo nút bấm giống GamePanel của ông
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setForeground(new Color(60, 60, 60));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(220, 208, 48));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 26));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(60, 60, 60));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
            }
        });
        return btn;
    }
}