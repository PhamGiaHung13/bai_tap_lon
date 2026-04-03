//package minigames.sudoku.Test;
//
//import minigames.sudoku.UI.SudokuPanel;
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.io.File;
//import java.io.IOException;
//
//public class SudokuTest {
//    static class BackgroundPanel extends JPanel {
//        private Image bg;
//
//        public BackgroundPanel() {
//            try {
//                // Thử nạp ảnh từ file (Cách này ưu tiên vì dễ kiểm soát)
//                File imgFile = new File("bg1.jpg");
//                if (imgFile.exists()) {
//                    bg = new ImageIcon(imgFile.getAbsolutePath()).getImage();
//                } else {
//                    bg = ImageIO.read(getClass().getResource("/minigames/sudoku/bg1.jpg"));
//                }
//            } catch (IOException e) {
//                System.err.println("Không tìm thấy bg1.jpg!");
//            }
//            setLayout(new BorderLayout());
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            if (bg != null) {
//                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String[] options = {"Easy", "Medium", "Hard"};
//        int choice = JOptionPane.showOptionDialog(
//                null, "Chọn độ khó:", "Sudoku Pro",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//                null, options, options[0]
//        );
//
//        if (choice == -1) System.exit(0);
//
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Sudoku Game - Professional Edition");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//            // 2. ÉP BACKGROUND PANEL LÀM CONTENTPANE (Chìa khóa để hiện ảnh nền)
//            BackgroundPanel mainBackground = new BackgroundPanel();
//            frame.setContentPane(mainBackground);
//
//            // 3. Khởi tạo SudokuPanel của ông
//            SudokuPanel gameUI = new SudokuPanel(choice + 1);
//
//            // QUAN TRỌNG: Làm SudokuPanel trong suốt để ảnh nền của BackgroundPanel lộ ra
//            gameUI.setOpaque(false);
//
//            frame.add(gameUI);
//
//            frame.setMinimumSize(new Dimension(1050, 950));
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//        });
//    }
//}