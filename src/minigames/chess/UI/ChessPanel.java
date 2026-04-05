package minigames.chess.UI;

import Controller.GameController;
import minigames.MinigamePanel;
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
import javax.sound.sampled.*;

public class ChessPanel extends MinigamePanel {

    private ChessPuzzle puzzle;
    private int difficulty;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Map<String, Image> pieceImages = new HashMap<>();
    private Image boardImage;
    private String[][] board = new String[8][8];
    private Image backgroundImage;

    private int timeLeft = 180;
    private Timer countdown;
    private final int SIDE_WIDTH = 320;

    public ChessPanel(int difficulty, GameController controller) {
        super(controller);
        this.difficulty = difficulty;
        this.puzzle = new ChessPuzzle(difficulty);

        loadResources();
        resetPosition();
        startTimer();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOver) return;

                int usableWidth = getWidth() - SIDE_WIDTH - 60;
                int usableHeight = getHeight() - 80;
                int boardSize = (int) (Math.min(usableWidth, usableHeight) * 0.95);
                int xOffset = (usableWidth - boardSize) / 2 + 30;
                int yOffset = (getHeight() - boardSize) / 2;
                int cellSize = boardSize / 8;

                int btnW = 220, btnH = 60;
                int btnX = getWidth() - SIDE_WIDTH + (SIDE_WIDTH - btnW) / 2;
                int btnY = getHeight() - 120;

                // Nút GIVE UP
                if (e.getX() >= btnX && e.getX() <= btnX + btnW &&
                        e.getY() >= btnY && e.getY() <= btnY + btnH) {
                    if (!isGameOver) {
                        handleLose();
                    }
                    return;
                }

                int col = (e.getX() - xOffset) / cellSize;
                int row = (e.getY() - yOffset) / cellSize;

