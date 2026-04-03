package minigames.chess.UI;

import Controller.GameController;
import minigames.chess.Logic.ChessPuzzle;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChessPanel extends JPanel {

    private ChessPuzzle puzzle;
    private int difficulty;
    private GameController controller;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Map<String, Image> pieceImages = new HashMap<>();
    private Image boardImage;
    private String[][] board = new String[8][8];
    private Image backgroundImage;

    // Quản lý Timer trực tiếp tại đây
    private int timeLeft = 300;
    private Timer countdown;

    public ChessPanel(int difficulty, GameController controller) {
        this.difficulty = difficulty;
        this.controller = controller;
        this.puzzle = new ChessPuzzle(difficulty);

        loadResources();
        resetPosition();
        startTimer();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // CHÚ Ý: Trừ đi 300px Sidebar bên phải khi tính toán vùng click bàn cờ
                int sideWidth = 300;
                int usableWidth = getWidth() - sideWidth;
                int boardSize = Math.min(usableWidth, getHeight());
                int xOffset = (usableWidth - boardSize) / 2;
                int yOffset = (getHeight() - boardSize) / 2;
                int dynamicCellSize = boardSize / 8;

                // Kiểm tra nếu click vào vùng nút "GIVE UP" ở Sidebar
                if (e.getX() > getWidth() - 250 && e.getX() < getWidth() - 50 &&
                        e.getY() > getHeight() - 100 && e.getY() < getHeight() - 50) {
                    stopTimer();
                    controller.onMinigameLose();
                    return;
                }

                int col = (e.getX() - xOffset) / dynamicCellSize;
                int row = (e.getY() - yOffset) / dynamicCellSize;

                if (row < 0 || row >= 8 || col < 0 || col >= 8) return;

                if (selectedRow == -1) {
                    if (board[row][col] != null && board[row][col].startsWith("w")) {
                        selectedRow = row;
                        selectedCol = col;
                    }
                } else {
                    String from = toChess(selectedRow, selectedCol);
                    String to = toChess(row, col);

                    if (puzzle.playerMove(from, to)) {
                        board[row][col] = board[selectedRow][selectedCol];
                        board[selectedRow][selectedCol] = null;

                        if (puzzle.isBlackTurn()) {
                            String[] blackMove = puzzle.getBlackMove();
                            int[] f = fromChess(blackMove[0]);
                            int[] t = fromChess(blackMove[1]);
                            board[t[0]][t[1]] = board[f[0]][f[1]];
                            board[f[0]][f[1]] = null;
                        }

                        if (puzzle.isSolved()) {
                            stopTimer();
                            repaint();
                            controller.onMinigameWin();
                        }
                    } else {
                        stopTimer();
                        controller.onMinigameLose();
                    }
                    selectedRow = -1;
                    selectedCol = -1;
                }
                repaint();
            }
        });
    }

    private void startTimer() {
        if (countdown != null) countdown.stop();
        countdown = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 0) {
                stopTimer();
                controller.onMinigameLose();
            }
            repaint();
        });
        countdown.start();
    }

    public void stopTimer() {
        if (countdown != null) countdown.stop();
    }

    private void loadResources() {
        try {
            backgroundImage = ImageIO.read(new File("src/minigames/chess/bg.png"));
            boardImage = ImageIO.read(new File("src/minigames/chess/board.png"));
            String[] types = {"wK", "wQ", "wR", "wB", "wN", "wP", "bK", "bQ", "bR", "bB", "bN", "bP"};
            for (String type : types) {
                pieceImages.put(type, ImageIO.read(new File("src/minigames/chess/" + type + ".png")));
            }
        } catch (IOException e) {
            System.err.println("Lỗi nạp ảnh Chess: " + e.getMessage());
        }
    }

    private void resetPosition() {
        puzzle.reset();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) board[i][j] = null;
        }

        if (difficulty == 1) {
            board[0][0] = "bK"; board[0][1] = "bR"; board[0][7] = "bR";
            board[1][0] = "bP"; board[1][4] = "bB"; board[1][5] = "bQ";
            board[2][2] = "bP"; board[3][3] = "wN"; board[6][2] = "bP";
            board[6][3] = "wQ"; board[6][5] = "wB"; board[7][0] = "wK"; board[7][1] = "wR";
        } else if (difficulty == 2) {
            board[3][2] = "wR"; board[5][3] = "wK"; board[7][4] = "bK";
        } else {
            board[3][5] = "wN"; board[3][4] = "wQ"; board[1][3] = "wP";
            board[0][1] = "bK"; board[0][6] = "bR";
        }
    }

    private String toChess(int row, int col) { return "" + (char) ('a' + col) + (8 - row); }
    private int[] fromChess(String square) {
        int col = square.charAt(0) - 'a';
        int row = 8 - (square.charAt(1) - '0');
        return new int[]{row, col};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Vẽ Background toàn màn hình
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // 2. Vẽ Sidebar (Vùng đen bên phải - 300px)
        int sideWidth = 300;
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(getWidth() - sideWidth, 0, sideWidth, getHeight());

        // Vẽ đồng hồ Timer lên Sidebar
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 55));
        String timeStr = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
        g2d.drawString(timeStr, getWidth() - 260, 120);

        // Vẽ nút "GIVE UP" giả lập (Để tránh lỗi focus nút bấm)
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillRoundRect(getWidth() - 250, getHeight() - 100, 200, 50, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("GIVE UP", getWidth() - 195, getHeight() - 67);

        // 3. Tính toán vùng vẽ bàn cờ (Trừ đi Sidebar)
        int usableWidth = getWidth() - sideWidth;
        int boardSize = Math.min(usableWidth, getHeight());
        int xOffset = (usableWidth - boardSize) / 2;
        int yOffset = (getHeight() - boardSize) / 2;
        int dynamicCellSize = boardSize / 8;

        if (boardImage != null) {
            g2d.drawImage(boardImage, xOffset, yOffset, boardSize, boardSize, null);
        }

        // 4. Vẽ quân cờ
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int x = xOffset + c * dynamicCellSize;
                int y = yOffset + r * dynamicCellSize;

                if (r == selectedRow && c == selectedCol) {
                    g2d.setColor(new Color(186, 202, 68, 180));
                    g2d.fillRect(x, y, dynamicCellSize, dynamicCellSize);
                }

                String pieceType = board[r][c];
                if (pieceType != null && pieceImages.containsKey(pieceType)) {
                    g2d.drawImage(pieceImages.get(pieceType), x + 5, y + 5,
                            dynamicCellSize - 10, dynamicCellSize - 10, null);
                }
            }
        }
    }
}