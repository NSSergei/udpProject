import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dto.RequestData;
import dto.ResponseData;
import enums.FormOfEducation;
import model.Coordinates;
import model.Location;
import model.Person;
import model.StudyGroup;
import model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DataTransferTest {
    @Test
    void requestConstructorsPopulateTheirFields() {
        User user = new User("alice01", "password-hash");
        StudyGroup group = new StudyGroup("Java", new Coordinates(1, 2.5), LocalDateTime.of(2026, 1, 2, 3, 4),
                20, 4, 4.5, FormOfEducation.FULL_TIME_EDUCATION,
                new Person("Admin", 170, null, null, null, null));

        RequestData authRequest = new RequestData("login", user);
        RequestData idRequest = new RequestData("remove_by_id", 8, 3);
        RequestData updateRequest = new RequestData("update", group, 8, 3);
        RequestData nameRequest = new RequestData("filter_contains_name", "Java", 3);
        RequestData averageRequest = new RequestData("filter_greater_than_average_mark", 4.0, 3);
        RequestData addRequest = new RequestData("add", group, 3);
        RequestData userRequest = new RequestData("show", 3);

        assertSame(user, authRequest.getUser());
        assertEquals(8, idRequest.getId());
        assertEquals(3, idRequest.getUserId());
        assertSame(group, updateRequest.getStudyGroup());
        assertEquals(8, updateRequest.getId());
        assertEquals("Java", nameRequest.getName());
        assertEquals(4.0, averageRequest.getAverage());
        assertSame(group, addRequest.getStudyGroup());
        assertEquals(3, userRequest.getUserId());
    }

    @Test
    void responseConstructorsAndXmlRoundTripPreserveValues() throws Exception {
        ResponseData response = new ResponseData(true, "done", 17L);
        XmlMapper mapper = new XmlMapper();

        ResponseData decoded = mapper.readValue(mapper.writeValueAsBytes(response), ResponseData.class);
        ResponseData messageOnly = new ResponseData("message");
        ResponseData empty = new ResponseData();

        assertTrue(decoded.isSuccess());
        assertEquals("done", decoded.getMessage());
        assertEquals(17L, decoded.getUserId());
        assertEquals("message", messageOnly.getMessage());
        assertFalse(messageOnly.isSuccess());
        assertNull(empty.getMessage());
    }

    @Test
    void modelSettersAndOrderingUseIds() {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(4);
        coordinates.setY(7.5);
        User user = new User();
        user.setId(9);
        user.setPasswordHash("hash");
        StudyGroup lower = new StudyGroup();
        StudyGroup higher = new StudyGroup();
        lower.setId(1);
        higher.setId(2);

        assertEquals(4, coordinates.getX());
        assertEquals(7.5, coordinates.getY());
        assertEquals(9, user.getId());
        assertEquals("hash", user.getPasswordHash());
        assertTrue(lower.compareTo(higher) < 0);
        assertEquals(0, lower.compareTo(lower));
        assertTrue(higher.compareTo(lower) > 0);
    }

    @Test
    void modelAccessorsAndRepresentationsExposeStoredValues() {
        Person person = new Person();
        person.setName("Admin");
        person.setHeight(180);
        person.setEyeColor(enums.Color.BLUE);
        person.setHairColor(enums.Color.BROWN);
        person.setNationality(enums.Country.JAPAN);
        person.setLocation(new Location(1.5f, 2, 3.5f));
        StudyGroup group = new StudyGroup(11L, "Systems", new Coordinates(2, 3.0),
                LocalDateTime.of(2026, 3, 4, 5, 6), 20, 3, 4.7,
                FormOfEducation.EVENING_CLASSES, person);
        group.setName("Distributed Systems");
        group.setCoordinates(new Coordinates(4, 5.0));
        group.setStudentsCount(21);
        group.setTransferredStudents(4);
        group.setAverageMark(4.8);
        group.setFormOfEducation(FormOfEducation.DISTANCE_EDUCATION);
        group.setGroupAdmin(person);

        assertEquals("Admin", person.getName());
        assertEquals(180, person.getHeight());
        assertEquals(enums.Color.BLUE, person.getEyeColor());
        assertEquals(enums.Color.BROWN, person.getHairColor());
        assertEquals(enums.Country.JAPAN, person.getNationality());
        assertEquals(1.5f, person.getLocation().getX());
        assertEquals(2, person.getLocation().getY());
        assertEquals(3.5f, person.getLocation().getZ());
        assertTrue(person.toString().contains("Admin"));
        assertTrue(person.getLocation().toString().contains("z=3.5"));
        assertEquals("Coordinates{x=4, y=5.0}", group.getCoordinates().toString());
        assertEquals(11L, group.getId());
        assertEquals("Distributed Systems", group.getName());
        assertEquals(21L, group.getStudentsCount());
        assertEquals(4, group.getTransferredStudents());
        assertEquals(4.8, group.getAverageMark());
        assertEquals(FormOfEducation.DISTANCE_EDUCATION, group.getFormOfEducation());
        assertSame(person, group.getGroupAdmin());
        assertTrue(group.toString().contains("Distributed Systems"));
    }
}
