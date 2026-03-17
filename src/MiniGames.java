import java.util.Random;

public class MiniGames {

    String difficulty;
    Random rand = new Random();

    public MiniGames(String difficulty){
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
}