package core.Logic;

import Controller.MiniGames;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class Events {

    private boolean gameOver = false;
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


    private void revealWithTimer(int r, int c){
        List<Tile> tilesToReveal = gameBoard.getRevealTiles(r, c);
        javax.swing.Timer timer = new javax.swing.Timer(20, null);

        timer.addActionListener(e -> {
            if(tilesToReveal.isEmpty()){
                timer.stop();
                return;
            }
            Tile t = tilesToReveal.remove(0);

            if(t.isRevealed()) return;

            t.setRevealed(true);
            t.setEnabled(false);

            int mines = t.getMinesAround();

            if(mines > 0) t.setText(String.valueOf(mines));
            else t.setBackground(Color.WHITE);
        });
        timer.start();
    }


    // ========================
    // Click vào ô
    // ========================

    //chuyen thanh kieu enum ClickResult
    public ClickResult clickTile(Tile tile) {
        if (gameOver || tile.isRevealed()) return ClickResult.SAFE;

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
                    System.out.println("ban da duoc cuu");
                    tile.setMine(false);//xoa min tai vi tri vua choi minigame
                    tile.setText("\uD83D\uDCA3");           //  💣
                    revealWithTimer(r, c);
                    return ClickResult.SAFE;
                }
            }

            gameOver = true;
            return ClickResult.MINE;
        }

        // mở ô - them hieu ung lan ra tu tu
        revealWithTimer(r, c);
        return ClickResult.SAFE;
    }


}