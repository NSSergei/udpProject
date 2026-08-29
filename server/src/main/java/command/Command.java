package command;

import java.io.FileNotFoundException;

@FunctionalInterface
public interface Command {
    void execute() throws FileNotFoundException;
}
