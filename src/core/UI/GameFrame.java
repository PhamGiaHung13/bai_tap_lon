    package core.UI;
    import javax.swing.*;

    import Controller.GameController;
    import DB.GameDAO;
    import DB.Player;
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


        private Player currentPlayer;
        private GameDAO gameDAO = new GameDAO();

        public void initLogin() {
            // Hiện cái bảng nhỏ cho người ta nhập tên
            String name = JOptionPane.showInputDialog(
                    null,
                    "Welcome to HKL Minesweeper!\nEnter your name bro:",
                    "Login",
                    JOptionPane.QUESTION_MESSAGE
            );

            // Nếu người dùng bấm Cancel hoặc để trống, cho tên mặc định là Guest
            if (name == null || name.trim().isEmpty()) {
                name = "Guest_" + System.currentTimeMillis() % 1000;
            }

            // Gọi DAO để kiểm tra: Nếu có tên rồi thì lấy ID, chưa có thì tạo mới
            this.currentPlayer = gameDAO.getOrCreatePlayer(name);

            if (this.currentPlayer != null) {
                System.out.println("Log in Successful: " + currentPlayer.getUsername() + " (ID: " + currentPlayer.getId() + ")");
            }
        }

        ///  --------------- CONSTRUCTOR
        public GameFrame() {
            initLogin();

            setTitle("HKL Minesweeper");//----- dat tieu de
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//--- an nut X thoat game(khong cho game chay ngam)

            try {
                // Cách này giúp Java tìm ảnh ngay trong classpath
                java.net.URL iconURL = getClass().getResource("/core/Image/favmine.png");
                if (iconURL != null) {
                    ImageIcon icon = new ImageIcon(iconURL);
                    this.setIconImage(icon.getImage());
                }
            } catch (Exception e) {
                System.out.println("Không nạp được favicon: " + e.getMessage());
            }


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

            menuPanel = new MenuPanel(this);
            difficultyPanel = new DifficultyPanel(this);
            settingsPanel = new SettingsPanel(this);
            achievementsPanel = new AchievementsPanel(this, currentPlayer);

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

            gamePanel = new GamePanel(newBoard, currentPlayer) ;

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

        public void showAchievements(){
            cardLayout.show(mainPanel, "ACHIEVEMENTS");
        }


    }
