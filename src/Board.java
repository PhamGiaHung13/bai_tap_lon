
import java.util.Random;

public class Board {

    int rows = 8;
    int columns = 8;
    int minesCount = 10;

    Tile[][] board;
    boolean gameOver = false;
    int tilesClicked = 0;

    public Board() {
        initializeBoard();
        setMines();
    }

    // =========================
    // 1. Tạo bảng
    // =========================
    public void initializeBoard() {
        board = new Tile[rows][columns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                board[r][c] = new Tile(r, c);
            }
        }
    }

    // =========================
    // 2. Đặt mìn
    // =========================
    public void setMines() {
        Random rand = new Random();
        int minesLeft = minesCount;

        while (minesLeft > 0) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(columns);

            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                minesLeft--;
            }
        }
    }

    // =========================
    // 3. Đếm mìn xung quanh
    // =========================
    public int countMines(int r, int c) {

        int count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                int newR = r + i;
                int newC = c + j;

                if (newR >= 0 && newR < rows &&
                        newC >= 0 && newC < columns &&
                        board[newR][newC].isMine()) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================
    // 4. Mở ô
    // =========================
    public void revealTile(int r, int c) {

        if (r < 0 || r >= rows || c < 0 || c >= columns) return;

        Tile tile = board[r][c];

        if (tile.isRevealed()) return;

        tile.setRevealed(true);
        tilesClicked++;

        int minesAround = countMines(r, c);
        tile.setMinesAround(minesAround);

        if (minesAround == 0) {

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {

                    if (i != 0 || j != 0) {
                        revealTile(r + i, c + j);
                    }

                }
            }

        }
    }
}