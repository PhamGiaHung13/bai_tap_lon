package DB;

public class Player {
    private int id;
    private String username;
    private long totalExp;
    private long totalCoins;
    private long totalMastery;

    // Constructor, Getters và Setters
    public Player(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(long totalExp) {
        this.totalExp = totalExp;
    }

    public long getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(long totalCoins) {
        this.totalCoins = totalCoins;
    }

    public long getTotalMastery() {
        return totalMastery;
    }

    public void setTotalMastery(long totalMastery) {
        this.totalMastery = totalMastery;
    }
// ... (Thêm các getter/setter cho Exp, Coin để hiển thị lên bảng Achievement)
}
