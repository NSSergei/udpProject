package command;

import service.ClientCommandService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExecuteScript implements Command {
    private final File file;
    private final ClientCommandService clientCommandService;

    public ExecuteScript(File file,
                         ClientCommandService clientCommandService) {
        this.file = file;
        this.clientCommandService = clientCommandService;
    }

    @Override
    public void execute() throws IOException {
        Scanner scanner = new Scanner(file);
        List<String> commands = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            commands.add(command);
        }
        for(int i = 0; i < commands.size(); i++) {
            clientCommandService.run(commands.get(i));
        }
    }
}
