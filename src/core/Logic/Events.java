package core.Logic;

import Controller.GameController;
import java.util.List;
import Controller.GameController;

import javax.swing.*;
import java.awt.*;

public class Events {

    boolean chance = false;
    private GameController controller;

    private Board gameBoard;
    private Tile[][] board;

    public Events(Board board) {
        this.gameBoard = board;
        this.board = board.board;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    //enumeration - liet ke trang thai
    public enum ClickResult{
        SAFE,
        MINE,
        CONTINUE;
    }

    private void revealWithTimer(int r, int c, Runnable onUpdate){
        List<Tile> tilesToReveal = gameBoard.getRevealTiles(r, c);
        javax.swing.Timer timer = new javax.swing.Timer(20, null);

        timer.addActionListener(e -> {
            if(tilesToReveal.isEmpty()){
                timer.stop();
                return;
            }
            Tile t = tilesToReveal.remove(0);

            if(!t.isRevealed()) {
                t.setRevealed(true);
                gameBoard.tilesClicked++;//dem so o da mo
                if(onUpdate != null) onUpdate.run();//callback function
            }

        });
        timer.start();
    }


    // ========================
    // Click vào ô
    // ========================

    //su kien
    public ClickResult clickTile(Tile tile, Runnable onUpdate) {
        // 1. Kiểm tra trạng thái cơ bản
        if (gameBoard.gameOver || tile.isRevealed()) return ClickResult.SAFE;

        // 2. Xử lý khi đạp trúng mìn
        if (tile.isMine()) {
            // LẤY ĐỘ KHÓ (DIFFICULTY) CHO MINIGAME
            int difficulty;
            if (gameBoard.mode == 4) {
                // Nếu là Custom Mode: Tính dựa trên số lượng ô thực tế (Logic nằm trong Board)
                difficulty = gameBoard.getDifficulty();
            } else {
                // Nếu là Easy(1), Medium(2), Hard(3): Lấy trực tiếp Mode
                difficulty = gameBoard.mode;
            }
            // 3. XỬ LÝ HỒI SINH (SECOND CHANCE)
            // Nếu chưa dùng chance và đã gắn Controller
            if (!chance && controller != null) {
                chance = true; // Đánh dấu đã dùng quyền trợ giúp

                // Gửi độ khó đã tính toán ở trên vào Minigame
                controller.startSecondChance(tile, difficulty);

                // Trả về SAFE để GamePanel không hiện hiệu ứng nổ bom ngay
                return ClickResult.CONTINUE;
            }

            // Nếu đã hết lượt hoặc ko có controller -> Chết thực sự
            gameBoard.gameOver = true;
            return ClickResult.MINE;
        }

        // 4. Nếu không trúng mìn -> Mở ô bình thường
        revealWithTimer(tile.row, tile.col, onUpdate);
        return ClickResult.SAFE;
    }
}