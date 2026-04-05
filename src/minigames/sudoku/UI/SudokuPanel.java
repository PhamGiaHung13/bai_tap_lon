package minigames.sudoku.UI;

import Controller.GameController;
import minigames.MinigamePanel;
import minigames.sudoku.Logic.SudokuGame;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SudokuPanel extends MinigamePanel {
    private SudokuGame game;
    private JLabel[][] cellLabels;
    private JLabel errorLabel;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Image bg;
    private Clip bgMusic;

    // Màu sắc giữ nguyên
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_FIXED_BG = new Color(157, 189, 220);
    private final Color COLOR_GRID_LINE = new Color(44, 62, 80);
    private final Color COLOR_SELECTED = new Color(180, 210, 255);
    private final Color COLOR_HIGHLIGHT = new Color(235, 245, 255);
    private final Color COLOR_ERROR_BG = new Color(255, 210, 210);
    private final Color COLOR_TEXT_USER = new Color(0, 102, 204);
    private final Color COLOR_TEXT_ERROR = new Color(220, 53, 69);

    public SudokuPanel(int difficulty, GameController controller) {
        super(controller);
        this.game = new SudokuGame(difficulty);

        // Giữ nguyên phần nạp ảnh cũ của ông
        try {
            java.net.URL imgUrl = getClass().getResource("/minigames/sudoku/bg1.jpg");
            if (imgUrl != null) bg = new ImageIcon(imgUrl).getImage();
        } catch (Exception e) {}

        // ĐÃ BỎ PHẦN PHÁT NHẠC NỀN MBG Ở ĐÂY

        setLayout(new BorderLayout(0, 5));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        initHeader();
        initCenter();
        initBottom();
        refreshUI();
    }

    // --- HÀM XỬ LÝ ÂM THANH: CHỈ SỬA ĐỂ TÌM FILE TRONG CÙNG PACKAGE ---
    private void playAudio(String resourceName, boolean loop) {
        new Thread(() -> {
            try {
                String path = "minigames/sudoku/" + resourceName;
                InputStream is = getClass().getClassLoader().getResourceAsStream(path);

                if (is == null) {
                    System.err.println("Vẫn không thấy file tại: " + path);
                    return;
                }

                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                if (loop) {
                    bgMusic = clip;
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void stopMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(244, 210, 225));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (isGameOver) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawCommonOverlay(g2);
            g2.dispose();
        }
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 60));

        ModernButton quitBtn = new ModernButton("GIVE UP", new Dimension(120, 40));
        quitBtn.setColors(new Color(220, 20, 20), new Color(130, 0, 0));
        quitBtn.setForeground(Color.WHITE);
        quitBtn.addActionListener(e -> {
            stopMusic();
            isGameOver = true;
            isVictory = false;
            repaint();
        });

        errorLabel = new JLabel("Mistakes: 0 / " + game.getMaxErrors());
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);

        header.add(quitBtn, BorderLayout.WEST);
        header.add(errorLabel, BorderLayout.CENTER);
        header.add(Box.createHorizontalStrut(120), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void initCenter() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        int size = game.getSize();
        cellLabels = new JLabel[size][size];

        JPanel board = new JPanel(new GridLayout(size, size, 0, 0));
        board.setBackground(COLOR_GRID_LINE);
        board.setBorder(BorderFactory.createLineBorder(COLOR_GRID_LINE, 4));

        int bR = (size == 4 || size == 6) ? 2 : 3;
        int bC = (size == 4) ? 2 : 3;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JLabel cell = new JLabel("", JLabel.CENTER);
                cell.setFont(new Font("SansSerif", Font.BOLD, 46));
                cell.setOpaque(true);
                cell.setBackground(COLOR_BG);

                int b = (r % bR == bR - 1 && r != size - 1) ? 4 : 1;
                int rt = (c % bC == bC - 1 && c != size - 1) ? 4 : 1;
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, b, rt, COLOR_GRID_LINE));

                final int row = r, col = c;
                cell.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (!isGameOver) {
                            selectedRow = row; selectedCol = col;
                            refreshUI();
                        }
                    }
                });
                cellLabels[r][c] = cell;
                board.add(cell);
            }
        }

        JPanel squarePanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Container p = getParent();
                int s = (p != null && p.getHeight() > 0) ? Math.min(p.getWidth(), p.getHeight() - 85) : 600;
                return new Dimension(s, s);
            }
        };
        squarePanel.setOpaque(false);
        squarePanel.add(board);
        centerWrapper.add(squarePanel);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void initBottom() {
        JPanel numPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        numPanel.setOpaque(false);
        for (int i = 1; i <= game.getSize(); i++) {
            ModernButton btn = new ModernButton(String.valueOf(i), new Dimension(55, 55));
            btn.setForeground(COLOR_TEXT_USER);
            int v = i;
            btn.addActionListener(e -> {
                if (!isGameOver && selectedRow != -1 && selectedCol != -1) {
                    // GỌI FILE "Move.wav" TRONG CÙNG THƯ MỤC
                    playAudio("Move.wav", false);

                    game.setValue(selectedRow, selectedCol, v);
                    errorLabel.setText("Mistakes: " + game.getErrors() + " / " + game.getMaxErrors());
                    refreshUI();
                    if (game.isGameOver()) { stopMusic(); isGameOver = true; isVictory = false; repaint(); }
                    else if (isWon()) { stopMusic(); isGameOver = true; isVictory = true; repaint(); }
                }
            });
            numPanel.add(btn);
        }
        add(numPanel, BorderLayout.SOUTH);
    }

    private boolean isConflict(int r, int c, int val) {
        if (val == 0) return false;
        int size = game.getSize();
        for (int i = 0; i < size; i++) {
            if (i != c && game.getValue(r, i) == val) return true;
            if (i != r && game.getValue(i, c) == val) return true;
        }
        int bR = (size == 4 || size == 6) ? 2 : 3;
        int bC = (size == 4) ? 2 : 3;
        int sR = (r / bR) * bR, sC = (c / bC) * bC;
        for (int i = sR; i < sR + bR; i++)
            for (int j = sC; j < sC + bC; j++)
                if ((i != r || j != c) && game.getValue(i, j) == val) return true;
        return false;
    }

    private boolean isWon() {
        for(int r=0; r<game.getSize(); r++)
            for(int c=0; c<game.getSize(); c++) {
                int v = game.getValue(r,c);
                if(v == 0 || !game.isCorrect(r,c,v) || isConflict(r,c,v)) return false;
            }
        return true;
    }

    public void refreshUI() {
        if (isGameOver) return;
        for (int r = 0; r < game.getSize(); r++) {
            for (int c = 0; c < game.getSize(); c++) {
                JLabel cell = cellLabels[r][c];
                int val = game.getValue(r, c);
                boolean fixed = game.isFixed(r, c);
                boolean correct = game.isCorrect(r, c, val);
                boolean conflict = isConflict(r, c, val);
                cell.setText(val == 0 ? "" : String.valueOf(val));
                if (val != 0 && (!correct || conflict)) cell.setBackground(COLOR_ERROR_BG);
                else if (r == selectedRow && c == selectedCol) cell.setBackground(COLOR_SELECTED);
                else if (r == selectedRow || c == selectedCol) cell.setBackground(COLOR_HIGHLIGHT);
                else if (fixed) cell.setBackground(COLOR_FIXED_BG);
                else cell.setBackground(COLOR_BG);
                cell.setForeground(fixed ? Color.BLACK : (correct ? COLOR_TEXT_USER : COLOR_TEXT_ERROR));
            }
        }
    }

    @Override
    protected void handleExit() {
        stopMusic();
        if (isVictory) controller.onMinigameWin();
        else controller.onMinigameLose();
    }
}

class ModernButton extends JButton {
    private Color baseColor = new Color(245, 245, 245), shadowColor = new Color(200, 200, 200);
    public ModernButton(String text, Dimension size) {
        super(text); setPreferredSize(size); setContentAreaFilled(false);
        setFocusPainted(false); setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 18)); setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public void setColors(Color b, Color s) { baseColor = b; shadowColor = s; }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth() - 4, h = getHeight() - 4;
        g2.setColor(new Color(0, 0, 0, 80)); g2.fillRoundRect(3, 3, w, h, 12, 12);
        Color c1 = getModel().isPressed() ? shadowColor : baseColor;
        Color c2 = getModel().isPressed() ? baseColor : shadowColor;
        g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        g2.setFont(getFont()); FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2, ty = (h + fm.getAscent()) / 2 - 2;
        g2.setColor(getForeground()); g2.drawString(getText(), tx, ty);
        g2.dispose();
    }
}