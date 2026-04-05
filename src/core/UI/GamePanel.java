package core.UI;

import Controller.GameController;
import DB.GameDAO;
import DB.Player;
import core.Audio.SoundManager;
import core.Logic.*;
import Controller.GameController;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import static javax.swing.BorderFactory.createEmptyBorder;


///   ------- LAYOUT PLAY GAME
public class GamePanel extends JPanel {

    Board board;
    Events events;
    ImageIcon bombIcon;
    private ImageIcon flagIcon;
    private JPanel boardPanel;
    private int tileSize;
    JLabel minesLabel;
    JButton faceBtn;
    ImageIcon smileIcon, deadIcon, winIcon, surpriseIcon = getScaledIcon("src/Image/surprise.png", 40);
    JLabel timerLabel;
    int time = 0;
    Timer gameTimer;
    boolean started = false;
    boolean isHolding = false;
    GameController controller;
    private Player currentPlayer; // Thêm biến này



/// ---------- CONSTRUCTOR
    public GamePanel(Board board, Player player) {


        ///--------------- LAYOUT GAMEPANEL
        this.board = board;
        this.events = new Events(board);
        currentPlayer = player;
        setLayout(new BorderLayout());//layout theo huong (N W S E)
        this.setBackground(new Color(192, 192, 192));
        this.setBorder(BorderFactory.createCompoundBorder(
                createThickFrame(true, 6, Color.WHITE, new Color(128,128,128)),
                createEmptyBorder(20, 20, 20, 20)
        ));

        setPreferredSize(new Dimension(
                board.columns * 40 + 100,
                board.rows * 40 + 150
        ));




        // ---------- 1.LABEL SO MIN CON LAI
        minesLabel = new JLabel("000");
        minesLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        minesLabel.setForeground(Color.RED);
        minesLabel.setBackground(Color.black);
        minesLabel.setOpaque(true);
        minesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minesLabel.setPreferredSize(new Dimension(100, 50));
        minesLabel.setBorder(BorderFactory.createCompoundBorder(createThickFrame(false, 3, new Color(128,128,128), Color.WHITE)
                , createEmptyBorder(10, 0, 0, 0))
        );




        //------- 2.BUTTON FACE ICON
        faceBtn = new JButton();
        faceBtn.setFocusPainted(false);
        faceBtn.setBackground(new Color(192, 192, 192));
        faceBtn.setBorder(createThickFrame(true, 4, Color.WHITE, new Color(128,128,128)));
        faceBtn.setPreferredSize(new Dimension(200, 60));

        smileIcon = getScaledIcon("src/Image/smile.png", 40);
        deadIcon = getScaledIcon("src/Image/die.png", 40);
        winIcon = getScaledIcon("src/Image/cool.png", 40);

        faceBtn.setIcon(smileIcon);
        faceBtn.addActionListener(e -> {
            SoundManager.play("src/core.Sound/tunetank.com_interface-cursor-click.wav");
            faceBtn.setBorder(createThickFrame(false, 3, new Color(128,128,128) , Color.WHITE));
            if(events != null){
                events.stopAllTimers();
            }
            new Timer(100, ev ->{
                restartGame();
                faceBtn.setBorder(createThickFrame(true, 4, Color.WHITE, new Color(128,128,128)));
                ((Timer) ev.getSource()).stop();
            }).start();
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(faceBtn);




        //------ 3.TIMER
        timerLabel = new JLabel("000");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        timerLabel.setForeground(Color.RED);
        timerLabel.setBackground(Color.BLACK);
        timerLabel.setOpaque(true);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setPreferredSize(new Dimension(100, 50));
        timerLabel.setBorder(BorderFactory.createCompoundBorder(createThickFrame(false, 3 , new Color(128,128,128) ,Color.WHITE)
                , createEmptyBorder(10, 0, 0, 0))
        );

        gameTimer = new Timer(1000, e -> {
            time++;
            timerLabel.setText(String.format("%03d", time));
        });







        /// ---------- TOOLBAR PANEL
        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setBackground(new Color(192, 192, 192));
        toolBar.setPreferredSize(new Dimension(0, 90));
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        createThickFrame(false, 6, new Color(128,128,128), Color.WHITE),
                        createEmptyBorder(10, 10, 10, 10)
                ),
                createEmptyBorder(0, 0, 0, 0)
        ));


