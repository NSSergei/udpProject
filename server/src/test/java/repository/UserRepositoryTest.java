package repository;

import exception.ValidationException;
import model.User;
import org.junit.jupiter.api.Test;
import security.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {
    @Test
    void registersUsersWithSaltedHashes() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getLong("id")).thenReturn(12L);
        User user = new User("alice01", "password123");

        String message = new UserRepository(connection).registerUser(user);

        assertEquals(12L, user.getId());
        assertTrue(user.getPasswordHash().startsWith("pbkdf2-sha256$"));
        assertTrue(message.contains("model.User"));
        verify(statement).setString(1, "alice01");
        verify(statement).setString(eq(2), argThat(hash -> hash.startsWith("pbkdf2-sha256$")));
    }

    @Test
    void registrationRejectsInvalidCredentialsAndMissingReturnedId() throws Exception {
        UserRepository repository = new UserRepository(mock(Connection.class));
        assertThrows(ValidationException.class, () -> repository.registerUser(new User(" ", "password123")));
        assertThrows(ValidationException.class, () -> repository.registerUser(new User("alice01", "short")));

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        assertEquals("Ошибка id не найден", new UserRepository(connection).registerUser(new User("alice01", "password123")));
    }

    @Test
    void logsInWithValidHashAndRejectsInvalidCredentials() throws Exception {
        PasswordHasher hasher = new PasswordHasher();
        String savedHash = hasher.passwordToHash("password123");
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getString("password")).thenReturn(savedHash);
        when(result.getLong("id")).thenReturn(12L);
        UserRepository repository = new UserRepository(connection);

        assertEquals(12L, repository.loginUser(new User("alice01", "password123")));
        assertThrows(ValidationException.class, () -> repository.loginUser(new User("alice01", "wrong-password")));
        assertThrows(ValidationException.class, () -> repository.loginUser(null));
        assertThrows(ValidationException.class, () -> repository.loginUser(new User(null, "password123")));
        verify(connection, times(2)).prepareStatement(contains("SELECT id, password"));
    }

    @Test
    void upgradesLegacyHashOnSuccessfulLogin() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        PreparedStatement update = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(query, update);
        when(query.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getString("password")).thenReturn("db5d888a0480461f4fb978746d1baf34");
        when(result.getLong("id")).thenReturn(25L);

        assertEquals(25L, new UserRepository(connection).loginUser(new User("alice01", "12345678")));

        verify(update).setString(eq(1), argThat(hash -> hash.startsWith("pbkdf2-sha256$")));
        verify(update).setLong(2, 25L);
        verify(update).executeUpdate();
    }

    @Test
    void wrapsDatabaseFailuresDuringLogin() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("offline"));

        assertThrows(RuntimeException.class,
                () -> new UserRepository(connection).loginUser(new User("alice01", "password123")));
    }
}
