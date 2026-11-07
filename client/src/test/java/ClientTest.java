import enums.Color;
import enums.Country;
import enums.FormOfEducation;
import method.StudyGroupCreator;
import model.StudyGroup;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {
    @Test
    public void correctStudyGroupCreator() {
        String input = """
                Java Developers
                10
                20.5
                30
                5
                4.7
                FULL_TIME_EDUCATION
                Sergei
                172
                BROWN
                GREEN
                JAPAN
                28
                29
                30
                """;

        Scanner scanner = new Scanner(input);

        StudyGroupCreator creator = new StudyGroupCreator();

        StudyGroup result = creator.createGroup(scanner);

        assertNotNull(creator);

        assertEquals("Java Developers", result.getName());

        assertEquals(10, result.getCoordinates().getX());
        assertEquals(20.5, result.getCoordinates().getY());

        assertEquals(30, result.getStudentsCount());
        assertEquals(5, result.getTransferredStudents());
        assertEquals(4.7, result.getAverageMark());

        assertEquals(
                FormOfEducation.FULL_TIME_EDUCATION,
                result.getFormOfEducation()
        );

        assertEquals(
                "Sergei",
                result.getGroupAdmin().getName()
        );

        assertEquals(
                172,
                result.getGroupAdmin().getHeight()
        );

        assertEquals(
                Color.BROWN,
                result.getGroupAdmin().getEyeColor()
        );

        assertEquals(
                Color.GREEN,
                result.getGroupAdmin().getHairColor()
        );

        assertEquals(
                Country.JAPAN,
                result.getGroupAdmin().getNationality()
        );

        assertEquals(
                28,
                result.getGroupAdmin().getLocation().getX()
        );

        assertEquals(
                29,
                result.getGroupAdmin().getLocation().getY()
        );

        assertEquals(
                30,
                result.getGroupAdmin().getLocation().getZ()
        );
    }

    @Test
    public void wrongStudyGroupCreator() {
        String input = """
                Java Developers
                10
                20.5
                30
                5
                4.7
                Ошибка
                Sergei
                172
                BROWN
                GREEN
                JAPAN
                28
                29
                30
        """;

        Scanner scanner = new Scanner(input);
        StudyGroupCreator creator = new StudyGroupCreator();

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> creator.createGroup(scanner)
        );

        assertEquals("No line found", exception.getMessage());
    }
}
