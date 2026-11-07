package command;

import model.User;
import repository.UserRepository;

import java.sql.SQLException;

public class Login implements Command {
    private final UserRepository userRepository;
    private final User user;
    private Long resultId;

    public Login(UserRepository userRepository, User user) {
        this.userRepository = userRepository;
        this.user = user;
    }

    @Override
    public void execute() throws SQLException {
       resultId = userRepository.loginUser(user);
    }

    public Long getResultId() {
        return resultId;
    }
}

