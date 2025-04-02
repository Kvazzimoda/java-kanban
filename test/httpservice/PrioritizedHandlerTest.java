package httpservice;

import data.Task;
import data.TaskStatus;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHandlerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer();
        taskManager = server.getTaskManager();
        server.start();

        client = HttpClient.newHttpClient();

        taskManager.clearTask();
        taskManager.clearSubtask();
        taskManager.deleteEpic();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Task Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        Task task2 = new Task("Task 2", "Task Description 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 13, 4, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        // Проверяем, что задачи отсортированы по startTime (task2 раньше task1)
        assertTrue(response.body().indexOf("\"title\": \"Task 2\"") < response.body().indexOf("\"title\": \"Task 1\""));
    }

    @Test
    void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}