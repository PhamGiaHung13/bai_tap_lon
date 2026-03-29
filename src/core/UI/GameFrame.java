    package core.UI;
    import javax.swing.*;
    
    import core.Logic.Board;
    //cua so game
    public class GameFrame extends JFrame {
        public GameFrame() {
            setTitle("Minesweeper");//dat tieu de
            setSize(600, 600);//kich thuoc cua so
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//an nut X thoat game(khong cho game chay ngam)
            setLocationRelativeTo(null);//can cua so ra giua man hinh
            showMenu();//hien menu panel
    
            setVisible(true);//hien thi cua so khong cho no tang hinh
        }
    
        public void showMenu() {
            setContentPane(new MenuPanel(this));//xuat menu panel
            revalidate();//refresh UI
        }
    
        public void startGame() {
            Board newBoard = new Board(1);
            setContentPane(new GamePanel(newBoard));//thay menu bang game panel
            revalidate();
        }
    
    }
