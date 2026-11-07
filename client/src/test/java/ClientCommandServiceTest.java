import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dto.RequestData;
import dto.ResponseData;
import method.StudyGroupCreator;
import method.UserCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClientCommandService;
import udpsMethod.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ClientCommandServiceTest {
    private final PrintStream originalOutput = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private DatagramSocket socket;
    private RecordingRequest request;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(output));
        socket = new DatagramSocket();
        request = new RecordingRequest(socket);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutput);
        socket.close();
    }

    @Test
    void loginStoresUserIdAndSendsCredentials() throws IOException {
        ClientCommandService service = service("alice01\npassword123\n");

        service.run("login");

        assertTrue(service.isFlag());
        assertEquals(17L, service.getCurrentUserId());
        assertEquals("login", request.lastRequest.getCommand());
        assertEquals("alice01", request.lastRequest.getUser().getLogin());
        assertEquals("password123", request.lastRequest.getUser().getPasswordHash());
    }

    @Test
    void failedLoginDoesNotCreateClientSession() throws IOException {
        request.response = new ResponseData(false, "denied", null);
        ClientCommandService service = service("alice01\npassword123\n");

        service.run("login");

        assertFalse(service.isFlag());
        assertNull(service.getCurrentUserId());
    }

    @Test
    void logoutClearsCurrentSession() throws IOException {
        ClientCommandService service = service("alice01\npassword123\n");
        service.run("login");

        service.run("logout");

        assertFalse(service.isFlag());
        assertNull(service.getCurrentUserId());
        assertTrue(output.toString().contains("Выход произошел успешно"));
    }

    @Test
    void removeGreaterCarriesBothTargetIdAndCurrentUserId() throws IOException {
        ClientCommandService service = service("alice01\npassword123\n");
        service.run("login");

        service.run("remove_greater 25");

        assertEquals("remove_greater", request.lastRequest.getCommand());
        assertEquals(25L, request.lastRequest.getId());
        assertEquals(17L, request.lastRequest.getUserId());
    }

    @Test
    void clearRequiresConfirmation() throws IOException {
        ClientCommandService confirmed = service("alice01\npassword123\nY\n");
        confirmed.run("login");
        confirmed.run("clear");
        assertEquals("clear", request.lastRequest.getCommand());

        request.lastRequest = null;
        ClientCommandService declined = service("alice01\npassword123\nN\n");
        declined.run("login");
        request.lastRequest = null;
        declined.run("clear");
        assertNull(request.lastRequest);
    }

    @Test
    void rejectsMissingArgumentsWithoutSendingRequests() throws IOException {
        ClientCommandService service = service("");

        service.run("");
        service.run("remove_by_id");
        service.run("update");
        service.run("filter_contains_name");
        service.run("filter_greater_than_average_mark");
        service.run("remove_greater");
        service.run("unknown-command");

        assertNull(request.lastRequest);
    }

    private ClientCommandService service(String input) {
        Scanner scanner = new Scanner(input);
        return new ClientCommandService(scanner, new StudyGroupCreator(), request, new UserCreator(scanner));
    }

    private static final class RecordingRequest extends Request {
        private RequestData lastRequest;
        private ResponseData response = new ResponseData(true, "ok", 17L);

        private RecordingRequest(DatagramSocket socket) throws Exception {
            super(socket, InetAddress.getLoopbackAddress(), 12345, new XmlMapper());
        }

        @Override
        public ResponseData run(RequestData requestData) {
            lastRequest = requestData;
            return response;
        }
    }
}
