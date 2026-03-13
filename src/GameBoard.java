import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameBoard {

    private int rows = 8;
    private int columns = 8;

    private ArrayList<String> minesLocation = new ArrayList<>(); // 2-2, 3-4, 2-1

    private boolean flagEnabled = false;
    private JButton flagButton; // nút bật tắt cờ
    private boolean gameOver = false;

    private Tile[][] board;

    // ========================
    // Bật / tắt chế độ cờ
    // ========================
    public void setFlag() {

        if (flagEnabled) {
            flagEnabled = false;
            flagButton.setBackground(Color.GRAY);
        } else {
            flagEnabled = true;
            flagButton.setBackground(Color.BLACK);
        }

    }

    // ========================
    // Click vào ô
    // ========================
    public void clickTile(Tile tile) {

        if (gameOver || tile.getClientProperty("clicked") != null) return;

        // chế độ đặt cờ
        if (flagEnabled) {

            String text = tile.getText();

            if (text.equals("")) {
                tile.setText("🚩");
            } else if (text.equals("🚩")) {
                tile.setText("");
            }

            return;
        }

        String tileId = tile.id; // ví dụ "0-0"

        // trúng mìn
        if (minesLocation.contains(tileId)) {
            gameOver = true;
            revealMines();
            return;
        }

        String[] coords = tileId.split("-");
        int r = Integer.parseInt(coords[0]);
        int c = Integer.parseInt(coords[1]);

        checkMines(r, c);

        // đánh dấu đã click
        tile.putClientProperty("clicked", true);
    }

    // ========================
    // Hiện tất cả mìn
    // ========================
    public void revealMines() {

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {

                Tile tile = board[r][c];

                if (minesLocation.contains(tile.id)) {

                    tile.setText("💣");
                    tile.setBackground(Color.RED);

                }
            }
        }

    }

}

void main() {

}
