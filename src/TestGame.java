public class TestBoard {

    public static void main(String[] args) {

        Board board = new Board();

        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.columns; c++) {

                if (board.board[r][c].isMine()) {
                    System.out.print("M ");
                } else {
                    System.out.print(". ");
                }

            }
            System.out.println();
        }

    }

}