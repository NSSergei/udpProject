import method.StudyGroupCreator;
import service.ClientCommandService;
import udpsMethod.Request;

import java.io.IOException;
import java.util.Scanner;

public class StudyGroupDisplay {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        StudyGroupCreator studyGroupCreator =
                new StudyGroupCreator();

        Request request =
                new Request();

        ClientCommandService commandService =
                new ClientCommandService(
                        scanner,
                        studyGroupCreator,
                        request
                );

        while (true) {
                System.out.println("введите команду");
                String input = scanner.nextLine();

                commandService.run(input);
            }

    }
}