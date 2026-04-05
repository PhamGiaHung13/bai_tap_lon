package core.Logic;

import Controller.GameController;
import core.Audio.SoundManager;

import java.util.ArrayList;
import java.util.List;
import Controller.GameController;

import javax.swing.*;
import java.awt.*;

public class Events {

    boolean chance = false;

    private Board gameBoard;
    private Tile[][] board;
    private GameController controller;
    private List<Timer> activeTimers = new ArrayList<>();




    public Events(Board board) {
        this.gameBoard = board;
        this.board = board.board;
    }


    /// ---- SET CONTROLLER
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
        final int[] revealedCount = {0};

        Timer timer = new Timer(90, null);
        activeTimers.add(timer);

        timer.addActionListener(e -> {

            if(gameBoard.gameOver || tilesToReveal.isEmpty()){
                timer.stop();
                activeTimers.remove(timer);
                return;
            }


            Tile t = tilesToReveal.remove(0);

            if(!t.isRevealed()) {
                t.setRevealed(true);
                gameBoard.tilesClicked++;
                if(onUpdate != null) onUpdate.run();

                int index = Math.min(++revealedCount[0], 8);
                SoundManager.play("src/core/Sound/reveal" + index + ".wav");

                // Nếu hết ô để mở thì dừng
                if (tilesToReveal.isEmpty()) {
                    timer.stop();
                    activeTimers.remove(timer);
                }

            }
        });
        timer.start();
    }


    ///  ---- STOP TIMER
    public void stopAllTimers() {
        for (Timer t : activeTimers) {
            if (t != null) t.stop();
        }
        activeTimers.clear();
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

            stopAllTimers();

            tile.setRevealed(true);
            if (onUpdate != null) onUpdate.run();

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

        // 🎧 SOUND + REVEAL
        if(tile.getMinesAround() > 0){

            // ĐẶT Ở ĐÂY ĐỂ CHECK
            System.out.println("--- CLICKED ---");
            System.out.println("Row: " + tile.row + " Col: " + tile.col);
            System.out.println("Mines Around: " + tile.getMinesAround());
            System.out.println("Is Mine: " + tile.isMine());

            tile.setRevealed(true);
            gameBoard.tilesClicked++;
            System.out.println("O nay co so mìn la: " + tile.getMinesAround());
            SoundManager.play("src/core/Sound/reveal" + tile.getMinesAround() + ".wav");

            if(onUpdate != null) onUpdate.run();

        } else {
            revealWithTimer(r, c, onUpdate);
        }

        return ClickResult.SAFE;
    }
}