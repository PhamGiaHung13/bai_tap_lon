package minigames.blockblast.Logic;

import java.util.Random;

public class Blockblast {

    public int rows = 20;//so hang cua bang
    public int cols = 10;//cot cua bang

    int[][] board = new int[rows][cols];//bang game (0=trong, 1=da co block)
    int[][] piece;//khoi hien tai dang roi

    int row, col;//vi tri cua khoi
    int score = 0;//diem

    long startTime;//thoi gian bat dau
    long timeLimit;//thoi gian gioi han

    boolean gameOver = false;
    boolean win = false;

    public int targetScore;//diem can dat de thang

    Random rand = new Random();
    //==========
    //Danh sach cac hinh khoi
    //==========
    int[][][] shapes = {
            {{1,1,1,1}},            //I
            {{1}},
            {{1,1},{1,1}},          //O
            {{0,1,0},{1,1,1}},      //T ngược
            {{1,0},{1,0},{1,1}},    //L
            {{0,1},{0,1},{1,1}},    //J
            {{0,1,1},{1,1,0}},      //S
            {{1,1,0},{0,1,1}},      //Z
            {{1,1},{1,0}},
            {{1,1},{0,1}}

    };

    public Blockblast(long timeLimit, int targetScore){
        this.timeLimit = timeLimit;
        this.targetScore = targetScore;
        this.startTime = System.currentTimeMillis();
        spawn();//sinh khoi dau tien
    }

    // ================= GAME LOOP =================
    public void tick(){// cập nhật game mỗi frame

        if(gameOver || win) return;

        //kiểm tra hết thời gian
        if(System.currentTimeMillis() - startTime >= timeLimit){
            gameOver = true;
            return;
        }

        //thử cho khối rơi xuống
        if(!collision(row + 1, col)){
            row++;
        } else {
            merge();        //ghép khối vào board
            clearLines();   //Xóa dòng đầy
            spawn();        //sih khối mới
        }

        //kiểm tra thắng
        if(score >= targetScore){
            win = true;
        }
    }

    // ================= CONTROL =================
    public void moveLeft(){//di chuyển sang trái
        if(!collision(row, col - 1)) col--;
    }

    public void moveRight(){//di chuyển sang phải
        if(!collision(row, col + 1)) col++;
    }

    public void moveDown(){//rơi xuống 1 bước
        if(!collision(row + 1, col)) row++;
    }

    public void rotate(){//xoay khối
        int[][] r = rotateMatrix(piece);

        if(!collisionWith(r, row, col)){
            piece = r;
        }
    }

    // ================= CORE =================
    void spawn(){//tạo khối mới

        //cho random shapes
        piece = copy(shapes[rand.nextInt(shapes.length)]);

        row = 0;//xuất hiện trên cùng
        col = cols/2 - piece[0].length/2;//căn giưa

        if(collision(row, col)){
            gameOver = true;//nếu spawn bị đè thì thua
        }
    }

    boolean collision(int r, int c){//kiểm tra va chạm khối hiện tại
        return collisionWith(piece, r, c);
    }

    boolean collisionWith(int[][] p, int r, int c){//kiểm tra va chạm với khối bất kì

        for(int i=0;i<p.length;i++){
            for(int j=0;j<p[i].length;j++){

                if(p[i][j] == 0) continue;//bỏ qua ô trống

                int nr = r + i;//hàng mới
                int nc = c + j;//cột mới

                //Kiểm tra ngoài biên
                if(nr >= rows || nc < 0 || nc >= cols) return true;
                //Kiểm tra de lên block khác
                if(nr >= 0 && board[nr][nc] != 0) return true;
            }
        }

        return false;
    }

    void merge(){//ghép khối vào bảng

        for(int i=0;i<piece.length;i++){
            for(int j=0;j<piece[i].length;j++){
                if(piece[i][j] == 1){
                    board[row + i][col + j] = 1;
                }
            }
        }
    }

    void clearLines(){//xóa cc dòng đầy

        for(int i=0;i<rows;i++){

            boolean full = true;

            for(int j=0;j<cols;j++){
                if(board[i][j] == 0){
                    full = false;
                    break;
                }
            }

            if(full){
                score += 100;//cộng điểm

                //kéo các dòng phía trên xuống
                for(int k=i;k>0;k--){
                    board[k] = board[k-1].clone();
                }

                board[0] = new int[cols];//Dòng trên cùng trống
            }
        }
    }

    int[][] rotateMatrix(int[][] m){//xoay ma trận 90 độ

        int r = m.length;
        int c = m[0].length;

        int[][] res = new int[c][r];

        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                res[j][r-1-i] = m[i][j];
            }
        }

        return res;
    }

    int[][] copy(int[][] a){//sao chép mảng 2D
        int[][] b = new int[a.length][];
        for(int i=0;i<a.length;i++){
            b[i] = a[i].clone();
        }
        return b;
    }

    // ================= GET =================
    public int[][] getBoard(){ return board; }
    public int[][] getPiece(){ return piece; }
    public int getRow(){ return row; }
    public int getCol(){ return col; }
    public int getScore(){ return score; }

    public boolean isGameOver(){ return gameOver; }
    public boolean isWin(){ return win; }

    public long getTimeLeft(){
        long t = System.currentTimeMillis() - startTime;
        return Math.max(0, timeLimit - t);
    }
}