                if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                    selectedRow = -1; selectedCol = -1;
                    repaint(); return;
                }

                // Hủy chọn nếu nhấn lại ô cũ
                if (row == selectedRow && col == selectedCol) {
                    selectedRow = -1; selectedCol = -1;
                    repaint(); return;
                }

                if (selectedRow == -1) {
                    if (board[row][col] != null && board[row][col].startsWith("w")) {
                        selectedRow = row; selectedCol = col;
                    }
                } else {
                    String from = toChess(selectedRow, selectedCol);
                    String to = toChess(row, col);

                    if (puzzle.playerMove(from, to)) {
                        updateBoard(selectedRow, selectedCol, row, col);

                        // Phát âm thanh theo kịch bản từng bước
                        handlePuzzleSound(puzzle.getStep());

                        repaint();

                        if (puzzle.isBlackTurn()) {
                            Timer aiDelay = new Timer(600, ev -> {
                                String[] blackMove = puzzle.getBlackMove(); // Sau lệnh này step tăng (ví dụ từ 3 lên 4)
                                if (blackMove != null) {
                                    updateBoard(fromChess(blackMove[0])[0], fromChess(blackMove[0])[1],
                                            fromChess(blackMove[1])[0], fromChess(blackMove[1])[1]);

                                    int s = puzzle.getStep(); // Lấy step mới nhất (bây giờ là 2 hoặc 4)

                                    // KIỂM TRA RIÊNG CHO MÁY
                                    if (difficulty == 3 && s == 4) {
                                        playSound("Capture.wav"); // Địch ăn quân ở bước này!
                                    } else {
                                        playSound("Move.wav"); // Các bước khác máy chỉ di chuyển
                                    }

                                    if (puzzle.isSolved()) handleWin();
                                    repaint();
                                }
                            });
                            aiDelay.setRepeats(false); aiDelay.start();
                        } else if (puzzle.isSolved()) {
                            handleWin();
                        }
                    } else {
                        handleLose();
                    }
                    selectedRow = -1; selectedCol = -1;
                }
                repaint();
            }
        });
    }

    // Logic âm thanh cố định cho mỗi chế độ
    private void handlePuzzleSound(int step) {
        if (difficulty == 1) {
            // Dif 1: 1 bước duy nhất là chiếu (Check)
            playSound("Check.wav");
        } else if (difficulty == 2) {
            // Dif 2: Check (1) -> Move (2) -> Check (3)
            if (step == 1 || step == 3) playSound("Check.wav");
            else playSound("Move.wav");
        } else {
            if (step <= 3) playSound("Move.wav");
            else playSound("Check.wav");
        }
    }

    private void playSound(String fileName) {
        new Thread(() -> {
            try {
                // Tự động đổi sang .wav nếu code gọi .mp3
                String name = fileName.endsWith(".mp3") ? fileName.replace(".mp3", ".wav") : fileName;
                File soundFile = new File("src/minigames/chess/" + name);

                if (soundFile.exists()) {
                    AudioInputStream ai = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(ai);
                    clip.start();
                    // Giữ thread sống để clip kịp phát
                    Thread.sleep(clip.getMicrosecondLength() / 1000 + 100);
                } else {
                    System.err.println("File not found: " + soundFile.getAbsolutePath());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void handleExit() {
        if (isVictory) controller.onMinigameWin();
        else controller.onMinigameLose();
    }

    private void handleWin() {
        stopTimer();
        isGameOver = true;
        isVictory = true;
        playSound("Checkmate.wav");
        repaint();
    }

    private void handleLose() {
        stopTimer();
        isGameOver = true;
        isVictory = false;
        repaint();
    }

    private void updateBoard(int fR, int fC, int tR, int tC) {
        board[tR][tC] = board[fR][fC];
        board[fR][fC] = null;
    }

    private void startTimer() {
        if (countdown != null) countdown.stop();
        countdown = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 0) handleLose();
            repaint();
        });
        countdown.start();
    }

    public void stopTimer() { if (countdown != null) countdown.stop(); }

    private void loadResources() {
        try {
            backgroundImage = ImageIO.read(new File("src/minigames/chess/bg.png"));
            boardImage = ImageIO.read(new File("src/minigames/chess/board.png"));
            String[] pieces = {"wK","wQ","wR","wB","wN","wP","bK","bQ","bR","bB","bN","bP"};
            for (String p : pieces) pieceImages.put(p, ImageIO.read(new File("src/minigames/chess/" + p + ".png")));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void resetPosition() {
        puzzle.reset();
        for (int i=0; i<8; i++) for (int j=0; j<8; j++) board[i][j] = null;
        if (difficulty == 1) {
            board[0][0]="bK"; board[0][1]="bR"; board[0][7]="bR"; board[1][0]="bP"; board[1][4]="bB";
            board[1][5]="bQ"; board[2][2]="bP"; board[3][3]="wN"; board[6][2]="bP"; board[6][3]="wQ";
            board[6][5]="wB"; board[7][0]="wK"; board[7][1]="wR";
        } else if (difficulty == 2) {
            board[0][3]="bR"; board[1][3]="bB"; board[1][4]="bQ"; board[2][3]="bK"; board[2][2]="bP";
            board[2][5]="bB"; board[3][4]="bP"; board[2][1]="wQ"; board[4][2]="wP"; board[4][4]="wB";
            board[7][6]="wK"; board[6][5]="wP"; board[6][6]="wP";
        } else {
            board[3][5]="wK"; board[7][2]="wR"; board[6][6]="wP"; board[3][7]="bK"; board[0][3]="bB";
            board[2][7]="bP"; board[5][6]="bP";
        }
    }

    private String toChess(int r, int c) { return "" + (char)('a'+c) + (8-r); }
    private int[] fromChess(String s) { return new int[]{8-(s.charAt(1)-'0'), s.charAt(0)-'a'}; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(getWidth() - SIDE_WIDTH, 0, SIDE_WIDTH, getHeight());

        drawChessUI(g2);
        drawCommonOverlay(g2);
    }

    private void drawChessUI(Graphics2D g2) {
        int tx = getWidth() - SIDE_WIDTH + 30;
        g2.setColor(new Color(20, 20, 20));
        g2.fillRoundRect(tx, 40, 260, 110, 25, 25);
        g2.setColor(new Color(255, 50, 50));
        g2.setFont(new Font("Monospaced", Font.BOLD, 60));
        g2.drawString(String.format("%02d:%02d", timeLeft/60, timeLeft%60), tx + 40, 115);

        int bx = getWidth() - SIDE_WIDTH + 50;
        int by = getHeight() - 120;
        g2.setColor(new Color(100, 0, 0)); g2.fillRoundRect(bx, by + 4, 220, 60, 20, 20);
        g2.setColor(new Color(180, 0, 0)); g2.fillRoundRect(bx, by, 220, 60, 20, 20);
        g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("GIVE UP", bx + 60, by + 38);

        int boardSize = (int)(Math.min(getWidth() - SIDE_WIDTH - 60, getHeight() - 80) * 0.95);
        int xo = (getWidth() - SIDE_WIDTH - 60 - boardSize)/2 + 30;
        int yo = (getHeight() - boardSize)/2;
        int cs = boardSize / 8;

        if (boardImage != null) g2.drawImage(boardImage, xo, yo, boardSize, boardSize, null);

        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                if (r == selectedRow && c == selectedCol) {
                    g2.setColor(new Color(186, 202, 68, 180));
                    g2.fillRect(xo + c*cs, yo + r*cs, cs, cs);
                }
                String p = board[r][c];
                if (p != null) {
                    int ps = (int)(cs * 0.85);
                    g2.drawImage(pieceImages.get(p), xo + c*cs + (cs-ps)/2, yo + r*cs + (cs-ps)/2, ps, ps, null);
                }
            }
        }
    }
}