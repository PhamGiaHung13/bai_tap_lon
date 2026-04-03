package minigames.sudoku.UI;

import Controller.GameController;
import minigames.sudoku.Logic.SudokuGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SudokuPanel extends JPanel {
    private SudokuGame game;
    private GameController controller;
    private JLabel[][] cellLabels;
    private JLabel errorLabel;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Image bg;

    // --- BẢNG MÀU CHUẨN THEO CODE CŨ ---
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_FIXED_BG = new Color(157, 189, 220);
    private final Color COLOR_GRID_LINE = new Color(44, 62, 80);
    private final Color COLOR_SELECTED = new Color(180, 210, 255);
    private final Color COLOR_HIGHLIGHT = new Color(235, 245, 255);
    private final Color COLOR_ERROR_BG = new Color(255, 210, 210);
    private final Color COLOR_TEXT_USER = new Color(0, 102, 204);
    private final Color COLOR_TEXT_ERROR = new Color(220, 53, 69);

    public SudokuPanel(int difficulty, GameController controller) {
        this.game = new SudokuGame(difficulty);
        this.controller = controller;

        // 1. NẠP ẢNH NỀN (Bưng từ bản Test qua)
        try {
            File imgFile = new File("bg1.jpg");
            if (imgFile.exists()) {
                bg = new ImageIcon(imgFile.getAbsolutePath()).getImage();
            } else {
                java.net.URL imgUrl = getClass().getResource("/minigames/sudoku/bg1.jpg");
                if (imgUrl != null) bg = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception e) {
            System.err.println("Không tìm thấy bg1.jpg!");
        }

        // 2. LAYOUT TỔNG THỂ
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        initHeader();
        initCenter();
        initBottom();
        refreshUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(244, 210, 225));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));

        ModernButton quitBtn = new ModernButton("Quit", new Dimension(110, 45));
        quitBtn.setColors(new Color(255, 240, 240), new Color(240, 180, 180));
        quitBtn.setForeground(COLOR_TEXT_ERROR);
        quitBtn.addActionListener(e -> controller.onMinigameLose());

        errorLabel = new JLabel("Mistakes: 0 / " + game.getMaxErrors());
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);

        header.add(quitBtn, BorderLayout.WEST);
        header.add(errorLabel, BorderLayout.CENTER);
        header.add(Box.createHorizontalStrut(110), BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void initCenter() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        int size = game.getSize();
        cellLabels = new JLabel[size][size];

        JPanel board = new JPanel(new GridLayout(size, size));
        board.setBackground(COLOR_BG);
        board.setBorder(BorderFactory.createLineBorder(COLOR_GRID_LINE, 3));

        // Logic chia khối chuẩn: 4x4 -> 2x2, 6x6 -> 2x3, 9x9 -> 3x3
        int boxRows = (size == 4 || size == 6) ? 2 : 3;
        int boxCols = (size == 4) ? 2 : 3;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JLabel cell = new JLabel("", JLabel.CENTER);
                cell.setFont(new Font("SansSerif", Font.BOLD, 34));
                cell.setOpaque(true);

                // Vẽ đường kẻ đậm MatteBorder (Chỉ vẽ bên trong, không vẽ rìa ngoài)
                int b = (r % boxRows == boxRows - 1 && r != size - 1) ? 3 : 1;
                int rt = (c % boxCols == boxCols - 1 && c != size - 1) ? 3 : 1;
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, b, rt, COLOR_GRID_LINE));

                final int row = r, col = c;
                cell.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        selectedRow = row; selectedCol = col;
                        refreshUI();
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
                if (p != null && p.getHeight() > 0) {
                    int s = Math.min(p.getWidth(), p.getHeight() - 120);
                    return new Dimension(s, s);
                }
                return new Dimension(500, 500);
            }
        };
        squarePanel.setOpaque(false);
        squarePanel.add(board);
        centerWrapper.add(squarePanel);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void initBottom() {
        JPanel numPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        numPanel.setOpaque(false);
        for (int i = 1; i <= game.getSize(); i++) {
            ModernButton btn = new ModernButton(String.valueOf(i), new Dimension(70, 70));
            btn.setForeground(COLOR_TEXT_USER);
            int v = i;
            btn.addActionListener(e -> {
                if (selectedRow != -1 && selectedCol != -1) {
                    game.setValue(selectedRow, selectedCol, v);
                    errorLabel.setText("Mistakes: " + game.getErrors() + " / " + game.getMaxErrors());
                    refreshUI();
                    if (game.isGameOver()) controller.onMinigameLose();
                    else if (isWon()) controller.onMinigameWin();
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
        int boxRows = (size == 4 || size == 6) ? 2 : 3;
        int boxCols = (size == 4) ? 2 : 3;
        int startR = (r / boxRows) * boxRows;
        int startC = (c / boxCols) * boxCols;
        for (int i = startR; i < startR + boxRows; i++) {
            for (int j = startC; j < startC + boxCols; j++) {
                if ((i != r || j != c) && game.getValue(i, j) == val) return true;
            }
        }
        return false;
    }

    private boolean isWon() {
        for(int r=0; r<game.getSize(); r++) {
            for(int c=0; c<game.getSize(); c++) {
                int val = game.getValue(r,c);
                if(val == 0 || !game.isCorrect(r,c,val) || isConflict(r,c,val)) return false;
            }
        }
        return true;
    }

    public void refreshUI() {
        for (int r = 0; r < game.getSize(); r++) {
            for (int c = 0; c < game.getSize(); c++) {
                JLabel cell = cellLabels[r][c];
                int val = game.getValue(r, c);
                boolean fixed = game.isFixed(r, c);
                boolean correct = game.isCorrect(r, c, val);
                boolean conflict = isConflict(r, c, val);

                cell.setText(val == 0 ? "" : String.valueOf(val));

                // THỨ TỰ ƯU TIÊN MÀU SẮC CHUẨN
                if (val != 0 && (!correct || conflict)) {
                    cell.setBackground(COLOR_ERROR_BG); // Sai/Trùng -> Đỏ
                } else if (r == selectedRow && c == selectedCol) {
                    cell.setBackground(COLOR_SELECTED); // Đang chọn -> Xanh đậm
                } else if (r == selectedRow || c == selectedCol) {
                    cell.setBackground(COLOR_HIGHLIGHT); // Cùng hàng/cột -> Xanh nhạt
                } else if (fixed) {
                    cell.setBackground(COLOR_FIXED_BG); // Mặc định -> Xanh xám
                } else {
                    cell.setBackground(COLOR_BG); // Ô trống -> Trắng
                }

                cell.setForeground(fixed ? Color.BLACK : (correct ? COLOR_TEXT_USER : COLOR_TEXT_ERROR));
            }
        }
    }
}

class ModernButton extends JButton {
    private Color baseColor = new Color(245, 245, 245), shadowColor = new Color(200, 200, 200);
    private boolean hovered = false;

    public ModernButton(String text, Dimension size) {
        super(text);
        setPreferredSize(size); setContentAreaFilled(false);
        setFocusPainted(false); setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 26)); setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
        });
    }
    public void setColors(Color b, Color s) { baseColor = b; shadowColor = s; }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int m = hovered ? 0 : 5;
        int w = getWidth()-(m*2), h = getHeight()-(m*2);
        if (getModel().isPressed()) {
            g2.setColor(baseColor); g2.fillRoundRect(m, m+4, w, h-4, 15, 15);
        } else {
            g2.setColor(shadowColor); g2.fillRoundRect(m, m+6, w, h-6, 15, 15);
            g2.setColor(baseColor); g2.fillRoundRect(m, m, w, h-6, 15, 15);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}