import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dataBase.DatabaseInitializer;
import dto.RequestData;
import dto.ResponseData;
import exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.StudyGroupRepository;
import repository.UserRepository;
import service.CommandService;
import udpsMethod.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Server implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final int MAX_DATAGRAM_BYTES = 65_507;
    private static final int WORKER_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final int QUEUE_CAPACITY = 256;

    private final DatagramSocket socket;
    private final Connection connection;
    private final CommandService commandService;
    private final XmlMapper xmlMapper;
    private final ThreadPoolExecutor workers;
    private final LongAdder received = new LongAdder();
    private final LongAdder completed = new LongAdder();
    private final LongAdder failed = new LongAdder();
    private final LongAdder processingNanos = new LongAdder();
    private volatile boolean running;

    public Server() throws IOException, SQLException {
        this(Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "12345")), openConnection());
    }

    public Server(int port, Connection connection) throws IOException {
        this.socket = new DatagramSocket(port);
        this.connection = connection;
        this.commandService = new CommandService(new StudyGroupRepository(connection), new UserRepository(connection));
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.registerModule(new JavaTimeModule());
        this.xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.workers = new ThreadPoolExecutor(
                WORKER_COUNT,
                WORKER_COUNT,
                0,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                runnable -> {
                    Thread thread = new Thread(runnable, "udp-worker");
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private static Connection openConnection() throws SQLException {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "5432");
        String database = System.getenv().getOrDefault("DB_NAME", "studs");
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://" + host + ":" + port + "/" + database);
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) throws IOException, SQLException {
        Server server = new Server();
        DatabaseInitializer.initialize(server.connection);
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        server.run();
    }

    public void run() {
        running = true;
        long lastMetrics = System.nanoTime();
        long lastCompleted = 0;
        long lastFailed = 0;
        while (running) {
            byte[] buffer = new byte[MAX_DATAGRAM_BYTES];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                received.increment();
                workers.execute(() -> process(packet));
            } catch (SocketException e) {
                if (running) {
                    log.error("UDP socket failed", e);
                    failed.increment();
                }
            } catch (IOException | RuntimeException e) {
                log.error("Unable to receive UDP request", e);
                failed.increment();
            }
            if (System.nanoTime() - lastMetrics >= TimeUnit.SECONDS.toNanos(10)) {
                long totalCompleted = completed.sum();
                long totalFailed = failed.sum();
                log.info("metrics received={} completed={} failed={} completedPer10s={} failedPer10s={} meanProcessingMs={} activeWorkers={} queued={}",
                        received.sum(), totalCompleted, totalFailed,
                        totalCompleted - lastCompleted, totalFailed - lastFailed,
                        totalCompleted == 0 ? 0 : processingNanos.sum() / totalCompleted / 1_000_000.0,
                        workers.getActiveCount(), workers.getQueue().size());
                lastCompleted = totalCompleted;
                lastFailed = totalFailed;
                lastMetrics = System.nanoTime();
            }
        }
    }

    private void process(DatagramPacket packet) {
        long start = System.nanoTime();
        try {
            String payload = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
            RequestData request = xmlMapper.readValue(payload, RequestData.class);
            ResponseData result = commandService.run(request);
            send(result, packet);
            completed.increment();
        } catch (ValidationException e) {
            send(new ResponseData(false, e.getMessage(), null), packet);
            failed.increment();
        } catch (Exception e) {
            log.warn("Request processing failed", e);
            send(new ResponseData(false, "Request could not be processed", null), packet);
            failed.increment();
        } finally {
            processingNanos.add(System.nanoTime() - start);
        }
    }

    private void send(ResponseData response, DatagramPacket request) {
        try {
            byte[] payload = xmlMapper.writeValueAsBytes(response);
            if (payload.length > MAX_DATAGRAM_BYTES) {
                payload = xmlMapper.writeValueAsBytes(new ResponseData(false, "Response exceeds UDP packet limit", null));
            }
            DatagramPacket result = new DatagramPacket(payload, payload.length, request.getAddress(), request.getPort());
            socket.send(result);
        } catch (IOException e) {
            log.warn("Unable to send UDP response", e);
            failed.increment();
        }
    }

    @Override
    public void close() {
        running = false;
        socket.close();
        workers.shutdown();
        try {
            if (!workers.awaitTermination(5, TimeUnit.SECONDS)) {
                workers.shutdownNow();
            }
            connection.close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            workers.shutdownNow();
        } catch (SQLException e) {
            log.warn("Unable to close database connection", e);
        }
    }
}
