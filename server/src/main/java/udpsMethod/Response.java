package udpsMethod;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class Response {
    private final DatagramSocket socket;
    public Response(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendResponse(String response, DatagramPacket request) throws IOException {

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        //packet с информацией результат запроса + данных о клиете
        //А когда ты создаёшь пакет для отправки, ситуация обратная:
        //Здесь ты уже должен указать:
        //что отправить
        //кому отправить
        //на какой порт

        DatagramPacket answer = new DatagramPacket(
                data, //данные, которые прислал клиент
                data.length, //размер данных
                request.getAddress(), //IP-адрес клиента
                request.getPort() //порт клиента
        );

        socket.send(answer);
    }
}

