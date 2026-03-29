package core.Logic;

import Controller.MiniGames;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class Events {

    boolean chance = false;

    private Board gameBoard;
    private Tile[][] board;

    public Events(Board board) {
        this.gameBoard = board;
        this.board = board.board;
    }

    //enumeration - liet ke trang thai
    public enum ClickResult{
        SAFE,
        MINE
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
            if(!chance){
                MiniGames mini = new MiniGames(difficulty);
                boolean survive = mini.playMiniGame();

                chance = true;

                if(survive){
                    System.out.println("ban da duoc cuu");
                    tile.setRevealed(true);
                    if(onUpdate != null) onUpdate.run();
                    return ClickResult.SAFE;
                }
            }

            gameBoard.gameOver = true;
            return ClickResult.MINE;
        }

        // mở ô - them hieu ung lan ra tu tu
        revealWithTimer(r, c, onUpdate);
        return ClickResult.SAFE;
    }


}