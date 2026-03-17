import java.util.Random;

public class MiniGames {

    int difficulty;
    Random rand = new Random();

    public MiniGames(int difficulty){
        this.difficulty = difficulty;
    }

    boolean playMiniGame(){

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

    // ======================
    // Sudoku
    // ======================
    boolean sudokuGame(){

        SudokuGame sudoku = new SudokuGame(difficulty);

        // tạm thời auto thắng
        return true;
    }
}