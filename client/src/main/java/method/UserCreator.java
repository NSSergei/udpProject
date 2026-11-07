package method;

import exception.ValidationException;
import model.User;

import java.util.Scanner;

public class UserCreator {
    Scanner scanner;
    public UserCreator(Scanner scanner) {
        this.scanner = scanner;
    }

    public User createUser(Scanner scanner) {
        System.out.println("Введите логин для user длина не может быть меньше 6 символов");
        String login = scanner.nextLine();
        if (login.length() < 6) {
            throw  new ValidationException("Длина логина не совпадает с условием");
        }

        System.out.println("Введите пароль для user длина не может быть меньше 8 символов");
        String password = scanner.nextLine();
        if (password.length() < 8) {
            throw  new ValidationException("длина пароля не совпадает с условием");
        }

        User user = new User(login,password);

        return user;
    }
}
