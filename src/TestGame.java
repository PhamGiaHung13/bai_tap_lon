import javax.swing.*;
import java.awt.*;

public class TestGame {

    JFrame frame;
    JPanel boardPanel;
    JPanel topPanel;

    JButton flagButton;

    Board board;
    Events events;

    public TestGame() {

        // ======================
        // Tạo logic game
        // ======================
        board = new Board();
        events = new Events(board);

        // ======================
        // Tạo cửa sổ
        // ======================
        frame = new JFrame("Minesweeper");
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ======================
        // Panel trên (nút cờ)
        // ======================
        topPanel = new JPanel();

        flagButton = new JButton("🚩 Flag");
        flagButton.setBackground(Color.GRAY);

        events.setFlagButton(flagButton);

        flagButton.addActionListener(e -> {
            events.setFlag();
        });

        topPanel.add(flagButton);

        frame.add(topPanel, BorderLayout.NORTH);

        // ======================
        // Panel board
        // ======================
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(board.rows, board.columns));

        Tile[][] tiles = board.board;

        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.columns; c++) {

                Tile tile = tiles[r][c];

                tile.setFont(new Font("Arial", Font.BOLD, 20));

                tile.addActionListener(e -> {
                    events.clickTile(tile);
                });

                boardPanel.add(tile);

            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new TestGame();
        });

    }

}