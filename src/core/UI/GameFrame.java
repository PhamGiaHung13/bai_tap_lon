    package core.UI;
    import javax.swing.*;

    import Controller.GameController;
    import core.Audio.SoundManager;
    import core.Logic.Board;

    import java.awt.*;

    //cua so game
    public class GameFrame extends JFrame {

        private CardLayout cardLayout;
        private JPanel mainPanel;
        private MenuPanel menuPanel;
        private GamePanel gamePanel;
        private DifficultyPanel difficultyPanel;
        private SettingsPanel settingsPanel;
        private AchievementsPanel achievementsPanel;
        private GameController gameController;





        ///  --------------- CONSTRUCTOR
        public GameFrame() {
            setTitle("HKL Minesweeper");//----- dat tieu de
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//--- an nut X thoat game(khong cho game chay ngam)

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);

            menuPanel = new MenuPanel(this);
            difficultyPanel = new DifficultyPanel(this);
            settingsPanel = new SettingsPanel(this);
            achievementsPanel = new AchievementsPanel(this);


            mainPanel.add(menuPanel, "MENU");
            mainPanel.add(difficultyPanel, "DIFFICULTY");
            mainPanel.add(settingsPanel, "SETTING");
            mainPanel.add(achievementsPanel, "ACHIEVEMENTS");


            setContentPane(mainPanel);

            pack();// ---- lay size theo component no chua
            setLocationRelativeTo(null);//--- can cua so ra giua man hinh
            setVisible(true);// --- hien thi cua so khong cho no tang hinh
        }






        /// ----------- START GAME
        public void startGame(int mode) {
            SoundManager.stopBGM();
            Board newBoard = new Board(mode);

            if(gamePanel != null){
                mainPanel.remove(gamePanel);
            }

            gamePanel = new GamePanel(newBoard);

            gameController = new GameController(this, gamePanel);

            mainPanel.add(gamePanel, "GAME");
            cardLayout.show(mainPanel, "GAME");

            int tileSize = 40;

            int width = newBoard.columns * tileSize;
            int height = newBoard.rows * tileSize;

            // thêm UI
            width += 120;
            height += 180;

            setSize(width, height);
            setResizable(false);
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



        /// --------- SHOW DIFFICULTY
        public void showDifficulty(){
            cardLayout.show(mainPanel, "DIFFICULTY");
        }

        public void showSetting(){
            cardLayout.show(mainPanel, "SETTING");
        }

        public void showAchievements() {
            cardLayout.show(mainPanel, "ACHIEVEMENTS");
        }

    }
