package minigames.chess.Logic;

public class ChessPuzzle {

    private int step = 0;
    private String[][] solution;

    public ChessPuzzle(int difficulty){

        if(difficulty == 1){
            // Mate in 1
            solution = new String[][]{
                    {"g7","h7"}
            };
        }

        else if(difficulty == 2){
            // Mate in 2 (dễ)
            solution = new String[][]{
                    {"g7","h7"},
                    {"h7","g8"}
            };
        }

        else{
            // Mate in 2 (khó hơn)
            // Đúng thứ tự bắt buộc:
            // 1. Qh6+ !!
            // 2. Qg7#

            solution = new String[][]{
                    {"h6","h7"},
                    {"h7","g7"}
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

        // Nếu đi sai -> reset lại từ đầu (hard mode)
        step = 0;
        return false;
    }

    public boolean isSolved(){
        return step == solution.length;
    }

    public void reset(){
        step = 0;
    }
}