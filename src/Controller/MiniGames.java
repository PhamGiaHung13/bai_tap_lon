package Controller;

import minigames.chess.Logic.ChessPuzzle;
import minigames.maze.Logic.MazeGame;
import minigames.sudoku.Logic.SudokuGame;

import java.util.Random;
import java.util.Scanner;

public class MiniGames {

    int difficulty;
    Random rand = new Random();

    public MiniGames(int difficulty){
        this.difficulty = difficulty;
    }

    public boolean playMiniGame(){

        int game = rand.nextInt(3);

        if(game == 0){
            return chessPuzzle();
        }

        if(game == 1){
            return mazeGame();
        }

        return sudokuGame();
    }

    // ======================
    // Chess puzzle
    // ======================
    boolean chessPuzzle(){

        ChessPuzzle puzzle = new ChessPuzzle(difficulty);

        // tạm thời auto thắng để test
        return true;
    }

    // ======================
    // Maze
    // ======================
    boolean mazeGame(){

        MazeGame maze = new MazeGame(difficulty);

        // tạm thời auto thắng
        return true;
    }

    // ================== SUDOKU GAME ==================
    boolean sudokuGame(){

        SudokuGame game = new SudokuGame(difficulty);

        int maxErrors;
        if(difficulty == 1) maxErrors = 5;
        else if(difficulty == 2) maxErrors = 3;
        else maxErrors = 2;

        while(true){

            game.printPuzzle();

            int[][] board = game.getPuzzle();
            int size = game.getSize();

            // tìm ô trống
            int r = -1, c = -1;
            for(int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    if(board[i][j] == 0){
                        r = i;
                        c = j;
                        break;
                    }
                }
                if(r != -1) break;
            }

            if(r == -1) return true; // WIN

            Scanner sc = new Scanner(System.in);

            System.out.println("Sudoku: ["+r+"]["+c+"] = ");
            int guess = sc.nextInt();

            if(!game.move(r,c,guess)){
                maxErrors--;
                System.out.println("Sai! Con " + maxErrors);

                if(maxErrors == 0){
                    return false; // LOSE
                }
            }
        }
    }
}
