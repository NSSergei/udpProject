package service;

import command.ExecuteScript;
import command.Exit;
import command.Help;
import method.StudyGroupCreator;
import method.UserCreator;
import model.StudyGroup;
import model.User;
import dto.RequestData;
import udpsMethod.Request;
import dto.ResponseData;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientCommandService {
    private final Scanner scanner;
    private final StudyGroupCreator studyGroupCreator;
    private final UserCreator userCreator;
    private final Request request;
    private boolean flag = false;
    private Long currentUserId = null;

    public ClientCommandService(Scanner scanner,
                                StudyGroupCreator studyGroupCreator,
                                Request request,
                                UserCreator userCreator) {
        this.scanner = scanner;
        this.studyGroupCreator = studyGroupCreator;
        this.request = request;
        this.userCreator = userCreator;
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
                sendAndPrint(new RequestData(command, currentUserId));
                break;
            case "show":
                sendAndPrint(new RequestData(command, currentUserId));
                break;
            case "add":
                StudyGroup studyGroup = studyGroupCreator.createGroup(scanner);
                sendAndPrint(new RequestData("add", studyGroup, currentUserId));
                break;
            case "update":
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода id или команда отсутствует");
                    break;
                }

                long updateId = Long.parseLong(parts[1]);
                StudyGroup updateGroup = studyGroupCreator.createGroup(scanner);
                sendAndPrint(new RequestData(command, updateGroup, updateId, currentUserId));
                break;
            case "remove_by_id":
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода id или команда отсутствует");
                    break;
                }
                long id = Long.parseLong(parts[1]);
                sendAndPrint(new RequestData(command, id, currentUserId));
                break;

            case "clear":
                System.out.println("Хотите ли вы удалить коллекцию полностью?" +
                        "Y - да, N или любую другую клавишу отличную от Y - нет");
                String result = scanner.nextLine().toUpperCase();
                if (result.equals("Y")) {
                    sendAndPrint(new RequestData(command, currentUserId));
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
                break;
            case "remove_first":
                sendAndPrint(new RequestData(command, currentUserId));
                break;
            case "head":
                sendAndPrint(new RequestData(command, currentUserId));
                break;
            case "remove_greater":
                if (parts.length != 2) {
                    System.out.println("Необходимо указать id или название метода");
                    break;
                }
                long greaterId = Long.parseLong(parts[1]) ;
                sendAndPrint(new RequestData(command, greaterId));
                break;
            case "average_of_average_mark":
                sendAndPrint(new RequestData(command, currentUserId));
                break;
            case "filter_contains_name":
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода +-+-+-+");
                    break;
                }
                String name = parts[1];
                sendAndPrint(new RequestData(command, name, currentUserId));
                break;
            case "filter_greater_than_average_mark":
                if (parts.length !=2) {
                    System.out.println("Ошибка ввода +-+-+-+");
                    break;
                }
                double average = Double.parseDouble(parts[1]);
                sendAndPrint(new RequestData(command, average, currentUserId));
                break;
            case "register":
                User userCreate = userCreator.createUser(scanner);
                sendAndPrint(new RequestData(command, userCreate));
                break;
            case "login":
                System.out.println("Введите логин");
                String login = scanner.nextLine();

                System.out.println("Введите пароль");
                String password = scanner.nextLine();

                ResponseData responseData = request.run(new RequestData(command,new User(login,password)));

                System.out.println(responseData.getMessage());

                if (responseData.isSuccess()) {
                    currentUserId = responseData.getUserId();
                    flag = true;
                }

                break;
            case "logout":
                if (!flag) {
                    System.out.println("Вы не вошли в учетную запись");
                    break;
                }
                flag = false;
                currentUserId = null;
                System.out.println("Выход произошел успешно");
                break;
            default:
                System.out.println("Команда не найдена " + command);
                break;
        }
    }

    public void sendAndPrint(RequestData requestData) throws IOException {
        long start = System.nanoTime();

        ResponseData responseData = request.run(requestData);

        long end = System.nanoTime();

        System.out.println(responseData.getMessage());
        System.out.println("Время запроса: "
                + (end - start) / 1_000_000.0
                + " ms");
    }

    public boolean isFlag() {
        return flag;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }
}
