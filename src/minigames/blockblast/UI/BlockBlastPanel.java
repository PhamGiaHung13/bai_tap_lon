package minigames.blockblast.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.net.URL;
import Controller.GameController;
import minigames.MinigamePanel;
import minigames.blockblast.Logic.Blockblast;

public class BlockBlastPanel extends MinigamePanel {

    private Blockblast game;
    private Timer logicTimer;
    private Timer renderTimer;
    private final int blockSize = 32;
    private Image backgroundImage;
    private Clip bgMusic;

    private final Color boardBg = new Color(20, 20, 30, 180);
    private final Color gridColor = new Color(255, 255, 255, 15);
    private final Font hudFont = new Font("Consolas", Font.BOLD, 18);

    private float hue = 0.0f;
    private int currentDifficulty;

    public BlockBlastPanel(int difficulty, GameController controller) {
        super(controller);
        this.currentDifficulty = difficulty;

        int target = 500 + ((difficulty - 1) * 250);
        long timeLimit = 250000 - ((difficulty - 1) * 60000L);

        loadResources();
        playBackgroundMusic("src/minigames/blockblast/soundtrack.wav");
        initGame(timeLimit, target, difficulty);
    }

    @Override
    protected void handleExit() {
        stopTimers();
        stopMusic();
        if (isVictory) controller.onMinigameWin();
        else controller.onMinigameLose();
    }

    private void loadResources() {
        try {
            backgroundImage = new ImageIcon("src/minigames/blockblast/bg.png").getImage();
        } catch (Exception e) {
            System.out.println("Không tìm thấy bg.png");
        }
    }

    private void playBackgroundMusic(String fileName) {
        try {

            java.io.File soundFile = new java.io.File(fileName);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                bgMusic = AudioSystem.getClip();
                bgMusic.open(audioIn);
                bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
                bgMusic.start();
            } else {
                System.err.println("Không tìm thấy file nhạc tại: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("Lỗi load nhạc: " + e.getMessage());
            e.printStackTrace(); // In ra lỗi chi tiết để debug
        }
    }

    private void stopMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }

    private void stopTimers() {
        if (logicTimer != null) logicTimer.stop();
        if (renderTimer != null) renderTimer.stop();
    }

    private void initGame(long timeLimit, int target, int difficulty) {
        game = new Blockblast(timeLimit, target);
        int fallSpeed = 550 - ((difficulty - 1) * 150);
        if (fallSpeed < 180) fallSpeed = 180;

        logicTimer = new Timer(fallSpeed, e -> {
            game.tick();
            checkGameStatus();
        });
        logicTimer.start();

        renderTimer = new Timer(30, e -> {
            hue += 0.008f;
            repaint();
        });
        renderTimer.start();

        // 1. ÉP LAYOUT VỀ NULL để nút nằm đúng tọa độ setBounds
        setLayout(null);

        // 2. ĐỊNH NGHĨA NÚT GIVE UP (CUSTOM UI)
        JButton giveUpBtn = new JButton("GIVE UP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 15; // Độ bo góc

                // Vẽ bóng đổ (Shadow)
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(5, 5, w - 10, h - 10, arc, arc);

                // Vẽ thân nút (Gradient Đỏ)
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 50, 50), 0, h, new Color(150, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w - 10, h - 10, arc, arc);

                // Vẽ viền highlight cho nổi khối
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, w - 10, h - 10, arc, arc);

                // Vẽ chữ GIVE UP (Căn giữa nút)
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int textX = (w - 10 - fm.stringWidth(text)) / 2;
                int textY = (h - 10 + fm.getAscent()) / 2 - 2;

