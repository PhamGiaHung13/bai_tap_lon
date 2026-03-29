package minigames.maze.Logic;

import java.util.*;

public class MazeGame {

    private int rows;
    private int cols;
    private int[][] maze;

    private int playerRow, playerCol;
    private int endRow, endCol;

    private Random rand = new Random();

    public MazeGame(int difficulty){

        if(difficulty == 1){
            rows = 7;
            cols = 7;
        }
        else if(difficulty == 2){
            rows = 11;
            cols = 11;
        }
        else{
            rows = 15;
            cols = 15;
        }

        maze = new int[rows][cols];

        for(int r=0;r<rows;r++){
            for(int c=0;c<cols;c++){
                maze[r][c] = 1;
            }
        }

        generate(1,1);

        randomStartEnd();
    }

    // ====== TẠO MAZE ======
    private void generate(int r,int c){

        maze[r][c] = 0;

        int[][] dirs = {{0,2},{0,-2},{2,0},{-2,0}};
        List<int[]> directions = Arrays.asList(dirs);
        Collections.shuffle(directions);

        for(int[] d : directions){

            int nr = r + d[0];
            int nc = c + d[1];

            if(nr>0 && nr<rows-1 && nc>0 && nc<cols-1 && maze[nr][nc]==1){

                maze[r + d[0]/2][c + d[1]/2] = 0;
                generate(nr,nc);
            }
        }
    }

    // ====== RANDOM START & END ======
    private void randomStartEnd(){

        List<int[]> paths = new ArrayList<>();

        for(int r=1;r<rows-1;r++){
            for(int c=1;c<cols-1;c++){
                if(maze[r][c] == 0){
                    paths.add(new int[]{r,c});
                }
            }
        }

        int[] start = paths.get(rand.nextInt(paths.size()));
        int[] end = paths.get(rand.nextInt(paths.size()));

        playerRow = start[0];
        playerCol = start[1];

        endRow = end[0];
        endCol = end[1];
    }

    // ====== MOVE ======
    public void move(int dr, int dc){
        int newR = playerRow + dr;
        int newC = playerCol + dc;

        if(newR >=0 && newR<rows && newC>=0 && newC<cols){
            if(maze[newR][newC] == 0){
                playerRow = newR;
                playerCol = newC;
            }
        }
    }

    public boolean isWin(){
        return playerRow == endRow && playerCol == endCol;
    }

    // ====== GETTERS ======
    public int[][] getMaze(){ return maze; }
    public int getRows(){ return rows; }
    public int getCols(){ return cols; }
    public int getPlayerRow(){ return playerRow; }
    public int getPlayerCol(){ return playerCol; }
    public int getEndRow(){ return endRow; }
    public int getEndCol(){ return endCol; }
}