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
            /**
             * MEDIUM (Ảnh 2): Mate in 2
             * Trắng: Xe c5, Vua d3. Đen: Vua e1.
             */
            solution = new String[][]{
                    {"c5", "h5"},
                    {"e1", "f1"},
                    {"h5", "h1"}
            };
        }
        else {
            /**
             * HARD (Ảnh 3): Mate in 3
             */
            solution = new String[][]{
                    {"f5", "d6"},
                    {"b8", "a8"},
                    {"e5", "b8"},
                    {"a8", "b8"},
                    {"d7", "d8"}
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
}