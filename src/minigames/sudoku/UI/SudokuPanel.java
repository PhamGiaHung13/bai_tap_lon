package minigames.sudoku.UI;

import minigames.sudoku.Logic.SudokuGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SudokuPanel extends JPanel {

    private SudokuGame game;

    private int cellSize = 50;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public SudokuPanel(int difficulty) {
        this.game = new SudokuGame(difficulty);

        int size = game.getSize();
        setPreferredSize(new Dimension(size * cellSize, size * cellSize + 60));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int size = game.getSize();

                if (e.getY() < size * cellSize) {
                    selectedRow = e.getY() / cellSize;
                    selectedCol = e.getX() / cellSize;
                } else if (selectedRow != -1) {

                    int number = e.getX() / cellSize + 1;

                    if (number <= size) {
                        boolean correct = game.move(selectedRow, selectedCol, number);

                        if (!correct) {
                            JOptionPane.showMessageDialog(null, "Sai rồi!");
                        }

                        if (game.isWin()) {
                            JOptionPane.showMessageDialog(null, "Sudoku Complete!");
                        }
                    }
                }

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size = game.getSize();
        int[][] puzzle = game.getPuzzle();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                if (r == selectedRow && c == selectedCol) {
                    g.setColor(new Color(220, 220, 255));
                    g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                }

                g.setColor(Color.BLACK);
                g.drawRect(c * cellSize, r * cellSize, cellSize, cellSize);

                if (puzzle[r][c] != 0) {
                    g.drawString(
                            String.valueOf(puzzle[r][c]),
                            c * cellSize + cellSize / 2 - 4,
                            r * cellSize + cellSize / 2 + 5
                    );
                }
            }
        }

        // number picker
        for (int i = 0; i < size; i++) {
            g.drawRect(i * cellSize, size * cellSize, cellSize, cellSize);
            g.drawString(
                    String.valueOf(i + 1),
                    i * cellSize + cellSize / 2 - 4,
                    size * cellSize + cellSize / 2 + 5
            );
        }
    }
}
