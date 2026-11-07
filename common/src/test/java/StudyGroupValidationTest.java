import exception.ValidationException;
import model.Coordinates;
import model.Person;
import model.StudyGroup;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StudyGroupValidationTest {
    @Test
    void rejectsMissingCoordinates() {
        StudyGroup group = new StudyGroup("Group", null, LocalDateTime.now(), 1, 1, 1,
                enums.FormOfEducation.FULL_TIME_EDUCATION, new Person("Admin", 170, null, null, null, null));

        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
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
        StudyGroup group = new StudyGroup("Group", new Coordinates(0, 0.0), LocalDateTime.now(), 0, 1, 1,
                enums.FormOfEducation.FULL_TIME_EDUCATION, new Person("Admin", 170, null, null, null, null));

        assertThrows(ValidationException.class, () -> StudyGroup.validateGroup(group));
    }
}
