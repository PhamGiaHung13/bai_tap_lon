package minigames.maze.UI;

import Controller.GameController;
import minigames.MinigamePanel; // Import lớp cha mới
import minigames.maze.Logic.MazeGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;

public class MazePanel extends MinigamePanel { // Đổi thành extends MinigamePanel

    private MazeGame game;
    private int tileSize;

    private Image wallImg, playerImg, foodImg;
    private int timeLimit;
    private int timeLeft;
    private Timer timer;
    private Clip bgMusic;

    private JProgressBar timeBar;
    private JLabel timeLabel;
    private int currentDifficulty;

    public MazePanel(int difficulty, GameController controller) {
        super(controller); // Gọi constructor lớp cha để khởi tạo MouseListener chung
        this.game = new MazeGame(difficulty);
        this.currentDifficulty = difficulty;

        this.timeLimit = (difficulty == 1) ? 40 : (difficulty == 2 ? 30 : 20);
        this.timeLeft = timeLimit;

        setBackground(new Color(10, 10, 15));
        setFocusable(true);
        setLayout(null);

        loadImages();
        playBackgroundMusic("maze_bgm.wav");
        initUI();
        initKeyControl();
        initTimer();

        timer.start();
    }

    private void playBackgroundMusic(String fileName) {
        try {
            File musicFile = new File("src/minigames/maze/" + fileName);
            if (musicFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile.getAbsoluteFile());
                bgMusic = AudioSystem.getClip();
                bgMusic.open(audioIn);
                bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
                bgMusic.start();
            }
        } catch (Exception e) {
            System.err.println("Lỗi nhạc Maze: " + e.getMessage());
        }
    }

    // Logic dừng game: cập nhật trạng thái để lớp cha biết đường vẽ Overlay
    private void stopGameLogic(boolean win) {
        if (timer != null) timer.stop();
        this.isGameOver = true;
        this.isVictory = win;
        repaint();
    }

    // Thực hiện dọn dẹp và trả kết quả về Controller (Ghi đè hàm của lớp cha)
    @Override
    protected void handleExit() {
        if (timer != null) timer.stop();
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
        if (isVictory) controller.onMinigameWin();
        else controller.onMinigameLose();
    }

    private void loadImages() {
        String[] paths = {"wall.png", "bch.png", "powerfood.png"};
        Image[] imgs = new Image[3];
        for (int i = 0; i < paths.length; i++) {
            File f = new File("src/minigames/maze/images/" + paths[i]);
            if (f.exists()) {
                imgs[i] = new ImageIcon(f.getAbsolutePath()).getImage();
            }
        }
        wallImg = imgs[0]; playerImg = imgs[1]; foodImg = imgs[2];
    }

    private void initUI() {
        // --- NÂNG CẤP NÚT GIVE UP RÕ RÀNG HƠN ---
        JButton quitButton = new JButton("GIVE UP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth() - 5;
                int h = getHeight() - 5;

                // 1. Shadow (Đổ bóng nhòe nhẹ)
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(4, 4, w, h, 15, 15);

                // 2. Body Gradient (Màu chuyển từ đỏ tươi sang đỏ đậm tạo độ khối)
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 20, 20), 0, h, new Color(130, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 15, 15);

                // 3. Viền nổi (Highlight ở mép trên)
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, w, h, 15, 15);

                // 4. Vẽ Chữ (Sắc nét hơn)
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h + fm.getAscent()) / 2 - 2;

                // Đổ bóng chữ đen đặc
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), tx + 1, ty + 1);
                // Chữ trắng rực rỡ
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };

        // Điều chỉnh kích thước lớn hơn một chút để nhìn cho rõ
        quitButton.setBounds(30, 30, 140, 50);
        quitButton.setFocusable(false);
        quitButton.setBorderPainted(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Chữ to hơn chút

        quitButton.addActionListener(e -> {
            if (timer != null) timer.stop();

            this.isGameOver = true;
            this.isVictory = false;
            quitButton.setVisible(false);

            repaint();
        });
        add(quitButton);

        timeLabel = new JLabel(timeLeft + "s", SwingConstants.CENTER);
        timeLabel.setForeground(new Color(255, 255, 255, 200));
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        add(timeLabel);

        timeBar = new JProgressBar(SwingConstants.VERTICAL, 0, timeLimit);
        timeBar.setValue(timeLimit);
        timeBar.setBackground(new Color(20, 20, 30));
        timeBar.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20)));
        add(timeBar);
    }

    private void initKeyControl() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isGameOver) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> game.move(-1, 0);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> game.move(1, 0);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> game.move(0, -1);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> game.move(0, 1);
                }

                if (game.isWin()) {
                    stopGameLogic(true);
                }
                repaint();
            }
        });
    }

    private void initTimer() {
        timer = new Timer(1000, e -> {
            if (isVictory || isGameOver) {
                timer.stop();
                return;
            }

            timeLeft--;
            if (timeLeft <= 0) {
                timeLeft = 0;
                stopGameLogic(false);
            }
            updateTimeBar();
            repaint();
        });
    }

    private void updateTimeBar() {
        timeBar.setValue(timeLeft);
        timeLabel.setText(timeLeft + "s");
        double percent = (double) timeLeft / timeLimit;
        if (percent > 0.6) timeBar.setForeground(new Color(0, 200, 100));
        else if (percent > 0.3) timeBar.setForeground(new Color(255, 180, 0));
        else timeBar.setForeground(new Color(255, 50, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = game.getRows(), cols = game.getCols();
        int marginX = 120, marginY = 120;
        tileSize = Math.min((getWidth() - marginX * 2) / cols, (getHeight() - marginY) / rows);
        int mazeW = cols * tileSize, mazeH = rows * tileSize;
        int xOff = (getWidth() - mazeW) / 2;
        int yOff = (getHeight() - mazeH) / 2;
        int pR = game.getPlayerRow(), pC = game.getPlayerCol();

        // 1. Shadow (Giữ nguyên)
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(xOff + 10, yOff + 10, mazeW, mazeH, 15, 15);

        // 2. Vẽ Maze (Giữ nguyên)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = xOff + c * tileSize, y = yOff + r * tileSize;
                double dist = Math.sqrt(Math.pow(r - pR, 2) + Math.pow(c - pC, 2));
                float alpha = (float) Math.max(0.1, 1.0 - (dist / 7.0));

                if (game.getMaze()[r][c] == 1) {
                    g2.setColor(new Color(45, 50, 70));
                    g2.fillRoundRect(x + 1, y + 1, tileSize - 2, tileSize - 2, 4, 4);
                    if (wallImg != null) {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        g2.drawImage(wallImg, x, y, tileSize, tileSize, null);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    }
                } else {
                    g2.setColor(new Color(20, 20, 30));
                    g2.fillRect(x, y, tileSize, tileSize);
                    if (game.getFood()[r][c]) {
                        g2.setColor(new Color(255, 200, 0, (int)(alpha * 255)));
                        int fs = tileSize / 4;
                        g2.fillOval(x + (tileSize-fs)/2, y + (tileSize-fs)/2, fs, fs);
                    }
                }
            }
        }

        // 3. Exit (Giữ nguyên)
        int ex = xOff + game.getExitCol() * tileSize, ey = yOff + game.getExitRow() * tileSize;
        g2.setColor(new Color(0, 255, 100, 80));
        g2.fillOval(ex - 2, ey - 2, tileSize + 4, tileSize + 4);
        g2.setColor(new Color(0, 255, 100));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(ex + 4, ey + 4, tileSize - 8, tileSize - 8, 8, 8);

        // 4. Player (Giữ nguyên)
        if (playerImg != null) g2.drawImage(playerImg, xOff + pC * tileSize, yOff + pR * tileSize, tileSize, tileSize, null);

        // 5. Vignette Effect (Giữ nguyên)
        RadialGradientPaint vignette = new RadialGradientPaint(
                (xOff + pC * tileSize) + tileSize/2, (yOff + pR * tileSize) + tileSize/2, Math.max(getWidth(), getHeight()),
                new float[]{0f, 0.4f, 1f},
                new Color[]{new Color(0,0,0,0), new Color(0,0,0,80), new Color(10,10,20, 240)}
        );
        g2.setPaint(vignette);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 6. UI Position (Giữ nguyên)
        timeBar.setBounds(xOff + mazeW + 40, yOff, 15, mazeH);
        timeLabel.setBounds(xOff + mazeW + 10, yOff - 35, 80, 30);

        // 7. LEVEL DISPLAY (Giữ nguyên)
        g2.setFont(new Font("Consolas", Font.BOLD, 22));
        g2.setColor(new Color(0, 255, 255));
        g2.drawString("LEVEL: " + currentDifficulty, xOff, yOff + mazeH + 40);

        // 8. GỌI LỚP CHA VẼ OVERLAY CHUNG
        drawCommonOverlay(g2);
    }
}