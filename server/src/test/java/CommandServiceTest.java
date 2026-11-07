import dto.RequestData;
import dto.ResponseData;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import enums.FormOfEducation;
import exception.ValidationException;
import model.Coordinates;
import model.Person;
import model.StudyGroup;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.StudyGroupRepository;
import repository.UserRepository;
import service.CommandService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommandServiceTest {
    private StudyGroupRepository groups;
    private UserRepository users;
    private CommandService service;

    @BeforeEach
    void setUp() {
        groups = mock(StudyGroupRepository.class);
        users = mock(UserRepository.class);
        service = new CommandService(groups, users);
    }

    @Test
    void rejectsNullBlankAndUnauthenticatedRequests() throws Exception {
        assertThrows(ValidationException.class, () -> service.run(null));
        assertThrows(ValidationException.class, () -> service.run(new RequestData()));
        assertThrows(ValidationException.class, () -> service.run(new RequestData("  ", 1L)));
        assertThrows(ValidationException.class, () -> service.run(new RequestData("show", 0L)));
        RequestData missingUserId = new XmlMapper().readValue(
                "<RequestData><command>show</command></RequestData>", RequestData.class);
        assertThrows(ValidationException.class, () -> service.run(missingUserId));
    }

    @Test
    void reportsUnknownCommandsAsFailures() throws Exception {
        ResponseData response = service.run(new RequestData("does_not_exist", 1L));

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Unknown command"));
        verifyNoInteractions(groups, users);
    }

    @Test
    void readsAndReturnsCollectionQueries() throws Exception {
        when(groups.showInfo(7L)).thenReturn("3");
        when(groups.showAllCollection(7L)).thenReturn("groups");
        when(groups.showFirstElement(7L)).thenReturn("first");
        when(groups.showAverageMark(7L)).thenReturn("4.1");
        when(groups.showContainsName("Java", 7L)).thenReturn("matching group");
        when(groups.showGreaterThanAverageMark(4.0, 7L)).thenReturn("high marks");

        assertTrue(service.run(new RequestData("info", 7L)).getMessage().contains("3"));
        assertEquals("groups", service.run(new RequestData("show", 7L)).getMessage());
        assertEquals("first", service.run(new RequestData("head", 7L)).getMessage());
        assertEquals("4.1", service.run(new RequestData("average_of_average_mark", 7L)).getMessage());
        assertEquals("matching group", service.run(new RequestData("filter_contains_name", "Java", 7L)).getMessage());
        assertEquals("high marks", service.run(new RequestData("filter_greater_than_average_mark", 4.0, 7L)).getMessage());

        verify(groups).showInfo(7L);
        verify(groups).showAllCollection(7L);
        verify(groups).showFirstElement(7L);
        verify(groups).showAverageMark(7L);
        verify(groups).showContainsName("Java", 7L);
        verify(groups).showGreaterThanAverageMark(4.0, 7L);
    }

    @Test
    void dispatchesAllMutationCommandsWithOwnershipAndArguments() throws Exception {
        StudyGroup group = validGroup();

        ResponseData added = service.run(new RequestData("add", group, 7L));
        ResponseData updated = service.run(new RequestData("update", group, 19L, 7L));
        ResponseData removed = service.run(new RequestData("remove_by_id", 19L, 7L));
        ResponseData cleared = service.run(new RequestData("clear", 7L));
        ResponseData removedFirst = service.run(new RequestData("remove_first", 7L));
        ResponseData removedGreater = service.run(new RequestData("remove_greater", 19L, 7L));

        assertTrue(added.isSuccess());
        assertTrue(updated.isSuccess());
        assertTrue(removed.isSuccess());
        assertTrue(cleared.isSuccess());
        assertTrue(removedFirst.isSuccess());
        assertTrue(removedGreater.isSuccess());
        verify(groups).addStudyGroup(group, 7L);
        verify(groups).updateStudyGroup(19L, group, 7L);
        verify(groups).deleteById(19L, 7L);
        verify(groups).deleteAll(7L);
        verify(groups).deleteFirst(7L);
        verify(groups).removeGreater(19L, 7L);
    }

    @Test
    void rejectsInvalidGroupsBeforeWriting() throws Exception {
        assertThrows(ValidationException.class, () -> service.run(new RequestData("add", (StudyGroup) null, 7L)));
        assertThrows(ValidationException.class, () -> service.run(new RequestData("update", (StudyGroup) null, 19L, 7L)));
        assertThrows(ValidationException.class, () -> service.run(new RequestData("update", validGroup(), 0L, 7L)));
        verify(groups, never()).addStudyGroup(any(), any());
        verify(groups, never()).updateStudyGroup(anyLong(), any(), any());
    }

    @Test
    void registersAndAuthenticatesUsers() throws Exception {
        User submitted = new User("alice01", "password123");
        when(users.loginUser(any(User.class))).thenReturn(42L);

        ResponseData registered = service.run(new RequestData("register", submitted));
        ResponseData authenticated = service.run(new RequestData("login", submitted));

        assertTrue(registered.isSuccess());
        assertTrue(authenticated.isSuccess());
        assertEquals(42L, authenticated.getUserId());
        assertEquals("42", authenticated.getMessage());
        verify(users).registerUser(argThat(user -> user != submitted
                && "alice01".equals(user.getLogin())
                && "password123".equals(user.getPasswordHash())));
        verify(users).loginUser(argThat(user -> user != submitted && "alice01".equals(user.getLogin())));
    }

    @Test
    void rejectsAuthenticationRequestsWithoutCredentials() {
        assertThrows(ValidationException.class, () -> service.run(new RequestData("login", (User) null)));
        assertThrows(ValidationException.class, () -> service.run(new RequestData("register", (User) null)));
    }

    private static StudyGroup validGroup() {
        return new StudyGroup("Java", new Coordinates(10, 20.0), LocalDateTime.of(2026, 1, 1, 0, 0),
                30, 4, 4.5, FormOfEducation.FULL_TIME_EDUCATION,
                new Person("Admin", 170, null, null, null, null));
    }
}
