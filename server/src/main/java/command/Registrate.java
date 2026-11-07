package command;

import model.User;
import repository.UserRepository;

import java.sql.SQLException;

public class Registrate implements Command {
    private final UserRepository userRepository;
    private final User user;

    public Registrate(UserRepository userRepository, User user) {
        this.userRepository = userRepository;
        this.user = user;
    }
    @Override
    public void execute() throws SQLException {
        userRepository.registerUser(user);
    }
}
