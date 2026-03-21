package core.Logic;

import Controller.MiniGames;

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
                    gameBoard.revealTile(r,c);

                    return ClickResult.SAFE;
                }
            }

            gameOver = true;
            return ClickResult.MINE;
        }

        // mở ô
        gameBoard.revealTile(r, c);
        return ClickResult.SAFE;
    }


}