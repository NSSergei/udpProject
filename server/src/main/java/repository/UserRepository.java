package repository;

import exception.ValidationException;
import model.User;
import security.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private final Connection connection;
    private final PasswordHasher passwordHasher;

    public UserRepository(Connection connection) {
        this.connection = connection;
        passwordHasher = new PasswordHasher();
    }


    public String registerUser(User user) throws SQLException {
        String sqlRegister = """
                INSERT INTO users(
                    login,
                    password
                    )
                VALUES(?,?)
                RETURNING id
                """;

        if (user.getLogin() == null || user.getLogin().trim().length() < 6
                || user.getPasswordHash() == null || user.getPasswordHash().length() < 8) {
            throw new ValidationException("Login must contain at least 6 characters and password at least 8");
        }

        try(PreparedStatement pr = connection.prepareStatement(sqlRegister)) {
            pr.setString(1,user.getLogin());
            user.setPasswordHash(passwordHasher.passwordToHash(user.getPasswordHash()));
            pr.setString(2, user.getPasswordHash());

            try (ResultSet rs = pr.executeQuery()){
                if (rs.next()) {
                    long id = rs.getLong("id");
                    user.setId(id);
                } else {
                    return "Ошибка id не найден";
                }
            }
        }
        return user.toString();
    }

    public Long loginUser(User user) {
        if (user == null || user.getLogin() == null || user.getPasswordHash() == null) {
            throw new ValidationException("Invalid login or password");
        }
        String sqlLogin = """
                SELECT id, password 
                FROM users 
                WHERE users.login = ?
                """;

        try(PreparedStatement ps = connection.prepareStatement(sqlLogin)){
            ps.setString(1, user.getLogin());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (passwordHasher.verify(user.getPasswordHash(), storedPassword)) {
                        long userId = rs.getLong("id");
                        if (passwordHasher.needsUpgrade(storedPassword)) {
                            upgradePasswordHash(userId, passwordHasher.passwordToHash(user.getPasswordHash()));
                        }
                        return userId;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new ValidationException("Invalid login or password");
    }

    private void upgradePasswordHash(long userId, String passwordHash) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET password = ? WHERE id = ?")) {
            statement.setString(1, passwordHash);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }
}
