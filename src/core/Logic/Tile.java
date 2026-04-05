
package core.Logic;

import javax.swing.*;

public class Tile extends JButton {

    public int row;
    public int col;

    boolean mine = false;
    boolean revealed = false;
    boolean Flag  = false;
    int minesAround = 0;

    private boolean isExploded = false; // Mặc định là false

    public Tile(int r, int c) {
        this.row = r;
        this.col = c;

        setFlagged(false);
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

    public boolean isFlagged(){return Flag;}

    public void setFlagged(boolean flag){Flag = flag;}

    public void setExploded(boolean exploded) { this.isExploded = exploded; }
    public boolean isExploded() { return isExploded; }
}
