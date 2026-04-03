package core.Logic;

import Controller.GameController;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class Events {

    boolean chance = false;

    private Board gameBoard;
    private Tile[][] board;
    private GameController controller;

    public Events(Board board) {
        this.gameBoard = board;
        this.board = board.board;
    }


    /// ----
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
        if (gameBoard.gameOver || tile.isRevealed()) return ClickResult.SAFE;

        int r = tile.row;
        int c = tile.col;

        // neu dam phai min , start random minigame
        if (tile.isMine()) {

            int difficulty;
            //do kho cho minigame
            if(gameBoard.mode == 4){
                difficulty = gameBoard.getDifficulty();
            } else {
                difficulty = gameBoard.mode;
            }

            //kiem tra de hoi sinh 1 lan duy nhat
            if(!chance && controller != null){

                chance = true;

                controller.startSecondChance(tile, difficulty);

                return ClickResult.CONTINUE;
            }

            gameBoard.gameOver = true;
            return ClickResult.MINE;
        }

        // mở ô - them hieu ung lan ra tu tu
        revealWithTimer(r, c, onUpdate);
        return ClickResult.SAFE;
    }


}