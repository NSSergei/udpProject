import exception.ValidationException;
import method.UserCreator;
import model.User;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserCreatorTest {
    @Test
    void createsUserFromValidInput() {
        Scanner scanner = new Scanner("alice01\npassword123\n");

        User user = new UserCreator(scanner).createUser(scanner);

        assertEquals("alice01", user.getLogin());
        assertEquals("password123", user.getPasswordHash());
    }

    @Test
    void rejectsShortLogin() {
        Scanner scanner = new Scanner("abc\npassword123\n");

        assertThrows(ValidationException.class, () -> new UserCreator(scanner).createUser(scanner));
    }

    @Test
    void rejectsShortPassword() {
        Scanner scanner = new Scanner("alice01\nshort\n");

        assertThrows(ValidationException.class, () -> new UserCreator(scanner).createUser(scanner));
    }
}
