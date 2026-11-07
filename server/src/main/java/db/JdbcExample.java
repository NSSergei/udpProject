package db;

import java.sql.*;

public class JdbcExample {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/studs";
        String user = "postgres";
        String password = "postgres";

        // try-with-resources автоматически закрывает ресурсы
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id"
             )) {

            ps.setString(1, "admin");
            ps.setString(2, "md2_hash_here");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    System.out.println("Inserted id: " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}