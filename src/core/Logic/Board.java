package core.Logic;

import java.util.Random;

public class Board {

    public int rows;
    public int columns;
    public int minesCount;
    public int mode;

    public Tile[][] board;

    boolean gameOver = false;
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

    public void revealTile(int r, int c){

        if(r < 0 || r >= rows || c < 0 || c >= columns) return;

        Tile tile = board[r][c];

        if(tile.isRevealed()) return;

        tile.setRevealed(true);
        tilesClicked++;

        int minesAround = countMines(r, c);
        tile.setMinesAround(minesAround);

        if(minesAround == 0){

            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){

                    if(i != 0 || j != 0){

                        revealTile(r + i, c + j);

                    }

                }
            }
        }
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

    public Tile getTile(int r, int c){
        return board[r][c];
    }
}