        toolBar.add(minesLabel, BorderLayout.WEST);
        toolBar.add(centerPanel, BorderLayout.CENTER);
        toolBar.add(timerLabel, BorderLayout.EAST);



        ///---------- layout BOARD (gridlayout la layout theo ma tran)
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(board.rows, board.columns));
        boardPanel.setBackground(new Color(192, 192, 192));
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                createEmptyBorder(20, 0, 0, 0), // khoang cach
                createThickFrame(false, 6 , new Color(128,128,128) ,Color.WHITE) // vien lom 6px
        ));


        //-------- SAP XEP VI TRI BOARD PANEL VA TOOLBAR PANEL
        add(toolBar, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);



        //-------- LISTENER RESIZE TILE THEO BOARD
        boardPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateTileSize();
            }
        });



        ///------- FUNCTION PLAY GAME
        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.columns; c++) {

                Tile tile = board.getTile(r, c);

                setFocusable(false);
                tile.setFocusPainted(false);
                tile.setOpaque(true);
                tile.setBackground(new Color(192,192,192));


                // ----------------- GAMEPLAY
                addTileListener(tile);
                boardPanel.add(tile);
            }
        }




        // ------ ESC
    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ESCAPE"), "pauseGame"
    );

    getActionMap().put("pauseGame", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SoundManager.play("src/core.Sound/pause_menu.wav");
            showPauseMenu();
        }
    });



        /// ---------
    }




    ///  ----------------- TILE LISTENER
    private void addTileListener(Tile tile){

        /// ------ MOUSE LISTENER - GAMEPLAY
        tile.addMouseListener(new java.awt.event.MouseAdapter(){


            //-------- HOLD MOUSE
            @Override
            public void mousePressed(java.awt.event.MouseEvent e){
                if(board.gameOver || board.isWin()) return;

                // --------- FACE EMOTION (LEFT CLICK)
                if(SwingUtilities.isLeftMouseButton(e)){
                    isHolding = true;
                    faceBtn.setIcon(surpriseIcon);
                    handlePreview(tile);
                }

                //--- PUT FLAG (RIGHT CLICK)
                if(SwingUtilities.isRightMouseButton(e)) {
                    if(tile.isRevealed()) return;
                    SoundManager.play("src\\core.Sound\\flag_melee_var1_02.wav");
                    tile.setFlagged(!tile.isFlagged());
                    updateUIBoard();
                }
            }

            //----------- RELEASE MOUSE
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e){
                isHolding = false;
                updateFace(); // ------- TRA VE TRANG THAI FACE
                clearPreview();// ------- CLEAR PREVIEW
            }

            //--------- HOVER (in and out)
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt){
                if(board.gameOver) return;

                if(isHolding){
                    handlePreview(tile);
                }else {
                    if (!tile.isRevealed()) {
                        tile.setBackground(new Color(210, 210, 210));
                    }
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt){
                if(board.gameOver) return;
                if(!tile.isRevealed()){
                    tile.setBackground(new Color(192,192,192));
                }
            }
        });

        //------- ACTION LISTENER (click)
        tile.addActionListener(e -> {
//            gameTimer.start();

            // ----- VO HIEU HOA  CLICK
            if(board.gameOver || tile.isFlagged() || board.isWin()){
                return;
            }



            if(!started){
                gameTimer.start();
                started = true;
            }

            //----- LEFT CLICK
            Events.ClickResult result = events.clickTile(tile, () -> {
                updateUIBoard();

                //----- WIN
                if(board.isWin()){
                    gameTimer.stop();

                    int currentDiff = board.getDifficulty();
                    int exp = currentDiff * 20;
                    int coins = currentDiff * 5;
                    int mastery = 1;


                    SoundManager.playBGM("src/core.Sound/BGM_WIN.wav");
                    playWinAnimation(()->{
                        showWinDialog(exp, coins , mastery);
                    });
                    handleWin();
                }
            });

            //---------- HIEU UNG BOM NHAP NHAY
            int[] count = {0};  //  -----  mang 1 phan tu de dem so lan nhap nhay bom
            if(result == Events.ClickResult.MINE){
                gameTimer.stop();

                if(events != null){
                    events.stopAllTimers();
                }

                SoundManager.play("src/core.Sound/DRAGONBOMB.wav");

                new javax.swing.Timer(100,i ->{
                    tile.setBackground(tile.getBackground() == Color.RED ? Color.WHITE : Color.RED);
                    count[0]++;
                    if(count[0] > 10) ((Timer)i.getSource()).stop();
                }).start();
                SoundManager.playBGM("src/core.Sound/BGM_lose.wav");


                revealAllMines();// ---- hien tat ca bom khi thua
            }
            updateUIBoard();// --- cap nhat board
        });
    }





    ///  ---------- UPDATE FACE
    private void updateFace(){
        if(board.gameOver) faceBtn.setIcon(deadIcon);
        else if(board.isWin()) faceBtn.setIcon(winIcon);
        else faceBtn.setIcon(smileIcon);
    }







    /// -----------  DRAW BORDER  --------
    private Border createThickFrame(boolean raised, int thickness, Color Top, Color Bottom) {
        return new javax.swing.border.AbstractBorder(){

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int t = thickness; // border thickness


                Color topColor, leftColor, bottomColor, rightColor;

                if (raised) {
                    topColor = leftColor = Top;
                    bottomColor = rightColor = Bottom;
                } else {
                    topColor = leftColor = Top;
                    bottomColor = rightColor = Bottom;
                }

                // ---VE CANH TREN( hinh thang) ---
                g2.setColor(topColor);
                g2.fillPolygon(new int[]{x, x + w, x + w - t, x + t},
                        new int[]{y, y, y + t, y + t}, 4);

                // --- VE CANH TRAI (Hinh thang) ---
                g2.setColor(leftColor);
                g2.fillPolygon(new int[]{x, x + t, x + t, x},
                        new int[]{y, y + t, y + h - t, y + h}, 4);

                // --- VE CANH DUOI (Hinh thang) ---
                g2.setColor(bottomColor);
                g2.fillPolygon(new int[]{x, x + t, x + w - t, x + w},
                        new int[]{y + h, y + h - t, y + h - t, y + h}, 4);

                // --- VE CANH PHAI (hinh thang) ---
                g2.setColor(rightColor);
                g2.fillPolygon(new int[]{x + w, x + w - t, x + w - t, x + w},
                        new int[]{y, y + t, y + h - t, y + h}, 4);


            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(6, 6, 6, 6);
            }
        };
    }





