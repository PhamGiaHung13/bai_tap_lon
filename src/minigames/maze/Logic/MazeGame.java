package minigames.maze.Logic;

import java.util.*;

public class MazeGame {

    private int rows, cols;
    private int[][] maze;          // 1 = wall, 0 = path
    private boolean[][] food;      // food tồn tại ở đường đi

    private int playerRow, playerCol;
    private int exitRow, exitCol;

    private Random rand = new Random();

    public MazeGame(int difficulty){

        // ===== KÍCH THƯỚC THEO ĐỘ KHÓ =====
        if(difficulty == 1){
            rows = 15; cols = 19;
        }
        else if(difficulty == 2){
            rows = 19; cols = 25;
        }
        else{
            rows = 21; cols = 29;
        }

        maze = new int[rows][cols];

        generateNewMaze();
    }

    // =====================================================
    // ================== TẠO MAP MỚI ======================
    // =====================================================

    private void generateNewMaze(){

        // tạo toàn tường
        for(int r = 0; r < rows; r++){
            Arrays.fill(maze[r], 1);
        }

        // sinh maze bên trong
        generateMaze(1,1);

        // start & exit cố định
        playerRow = 1;
        playerCol = 1;

        exitRow = rows - 2;
        exitCol = cols - 2;

        maze[playerRow][playerCol] = 0;
        maze[exitRow][exitCol] = 0;

        // tăng độ khó: mở thêm ngẫu nhiên nhiều nhánh phụ
        addExtraPaths();

        // khởi tạo food
        initFood();
    }

    // =====================================================
    // ================== DFS CARVE =========================
    // =====================================================

    private void generateMaze(int r, int c){

        maze[r][c] = 0;

        int[][] dirs = {
                {0,2},{0,-2},{2,0},{-2,0}
        };

        List<int[]> directions = Arrays.asList(dirs);
        Collections.shuffle(directions, rand);

        for(int[] d : directions){

            int nr = r + d[0];
            int nc = c + d[1];

            if(nr > 0 && nr < rows-1 &&
                    nc > 0 && nc < cols-1 &&
                    maze[nr][nc] == 1){

                maze[r + d[0]/2][c + d[1]/2] = 0;
                generateMaze(nr,nc);
            }
        }
    }

    // =====================================================
    // ============ TĂNG ĐỘ KHÓ (THÊM NGÕ CỤT) ============
    // =====================================================

    private void addExtraPaths(){

        int extra = (rows * cols) / 15; // mật độ mở thêm

        for(int i = 0; i < extra; i++){

            int r = rand.nextInt(rows-2) + 1;
            int c = rand.nextInt(cols-2) + 1;

            maze[r][c] = 0;
        }
    }

    // =====================================================
    // ================= KHỞI TẠO FOOD =====================
    // =====================================================

    private void initFood(){

        food = new boolean[rows][cols];

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                if(maze[r][c] == 0){
                    food[r][c] = true;
                }
            }
        }

        food[playerRow][playerCol] = false;
        food[exitRow][exitCol] = false;
    }

    // =====================================================
    // ====================== MOVE ==========================
    // =====================================================

    public void move(int dr, int dc){

        int newR = playerRow + dr;
        int newC = playerCol + dc;

        if(newR >= 0 && newR < rows &&
                newC >= 0 && newC < cols &&
                maze[newR][newC] == 0){

            playerRow = newR;
            playerCol = newC;

            // ăn food
            if(food[newR][newC]){
                food[newR][newC] = false;
            }
        }
    }

    // =====================================================
    // ====================== WIN ===========================
    // =====================================================

    public boolean isWin(){
        return playerRow == exitRow && playerCol == exitCol;
    }

    // =====================================================
    // ====================== RESET =========================
    // =====================================================

    public void reset(){
        generateNewMaze();
    }

    // =====================================================
    // ===================== GETTERS ========================
    // =====================================================

    public int[][] getMaze(){ return maze; }
    public boolean[][] getFood(){ return food; }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }

    public int getPlayerRow(){ return playerRow; }
    public int getPlayerCol(){ return playerCol; }

    public int getExitRow(){ return exitRow; }
    public int getExitCol(){ return exitCol; }
}