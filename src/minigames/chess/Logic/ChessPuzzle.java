package minigames.chess.Logic;

public class ChessPuzzle {

    int step = 0;
    String[][] solution;

    public ChessPuzzle(int difficulty){

        if(difficulty == 1){
            // mate in 1
            solution = new String[][]{
                    {"g7","h7"}
            };
        }

        else if(difficulty == 2){
            // mate in 2
            solution = new String[][]{
                    {"g7","h7"},
                    {"h7","g8"}
            };
        }

        else{
            // mate in 3
            solution = new String[][]{
                    {"g7","h7"},
                    {"h7","g8"},
                    {"g8","h8"}
            };
        }
    }

    public boolean move(String from, String to){

        if(step >= solution.length) return false;

        String[] correct = solution[step];

        if(from.equals(correct[0]) && to.equals(correct[1])){
            step++;
            return true;
        }

        return false;
    }

    public boolean isSolved(){
        return step == solution.length;
    }
}
