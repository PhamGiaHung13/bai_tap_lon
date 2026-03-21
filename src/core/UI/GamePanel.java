package core.UI;

import core.Logic.*;


import javax.swing.*;
import java.awt.*;

//layout choi game
public class GamePanel extends JPanel {

    Board board;
    Events events;
    boolean flagMode = false;
    JButton flagBtn;

    int[] count = {0};//mang 1 phan tu de dep so lan nhap nhay bom

    public GamePanel(Board board){

        this.board = board;
        this.events = new Events(board);
        flagBtn = new JButton("🚩 FLAG");//🚩

        setLayout(new BorderLayout());//layout theo huong

        JPanel toolBar = new JPanel();//tao 1 panel toolbar chua flagBtn, ve sau co the add them thoi gian , so luong min cac thu
        toolBar.setBackground(Color.WHITE);
        toolBar.add(flagBtn);

        flagBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        flagBtn.setFocusPainted(false);
        flagBtn.setBackground(new Color(1,130,180));

        flagBtn.addActionListener(e -> {
            flagMode = !flagMode; // chuyen doi mode dat flag

            if(flagMode) flagBtn.setBackground(Color.GREEN);
            else flagBtn.setBackground(new Color(1,130,180)); //70,130,180

        });

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(board.rows, board.columns));//gridlayout la layout theo ma tran(tuc la cai bang sudoku)

        add(toolBar, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        for(int r = 0; r < board.rows; r++){
            for(int c = 0; c < board.columns; c++){

                Tile tile = board.getTile(r,c);

//                tile.setFocusPainted(false);
//                tile.setContentAreaFilled(false); // Dòng này cực quan trọng để màu setBackground hiện lên chuẩn nhất
//                tile.setOpaque(true);

                tile.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt){
                         tile.setBackground(Color.WHITE);
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt){
                        if(!tile.isRevealed()) tile.setBackground(new JButton().getBackground());
                    }


                });

                tile.addActionListener(e -> {
                    if(flagMode){
                        if(tile.getText().equals("")) tile.setText("🚩");
                        else tile.setText("");
                        return;
                    }

                    Events.ClickResult result = events.clickTile(tile);

                    if(result == Events.ClickResult.MINE){
                        tile.setText("💣"); //  💣
                        new javax.swing.Timer(100,i ->{
                            tile.setBackground(tile.getBackground() == Color.RED ? Color.WHITE : Color.RED);
                            count[0]++;
                            if(count[0] >= 10) ((Timer)i.getSource()).stop();
                        }).start();

                        revealAllMines();
                    }
                    updateUIBoard();
                });

                boardPanel.add(tile);
            }
        }

    }

    public void revealAllMines(){
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);

                if(tile.isMine()){
                    tile.setText("\uD83D\uDCA3");
                    tile.setBackground(Color.RED);
                }

            }
    }

    public void updateUIBoard(){
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++) {
                Tile tile = board.getTile(r, c);

                if(tile.isRevealed()) tile.setEnabled(false);
                int mines = tile.getMinesAround();

                if(mines > 0)
                    tile.setText(String.valueOf(mines));

            }
    }


}