public class SudokuGame {

    int[][] solution;
    int size;

    public SudokuGame(int difficulty){

        if(difficulty == 1){

            size = 4;

            solution = new int[][]{
                    {1,2,3,4},
                    {3,4,1,2},
                    {2,1,4,3},
                    {4,3,2,1}
            };
        }

        else if(difficulty == 2){

            size = 6;

            solution = new int[][]{
                    {1,2,3,4,5,6},
                    {4,5,6,1,2,3},
                    {2,3,1,5,6,4},
                    {5,6,4,2,3,1},
                    {3,1,2,6,4,5},
                    {6,4,5,3,1,2}
            };
        }

        else{

            size = 9;

            solution = new int[][]{
                    {5,3,4,6,7,8,9,1,2},
                    {6,7,2,1,9,5,3,4,8},
                    {1,9,8,3,4,2,5,6,7},
                    {8,5,9,7,6,1,4,2,3},
                    {4,2,6,8,5,3,7,9,1},
                    {7,1,3,9,2,4,8,5,6},
                    {9,6,1,5,3,7,2,8,4},
                    {2,8,7,4,1,9,6,3,5},
                    {3,4,5,2,8,6,1,7,9}
            };
        }
    }

    public boolean move(int r,int c,int value){

        if(solution[r][c] == value){
            return true;
        }

        return false;
    }

    public int getSize(){
        return size;
    }

    public int[][] getSolution(){
        return solution;
    }
}