import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dto.RequestData;
import dto.ResponseData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ServerUdpTest {
    private Server server;
    private Thread serverThread;
    private DatagramSocket client;
    private int port;

    @AfterEach
    void closeServer() throws Exception {
        if (server != null) {
            server.close();
        }
        if (serverThread != null) {
            serverThread.join(1000);
        }
        if (client != null) {
            client.close();
        }
    }

    @Test
    void answersUnknownCommandOverUdp() throws Exception {
        startServer();

        ResponseData response = send("<RequestData><command>missing</command><userId>1</userId></RequestData>");

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Unknown command"));
    }

    @Test
    void returnsFailureResponseForMalformedXml() throws Exception {
        startServer();

        ResponseData response = send("<RequestData>");

        assertFalse(response.isSuccess());
        assertEquals("Request could not be processed", response.getMessage());
    }

    private void startServer() throws Exception {
        try (DatagramSocket reservation = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
            port = reservation.getLocalPort();
        }
        Connection connection = mock(Connection.class);
        server = new Server(port, connection);
        serverThread = new Thread(server::run);
        serverThread.setDaemon(true);
        serverThread.start();
        client = new DatagramSocket();
        client.setSoTimeout(2000);
    }

    private ResponseData send(String xml) throws Exception {
        byte[] request = xml.getBytes(StandardCharsets.UTF_8);
        DatagramPacket outbound = new DatagramPacket(request, request.length,
                InetAddress.getLoopbackAddress(), serverPort());
        client.send(outbound);
        byte[] buffer = new byte[65507];
        DatagramPacket inbound = new DatagramPacket(buffer, buffer.length);
        client.receive(inbound);
        return new XmlMapper().readValue(inbound.getData(), inbound.getOffset(), inbound.getLength(), ResponseData.class);
    }

    private int serverPort() {
        return port;
    }
}
