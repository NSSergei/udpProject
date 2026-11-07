import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dto.RequestData;
import dto.ResponseData;
import model.User;
import org.junit.jupiter.api.Test;
import udpsMethod.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {
    @Test
    void sendsXmlAndDecodesServerResponse() throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        XmlMapper mapper = new XmlMapper();
        try (DatagramSocket server = new DatagramSocket(0, loopback);
             DatagramSocket clientSocket = new DatagramSocket()) {
            AtomicReference<RequestData> decodedRequest = new AtomicReference<>();
            AtomicReference<Throwable> serverFailure = new AtomicReference<>();
            Thread responder = new Thread(() -> {
                try {
                    byte[] bytes = new byte[4096];
                    DatagramPacket requestPacket = new DatagramPacket(bytes, bytes.length);
                    server.receive(requestPacket);
                    decodedRequest.set(mapper.readValue(new String(bytes, 0, requestPacket.getLength(), StandardCharsets.UTF_8), RequestData.class));
                    byte[] response = mapper.writeValueAsBytes(new ResponseData(true, "received", 23L));
                    server.send(new DatagramPacket(response, response.length, requestPacket.getAddress(), requestPacket.getPort()));
                } catch (Throwable e) {
                    serverFailure.set(e);
                }
            });
            responder.start();

            Request request = new Request(clientSocket, loopback, server.getLocalPort(), mapper);
            ResponseData response = request.run(new RequestData("login", new User("alice01", "secret123")));
            responder.join(2000);

            assertFalse(responder.isAlive());
            assertNull(serverFailure.get());
            assertEquals("login", decodedRequest.get().getCommand());
            assertEquals("alice01", decodedRequest.get().getUser().getLogin());
            assertTrue(response.isSuccess());
            assertEquals("received", response.getMessage());
            assertEquals(23L, response.getUserId());
        }
    }

    @Test
    void rejectsResponseFromUnexpectedPort() throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (DatagramSocket server = new DatagramSocket(0, loopback);
             DatagramSocket otherServer = new DatagramSocket(0, loopback);
             DatagramSocket clientSocket = new DatagramSocket()) {
            Thread responder = new Thread(() -> {
                try {
                    DatagramPacket incoming = new DatagramPacket(new byte[1024], 1024);
                    server.receive(incoming);
                    byte[] bytes = new XmlMapper().writeValueAsBytes(new ResponseData(true, "wrong source", 1L));
                    otherServer.send(new DatagramPacket(bytes, bytes.length, incoming.getAddress(), incoming.getPort()));
                } catch (IOException ignored) {
                }
            });
            responder.start();

            Request request = new Request(clientSocket, loopback, server.getLocalPort(), new XmlMapper());

            IOException error = assertThrows(IOException.class,
                    () -> request.run(new RequestData("show", 1L)));
            responder.join(2000);
            assertEquals("Received response from an unexpected endpoint", error.getMessage());
        }
    }

    @Test
    void wrapsSocketTimeoutAsIoFailure() throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (DatagramSocket server = new DatagramSocket(0, loopback);
             DatagramSocket clientSocket = new DatagramSocket()) {
            Request request = new Request(clientSocket, loopback, server.getLocalPort(), new XmlMapper());

            IOException error = assertThrows(IOException.class,
                    () -> request.run(new RequestData("show", 1L)));

            assertEquals("Time out: server out of response", error.getMessage());
            assertInstanceOf(SocketTimeoutException.class, error.getCause());
        }
    }
}