///---------- UPDATE MINELEFT ----
    private void updateMinesCounter(){
        int flags = 0;
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++)
                if(board.getTile(r,c).isFlagged()) flags++;
        int mineLeft = board.minesCount - flags;
        minesLabel.setText(String.format("%03d", mineLeft));
    }



    ///-------------- SCALE ICON (THAY DOI KICH THUOC)
    private ImageIcon getScaledIcon(String path, int size){
        Image img = new ImageIcon(path).getImage();
        Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }



    /// --------- SET TILE SIZE THEO BOARD PANEL SIZE
    private void updateTileSize(){
        int w = boardPanel.getWidth();
        int h = boardPanel.getHeight();

        tileSize = Math.min(w / board.columns,
                h / board.rows);

        // update icon
        bombIcon = getScaledIcon("src/Image/trump.png", tileSize);
        flagIcon = getScaledIcon("src/Image/flag.png", tileSize);

        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);

                tile.setFont(new Font("Segoe UI", Font.BOLD, 3 *tileSize / 4));
            }
        updateUIBoard();
    }






    /// ------- HANDLE PREVIEW TILE
    private java.util.List<Tile> previewTiles = new ArrayList<>();
    private void handlePreview(Tile tile){
        if(board.gameOver) return;
        clearPreview();

        if(!tile.isRevealed()){
            tile.setBorder(createThickFrame(false, 4, new Color(128,128,128) , Color.WHITE));
            tile.setBackground(new Color(220,220,220));
            previewTiles.add(tile);
            return;
        }

        if(tile.getMinesAround() > 0){
            for(int i=-1;i<=1;i++)
                for(int j=-1;j<=1;j++)
                    if(i!=0 || j!=0){
                        int nr = tile.row + i;
                        int nc = tile.col + j;

                        if(nr>=0 && nr< board.rows && nc>=0 && nc< board.columns)
                            if(!board.getTile(nr, nc).isRevealed()){
                                board.getTile(nr, nc).setBorder(createThickFrame(false, 4, new Color(128,128,128) , Color.WHITE));
                                board.getTile(nr, nc).setBackground(new Color(220,220,220));
                                previewTiles.add(board.getTile(nr, nc));
                            }
                    }
        }
    }





    ///  --------- CLEAR PREVIEW
    private void clearPreview(){
        for(Tile t : previewTiles){
            if(!t.isRevealed()){
                t.setBorder(createThickFrame(true, 5, Color.WHITE, new Color(128,128,128)));
                t.setBackground(new Color(192,192,192));
            }
        }
        previewTiles.clear();
    }





    ///-------------  REVEAL ALL BOMB
    public void revealAllMines(){
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);

                    if(tile.isMine() && !tile.isFlagged()){
                        tile.setRevealed(true);
                }
            }
        updateUIBoard();
    }




    /// ----------  COLOR NUMBER
    private Color getColor(int mines){
        switch(mines){
            case 1: return Color.BLUE;
            case 2: return new Color(0,128,0);
            case 3: return Color.RED;
            case 4: return Color.MAGENTA;
            case 5: return new Color(128,0,0);
            case 6: return new Color(0,128,128);
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }






    ///  ------- WIN DIALOG WIN
    private void showWinDialog(int exp, int coins, int mastery) {
        JDialog winDialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "VICTORY",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        winDialog.setUndecorated(true);
        winDialog.setSize(400, 350);
        winDialog.setLocationRelativeTo(this);
        winDialog.setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        // --- 1. HEADER
        JLabel lblStatus = new JLabel("VICTORY!", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 45));
        lblStatus.setForeground(new Color(220, 208, 48));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));

        // --- 2. CENTER
        JPanel centerContent = new JPanel(new GridLayout(4, 1, 5, 5));
        centerContent.setOpaque(false);

        JLabel lblStats = new JLabel("Time: " + timerLabel.getText() + "s", SwingConstants.CENTER);
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblStats.setForeground(new Color(80, 80, 80));

        centerContent.add(lblStats);
        centerContent.add(new JLabel("EXP: +" + exp, SwingConstants.CENTER) {{ setFont(new Font("Segoe UI", Font.BOLD, 18)); setForeground(new Color(50, 150, 50)); }});
        centerContent.add(new JLabel("Coins: +" + coins, SwingConstants.CENTER) {{ setFont(new Font("Segoe UI", Font.BOLD, 18)); setForeground(new Color(200, 150, 0)); }});
        centerContent.add(new JLabel("Mastery: +" + mastery, SwingConstants.CENTER) {{ setFont(new Font("Segoe UI", Font.BOLD, 18)); setForeground(new Color(138, 43, 226)); }});

        // --- 3. SOUTH: BUTTONS
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setOpaque(false);

        JButton backToGameBtn = createMenuButton("Back");
        JButton menuBtn = createMenuButton("Menu");

        backToGameBtn.addActionListener(e -> {
            winDialog.dispose();

        });

        menuBtn.addActionListener(e -> {
            SoundManager.play("src/core.Sound/tunetank.com_interface-cursor-click.wav");

            winDialog.dispose();
            Window window = SwingUtilities.getWindowAncestor(this);
            if(window instanceof GameFrame) ((GameFrame) window).showMenu();
            SoundManager.playBGM("src/core.Sound/music.wav");

        });

        btnPanel.add(backToGameBtn);
        btnPanel.add(menuBtn);

        mainPanel.add(lblStatus, BorderLayout.NORTH);
        mainPanel.add(centerContent, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        winDialog.setContentPane(mainPanel);
        winDialog.setVisible(true);
    }




    /// ---------- WIN ANIMATION
    private void playWinAnimation(Runnable onComplete){
        Timer winTimer = new Timer(120, null);

        final boolean[] toggle = {false};
        final int[] count = {0};

        winTimer.addActionListener(e -> {
            toggle[0] = !toggle[0];

            for(int r = 0; r < board.rows; r++){
                for(int c = 0; c < board.columns; c++){
                    Tile tile = board.getTile(r, c);
                    if(tile.isRevealed()){

                        if (toggle[0]) tile.setBackground(Color.CYAN);
                        else tile.setBackground(Color.green);
                    }
                }
            }

            count[0]++;
            if(count[0] >= 20) {
                winTimer.stop();
                updateUIBoard();

                if(onComplete != null) {
                    onComplete.run();
                }
            }
        });
        winTimer.start();
    }






    /// --------- MENU BUTTON
    private JButton createMenuButton(String text){
        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(new Color(60, 60, 60));

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // hover + click effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {


            public void mouseEntered(java.awt.event.MouseEvent e){
                SoundManager.play("src/core.Sound/menu_hover.wav");

                btn.setForeground(new Color(220, 208, 48));
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setForeground(new Color(60, 60, 60));
            }

        });

        return btn;
    }





    ///  --------- PAUSE MENU

    private void showPauseMenu() {
        gameTimer.stop();
        JDialog pauseDialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Paused",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        pauseDialog.setUndecorated(true); // Bỏ viền Windows cho đồng bộ với Win Dialog
        pauseDialog.setSize(350, 400);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setBackground(new Color(0, 0, 0, 0)); // Nền trong suốt để bo góc

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền trắng mờ (Glassmorphism)
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);

            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        // Tiêu đề PAUSED
        JLabel Title = new JLabel("PAUSED", SwingConstants.CENTER);
        Title.setFont(new Font("Arial", Font.BOLD, 40));
        Title.setForeground(new Color(220, 208, 48));
        Title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Panel chứa các nút bấm
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));

        JButton resumeBtn = createMenuButton("Resume");
        JButton menuBtn   = createMenuButton("Main Menu");
        JButton exitBtn   = createMenuButton("Exit");

        // Action cho các nút
        resumeBtn.addActionListener(e -> {
            SoundManager.play("src/core.Sound/tunetank.com_interface-cursor-click.wav");
            gameTimer.start();
            pauseDialog.dispose();
        });

        menuBtn.addActionListener(e -> {
            SoundManager.play("src/core.Sound/tunetank.com_interface-cursor-click.wav");

            if(events != null){
                events.stopAllTimers();
            }

            pauseDialog.dispose();
            Window window = SwingUtilities.getWindowAncestor(this);
            if(window instanceof GameFrame){
                GameFrame frame = (GameFrame) window;
                frame.showMenu();
                SoundManager.playBGM("src/core.Sound/music.wav");
                frame.pack();
                frame.setLocationRelativeTo(null);
            }
        });

        exitBtn.addActionListener(e -> {
            SoundManager.play("src/core.Sound/tunetank.com_interface-cursor-click.wav");
            System.exit(0);
        });

        btnPanel.add(resumeBtn);
        btnPanel.add(menuBtn);
        btnPanel.add(exitBtn);

        mainPanel.add(Title, BorderLayout.NORTH);
        mainPanel.add(btnPanel, BorderLayout.CENTER);

        pauseDialog.setContentPane(mainPanel);
        pauseDialog.setVisible(true);
    }





    /// ---------- UPDATE BOARD
    public void updateUIBoard(){
        updateFace();
        updateMinesCounter();

        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);


                //---- RESET TILE
                tile.setIcon(null);
                tile.setText("");


                // ------- IF TILE IS OPENED
                if(tile.isRevealed()){


                    //--- IF TILE IS MINE
                    if(tile.isMine()){

                        tile.setIcon(bombIcon);
                        tile.setBorder(BorderFactory.createLineBorder(Color.gray));
                        if (!tile.isExploded())
                            tile.setBackground(Color.LIGHT_GRAY);

                    //---- IF TILE NOT MINE
                    } else{
                        tile.setBorder(BorderFactory.createLineBorder(Color.gray));
                        tile.setBackground(new Color(220,220,220));

                        int mines = tile.getMinesAround();

                        if(mines > 0){
                            tile.setForeground(getColor(mines));
                            tile.setText(String.valueOf(mines));
                        }
                    }
                }


                //---------- IF TILE IS CLOSING
                else{
                    tile.setBorder(createThickFrame(true, 5, Color.WHITE, new Color(128, 128,128)));
                    if(tile.isFlagged()) {
                        tile.setIcon(flagIcon);
                        if (board.gameOver && !tile.isMine()) {
                            tile.setBackground(new Color(240, 70, 70));
                            tile.setBorder(createThickFrame(true, 5, new Color(240, 100, 100), new Color(195, 25,25)));
                        }

                    }
                }

            }
        boardPanel.revalidate();// ---- tinh toan lai bo cuc
        boardPanel.repaint();// ---- ve lai mau sac hinh anh
    }





    ///  --------- CONNECT CONTROLLER
    public Board getBoard() {
        return this.board;
    }


    // Hàm cực quan trọng để nối Controller
    public void setController(GameController controller) {
        this.controller = controller;
        if(this.events != null) {
            this.events.setController(controller);
        }
    }


    /// --------- RESTART GAME
    public void restartGame() {
        SoundManager.stopBGM();
        this.board = new Board(board.rows, board.columns, board.minesCount);
        this.events = new Events(board);

        if(events != null){
            events.stopAllTimers();
        }

        // NỐI LẠI CONTROLLER KHI CHƠI VÁN MỚI
        if (this.controller != null) {
            this.events.setController(this.controller);
        }

        gameTimer.stop();
        time = 0;
        started = false;
        timerLabel.setText("000");
        faceBtn.setIcon(smileIcon);
        boardPanel.removeAll();

        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.columns; c++) {

                Tile tile = board.getTile(r, c);
                tile.setBackground(new Color(192,192,192));

                setFocusable(false);
                tile.setFocusPainted(false);
                tile.setOpaque(true);

                addTileListener(tile);
                boardPanel.add(tile);
            }
        }
        updateTileSize();
        updateUIBoard();
    }





    ///  --------- DATABASE


    private void handleWin() {
        gameTimer.stop();

        // Lấy thông số từ board của ông
        int currentDiff = board.getDifficulty();
        double finalTime = (double) time;

        // Tính toán thưởng dựa trên level (ví dụ thôi, ông tự chỉnh nhé)
        int exp = currentDiff * 20;
        int coins = currentDiff * 5;
        int mastery = 1;
        System.out.println("Player ID: " + currentPlayer.getId());
        GameDAO dao = new GameDAO();
        dao.saveGameResult(currentPlayer.getId(), finalTime, exp, coins, mastery, currentDiff);

    }


    /// ------------- THUA MINIGAME THI CHAY CAI NAY
    public void handleFinalExplosion(Tile fatalBomb) {
        if (fatalBomb == null) return;

        // Chạy âm thanh nổ
        SoundManager.play("src/core.Sound/DRAGONBOMB.wav");

        // Timer nhấp nháy
        Timer flashTimer = new Timer(100, null);
        final int[] count = {0};

        flashTimer.addActionListener(e -> {
            // Đảo màu giữa Đỏ và Trắng
            if (count[0] % 2 == 0) {
                fatalBomb.setBackground(Color.RED);
                fatalBomb.setRevealed(true); // Đảm bảo nó hiện icon bom
            } else {
                fatalBomb.setBackground(Color.WHITE);
            }

            updateUIBoard(); // Vẽ lại để cập nhật màu
            count[0]++;

            if (count[0] >= 10) {
                ((Timer) e.getSource()).stop();
                fatalBomb.setBackground(Color.RED); // Chốt hạ màu đỏ
                revealAllMines(); // Sau khi nhấp nháy xong thì hiện toàn bộ bom còn lại
                updateUIBoard();
            }
        });

        flashTimer.start();
        SoundManager.playBGM("src/core.Sound/BGM_lose.wav");
    }


}