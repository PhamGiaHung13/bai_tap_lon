import java.util.Random;

public class SudokuGame {

    int size;
    int[][] solution;
    int[][] puzzle;

    int errors = 0;
    Random rand = new Random();

    public SudokuGame(int difficulty){

        if(difficulty == 1) size = 4;
        else if(difficulty == 2) size = 6;
        else size = 9;

        solution = new int[size][size];
        puzzle = new int[size][size];

        generate(0, 0);          // sinh solution
        copy(solution, puzzle);
        removeCells(difficulty); // tạo puzzle theo độ khó
    }

    // ================== SINH SOLUTION ==================
    boolean generate(int r, int c){

        if(r == size) return true;

        int nextR = (c == size - 1) ? r + 1 : r;
        int nextC = (c + 1) % size;

        int[] nums = shuffle();

        for(int num : nums){
            if(isSafe(r, c, num)){
                solution[r][c] = num;

                if(generate(nextR, nextC)) return true;
            }
        }

        solution[r][c] = 0;
        return false;
    }

    int[] shuffle(){
        int[] arr = new int[size];
        for(int i=0;i<size;i++) arr[i] = i+1;

        for(int i=0;i<size;i++){
            int j = rand.nextInt(size);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        return arr;
    }

    boolean isSafe(int r, int c, int val){

        for(int i=0;i<size;i++){
            if(solution[r][i] == val || solution[i][c] == val){
                return false;
            }
        }

        if(size == 6){
            int boxR = (r/2)*2;
            int boxC = (c/3)*3;

            for(int i=0;i<2;i++){
                for(int j=0;j<3;j++){
                    if(solution[boxR+i][boxC+j] == val){
                        return false;
                    }
                }
            }
        } else {
            int box = (int)Math.sqrt(size);
            int boxR = (r/box)*box;
            int boxC = (c/box)*box;

            for(int i=0;i<box;i++){
                for(int j=0;j<box;j++){
                    if(solution[boxR+i][boxC+j] == val){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    void copy(int[][] a, int[][] b){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                b[i][j] = a[i][j];
            }
        }
    }

    // ================== TẠO PUZZLE ==================
    void removeCells(int difficulty){

        int remove;

        if(difficulty == 1) remove = size * size / 4;
        else if(difficulty == 2) remove = size * size / 2;
        else remove = size * size * 3 / 4;

        while(remove > 0){
            int r = rand.nextInt(size);
            int c = rand.nextInt(size);

            if(puzzle[r][c] != 0){
                puzzle[r][c] = 0;
                remove--;
            }
        }
    }

    // ================== MOVE ==================
    public boolean move(int r, int c, int val){

        if(puzzle[r][c] != 0) return false;

        if(solution[r][c] == val){
            puzzle[r][c] = val;
            return true;
        } else {
            errors++;
            return false;
        }
    }

    // ================== CHECK WIN ==================
    public boolean isWin(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(puzzle[i][j] == 0) return false;
            }
        }
        return true;
    }

    public void printPuzzle(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(puzzle[i][j] == 0){
                    System.out.print(". ");
                } else {
                    System.out.print(puzzle[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public int[][] getPuzzle(){ return puzzle; }
    public int[][] getSolution(){ return solution; }
    public int getSize(){ return size; }
}