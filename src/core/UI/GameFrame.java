    package core.UI;
    import javax.swing.*;

    import core.Logic.Board;

    import java.awt.*;

    //cua so game
    public class GameFrame extends JFrame {

        private CardLayout cardLayout;
        private JPanel mainPanel;
        private MenuPanel menuPanel;
        private GamePanel gamePanel;






        ///  --------------- CONSTRUCTOR
        public GameFrame() {
            setTitle("HKL Minesweeper");//dat tieu de
//            setSize(1200, 700);//kich thuoc cua so
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//an nut X thoat game(khong cho game chay ngam)

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);

            menuPanel = new MenuPanel(this);
            mainPanel.add(menuPanel, "MENU");
            setContentPane(mainPanel);

            pack();
            setLocationRelativeTo(null);//can cua so ra giua man hinh
            setVisible(true);//hien thi cua so khong cho no tang hinh
        }






        /// ----------- START GAME
        public void startGame(int mode) {

            Board newBoard = new Board(mode);

            if(gamePanel != null){
                mainPanel.remove(gamePanel);
            }

            gamePanel = new GamePanel(newBoard);

            mainPanel.add(gamePanel, "GAME");
            cardLayout.show(mainPanel, "GAME");


            int tileSize = 40;

            int width = newBoard.columns * tileSize + 150;
            int height = newBoard.rows * tileSize + 150;

            setSize(width, height);   // ------ size board theo tile

            setLocationRelativeTo(null);
        }


        /// --------- SHOW MENU
        public void showMenu(){

            if(gamePanel != null){
                mainPanel.remove(gamePanel);
                gamePanel = null;
            }

            cardLayout.show(mainPanel, "MENU");

            mainPanel.revalidate();
            mainPanel.repaint();

            pack();
            setLocationRelativeTo(null);
        }
    
    }
