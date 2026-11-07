import exception.ValidationException;
import model.Coordinates;
import model.Location;
import model.Person;
import model.StudyGroup;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StudyGroupValidationTest {
    @Test
    void acceptsValidGroupWithOptionalPersonProperties() {
        StudyGroup group = validGroup();

        assertTrue(StudyGroup.validateGroup(group));
        assertTrue(Coordinates.validateCoordinate(-175, 743.0));
        assertTrue(Person.validatePerson(group.getGroupAdmin()));
    }

    @Test
    void rejectsNullGroupAndBlankName() {
        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(null));
        StudyGroup group = validGroup();
        group.setName("  ");

        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
    }

    @Test
    void rejectsMissingCoordinates() {
        StudyGroup group = validGroup();
        group.setCoordinates(null);

        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
    }

    @Test
    void rejectsCoordinateBoundariesAndNonFiniteValues() {
        assertThrows(ValidationException.class, () -> Coordinates.validateCoordinate(null, 1.0));
        assertThrows(ValidationException.class, () -> Coordinates.validateCoordinate(-176, 1.0));
        assertThrows(ValidationException.class, () -> Coordinates.validateCoordinate(0, null));
        assertThrows(ValidationException.class, () -> Coordinates.validateCoordinate(0, 744.0));
        assertThrows(ValidationException.class, () -> Coordinates.validateCoordinate(0, Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsNonFiniteCoordinatesAndMarks() {
        assertThrows(ValidationException.class, () -> Coordinates.validateCoordinate(0, Double.NaN));
        StudyGroup group = new StudyGroup("Group", new Coordinates(0, 0.0), LocalDateTime.now(), 1, 1,
                Double.POSITIVE_INFINITY, enums.FormOfEducation.FULL_TIME_EDUCATION,
                new Person("Admin", 170, null, null, null, null));

        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
    }

    @Test
    void rejectsZeroStudentCount() {
        StudyGroup group = validGroup();
        group.setStudentsCount(0);

        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
    }

    @Test
    void rejectsInvalidRequiredFields() {
        StudyGroup group = new StudyGroup("Group", new Coordinates(1, 2.0), null, 10, 2, 4.5,
                enums.FormOfEducation.FULL_TIME_EDUCATION, new Person("Admin", 170, null, null, null, null));
        assertInvalid(group);

        group = validGroup();
        group.setTransferredStudents(0);
        assertInvalid(group);

        group = validGroup();
        group.setAverageMark(0);
        assertInvalid(group);

        group = validGroup();
        group.setFormOfEducation(null);
        assertInvalid(group);

        group = validGroup();
        group.setGroupAdmin(null);
        assertInvalid(group);
    }

    @Test
    void rejectsInvalidPersonAndLocation() {
        assertThrows(ValidationException.class, () -> Person.validatePerson(null));
        assertThrows(ValidationException.class, () -> Person.validatePerson(new Person(" ", 170, null, null, null, null)));
        assertThrows(ValidationException.class, () -> Person.validatePerson(new Person("Admin", 0, null, null, null, null)));
        assertThrows(ValidationException.class, () -> Person.validatePerson(
                new Person("Admin", 170, null, null, null, new Location(null, 2, 3))));
        assertThrows(ValidationException.class, () -> Person.validatePerson(
                new Person("Admin", 170, null, null, null, new Location(Float.NaN, 2, 3))));
    }

    private static StudyGroup validGroup() {
        return new StudyGroup("Group", new Coordinates(1, 2.0), LocalDateTime.now(), 10, 2, 4.5,
                enums.FormOfEducation.FULL_TIME_EDUCATION, new Person("Admin", 170, null, null, null, null));
    }

    private static void assertInvalid(StudyGroup group) {
        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
    }
}
