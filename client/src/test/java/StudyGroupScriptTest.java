import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dto.RequestData;
import dto.ResponseData;
import method.StudyGroupCreator;
import method.UserCreator;
import model.StudyGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.ClientCommandService;
import udpsMethod.Request;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class StudyGroupScriptTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsGroupFromScriptFields() throws Exception {
        Scanner scanner = new Scanner("Script Group\n1\n2.5\n20\n3\n4.2\nFULL_TIME_EDUCATION\nAdmin\n180\nBLUE\nBROWN\nJAPAN\n1.5\n2\n3.5\n");

        StudyGroup group = new StudyGroupCreator().groupCreateFile(scanner);

        assertNotNull(group);
        assertEquals("Script Group", group.getName());
        assertEquals(20L, group.getStudentsCount());
        assertEquals("Admin", group.getGroupAdmin().getName());
        assertEquals(3.5f, group.getGroupAdmin().getLocation().getZ());
    }

    @Test
    void returnsNullWhenScriptGroupFieldsAreInvalid() throws Exception {
        Scanner scanner = new Scanner("Group\nnot-an-integer\n");

        assertNull(new StudyGroupCreator().groupCreateFile(scanner));
    }

    @Test
    void executesNonAddScriptCommands() throws Exception {
        Path script = temporaryDirectory.resolve("commands.txt");
        Files.writeString(script, "unknown-command\n");
        try (DatagramSocket socket = new DatagramSocket()) {
            Request request = new Request(socket, InetAddress.getLoopbackAddress(), 12345, new XmlMapper()) {
                @Override
                public ResponseData run(RequestData requestData) throws IOException {
                    fail("Unknown script commands must not send requests");
                    return null;
                }
            };
            Scanner input = new Scanner("");
            ClientCommandService service = new ClientCommandService(input, new StudyGroupCreator(), request,
                    new UserCreator(input));

            assertDoesNotThrow(() -> new command.ExecuteScript(script.toFile(), service).execute());
        }
    }

    @Test
    void reportsMissingScriptFiles() {
        File file = temporaryDirectory.resolve("missing.txt").toFile();
        try (DatagramSocket socket = new DatagramSocket()) {
            Request request = new Request(socket, InetAddress.getLoopbackAddress(), 12345, new XmlMapper());
            Scanner input = new Scanner("");
            ClientCommandService service = new ClientCommandService(input, new StudyGroupCreator(), request,
                    new UserCreator(input));

            assertThrows(IOException.class, () -> new command.ExecuteScript(file, service).execute());
        } catch (IOException e) {
            fail(e);
        }
    }
}
