package udpsMethod;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dto.ResponseData;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResponseTest {
    @Test
    void serializesAndSendsResponseToRequestEndpoint() throws Exception {
        DatagramSocket socket = mock(DatagramSocket.class);
        XmlMapper mapper = new XmlMapper();
        Response response = new Response(socket, mapper);
        DatagramPacket request = new DatagramPacket(new byte[0], 0, InetAddress.getLoopbackAddress(), 43210);

        response.sendResponse(new ResponseData(true, "completed", 8L), request);

        var packetCaptor = org.mockito.ArgumentCaptor.forClass(DatagramPacket.class);
        verify(socket).send(packetCaptor.capture());
        DatagramPacket sent = packetCaptor.getValue();
        ResponseData decoded = mapper.readValue(new String(sent.getData(), sent.getOffset(), sent.getLength(),
                StandardCharsets.UTF_8), ResponseData.class);
        assertEquals(InetAddress.getLoopbackAddress(), sent.getAddress());
        assertEquals(43210, sent.getPort());
        assertTrue(decoded.isSuccess());
        assertEquals("completed", decoded.getMessage());
        assertEquals(8L, decoded.getUserId());
    }

    @Test
    void propagatesDatagramSendFailures() throws Exception {
        DatagramSocket socket = mock(DatagramSocket.class);
        doThrow(new java.net.SocketException("closed")).when(socket).send(any(DatagramPacket.class));
        Response response = new Response(socket, new XmlMapper());
        DatagramPacket request = new DatagramPacket(new byte[0], 0, InetAddress.getLoopbackAddress(), 43210);

        assertThrows(java.net.SocketException.class,
                () -> response.sendResponse(new ResponseData(false, "failed", null), request));
    }
}
