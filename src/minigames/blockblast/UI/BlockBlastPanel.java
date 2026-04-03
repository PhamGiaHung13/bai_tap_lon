package minigames.blockblast.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import Controller.GameController;
import minigames.blockblast.Logic.Blockblast;

public class BlockBlastPanel extends JPanel implements ActionListener {

    private Blockblast game;
    private Timer timer;
    private final int blockSize = 30;
    private GameController controller; // Kết nối với hệ thống Minesweeper

    // Màu sắc giao diện
    private final Color bgColor = new Color(190, 240, 190);
    private final Color boardBg = new Color(220, 245, 220);
    private final Color borderColor = Color.BLACK;
    private final Color[] blockColors = {
            new Color(255, 140, 150), new Color(255, 200, 140),
            new Color(255, 240, 140), new Color(140, 240, 180),
            new Color(140, 200, 255), new Color(200, 160, 255)
    };

    private Random rand = new Random();

    // ===== CONSTRUCTOR 1: DÙNG TRONG GAME CHÍNH (HỒI SINH) =====
    public BlockBlastPanel(int difficulty, GameController controller) {
        this.controller = controller;
        // Độ khó: Tăng điểm yêu cầu và giảm thời gian dựa trên difficulty
        int target = 100 + (difficulty * 50);
        long timeLimit = 120000 - (difficulty * 10000L); // Cấp càng cao càng ít thời gian
        initGame(timeLimit, target);
    }

    // ===== CONSTRUCTOR 2: DÙNG ĐỂ CHẠY THỬ (MAIN) =====
    public BlockBlastPanel() {
        this.controller = null;
        initGame(300000, 300); // Mặc định 5 phút, 300 điểm
    }

    /**
     * Khởi tạo các thành phần dùng chung
     */
    private void initGame(long timeLimit, int target) {
        game = new Blockblast(timeLimit, target);

        // Timer rơi gạch (0.5 giây/bước)
        timer = new Timer(500, this);
        timer.start();

        setBackground(bgColor);
        setFocusable(true);
        setLayout(null); // Layout tự do để đặt nút Exit

        // Nút Thoát/Bỏ cuộc
        JButton exitBtn = new JButton("Give Up");
        exitBtn.setBounds(20, 20, 120, 40);
        exitBtn.setBackground(new Color(255, 150, 150));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitBtn.setFocusable(false); // Cực kỳ quan trọng để không bị mất Focus bàn phím

        exitBtn.addActionListener(e -> {
            timer.stop();
            if (controller != null) {
                controller.onMinigameLose();
            } else {
                System.exit(0);
            }
        });
        add(exitBtn);

        // Xử lý phím điều khiển
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (game.isGameOver() || game.isWin()) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  game.moveLeft();  break;
                    case KeyEvent.VK_RIGHT: game.moveRight(); break;
                    case KeyEvent.VK_DOWN:  game.moveDown();  break;
                    case KeyEvent.VK_UP:    game.rotate();    break;
                }
                repaint();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        game.tick(); // Cập nhật logic rơi gạch

        // Nếu đang trong chế độ hồi sinh, kiểm tra thắng thua để báo về Controller
        if (controller != null) {
            if (game.isWin()) {
                timer.stop();
                controller.onMinigameWin();
            } else if (game.isGameOver()) {
                timer.stop();
                controller.onMinigameLose();
            }
        }
        repaint();
    }

    /**
     * Vẽ khối gạch hiệu ứng 3D
     */
    private void drawBlock3D(Graphics2D g2d, int x, int y, Color baseColor) {
        int arc = 12;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Đổ màu Gradient
        GradientPaint gp = new GradientPaint(x, y, baseColor.brighter(), x, y + blockSize, baseColor.darker());
        g2d.setPaint(gp);
        g2d.fillRoundRect(x, y, blockSize, blockSize, arc, arc);

        // Ánh sáng trắng phía trên
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillRoundRect(x + 4, y + 4, blockSize - 8, blockSize / 3, arc, arc);

        // Viền đậm
        g2d.setColor(baseColor.darker().darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, blockSize, blockSize, arc, arc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int boardWidth = game.cols * blockSize;
        int boardHeight = game.rows * blockSize;
        int offsetX = (getWidth() - boardWidth) / 2;
        int offsetY = (getHeight() - boardHeight) / 2;

        // Vẽ nền bảng chơi
        g2d.setColor(boardBg);
        g2d.fillRect(offsetX, offsetY, boardWidth, boardHeight);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(offsetX, offsetY, boardWidth, boardHeight);

        // Vẽ lưới ô vuông
        g2d.setColor(new Color(0, 0, 0, 30));
        for (int i = 0; i <= game.rows; i++)
            g2d.drawLine(offsetX, offsetY + i * blockSize, offsetX + boardWidth, offsetY + i * blockSize);
        for (int j = 0; j <= game.cols; j++)
            g2d.drawLine(offsetX + j * blockSize, offsetY, offsetX + j * blockSize, offsetY + boardHeight);

        // Vẽ các khối đã nằm trên bàn
        int[][] board = game.getBoard();
        for (int i = 0; i < game.rows; i++) {
            for (int j = 0; j < game.cols; j++) {
                if (board[i][j] != 0) {
                    Color c = blockColors[board[i][j] % blockColors.length];
                    drawBlock3D(g2d, offsetX + j * blockSize, offsetY + i * blockSize, c);
                }
            }
        }

        // Vẽ khối đang rơi (màu Cam đặc trưng)
        int[][] piece = game.getPiece();
        if (piece != null) {
            for (int i = 0; i < piece.length; i++) {
                for (int j = 0; j < piece[i].length; j++) {
                    if (piece[i][j] != 0) {
                        drawBlock3D(g2d, offsetX + (game.getCol() + j) * blockSize,
                                offsetY + (game.getRow() + i) * blockSize, Color.ORANGE);
                    }
                }
            }
        }

        // Thông tin điểm và thời gian
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + game.getScore(), offsetX, offsetY - 10);
        g2d.drawString("Time: " + game.getTimeLeft() / 1000 + "s", offsetX + 150, offsetY - 10);

        // Hiển thị trạng thái kết thúc (chỉ khi chạy test độc lập)
        if (controller == null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            if (game.isGameOver()) {
                g2d.setColor(Color.RED);
                g2d.drawString("GAME OVER", getWidth()/2 - 120, getHeight()/2);
            }
            if (game.isWin()) {
                g2d.setColor(new Color(0, 150, 0));
                g2d.drawString("YOU WIN!", getWidth()/2 - 90, getHeight()/2);
            }
        }
    }

    // Hàm Main để chạy thử Panel này
    public static void main(String[] args) {
        JFrame frame = new JFrame("Block Blast Prototype");
        BlockBlastPanel panel = new BlockBlastPanel();
        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Bắt buộc gọi sau khi setVisible để nhận sự kiện bàn phím
        panel.requestFocusInWindow();
    }
}