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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer();
        taskManager = server.getTaskManager();
        server.start();

        client = HttpClient.newHttpClient();

        // Очищаем данные перед каждым тестом
        taskManager.clearTask();
        taskManager.clearSubtask();
        taskManager.deleteEpic();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Task Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        Task task2 = new Task("Task 2", "Task Description 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 13, 6, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Добавляем задачи в историю
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        // Проверяем историю перед запросом
        List<Task> history = taskManager.getHistory();
        System.out.println("History before request: " + history);
        assertFalse(history.isEmpty(), "History should not be empty");
        assertEquals(2, history.size(), "History should contain 2 tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"title\": \"Task 1\""), "Response should contain Task 1 title");
        assertTrue(response.body().contains("\"title\": \"Task 2\""), "Response should contain Task 2 title");
    }

    @Test
    void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}