import command.Registrate;
import model.User;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import security.PasswordHasher;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServer {

    @Test
    void currectRegustrationTest() throws SQLException {
        User user = mock(User.class);
        UserRepository userRepository = mock(UserRepository.class);
        Registrate command = new Registrate(userRepository, user);
        command.execute();
        verify(userRepository).registerUser(user);
    }

    @Test
    void shouldThrowExceptionWhenLoginAlreadyExists() throws SQLException {
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User("Vlad","12345678");
        Registrate command = new Registrate(userRepository, user);
        doThrow(new SQLException("Login already Exists")).when(userRepository).registerUser(user);

        assertThrows(SQLException.class, command::execute);
    }

    @Test
    void passwordsAreSaltedAndVerifiable() {
        PasswordHasher hasher = new PasswordHasher();
        String first = hasher.passwordToHash("correct horse battery staple");
        String second = hasher.passwordToHash("correct horse battery staple");

        assertNotEquals(first, second);
        assertTrue(hasher.verify("correct horse battery staple", first));
        assertFalse(hasher.verify("incorrect", first));
        assertFalse(hasher.needsUpgrade(first));
    }

    @Test
    void legacyPasswordsCanBeVerifiedForMigration() {
        PasswordHasher hasher = new PasswordHasher();

        assertTrue(hasher.verify("12345678", "db5d888a0480461f4fb978746d1baf34"));
        assertTrue(hasher.needsUpgrade("db5d888a0480461f4fb978746d1baf34"));
        assertFalse(hasher.verify("87654321", "db5d888a0480461f4fb978746d1baf34"));
    }
}
