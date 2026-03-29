package minigames.chess.UI;

import minigames.chess.Logic.ChessPuzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessPanel extends JPanel {

    private ChessPuzzle puzzle;
    private int cellSize = 70;

    private int selectedRow = -1;
    private int selectedCol = -1;

    // chỉ demo 1 quân hậu trắng
    private int queenRow = 6; // g7
    private int queenCol = 6;

    public ChessPanel(int difficulty){

        puzzle = new ChessPuzzle(difficulty);

        setPreferredSize(new Dimension(8*cellSize, 8*cellSize));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;

                if(selectedRow == -1){
                    // chọn quân
                    if(row == queenRow && col == queenCol){
                        selectedRow = row;
                        selectedCol = col;
                    }
                }
                else{
                    // đi quân
                    String from = toChess(selectedRow, selectedCol);
                    String to = toChess(row, col);

                    boolean correct = puzzle.move(from, to);

                    if(correct){
                        queenRow = row;
                        queenCol = col;

                        if(puzzle.isSolved()){
                            JOptionPane.showMessageDialog(null,"CHECKMATE!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Sai nước! Reset!");
                        puzzle.reset();
                        queenRow = 6;
                        queenCol = 6;
                    }

                    selectedRow = -1;
                    selectedCol = -1;
                }

                repaint();
            }
        });
    }

    private String toChess(int row,int col){
        char file = (char)('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        for(int r=0;r<8;r++){
            for(int c=0;c<8;c++){

                if((r+c)%2==0)
                    g.setColor(new Color(240,217,181));
                else
                    g.setColor(new Color(181,136,99));

                g.fillRect(c*cellSize,r*cellSize,cellSize,cellSize);
            }
        }

        // highlight
        if(selectedRow != -1){
            g.setColor(Color.YELLOW);
            g.drawRect(selectedCol*cellSize,selectedRow*cellSize,cellSize,cellSize);
        }

        // vẽ hậu
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,40));
        g.drawString("Q",
                queenCol*cellSize + cellSize/3,
                queenRow*cellSize + cellSize/2 + 10);
    }
}