package Controller;

import core.Audio.SoundManager;
import core.UI.GameFrame;
import core.UI.GamePanel;
import core.Logic.Tile;
import minigames.sudoku.UI.SudokuPanel;
import minigames.maze.UI.MazePanel;
import minigames.blockblast.UI.BlockBlastPanel;
import minigames.chess.UI.ChessPanel;
import javax.swing.*;
import java.awt.*;
import java.util.Random;




public class GameController {
    private GameFrame frame;
    private GamePanel minesPanel;
    private Tile currentBombTile;
    private Random rand = new Random();

    // Lấy ra mainPanel và CardLayout từ Frame để điều khiển
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public GameController(GameFrame frame, GamePanel minesPanel) {
        this.frame = frame;
        this.minesPanel = minesPanel;
        this.minesPanel.setController(this);

        // TRÍCH XUẤT mainPanel TỪ FRAME (Vì ông đặt nó làm ContentPane trong GameFrame)
        this.mainPanel = (JPanel) frame.getContentPane();
        this.cardLayout = (CardLayout) mainPanel.getLayout();
    }

    public void startSecondChance(Tile bombTile, int difficulty) {
        this.currentBombTile = bombTile;
        minesPanel.gameTimer.stop();
        JPanel gameUI = null;

        // Random từ 0 đến 3
        int Number = rand.nextInt(4);

        try {
            switch (Number) {
                case 0:
                    gameUI = new SudokuPanel(difficulty, this);
                    break;
                case 1:
                    gameUI = new MazePanel(difficulty, this);
                    break;
                case 2:
                    // CHÚ Ý: Đảm bảo đúng tên Class và tham số
                    gameUI = new BlockBlastPanel(difficulty, this);
                    break;
                case 3:
                    gameUI = new ChessPanel(difficulty, this);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Minigame: " + e.getMessage());
            // Nếu lỗi game mới, ép quay về Sudoku cho an toàn
        }

        if (gameUI != null) {
            mainPanel.add(gameUI, "MINIGAME");
            cardLayout.show(mainPanel, "MINIGAME");

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setResizable(true);
            mainPanel.revalidate();
            mainPanel.repaint();

            // CỰC KỲ QUAN TRỌNG: BlockBlast và Maze cần cái này để nhận phím
            gameUI.requestFocusInWindow();
        }
    }

    public void onMinigameWin() {
        if (currentBombTile != null) {
            currentBombTile.setRevealed(true);
        }
        backToMines();

    }



    public void onMinigameLose() {

        if (currentBombTile != null) {
            currentBombTile.setExploded(true); // Đánh dấu đây là quả bom gây Game Over
        }

        minesPanel.getBoard().gameOver = true;
        minesPanel.revealAllMines();




        backToMines();
        minesPanel.gameTimer.stop();
        minesPanel.handleFinalExplosion(currentBombTile);

    }



    private void backToMines() {
        // QUAY VỀ CARD "GAME" CÓ SẴN TRONG FRAME
        cardLayout.show(mainPanel, "GAME");

        // Khôi phục kích thước bàn cờ mìn
        frame.setExtendedState(JFrame.NORMAL);
        int tileSize = 40;
        int width = minesPanel.getBoard().columns * tileSize + 120;
        int height = minesPanel.getBoard().rows * tileSize + 180;

        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);


        mainPanel.revalidate();
        mainPanel.repaint();

        minesPanel.gameTimer.start();
        minesPanel.updateUIBoard();
        minesPanel.requestFocusInWindow();
    }
}