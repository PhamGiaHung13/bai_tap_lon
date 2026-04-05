package minigames.chess.Logic;

public class ChessPuzzle {

    private int step = 0;
    private String[][] solution;
    private int difficulty;

    public ChessPuzzle(int difficulty) {
        this.difficulty = difficulty;
        setupPuzzle();
    }

    private void setupPuzzle() {
        if (difficulty == 1) {
            solution = new String[][]{
                    {"d5", "c7"}
            };
        }
        else if (difficulty == 2) {
            solution = new String[][]{
                    {"c4", "c5"},
                    {"d6", "e6"},
                    {"b6", "b3"}
            };
        }
        else {

            solution = new String[][]{
                    {"c1", "h1"},
                    {"d8", "h4"},
                    {"h1", "h2"},
                    {"g3", "h2"},
                    {"g2", "g4"}
            };
        }
    }

    public boolean playerMove(String from, String to) {
        if (step >= solution.length) return false;
        String[] correct = solution[step];
        if (step % 2 == 0) {
            if (from.equalsIgnoreCase(correct[0]) && to.equalsIgnoreCase(correct[1])) {
                step++;
                return true;
            }
        }
        step = 0;
        return false;
    }

    public boolean isBlackTurn() {
        return step < solution.length && step % 2 == 1;
    }

    public String[] getBlackMove() {
        if (isBlackTurn()) {
            String[] move = solution[step];
            step++;
            return move;
        }
        return null;
    }

    public boolean isSolved() {
        return step == solution.length;
    }

    public void reset() {
        step = 0;
        setupPuzzle();
    }

    public int getStep() {
        return step;
    }
}