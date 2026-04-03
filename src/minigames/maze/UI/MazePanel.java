package minigames.maze.UI;

import Controller.GameController;
import minigames.maze.Logic.MazeGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class MazePanel extends JPanel {

    private MazeGame game;
    private GameController controller;
    private int tileSize;

    private Image wallImg, playerImg, foodImg;

    private int timeLimit;
    private int timeLeft;
    private Timer timer;

    private JProgressBar timeBar;
    private JLabel timeLabel;

    public MazePanel(int difficulty, GameController controller) {
        this.game = new MazeGame(difficulty);
        this.controller = controller;

        // Thiết lập thời gian theo độ khó: 1->40s, 2->30s, 3->20s
        this.timeLimit = (difficulty == 1) ? 40 : (difficulty == 2 ? 30 : 20);
        this.timeLeft = timeLimit;

        setBackground(new Color(10, 10, 15)); // Màu nền tối sâu
        setFocusable(true);
        setLayout(null); // Dùng null để tự do đặt vị trí Progress Bar theo tọa độ Maze

        loadImages();
        initUI();
        initKeyControl();
        initTimer();

        timer.start();
    }

    private void loadImages() {
        // Nạp ảnh an toàn: Thử từ file ngoài trước, sau đó tới resource
        String[] paths = {"wall.png", "bch.png", "powerfood.png"};
        Image[] imgs = new Image[3];

        for (int i = 0; i < paths.length; i++) {
            File f = new File("src/minigames/maze/Images/" + paths[i]);
            if (f.exists()) {
                imgs[i] = new ImageIcon(f.getAbsolutePath()).getImage();
            } else {
                java.net.URL url = getClass().getResource("/minigames/maze/Images/" + paths[i]);
                if (url != null) imgs[i] = new ImageIcon(url).getImage();
            }
        }
        wallImg = imgs[0]; playerImg = imgs[1]; foodImg = imgs[2];
    }

    private void initUI() {
        // Nút Quit tích hợp về Controller
        JButton quitButton = new JButton("Quit");
        quitButton.setBounds(30, 30, 100, 40);
        quitButton.setFocusable(false);
        quitButton.setBackground(new Color(220, 53, 69));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.addActionListener(e -> controller.onMinigameLose());
        add(quitButton);

        timeLabel = new JLabel(timeLeft + "s", SwingConstants.CENTER);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Consolas", Font.BOLD, 22));
        add(timeLabel);

        timeBar = new JProgressBar(SwingConstants.VERTICAL, 0, timeLimit);
        timeBar.setValue(timeLimit);
        timeBar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(timeBar);
    }

    private void initKeyControl() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> game.move(-1, 0);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> game.move(1, 0);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> game.move(0, -1);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> game.move(0, 1);
                }
                if (game.isWin()) {
                    timer.stop();
                    controller.onMinigameWin();
                }
                repaint();
            }
        });
    }

    private void initTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 0) {
                timer.stop();
                controller.onMinigameLose();
            }
            updateTimeBar();
            repaint();
        });
    }

    private void updateTimeBar() {
        timeBar.setValue(timeLeft);
        timeLabel.setText(timeLeft + "s");
        double percent = (double) timeLeft / timeLimit;
        if (percent > 0.6) timeBar.setForeground(new Color(40, 167, 69));
        else if (percent > 0.3) timeBar.setForeground(new Color(255, 193, 7));
        else timeBar.setForeground(new Color(220, 53, 69));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = game.getRows(), cols = game.getCols();
        int panelWidth = getWidth(), panelHeight = getHeight();

        // Tính toán kích thước ô dựa trên Frame thực tế
        int margin = 100;
        tileSize = Math.min((panelWidth - margin * 2) / cols, (panelHeight - margin) / rows);

        int mazeWidth = cols * tileSize, mazeHeight = rows * tileSize;
        int xOffset = (panelWidth - mazeWidth) / 2;
        int yOffset = (panelHeight - mazeHeight) / 2 + 30;

        int[][] mazeData = game.getMaze();
        boolean[][] foodData = game.getFood();

        // Vẽ Maze
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = xOffset + c * tileSize;
                int y = yOffset + r * tileSize;

                if (mazeData[r][c] == 1) {
                    if (wallImg != null) g2.drawImage(wallImg, x, y, tileSize, tileSize, null);
                    else { g2.setColor(Color.DARK_GRAY); g2.fillRect(x, y, tileSize, tileSize); }
                } else {
                    g2.setColor(new Color(20, 20, 25));
                    g2.fillRect(x, y, tileSize, tileSize);
                    if (foodData[r][c]) {
                        int fSize = tileSize / 3;
                        if (foodImg != null) g2.drawImage(foodImg, x + fSize, y + fSize, fSize, fSize, null);
                        else { g2.setColor(Color.YELLOW); g2.fillOval(x + fSize, y + fSize, fSize, fSize); }
                    }
                }
            }
        }

        // Vẽ Đích (Exit)
        g2.setColor(new Color(40, 167, 69, 180));
        g2.fillRoundRect(xOffset + game.getExitCol() * tileSize + 2, yOffset + game.getExitRow() * tileSize + 2,
                tileSize - 4, tileSize - 4, 10, 10);

        // Vẽ Player
        if (playerImg != null) {
            g2.drawImage(playerImg, xOffset + game.getPlayerCol() * tileSize, yOffset + game.getPlayerRow() * tileSize,
                    tileSize, tileSize, null);
        }

        // Cập nhật vị trí UI Components theo Maze thực tế
        timeBar.setBounds(xOffset + mazeWidth + 30, yOffset, 30, mazeHeight);
        timeLabel.setBounds(xOffset + mazeWidth + 10, yOffset - 40, 70, 30);
    }
}