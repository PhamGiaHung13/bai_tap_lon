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
}
