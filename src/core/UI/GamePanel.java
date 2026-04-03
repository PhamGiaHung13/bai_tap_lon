package core.UI;

import core.Logic.*;


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



/// ---------- CONSTRUCTOR
    public GamePanel(Board board) {


        ///--------------- LAYOUT GAMEPANEL
        this.board = board;
        this.events = new Events(board);
        setLayout(new BorderLayout());//layout theo huong (N W S E)
        this.setBackground(new Color(192, 192, 192));
        this.setBorder(BorderFactory.createCompoundBorder(
                createThickFrame(true, 6),
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
        minesLabel.setBorder(BorderFactory.createCompoundBorder(createThickFrame(false, 3)
                , createEmptyBorder(5, 0, 0, 0))
        );




        //------- 2.BUTTON FACE ICON
        faceBtn = new JButton();
        faceBtn.setFocusPainted(false);
        faceBtn.setBackground(new Color(192, 192, 192));
        faceBtn.setBorder(createThickFrame(true, 4));
        faceBtn.setPreferredSize(new Dimension(200, 60));

        smileIcon = getScaledIcon("src/Image/smile.png", 40);
        deadIcon = getScaledIcon("src/Image/die.png", 40);
        winIcon = getScaledIcon("src/Image/cool.png", 40);

        faceBtn.setIcon(smileIcon);
        faceBtn.addActionListener(e -> {
            faceBtn.setBorder(createThickFrame(false, 4));
            new Timer(100, ev ->{
                restartGame();
                faceBtn.setBorder(createThickFrame(true, 4));
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
        timerLabel.setBorder(BorderFactory.createCompoundBorder(createThickFrame(false, 3)
                , createEmptyBorder(5, 0, 0, 0))
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
                        createThickFrame(false, 6), // Viền lõm của Toolbar
                        createEmptyBorder(10, 10, 10, 10) // Padding trong Toolbar
                ),
                createEmptyBorder(0, 0, 0, 0) // Margin DUOI
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
                createThickFrame(false, 6) // vien lom 6px
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

                tile.setFocusPainted(false);
                tile.setOpaque(true);

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
                if(board.gameOver) return;

                // --------- FACE EMOTION (LEFT CLICK)
                if(SwingUtilities.isLeftMouseButton(e)){
                    isHolding = true;
                    faceBtn.setIcon(surpriseIcon);
                    handlePreview(tile);
                }

                //--- PUT FLAG (RIGHT CLICK)
                if(SwingUtilities.isRightMouseButton(e)) {
                    if(tile.isRevealed()) return;
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

            if(!started){
                gameTimer.start();
                started = true;
            }

            if(board.gameOver || tile.isFlagged()){
                return;
            }

            //----- LEFT CLICK
            Events.ClickResult result = events.clickTile(tile, () -> {
                updateUIBoard();

                //----- WIN
                if(board.isWin()){
                    gameTimer.stop();
                    playWinAnimation();
                    JOptionPane.showMessageDialog(this, "YOU WIN!");
                }
            });

            //---------- HIEU UNG BOM NHAP NHAY
            int[] count = {0};  //  -----  mang 1 phan tu de dem so lan nhap nhay bom
            if(result == Events.ClickResult.MINE){
                gameTimer.stop();

                new javax.swing.Timer(100,i ->{
                    tile.setBackground(tile.getBackground() == Color.RED ? Color.WHITE : Color.RED);
                    count[0]++;
                    if(count[0] > 10) ((Timer)i.getSource()).stop();
                }).start();

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
    private Border createThickFrame(boolean raised, int thickness) {
        return new javax.swing.border.AbstractBorder() {

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int t = thickness; // border thickness

                Color light = Color.WHITE;
                Color dark  = new Color(128, 128, 128);
                Color topColor, leftColor, bottomColor, rightColor;

                if (raised) {
                    topColor = leftColor = light;
                    bottomColor = rightColor = dark;
                } else {
                    topColor = leftColor = dark;
                    bottomColor = rightColor = light;
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

                // --- Ve them cac duong chi o cac khop noi
                if(raised)
                    g2.setColor(light);
                else
                    g2.setColor(dark);

                g2.drawLine(x, y, x + t, y + t); // top-left
                g2.drawLine(x + w, y, x + w - t, y + t); // top-right

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

                tile.setFont(new Font("Segoe UI", Font.BOLD, tileSize / 3));
            }
        updateUIBoard();
    }






    /// ------- HANDLE PREVIEW TILE
    private java.util.List<Tile> previewTiles = new ArrayList<>();
    private void handlePreview(Tile tile){
        if(board.gameOver) return;
        clearPreview();

        if(!tile.isRevealed()){
            tile.setBorder(createThickFrame(false, 4));
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
                                board.getTile(nr, nc).setBorder(createThickFrame(false, 4));
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
                t.setBorder(createThickFrame(true, 5));
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




    /// --------- FACE BUTTON RESTART GAME
    private void restartGame(){
        this.board = new Board(board.rows, board.columns, board.minesCount);
        this.events = new Events(board);

        gameTimer.stop();
        time = 0;
        started = false;
        timerLabel.setText("000");

        faceBtn.setIcon(smileIcon);

        boardPanel.removeAll();

        for(int r = 0; r < board.rows; r++){
            for(int c = 0; c < board.columns; c++){

                Tile tile = board.getTile(r,c);

                tile.setFocusPainted(false);
                tile.setOpaque(true);

                addTileListener(tile);
                boardPanel.add(tile);
            }
        }
        updateUIBoard();

    }





    /// ---------- WIN ANIMATION
    private void playWinAnimation(){
        Timer winTimer = new Timer(120, null);

        final boolean[] toggle = {false};
        final int[] count = {0};

        winTimer.addActionListener(e -> {
            toggle[0] = !toggle[0];

            for(int r = 0; r < board.rows; r++){
                for(int c = 0; c < board.columns; c++){
                    Tile tile = board.getTile(r, c);

                    if(toggle[0]) tile.setBackground(Color.CYAN);
                    else tile.setBackground(Color.green);
                }
            }

            count[0]++;
            if(count[0] >= 10) {
                winTimer.stop();
                updateUIBoard();
            }
        });
        winTimer.start();
    }






    /// --------- MENU BUTTON
    private JButton createMenuButton(String text){
        JButton btn = new JButton(text);
        btn.setBackground(new Color(192, 192, 192));
        btn.setFocusPainted(false);
        btn.setBorder(createThickFrame(true, 1));
        return btn;
    }





    ///  --------- PAUSE MENU

    private void showPauseMenu(){
        gameTimer.stop();
        JDialog pauseDialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Paused",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        pauseDialog.setSize(300, 300);
        pauseDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.
//                createThickFrame(false, 6),
                createEmptyBorder(20, 20, 20, 20));


        JButton resumeBtn = createMenuButton("Resume");
        JButton menuBtn   = createMenuButton("Main Menu");
        JButton exitBtn   = createMenuButton("Exit");


        panel.add(resumeBtn);
        panel.add(menuBtn);
        panel.add(exitBtn);

        pauseDialog.setContentPane(panel);

        resumeBtn.addActionListener(e -> {
            gameTimer.start();
            pauseDialog.dispose();
        });


        menuBtn.addActionListener(e -> {
            pauseDialog.dispose();

            Window window = SwingUtilities.getWindowAncestor(this);
            if(window instanceof GameFrame){
                GameFrame frame = (GameFrame) window;
                frame.showMenu();

                frame.pack();           // reset size theo preferredSize
                frame.setLocationRelativeTo(null);
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

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
                    tile.setBackground(new Color(192,192,192));
                    tile.setBorder(createThickFrame(true, 5));
                    if(tile.isFlagged()) {
                        tile.setIcon(flagIcon);
                        if (board.gameOver && !tile.isMine()) {
                            tile.setBackground(new Color(240, 70, 70));
//                            tile.setBorder(null);
                        }

                    }
                }

            }
        boardPanel.revalidate();// ---- tinh toan lai bo cuc
        boardPanel.repaint();// ---- ve lai mau sac hinh anh
    }

}