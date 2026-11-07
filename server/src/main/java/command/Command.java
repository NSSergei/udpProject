package command;

import java.io.FileNotFoundException;
import java.sql.SQLException;

@FunctionalInterface
public interface Command {
    void execute() throws  SQLException;
}