                // Đổ bóng chữ cho dễ đọc
                g2.setColor(Color.BLACK);
                g2.drawString(text, textX + 1, textY + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(text, textX, textY);

                g2.dispose();
            }
        };

        // 3. THIẾT LẬP VỊ TRÍ & KÍCH THƯỚC (Góc trái, To bự)
        giveUpBtn.setBounds(20, 20, 140, 50); // Tọa độ (20,20), Rộng 160, Cao 55
        giveUpBtn.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Font chữ to (size 20)
        giveUpBtn.setFocusable(false);
        giveUpBtn.setContentAreaFilled(false);
        giveUpBtn.setBorderPainted(false);

        // 4. XỬ LÝ SỰ KIỆN KHI BẤM NÚT
        giveUpBtn.addActionListener(e -> {
            stopTimers();
            stopMusic();
            if (controller != null) {
                controller.onMinigameLose();
            } else {
                System.exit(0);
            }
        });
        add(giveUpBtn);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (game.isGameOver() || game.isWin()) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> game.moveLeft();
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> game.moveRight();
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> game.moveDown();
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> game.rotate();
                }
                repaint();
            }
        });
    }

    private void drawBlock(Graphics2D g2d, int x, int y, Color c, boolean isGhost) {
        int arc = 8;
        if (isGhost) {
            g2d.setColor(new Color(255, 255, 255, 35));
            g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
            g2d.drawRoundRect(x + 3, y + 3, blockSize - 6, blockSize - 6, arc, arc);
            return;
        }
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(x + 2, y + 2, blockSize - 2, blockSize - 2, arc, arc);
        GradientPaint gp = new GradientPaint(x, y, c, x + blockSize, y + blockSize, c.darker());
        g2d.setPaint(gp);
        g2d.fillRoundRect(x, y, blockSize - 2, blockSize - 2, arc, arc);
        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.fillRoundRect(x + 4, y + 4, blockSize - 10, blockSize / 4, arc, arc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        else { g2d.setColor(new Color(10, 10, 15)); g2d.fillRect(0, 0, getWidth(), getHeight()); }

        int boardW = game.cols * blockSize, boardH = game.rows * blockSize;
        int xOff = (getWidth() - boardW) / 2, yOff = (getHeight() - boardH) / 2;

        drawTextWithShadow(g2d, "SCORE: " + game.getScore() + " / " + game.targetScore, xOff, yOff - 15, Color.WHITE);
        String timeStr = "TIME: " + (game.getTimeLeft() / 1000) + "s";
        int timeW = g2d.getFontMetrics(hudFont).stringWidth(timeStr);
        drawTextWithShadow(g2d, timeStr, xOff + boardW - timeW, yOff - 15, (game.getTimeLeft() < 30000) ? Color.RED : Color.YELLOW);
        drawTextWithShadow(g2d, "LEVEL: " + currentDifficulty, xOff, yOff + boardH + 25, Color.CYAN);

        g2d.setColor(boardBg);
        g2d.fillRoundRect(xOff, yOff, boardW, boardH, 15, 15);
        g2d.setColor(gridColor);
        for (int i = 0; i <= game.rows; i++) g2d.drawLine(xOff, yOff + i * blockSize, xOff + boardW, yOff + i * blockSize);
        for (int j = 0; j <= game.cols; j++) g2d.drawLine(xOff + j * blockSize, yOff, xOff + j * blockSize, yOff + boardH);

        int[][] board = game.getBoard();
        Color staticColor = new Color(60, 100, 200);
        for (int r = 0; r < game.rows; r++) {
            for (int c = 0; c < game.cols; c++) if (board[r][c] != 0) drawBlock(g2d, xOff + c * blockSize, yOff + r * blockSize, staticColor, false);
        }

        int[][] piece = game.getPiece();
        if (piece != null) {
            Color rainbow = Color.getHSBColor(hue, 0.75f, 0.9f);
            int ghostR = findGhostRow();
            for (int r = 0; r < piece.length; r++) {
                for (int c = 0; c < piece[r].length; c++) {
                    if (piece[r][c] != 0) {
                        int drawX = xOff + (game.getCol() + c) * blockSize;
                        drawBlock(g2d, drawX, yOff + (ghostR + r) * blockSize, null, true);
                        drawBlock(g2d, drawX, yOff + (game.getRow() + r) * blockSize, rainbow, false);
                    }
                }
            }
        }
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(xOff, yOff, boardW, boardH, 15, 15);

        drawCommonOverlay(g2d);
    }

    private void drawTextWithShadow(Graphics2D g2d, String text, int x, int y, Color mainColor) {
        g2d.setFont(hudFont);
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(text, x + 2, y + 2);
        g2d.setColor(mainColor);
        g2d.drawString(text, x, y);
    }

    private int findGhostRow() {
        int r = game.getRow();
        while (r + 1 < game.rows && !collisionAt(r + 1, game.getCol())) r++;
        return r;
    }

    private boolean collisionAt(int nextR, int nextC) {
        int[][] p = game.getPiece();
        int[][] b = game.getBoard();
        if (p == null) return true;
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p[i].length; j++) {
                if (p[i][j] != 0) {
                    int nr = nextR + i, nc = nextC + j;
                    if (nr >= game.rows || nc < 0 || nc >= game.cols) return true;
                    if (nr >= 0 && b[nr][nc] != 0) return true;
                }
            }
        }
        return false;
    }

    private void checkGameStatus() {
        if (game.isGameOver() || game.isWin()) {
            this.isGameOver = true;
            this.isVictory = game.isWin();
            stopTimers();
            repaint();
        }
    }
}