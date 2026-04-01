package core.UI;

import core.Logic.*;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


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
    ImageIcon smileIcon, deadIcon, winIcon;
    JLabel timerLabel;
    int time = 0;
    Timer gameTimer;
    boolean started = false;

/// ---------- CONSTRUCTOR
    public GamePanel(Board board){


        ///--------------- LAYOUT GAMEPANEL
        this.board = board;
        this.events = new Events(board);
        setLayout(new BorderLayout());//layout theo huong (N W S E)
        this.setBackground(new Color(192, 192, 192));
        this.setBorder(BorderFactory.createCompoundBorder(
                createThickFrame(true, 6), // Viền vát ngoài cùng
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // Đây chính là Margin tạo "khoảng không"
        ));




        // ---------- 1.LABEL SO MIN CON LAI
        minesLabel = new JLabel("000");
        minesLabel.setFont(new Font("Consolas", Font.BOLD, 40));
        minesLabel.setForeground(Color.RED);
        minesLabel.setBackground(Color.black);
        minesLabel.setOpaque(true);
        minesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minesLabel.setPreferredSize(new Dimension(50, 50));
        minesLabel.setBorder(BorderFactory.createCompoundBorder(createThickFrame(false, 3)
        ,BorderFactory.createEmptyBorder(20, 10, 5, 10))
        );





        //------- 2.BUTTON FACE ICON
        faceBtn = new JButton();
        faceBtn.setFocusPainted(false);
        faceBtn.setBackground(new Color(192,192,192));
        faceBtn.setBorder(createThickFrame(true, 4));
        faceBtn.setPreferredSize(new Dimension(60,60));

        smileIcon = getScaledIcon("src/Image/smile.png", 40);
        deadIcon = getScaledIcon("src/Image/die.png", 40);
        winIcon = getScaledIcon("src/Image/cool.png", 40);

        faceBtn.setIcon(smileIcon);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(faceBtn);





        //------ 3.TIMER
        timerLabel = new JLabel("000");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 40));
        timerLabel.setForeground(Color.RED);
        timerLabel.setBackground(Color.BLACK);
        timerLabel.setOpaque(true);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setPreferredSize(new Dimension(50, 50));
        timerLabel.setBorder(BorderFactory.createCompoundBorder(createThickFrame(false, 3)
                ,BorderFactory.createEmptyBorder(20, 10, 5, 10))
        );

        gameTimer = new Timer(1000, e -> {
            time++;
            timerLabel.setText(String.format("%03d", time));
        });




        /// ---------- TOOLBAR PANEL
        JPanel toolBar = new JPanel(new GridLayout(1, 3, 20, 0));
        toolBar.setBackground(new Color(192, 192, 192));
        toolBar.setPreferredSize(new Dimension(0, 90));
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        createThickFrame(false, 6), // Viền lõm của Toolbar
                        BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding trong Toolbar
                ),
                BorderFactory.createEmptyBorder(0, 0, 0, 0) // Margin DUOI
        ));
        toolBar.add(minesLabel);
        toolBar.add(centerPanel);
        toolBar.add(timerLabel);



        ///---------- layout BOARD(gridlayout la layout theo ma tran)
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(board.rows, board.columns));
        boardPanel.setBackground(new Color(192, 192, 192));
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 0, 0, 0), // khoang cach
                createThickFrame(false, 6) // vien lom 6px
        ));



        //-------- SAP XEP VI TRI BOARD PANEL VA TOOLBAR PANEL
        add(toolBar, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);


        ///-------- LISTENER RESIZE
        // COMPONENT
        boardPanel.addComponentListener(new java.awt.event.ComponentAdapter(){
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateTileSize();
            }
        });


        ///------- FUNCTION PLAY GAME
        for(int r = 0; r < board.rows; r++){
            for(int c = 0; c < board.columns; c++){

                Tile tile = board.getTile(r,c);

                tile.setFocusPainted(false);
                tile.setOpaque(true);


                /// ------ MOUSE LISTENER
                tile.addMouseListener(new java.awt.event.MouseAdapter(){


                    //--- PUT FLAG (RIGHT CLICK)
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e){
                        if(board.gameOver) return;
                        if(SwingUtilities.isRightMouseButton(e)) {
                            if(tile.isRevealed()) return;
                            tile.setFlagged(!tile.isFlagged());
                            updateUIBoard();
                        }
                    }
                    public void mouseReleased(){

                    }

                    //--------- HOVER (in and out)
                    public void mouseEntered(java.awt.event.MouseEvent evt){
                        if(board.gameOver) return;
                        if(!tile.isRevealed()){
                            tile.setBackground(new Color(210, 210, 210));
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

                boardPanel.add(tile);
            }
        }
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

//---------- UPDATE MINELEFT ----
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
        tileSize = Math.min(boardPanel.getWidth() / board.columns,
                            boardPanel.getHeight() / board.rows);
        //--- SET IMAGE
        bombIcon = getScaledIcon("src/Image/trump.png", tileSize); //💣
        flagIcon = getScaledIcon("src/Image/flag.png", tileSize); //🚩

        // --- TILE
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){

                Tile tile = board.getTile(r, c);

                tile.setPreferredSize(new Dimension(tileSize, tileSize));

                tile.setFont(new Font("Segoe UI", Font.BOLD, tileSize / 3));
            }
        updateUIBoard();
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



    /// ---------- UPDATE BOARD
    public void updateUIBoard(){

        if(board.gameOver) faceBtn.setIcon(deadIcon);
        else if(board.isWin()) faceBtn.setIcon(winIcon);
        else faceBtn.setIcon(smileIcon);
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
                        tile.setBorder(createThickFrame(false, 4));;
                        tile.setBackground(Color.LIGHT_GRAY);


                    //---- IF TILE NOT MINE
                    } else{
                        tile.setBorder(createThickFrame(false, 4));
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
                    if(tile.isFlagged()){
                        tile.setIcon(flagIcon);
                        if(board.gameOver && !tile.isMine()) tile.setBackground(new Color(240, 70, 70));

                    }
                }

            }
        boardPanel.revalidate();// ---- tinh toan lai bo cuc
        boardPanel.repaint();// ---- ve lai mau sac hinh anh
    }

}