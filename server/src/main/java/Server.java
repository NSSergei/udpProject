import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import command.Save;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.StudyGroupRepository;
import request.RequestData;

import service.CommandService;
import storage.XmlStorage;
import udpsMethod.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;


public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final DatagramSocket socket;
        private final Response response;
        private final XmlMapper xmlMapper;
        private final StudyGroupRepository repository;
        private final XmlStorage xmlStorage;
        private boolean running;
        private final CommandService commandService;

        public Server() throws IOException {
            //System.out.println("SERVER: socket created on port 12345");
            log.info("SERVER: socket created on port 12345");
            socket = new DatagramSocket(12345);
            response = new Response(socket);
            xmlStorage = new XmlStorage();
            repository = new StudyGroupRepository();
            commandService = new CommandService(repository, xmlStorage);
            /*xmlMapper = new XmlMapper();
            xmlMapper.findAndRegisterModules();*/

            xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JavaTimeModule());
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


            //System.out.println("SERVER: initialized");
            log.info("SERVER: initialized");
        }

        public static void main(String[] args) throws IOException {
            Server server = new Server();

            Thread hook = new Thread(() -> {
               //System.out.println("Программа завершается");
                log.info("Программа завершается");
                server.stop();
            });

            Runtime.getRuntime().addShutdownHook(hook);

            //System.out.println("SERVER: starting");

            log.info("SERVER: starting");
            server.run();
        }

        public void run() throws IOException {
            running = true;

            try {
                while (running) {
                    //System.out.println("SERVER: waiting for packet");
                    log.debug("SERVER: waiting for packet");
                    byte[] buffer = new byte[65535];
                    /*packet.getData()    → ЧТО клиент прислал
                    packet.getAddress()  → ОТКУДА клиент прислал
                    packet.getPort()     → С какого порта*/
                    //это пакет-заготовка для приёма пакета
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    //до socket.receive(packet); в нём ещё нет данных клиента.
                    socket.receive(packet);
                    //System.out.println("SERVER: packet received");
                    log.debug("SERVER: packet received");

                    //System.out.println("1. Пакет получен");
                    log.debug("1 the packet passed into XML ");
                    //После получения packet заполняется:
                    //«Возьми байты, которые пришли в packet,
                    // и преврати их обратно в строку XML».
                    String xml = new String(
                            packet.getData(),
                            0,
                            packet.getLength(),
                            StandardCharsets.UTF_8);
                    log.debug("2. XML received");
                    //System.out.println("2. XML получен");


                    //здесь уже происходит десериализация:
                    RequestData requestData = xmlMapper.readValue(xml, RequestData.class);
                    log.debug("3. RequestData received");
                    //System.out.println("3. RequestData создан");

                    String result = commandService.run(requestData);

                    //System.out.println("4. Результат: " + result);
                    log.info("4. Result was created: {} ", result);

                    log.debug("start to send Response");
                    response.sendResponse(result, packet);

                    log.debug("response was send");
                    //System.out.println("5. Ответ отправлен");
                }
            } catch (SocketException e) {
                if (running) {
                    throw e;
                }
            }
            log.info("start to Save info");
            new Save(xmlStorage, repository).execute();
            log.info("successful saving");
        }

        public void stop() {
            log.info("start change running to false");
            running = false;
            log.info("Server is shutting down");
            socket.close();
            log.info("Socket closed");
        }
}

