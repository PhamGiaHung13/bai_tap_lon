package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {
    // Thay đổi thông tin theo máy của ông
    private static final String URL = "jdbc:mysql://localhost:3306/minesweeper";
    private static final String USER = "root"; // Mặc định của MySQL
    private static final String PASS = "Giahung131006"; // Mật khẩu lúc ông cài MySQL

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy Driver MySQL!");
        }
    }
}