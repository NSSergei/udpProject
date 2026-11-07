package command;

import dto.RequestData;
import method.StudyGroupCreator;
import model.StudyGroup;
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
        System.out.println("Рабочая директория: " + System.getProperty("user.dir"));
        System.out.println("Путь к файлу: " + file.getAbsolutePath());
        System.out.println("Файл существует: " + file.exists());

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("add")) {
                StudyGroup studyGroup = new StudyGroupCreator().groupCreateFile(scanner);
                clientCommandService.sendAndPrint(new RequestData("add",studyGroup, clientCommandService.getCurrentUserId()));
            } else {
                clientCommandService.run(command);
            }
        }
    }
}
