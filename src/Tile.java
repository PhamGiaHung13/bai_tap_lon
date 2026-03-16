
import javax.swing.*;

public class Tile extends JButton {

    int row;
    int col;

    boolean mine = false;
    boolean revealed = false;

    int minesAround = 0;

    public Tile(int r, int c) {
        this.row = r;
        this.col = c;

        setText("");
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void setMinesAround(int count) {
        this.minesAround = count;
    }

    public int getMinesAround() {
        return minesAround;
    }
}