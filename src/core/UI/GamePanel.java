package core.UI;

import core.Logic.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

//layout choi game
public class GamePanel extends JPanel {

    Board board;
    Events events;
    boolean flagMode = false;
    JButton flagBtn;

    ImageIcon bombIcon;
    private ImageIcon flagIcon;
    private int tileSize;

    private JPanel boardPanel = new JPanel();

    //scale icon(thay doi kich thuoc)
    private ImageIcon getScaledIcon(String path, int size){
        Image img = new ImageIcon(path).getImage();
        Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public GamePanel(Board board){

        this.board = board;
        this.events = new Events(board);
        flagBtn = new JButton("🚩 FLAG");//🚩

        //main layout gamepanel
        setLayout(new BorderLayout());//layout theo huong (N W S E)

        //tao 1 panel toolbar chua flagBtn, ve sau co the add them thoi gian , so luong min cac thu
        JPanel toolBar = new JPanel();
        toolBar.setBackground(Color.WHITE);
        toolBar.add(flagBtn);

        //design button
        flagBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        flagBtn.setFocusPainted(false);
        flagBtn.setBackground(new Color(1,130,180));

        //switch mode flag
        flagBtn.addActionListener(e -> {
            flagMode = !flagMode;
            if(flagMode) flagBtn.setBackground(Color.GREEN);
            else flagBtn.setBackground(new Color(1,130,180)); //70,130,180
        });

        //layout board
        boardPanel.setLayout(new GridLayout(board.rows, board.columns));
        //gridlayout la layout theo ma tran

        //sap xep vi tri toolbar va board
        add(toolBar, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        //listener Resize
        boardPanel.addComponentListener(new java.awt.event.ComponentAdapter(){
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateTileSize();
            }
        });


        //su kien choi game
        for(int r = 0; r < board.rows; r++){
            for(int c = 0; c < board.columns; c++){

                Tile tile = board.getTile(r,c);

//                tile.setFocusPainted(false);
//                tile.setContentAreaFilled(false); // Dòng này cực quan trọng để màu setBackground hiện lên chuẩn nhất
//                tile.setOpaque(true);

                //hover
                tile.addMouseListener(new java.awt.event.MouseAdapter(){
                    Color defaultColor = tile.getBackground();
                    public void mouseEntered(java.awt.event.MouseEvent evt){
                        if(!tile.isRevealed()){
                            tile.setBackground(Color.WHITE);
                        }
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt){
                        if(!tile.isRevealed()){
                            tile.setBackground(defaultColor);
                        }
                    }
                });

                //click
                tile.addActionListener(e -> {
                    if(board.gameOver) return;
                    if(flagMode){
                        //trang thai dat co/flag
                        if(tile.isRevealed()) return;

                        tile.setFlagged(!tile.isFlagged());

                        updateUIBoard();
                        return;
                    }

                    //goi click tu event
                    Events.ClickResult result = events.clickTile(tile, () -> {
                        updateUIBoard();
                        //xu li win
                        if(board.isWin()){
                            JOptionPane.showMessageDialog(this, "YOU WIN!");
                        }
                    });

                    //hieu ung bom nhap nhay
                    int[] count = {0};//mang 1 phan tu de dem so lan nhap nhay bom
                    if(result == Events.ClickResult.MINE){

                        new javax.swing.Timer(100,i ->{
                            tile.setBackground(tile.getBackground() == Color.RED ? Color.WHITE : Color.RED);
                            count[0]++;
                            if(count[0] >= 10) ((Timer)i.getSource()).stop();
                        }).start();

                        revealAllMines();//hien tat ca bom khi thua
                    }
                    updateUIBoard();//cap nhat board
                });

                boardPanel.add(tile);
            }
        }

    }

    //tilesize theo panel size va set trump va iran
    private void updateTileSize(){
        tileSize = Math.min(boardPanel.getWidth() / board.columns,
                            boardPanel.getHeight() / board.rows);

        bombIcon = getScaledIcon("src/Image/trump-removebg.png", tileSize); //💣
        flagIcon = getScaledIcon("src/Image/iran-removebg.png", tileSize); //🚩

        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);

                tile.setPreferredSize(new Dimension(tileSize, tileSize));
            }
        updateUIBoard();
    }

    public void revealAllMines(){
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);

                if(tile.isMine()){
                    tile.setRevealed(true);
                }
            }
        updateUIBoard();
    }

    //mau cho so bom
    private Color getColor(int mines){
        switch(mines){
            case 1: return Color.BLUE;
            case 2: return new Color(0,128,0);
            case 3: return Color.RED;
            case 4: return new Color(0,0,128);
            case 5: return new Color(128,0,0);
            case 6: return new Color(0,128,128);
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    public void updateUIBoard(){
        for(int r = 0; r < board.rows; r++)
            for(int c = 0; c < board.columns; c++){
                Tile tile = board.getTile(r, c);

                tile.setIcon(null);
                tile.setText("");

                if(tile.isRevealed()){

                    if(tile.isMine()){
                        tile.setIcon(bombIcon);

                        if(!board.gameOver) tile.setBackground(Color.LIGHT_GRAY);
                        else tile.setBackground(Color.RED);

                    } else{
                        tile.setBackground(Color.WHITE);

                        int mines = tile.getMinesAround();

                        if(mines > 0){
                            tile.setForeground(getColor(mines));
                            tile.setText(String.valueOf(mines));
                        }
                    }
                }

                else{
                    if(tile.isFlagged())
                        tile.setIcon(flagIcon);
                }

            }
        boardPanel.revalidate();//tinh toan lai bo cuc
        boardPanel.repaint();//ve lai mau sac hinh anh
    }

}