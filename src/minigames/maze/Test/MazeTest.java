//package minigames.maze.Test;
//
//import minigames.maze.Logic.MazeGame;
//import minigames.maze.UI.MazePanel;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class MazeTest {
//    public static void main(String[] args) {
//        MazeGame game = new MazeGame(2
//        ); // độ khó 1,2,3
//        MazePanel panel = new MazePanel(game, 30); // 30 giây
//
//        JFrame frame = new JFrame("Maze RedGhost");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        // gần full screen
//        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//        int width = (int) (screen.width * 0.9);
//        int height = (int) (screen.height * 0.9);
//        frame.setSize(width, height);
//        frame.setLayout(new BorderLayout());
//        frame.add(panel, BorderLayout.CENTER);
//
//        frame.setLocationRelativeTo(null);
//        frame.setResizable(true);
//        frame.setVisible(true);
//
//        panel.requestFocusInWindow();
//    }
//}