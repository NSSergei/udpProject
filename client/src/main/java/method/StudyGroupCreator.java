package method;

import enums.Color;
import enums.Country;
import enums.FormOfEducation;
import exception.ValidationException;
import model.Coordinates;
import model.Location;
import model.Person;
import model.StudyGroup;

import javax.imageio.IIOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class StudyGroupCreator {
    public StudyGroup createGroup(Scanner scanner) {

        while (true) {
            try {
                System.out.println("Введите имя");
                String name = scanner.nextLine();

                System.out.println("Введите координаты для x");
                Integer x = Integer.parseInt(scanner.nextLine());

                System.out.println("Введите координаты для y");
                Double y = Double.parseDouble(scanner.nextLine());

                Coordinates coordinates = new Coordinates(x, y);

                LocalDateTime creationDate = LocalDateTime.now();

                System.out.print("Введите кол-во студентов (studentsCount): ");
                long students = Long.parseLong(scanner.nextLine());

                System.out.print("Введите число transferredStudents: ");
                int transferred = Integer.parseInt(scanner.nextLine());

                System.out.println("Введите averageMark (среднюю оценку)");
                double averageMark = Double.parseDouble(scanner.nextLine());

                System.out.println("Введите FormOfEducation");
                FormOfEducation formOfEducation =
                        FormOfEducation.valueOf(scanner.nextLine());

                System.out.println("Введите имя Администратора");
                String admName = scanner.nextLine();

                System.out.println("Введите рост Администратора");
                Integer admHeight = Integer.parseInt(scanner.nextLine());

                System.out.println("Введите цвет глаз");
                Color admEyeColor = Color.valueOf(scanner.nextLine());

                System.out.println("Введите цвет волос");
                Color admHairColor = Color.valueOf(scanner.nextLine());

                System.out.println("Введите нацию");
                Country admNationality = Country.valueOf(scanner.nextLine());

                System.out.println("Координата X для Локации");
                Float locationX = Float.parseFloat(scanner.nextLine());

                System.out.println("Координата Y для Локации");
                Integer locationY = Integer.parseInt(scanner.nextLine());

                System.out.println("Координата Z для Локации");
                float locationZ = Float.parseFloat(scanner.nextLine());

                Location location = new Location(locationX, locationY, locationZ);

                Person person = new Person(
                        admName,
                        admHeight,
                        admEyeColor,
                        admHairColor,
                        admNationality,
                        location
                );

                return new StudyGroup(
                        name,
                        coordinates,
                        creationDate,
                        students,
                        transferred,
                        averageMark,
                        formOfEducation,
                        person
                );
            } catch (ValidationException e) {
                System.out.println("Ошибка валидации: " + e.getMessage());
                System.out.println("Заполните StudyGroup заново.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка в форма данных: " + e.getMessage());
                System.out.println("Заполните studyGroup корректно.");
            }
        }
    }

    public StudyGroup groupCreateFile(Scanner scanner) throws FileNotFoundException {
            try {

                String name = scanner.nextLine();

                Integer x = Integer.parseInt(scanner.nextLine());

                Double y = Double.parseDouble(scanner.nextLine());

                Coordinates coordinates = new Coordinates(x, y);

                LocalDateTime creationDate = LocalDateTime.now();

                long students = Long.parseLong(scanner.nextLine());

                int transferred = Integer.parseInt(scanner.nextLine());

                double averageMark = Double.parseDouble(scanner.nextLine());

                FormOfEducation formOfEducation =
                        FormOfEducation.valueOf(scanner.nextLine());

                String admName = scanner.nextLine();

                Integer admHeight = Integer.parseInt(scanner.nextLine());

                Color admEyeColor = Color.valueOf(scanner.nextLine());

                Color admHairColor = Color.valueOf(scanner.nextLine());

                Country admNationality = Country.valueOf(scanner.nextLine());

                Float locationX = Float.parseFloat(scanner.nextLine());

                Integer locationY = Integer.parseInt(scanner.nextLine());

                float locationZ = Float.parseFloat(scanner.nextLine());

                Location location = new Location(locationX, locationY, locationZ);

                Person person = new Person(
                        admName,
                        admHeight,
                        admEyeColor,
                        admHairColor,
                        admNationality,
                        location
                );

                return new StudyGroup(
                        name,
                        coordinates,
                        creationDate,
                        students,
                        transferred,
                        averageMark,
                        formOfEducation,
                        person
                );
            } catch (ValidationException e) {
                System.out.println("Ошибка валидации: " + e.getMessage());
                System.out.println("Заполните StudyGroup заново.");
                return null;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка в форма данных: " + e.getMessage());
                System.out.println("Заполните studyGroup корректно.");
                return null;
            }
        }

}
