import dto.RequestData;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import repository.StudyGroupRepository;
import repository.UserRepository;
import service.CommandService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CommandServiceTest {
    @Test
    void rejectsCollectionRequestsWithoutUserId() {
        CommandService service = new CommandService(mock(StudyGroupRepository.class), mock(UserRepository.class));

        assertThrows(ValidationException.class, () -> service.run(new RequestData("show", (long) 0)));
    }

    @Test
    void reportsUnknownCommandsAsFailures() throws Exception {
        CommandService service = new CommandService(mock(StudyGroupRepository.class), mock(UserRepository.class));

        var response = service.run(new RequestData("does_not_exist", 1L));

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Unknown command"));
    }
}
