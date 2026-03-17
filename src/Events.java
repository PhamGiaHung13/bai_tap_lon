import javax.swing.*;
import java.awt.*;

public class Events {

    private boolean flagEnabled = false;
    private JButton flagButton; // nút bật tắt cờ
    private boolean gameOver = false;
    boolean chance = false;

    private Board gameBoard;
    private Tile[][] board;

    public Events(Board board) {
        this.gameBoard = board;
        this.board = board.board;
    }

    public void setFlagButton(JButton button) {
        this.flagButton = button;
    }

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

        if (gameOver || tile.isRevealed()) return;

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

        int r = tile.row;
        int c = tile.col;

        // trúng mìn
        if (tile.isMine()) {

            int difficulty;

            if(gameBoard.mode == 4){
                difficulty = gameBoard.getDifficulty();
            } else {
                difficulty = gameBoard.mode;
            }

            if(!chance) {
                MiniGames mini = new MiniGames(difficulty);
                boolean survive = mini.playMiniGame();

                chance = true;

                if (survive) {
                    System.out.println("Ban da duoc cuu");
                    return;
                }
            }

            tile.setText("💣");
            tile.setBackground(Color.RED);

            gameOver = true;
            revealMines();

            return;
        }

        // mở ô
        gameBoard.revealTile(r, c);

        updateBoard();
    }

    // cập nhật UI theo logic Board
    public void updateBoard() {

        for (int r = 0; r < gameBoard.rows; r++) {
            for (int c = 0; c < gameBoard.columns; c++) {

                Tile tile = board[r][c];

                if (tile.isRevealed()) {

                    tile.setEnabled(false);

                    int mines = tile.getMinesAround();

                    if (mines > 0) {
                        tile.setText(String.valueOf(mines));
                    }

                }

            }
        }
    }

    // ========================
    // Hiện tất cả mìn
    // ========================
    public void revealMines() {

        for (int r = 0; r < gameBoard.rows; r++) {
            for (int c = 0; c < gameBoard.columns; c++) {

                Tile tile = board[r][c];

                if (tile.isMine()) {

                    tile.setText("💣");
                    tile.setBackground(Color.RED);

                }
            }
        }

    }

}
