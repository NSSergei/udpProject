package service;

import command.ExecuteScript;
import command.Exit;
import command.Help;
import method.StudyGroupCreator;
import model.StudyGroup;
import request.RequestData;
import udpsMethod.Request;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientCommandService {
    private final Scanner scanner;
    private final StudyGroupCreator studyGroupCreator;
    private final Request request;

    public ClientCommandService(Scanner scanner,
                                StudyGroupCreator studyGroupCreator,
                                Request request) {
        this.scanner = scanner;
        this.studyGroupCreator = studyGroupCreator;
        this.request = request;
    }

    public void run(String input) throws IOException {
        if (input.isEmpty()) {
            System.out.println("Ошибка ввода");
            return;
        }

        String[] parts = input.trim().split(" ");

        String command = parts[0];

        switch (command) {
            case "help":
                new Help().execute();
                break;
            case "info":
                request.run(new RequestData(command));
                break;
            case "show":
                request.run(new RequestData(command));
                break;
            case "add":
                StudyGroup studyGroup = studyGroupCreator.createGroup(scanner);
                request.run(new RequestData(command, studyGroup));
                break;
            case "update":
                long updateId = Long.parseLong(parts[1]);
                StudyGroup updateGroup = studyGroupCreator.createGroup(scanner);
                request.run(new RequestData(command, updateGroup, updateId));
                break;
            case "remove_by_id":
                //Задача на 28:08:2026г.
                //Сделать валидацию для проверки длины parts, чтобы избежать дублирования.
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода id или команда отсутствует");
                    break;
                }
                long id = Long.parseLong(parts[1]);
                request.run(new RequestData(command, id));
                break;

            case "clear":
                System.out.println("Хотите ли вы удалить коллекцию полностью?" +
                        "Y - да, N или любую другую клавишу отличную от Y - нет");
                String result = scanner.nextLine().toUpperCase();
                if (result.equals("Y")) {
                    request.run(new RequestData(command));
                    break;
                }
                System.out.println("Вы отказались удалять коллекцию");
                break;
            case "execute_script":
                if (parts.length != 2) {
                    System.out.println("Необходимо указать файл скрипта");
                    break;
                }
                File file = new File(parts[1]);
                new ExecuteScript(file, this).execute();
                break;
            case "exit":
                new Exit();
            case "remove_first":
                request.run(new RequestData(command));
                break;
            case "head":
                request.run(new RequestData(command));
                break;
            case "remove_greater":
                if (parts.length != 2) {
                    System.out.println("Необходимо указать id или название метода");
                    break;
                }
                long greaterId = Long.parseLong(parts[1]) ;
                request.run(new RequestData(command, greaterId));
                break;
            case "average_of_average_mark":
                request.run(new RequestData(command));
                break;
            case "filter_contains_name":
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода +-+-+-+");
                    break;
                }
                String name = parts[1];
                request.run(new RequestData(command, name));
                break;
            case "filter_greater_than_average_mark":
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода +-+-+-+");
                    break;
                }
                double average = Double.parseDouble(parts[1]);
                request.run(new RequestData(command, average));
                break;
            default:
                System.out.println("Команда не найдена");
                break;
        }
    }
}
