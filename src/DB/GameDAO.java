
package DB;


import java.sql.*;

public class GameDAO {

    public void saveGameResult(int playerId, double time, int exp, int coins, int mastery, int difficultyLevel) {
        // Chuyển đổi từ int sang String để khớp với ENUM trong MySQL
        String diffString;
        switch (difficultyLevel) {
            case 1: diffString = "easy"; break;
            case 2: diffString = "medium"; break;
            case 3: diffString = "hard"; break;
            default: diffString = "easy";
        }

        String sqlInsertResult = "INSERT INTO game_results (player_id, time_seconds, experience, minecoins, mastery, difficulty) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlUpdateStats = "INSERT INTO player_stats (player_id, total_experience, total_minecoins, total_mastery, best_time, total_games) " +
                "VALUES (?, ?, ?, ?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE " +
                "total_experience = total_experience + VALUES(total_experience), " +
                "total_minecoins = total_minecoins + VALUES(total_minecoins), " +
                "total_mastery = total_mastery + VALUES(total_mastery), " +
                "total_games = total_games + 1, " +
                "best_time = LEAST(best_time, VALUES(best_time))";

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsertResult)) {
                ps1.setInt(1, playerId);
                ps1.setDouble(2, time);
                ps1.setInt(3, exp);
                ps1.setInt(4, coins);
                ps1.setInt(5, mastery);
                ps1.setString(6, diffString);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateStats)) {
                ps2.setInt(1, playerId);
                ps2.setInt(2, exp);
                ps2.setInt(3, coins);
                ps2.setInt(4, mastery);
                ps2.setDouble(5, time);
                ps2.executeUpdate();
            }

            conn.commit();
            System.out.println("Data saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    ///  ---- DANG NHAP GAME
    public Player getOrCreatePlayer(String name) {
        String querySelect = "SELECT id FROM players WHERE username = ?";
        String queryInsert = "INSERT INTO players (username) VALUES (?)";

        try (Connection conn = DBContext.getConnection()) {
            // 1. Kiểm tra xem tên đã tồn tại chưa
            PreparedStatement ps = conn.prepareStatement(querySelect);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Player(rs.getInt("id"), name);
            } else {
                // 2. Nếu chưa có thì tạo mới
                PreparedStatement psIns = conn.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS);
                psIns.setString(1, name);
                psIns.executeUpdate();
                ResultSet rsKeys = psIns.getGeneratedKeys();
                if (rsKeys.next()) return new Player(rsKeys.getInt(1), name);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }



    ///  ------- LAY KI LUC
    public java.util.Map<String, Double> getBestTimes(int playerId) {
        java.util.Map<String, Double> bestTimes = new java.util.HashMap<>();
        // Câu truy vấn lấy thời gian nhỏ nhất cho mỗi độ khó của 1 người chơi
        String sql = "SELECT difficulty, MIN(time_seconds) as best FROM game_results " +
                "WHERE player_id = ? GROUP BY difficulty";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bestTimes.put(rs.getString("difficulty"), rs.getDouble("best"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return bestTimes;
    }


    // Trong GameDAO.java
    public java.util.Map<String, Object> getFullAchievements(int playerId) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();

        // 1. Lấy tổng chỉ số từ bảng player_stats
        String sqlStats = "SELECT total_experience, total_minecoins, total_mastery FROM player_stats WHERE player_id = ?";
        // 2. Lấy kỷ lục thời gian từ game_results (giữ nguyên logic cũ)
        String sqlTimes = "SELECT difficulty, MIN(time_seconds) as best FROM game_results WHERE player_id = ? GROUP BY difficulty";

        try (Connection conn = DBContext.getConnection()) {
            // Lấy stats
            PreparedStatement ps1 = conn.prepareStatement(sqlStats);
            ps1.setInt(1, playerId);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                data.put("total_exp", rs1.getInt("total_experience"));
                data.put("total_coins", rs1.getInt("total_minecoins"));
                data.put("total_mastery", rs1.getInt("total_mastery"));
            }

            // Lấy times
            PreparedStatement ps2 = conn.prepareStatement(sqlTimes);
            ps2.setInt(1, playerId);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                data.put(rs2.getString("difficulty").toLowerCase(), rs2.getDouble("best"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
}
