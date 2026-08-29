package method;

import enums.Color;
import enums.Country;
import enums.FormOfEducation;
import model.Coordinates;
import model.Location;
import model.Person;
import model.StudyGroup;

import java.time.LocalDateTime;
import java.util.Scanner;

public class StudyGroupCreator {
    public StudyGroup createGroup(Scanner scanner) {
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
    }
}
