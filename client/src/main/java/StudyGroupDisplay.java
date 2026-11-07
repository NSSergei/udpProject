import method.StudyGroupCreator;
import method.UserCreator;
import service.ClientCommandService;
import udpsMethod.Request;

import java.io.IOException;
import java.util.Scanner;

public class StudyGroupDisplay {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        StudyGroupCreator studyGroupCreator =
                new StudyGroupCreator();

        UserCreator user = new UserCreator(scanner);

        Request request =
                new Request();

        ClientCommandService commandService =
                new ClientCommandService(
                        scanner,
                        studyGroupCreator,
                        request,
                        user
                );

        while (scanner.hasNextLine()) {
            System.out.println("""
            Зарегистрируйтесь, а затем войдите в учетную запись.
            Если учетная запись уже существует — просто войдите.
            
            Команда для регистрации: register
            Команда для входа: login
            """);
            String auth = scanner.nextLine();
            if (auth.trim().equalsIgnoreCase("exit")) {
                break;
            }
                commandService.run(auth);
            while (commandService.isFlag() && scanner.hasNextLine()) {
                System.out.println("введите команду");
                String input = scanner.nextLine();
                commandService.run(input);
            }
        }
    }
}
