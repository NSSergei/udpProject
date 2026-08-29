/*package request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ResponseData {
    private final DatagramSocket socket;

    public ResponseData(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendResponse(String result, DatagramPacket packet) throws SocketException, IOException {
        byte[] data = result.getBytes(StandardCharsets.UTF_8);

        DatagramPacket packet1 = new DatagramPacket(
                data,
                data.length,
                packet.getAddress(),
                packet.getPort());

        socket.send(packet1);

    }
}*/
