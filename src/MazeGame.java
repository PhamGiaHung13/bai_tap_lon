import java.util.*;

public class MazeGame {

    int rows;
    int cols;
    int[][] maze;
    Random rand = new Random();

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
                maze[r][c] = 1; // tất cả là tường
            }
        }

        generate(1,1);
    }

    void generate(int r,int c){

        maze[r][c] = 0;

        int[][] dirs = {
                {0,2},{0,-2},{2,0},{-2,0}
        };

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

    public int[][] getMaze(){
        return maze;
    }
}