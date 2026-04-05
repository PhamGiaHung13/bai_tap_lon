package minigames.sudoku.Logic;

import java.util.Random;

public class SudokuGame {
    private int size;
    private int[][] board;
    private int[][] solution;
    private boolean[][] fixed;
    private boolean[][] locked;
    private int errors = 0;
    private int maxErrors;

    public SudokuGame(int difficulty) {
        if (difficulty == 1) {
            size = 4;
            maxErrors = 3;
        } else if (difficulty == 2) {
            size = 6;
            maxErrors = 4;
        } else {
            size = 9;
            maxErrors = 5;
        }

        board = new int[size][size];
        solution = new int[size][size];
        fixed = new boolean[size][size];
        locked = new boolean[size][size];

        generateSolution(0, 0);
        copy(solution, board);
        removeCells();
    }

    public int getSize() {
        return size;
    }

    public int getValue(int r, int c) {
        return board[r][c];
    }

    public boolean isFixed(int r, int c) {
        return fixed[r][c];
    }

    public boolean isLocked(int r, int c) {
        return locked[r][c];
    }

    public void lockCell(int r, int c) {
        locked[r][c] = true;
    }

    public boolean isCorrect(int r, int c, int value) {
        return solution[r][c] == value;
    }

    public int getErrors() {
        return errors;
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public boolean isGameOver() {
        return errors >= maxErrors;
    }

    public boolean isWin() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board[i][j] != solution[i][j])
                    return false;
        return true;
    }

    public void setValue(int r, int c, int value) {
        if (fixed[r][c] || locked[r][c]) return;
        board[r][c] = value;
        if (solution[r][c] == value) {
            lockCell(r, c);
        } else {
            if (value != 0) errors++;
        }
    }

    private boolean generateSolution(int row, int col) {
        if (row == size) return true;
        int nextRow = (col == size - 1) ? row + 1 : row;
        int nextCol = (col == size - 1) ? 0 : col + 1;
        int[] numbers = shuffle();
        for (int num : numbers) {
            if (valid(solution, row, col, num)) {
                solution[row][col] = num;
                if (generateSolution(nextRow, nextCol))
                    return true;
            }
        }
        solution[row][col] = 0;
        return false;
    }

    private int[] shuffle() {
        int[] nums = new int[size];
        for (int i = 0; i < size; i++)
            nums[i] = i + 1;

        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            int j = rand.nextInt(size);
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
        return nums;
    }

    private boolean valid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < size; i++) {
            if (grid[row][i] == num) return false;
            if (grid[i][col] == num) return false;
        }

        int boxRows;
        int boxCols;

        if (size == 6) {
            boxRows = 2;
            boxCols = 3;
        } else {
            boxRows = (int) Math.sqrt(size);
            boxCols = boxRows;
        }

        int startRow = row - row % boxRows;
        int startCol = col - col % boxCols;

        for (int r = 0; r < boxRows; r++) {
            for (int c = 0; c < boxCols; c++) {
                if (grid[startRow + r][startCol + c] == num)
                    return false;
            }
        }

        return true;
    }

    private void removeCells() {
        Random rand = new Random();
        int totalCells = size * size;
        int removeCount;

        if (size == 4) removeCount = 6 + rand.nextInt(3);
        else if (size == 6) removeCount = 15 + rand.nextInt(5);
        else removeCount = 45 + rand.nextInt(10);

        while (removeCount > 0) {
            int r = rand.nextInt(size);
            int c = rand.nextInt(size);

            if (board[r][c] != 0) {
                if (canRemove(r, c)) {
                    board[r][c] = 0;
                    removeCount--;
                }
            }
        }

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                fixed[i][j] = board[i][j] != 0;
    }

    private boolean canRemove(int r, int c) {
        int rowRemain = 0;
        int colRemain = 0;
        for (int i = 0; i < size; i++) {
            if (board[r][i] != 0) rowRemain++;
            if (board[i][c] != 0) colRemain++;
        }
        int min = (size == 4) ? 1 : 2;
        return rowRemain > min && colRemain > min;
    }

    private void copy(int[][] src, int[][] dest) {
        for (int i = 0; i < size; i++)
            System.arraycopy(src[i], 0, dest[i], 0, size);
    }
}
