package minigames.maze.UI;

import minigames.maze.Logic.MazeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MazePanel extends JPanel {

    private MazeGame game;
    private int cellSize = 30;

    public MazePanel(int difficulty){

        game = new MazeGame(difficulty);

        setPreferredSize(new Dimension(
                game.getCols()*cellSize,
                game.getRows()*cellSize
        ));

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch(e.getKeyCode()){
                    case KeyEvent.VK_UP -> game.move(-1,0);
                    case KeyEvent.VK_DOWN -> game.move(1,0);
                    case KeyEvent.VK_LEFT -> game.move(0,-1);
                    case KeyEvent.VK_RIGHT -> game.move(0,1);
                }

                if(game.isWin()){
                    JOptionPane.showMessageDialog(null,"YOU WIN!");
                }

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        int[][] maze = game.getMaze();

        for(int r=0;r<game.getRows();r++){
            for(int c=0;c<game.getCols();c++){

                if(maze[r][c] == 1){
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }

                g.fillRect(c*cellSize,r*cellSize,cellSize,cellSize);
            }
        }

        // Vẽ đích
        g.setColor(Color.GREEN);
        g.fillOval(
                game.getEndCol()*cellSize,
                game.getEndRow()*cellSize,
                cellSize,cellSize
        );

        // Vẽ player
        g.setColor(Color.RED);
        g.fillOval(
                game.getPlayerCol()*cellSize,
                game.getPlayerRow()*cellSize,
                cellSize,cellSize
        );
    }
}