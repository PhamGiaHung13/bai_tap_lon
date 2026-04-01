package core.Logic;

import java.util.*;

public class Board {

    public int rows;
    public int columns;
    public int minesCount;
    public int mode;

    public Tile[][] board;

    public boolean gameOver = false;
    int tilesClicked = 0;

    // ======================
    // Constructor chọn mode
    // ======================

    public Board(int mode){

        this.mode = mode;

        gameMode(mode);
        initializeBoard();
        setMines();
    }

    public Board() {
        this(1);
    }

    // ======================
    // Constructor custom
    // ======================
    public Board(int rows, int columns, int mines){

        this.rows = rows;
        this.columns = columns;
        this.minesCount = mines;
        this.mode = 4;

        initializeBoard();
        setMines();
    }

    // ======================
    // Chọn mode
    // ======================
    void gameMode(int mode){

        switch(mode){

            case 1:
                rows = 8;
                columns = 8;
                minesCount = 10;
                break;

            case 2:
                rows = 16;
                columns = 16;
                minesCount = 40;
                break;

            case 3:
                rows = 16;
                columns = 30;
                minesCount = 99;
                break;

            default:
                rows = 8;
                columns = 8;
                minesCount = 10;
        }
    }

    // =========================
    // 1. Tạo bảng
    // =========================

    public void initializeBoard(){

        board = new Tile[rows][columns];

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < columns; c++){

                board[r][c] = new Tile(r, c);

            }
        }
    }

    // =========================
    // 2. Đặt mìn
    // =========================

    public void setMines(){

        Random rand = new Random();
        int minesLeft = minesCount;

        while(minesLeft > 0){

            int r = rand.nextInt(rows);
            int c = rand.nextInt(columns);

            if(!board[r][c].isMine()){

                board[r][c].setMine(true);
                minesLeft--;

            }
        }
    }

    // =========================
    // 3. Đếm mìn xung quanh
    // =========================

    public int countMines(int r, int c){

        int count = 0;

        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){

                if(i == 0 && j == 0) continue;

                int newR = r + i;
                int newC = c + j;

                if(newR >= 0 && newR < rows &&
                        newC >= 0 && newC < columns &&
                        board[newR][newC].isMine()){

                    count++;

                }
            }
        }

        return count;
    }

    // =========================
    // 4. Mở ô
    // =========================

    public List<Tile> getRevealTiles(int r, int c){
        List<Tile> result = new ArrayList<>();
        boolean[][] visited = new boolean[rows][columns];

        Queue<Tile> queue = new LinkedList<>();
        queue.add(board[r][c]);

        while(!queue.isEmpty()){
            Tile tile = queue.poll();
            int row = tile.row, col = tile.col;
            if(row < 0 || row >= rows || col < 0 || col >= columns) continue;
            if(visited[row][col]) continue;

            visited[row][col] = true;

            int mines = countMines(row, col);
            tile.setMinesAround(mines);

             result.add(tile);

            if(mines == 0){
                for(int i=-1;i<=1;i++)
                    for(int j=-1;j<=1;j++)
                        if(i!=0 || j!=0){
                            int nr = row + i;
                            int nc = col + j;

                            if(nr>=0 && nr<rows && nc>=0 && nc<columns)
                                queue.add(board[nr][nc]);
                        }
            }
        }
        return result;
    }



    // =========================
    // Lấy độ khó cho MiniGame
    // =========================

    public int getDifficulty(){

        int size = rows * columns;

        if(size <= 64) return 1;        // easy
        if(size <= 256) return 2;       // medium
        return 3;                       // hard
    }

    //lay tile
    public Tile getTile(int r, int c){
        return board[r][c];
    }

    //check win
    public boolean isWin(){
        return tilesClicked == (rows * columns - minesCount);
    }

}

