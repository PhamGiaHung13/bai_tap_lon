    package core.UI;
    import javax.swing.*;
    
    import core.Logic.Board;

    import java.awt.*;

    //cua so game
    public class GameFrame extends JFrame {

        private CardLayout cardLayout;
        private JPanel mainPanel;



        public GameFrame() {
            setTitle("HKL Minesweeper");//dat tieu de
            setSize(1200, 700);//kich thuoc cua so
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//an nut X thoat game(khong cho game chay ngam)

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);


            mainPanel.add(new MenuPanel(this), "MENU");


            setLocationRelativeTo(null);//can cua so ra giua man hinh
            setContentPane(mainPanel);
            setVisible(true);//hien thi cua so khong cho no tang hinh
        }

    
        public void startGame(int mode) {
            Board newBoard = new Board(mode);

            GamePanel gamePanel = new GamePanel(newBoard);

            int tileSize = 40;

            int width = newBoard.columns * tileSize + 100;
            int height = newBoard.rows * tileSize + 150;

            setSize(width, height);

            mainPanel.add(gamePanel, "GAME");
            cardLayout.show(mainPanel, "GAME");

        }
    
    }
